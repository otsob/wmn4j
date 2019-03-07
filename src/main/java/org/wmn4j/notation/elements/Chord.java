/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Represents a chord. This class should be used for chords where the notes are
 * all of same length. For polyphonic textures add voices to the
 * {@link Measure}. This class is immutable.
 */
public final class Chord implements Durational, Iterable<Note> {
	private final List<Note> notes;

	/**
	 * Returns an instance of with the given {@link Note} objects.
	 *
	 * @param n A non-empty and non-null array of {@link Note} objects
	 * @return a chord with the given notes
	 *
	 * @throws NullPointerException     if notes is null
	 * @throws IllegalArgumentException if notes is empty or all Note objects in
	 *                                  notes are not of same duration
	 */
	public static Chord getChord(Note... n) {
		return new Chord(Arrays.asList(n));
	}

	/**
	 * Returns an instance with the given {@link Note}s.
	 *
	 * @param notes A non-empty and non-null Collection of {@link Note} objects
	 * @return a chord with the given notes
	 *
	 * @throws NullPointerException     if notes is null.
	 * @throws IllegalArgumentException if notes is empty or all Note objects in
	 *                                  notes are not of same duration.
	 */
	public static Chord getChord(Collection<Note> notes) {
		return new Chord(notes);
	}

	/**
	 * Private constructor. Use static getters for getting an instance.
	 *
	 * @throws NullPointerException     if notes is null
	 * @throws IllegalArgumentException if notes is empty or all Note objects in
	 *                                  notes are not of same duration
	 * @param notes the notes that are put in this Chord
	 */
	private Chord(Collection<Note> notes) {

		final List<Note> notesCopy = new ArrayList<>(Objects.requireNonNull(notes));

		if (notesCopy.isEmpty()) {
			throw new IllegalArgumentException("Chord cannot be constructed with an empty List of notes");
		}

		notesCopy.sort(Note::compareByPitch);
		this.notes = Collections.unmodifiableList(notesCopy);

		final Duration d = this.notes.get(0).getDuration();

		for (Note n : this.notes) {
			if (!d.equals(n.getDuration())) {
				throw new IllegalArgumentException("All notes in chord must be of same duration");
			}
		}
	}

	@Override
	public Duration getDuration() {
		return this.notes.get(0).getDuration();
	}

	/**
	 * Returns the {@link Note} at the given index counting from lowest pitch in
	 * this {@link Chord}.
	 *
	 * @throws IllegalArgumentException if fromLowest is smaller than 0 or at least
	 *                                  the number of notes in this Chord
	 * @param fromLowest index of note, 0 being the lowest note in the chord
	 * @return the note from index fromLowest
	 */
	public Note getNote(int fromLowest) {
		if (fromLowest < 0 || fromLowest >= this.notes.size()) {
			throw new IllegalArgumentException(
					"Tried to get note with invalid index: " + fromLowest + "from chord: " + this);
		}

		return this.notes.get(fromLowest);
	}

	/**
	 * Returns the lowest note in this chord.
	 *
	 * @return the note with the lowest pitch in this Chord.
	 */
	public Note getLowestNote() {
		return this.getNote(0);
	}

	/**
	 * Returns the highest note in this chord.
	 *
	 * @return the note with the highest pitch in this Chord.
	 */
	public Note getHighestNote() {
		return this.getNote(this.notes.size() - 1);
	}

	/**
	 * Returns the number of notes in this chord.
	 *
	 * @return number of notes in this chord.
	 */
	public int getNoteCount() {
		return this.notes.size();
	}

	/**
	 * Returns a chord with the notes of this chord and the given note.
	 *
	 * @param note the note that is added
	 * @return a chord with the notes of this and the added note
	 */
	public Chord add(Note note) {
		final ArrayList<Note> noteList = new ArrayList<>(this.notes);
		noteList.add(note);
		return Chord.getChord(noteList);
	}

	/**
	 * Returns a Chord with the given note removed.
	 *
	 * @param note note to be removed
	 * @return a chord without the given note
	 */
	public Chord remove(Note note) {
		final ArrayList<Note> noteList = new ArrayList<>(this.notes);
		noteList.remove(note);
		return Chord.getChord(noteList);
	}

	/**
	 * Returns true if this chord contains the given pitch.
	 *
	 * @param pitch pitch whose presence in this chord is checked
	 * @return true if this contains the given pitch, false otherwise
	 */
	public boolean contains(Pitch pitch) {
		for (Note note : this.notes) {
			if (note.getPitch().equals(pitch)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if this contains the given note.
	 *
	 * @param note the note whose presence in this chord is checked
	 * @return true if this contains the given note, false otherwise
	 */
	public boolean contains(Note note) {
		return this.notes.contains(note);
	}

	/**
	 * Returns a chord with the given pitch removed.
	 *
	 * @param pitch pitch of the note to be removed
	 * @return a chord without a note with the given pitch
	 */
	public Chord remove(Pitch pitch) {
		if (this.contains(pitch)) {
			final List<Note> newNotes = new ArrayList<>(this.notes);
			newNotes.remove(Note.getNote(pitch, this.getDuration()));
			return Chord.getChord(newNotes);
		}

		return this;
	}

	/**
	 * Returns true if the given Object is equal to this.
	 *
	 * @param o Object against which this is compared for equality.
	 * @return true if o is an instance of Chord and contains all and no other notes
	 *         than the ones in this Chord.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Chord)) {
			return false;
		}

		final Chord other = (Chord) o;

		if (!this.getDuration().equals(other.getDuration())) {
			return false;
		}

		if (this.getNoteCount() != other.getNoteCount()) {
			return false;
		}

		if (!this.notes.equals(other.notes)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.notes);
		return hash;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("[");

		for (int i = 0; i < this.notes.size(); ++i) {
			strBuilder.append(this.notes.get(i).toString());

			if (i != this.notes.size() - 1) {
				strBuilder.append(",");
			}
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}

	@Override
	public boolean isRest() {
		return false;
	}

	@Override
	public Iterator<Note> iterator() {
		return this.notes.iterator();
	}
}
