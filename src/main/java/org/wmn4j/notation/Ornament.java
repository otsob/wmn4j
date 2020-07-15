/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents an ornament, such as a trill or mordent. This class represents ornaments that are linked to
 * a single note, connected notations are represented using the class {@link Notation}.
 * <p>
 * This class is immutable.
 */
public final class Ornament {

	/**
	 * Defines the type of an ornament.
	 */
	public enum Type {
		/**
		 * Specifies a delayed and inverted turn (gruppetto).
		 */
		DELAYED_INVERTED_TURN,

		/**
		 * Specifies a delayed turn (gruppetto).
		 */
		DELAYED_TURN,

		/**
		 * Specifies an inverted mordent.
		 */
		INVERTED_MORDENT,

		/**
		 * Specifies an inverted turn (gruppetto).
		 */
		INVERTED_TURN,

		/**
		 * Specifies a mordent.
		 */
		MORDENT,

		/**
		 * Specifies a single tremolo marking.
		 */
		SINGLE_TREMOLO,

		/**
		 * Specifies a double tremolo marking.
		 */
		DOUBLE_TREMOLO,

		/**
		 * Specifies a triple tremolo marking.
		 */
		TRIPLE_TREMOLO,

		/**
		 * Specifies a trill marking.
		 */
		TRILL,

		/**
		 * Specifies a turn (gruppetto).
		 */
		TURN,

		/**
		 * Specifies one or more grace notes before the actual note.
		 */
		GRACE_NOTES,

		/**
		 * Specifies one or more grace notes to be played after the actual note.
		 */
		SUCCEEDING_GRACE_NOTES,
	}

	/**
	 * Returns an ornament of the given type.
	 *
	 * @param type the type of the ornament
	 * @return an ornament of the given type
	 */
	public static Ornament of(Type type) {
		return new Ornament(type);
	}

	/**
	 * Returns a grace notes ornament.
	 * For connecting grace notes to the principle note it is best to use the {@link NoteBuilder} and {@link
	 * GraceNoteBuilder}
	 * classes when creating notes with grace notes.
	 *
	 * @param ornamentalNotes the ornamental notes in the preceding list of grace notes
	 * @return a grace notes ornament
	 */
	public static Ornament graceNotes(List<? extends Ornamental> ornamentalNotes) {
		return new Ornament(Type.GRACE_NOTES, ornamentalNotes);
	}

	/**
	 * Returns a succeeding grace notes ornament.
	 *
	 * @param ornamentalNotes the ornamental notes in the succeeding list of grace notes
	 * @return a succeeding grace notes ornament
	 */
	public static Ornament succeedingGraceNotes(List<? extends Ornamental> ornamentalNotes) {
		return new Ornament(Type.SUCCEEDING_GRACE_NOTES, ornamentalNotes);
	}

	private final Type type;
	private final List<Ornamental> ornamentalNotes;

	private Ornament(Type type) {
		this.type = type;
		this.ornamentalNotes = Collections.emptyList();
	}

	private Ornament(Type type, List<? extends Ornamental> ornamentalNotes) {
		this.type = type;
		this.ornamentalNotes = Collections.unmodifiableList(new ArrayList<>(ornamentalNotes));
	}

	/**
	 * Returns the type of this ornament.
	 *
	 * @return the type of this ornament
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the ornamental notes associated with this ornament.
	 * Returns empty if there are no ornamental notes, e.g., grace notes
	 * associated with this ornamental.
	 *
	 * @return the ornamental notes associated with this ornament
	 */
	public List<Ornamental> getOrnamentalNotes() {
		return ornamentalNotes;
	}
}
