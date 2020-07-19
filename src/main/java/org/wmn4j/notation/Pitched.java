/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Interface for notation elements that have pitch.
 */
public interface Pitched {

	/**
	 * Returns the pitch of the notation element.
	 *
	 * @return the pitch of the notation element
	 */
	Pitch getPitch();

	/**
	 * Returns an integer that specifies if this has higher, lower, or
	 * equal pitch to the given pitched notation element.
	 *
	 * @param other the piched element to which this is compared for pitch
	 * @return negative integer if this is lower than other, positive integer
	 * if this is higher than other, 0 if this and other are (enharmonically) of same
	 * pitch height.
	 */
	default int compareByPitch(Pitched other) {
		return getPitch().compareTo(other.getPitch());
	}
}
