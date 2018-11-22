/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jnotation.noteobjects;

import java.util.Objects;

/**
 * The <code>Pitch</code> class represents a pitch. Pitches consist of the basic
 * pitch letter <code>Pitch.Base</code>, alter number which tells by how many
 * half-steps the pitch is altered, and octave number which tells the octave of
 * the note. Octave number is based on
 * <a href="http://en.wikipedia.org/wiki/Scientific_pitch_notation">scientific
 * pitch notation</a>. This class is immutable.
 * 
 * @author Otso Björklund
 */
public class Pitch implements Comparable<Pitch> {

	/**
	 * The letter in a pitch name.
	 */
	// TODO: Add the number to the enum.
	public enum Base {
		C, D, E, F, G, A, B
	}

	/**
	 * The limit for altering notes in half-steps (=3).
	 */
	public static final int ALTER_LIMIT = 3;
	/**
	 * Highest allowed octave number (=10).
	 */
	public static final int MAX_OCTAVE = 10;

	private final Base pitchBase;
	private final int alter;
	private final int octave;

	/**
	 * Returns a <code>Pitch</code> object.
	 * 
	 * @throws IllegalArgumentException
	 *             if alter is greater than {@link #ALTER_LIMIT ALTER_LIMIT} of
	 *             smaller than {@link #ALTER_LIMIT -1*ALTER_LIMIT}, or if octave is
	 *             negative or larger than {@link #MAX_OCTAVE MAX_OCTAVE}.
	 * @param pitchName
	 *            the letter on which the name of the pitch is based.
	 * @param alter
	 *            by how many half-steps the pitch is altered up (positive values)
	 *            or down (negative values).
	 * @param octave
	 *            the octave of the pitch.
	 * @return Pitch object with the specified attributes.
	 */
	public static Pitch getPitch(Base pitchName, int alter, int octave) {
		if (alter > ALTER_LIMIT || alter < -1 * ALTER_LIMIT)
			throw new IllegalArgumentException(
					"alter was " + alter + ". alter must be between -" + ALTER_LIMIT + " and " + ALTER_LIMIT);

		if (octave < 0 || octave > MAX_OCTAVE)
			throw new IllegalArgumentException("octave was " + octave + ". octave must be between 0 and " + MAX_OCTAVE);

		return new Pitch(pitchName, alter, octave);
	}

	/**
	 * Private constructor. To get a <code>Pitch</code> object use the method
	 * {@link #getPitch(wmnlibnotation.Pitch.Base, int, int) getPitch}.
	 * 
	 * @param pitchName
	 *            the letter on which the name of the pitch is based.
	 * @param alter
	 *            by how many half-steps the pitch is altered up or down.
	 * @param octave
	 *            the octave of the pitch.
	 */
	private Pitch(Base pitchName, int alter, int octave) {
		this.pitchBase = pitchName;
		this.alter = alter;
		this.octave = octave;
	}

	/**
	 * Returns the letter in the pitch name.
	 * 
	 * @return the letter on which the name of the pitch is based.
	 */
	public Base getBase() {
		return this.pitchBase;
	}

	/**
	 * Returns by how many half-steps the pitch is altered.
	 * 
	 * @return by how many half-steps the pitch is altered up (positive value) or
	 *         down (negative value).
	 */
	public int getAlter() {
		return this.alter;
	}

	/**
	 * Returns the octave number.
	 * 
	 * @return octave number of this pitch.
	 */
	public int getOctave() {
		return this.octave;
	}

	/**
	 * Get an integer representation of this <code>Pitch</code>. This is the MIDI
	 * number of the pitch (middle-C, C4, being 60). A <code>Pitch</code> is
	 * transformed into an integer using the formula
	 * <code> pitchAsInteger = base + alter + (octave + 1) * 12 </code>, where base
	 * is defined by the letter in the pitch name: C = 0, D = 2, E = 4, F = 5, G =
	 * 7, A = 9, B = 11. Alter is the number of sharps, or the number of flats * -1.
	 * For example, <code>C#4 = 0 + 1 + 5 * 12 = 61</code> and
	 * <code>Db4 = 2 - 1 + 5 * 12 = 61</code>.
	 * 
	 * @return this Pitch as an integer.
	 */
	public int toInt() {
		int pitchAsInt;
		// TODO: Instead of using the switch statement, add number to enum.
		switch (this.pitchBase) {
		case C:
			pitchAsInt = 0;
			break;
		case D:
			pitchAsInt = 2;
			break;
		case E:
			pitchAsInt = 4;
			break;
		case F:
			pitchAsInt = 5;
			break;
		case G:
			pitchAsInt = 7;
			break;
		case A:
			pitchAsInt = 9;
			break;
		case B:
			pitchAsInt = 11;
			break;
		default:
			pitchAsInt = 0;
		}

		return pitchAsInt + this.alter + (this.octave + 1) * 12;
	}

