/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.wmn4j.notation.Durational;

import java.util.Collection;
import java.util.List;

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

	/**
	 * Returns the indices of the parts in this selection in ascending order.
	 *
	 * @return the indices of the parts in this selection in ascending order
	 */
	List<Integer> getPartIndices();

	/**
	 * Returns an iterator that also provides access to the positions of the {@link Durational}
	 * objects in this selection.
	 *
	 * @return an iterator that also provides access to the positions
	 */
	PositionalIterator positionalIterator();

	/**
	 * Returns a selection of measures from this selection.
	 *
	 * @param firstMeasure the measure number of the first measure included in the selection
	 * @param lastMeasure  the measure number of the last measure included in the selection
	 * @return a subrange of measures from this selection
	 */
	Selection subSelection(int firstMeasure, int lastMeasure);

	/**
	 * Returns a selection or parts from this selection.
	 *
	 * @param partIndices the indices of the parts to select
	 * @return a selection or parts from this selection
	 */
	Selection subSelection(Collection<Integer> partIndices);
}
