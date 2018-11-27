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
 * 
 * @author Otso Björklund
 */
public class ChordBuilder implements DurationalBuilder {

	private final List<NoteBuilder> noteBuilders;
	private Duration duration;

	public ChordBuilder(NoteBuilder noteBuilder) {
		this.duration = noteBuilder.getDuration();
		this.noteBuilders = new ArrayList<>();
		this.noteBuilders.add(noteBuilder);
	}

	public ChordBuilder(List<NoteBuilder> noteBuilders) {
		// TODO: Make copies of the noteBuilders in the list.
		this.duration = noteBuilders.get(0).getDuration();
		this.noteBuilders = new ArrayList<>(noteBuilders);
	}

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
