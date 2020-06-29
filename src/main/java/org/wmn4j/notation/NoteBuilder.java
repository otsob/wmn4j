/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for building {@link Note} objects. The built note is cached.
 * <p>
 * Instances of this class are not thread-safe.
 */
public final class NoteBuilder implements DurationalBuilder {

	private Pitch pitch;
	private Duration duration;
	private Set<Articulation> articulations;
	private Set<Ornament> ornaments;
	private Map<Notation, NoteBuilder> noteConnections = new HashMap<>();
	private Map<Notation, GraceNoteBuilder> graceNoteConnections = new HashMap<>();
	private Set<Notation> connectedFrom = new HashSet<>();

	private Note cachedNote;
	private boolean isBuilding;

	/**
	 * Constructor.
	 *
	 * @param pitch    the pitch set in this builder
	 * @param duration the duration set in this builder
	 */
	public NoteBuilder(Pitch pitch, Duration duration) {
		this.pitch = pitch;
		this.duration = duration;
		this.articulations = EnumSet.noneOf(Articulation.class);
		this.ornaments = new HashSet<>();
	}

	/**
	 * Copy constructor for NoteBuilder. Creates a new instance of NoteBuilder that
	 * is a copy of the NoteBuilder given as an attribute.
	 * This does not copy connections to other builders through notations.
	 *
	 * @param builder the NoteBuilder to be copied
	 */
	public NoteBuilder(NoteBuilder builder) {
		this(builder.getPitch(), builder.getDuration());
		builder.getArticulations()
				.forEach(articulation -> this.articulations.add(articulation));
	}

	/**
	 * Constructor that creates a builder with the pitch, duration, and articulations of the given note.
	 * Ties and connected notations are not copied.
	 *
	 * @param note the note from which pitch, duration, and articulations are copied to the created builder
	 */
	public NoteBuilder(Note note) {
		this(note.getPitch(), note.getDuration());
		note.getArticulations()
				.forEach(articulation -> this.articulations.add(articulation));
	}

	/**
	 * Returns the pitch set in this builder.
	 *
	 * @return the pitch set in this builder
	 */
	public Pitch getPitch() {
		return pitch;
	}

