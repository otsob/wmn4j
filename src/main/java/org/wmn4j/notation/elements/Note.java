/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

/**
 * Represents a note. This class is immutable. Notes have pitch, duration,
 * articulations, and can be tied. A sequence of tied notes functions like a
 * singly linked list where a previous note keeps track of the following tied
 * note.
 */
public final class Note implements Durational, Pitched {

	private final Pitch pitch;
	private final Duration duration;
	private final Set<Articulation> articulations;
	private final List<LinkedArticulation> multiNoteArticulations;

	private final Note tiedTo;
	private final boolean isTiedFrom;

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitchName the letter part of the pitch name
	 * @param alter     how many half-steps the pitch is altered up (positive) or
	 *                  down (negative)
	 * @param octave    octave number of the pitch
	 * @param duration  the duration of the note. Must not be null
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch.Base pitchName, int alter, int octave, Duration duration) {
		return of(Pitch.of(pitchName, alter, octave), duration, null);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch    the pitch of the note
	 * @param duration the duration of the note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Duration duration) {
		return of(pitch, duration, null);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch         the pitch of the note
	 * @param duration      the duration of the note
	 * @param articulations a set of Articulations associated with the note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Duration duration, Set<Articulation> articulations) {
		return new Note(pitch, duration, articulations, null, null, false);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch                  the pitch of the note
	 * @param duration               the duration of the note
	 * @param articulations          a set of Articulations associated with the note
	 * @param multiNoteArticulations list of the MultiNoteArticulations for the note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Duration duration, Set<Articulation> articulations,
			List<LinkedArticulation> multiNoteArticulations) {
		return new Note(pitch, duration, articulations, multiNoteArticulations, null, false);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch                  the pitch of the note
	 * @param duration               the duration of the note
	 * @param articulations          a set of Articulations associated with the note
	 * @param multiNoteArticulations list of the MultiNoteArticulations for the note
	 * @param tiedTo                 the following note to which this is tied
	 * @param isTiedFromPrevious     true if this is tied from the previous note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Duration duration, Set<Articulation> articulations,
			List<LinkedArticulation> multiNoteArticulations, Note tiedTo, boolean isTiedFromPrevious) {
		return new Note(pitch, duration, articulations, multiNoteArticulations, tiedTo, isTiedFromPrevious);
	}

	/**
	 * Private constructor.
	 */
	private Note(Pitch pitch, Duration duration, Set<Articulation> articulations,
			List<LinkedArticulation> multiNoteArticulations, Note tiedTo, boolean isTiedFromPrevious) {

		this.pitch = Objects.requireNonNull(pitch);
		this.duration = Objects.requireNonNull(duration);
		if (articulations != null && !articulations.isEmpty()) {
			this.articulations = Collections.unmodifiableSet(EnumSet.copyOf(articulations));
		} else {
			this.articulations = Collections.emptySet();
		}

		if (multiNoteArticulations != null && !multiNoteArticulations.isEmpty()) {
			this.multiNoteArticulations = Collections.unmodifiableList(new ArrayList<>(multiNoteArticulations));
		} else {
			this.multiNoteArticulations = Collections.emptyList();
		}

		this.tiedTo = tiedTo;
		this.isTiedFrom = isTiedFromPrevious;
	}

	/**
	 * Returns the pitch of this note.
	 *
	 * @return the pitch of this note
	 */
	@Override
	public Pitch getPitch() {
		return this.pitch;
	}

	/**
	 * Returns the duration of this note.
	 *
	 * @return the duration of this note
	 */
	@Override
	public Duration getDuration() {
		return this.duration;
	}

	@Override
	public boolean isRest() {
		return false;
	}

	/**
	 * Returns the articulations defined for this note.
	 *
	 * @return the articulations defined for this note
	 */
	public Set<Articulation> getArticulations() {
		return this.articulations;
	}

	/**
	 * Returns true if this note has articulations that only affect this note, such
	 * as staccato or tenuto.
	 *
	 * @return true if this note has articulations
	 */
	public boolean hasArticulations() {
		return !this.articulations.isEmpty();
	}

	/**
	 * Returns true if this note has the given articulation.
	 *
	 * @param articulation the articulation whose presence is checked
	 * @return true if this note has the given articulation
	 */
	public boolean hasArticulation(Articulation articulation) {
		return this.articulations.contains(articulation);
	}

	/**
	 * Returns the multinote articulations defined for this note. These are
	 * articulations such as slurs or glissando.
	 *
	 * @return the multinote articulations defined for this note
	 */
	public List<LinkedArticulation> getMultiNoteArticulations() {
		return this.multiNoteArticulations;
	}

	/**
	 * Returns true if this note is affected by any multinote articulations.
	 *
	 * @return true if this note is affected by any multinote articulations
	 */
	public boolean hasMultiNoteArticulations() {
		return !this.multiNoteArticulations.isEmpty();
	}

	/**
	 * Returns true if this note is tied to a following note.
	 *
	 * @return true if this note is tied to a following note
	 */
	public boolean isTiedToFollowing() {
		return this.tiedTo != null;
	}

	/**
	 * Returns true if this note is tied to a previous note.
	 *
	 * @return true if this note is tied to a previous note
	 */
	public boolean isTiedFromPrevious() {
		return this.isTiedFrom;
	}

	/**
	 * Returns true if this note is tied to a previous or to a following note.
	 *
	 * @return true if this note is tied to a previous or to a following note
	 */
	public boolean isTied() {
		return this.isTiedFromPrevious() || this.isTiedToFollowing();
	}

	/**
	 * Returns the total duration of tied notes starting from the onset of this
	 * note.
	 *
	 * For example, if three quarter notes are tied together, then the tied duration
	 * of the first note is a dotted half note. For a note that is not tied to a
	 * following note this is equal to the note's duration.
	 *
	 * @return the total duration of tied notes starting from the onset of this note
	 */
	public Duration getTiedDuration() {
		final List<Duration> tiedDurations = new ArrayList<>();

		Optional<Note> currentNote = Optional.of(this);
		while (currentNote.isPresent()) {
			tiedDurations.add(currentNote.get().getDuration());
			currentNote = currentNote.get().getFollowingTiedNote();
		}

		return Duration.sumOf(tiedDurations);
	}

	/**
	 * Returns the following note to which this note is tied. If this note is not
	 * tied to a following note, then returns empty.
	 *
	 * @return the following note to which this note is tied
	 */
	public Optional<Note> getFollowingTiedNote() {
		return Optional.ofNullable(this.tiedTo);
	}

	/**
	 * Returns an integer that specifies if this note is higher than, lower than, or
	 * equal in pitch to the given note.
	 *
	 * @param other the note to which this is compared for pitch
	 * @return negative integer if this note is lower than other, positive integer
	 *         if this is higher than other, 0 if notes are (enharmonically) of same
	 *         height.
	 */
	public int compareByPitch(Note other) {
		return this.pitch.compareTo(other.getPitch());
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		final String tieString = "->";

		if (this.isTiedFromPrevious()) {
			strBuilder.append(tieString);
		}

		strBuilder.append(this.pitch.toString()).append(this.duration.toString());

		if (!this.articulations.isEmpty()) {

			strBuilder.append("(");
			for (Articulation articulation : this.articulations) {
				strBuilder.append(articulation.toString()).append(" ");
			}

			strBuilder.replace(strBuilder.length() - 1, strBuilder.length(), "");
			strBuilder.append(")");
		}

		if (this.isTiedToFollowing()) {
			strBuilder.append(tieString);
		}

		return strBuilder.toString();
	}

	/**
	 * Returns true if this note is equal to the given note in pitch and duration.
	 *
	 * @param other note with which this is compared for pitch and duration equality
	 * @return true if this note is equal to the given note in pitch and duration
	 */
	public boolean equalsInPitchAndDuration(Note other) {
		return this.pitch.equals(other.pitch) && this.duration.equals(other.duration);
	}

	/**
	 * Returns true if this note is equal to the given object. Notes are equal if
	 * they have the same pitch, duration, and set of articulations.
	 *
	 * @param o the Object with which this Note is compared for equality
	 * @return true if Object o is of class Note and has the same Pitch, Duration,
	 *         and Articulations as this Node. false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Note)) {
			return false;
		}

		final Note other = (Note) o;

		if (!this.equalsInPitchAndDuration(other)) {
			return false;
		}

		if (!this.articulations.equals(other.articulations)) {
			return false;
		}

		// TODO: Effect of ties and MultiNoteArticulations.

		return true;
	}

	@Override
	public int hashCode() {
		// TODO: if equals is changed update this.
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.pitch);
		hash = 79 * hash + Objects.hashCode(this.duration);
		hash = 79 * hash + Objects.hashCode(this.articulations);
		return hash;
	}

}
