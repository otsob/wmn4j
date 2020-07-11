/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Represents builders that can be connected through {@link Notation} objects.
 */
public interface ConnectableBuilder {
	/**
	 * Connects this builder to the given note builder with the specified notation.
	 * <p>
	 * The connections set using this method are automatically resolved and built when the note builder is built.
	 *
	 * @param notation          the notation with which this is connected to the target
	 * @param targetNoteBuilder the note builder to which this is connected using the given notation
	 */
	void connectWith(Notation notation, NoteBuilder targetNoteBuilder);

	/**
	 * Connects this builder to the given grace note builder with the specified notation.
	 * <p>
	 * The connections set using this method are automatically resolved and built when the note builder is built.
	 *
	 * @param notation          the notation with which this is connected to the target
	 * @param targetNoteBuilder the grace note builder to which this is connected using the given notation
	 */
	void connectWith(Notation notation, GraceNoteBuilder targetNoteBuilder);
}
