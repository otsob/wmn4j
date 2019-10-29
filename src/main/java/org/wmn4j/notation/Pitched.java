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
}
