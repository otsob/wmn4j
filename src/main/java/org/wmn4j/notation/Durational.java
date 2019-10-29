/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Interface for all the notation objects that have a duration.
 */
public interface Durational {

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
