/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a grace note.
 * <p>
 * This class is immutable.
 */
public final class GraceNote implements Pitched, Ornamental {

	private final Note note;

	/**
	 * Returns an instance with the given parameters.
	 *
	 * @param pitch               the pitch of the grace note
	 * @param displayableDuration the displayable duration of the grace note
	 * @param articulations       a set of Articulations associated with the grace note
	 * @param notations           the notations for the grace note
	 * @param ornaments           ornaments for the grace note
	 * @return an instance with the given parameters
	 */
	public static GraceNote of(Pitch pitch, Duration displayableDuration, Set<Articulation> articulations,
			Collection<Notation.Type> notations, Collection<Ornament> ornaments) {
		return new GraceNote(pitch, displayableDuration, articulations, notations, ornaments);
	}

	/**
	 * Private constructor.
	 */
	private GraceNote(Pitch pitch, Duration duration, Set<Articulation> articulations,
			Collection<Notation.Type> notations, Collection<Ornament> ornaments) {

		// Use dummy connections in the internal note
		List<Notation.Connection> dummyConnections = notations.stream()
				.map(notationType -> Notation.Connection.endOf(Notation.of(notationType)))
				.collect(Collectors.toList());

		this.note = Note.of(pitch, duration, articulations, dummyConnections, ornaments);
	}

	/**
	 * Returns the pitch of this grace note.
	 *
	 * @return the pitch of this note
	 */
	@Override
	public Pitch getPitch() {
		return note.getPitch();
	}

	/**
	 * Returns all articulations defined for this grace note.
	 *
	 * @return the articulations defined for this grace note
	 */
	public Set<Articulation> getArticulations() {
		return note.getArticulations();
	}

	/**
	 * Returns all notations that affect this grace note.
	 *
	 * @return all notations that affect this grace note
	 */
	public Set<Notation> getNotations() {
		return note.getNotations();
	}

	/**
	 * Returns true if this grace note has articulations that only affect this grace note, such
	 * as staccato or tenuto.
	 *
	 * @return true if this grace note has articulations
	 */
	public boolean hasArticulations() {
		return note.hasArticulations();
	}

	/**
	 * Returns true if this grace note has the given articulation.
	 *
	 * @param articulation the articulation whose presence is checked
	 * @return true if this grace note has the given articulation
	 */
	public boolean hasArticulation(Articulation articulation) {
		return note.hasArticulation(articulation);
	}

	/**
	 * Returns true if this grace note is affected by a notation of the given type.
	 *
	 * @param notationType the type of notation whose presence is checked
	 * @return true if this grace note has the given notation
	 */
	public boolean hasNotation(Notation.Type notationType) {
		return note.hasNotation(notationType);
	}

	/**
	 * Returns true if this grace note is affected by any notations.
	 *
	 * @return true if this grace note is affected by any notations
	 */
	public boolean hasNotations() {
		return note.hasNotations();
	}

	/**
	 * Returns true if this grace note has ornaments, false otherwise.
	 *
	 * @return true if this grace note has ornaments, false otherwise
	 */
	public boolean hasOrnaments() {
		return note.hasOrnaments();
	}

	/**
	 * Returns true if this grace note has an ornament of the given type, false otherwise.
	 *
	 * @param type the type of the ornament whose presence is checked
	 * @return true if this grace note has an ornament of the given type, false otherwise
	 */
	public boolean hasOrnament(Ornament.Type type) {
		return note.hasOrnament(type);
	}

	/**
	 * Returns an unmodifiable view of the ornaments of this grace note.
	 *
	 * @return an unmodifiable view of the ornaments of this grace note
	 */
	public Collection<Ornament> getOrnaments() {
		return note.getOrnaments();
	}

	@Override
	public String toString() {
		return "[G: " + note.toString() + "]";
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

		if (!(o instanceof GraceNote)) {
			return false;
		}

		return this.note.equals(((GraceNote) o).note);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 79 * hash + Objects.hashCode(this.note);
		return hash;
	}

	@Override
	public Duration getDisplayableDuration() {
		return note.getDuration();
	}
}
