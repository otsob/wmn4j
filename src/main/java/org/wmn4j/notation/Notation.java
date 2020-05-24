/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a connected notation that spans across multiple notes, such as
 * a slur, glissando, or line. The connected notations are represented using connections
 * between adjacent notes. For example, if three notes are affected by the same
 * slur, the first will be connected to the second note and the second note will
 * be connected to the third note. The connections should follow the order in which the
 * notes are played as well as possible, e.g., downward arpeggiation should have the
 * highest note in the chord as the first note in the connected notation.
 * <p>
 * This class is immutable.
 */
public final class Notation {

	/**
	 * The type of the connected notation.
	 */
	public enum Type {
		/**
		 * Specifies a slur.
		 */
		SLUR,

		/**
		 * Specifies a glissando notation.
		 */
		GLISSANDO,

		/**
		 * Specifies an arpeggiation notation without specifying direction which is customarily
		 * arpeggiated from lowest note upwards.
		 */
		ARPEGGIATE,

		/**
		 * Specifies an arpeggiation from lowest note upwards that is explicitly marked with an arrow.
		 */
		ARPEGGIATE_UP,

		/**
		 * Specifies an arpeggiation from highest note downwards that is explicitly marked with an arrow.
		 */
		ARPEGGIATE_DOWN,

		/**
		 * Specifies a bracket indicating that notes should not be arpeggiated.
		 */
		NON_ARPEGGIATE,

	}

	private final Type type;

	/**
	 * Returns a connected notation with the given type.
	 *
	 * @param type the type of the connected notation
	 * @return a connected notation with the given type
	 */
	public static Notation of(Type type) {
		return new Notation(type);
	}

	/**
	 * Returns all notes affected by this connected notation starting from the given note.
	 *
	 * @param note the starting note
	 * @return all notes affected by this connected notation starting from the given note
	 */
	public List<Note> getAffectedStartingFrom(Note note) {

		final List<Note> affectedNotes = new ArrayList<>();
		Optional<Connection> notationConnectionOpt = note.getConnection(this);
		if (notationConnectionOpt.isEmpty()) {
			return affectedNotes;
		}

		affectedNotes.add(note);
		for (Note followingNote : notationConnectionOpt.get().getFollowingNotes()) {
			affectedNotes.add(followingNote);
		}

		return affectedNotes;
	}

	private Notation(Type type) {
		this.type = type;
	}

	/**
	 * Returns the type of the connected notation.
	 *
	 * @return the type of the connected notation
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Represents a connection between notes affected by the same connected notation.
	 */
	public static final class Connection {
		private final Notation notation;
		private final Note followingNote;
		private final boolean isBeginning;
		private final boolean isEnd;

		/**
		 * Returns a connection with the given connected notation from the first note
		 * affected by the notation.
		 *
		 * @param notation      the notation to which this connection belongs
		 * @param followingNote the note to which this connects
		 * @return a notation connection with the given notation from the first note
		 * affected by the notation
		 */
		public static Connection beginningOf(Notation notation, Note followingNote) {
			return new Connection(notation, Objects.requireNonNull(followingNote), true, false);
		}

		/**
		 * Returns a notation connection with the given notation to the given note.
		 *
		 * @param notation      the notation to which this connection belongs
		 * @param followingNote the note to which this connects
		 * @return a notation connection with the given notation to the given note
		 */
		public static Connection of(Notation notation, Note followingNote) {
			return new Connection(notation, Objects.requireNonNull(followingNote), false, false);
		}

		/**
		 * Returns a notation connection with the given notation that is used to
		 * denote the last note affected by the notation.
		 *
		 * @param notation the notation to which this connection belongs
		 * @return a notation connection with the given notation that is used to
		 * denote the last note affected by the notation
		 */
		public static Connection endOf(Notation notation) {
			return new Connection(notation, null, false, true);
		}

		/**
		 * Constructor.
		 *
		 * @param notation      the notation
		 * @param followingNote the note to which this notation is connected
		 * @param isBeginning   true if this is to be attached to the first note
		 *                      affected by this notation
		 * @param isEnd         true if this is to be attached to the last note affected
		 *                      by this notation
		 */
		private Connection(Notation notation, Note followingNote, boolean isBeginning,
				boolean isEnd) {
			this.notation = Objects.requireNonNull(notation);
			this.followingNote = followingNote;
			this.isBeginning = isBeginning;
			this.isEnd = isEnd;
		}

		/**
		 * Returns the notation to which this connection belongs.
		 *
		 * @return the notation to which this connection belongs
		 */
		public Notation getNotation() {
			return notation;
		}

		/**
		 * Returns the following note, i.e., the note to which this
		 * connects. If this connection denotes the last note affected by the notation,
		 * then returns empty.
		 *
		 * @return the following note
		 */
		public Optional<Note> getFollowingNote() {
			return Optional.ofNullable(followingNote);
		}

		/**
		 * Returns all following notes that are affected by the same notation to which this notation connection belongs.
		 *
		 * @return all following notes that are affected by the same notation to which this notation connection belongs
		 */
		public Iterable<Note> getFollowingNotes() {
			return new ConnectedNotationIterable(this);
		}

		/**
		 * Returns true if this denotes the connection starting from the first note
		 * affected by the notation.
		 *
		 * @return true if this denotes the connection starting from the first note
		 * affected by the notation
		 */
		public boolean isBeginning() {
			return isBeginning;
		}

		/**
		 * Returns true if this denotes the end of the notation.
		 *
		 * @return true if this denotes the end of the notation
		 */
		public boolean isEnd() {
			return isEnd;
		}

		/**
		 * Returns the type of this notation.
		 *
		 * @return the type of this notation
		 */
		public Notation.Type getType() {
			return notation.getType();
		}

	}

	private static final class ConnectedNotationIterable implements Iterable<Note> {

		private final Connection first;

		ConnectedNotationIterable(Connection first) {
			this.first = first;
		}

		@Override
		public Iterator<Note> iterator() {
			return new ConnectedNotationIterator(first);
		}

		private static final class ConnectedNotationIterator implements Iterator<Note> {

			private Optional<Connection> current;
			private final Notation notation;

			private ConnectedNotationIterator(Connection first) {
				current = Optional.of(first);
				notation = current.get().getNotation();
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
				current = nextNote.getConnection(notation);

				return nextNote;
			}
		}
	}
}
