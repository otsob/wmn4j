/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for building {@link Note} objects. The built note is cached.
 * <p>
 * Instances of this class are not thread-safe.
 */
public final class NoteBuilder implements DurationalBuilder, ConnectableBuilder {

	private Pitch pitch;
	private Duration duration;
	private Set<Articulation> articulations;
	private Set<Ornament> ornaments;
	private Map<Notation, NoteBuilder> noteConnections = new HashMap<>();
	private Map<Notation, GraceNoteBuilder> graceNoteConnections = new HashMap<>();
	private Set<Notation> connectedFrom = new HashSet<>();
	private List<? extends OrnamentalBuilder> precedingGraceNotes = new ArrayList<>();
	private List<? extends OrnamentalBuilder> succeedingGraceNotes = new ArrayList<>();

	private Note cachedNote;
	private boolean isBuilding;

	/**
	 * Constructor.
	 * <p>
	 * This constructor does not set any default values for the values.
	 */
	public NoteBuilder() {
		this.articulations = EnumSet.noneOf(Articulation.class);
		this.ornaments = new HashSet<>();
	}

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

		builder.getOrnaments().stream()
				.filter(ornament -> !ornament.getType().equals(Ornament.Type.GRACE_NOTES)
						&& !ornament.getType().equals(Ornament.Type.SUCCEEDING_GRACE_NOTES))
				.forEach(ornament -> this.ornaments.add(ornament));
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
	 * For adding grace notes the {@link NoteBuilder#setPrecedingGraceNotes(List)} and
	 * {@link NoteBuilder#setSucceedingGraceNotes(List)} methods should be used.
	 *
	 * @param ornament the ornament to add to this builder
	 */
	public void addOrnament(Ornament ornament) {
		ornaments.add(ornament);
	}

	/**
	 * Sets the given grace note builders to this builder.
	 * The grace notes will be built and added to this as preceding grace notes.
	 * The grace notes will be connected to the primary note with the note connections defined
	 * in the last grace note builder in the list. This builder takes ownership of the given builders.
	 *
	 * @param graceNoteBuilders the builders for the grace notes that this builder will add to the built note
	 */
	public void setPrecedingGraceNotes(List<? extends OrnamentalBuilder> graceNoteBuilders) {
		precedingGraceNotes = graceNoteBuilders;
		setPrincipalNoteForAll(precedingGraceNotes);
	}

	/**
	 * Sets the given grace note builders to this builder.
	 * The grace notes will be built and added to this as succeeding grace notes.
	 * This builder takes ownership of the given builders.
	 *
	 * @param graceNoteBuilders the builders for the grace notes that this builder will add to the built note
	 */
	public void setSucceedingGraceNotes(List<? extends OrnamentalBuilder> graceNoteBuilders) {
		succeedingGraceNotes = graceNoteBuilders;
		setPrincipalNoteForAll(succeedingGraceNotes);
	}

	private void setPrincipalNoteForAll(List<? extends OrnamentalBuilder> ornamentalBuilders) {
		for (OrnamentalBuilder builder : ornamentalBuilders) {
			if (builder instanceof GraceNoteBuilder) {
				((GraceNoteBuilder) builder).setPrincipalNote(this);
			} else if (builder instanceof GraceNoteChordBuilder) {
				((GraceNoteChordBuilder) builder).setPrincipalNote(this);
			}
		}
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

	@Override
	public void connectWith(Notation notation, NoteBuilder targetNoteBuilder) {
		noteConnections.put(notation, targetNoteBuilder);
		targetNoteBuilder.addToConnectedFrom(notation);
	}

	@Override
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
				connection = Notation.Connection.of(notation, buildGraceNoteThroughPrincipal(notation));
			}
		} else {
			if (isConnectedToNote) {
				connection = Notation.Connection.beginningOf(notation, noteConnections.get(notation).build());
			} else {
				connection = Notation.Connection.beginningOf(notation, buildGraceNoteThroughPrincipal(notation));
			}
		}

		return connection;
	}

	private GraceNote buildGraceNoteThroughPrincipal(Notation notation) {
		final GraceNoteBuilder graceNoteBuilder = graceNoteConnections.get(notation);
		final Optional<NoteBuilder> principal = graceNoteBuilder.getPrincipalNote();

		// If the principal note builder is present and is not building (i.e. it is not
		// the principal note of the grace not being built, build it before
		// the grace note.
		if (principal.isPresent() && !principal.get().isBuilding) {
			principal.get().build();
		}

		return graceNoteBuilder.build();
	}

	Map<Notation, NoteBuilder> getNoteConnections() {
		return noteConnections;
	}

	Set<Notation> getConnectedFrom() {
		return connectedFrom;
	}

