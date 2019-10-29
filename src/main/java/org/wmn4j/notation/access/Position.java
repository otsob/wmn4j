/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.wmn4j.notation.SingleStaffPart;

/**
 * Represents the position of a {@link org.wmn4j.notation.Durational}
 * in a {@link org.wmn4j.notation.Score}.
 * <p>
 * This class is immutable.
 */
public final class Position {

	private static final int NOT_IN_CHORD = -1;

	private final int partIndex;
	private final int staffNumber;
	private final int measureNumber;
	private final int voiceNumber;
	private final int indexInVoice;
	private final int indexInChord;

	/**
	 * Constructor for positions in parts with multiple staves.
	 *
	 * @param partIndex     the number (index) of the part in the the score
	 * @param staffNumber   the number of the staff in the part. For parts with a
	 *                      single staff use the constructor without the staffNumber
	 *                      parameter
	 * @param measureNumber the measure number
	 * @param voiceNumber   the voice number in the measure
	 * @param indexInVoice  the index in the voice specified by voiceNumber
	 */
	public Position(int partIndex, int staffNumber, int measureNumber, int voiceNumber, int indexInVoice) {
		this.partIndex = partIndex;
		this.staffNumber = staffNumber;
		this.measureNumber = measureNumber;
		this.voiceNumber = voiceNumber;
		this.indexInVoice = indexInVoice;
		this.indexInChord = NOT_IN_CHORD;
	}

	/**
	 * Constructor for positions that can be used to access a note in a chord.
	 *
	 * @param partIndex     The number (index) of the part in the score
	 * @param staffNumber   The number of the staff in the part. For single staff
	 *                      parts use the constructor without the staffNumber
	 *                      parameter
	 * @param measureNumber The measure number
	 * @param voiceNumber   The voice number in the measure
	 * @param indexInVoice  The index in the voice specified by voiceNumber
	 * @param indexInChord  Starting from the bottom of the chord, the index of the
	 *                      note
	 */
	public Position(int partIndex, int staffNumber, int measureNumber, int voiceNumber, int indexInVoice,
			int indexInChord) {
		this.partIndex = partIndex;
		this.staffNumber = staffNumber;
		this.measureNumber = measureNumber;
		this.voiceNumber = voiceNumber;
		this.indexInVoice = indexInVoice;
		this.indexInChord = indexInChord;
	}

	/**
	 * Constructor for position in a part with only a single staff.
	 *
	 * @param partIndex     the number (index) of the part in the score
	 * @param measureNumber the measure number
	 * @param voiceNumber   the voice number in the measure
	 * @param indexInVoice  the index in the voice specified by voiceNumber
	 */
	public Position(int partIndex, int measureNumber, int voiceNumber, int indexInVoice) {
		this.partIndex = partIndex;
		this.staffNumber = SingleStaffPart.STAFF_NUMBER;
		this.measureNumber = measureNumber;
		this.voiceNumber = voiceNumber;
		this.indexInVoice = indexInVoice;
		this.indexInChord = NOT_IN_CHORD;
	}

	/**
	 * Returns the part number of this position.
	 *
	 * @return The number (index) of the part in the score
	 */
	public int getPartIndex() {
		return this.partIndex;
	}

	/**
	 * Returns the staff number in the part pointed to by this position.
	 *
	 * @return the number of the staff in the part
	 */
	public int getStaffNumber() {
		return this.staffNumber;
	}

	/**
	 * Returns the number of the measure to which this position points.
	 *
	 * @return the measure number specified by this position
	 */
	public int getMeasureNumber() {
		return this.measureNumber;
	}

	/**
	 * Returns the number of the voice to which this position points.
	 *
	 * @return the number of the voice in the measure
	 */
	public int getVoiceNumber() {
		return this.voiceNumber;
	}

	/**
	 * Returns the index in the voice to which this position points.
	 *
	 * @return the index in the voice to which this position points
	 */
	public int getIndexInVoice() {
		return this.indexInVoice;
	}

	/**
	 * Returns true if this position points to a note in a chord.
	 *
	 * @return true if this position is for a note in a chord
	 */
	public boolean isInChord() {
		return this.indexInChord != NOT_IN_CHORD;
	}

	/**
	 * Returns the index of the note in a chord to which this position points.
	 *
	 * @return the index, counting from the bottom of the chord, of a note in a
	 * chord
	 */
	public int getIndexInChord() {
		return this.indexInChord;
	}

	/**
	 * Returns true if this position is equal to the given object. Two positions are
	 * equal if and only if they are equal in all fields.
	 *
	 * @param o Object with which this is compared for equality
	 * @return true if o is equal to this, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Position)) {
			return false;
		}

		final Position other = (Position) o;

		if (this.partIndex != other.getPartIndex()) {
			return false;
		}

		if (this.staffNumber != other.getStaffNumber()) {
			return false;
		}

		if (other.getMeasureNumber() != this.measureNumber) {
			return false;
		}

		if (other.getVoiceNumber() != this.voiceNumber) {
			return false;
		}

		if (other.getIndexInVoice() != this.indexInVoice) {
			return false;
		}

		if (other.getIndexInChord() != this.indexInChord) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + this.partIndex;
		hash = 53 * hash + this.staffNumber;
		hash = 53 * hash + this.measureNumber;
		hash = 53 * hash + this.voiceNumber;
		hash = 53 * hash + this.indexInVoice;
		hash = 53 * hash + this.indexInChord;
		return hash;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Part: ").append(this.partIndex).append(", Staff: ").append(this.staffNumber)
				.append(", Measure: ").append(this.measureNumber).append(", Voice: ").append(this.voiceNumber)
				.append(", Index: ").append(this.indexInVoice);

		if (isInChord()) {
			strBuilder.append(", Index in chord: ").append(this.indexInChord);
		}

		return strBuilder.toString();
	}
}
