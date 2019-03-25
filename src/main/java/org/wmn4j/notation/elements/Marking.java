/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents an articulation marking that spans across multiple notes, such as
 * a slur or a glissando. The articulations are represented using connections
 * between adjacent notes. For example, if three notes are affected by the same
 * slur, the first will be connected to the second note and the second note will
 * be connected to the third note. This class is immutable.
 */
public final class Marking {

	/**
	 * The type of the marking.
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

	/**
	 * Returns all notes affected by this marking starting from the given note.
	 *
	 * @param note the starting note
	 * @return all notes affected by this marking starting from the given note
	 */
	public List<Note> getAffectedStartingFrom(Note note) {

		final List<Note> affectedNotes = new ArrayList<>();
		Optional<Connection> markingConnectionOpt = note.getMarkingConnection(this);
		if (markingConnectionOpt.isEmpty()) {
			return affectedNotes;
		}

		affectedNotes.add(note);
		for (Note followingNote : markingConnectionOpt.get().getFollowingNotes()) {
			affectedNotes.add(followingNote);
		}

		return affectedNotes;
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

	/**
	 * Represents a connection between notes affected by the same marking.
	 */
	public static final class Connection {
		private final Marking marking;
		private final Note followingNote;
		private final boolean isBeginning;
		private final boolean isEnd;

		/**
		 * Returns a connection with the given marking from the first note
		 * affected by the marking.
		 *
		 * @param marking       the marking to which this connection belongs
		 * @param followingNote the note to which this connects
		 * @return a marking connection with the given marking from the first note
		 * affected by the marking
		 */
		public static Connection beginningOf(Marking marking, Note followingNote) {
			return new Connection(marking, Objects.requireNonNull(followingNote), true, false);
		}

		/**
		 * Returns a marking connection with the given marking to the given note.
		 *
		 * @param marking       the marking to which this connection belongs
		 * @param followingNote the note to which this connects
		 * @return a marking connection with the given marking to the given note
		 */
		public static Connection of(Marking marking, Note followingNote) {
			return new Connection(marking, Objects.requireNonNull(followingNote), false, false);
		}

		/**
		 * Returns a marking connection with the given marking that is used to
		 * denote the last note affected by the marking.
		 *
		 * @param marking the marking to which this connection belongs
		 * @return a marking connection with the given marking that is used to
		 * denote the last note affected by the marking
		 */
		public static Connection endOf(Marking marking) {
			return new Connection(marking, null, false, true);
		}

		/**
		 * Constructor.
		 *
		 * @param marking       the marking
		 * @param followingNote the note to which this marking is connected
		 * @param isBeginning   true if this is to be attached to the first note
		 *                      affected by this marking
		 * @param isEnd         true if this is to be attached to the last note affected
		 *                      by this marking
		 */
		private Connection(Marking marking, Note followingNote, boolean isBeginning,
				boolean isEnd) {
			this.marking = Objects.requireNonNull(marking);
			this.followingNote = followingNote;
			this.isBeginning = isBeginning;
			this.isEnd = isEnd;
		}

		/**
		 * Returns the marking to which this connection belongs.
		 *
		 * @return the marking to which this connection belongs
		 */
		public Marking getMarking() {
			return marking;
		}

		/**
		 * Returns the following note, i.e., the note to which this
		 * connects. If this connection denotes the last note affected by the marking,
		 * then returns empty.
		 *
		 * @return the following note
		 */
		public Optional<Note> getFollowingNote() {
			return Optional.ofNullable(followingNote);
		}

		/**
		 * Returns all following notes that are affected by the same marking to which this marking connection belongs.
		 *
		 * @return all following notes that are affected by the same marking to which this marking connection belongs
		 */
		public Iterable<Note> getFollowingNotes() {
			return new MarkingConnectionIterable(this);
		}

		/**
		 * Returns true if this denotes the connection starting from the first note
		 * affected by the marking.
		 *
		 * @return true if this denotes the connection starting from the first note
		 * affected by the marking
		 */
		public boolean isBeginning() {
			return isBeginning;
		}

		/**
		 * Returns true if this denotes the end of the marking.
		 *
		 * @return true if this denotes the end of the marking
		 */
		public boolean isEnd() {
			return isEnd;
		}

		/**
		 * Returns the type of this marking.
		 *
		 * @return the type of this marking
		 */
		public Marking.Type getType() {
			return marking.getType();
		}

	}

	private static final class MarkingConnectionIterable implements Iterable<Note> {

		private final Connection first;

		MarkingConnectionIterable(Connection first) {
			this.first = first;
		}

		@Override
		public Iterator<Note> iterator() {
			return new MarkingConnectionIterator(first);
		}

		private static final class MarkingConnectionIterator implements Iterator<Note> {

			private Optional<Connection> current;
			private final Marking marking;

			private MarkingConnectionIterator(Connection first) {
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
				current = nextNote.getMarkingConnection(marking);

				return nextNote;
			}
		}
	}
}
