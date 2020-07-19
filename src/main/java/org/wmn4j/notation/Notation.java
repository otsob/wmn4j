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
 * Represents a connected notation that spans across multiple notes, such as ties,
 * slurs, or arpeggiation. The connected notations are represented using connections
 * between adjacent notes. For example, if three notes are affected by the same
 * slur, the first will be connected to the second note and the second note will
 * be connected to the third note. The connections should follow the left to right
 * order in the score (i.e. connections go from left to right). For chords the connections
 * should got from lowest note to highest (e.g., beginning of arpeggiation is the lowest
 * note of the chord).
 * <p>
 * This class is immutable.
 */
public final class Notation {

	/**
	 * Represents notation elements that can be connected to each other
	 * through notations, like notes and grace notes.
	 */
	public interface Connectable {
		/**
		 * Returns the notation connection belonging to the given notation. If no notation connection for the notation
		 * is
		 * present, return empty.
		 *
		 * @param notation the notation for which the notation connection is returned
		 * @return the notation connection belonging to the given notation
		 */
		Optional<Notation.Connection> getConnection(Notation notation);

		/**
		 * Returns true if this note begins a notation of the given type.
		 *
		 * @param notationType the type of the notation for whose beginning this
		 *                     note is checked
		 * @return true if this note begins a notation of the given type
		 */
		boolean beginsNotation(Notation.Type notationType);

		/**
		 * Returns true if this note ends a notation of the given type.
		 *
		 * @param notationType the type of the notation for whose end this note
		 *                     is checked
		 * @return true if this note ends a notation of the given type
		 */
		boolean endsNotation(Notation.Type notationType);
	}

	/**
	 * The type of the connected notation.
	 */
	public enum Type {

		/**
		 * Specifies a tie between notes.
		 */
		TIE,

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

	/**
	 * The style used in the connected notation.
	 */
	public enum Style {

		/**
		 * Specifies a solid line or curve.
		 */
		SOLID,

		/**
		 * Specifies a wavy line or curve.
		 */
		WAVY,

		/**
		 * Specifies a dashed line or curve.
		 */
		DASHED,

		/**
		 * Specifies a dotted line or curve.
		 */
		DOTTED,
	}

	private final Type type;
	private final Style style;

	/**
	 * Returns a connected notation with the given type.
	 *
	 * @param type the type of the connected notation
	 * @return a connected notation with the given type
	 */
	public static Notation of(Type type) {
		return new Notation(type, Style.SOLID);
	}

	/**
	 * Returns a connected notation with the given type and line type.
	 *
	 * @param type  the type of the connected notation
	 * @param style the type of line to use in
	 * @return a connected notation with the given type
	 */
	public static Notation of(Type type, Style style) {
		return new Notation(type, style);
	}

	/**
	 * Returns all notes affected by this connected notation starting from the given connectable notation element.
	 *
	 * @param connectable the starting connectable
	 * @return all notes affected by this connected notation starting from the given connectable
	 */
	public List<Note> getNotesStartingFrom(Notation.Connectable connectable) {
		return getStartingFrom(connectable, Note.class);
	}

	/**
	 * Returns all grace notes affected by this connected notation starting from the given connectable notation element.
	 *
	 * @param connectable the starting connectable
	 * @return all grace notes affected by this connected notation starting from the given connectable
	 */
	public List<GraceNote> getGraceNotesStartingFrom(Notation.Connectable connectable) {
		return getStartingFrom(connectable, GraceNote.class);
	}

	<T extends Connectable> List<T> getStartingFrom(Notation.Connectable connectable, Class<T> type) {

		final List<T> affectedNotes = new ArrayList<>();
		Optional<Connection> notationConnectionOpt = connectable.getConnection(this);
		if (notationConnectionOpt.isEmpty()) {
			return affectedNotes;
		}

		if (type.isAssignableFrom(connectable.getClass())) {
			affectedNotes.add(type.cast(connectable));
		}

		for (T followingNote : notationConnectionOpt.get().getFollowingIterable(type)) {
			affectedNotes.add(followingNote);
		}

		return affectedNotes;
	}

