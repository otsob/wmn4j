/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a note. Notes have (optionally) pitch, duration,
 * articulations, and can be tied. A sequence of tied notes functions like a
 * singly linked list where a previous note keeps track of the following tied
 * note.
 * <p>
 * This class is immutable.
 */
public final class Note implements Durational, OptionallyPitched, Notation.Connectable {

	private final Pitch pitch;
	private final Pitch displayPitch;
	private final Duration duration;
	private final Set<Articulation> articulations;
	private final Collection<Notation.Connection> notationConnections;
	private final Collection<Ornament> ornaments;

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitchName  the letter part of the pitch name
	 * @param accidental the accidental of the pitch
	 * @param octave     octave number of the pitch
	 * @param duration   the duration of the note. Must not be null
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch.Base pitchName, Pitch.Accidental accidental, int octave, Duration duration) {
		return of(Pitch.of(pitchName, accidental, octave), duration, null);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch    (optional) the pitch of the note
	 * @param duration the duration of the note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Duration duration) {
		return of(pitch, duration, null);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch         (optional) the pitch of the note
	 * @param duration      the duration of the note
	 * @param articulations a set of Articulations associated with the note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Duration duration, Set<Articulation> articulations) {
		return new Note(pitch, null, duration, articulations, null, null);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch               (optional/nullable) the pitch of the note
	 * @param duration            the duration of the note
	 * @param articulations       a set of Articulations associated with the note
	 * @param notationConnections the notation connections for the note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Duration duration, Set<Articulation> articulations,
			Collection<Notation.Connection> notationConnections) {
		return new Note(pitch, null, duration, articulations, notationConnections, null);
	}

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch               (optional/nullable) the sounding pitch of the note
	 * @param displayPitch        (optional/nullable) the written displayed pitch or staff position of the note
	 * @param duration            the duration of the note
	 * @param articulations       a set of Articulations associated with the note
	 * @param notationConnections the notation connections for the note
	 * @param ornaments           ornaments for the note
	 * @return an instance with the given parameters
	 */
	public static Note of(Pitch pitch, Pitch displayPitch, Duration duration, Set<Articulation> articulations,
			Collection<Notation.Connection> notationConnections, Collection<Ornament> ornaments) {
		return new Note(pitch, displayPitch, duration, articulations, notationConnections, ornaments);
	}

	/**
	 * Private constructor.
	 */
	private Note(Pitch pitch, Pitch displayPitch, Duration duration, Set<Articulation> articulations,
			Collection<Notation.Connection> notationConnections, Collection<Ornament> ornaments) {

		this.pitch = pitch;
		this.displayPitch = Objects.requireNonNull(displayPitch == null ? pitch : displayPitch,
				"Cannot create a note without at least display pitch");

		this.duration = Objects.requireNonNull(duration);
		if (articulations != null && !articulations.isEmpty()) {
			this.articulations = Collections.unmodifiableSet(EnumSet.copyOf(articulations));
		} else {
			this.articulations = Collections.emptySet();
		}

		if (notationConnections != null && !notationConnections.isEmpty()) {
			this.notationConnections = Collections.unmodifiableList(new ArrayList<>(notationConnections));
		} else {
			this.notationConnections = Collections.emptyList();
		}

		if (ornaments != null && !ornaments.isEmpty()) {
			this.ornaments = copyOrnaments(ornaments);
		} else {
			this.ornaments = Collections.emptySet();
		}
	}

	private Collection<Ornament> copyOrnaments(Collection<Ornament> ornaments) {
		Set<Ornament> copiedOrnaments = new HashSet<>(ornaments.size());

		for (Ornament ornament : ornaments) {
			// Copying preceding grace notes requires special handling.
			if (ornament.getType().equals(Ornament.Type.GRACE_NOTES)) {
				copiedOrnaments.add(copyGraceNotesWithConnections(ornament));
			} else {
				copiedOrnaments.add(ornament);
			}
		}

		return Collections.unmodifiableSet(copiedOrnaments);
	}

	private boolean isGraceNoteCopyingNeeded(Ornament graceNotes) {
		List<Ornamental> ornamentals = graceNotes.getOrnamentalNotes();

		if (!ornamentals.isEmpty()) {
			Ornamental last = ornamentals.get(ornamentals.size() - 1);
			if (last instanceof GraceNote) {
				return !((GraceNote) last).getPrincipalNoteConnections().isEmpty();
			} else if (last instanceof GraceNoteChord) {
				return ((GraceNoteChord) last).isConnectedToPrincipalNote();
			}
		}

		return false;
	}

	private Ornament copyGraceNotesWithConnections(Ornament graceNotes) {
		if (!isGraceNoteCopyingNeeded(graceNotes)) {
			return graceNotes;
		}

		List<Ornamental> copiedOrnamentals = new ArrayList<>(graceNotes.getOrnamentalNotes());

		Notation.Connectable target = this;

		final int indexOfLast = copiedOrnamentals.size() - 1;

		for (int i = indexOfLast; i >= 0; --i) {
			Ornamental original = copiedOrnamentals.get(i);
			if (original instanceof GraceNote) {
				GraceNote originalGraceNote = (GraceNote) original;
				GraceNote linkedCopy = createGraceNoteWithCorrectConnections(
						originalGraceNote.getPrincipalNoteConnections(), target,
						indexOfLast, i, originalGraceNote);
				copiedOrnamentals.set(i, linkedCopy);
				target = linkedCopy;
			} else if (original instanceof GraceNoteChord) {
				final GraceNoteChord originalGraceNoteChord = (GraceNoteChord) original;
				List<GraceNote> copiedChordNotes = new ArrayList<>(originalGraceNoteChord.getNoteCount());

				for (int chordIndex = originalGraceNoteChord.getNoteCount() - 1; chordIndex >= 0; --chordIndex) {
					GraceNote originalGraceNote = originalGraceNoteChord.getNote(chordIndex);
					GraceNote linkedCopy = createGraceNoteWithCorrectConnections(
							originalGraceNote.getPrincipalNoteConnections(), target,
							indexOfLast, i,
							originalGraceNote);
					copiedChordNotes.add(linkedCopy);
					target = linkedCopy;
				}

				copiedOrnamentals.set(i, GraceNoteChord.of(copiedChordNotes));
			}
		}

		return Ornament.graceNotes(copiedOrnamentals);
	}

	private GraceNote createGraceNoteWithCorrectConnections(Collection<Notation.Connection> principalNoteConnections,
			Notation.Connectable target, int indexOfLast, int i, GraceNote originalGraceNote) {
		Collection<Notation.Connection> connections;

		if (i == indexOfLast) {
			List<Notation.Connection> originalAndPrincipalNoteConnections = new ArrayList<>(
					originalGraceNote.getConnections());
			originalAndPrincipalNoteConnections.addAll(principalNoteConnections);
			connections = originalAndPrincipalNoteConnections;
		} else {
			connections = originalGraceNote.getConnections();
		}
		return copyGraceNoteWithConnections(target, originalGraceNote, connections);
	}

	private GraceNote copyGraceNoteWithConnections(Notation.Connectable target, GraceNote original,
			Collection<Notation.Connection> connections) {
		return GraceNote.of(original.getPitch().orElse(null), original.getDisplayableDuration(),
				original.getArticulations(), copyConnections(target, connections),
				original.getOrnaments(), original.getType());
	}

	private Collection<Notation.Connection> copyConnections(Notation.Connectable target,
			Collection<Notation.Connection> connections) {
		Collection<Notation.Connection> newNotationConnections = new ArrayList<>();
		for (Notation.Connection connection : connections) {
			if (!connection.isEnd()) {
				Notation.Connection newConnection = connection;
				if (target instanceof Note) {
					Note targetNote = (Note) target;
					if (isConnectedToSimilarTarget(targetNote, connection, Note.class)) {
						newConnection = createConnectionToNote(connection, targetNote);
					}
				} else if (target instanceof GraceNote) {
					GraceNote targetNote = (GraceNote) target;
					if (isConnectedToSimilarTarget(targetNote, connection, GraceNote.class)) {
						newConnection = createConnectionToGraceNote(connection, targetNote);
					}
				}

				newNotationConnections.add(newConnection);
			} else {
				newNotationConnections.add(connection);
			}
		}
		return newNotationConnections;
	}

	private <T extends Notation.Connectable> boolean isConnectedToSimilarTarget(T target,
			Notation.Connection connection,
			Class<T> targetType) {

		if (targetType.isAssignableFrom(Note.class)) {
			Optional<Note> connectedNote = connection.getFollowingNote();
			if (connectedNote.isPresent()) {
				return connectedNote.get().equalsInPitchAndDuration((Note) target);
			}
		} else if (targetType.isAssignableFrom(GraceNote.class)) {
			Optional<GraceNote> connectedNote = connection.getFollowingGraceNote();
			if (connectedNote.isPresent()) {
				return connectedNote.get().getPitch().equals(((GraceNote) target).getPitch());
			}
		}

		return false;
	}

	private Notation.Connection createConnectionToNote(Notation.Connection connection, Note target) {
		if (connection.isBeginning()) {
			return Notation.Connection.beginningOf(connection.getNotation(), target);
		}

		return Notation.Connection.of(connection.getNotation(), target);
	}

	private Notation.Connection createConnectionToGraceNote(Notation.Connection connection, GraceNote target) {
		if (connection.isBeginning()) {
			return Notation.Connection.beginningOf(connection.getNotation(), target);
		}

		return Notation.Connection.of(connection.getNotation(), target);
	}

	Collection<Notation.Connection> getConnections() {
		return notationConnections;
	}

	@Override
	public boolean hasPitch() {
		return pitch != null;
	}

	@Override
	public Optional<Pitch> getPitch() {
		return Optional.ofNullable(pitch);
	}

	@Override
	public Pitch getDisplayPitch() {
		if (pitch != null) {
			return pitch;
		}

		return displayPitch;
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
	public boolean isNote() {
		return true;
	}

	/**
	 * Returns all articulations defined for this note.
	 *
	 * @return the articulations defined for this note
	 */
	public Set<Articulation> getArticulations() {
		return this.articulations;
	}

	/**
	 * Returns all notations that affect this note.
	 *
	 * @return all notations that affect this note
	 */
	public Set<Notation> getNotations() {
		return notationConnections.stream().map(connection -> connection.getNotation())
				.collect(Collectors.toUnmodifiableSet());
	}

	@Override
	public Optional<Notation.Connection> getConnection(Notation notation) {
		return notationConnections.stream()
				.filter(articulation -> articulation.getNotation().equals(notation))
				.findFirst();
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
	 * Returns true if this note is affected by a notation of the given type.
	 *
	 * @param notationType the type of notation whose presence is checked
	 * @return true if this note has the given notation
	 */
	public boolean hasNotation(Notation.Type notationType) {
		return notationConnections.stream()
				.anyMatch(notationConnection -> notationConnection.getType().equals(notationType));
	}

	@Override
	public boolean beginsNotation(Notation.Type notationType) {
		return notationConnections.stream()
				.anyMatch(notationConnection -> notationConnection.getType().equals(notationType)
						&& notationConnection.isBeginning());
	}

	@Override
	public boolean endsNotation(Notation.Type notationType) {
		return notationConnections.stream()
				.anyMatch(notationConnection -> notationConnection.getType().equals(notationType)
						&& notationConnection.isEnd());
	}

	/**
	 * Returns true if this note is affected by any notations.
	 *
	 * @return true if this note is affected by any notations
	 */
	public boolean hasNotations() {
		return !this.notationConnections.isEmpty();
	}

	/**
	 * Returns true if this note has ornaments, false otherwise.
	 *
	 * @return true if this note has ornaments, false otherwise
	 */
	public boolean hasOrnaments() {
		return !ornaments.isEmpty();
	}

	/**
	 * Returns true if this note has an ornament of the given type, false otherwise.
	 *
	 * @param type the type of the ornament whose presence is checked
	 * @return true if this note has an ornament of the given type, false otherwise
	 */
	public boolean hasOrnament(Ornament.Type type) {
		return ornaments.stream().anyMatch(ornament -> ornament.getType().equals(type));
	}

	/**
	 * Returns an unmodifiable view of the ornaments of this note.
	 *
	 * @return an unmodifiable view of the ornaments of this note
	 */
	public Collection<Ornament> getOrnaments() {
		return ornaments;
	}

	/**
	 * Returns true if this note is tied to a following note.
	 *
	 * @return true if this note is tied to a following note
	 */
	public boolean isTiedToFollowing() {
		return notationConnections.stream().anyMatch(notationConnection -> notationConnection.getType().equals(
				Notation.Type.TIE) && !notationConnection.isEnd());
	}

	/**
	 * Returns true if this note is tied to a previous note.
	 *
	 * @return true if this note is tied to a previous note
	 */
	public boolean isTiedFromPrevious() {
		return notationConnections.stream().anyMatch(notationConnection -> notationConnection.getType().equals(
				Notation.Type.TIE) && !notationConnection.isBeginning());
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
	 * <p>
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

		return Duration.sum(tiedDurations);
	}

	/**
	 * Returns the following note to which this note is tied. If this note is not
	 * tied to a following note, then returns empty.
	 *
	 * @return the following note to which this note is tied
	 */
	public Optional<Note> getFollowingTiedNote() {
		Optional<Notation.Connection> tieConnection = notationConnections.stream()
				.filter(connection -> connection.getType().equals(
						Notation.Type.TIE) && connection.isBeginning()).findFirst();

		if (tieConnection.isEmpty()) {
			return Optional.empty();
		}

		return tieConnection.get().getFollowingNote();
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		final String tieString = "->";

		if (this.isTiedFromPrevious()) {
			strBuilder.append(tieString);
		}

		final String endDelimiter = ")";
		final String startDelimiter = "(";
		if (pitch != null) {
			strBuilder.append(this.pitch);
		} else {
			strBuilder.append("Unpitched").append(startDelimiter).append(displayPitch).append(endDelimiter);
		}
		strBuilder.append(this.duration.toString());

		if (!this.articulations.isEmpty()) {

			strBuilder.append(startDelimiter);
			for (Articulation articulation : this.articulations) {
				strBuilder.append(articulation.toString()).append(" ");
			}

			strBuilder.replace(strBuilder.length() - 1, strBuilder.length(), "");
			strBuilder.append(endDelimiter);
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
		return Objects.equals(pitch, other.pitch) && this.duration.equals(other.duration);
	}

	/**
	 * Returns true if this note is equal to the given object. Notes are equal if
	 * they have the same pitch, duration, and set of articulations.
	 *
	 * @param o the Object with which this Note is compared for equality
	 * @return true if Object o is of class Note and has the same Pitch, Duration,
	 * and Articulations as this Node. false otherwise
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

		if (!this.displayPitch.equals(other.displayPitch)) {
			return false;
		}

		if (!this.equalsInPitchAndDuration(other)) {
			return false;
		}

		if (!this.articulations.equals(other.articulations)) {
			return false;
		}

		if (!getNotationConnectionTypes().equals(other.getNotationConnectionTypes())) {
			return false;
		}

		return equalsInOrnaments(other);
	}

	private boolean equalsInOrnaments(Note other) {
		return ornaments.equals(other.ornaments);
	}

	private Set<Notation.Type> getNotationConnectionTypes() {
		return notationConnections.stream()
				.map(Notation.Connection::getType).collect(Collectors.toSet());
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.pitch);
		hash = 79 * hash + Objects.hashCode(this.displayPitch);
		hash = 79 * hash + Objects.hashCode(this.duration);
		hash = 79 * hash + Objects.hashCode(this.articulations);
		hash = 79 * hash + Objects.hashCode(getNotationConnectionTypes());
		return hash;
	}

}
