/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an articulation marking that spans across multiple notes, such as
 * a slur or a glissando. The articulations are represented as a connection
 * between adjacent notes. For example, if three notes are affected the same
 * slur, the first will be connected to the second note and the second note will
 * be connected to the third note. Is immutable.
 */
public final class LinkedArticulation {

	private final Marking marking;
	private final Note followingNote;
	private final boolean isBeginning;
	private final boolean isEnd;

	/**
	 * Returns an articulation connection with the given marking from the first note
	 * affected by the articulation.
	 *
	 * @param marking       the marking used for the articulation
	 * @param followingNote the note to which this connects
	 * @return an articulation connection with the given marking from the first note
	 * affected by the articulation
	 */
	public static LinkedArticulation beginningOf(Marking marking, Note followingNote) {
		return new LinkedArticulation(marking, Objects.requireNonNull(followingNote), true, false);
	}

	/**
	 * Returns an articulation connection with the given marking to the given note.
	 *
	 * @param marking       the marking used for the articulation
	 * @param followingNote the note to which this connects
	 * @return an articulation connection with the given marking to the given note
	 */
	public static LinkedArticulation of(Marking marking, Note followingNote) {
		return new LinkedArticulation(marking, Objects.requireNonNull(followingNote), false, false);
	}

	/**
	 * Returns an articulation connection with the given marking that is used to
	 * denote the last note affected by the articulation.
	 *
	 * @param marking the marking used for the articulation
	 * @return an articulation connection with the given marking that is used to
	 * denote the last note affected by the articulation
	 */
	public static LinkedArticulation endOf(Marking marking) {
		return new LinkedArticulation(marking, null, false, true);
	}

	/**
	 * Constructor.
	 *
	 * @param marking       the marking used for the articulation
	 * @param followingNote the note to which this articulation is connected
	 * @param isBeginning   true if this is to be attached to the first note
	 *                      affected by this articulation
	 * @param isEnd         true if this is to be attached to the last note affected
	 *                      by this articulation
	 */
	private LinkedArticulation(Marking marking, Note followingNote, boolean isBeginning,
			boolean isEnd) {
		this.marking = Objects.requireNonNull(marking);
		this.followingNote = followingNote;
		this.isBeginning = isBeginning;
		this.isEnd = isEnd;
	}

	/**
	 * Returns the marking used for this articulation.
	 *
	 * @return the marking used for this articulation
	 */
	public Marking getMarking() {
		return marking;
	}

	/**
	 * Returns the following note, i.e., the note to which this articulation
	 * connects. If this link denotes the last note affected by the articulation,
	 * then returns empty.
	 *
	 * @return the following note
	 */
	public Optional<Note> getFollowingNote() {
		return Optional.ofNullable(followingNote);
	}

	/**
	 * Returns all following notes that are affected by the same marking to which this linked articulation belongs.
	 *
	 * @return all following notes that are affected by the same marking to which this linked articulation belongs
	 */
	public Iterable<Note> getFollowingNotes() {
		return new LinkedArticulationIterable(this);
	}

	/**
	 * Returns true if this denotes the connection starting from the first note
	 * affected by the articulation.
	 *
	 * @return true if this denotes the connection starting from the first note
	 * affected by the articulation
	 */
	public boolean isBeginning() {
		return isBeginning;
	}

	/**
	 * Returns true if this denotes the end of the articulation.
	 *
	 * @return true if this denotes the end of the articulation
	 */
	public boolean isEnd() {
		return isEnd;
	}

	/**
	 * Returns the type of this articulation.
	 *
	 * @return the type of this articulation
	 */
	public Marking.Type getType() {
		return marking.getType();
	}

	/**
	 * Represents the marking used for an articulation.
	 */
	public static final class Marking {

		/**
		 * The type of the articulation.
		 */
		public enum Type {
			/**
			 * Specifies a slur.
			 */
			SLUR,

			/**
			 * Specifies a glissando marking.
			 */
			GLISSANDO
		}

		private final Type type;

		/**
		 * Returns a marking with the given type.
		 *
		 * @param type the type of the marking
		 * @return a marking with the given type
		 */
		public static Marking of(Type type) {
			return new Marking(type);
		}

		private Marking(Type type) {
			this.type = type;
		}

		/**
		 * Returns the type of the marking.
		 *
		 * @return the type of the marking
		 */
		public Type getType() {
			return type;
		}
	}

	private static final class LinkedArticulationIterable implements Iterable<Note> {

		private final LinkedArticulation first;

		LinkedArticulationIterable(LinkedArticulation first) {
			this.first = first;
		}

		@Override
		public Iterator<Note> iterator() {
			return new LinkedArticulationIterator(first);
		}

		private static final class LinkedArticulationIterator implements Iterator<Note> {

			private Optional<LinkedArticulation> current;
			private final Marking marking;

			private LinkedArticulationIterator(LinkedArticulation first) {
				current = Optional.of(first);
				marking = current.get().getMarking();
			}

			@Override
			public boolean hasNext() {
				return current.isPresent() && current.get().getFollowingNote().isPresent();
			}

			@Override
			public Note next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				Note nextNote = current.get().getFollowingNote().get();
				current = nextNote.getLinkedArticulation(marking);

				return nextNote;
			}
		}
	}
}
