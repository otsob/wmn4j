/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jnotation.iterators;

import wmn4jnotation.noteobjects.SingleStaffPart;

/**
 * Defines the position of a <code>Durational</code> in a <code>Score</code>. Is
 * immutable.
 * 
 * @author Otso Björklund
 */
public class ScorePosition {

	private final int partNumber;
	private final int staffNumber;
	private final int measureNumber;
	private final int voiceNumber;
	private final int indexInVoice;
	private final int indexInChord;

	private static final int NOT_IN_CHORD = -1;

	/**
	 * Constructor for <code>ScorePosition</code> for parts with multiple staves.
	 * 
	 * @param partNumber
	 *            The number (index) of the part in the <code>Score</code>.
	 * @param staffNumber
	 *            The number of the staff in the <code>Part</code>. For
	 *            <code>SingleStaffPart</code> objects use the constructor without
	 *            the staffNumber parameter.
	 * @param measureNumber
	 *            The measure number.
	 * @param voiceNumber
	 *            The voice number in the measure.
	 * @param indexInVoice
	 *            The index in the voice specified by voiceNumber.
	 */
	public ScorePosition(int partNumber, int staffNumber, int measureNumber, int voiceNumber, int indexInVoice) {
		this.partNumber = partNumber;
		this.staffNumber = staffNumber;
		this.measureNumber = measureNumber;
		this.voiceNumber = voiceNumber;
		this.indexInVoice = indexInVoice;
		this.indexInChord = NOT_IN_CHORD;
	}

	/**
	 * Constructor for <code>ScorePosition</code> that can be used to access a note
	 * in a chord.
	 * 
	 * @param partNumber
	 *            The number (index) of the part in the <code>Score</code>.
	 * @param staffNumber
	 *            The number of the staff in the <code>Part</code>. For
	 *            <code>SingleStaffPart</code> objects use the constructor without
	 *            the staffNumber parameter.
	 * @param measureNumber
	 *            The measure number.
	 * @param voiceNumber
	 *            The voice number in the measure.
	 * @param indexInVoice
	 *            The index in the voice specified by voiceNumber.
	 * @param indexInChord
	 *            Starting from the bottom of the chord, the index of the Note.
	 */
	public ScorePosition(int partNumber, int staffNumber, int measureNumber, int voiceNumber, int indexInVoice,
			int indexInChord) {
		this.partNumber = partNumber;
		this.staffNumber = staffNumber;
		this.measureNumber = measureNumber;
		this.voiceNumber = voiceNumber;
		this.indexInVoice = indexInVoice;
		this.indexInChord = indexInChord;
	}

	/**
	 * Constructor for position in a part with only a single staff.
	 * 
	 * @param partNumber
	 *            The number (index) of the part in the <code>Score</code>.
	 * @param measureNumber
	 *            The measure number.
	 * @param voiceNumber
	 *            The voice number in the measure.
	 * @param indexInVoice
	 *            The index in the voice specified by voiceNumber.
	 */
	public ScorePosition(int partNumber, int measureNumber, int voiceNumber, int indexInVoice) {
		this.partNumber = partNumber;
		this.staffNumber = SingleStaffPart.STAFF_NUMBER;
		this.measureNumber = measureNumber;
		this.voiceNumber = voiceNumber;
		this.indexInVoice = indexInVoice;
		this.indexInChord = NOT_IN_CHORD;
	}

	/**
	 * @return The number (index) of the part in the score.
	 */
	public int getPartNumber() {
		return this.partNumber;
	}

	/**
	 * @return The number of the staff in the part.
	 */
	public int getStaffNumber() {
		return this.staffNumber;
	}

	/**
	 * @return The measure number specified by this position.
	 */
	public int getMeasureNumber() {
		return this.measureNumber;
	}

	/**
	 * @return The number of the voice in the measure.
	 */
	public int getVoiceNumber() {
		return this.voiceNumber;
	}

	/**
	 * @return The index of the <code>Durational</code> in the voice.
	 */
	public int getIndexInVoice() {
		return this.indexInVoice;
	}

	/**
	 * Check if this position refers to note in a chord.
	 * 
	 * @return true if this position is for a note in a chord.
	 */
	public boolean isInChord() {
		return this.indexInChord != NOT_IN_CHORD;
	}

	/**
	 * Get the index of the note in a chord specified by this position.
	 * 
	 * @return the index, counting from the bottom of the chord, of a note in a
	 *         chord.
	 */
	public int getIndexInChord() {
		return this.indexInChord;
	}

	/**
	 * Compares this <code>ScorePosition</code> with equality against
	 * <code>Object o</code>. Two positions are equal if and only if all of their
	 * properties are equal.
	 * 
	 * @param o
	 *            Object with which this is compared for equality.
	 * @return True if o is equal to this, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof ScorePosition))
			return false;

		ScorePosition other = (ScorePosition) o;

		if (this.partNumber != other.getPartNumber())
			return false;

		if (this.staffNumber != other.getStaffNumber())
			return false;

		if (other.getMeasureNumber() != this.measureNumber)
			return false;

		if (other.getVoiceNumber() != this.voiceNumber)
			return false;

		if (other.getIndexInVoice() != this.indexInVoice)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + this.partNumber;
		hash = 53 * hash + this.staffNumber;
		hash = 53 * hash + this.measureNumber;
		hash = 53 * hash + this.voiceNumber;
		hash = 53 * hash + this.indexInVoice;
		return hash;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Part: ").append(this.partNumber).append(", Staff: ").append(this.staffNumber)
				.append(", Measure: ").append(this.measureNumber).append(", Voice: ").append(this.voiceNumber)
				.append(", Index: ").append(this.indexInVoice);

		return strBuilder.toString();
	}
}
