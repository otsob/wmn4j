/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Basic durations.
 */
public final class Durations {
	/**
	 * Constant value for whole note duration.
	 */
	public static final Duration WHOLE = Duration.of(1, 1);

	/**
	 * Constant value for half note duration.
	 */
	public static final Duration HALF = Duration.of(1, 2);

	/**
	 * Constant value for quarter note duration.
	 */
	public static final Duration QUARTER = Duration.of(1, 4);

	/**
	 * Constant value for eight note duration.
	 */
	public static final Duration EIGHTH = Duration.of(1, 8);

	/**
	 * Constant value for sixteenth note duration.
	 */
	public static final Duration SIXTEENTH = Duration.of(1, 16);

	/**
	 * Constant value for thirtysecond note duration.
	 */
	public static final Duration THIRTYSECOND = Duration.of(1, 32);

	/**
	 * Constant value for sixtyfourth note duration.
	 */
	public static final Duration SIXTYFOURTH = Duration.of(1, 64);

	/**
	 * Constant value for quarter note triplet duration.
	 */
	public static final Duration QUARTER_TRIPLET = Duration.of(1, 6);

	/**
	 * Constant value for eight note triplet duration.
	 */
	public static final Duration EIGHTH_TRIPLET = Duration.of(1, 12);

	/**
	 * Constant value for sixteenth note triplet duration.
	 */
	public static final Duration SIXTEENTH_TRIPLET = Duration.of(1, 24);

	private Durations() {
		// Not meant to be instantiated.
		throw new AssertionError();
	}
}
