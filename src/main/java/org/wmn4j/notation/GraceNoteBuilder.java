/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Class for building {@link GraceNote} objects. The built grace note is cached.
 * <p>
 * Instances of this class are not thread-safe.
 */
public final class GraceNoteBuilder implements ConnectableBuilder, OrnamentalBuilder {

	private final NoteBuilder noteBuilder;
	private NoteBuilder principalNoteBuilder;
	private Collection<Notation.Connection> principalNoteConnections;

	private GraceNote.Type graceNoteType;
	private GraceNote cachedNote;
	private boolean isBuilding;

	/**
	 * Constructor.
	 *
	 * @param pitch               the pitch set in this builder
	 * @param displayableDuration the displayable duration set in this builder
	 */
	public GraceNoteBuilder(Pitch pitch, Duration displayableDuration) {
		noteBuilder = new NoteBuilder(pitch, displayableDuration);
		graceNoteType = GraceNote.Type.GRACE_NOTE;
		isBuilding = false;
		principalNoteConnections = Collections.emptyList();
	}

	/**
	 * Constructor.
	 * <p>
	 * The note builder is copied, so this constructor does not take ownership of it.
	 *
	 * @param noteBuilder the note builder from which all attributes are copied to this grace note builder
	 */
	public GraceNoteBuilder(NoteBuilder noteBuilder) {
		this(noteBuilder, true);
	}

	/**
	 * Returns a new {@link GraceNoteBuilder} that takes ownership of the given note builder.
	 *
	 * @param noteBuilder the note builder that the returned grace note builder takes ownership of
	 * @return a new {@link GraceNoteBuilder} that takes ownership of the given note builder
	 */
	public static GraceNoteBuilder moveFrom(NoteBuilder noteBuilder) {
		return new GraceNoteBuilder(noteBuilder, false);
	}

	private GraceNoteBuilder(NoteBuilder noteBuilder, boolean deepCopy) {
		if (deepCopy) {
			this.noteBuilder = new NoteBuilder(noteBuilder);
		} else {
			this.noteBuilder = noteBuilder;
		}
		graceNoteType = GraceNote.Type.GRACE_NOTE;
		isBuilding = false;
		principalNoteConnections = Collections.emptyList();
	}

	/**
	 * Returns the pitch set in this builder.
	 *
	 * @return the pitch set in this builder
	 */
	public Pitch getPitch() {
		return noteBuilder.getPitch();
	}

	/**
	 * Sets the pitch in this builder.
	 *
	 * @param pitch the pitch to be set in this builder
	 */
	public void setPitch(Pitch pitch) {
		noteBuilder.setPitch(pitch);
	}

	/**
	 * Returns the grace note type set in this builder.
	 *
	 * @return the grace note type set in this builder
	 */
	public GraceNote.Type getGraceNoteType() {
		return graceNoteType;
	}

	/**
	 * Sets the grace note type for this builder.
	 *
	 * @param graceNoteType the grace note type to set in this builder
	 */
	public void setGraceNoteType(GraceNote.Type graceNoteType) {
		this.graceNoteType = graceNoteType;
	}

	/**
	 * Returns the displayable duration set in this builder.
	 *
	 * @return the duration currently set in this builder
	 */
	public Duration getDisplayableDuration() {
		return noteBuilder.getDuration();
	}

	void addToConnectedFrom(Notation notation) {
		noteBuilder.addToConnectedFrom(notation);
	}

	Map<Notation, NoteBuilder> getNoteConnections() {
		return noteBuilder.getNoteConnections();
	}

	Set<Notation> getConnectedFrom() {
		return noteBuilder.getConnectedFrom();
	}

	/**
	 * Sets the displayable duration in this builder.
	 *
	 * @param duration The duration to be set in this builder
	 */
	public void setDisplayableDuration(Duration duration) {
		noteBuilder.setDuration(duration);
	}

	/**
	 * Returns the articulations set in this builder.
	 *
	 * @return the articulations set in this builder
	 */
	public Set<Articulation> getArticulations() {
		return noteBuilder.getArticulations();
	}

	void setPrincipalNote(NoteBuilder noteBuilder) {
		principalNoteBuilder = noteBuilder;
	}

	void setCachedNote(GraceNote cachedNote) {
		this.cachedNote = cachedNote;
	}

	Optional<NoteBuilder> getPrincipalNote() {
		return Optional.ofNullable(principalNoteBuilder);
	}

	/**
	 * Sets the articulations in this builder.
	 *
	 * @param articulations the articulations that are set into this builder
	 */
	public void setArticulations(Set<Articulation> articulations) {
		noteBuilder.setArticulations(articulations);
	}

	/**
	 * Adds an articulation to this builder.
	 *
	 * @param articulation the articulation that is added to this builder
	 */
	public void addArticulation(Articulation articulation) {
		noteBuilder.addArticulation(articulation);
	}

	/**
	 * Adds the given ornament to this builder.
	 *
	 * @param ornament the ornament to add to this builder
	 */
	public void addOrnament(Ornament ornament) {
		noteBuilder.addOrnament(ornament);
	}

	void setPrincipalNoteConnections(Collection<Notation.Connection> connections) {
		principalNoteConnections = connections;
	}

	@Override
	public void connectWith(Notation notation, NoteBuilder targetNoteBuilder) {
		noteBuilder.connectWith(notation, targetNoteBuilder);
	}

	@Override
	public void connectWith(Notation notation, GraceNoteBuilder targetNoteBuilder) {
		noteBuilder.connectWith(notation, targetNoteBuilder);
	}

	/**
	 * Removes the cached note that was built on the previous call of {@link #build
	 * build}.
	 */
	public void clearCache() {
		this.cachedNote = null;
		this.noteBuilder.clearCache();
	}

	/**
	 * Creates a grace note with the values set in this builder. This method has side
	 * effects. The method calls {@link #build build} recursively on the following
	 * {@link NoteBuilder} and {@link GraceNoteBuilder} objects to which this is tied. Calling this method to
	 * create the first note in a sequence of tied notes builds all the tied notes.
	 * The {@link Note} objects are cached in the builders. In case of tied notes,
	 * the temporally first one should be built first.
	 *
	 * @return a note instance with the values set in this builder.
	 */
	@Override
	public GraceNote build() {

		if (this.cachedNote == null) {
			if (isBuilding) {
				throw new IllegalStateException(
						"Trying to build a grace note from a builder that is currently building. "
								+ "This can be caused by concurrent modification or by a circular dependency between "
								+ "note builders caused by slurs or notations.");
			}

			isBuilding = true;

			this.cachedNote = GraceNote
					.of(noteBuilder.getPitch(), noteBuilder.getDuration(), noteBuilder.getArticulations(),
							noteBuilder.getResolvedNotationConnections(), noteBuilder.getOrnaments(), graceNoteType,
							principalNoteConnections);

			isBuilding = false;
		}

		return this.cachedNote;
	}
}
