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
	default boolean isRest() {
		return false;
	}

	/**
	 * Returns true if this is a note, false otherwise.
	 *
	 * @return true if this is a note, false otherwise
	 */
	default boolean isNote() {
		return false;
	}

	/**
	 * Returns true if this is a chord, false otherwise.
	 *
	 * @return true if this is a chord, false otherwise
	 */
	default boolean isChord() {
		return false;
	}

	/**
	 * Converts this to Rest if this is a rest.
	 * Otherwise throws exception.
	 *
	 * @return this as a rest
	 */
	default Rest toRest() {
		return (Rest) this;
	}

	/**
	 * Converts this to Note if this is a note.
	 * Otherwise throws exception.
	 *
	 * @return this as a note
	 */
	default Note toNote() {
		return (Note) this;
	}

	/**
	 * Converts this to Chord if this is a chord.
	 * Otherwise throws exception.
	 *
	 * @return this as a chord
	 */
	default Chord toChord() {
		return (Chord) this;
	}
}
