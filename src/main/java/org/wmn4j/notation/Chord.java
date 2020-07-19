/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a chord. This class should be used for chords where the notes are
 * all of same length. For polyphonic textures add voices to the
 * {@link Measure}.
 * <p>
 * This class is immutable.
 */
public final class Chord implements Durational, Iterable<Note> {
	private final GenericChord<Note> internalChord;

	/**
	 * Returns an instance of with the given {@link Note} objects.
	 *
	 * @param n A non-empty and non-null array of {@link Note} objects
	 * @return a chord with the given notes
	 * @throws NullPointerException     if notes is null
	 * @throws IllegalArgumentException if notes is empty or all Note objects in
	 *                                  notes are not of same duration
	 */
	public static Chord of(Note... n) {
		return new Chord(Arrays.asList(n));
	}

	/**
	 * Returns an instance with the given {@link Note}s.
	 *
	 * @param notes A non-empty and non-null Collection of {@link Note} objects
	 * @return a chord with the given notes
	 * @throws NullPointerException     if notes is null.
	 * @throws IllegalArgumentException if notes is empty or all Note objects in
	 *                                  notes are not of same duration.
	 */
	public static Chord of(Collection<Note> notes) {
		return new Chord(notes);
	}

	private Chord(Collection<Note> internalChord) {
		this(new GenericChord<>(internalChord));
	}

	private Chord(GenericChord<Note> internalChord) {
		this.internalChord = internalChord;

		final Duration d = this.internalChord.getNote(0).getDuration();

		for (Note n : this.internalChord) {
			if (!d.equals(n.getDuration())) {
				throw new IllegalArgumentException("All notes in chord must be of same duration");
			}
		}
	}

	@Override
	public Duration getDuration() {
		return this.internalChord.getNote(0).getDuration();
	}

	/**
	 * Returns the {@link Note} at the given index counting from lowest pitch in
	 * this {@link Chord}.
	 *
	 * @param fromLowest index of note, 0 being the lowest note in the chord
	 * @return the note from index fromLowest
	 * @throws IllegalArgumentException if fromLowest is smaller than 0 or at least
	 *                                  the number of notes in this Chord
	 */
	public Note getNote(int fromLowest) {
		return this.internalChord.getNote(fromLowest);
	}

	/**
	 * Returns the lowest note in this chord.
	 *
	 * @return the note with the lowest pitch in this Chord.
	 */
	public Note getLowestNote() {
		return internalChord.getLowestNote();
	}

	/**
	 * Returns the highest note in this chord.
	 *
	 * @return the note with the highest pitch in this Chord.
	 */
	public Note getHighestNote() {
		return internalChord.getHighestNote();
	}

	/**
	 * Returns the number of notes in this chord.
	 *
	 * @return number of notes in this chord.
	 */
	public int getNoteCount() {
		return internalChord.getNoteCount();
	}

	/**
	 * Returns a chord with the notes of this chord and the given note.
	 *
	 * @param note the note that is added
	 * @return a chord with the notes of this and the added note
	 */
	public Chord add(Note note) {
		return new Chord(internalChord.add(note));
	}

	/**
	 * Returns a Chord with the given note removed.
	 *
	 * @param note note to be removed
	 * @return a chord without the given note
	 */
	public Chord remove(Note note) {
		return new Chord(internalChord.remove(note));
	}

	/**
	 * Returns true if this chord contains the given pitch.
	 *
	 * @param pitch pitch whose presence in this chord is checked
	 * @return true if this contains the given pitch, false otherwise
	 */
	public boolean contains(Pitch pitch) {
		return internalChord.contains(pitch);
	}

	/**
	 * Returns true if this contains the given note.
	 *
	 * @param note the note whose presence in this chord is checked
	 * @return true if this contains the given note, false otherwise
	 */
	public boolean contains(Note note) {
		return this.internalChord.contains(note);
	}

	/**
	 * Returns a chord with the given pitch removed.
	 *
	 * @param pitch pitch of the note to be removed
	 * @return a chord without a note with the given pitch
	 */
	public Chord remove(Pitch pitch) {
		return new Chord(internalChord.remove(pitch));
	}

	/**
	 * Returns true if this chord has articulations.
	 *
	 * @return true if this chord has articulations
	 */
	public boolean hasArticulations() {
		return this.internalChord.stream().anyMatch(note -> note.hasArticulations());
	}

	/**
	 * Returns true if this chord has the given articulation.
	 *
	 * @param articulation the articulation whose presence is checked
	 * @return true if this chord has the given articulation
	 */
	public boolean hasArticulation(Articulation articulation) {
		return this.internalChord.stream().anyMatch(note -> note.hasArticulation(articulation));
	}

	/**
	 * Returns an unmodifiable view of all articulations defined for this chord.
	 *
	 * @return the articulations defined for this chord
	 */
	public Collection<Articulation> getArticulations() {
		return this.internalChord.stream().flatMap(note -> note.getArticulations().stream())
				.collect(Collectors.toUnmodifiableSet());
	}

	/**
	 * Returns true if the given Object is equal to this.
	 *
	 * @param o Object against which this is compared for equality.
	 * @return true if o is an instance of Chord and contains all and no other notes
	 * than the ones in this Chord.
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

		if (!this.internalChord.equals(other.internalChord)) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.internalChord);
		return hash;
	}

	@Override
	public String toString() {
		return internalChord.toString();
	}

	@Override
	public boolean isRest() {
		return false;
	}

	@Override
	public Iterator<Note> iterator() {
		return this.internalChord.iterator();
	}
}
