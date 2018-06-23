/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import wmnlibnotation.noteobjects.Articulation;
import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.MultiNoteArticulation;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Pitch;

/**
 * Class for building <code>Note</code> objects. The built note is cached. This
 * is used for ensuring that sequences of tied notes are built correctly.
 * 
 * @author Otso Björklund
 */
public class NoteBuilder implements DurationalBuilder {

	private Pitch pitch;
	private Duration duration;
	private Set<Articulation> articulations;
	private List<MultiNoteArticulation> multiNoteArticulations;
	private Note tiedTo;
	private boolean isTiedFromPrevious;
	private NoteBuilder followingTied;

	private Note cachedNote;

	/**
	 * @param pitch
	 *            The pitch set in this builder.
	 * @param duration
	 *            The duration set in this builder.
	 */
	public NoteBuilder(Pitch pitch, Duration duration) {
		this.pitch = pitch;
		this.duration = duration;
		this.articulations = EnumSet.noneOf(Articulation.class);
		this.multiNoteArticulations = new ArrayList<>();
		this.isTiedFromPrevious = false;
	}

	/**
	 * @return The pitch currently set in this builder.
	 */
	public Pitch getPitch() {
		return pitch;
	}

	/**
	 * @param pitch
	 *            The pitch to be set in this builder.
	 */
	public void setPitch(Pitch pitch) {
		this.pitch = pitch;
	}

	/**
	 * @return The duration currently set in this builder.
	 */
	@Override
	public Duration getDuration() {
		return duration;
	}

	/**
	 * @param duration
	 *            The duration to be set in this builder.
	 */
	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public Set<Articulation> getArticulations() {
		return articulations;
	}

	public void setArticulations(Set<Articulation> articulations) {
		this.articulations = articulations;
	}

	public void addArticulation(Articulation articulation) {
		this.articulations.add(articulation);
	}

	public List<MultiNoteArticulation> getMultiNoteArticulations() {
		return multiNoteArticulations;
	}

	public void addMultiNoteArticulation(MultiNoteArticulation articulation) {
		this.multiNoteArticulations.add(articulation);
	}

	public void setMultiNoteArticulations(List<MultiNoteArticulation> multiNoteArticulations) {
		this.multiNoteArticulations = multiNoteArticulations;
	}

	/**
	 * Tie this builder to a following builder. The builder that this is tied to
	 * should be built before this. When the notes are built the <code>Note</code>
	 * returned by the call of {@link #build build} on this will be tied to the
	 * <code>Note</code> built by builder.
	 * 
	 * @param builder
	 *            The builder for the following note that this is to be tied to.
	 */
	public void addTieToFollowing(NoteBuilder builder) {
		this.followingTied = builder;
		builder.setIsTiedFromPrevious(true);
	}

	public Note getTiedTo() {
		return tiedTo;
	}

	public void setTiedTo(Note tiedTo) {
		this.tiedTo = tiedTo;
	}

	public boolean isIsTiedFromPrevious() {
		return isTiedFromPrevious;
	}

	public void setIsTiedFromPrevious(boolean isTiedFromPrevious) {
		this.isTiedFromPrevious = isTiedFromPrevious;
	}

	/**
	 * Removes the cached <code>Note</code> that was built on the previous call of
	 * {@link #build build}.
	 */
	public void clearCache() {
		this.cachedNote = null;
	}

	/**
	 * Creates a <code>Note</code> with the values set in this builder. This method
	 * has side effects. The method calls {@link #build build} recursively on the
	 * following <code>NoteBuilder</code> objects to which this is tied. Calling
	 * this method to create the first note in a sequence of tied notes builds all
	 * the tied notes. The <code>Note</code> objects are cached in the builders. In
	 * case of tied notes, the temporally first one should be built first.
	 * 
	 * @return <code>Note</code> instance with the values set in this builder.
	 */
	@Override
	public Note build() {

		if (this.cachedNote == null) {
			if (this.followingTied != null) {
				this.tiedTo = this.followingTied.build();
			}

			this.cachedNote = Note.getNote(this.pitch, this.duration, this.articulations, this.multiNoteArticulations,
					this.tiedTo, this.isTiedFromPrevious);
		}

		return this.cachedNote;
	}
}
