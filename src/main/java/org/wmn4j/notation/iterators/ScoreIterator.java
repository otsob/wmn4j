/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.wmn4j.notation.elements.Durational;

/**
 * Interface for iterators that iterate through the
 * {@link org.wmn4j.notation.elements.Durational} objects in a
 * {@link org.wmn4j.notation.elements.Score}.
 */
public interface ScoreIterator extends Iterator<Durational> {

	/**
	 * Returns true if this iterator has elements left.
	 *
	 * @return true if not at the end, false otherwise
	 */
	@Override
	boolean hasNext();

	/**
	 * Get the next durational notation element from the score. Order of iteration
	 * depends on the implementation.
	 *
	 * @return next durational element
	 * @throws NoSuchElementException if this iterator has already reached the end
	 *                                of the score
	 */
	@Override
	Durational next() throws NoSuchElementException;

	/**
	 * Returns the position of the {@link Durational} returned by the last call of
	 * {@link #next() next}. This method should only be called after {@link #next()
	 * next} has been called.
	 *
	 * @return the position of the previously returned duration notation element
	 * @throws IllegalStateException if {@link #next() next} has not been called on
	 *                               the iterator
	 */
	ScorePosition getPositionOfPrevious() throws IllegalStateException;
}
