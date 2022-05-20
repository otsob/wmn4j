/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Interface for all the notation objects that have a duration.
 * This interface is not intended to be implemented by classes other
 * than the basic notation classes {@link Note}, {@link Rest}, and {@link Chord}.
 * <p>
 * All implementations of this interface must be thread-safe.
 */
public sealed interface Durational permits Note, Rest, Chord {

	/**
	 * Returns the duration of this.
	 *
	 * @return the duration of this
	 */
	Duration getDuration();

	/**
	 * Returns true if this is a rest, false otherwise.
	 *
	 * @return true if this is a rest, false otherwise
	 */
	boolean isRest();
}
