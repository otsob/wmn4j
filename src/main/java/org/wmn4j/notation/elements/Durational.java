/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Interface for all the notation objects that have a duration.
 */
public interface Durational {

	/**
	 * @return <code>Duration</code> of this.
	 */
	Duration getDuration();

	/**
	 * @return true if this is a rest, false otherwise.
	 */
	boolean isRest();
}
