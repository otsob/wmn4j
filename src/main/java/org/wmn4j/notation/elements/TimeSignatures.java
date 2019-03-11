/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Common time signatures.
 */
public final class TimeSignatures {

	/**
	 * The time signature 4/4.
	 */
	public static final TimeSignature FOUR_FOUR = TimeSignature.of(4, 4);

	/**
	 * The time signature 3/4.
	 */
	public static final TimeSignature THREE_FOUR = TimeSignature.of(3, 4);

	/**
	 * The time signature "/4.
	 */
	public static final TimeSignature TWO_FOUR = TimeSignature.of(2, 4);

	/**
	 * The time signature 3/8.
	 */
	public static final TimeSignature THREE_EIGHT = TimeSignature.of(3, 8);

	/**
	 * The time signature 6/8.
	 */
	public static final TimeSignature SIX_EIGHT = TimeSignature.of(6, 8);

	private TimeSignatures() {
		// Not meant to be instantiated.
		throw new AssertionError();
	}
}
