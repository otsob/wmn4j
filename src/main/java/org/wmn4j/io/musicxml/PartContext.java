/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.access.Offset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Container class for keeping track of all the context dependent
 * variables when reading a part from MusicXML.
 */
final class PartContext {

	private int staff;
	private int voice;
	private int measureNumber;

	private boolean hasChordTag;

	private Set<Integer> staves = new HashSet<>();

	private final PartBuilder partBuilder;

	// All map integer keys represent staff numbers within a single part.
	private Map<Integer, ChordBuffer> chordBuffers = new HashMap<>();
	private Map<Integer, MeasureBuilder> measureBuilders = new HashMap<>();
	// Offset durations are handled as a list even though the forward element in
	// MusicXML may contain a staff number (optional). However, the purpose of that
	// is so unclear that it is ignored.
	private Collection<Duration> offsetDurations = new ArrayList<>();
	private Collection<Duration> backupDurations = new ArrayList<>();

	PartContext(PartBuilder partBuilder) {
		this.partBuilder = partBuilder;
		setStaff(Part.DEFAULT_STAFF_NUMBER);
	}

	public PartBuilder getPartBuilder() {
		return partBuilder;
	}

	public int getVoice() {
		return voice;
	}

	public void setVoice(int voice) {
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
	 * @param noteBuilder (can be null) note builder to add to buffer
	 */
	void updateChordBuffer(NoteBuilder noteBuilder) {
		final ChordBuffer buffer = chordBuffers.get(staff);

		if (!hasChordTag || noteBuilder == null) {
			buffer.flushTo(measureBuilders.get(staff));
		}

		if (noteBuilder != null) {
			buffer.addNote(noteBuilder, voice);
			if (!hasChordTag) {
				offsetDurations.add(noteBuilder.getDuration());
			}
		}
	}

	private Duration getOffset() {
		if (offsetDurations.isEmpty()) {
			return null;
		}

		Duration offsetDuration = Duration.sum(offsetDurations);

		if (!backupDurations.isEmpty()) {
			final Duration backupDuration = Duration.sum(backupDurations);
			offsetDuration = offsetDuration.subtract(backupDuration);
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

	void finishMeasureElement() {
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
}
