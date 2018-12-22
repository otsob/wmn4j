/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Collection of basic durations.
 *
 * @author Otso Björklund
 */
public final class Durations {
	/**
	 * Constant value for whole note duration.
	 */
	public static final Duration WHOLE = Duration.getDuration(1, 1);

	/**
	 * Constant value for half note duration.
	 */
	public static final Duration HALF = Duration.getDuration(1, 2);

	/**
	 * Constant value for quarter note duration.
	 */
	public static final Duration QUARTER = Duration.getDuration(1, 4);

	/**
	 * Constant value for eight note duration.
	 */
	public static final Duration EIGHT = Duration.getDuration(1, 8);

	/**
	 * Constant value for sixteenth note duration.
	 */
	public static final Duration SIXTEENTH = Duration.getDuration(1, 16);

	/**
	 * Constant value for thirtysecond note duration.
	 */
	public static final Duration THIRTYSECOND = Duration.getDuration(1, 32);

	/**
	 * Constant value for sixtyfourth note duration.
	 */
	public static final Duration SIXTYFOURTH = Duration.getDuration(1, 64);

	/**
	 * Constant value for quarter note triplet duration.
	 */
	public static final Duration QUARTER_TRIPLET = Duration.getDuration(1, 6);

	/**
	 * Constant value for eight note triplet duration.
	 */
	public static final Duration EIGHT_TRIPLET = Duration.getDuration(1, 12);

	/**
	 * Constant value for sixteenth note triplet duration.
	 */
	public static final Duration SIXTEENTH_TRIPLET = Duration.getDuration(1, 24);

	private Durations() {
		// Not meant to be instantiated.
		throw new AssertionError();
	}
}
