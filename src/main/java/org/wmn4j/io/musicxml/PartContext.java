/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.apache.commons.math3.fraction.Fraction;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.GraceNoteBuilder;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Ornamental;
import org.wmn4j.notation.OrnamentalBuilder;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.access.Offset;
import org.wmn4j.notation.directions.Direction;
import org.wmn4j.notation.techniques.Technique;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * Container class for keeping track of all the context dependent
 * variables when reading a part from MusicXML.
 */
final class PartContext {

	private int staff;
	private int voice;
	private int measureNumber;

	private boolean hasChordTag;

	// Flag that is set to true if the previous note read
	// has been an artificial harmonic marking note and not
	// an individual note.
	private boolean isPrevArtificialHarmonicPitch;

	private Set<Integer> staves = new HashSet<>();
	private BiConsumer<Integer, Integer> arpeggioResolver;

	private final PartBuilder partBuilder;

	private NoteBuilder prevNoteBuilder;

	private Direction.Type directionType;
	private String directionText;
	private Fraction directionOffset;
	private int directionStaff = Part.DEFAULT_STAFF_NUMBER;

	// All map integer keys represent staff numbers within a single part.
	private Map<Integer, ChordBuffer> chordBuffers = new HashMap<>();
	private Map<Integer, MeasureBuilder> measureBuilders = new HashMap<>();
	private Map<Integer, OrnamentalBuffer> ornamentalNoteBuffers = new HashMap<>();

	// Offset durations are handled as a list even though the forward element in
	// MusicXML may contain a staff number (optional). However, the purpose of that
	// is so unclear that it is ignored.
	private Collection<Duration> offsetDurations = new ArrayList<>();
	private Collection<Duration> backupDurations = new ArrayList<>();

	private Map<Technique.AdditionalValue, Object> harmonicValues = new HashMap<>();

	PartContext(PartBuilder partBuilder) {
		this.partBuilder = partBuilder;
		setStaff(Part.DEFAULT_STAFF_NUMBER);
	}

	void setArpeggioResolver(BiConsumer<Integer, Integer> resolver) {
		arpeggioResolver = resolver;
	}

	PartBuilder getPartBuilder() {
		return partBuilder;
	}

	int getVoice() {
		return voice;
	}

	void setVoice(int voice) {
		this.voice = voice;
	}

	/**
	 * Returns the measure builder of the current staff.
	 *
	 * @return the measure builder of the current staff
	 */
	MeasureBuilder getMeasureBuilder() {
		return measureBuilders.get(staff);
	}

	/**
	 * Updates the chord buffer by setting previously accumulated
	 * notebuilders to the measurebuilder as correct type and adding
	 * the (optional) new note builder to buffer.
	 *
	 * @param builder (can be null) durational builder that can be a notebuilder or restbuilder
	 */
	void updateChordBuffer(DurationalBuilder builder) {
		final ChordBuffer buffer = chordBuffers.get(staff);

		if (!hasChordTag || builder == null) {
			DurationalBuilder added = buffer.flushTo(measureBuilders.get(staff), arpeggioResolver);

			// The touching and sounding pitches of artificial harmonics are handled
			// as notes with the chord tag in MusicXML. When a previous "chord" ends,
			// and it has been a note with an artificial harmonic and not really a chord,
			// the accumulated harmonic values need to be added.
			if (isPrevArtificialHarmonicPitch && added.isNoteBuilder()) {
				added.toNoteBuilder().addTechnique(Technique.of(Technique.Type.HARMONIC, harmonicValues));
				harmonicValues = new HashMap<>();
				isPrevArtificialHarmonicPitch = false;
			}
		}

		if (builder == null) {
			return;
		}

		if (builder.isNoteBuilder() && !isPrevArtificialHarmonicPitch) {
			NoteBuilder noteBuilder = builder.toNoteBuilder();
			buffer.addNote(noteBuilder, staff, voice);
			if (!hasChordTag) {
				offsetDurations.add(noteBuilder.getDuration());
			}

			prevNoteBuilder = noteBuilder;
		} else if (builder.isRestBuilder()) {
			offsetDurations.add(builder.getDuration());
		}
	}

	void updateOrnamentalBuffer(GraceNoteBuilder graceNoteBuilder) {
		ornamentalNoteBuffers.get(staff).addNote(graceNoteBuilder, hasChordTag, staff, voice, arpeggioResolver);
	}

