/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.Duration;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
	private Map<Integer, List<Duration>> offsets = new HashMap<>();

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
		}
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
			entry.setValue(MeasureBuilder.withAttributesOf(builder));
		}
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
		offsets.put(staff, new ArrayList<>());
	}
}
