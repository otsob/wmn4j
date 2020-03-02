/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Common time signatures.
 */
public final class TimeSignatures {

	/**
	 * The common time signature.
	 */
	public static final TimeSignature COMMON = TimeSignature.of(4, Durations.QUARTER, TimeSignature.Symbol.COMMON);

	/**
	 * The cut time (alla breve) time signature.
	 */
	public static final TimeSignature CUT_TIME = TimeSignature.of(2, Durations.HALF, TimeSignature.Symbol.CUT_TIME);

	/**
	 * The time signature 4/4.
	 */
	public static final TimeSignature FOUR_FOUR = TimeSignature.of(4, 4);

	/**
	 * The time signature 3/4.
	 */
	public static final TimeSignature THREE_FOUR = TimeSignature.of(3, 4);

	/**
	 * The time signature 2/4.
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
