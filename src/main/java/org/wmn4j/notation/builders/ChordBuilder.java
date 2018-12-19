/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import java.util.ArrayList;
import java.util.List;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Note;

/**
 * Class for building <code>Chord</code> objects.
 */
public class ChordBuilder implements DurationalBuilder {

	private final List<NoteBuilder> noteBuilders;
	private Duration duration;

	/**
	 * Constructor.
	 *
	 * @param noteBuilder the note builder that specifies the first note in this
	 *                    builder
	 */
	public ChordBuilder(NoteBuilder noteBuilder) {
		this.duration = noteBuilder.getDuration();
		this.noteBuilders = new ArrayList<>();
		this.noteBuilders.add(new NoteBuilder(noteBuilder));
	}

	/**
	 * Constructor.
	 *
	 * @param noteBuilders the note builders that are placed into this builder
	 */
	public ChordBuilder(List<NoteBuilder> noteBuilders) {
		List<NoteBuilder> noteBuildersCopy = new ArrayList<>();
		noteBuilders.forEach(builder -> noteBuildersCopy.add(new NoteBuilder(builder)));
		this.noteBuilders = noteBuildersCopy;
		this.duration = noteBuilders.get(0).getDuration();	
	}

	/**
	 * Adds the given NoteBuilder into this builder.
	 *
	 * @param noteBuilder the note builder that is added to this builder
	 * @return reference to this
	 */
	public ChordBuilder add(NoteBuilder noteBuilder) {
		// TODO: Make a copy of the NoteBuilder.
		this.noteBuilders.add(noteBuilder);
		return this;
	}

	// TODO: Add methods for:
	// Removing
	// Setting at index
	// Getting at index
	// Changing duration.

	// TODO: Add tests.

	@Override
	public Duration getDuration() {
		return this.duration;
	}

	@Override
	public Chord build() {
		List<Note> notes = new ArrayList<>();
		this.noteBuilders.forEach((builder) -> notes.add(builder.build()));
		return Chord.getChord(notes);
	}

}
