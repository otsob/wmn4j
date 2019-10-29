package org.wmn4j.notation.access;

import org.wmn4j.notation.Durational;

import java.util.Iterator;

/**
 * Iterator for {@link Durational} objects in a measure. The iterator iterates
 * through the notes in the Measure voice by voice going from the earliest
 * durational in the voice to the last on each voice. The order of voices is
 * unspecified. The iterator does not support removing.
 * <p>
 * Instances of this class are not thread-safe.
 */
public interface MeasureIterator extends Iterator<Durational> {

	/**
	 * Returns the voice of the {@link Durational} that was returned by the last
	 * call of {@link #next() next}.
	 *
	 * @return the voice of the {@link Durational} that was returned by the last
	 * call of {@link #next() next}. If next has not been called, return
	 * value is useless.
	 */
	int getVoiceOfPrevious();

	/**
	 * Returns the index of the {@link Durational} that was returned by the last
	 * call of {@link #next() next}.
	 *
	 * @return the index of the {@link Durational} that was returned by the last
	 * call of {@link #next() next}. If next has not been called, return
	 * value is useless.
	 */
	int getIndexOfPrevious();

	@Override
	default void remove() {
		throw new UnsupportedOperationException("Removing not supported.");
	}
}