	/**
	 * Sets the pitch in this builder.
	 *
	 * @param pitch the pitch to be set in this builder
	 */
	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}

	/**
	 * Returns the duration set in this builder.
	 *
	 * @return the duration currently set in this builder
	 */
	@Override
	public Duration getDuration() {
		return duration;
	}

	/**
	 * Sets the duration in this builder.
	 *
	 * @param duration The duration to be set in this builder
	 */
	@Override
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	/**
	 * Returns the articulations set in this builder.
	 *
	 * @return the articulations set in this builder
	 */
	public Set<Articulation> getArticulations() {
		return articulations;
	}

	/**
	 * Sets the articulations in this builder.
	 *
	 * @param articulations the articulations that are set into this builder
	 */
	public void setArticulations(Set<Articulation> articulations) {
		this.articulations = articulations;
	}

	/**
	 * Adds an articulation to this builder.
	 *
	 * @param articulation the articulation that is added to this builder
	 */
	public void addArticulation(Articulation articulation) {
		this.articulations.add(articulation);
	}

	/**
	 * Adds the given ornament to this builder.
	 *
	 * @param ornament the ornament to add to this builder
	 */
	public void addOrnament(Ornament ornament) {
		ornaments.add(ornament);
	}

	/**
	 * Returns the ornaments set in this builder.
	 *
	 * @return the ornaments set in this builder
	 */
	public Collection<Ornament> getOrnaments() {
		return ornaments;
	}

	/**
	 * Tie this builder to a following builder. The builder that this is tied to
	 * should be built before this. When the notes are built the {@link Note}
	 * returned by the call of {@link #build build} on this will be tied to the
	 * <code>Note</code> built by builder.
	 *
	 * @param builder The builder for the following note that this is to be tied to.
	 */
	public void addTieToFollowing(NoteBuilder builder) {
		connectWith(Notation.of(Notation.Type.TIE), builder);
	}

	/**
	 * Returns true if this is tied from the previous note or notebuilder.
	 *
	 * @return true if this is tied from the previous note or notebuilder
	 */
	public boolean isTiedFromPrevious() {
		return connectedFrom.stream().anyMatch(connection -> connection.getType().equals(Notation.Type.TIE));
	}

	/**
	 * Returns the NoteBuilder of the following tied note, if there is one.
	 *
	 * @return Optional containing the NoteBuilder of the following tied note if
	 * there is one, otherwise empty Optional.
	 */
	public Optional<NoteBuilder> getFollowingTied() {
		Optional<Notation> tieConnection = noteConnections.keySet().stream()
				.filter(notation -> notation.getType().equals(Notation.Type.TIE))
				.findAny();

		if (tieConnection.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(noteConnections.get(tieConnection));
	}

	void addToConnectedFrom(Notation notation) {
		connectedFrom.add(notation);
	}

	/**
	 * Connects this builder to the given builder with the specified notation.
	 * <p>
	 * The connections set using this method are automatically resolved and built when the note builder is built.
	 *
	 * @param notation          the notation with which this is connected to the target
	 * @param targetNoteBuilder the note builder to which this is connected using the given notation
	 */
	public void connectWith(Notation notation, NoteBuilder targetNoteBuilder) {
		noteConnections.put(notation, targetNoteBuilder);
		targetNoteBuilder.addToConnectedFrom(notation);
	}

	/**
	 * Connects this builder to the given grace note builder with the specified notation.
	 * <p>
	 * The connections set using this method are automatically resolved and built when the note builder is built.
	 *
	 * @param notation          the notation with which this is connected to the target
	 * @param targetNoteBuilder the grace note builder to which this is connected using the given notation
	 */
	public void connectWith(Notation notation, GraceNoteBuilder targetNoteBuilder) {
		graceNoteConnections.put(notation, targetNoteBuilder);
		targetNoteBuilder.addToConnectedFrom(notation);
	}

	/**
	 * Removes the cached note that was built on the previous call of {@link #build
	 * build}.
	 */
	public void clearCache() {
		this.cachedNote = null;
	}

	Collection<Notation.Connection> getResolvedNotationConnections() {
		Set<Notation> notations = new HashSet<>(connectedFrom);
		notations.addAll(noteConnections.keySet());
		notations.addAll(graceNoteConnections.keySet());
		return notations.stream().map(notation -> resolveConnection(notation)).collect(Collectors.toList());
	}

	private Notation.Connection resolveConnection(Notation notation) {

		Notation.Connection connection;
		final boolean isConnectedToNote = noteConnections.containsKey(notation);
		final boolean isConnectedToGraceNote = graceNoteConnections.containsKey(notation);

		if (connectedFrom.contains(notation)) {
			if (!isConnectedToNote && !isConnectedToGraceNote) {
				connection = Notation.Connection.endOf(notation);
			} else if (isConnectedToNote) {
				connection = Notation.Connection.of(notation, noteConnections.get(notation).build());
			} else {
				connection = Notation.Connection.of(notation, graceNoteConnections.get(notation).build());
			}
		} else {
			if (isConnectedToNote) {
				connection = Notation.Connection.beginningOf(notation, noteConnections.get(notation).build());
			} else {
				connection = Notation.Connection.beginningOf(notation, graceNoteConnections.get(notation).build());
			}
		}

		return connection;
	}

	/**
	 * Creates a note with the values set in this builder. This method has side
	 * effects. The method calls {@link #build build} recursively on the following
	 * {@link NoteBuilder} and {@link GraceNoteBuilder}  objects to which this is tied. Calling this method to
	 * create the first note in a sequence of tied notes builds all the tied notes.
	 * The {@link Note} objects are cached in the builders. In case of tied notes,
	 * the temporally first one should be built first.
	 *
	 * @return a note instance with the values set in this builder.
	 */
	@Override
	public Note build() {

		if (this.cachedNote == null) {
			if (isBuilding) {
				throw new IllegalStateException("Trying to build a note from a builder that is currently building. "
						+ "This can be caused by concurrent modification or by a circular dependency between "
						+ "note builders caused by slurs or notations.");
			}

			isBuilding = true;

			this.cachedNote = Note
					.of(this.pitch, this.duration, this.articulations, getResolvedNotationConnections(), ornaments);

			isBuilding = false;
		}

		return this.cachedNote;
	}
}