	private List<Ornament> buildGraceNotes() {
		// There can be at most two elements added to this list.
		List<Ornament> graceNotes = new ArrayList<>(2);

		if (!succeedingGraceNotes.isEmpty()) {
			List<Ornamental> builtGraceNotes = succeedingGraceNotes.stream().map(OrnamentalBuilder::build)
					.collect(Collectors.toList());

			graceNotes.add(Ornament.succeedingGraceNotes(builtGraceNotes));
		}

		if (!precedingGraceNotes.isEmpty()) {
			for (GraceNoteBuilder lastGraceNoteBuilder : getPrecedingGraceNoteBuilders()) {
				List<Notation.Connection> principalNoteConnections = new ArrayList<>();
				Map<Notation, NoteBuilder> noteConnectionsFromGraceNote = lastGraceNoteBuilder.getNoteConnections();
				Set<Notation> lastGraceNoteConnectedFrom = lastGraceNoteBuilder.getConnectedFrom();

				List<Notation> notationsToRemove = new ArrayList<>();

				for (Notation notation : noteConnectionsFromGraceNote.keySet()) {
					if (noteConnectionsFromGraceNote.get(notation) == this) {
						if (lastGraceNoteConnectedFrom.contains(notation)) {
							principalNoteConnections
									.add(Notation.Connection.of(notation, createWithPitchAndDuration()));
						} else {
							principalNoteConnections
									.add(Notation.Connection.beginningOf(notation, createWithPitchAndDuration()));
						}
						notationsToRemove.add(notation);
					}
				}

				for (Notation notation : notationsToRemove) {
					noteConnectionsFromGraceNote.remove(notation);
					lastGraceNoteConnectedFrom.remove(notation);
				}

				lastGraceNoteBuilder.setPrincipalNoteConnections(principalNoteConnections);
			}

			List<Ornamental> builtGraceNotes = precedingGraceNotes.stream().map(OrnamentalBuilder::build)
					.collect(Collectors.toList());

			graceNotes.add(Ornament.graceNotes(builtGraceNotes));
		}

		return graceNotes;
	}

	private Iterable<GraceNoteBuilder> getPrecedingGraceNoteBuilders() {
		OrnamentalBuilder last = precedingGraceNotes.get(precedingGraceNotes.size() - 1);
		if (last instanceof GraceNoteBuilder) {
			return Arrays.asList((GraceNoteBuilder) last);
		} else if (last instanceof GraceNoteChordBuilder) {
			return (GraceNoteChordBuilder) last;
		}

		return Collections.emptyList();
	}

	private Note createWithPitchAndDuration() {
		return Note.of(pitch, duration);
	}

	Collection<Ornament> buildOrnaments() {
		List<Ornament> allOrnaments = new ArrayList<>(ornaments);
		allOrnaments.addAll(buildGraceNotes());
		return allOrnaments;
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
					.of(this.pitch, this.duration, this.articulations, getResolvedNotationConnections(),
							buildOrnaments());

			updateGraceNoteBuilders();

			isBuilding = false;
		}

		return this.cachedNote;
	}

	/*
	 * After the note has been built, the GraceNoteBuilders need to be updated to
	 * have the correct cached GraceNotes in them to ensure all connected notations
	 * are resolved correctly for other notes if they are connected to the grace notes
	 * of the built note.
	 */
	private void updateGraceNoteBuilders() {
		Optional<Ornament> precedingGraceNotesOpt = cachedNote.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(
						Ornament.Type.GRACE_NOTES)).findFirst();

		updateGraceNotes(precedingGraceNotes, precedingGraceNotesOpt);

		Optional<Ornament> succeedingGraceNotesOpt = cachedNote.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(
						Ornament.Type.SUCCEEDING_GRACE_NOTES)).findFirst();

		updateGraceNotes(succeedingGraceNotes, succeedingGraceNotesOpt);
	}

	private void updateGraceNotes(List<? extends OrnamentalBuilder> builders, Optional<Ornament> builtGraceNotes) {
		if (builtGraceNotes.isPresent()) {
			List<Ornamental> ornamenalNotes = builtGraceNotes.get().getOrnamentalNotes();

			for (int i = 0; i < ornamenalNotes.size(); ++i) {
				OrnamentalBuilder builder = builders.get(i);
				if (builder instanceof GraceNoteBuilder) {
					((GraceNoteBuilder) builder).setCachedNote((GraceNote) ornamenalNotes.get(i));
				} else if (builder instanceof GraceNoteChordBuilder) {
					((GraceNoteChordBuilder) builder).setCachedChord((GraceNoteChord) ornamenalNotes.get(i));
				}
			}
		}
	}
}
