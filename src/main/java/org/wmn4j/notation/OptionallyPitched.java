/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Optional;

/**
 * Interface for notation elements that can have pitch
 * and are positioned on a staff.
 */
public interface OptionallyPitched {

	/**
	 * Returns true if this has a pitch, otherwise false.
	 *
	 * @return true if this has a pitch
	 */
	boolean hasPitch();

	/**
	 * Returns the concert pitch of the notation element if present.
	 * For unpitched notation elements returns empty.
	 *
	 * @return concert pitch of the notation element if present
	 */
	Optional<Pitch> getPitch();

	/**
	 * Returns the pitch that indicates the staff position of
	 * the notation element.
	 *
	 * @return the pitch that indicates the staff position of
	 * the notation element
	 */
	Pitch getDisplayPitch();

	/**
	 * Returns an integer that specifies if this has higher, lower, or
	 * equal pitch to the given pitched notation element. For unpitched
	 * elements defaults to display pitch.
	 *
	 * @param other the piched element to which this is compared for pitch
	 * @return negative integer if this is lower than other, positive integer
	 * if this is higher than other, 0 if this and other are (enharmonically) of same
	 * pitch height.
	 */
	default int compareByPitch(OptionallyPitched other) {
		return getPitch().orElseGet(this::getDisplayPitch)
				.compareTo(other.getPitch().orElseGet(other::getDisplayPitch));
	}
}