	/**
	 * Returns the PitchClass of this Pitch.
	 * 
	 * @return the <a href="http://en.wikipedia.org/wiki/Pitch_class">pitch
	 *         class</a> of this Pitch.
	 */
	public PitchClass getPitchClass() {
		return PitchClass.fromInt(this.toInt());
	}

	/**
	 * Returns the pitch class number of this pitch.
	 * 
	 * @return pitch class number of this pitch.
	 */
	public int getPCNumber() {
		return this.toInt() % 12;
	}

	/**
	 * Test enharmonic equality. Compare this to other for
	 * <a href="http://en.wikipedia.org/wiki/Enharmonic">enharmonic</a> equality.
	 * 
	 * @param other
	 *            Pitch against which this is compared.
	 * @return true if this is enharmonically equal to other, otherwise false.
	 */
	public boolean equalsEnharmonically(Pitch other) {
		return this.toInt() == other.toInt();
	}

	/**
	 * String representation of <code>Pitch</code>. <code>Pitch</code> objects are
	 * represented as strings of form <code>bao</code>, where <code>b</code> is the
	 * base letter in the pitch name, <code>a</code> is the alteration (sharps # or
	 * flats b), and <code>o</code> is the octave number. For example middle C-sharp
	 * is represented as the string <code>C#4</code>.
	 * 
	 * @return the string representation of this Pitch.
	 */
	@Override
	public String toString() {
		String pitchName = this.pitchBase.toString();

		if (alter >= 0) {
			for (int i = 0; i < alter; ++i)
				pitchName += "#";
		} else {
			for (int i = 0; i > alter; --i)
				pitchName += "b";
		}

		return pitchName + octave;
	}

	/**
	 * Compare this <code>Pitch</code> for equality with <code>Object o</code>. Two
	 * objects of class <code>Pitch</code> are equal if they have the same ' base
	 * letter, alterations (sharps or flats), and same octave. Pitches that are
	 * enharmonically the same but spelt differently are not equal. For example,
	 * <code>C#4 != Db4</code>.
	 * 
	 * @param o
	 *            Object against which this is compared for equality.
	 * @return true if Object o is of class Pitch and has the same, pitch base,
	 *         alterations, and octave as this.
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this)
			return true;

		if (!(o instanceof Pitch))
			return false;

		Pitch other = (Pitch) o;

		if (other.pitchBase != this.pitchBase)
			return false;

		if (other.octave != this.octave)
			return false;

		if (other.alter != this.alter)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + Objects.hashCode(this.pitchBase);
		hash = 89 * hash + this.alter;
		hash = 89 * hash + this.octave;
		return hash;
	}

	/**
	 * Compare this pitch against other for pitch height.
	 * 
	 * @param other
	 *            the Pitch against which this is compared.
	 * @return negative integer if this is lower than other, positive integer if
	 *         this is higher than other, 0 if pitches are (enharmonically) of same
	 *         height.
	 */
	@Override
	public int compareTo(Pitch other) {
		return this.toInt() - other.toInt();
	}

	/**
	 * @param other
	 *            the Pitch against which this is compared.
	 * @return true if this is higher than other, false otherwise.
	 */
	public boolean higherThan(Pitch other) {
		return this.toInt() > other.toInt();
	}

	/**
	 * @param other
	 *            the Pitch against which this is compared.
	 * @return true if this is lower than other, false otherwise.
	 */
	public boolean lowerThan(Pitch other) {
		return this.toInt() < other.toInt();
	}
}
