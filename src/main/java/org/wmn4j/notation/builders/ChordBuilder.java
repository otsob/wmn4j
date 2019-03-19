/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Note;

/**
 * Class for building <code>Chord</code> objects.
 */
public class ChordBuilder implements DurationalBuilder, Iterable<NoteBuilder> {

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
		final List<NoteBuilder> noteBuildersCopy = new ArrayList<>();
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
		this.noteBuilders.add(new NoteBuilder(noteBuilder));
		return this;
	}

	/**
	 * Remove a NoteBuilder placed in this ChordBuilder.
	 *
	 * @param filter Predicate to determine which NoteBuilders are removed
	 */
	public void removeIf(Predicate<NoteBuilder> filter) {
		this.noteBuilders.removeIf(filter);
	}

	/**
	 * Sets a new duration to this builder and all the NoteBuilders in it.
	 *
	 * @param duration Duration to set
	 */
	@Override
	public void setDuration(Duration duration) {
		for (NoteBuilder builder : this) {
			builder.setDuration(duration);
		}
		this.duration = duration;
	}

	@Override
	public Duration getDuration() {
		return this.duration;
	}

	@Override
	public Iterator<NoteBuilder> iterator() {
		return this.noteBuilders.iterator();
	}

	@Override
	public Chord build() {
		final List<Note> notes = new ArrayList<>();
		this.noteBuilders.forEach((builder) -> notes.add(builder.build()));
		return Chord.of(notes);
	}

}
