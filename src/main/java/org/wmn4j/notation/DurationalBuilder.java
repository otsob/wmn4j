/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Interface for builders that build {@link Durational} objects.
 * <p>
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public sealed interface DurationalBuilder permits NoteBuilder, RestBuilder, ChordBuilder {

	/**
	 * Returns a durational notation element with the values set in the builder.
	 *
	 * @return a durational notation element with the values set in the builder
	 */
	Durational build();

	/**
	 * Returns the duration set in this builder.
	 *
	 * @return the duration set in this builder
	 */
	Duration getDuration();

	/**
	 * Sets the duration of this builder to the given value.
	 *
	 * @param duration the duration that is set to this builder
	 */
	void setDuration(Duration duration);

	/**
	 * Returns true if this is a rest builder, false otherwise.
	 *
	 * @return true if this is a rest builder, false otherwise
	 */
	default boolean isRestBuilder() {
		return false;
	}

	/**
	 * Returns true if this is a note builder, false otherwise.
	 *
	 * @return true if this is a note builder, false otherwise
	 */
	default boolean isNoteBuilder() {
		return false;
	}

	/**
	 * Returns true if this is a chord builder, false otherwise.
	 *
	 * @return true if this is a chord builder, false otherwise
	 */
	default boolean isChordBuilder() {
		return false;
	}
}