	private Duration getOffset() {
		if (offsetDurations.isEmpty()) {
			return null;
		}

		Duration offsetDuration = Duration.sum(offsetDurations);

		if (!backupDurations.isEmpty()) {
			final Duration backupDuration = Duration.sum(backupDurations);

			// Backups with length exactly equal to the offset may occur on
			// staff changes, in which case offset is null and durations
			// should be reset.
			if (offsetDuration.isLongerThan(backupDuration)) {
				offsetDuration = offsetDuration.subtract(backupDuration);
			} else {
				offsetDuration = null;
			}
			backupDurations.clear();
		}

		offsetDurations.clear();
		offsetDurations.add(offsetDuration);

		return offsetDuration;
	}

	void addForwardDuration(Duration duration) {
		offsetDurations.add(duration);
	}

	void addBackupDuration(Duration duration) {
		backupDurations.add(duration);
	}

	void setChordTag(boolean hasChordTag) {
		this.hasChordTag = hasChordTag;
	}

	boolean hasGraceNotes() {
		return !ornamentalNoteBuffers.get(staff).isEmpty();
	}

	void addPreceedingOrnamentals(NoteBuilder noteBuilder) {
		List<OrnamentalBuilder> ornamentals = ornamentalNoteBuffers.get(staff).popOrnamentalBuilders();

		// Check for simple appogiatura type case.
		if (ornamentals.size() == 1 && ornamentals.get(0) instanceof GraceNoteBuilder) {
			final GraceNoteBuilder graceNoteBuilder = (GraceNoteBuilder) ornamentals.get(0);
			final int interval = noteBuilder.getPitch().toInt() - graceNoteBuilder.getPitch().toInt();
			// Consider as an appoggiatura grace notes that are at most one whole step away
			// and have a duration type that is half of the principal notes's duration. Also if
			// the acciaccatura type has already been set, then the grace note cannot be an appoggiatura.
			if (Math.abs(interval) <= 2
					&& noteBuilder.getDuration().divide(2).equals(graceNoteBuilder.getDisplayableDuration())
					&& !graceNoteBuilder.getGraceNoteType().equals(Ornamental.Type.ACCIACCATURA)) {
				graceNoteBuilder.setGraceNoteType(Ornamental.Type.APPOGGIATURA);
			}
		}

		noteBuilder.setPrecedingGraceNotes(ornamentals);
	}

	void addSucceedingOrnamentals(NoteBuilder noteBuilder) {
		final List<OrnamentalBuilder> ornamentals = ornamentalNoteBuffers.get(staff).popOrnamentalBuilders();
		noteBuilder.setSucceedingGraceNotes(ornamentals);
	}

	void finishMeasureElement() {
		if (prevNoteBuilder != null && hasGraceNotes()) {
			prevNoteBuilder.setSucceedingGraceNotes(ornamentalNoteBuffers.get(staff).popOrnamentalBuilders());
		}

		clearChordBuffers();
		for (Map.Entry<Integer, MeasureBuilder> entry : measureBuilders.entrySet()) {
			final int staffNumber = entry.getKey();
			final MeasureBuilder builder = entry.getValue();
			partBuilder.addToStaff(staffNumber, builder);
			entry.setValue(copyContext(builder));
		}

		offsetDurations.clear();
		backupDurations.clear();
	}

	private MeasureBuilder copyContext(MeasureBuilder builder) {
		// Copy the attributes and change those attributes that don't typically carry
		// on from one measure to the next and are not explicitly set in MusicXML.
		final MeasureBuilder copiedBuilder = MeasureBuilder.withAttributesOf(builder);

		copiedBuilder.setRightBarline(Barline.SINGLE);
		copiedBuilder.setLeftBarline(Barline.NONE);

		if (!builder.getClefChanges().isEmpty()) {
			final Clef lastClef = builder.getClefChanges().stream().max(Offset::compareTo).get().get();
			copiedBuilder.setClef(lastClef);
		}

		copiedBuilder.getClefChanges().clear();
		copiedBuilder.getDirections().clear();

		return copiedBuilder;
	}

	private void clearChordBuffers() {
		for (int s : staves) {
			this.staff = s;
			updateChordBuffer(null);
		}
	}

	void setStaff(int staff) {
		if (!staves.contains(staff)) {
			addStaff(staff);
		}

		this.staff = staff;
	}

