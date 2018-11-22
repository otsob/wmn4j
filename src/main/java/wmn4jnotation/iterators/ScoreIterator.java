/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jnotation.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import wmn4jnotation.noteobjects.Durational;

/**
 * Interface for iterators that iterate through the <code>Durational</code>
 * objects in a <code>Score</code>.
 * 
 * @author Otso Björklund
 */
public interface ScoreIterator extends Iterator<Durational> {

	/**
	 * Checks if the <code>ScoreIterator</code> is at the end of the score.
	 * 
	 * @return true if not at the end, false otherwise.
	 */
	public boolean hasNext();

	/**
	 * Get the next <code>Durational</code> from the score. Order of iteration
	 * depends on the implementation.
	 * 
	 * @return next <code>Durational</code>.
	 * @throws NoSuchElementException if next <code>Durational</code> is not
	 *                                available.
	 */
	public Durational next() throws NoSuchElementException;

	/**
	 * Returns the position of the <code>Durational</code> returned by the last call
	 * of {@link #next() next}. This method should only be called after
	 * {@link #next() next} has been called.
	 * 
	 * @return <code>ScorePosition</code> of the previously returned
	 *         <code>Durational</code>.
	 * @throws IllegalStateException if {@link #next() next} has not been called on
	 *                               the iterator
	 */
	public ScorePosition positionOfPrevious() throws IllegalStateException;
}