	private Notation(Type type, Style style) {
		this.type = type;
		this.style = style;
	}

	/**
	 * Returns the type of this connected notation.
	 *
	 * @return the type of this connected notation
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns the style of this connected notation.
	 *
	 * @return the style of this connected notation
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * Represents a connection between notes affected by the same connected notation.
	 */
	public static final class Connection {
		private final Notation notation;
		private final Connectable followingNote;
		private final boolean isBeginning;
		private final boolean isEnd;

		/**
		 * Returns a connection with the given connected notation from the first note
		 * affected by the notation.
		 *
		 * @param notation      the notation to which this connection belongs
		 * @param followingNote the grace note to which this connects
		 * @return a notation connection with the given notation from the first note
		 * affected by the notation
		 */
		public static Connection beginningOf(Notation notation, GraceNote followingNote) {
			return new Connection(notation, Objects.requireNonNull(followingNote), true, false);
		}

		/**
		 * Returns a notation connection with the given notation to the given note.
		 *
		 * @param notation      the notation to which this connection belongs
		 * @param followingNote the grace note to which this connects
		 * @return a notation connection with the given notation to the given note
		 */
		public static Connection of(Notation notation, GraceNote followingNote) {
			return new Connection(notation, Objects.requireNonNull(followingNote), false, false);
		}

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
		private Connection(Notation notation, Notation.Connectable followingNote, boolean isBeginning,
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
		 * connects. If this connection does not connect to a note or
		 * denotes the last note affected by the notation,
		 * then returns empty.
		 *
		 * @return the following note
		 */
		public Optional<Note> getFollowingNote() {
			return getFollowing(Note.class);
		}

		/**
		 * Returns the following grace note, i.e., the grace note to which this
		 * connects. If this connection does not connect to a grace note or
		 * denotes the last note affected by the notation,
		 * then returns empty.
		 *
		 * @return the following grace note
		 */
		public Optional<GraceNote> getFollowingGraceNote() {
			return getFollowing(GraceNote.class);
		}

		private <T extends Connectable> Optional<T> getFollowing(Class<T> type) {
			if (followingNote == null) {
				return Optional.empty();
			}

			if (type.isAssignableFrom(followingNote.getClass())) {
				return Optional.of(type.cast(followingNote));
			}

			return Optional.empty();
		}

		/**
		 * Returns all following notes that are affected by the same notation to which this notation connection belongs.
		 *
		 * @return all following notes that are affected by the same notation to which this notation connection belongs
		 */
		public Iterable<Note> getFollowingNotes() {
			return getFollowingIterable(Note.class);
		}

		/**
		 * Returns all following grace notes that are affected by the same notation to which this notation connection
		 * belongs.
		 *
		 * @return all following grace notes that are affected by the same
		 * notation to which this notation connection belongs
		 */
		public Iterable<GraceNote> getFollowingGraceNotes() {
			return getFollowingIterable(GraceNote.class);
		}

		private <T extends Connectable> Iterable<T> getFollowingIterable(Class<T> type) {
			return new ConnectedNotationIterable<>(this, type);
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

	private static final class ConnectedNotationIterable<T extends Connectable> implements Iterable<T> {

		private final Connection first;
		private final Class<T> type;

		ConnectedNotationIterable(Connection first, Class<T> type) {
			this.first = first;
			this.type = type;
		}

		@Override
		public Iterator<T> iterator() {
			return new ConnectedNotationIterator<>(first, type);
		}

		private static final class ConnectedNotationIterator<T extends Connectable> implements Iterator<T> {

			private Optional<Connection> current;
			private final Notation notation;
			private final Class<T> type;

			private ConnectedNotationIterator(Connection first, Class<T> type) {
				current = Optional.of(first);
				notation = current.get().getNotation();
				this.type = type;
			}

			@Override
			public boolean hasNext() {
				return current.isPresent() && current.get().getFollowing(type).isPresent();
			}

			@Override
			public T next() {
				if (!hasNext()) {
					throw new NoSuchElementException();
				}

				T nextNote = current.get().getFollowing(type).get();
				current = nextNote.getConnection(notation);

				return nextNote;
			}
		}
	}
}
