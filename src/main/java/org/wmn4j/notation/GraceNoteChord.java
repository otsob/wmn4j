/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a chord consisting of grace notes.
 * <p>
 * This class is immutable.
 */
public final class GraceNoteChord implements Ornamental, Iterable<GraceNote> {
	private final GenericChord<GraceNote> internalChord;

	/**
	 * Returns an instance with the given {@link GraceNote}s.
	 *
	 * @param notes A non-empty and non-null Collection of {@link GraceNote} objects
	 * @return a chord with the given notes
	 * @throws NullPointerException     if notes is null.
	 * @throws IllegalArgumentException if notes is empty or all GraceNote objects in
	 *                                  notes are do not have same displayable duration and grace note type.
	 */
	public static GraceNoteChord of(Collection<GraceNote> notes) {
		return new GraceNoteChord(notes);
	}

	private GraceNoteChord(Collection<GraceNote> internalChord) {
		this(new GenericChord<>(internalChord));
	}

	private GraceNoteChord(GenericChord<GraceNote> internalChord) {
		this.internalChord = internalChord;

		final Duration d = this.internalChord.getNote(0).getDisplayableDuration();
		final Ornamental.Type type = this.internalChord.getNote(0).getType();

		for (GraceNote n : this.internalChord) {
			if (!d.equals(n.getDisplayableDuration())) {
				throw new IllegalArgumentException("All grace notes in chord must be of same displayable duration");
			}

			if (!type.equals(n.getType())) {
				throw new IllegalArgumentException("All grace notes in chord must be of same type");
			}
		}
	}

	@Override
	public Duration getDisplayableDuration() {
		return this.internalChord.getNote(0).getDisplayableDuration();
	}

	@Override
	public Type getType() {
		return this.internalChord.getLowestNote().getType();
	}

	/**
	 * Returns the {@link GraceNote} at the given index counting from lowest pitch in
	 * this {@link GraceNoteChord}.
	 *
	 * @param fromLowest index of note, 0 being the lowest note in the chord
	 * @return the note from index fromLowest
	 * @throws IllegalArgumentException if fromLowest is smaller than 0 or at least
	 *                                  the number of notes in this chord
	 */
	public GraceNote getNote(int fromLowest) {
		return this.internalChord.getNote(fromLowest);
	}

	/**
	 * Returns the lowest grace note in this chord.
	 *
	 * @return the grace note with the lowest pitch in this chord.
	 */
	public GraceNote getLowestNote() {
		return internalChord.getLowestNote();
	}

	/**
	 * Returns the highest grace note in this chord.
	 *
	 * @return the grace note with the highest pitch in this chord.
	 */
	public GraceNote getHighestNote() {
		return internalChord.getHighestNote();
	}

	/**
	 * Returns the number of grace notes in this chord.
	 *
	 * @return number of grace notes in this chord
	 */
	public int getNoteCount() {
		return internalChord.getNoteCount();
	}

	/**
	 * Returns a chord with the grace notes of this chord and the given grace note.
	 *
	 * @param note the grace note that is added
	 * @return a chord with the grace notes of this and the added grace note
	 */
	public GraceNoteChord add(GraceNote note) {
		return new GraceNoteChord(internalChord.add(note));
	}

	/**
	 * Returns a chord with the given grace note removed.
	 *
	 * @param note grace note to be removed
	 * @return a chord without the given grace note
	 */
	public GraceNoteChord remove(GraceNote note) {
		return new GraceNoteChord(internalChord.remove(note));
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
	 * Returns true if this contains the given grace note.
	 *
	 * @param note the grace note whose presence in this chord is checked
	 * @return true if this contains the given grace note, false otherwise
	 */
	public boolean contains(GraceNote note) {
		return this.internalChord.contains(note);
	}

	/**
	 * Returns a chord with the given pitch removed.
	 *
	 * @param pitch pitch of the note to be removed
	 * @return a chord without a grace note with the given pitch
	 */
	public GraceNoteChord remove(Pitch pitch) {
		return new GraceNoteChord(internalChord.remove(pitch));
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

	boolean isConnectedToPrincipalNote() {
		return internalChord.stream().anyMatch(graceNote -> !graceNote.getPrincipalNoteConnections().isEmpty());
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
		if (!(o instanceof GraceNoteChord)) {
			return false;
		}

		final GraceNoteChord other = (GraceNoteChord) o;
		return this.internalChord.equals(other.internalChord);
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
	public Iterator<GraceNote> iterator() {
		return this.internalChord.iterator();
	}
}
