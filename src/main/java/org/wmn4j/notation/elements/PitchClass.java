/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Represents the 12 pitch classes in equal-temperament tuning.
 */
public enum PitchClass {
	/**
	 * The pitch class C.
	 */
	C(0),

	/**
	 * The pitch class C#/Db.
	 */
	CSHARP_DFLAT(1),

	/**
	 * The pitch class D.
	 */
	D(2),

	/**
	 * The pitch class D#/Eb.
	 */
	DSHARP_EFLAT(3),

	/**
	 * The pitch class E.
	 */
	E(4),

	/**
	 * The pitch class F.
	 */
	F(5),

	/**
	 * The pitch class F#/Gb.
	 */
	FSHARP_GFLAT(6),

	/**
	 * The pitch class G.
	 */
	G(7),

	/**
	 * The pitch class G#/Ab.
	 */
	GSHARP_AFLAT(8),

	/**
	 * The pitch class A.
	 */
	A(9),

	/**
	 * The pitch class A#/Bb.
	 */
	ASHARP_BFLAT(10),

	/**
	 * The pitch class B.
	 */
	B(11);

	private static final PitchClass PITCH_CLASSES[] = { C, CSHARP_DFLAT, D, DSHARP_EFLAT, E, F, FSHARP_GFLAT, G,
			GSHARP_AFLAT, A,
			ASHARP_BFLAT, B };

	private final int number;

	PitchClass(int number) {
		this.number = number;
	}

	/**
	 * @return the <a href="http://en.wikipedia.org/wiki/Pitch_class">pitch class
	 *         number</a> of this pitch class.
	 */
	public int toInt() {
		return this.number;
	}

	/**
	 * Computes the pitch class from the pitch number.
	 *
	 * @param pitchNumber The MIDI number of the pitch.
	 * @return pitch class corresponding to the pitchNumber.
	 */
	public static PitchClass fromInt(int pitchNumber) {
		if (pitchNumber < 0) {
			throw new IllegalArgumentException("pitchNumber must be non-negative.");
		}

		return PITCH_CLASSES[pitchNumber % 12];
	}
}
