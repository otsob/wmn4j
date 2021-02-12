/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.wmn4j.notation.Durational;

/**
 * Represents a selection of notation objects from a score.
 * <p>
 * A selection can be a range of measures from a score, or a selection of measures
 * from specific parts of a score.
 * <p>
 * Implementations of this class are immutable.
 */
public interface Selection extends Iterable<Durational> {

	/**
	 * Returns the measure number from which the selection range
	 * starts (inclusive).
	 *
	 * @return the measure number from which the selection range
	 * starts  (inclusive)
	 */
	int getFirst();

	/**
	 * Returns the measure number to which the selection range ends (inclusive).
	 *
	 * @return the measure number to which the selection range ends (inclusive)
	 */
	int getLast();
}
