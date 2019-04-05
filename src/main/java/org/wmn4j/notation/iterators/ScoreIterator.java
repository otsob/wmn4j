/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.iterators;

import org.wmn4j.notation.elements.Durational;

import java.util.Iterator;

/**
 * Interface for iterators that iterate through the
 * {@link org.wmn4j.notation.elements.Durational} objects in a
 * {@link org.wmn4j.notation.elements.Score}.
 */
public interface ScoreIterator extends Iterator<Durational> {

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

	@Override
	default void remove() {
		throw new UnsupportedOperationException("Removing not supported.");
	}
}
