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
	 * Defines the type of the ornamental note.
	 */
	enum Type {

		/**
		 * Specifies an acciaccatura ornamental.
		 */
		ACCIACCATURA,

		/**
		 * Specifies an appoggiature ornamental.
		 */
		APPOGGIATURA,

		/**
		 * Specifies a general type of ornamental grace note.
		 */
		GRACE_NOTE
	}

	/**
	 * Returns the duration type to show for this ornamental note.
	 *
	 * @return the duration type to show for this ornamental note
	 */
	Duration getDisplayableDuration();

	/**
	 * Returns the type of this ornamental.
	 *
	 * @return the type of this ornamental
	 */
	Type getType();
}
