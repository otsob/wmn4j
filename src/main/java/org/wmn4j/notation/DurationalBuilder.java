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
	 * @return reference to this
	 */
	DurationalBuilder setDuration(Duration duration);

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

	/**
	 * Converts this to RestBuilder if this is a RestBuilder.
	 * Otherwise throws exception.
	 *
	 * @return this as a rest builder
	 */
	default RestBuilder toRestBuilder() {
		return (RestBuilder) this;
	}

	/**
	 * Converts this to NoteBuilder if this is a NoteBuilder.
	 * Otherwise throws exception.
	 *
	 * @return this as a note builder
	 */
	default NoteBuilder toNoteBuilder() {
		return (NoteBuilder) this;
	}

	/**
	 * Converts this to ChordBuilder if this is a ChordBuilder.
	 * Otherwise throws exception.
	 *
	 * @return this as a chord builder
	 */
	default ChordBuilder toChordBuilder() {
		return (ChordBuilder) this;
	}
}
