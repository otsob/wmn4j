/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import org.wmn4j.notation.elements.Articulation;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Marking;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;

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
 */
public class NoteBuilder implements DurationalBuilder {

	private Pitch pitch;
	private Duration duration;
	private Set<Articulation> articulations;
	private Map<Marking, NoteBuilder> connectedTo = new HashMap<>();
	private Set<Marking> connectedFrom = new HashSet<>();
	private Note tiedTo;
	private boolean isTiedFromPrevious;
	private NoteBuilder followingTied;

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
		this.isTiedFromPrevious = false;
	}

	/**
	 * Copy constructor for NoteBuilder. Creates a new instance of NoteBuilder that
	 * is a copy of the NoteBuilder given as an attribute.
	 * This does not copy connections to other builders through markings.
	 *
	 * @param builder the NoteBuilder to be copied
	 */
	public NoteBuilder(NoteBuilder builder) {
		this(builder.getPitch(), builder.getDuration());
		builder.getArticulations()
				.forEach(articulation -> this.articulations.add(articulation));
		this.tiedTo = builder.getTiedTo();
		this.isTiedFromPrevious = builder.isTiedFromPrevious();
		builder.getFollowingTied()
				.ifPresent(tied -> this.followingTied = new NoteBuilder(tied));
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
	 * Tie this builder to a following builder. The builder that this is tied to
	 * should be built before this. When the notes are built the <code>Note</code>
	 * returned by the call of {@link #build build} on this will be tied to the
	 * <code>Note</code> built by builder.
	 *
	 * @param builder The builder for the following note that this is to be tied to.
	 */
	public void addTieToFollowing(NoteBuilder builder) {
		this.followingTied = builder;
		builder.setIsTiedFromPrevious(true);
	}

	/**
	 * Returns the following note to which this builder is tied.
	 *
	 * @return the note to which this is tied
	 */
	public Note getTiedTo() {
		return tiedTo;
	}

	/**
	 * Sets this to be tied to the given note.
	 *
	 * @param tiedTo the note to which this is set to be tied
	 */
	public void setTiedTo(Note tiedTo) {
		this.tiedTo = tiedTo;
	}

	/**
	 * Returns true if this is tied from the previous note or notebuilder.
	 *
	 * @return true if this is tied from the previous note or notebuilder
	 */
	public boolean isTiedFromPrevious() {
		return isTiedFromPrevious;
	}

	/**
	 * Sets this to be tied from the previous.
	 *
	 * @param isTiedFromPrevious the value that defines whether this is tied from
	 *                           the previous note or note builder
	 */
	public void setIsTiedFromPrevious(boolean isTiedFromPrevious) {
		this.isTiedFromPrevious = isTiedFromPrevious;
	}

	/**
	 * Returns the NoteBuilder of the following tied note, if there is one.
	 *
	 * @return Optional containing the NoteBuilder of the following tied note if
	 * there is one, otherwise empty Optional.
	 */
	public Optional<NoteBuilder> getFollowingTied() {
		return Optional.ofNullable(followingTied);
	}

	/**
	 * Connects this builder to the given builder with the specified marking.
	 * <p>
	 * The connections set using this method are automatically resolved and built when the note builder is built.
	 *
	 * @param marking           the marking with which this is connected to the target
	 * @param targetNoteBuilder the note builder to which this is connected using the given marking
	 */
	public void connectWith(Marking marking, NoteBuilder targetNoteBuilder) {
		connectedTo.put(marking, targetNoteBuilder);
		targetNoteBuilder.connectedFrom.add(marking);
	}

	/**
	 * Removes the cached note that was built on the previous call of {@link #build
	 * build}.
	 */
	public void clearCache() {
		this.cachedNote = null;
	}

	private Collection<Marking.Connection> getResolvedMarkingConnections() {
		Set<Marking> markings = new HashSet<>(connectedFrom);
		markings.addAll(connectedTo.keySet());
		return markings.stream().map(marking -> resolveConnection(marking)).collect(Collectors.toList());
	}

	private Marking.Connection resolveConnection(Marking marking) {

		if (!connectedTo.containsKey(marking) && connectedFrom.contains(marking)) {
			return Marking.Connection.endOf(marking);
		}

		if (connectedTo.containsKey(marking) && !connectedFrom.contains(marking)) {
			return Marking.Connection.beginningOf(marking, connectedTo.get(marking).build());
		}

		return Marking.Connection.of(marking, connectedTo.get(marking).build());
	}

	/**
	 * Creates a note with the values set in this builder. This method has side
	 * effects. The method calls {@link #build build} recursively on the following
	 * {@link NoteBuilder} objects to which this is tied. Calling this method to
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
						+ "note builders caused by slurs or markings.");
			}

			isBuilding = true;

			if (this.followingTied != null) {
				this.tiedTo = this.followingTied.build();
			}

			this.cachedNote = Note.of(this.pitch, this.duration, this.articulations, getResolvedMarkingConnections(),
					this.tiedTo, this.isTiedFromPrevious);

			isBuilding = false;
		}

		return this.cachedNote;
	}
}
