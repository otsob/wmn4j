/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Represents an ornamental note or chord that does not have a duration
 * that would take up some of the notated duration of a measure.
 * <p>
 * All implementations of this interface should be immutable.
 */
public interface Ornamental {

	/**
	 * Returns the duration type to show for this ornamental note.
	 *
	 * @return the duration type to show for this ornamental note
	 */
	Duration getDisplayableDuration();
}
