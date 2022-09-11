/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.wmn4j.notation.Durational;

import java.util.Iterator;

/**
 * Interface for iterating {@link Durational} objects and retrieving their
 * positions in a {@link org.wmn4j.notation.Score}.
 * <p>
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public interface PositionIterator extends Iterator<Durational> {
	/**
	 * Returns the position of the {@link Durational} returned by the last call of
	 * {@link #next() next}. This method should only be called after {@link #next()
	 * next} has been called.
	 *
	 * @return the position of the previously returned duration notation element
	 * @throws IllegalStateException if {@link #next() next} has not been called on
	 *                               the iterator
	 */
	Position getPositionOfPrevious() throws IllegalStateException;

	@Override
	default void remove() {
		throw new UnsupportedOperationException("Removing not supported.");
	}
}