	public int getStaff() {
		return staff;
	}

	void setMeasureNumber(int number) {
		measureNumber = number;
		for (MeasureBuilder builder : measureBuilders.values()) {
			builder.setNumber(measureNumber);
		}
	}

	private void addStaff(int staff) {
		staves.add(staff);

		final MeasureBuilder currentBuilder = getMeasureBuilder();
		MeasureBuilder newBuilder;
		if (currentBuilder != null) {
			newBuilder = copyContext(getMeasureBuilder());
		} else {
			newBuilder = new MeasureBuilder(measureNumber);
		}

		measureBuilders.put(staff, newBuilder);
		chordBuffers.put(staff, new ChordBuffer());
		ornamentalNoteBuffers.put(staff, new OrnamentalBuffer());
	}

	/**
	 * Adds clef either as the main clef of a measure or as a clef change.
	 *
	 * @param clef      the clef to add
	 * @param clefStaff the staff to which the clef is added, 0 indicates to use the currently set staff
	 */
	void addClef(Clef clef, int clefStaff) {
		final int staffNumber = clefStaff == 0 ? staff : clefStaff;
		if (!staves.contains(staffNumber)) {
			addStaff(staffNumber);
		}

		final Duration offset = getOffset();
		final MeasureBuilder builder = measureBuilders.get(staffNumber);

		if (offset == null) {
			builder.setClef(clef);
		} else {
			builder.addClefChange(offset, clef);
		}
	}

	void setDirectionOffset(Fraction offset) {
		this.directionOffset = offset;
	}

	void setDirectionType(Direction.Type directionType) {
		this.directionType = directionType;
	}

	void setDirectionText(String directionText) {
		this.directionText = directionText;
	}

	void setDirectionStaff(int directionStaff) {
		this.directionStaff = directionStaff;
	}

	void addDirection() {
		if (directionType == null) {
			// All direction types are not supported yet, so if directionType may not have been set.
			return;
		}

		measureBuilders.get(directionStaff)
				.addDirection(getDirectionOffset(), Direction.of(directionType, directionText));
		directionOffset = null;
		directionType = null;
		directionText = null;
		directionStaff = Part.DEFAULT_STAFF_NUMBER;
	}

	private Duration getDirectionOffset() {
		if (offsetDurations.isEmpty()) {
			if (directionOffset != null && directionOffset.compareTo(Fraction.ZERO) > 0) {
				return Duration.of(directionOffset.getNumerator(), directionOffset.getDenominator());
			}

			return null;
		}

		Duration offset = Duration.sum(offsetDurations);

		if (!backupDurations.isEmpty()) {
			final Duration backup = Duration.sum(backupDurations);
			if (backup.equals(offset)) {
				return null;
			}
			offset = offset.subtract(backup);
		}

		if (directionOffset != null) {
			final Fraction absOffset = directionOffset.abs();
			final Duration absOffsetDur = Duration.of(absOffset.getNumerator(), absOffset.getDenominator());
			if (directionOffset.equals(absOffset)) {
				offset = offset.add(absOffsetDur);
			} else {
				offset = offset.subtract(absOffsetDur);
			}
		}
		return offset;
	}

	public void incrementMeasureNumber() {
		++measureNumber;
	}

	public void setStaffLines(int lines) {
		if (lines == 1) {
			partBuilder.setStaffType(Staff.Type.SINGLE_LINE, staff);
		} else {
			partBuilder.setStaffType(Staff.Type.NORMAL, staff);
		}
	}

	public void setArtificialHarmonic() {
		harmonicValues.put(Technique.AdditionalValue.IS_ARTIFICIAL_HARMONIC, Boolean.TRUE);
	}

	public void setArtificialHarmonicBasePitch(Pitch pitch) {
		harmonicValues.put(Technique.AdditionalValue.HARMONIC_BASE_PITCH, pitch);
	}

	public void setArtificialHarmonicTouchingPitch(Pitch pitch) {
		harmonicValues.put(Technique.AdditionalValue.HARMONIC_TOUCHING_PITCH, pitch);
		isPrevArtificialHarmonicPitch = true;
	}

	public void setArtificialHarmonicSoundingPitch(Pitch pitch) {
		harmonicValues.put(Technique.AdditionalValue.HARMONIC_SOUNDING_PITCH, pitch);
		isPrevArtificialHarmonicPitch = true;
	}
}
