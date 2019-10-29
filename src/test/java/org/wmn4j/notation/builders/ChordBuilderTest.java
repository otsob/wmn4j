/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

package org.wmn4j.notation.builders;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChordBuilderTest {

	private List<NoteBuilder> getCMajorAsNoteBuilders() {
		final NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER);
		final NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final List<NoteBuilder> cMajor = new ArrayList<>();
		cMajor.add(first);
		cMajor.add(second);
		cMajor.add(third);

		return cMajor;
	}

	@Test
	void testWhenChordBuilderCreatedFromChordThenCorrectValuesAreSet() {
		final Chord cMajor = Chord.of(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER));

		ChordBuilder cMajorBuilder = new ChordBuilder(cMajor);
		assertEquals(cMajor, cMajorBuilder.build());
	}

	@Test
	void testConstructorWithListOfNoteBuilders() {
		final ChordBuilder builder = new ChordBuilder(getCMajorAsNoteBuilders());

		final Chord chord = builder.build();
		assertEquals(3, chord.getNoteCount());
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER)));
	}

	@Test
	void testConstructorWithListOfNoteBuildersCopiesNoteBuilders() {
		final List<NoteBuilder> cMajor = getCMajorAsNoteBuilders();

		final ChordBuilder builder = new ChordBuilder(cMajor);
		cMajor.get(0).setPitch(Pitch.of(Pitch.Base.B, 0, 4));

		final Chord chord = builder.build();
		assertEquals(3, chord.getNoteCount());
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER)));
	}

	@Test
	void testConstructorWithSingleNoteBuilderCopiesNoteBuilder() {
		final NoteBuilder note = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final ChordBuilder builder = new ChordBuilder(note);

		note.setPitch(Pitch.of(Pitch.Base.D, 0, 4));

		final Chord chord = builder.build();
		assertEquals(1, chord.getNoteCount());
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));
	}

	@Test
	void testAddNoteBuilderCopiesNoteBuilder() {
		final List<NoteBuilder> cMajor = getCMajorAsNoteBuilders();
		final ChordBuilder builder = new ChordBuilder(cMajor);

		final NoteBuilder note = new NoteBuilder(Pitch.of(Pitch.Base.B, 0, 4), Durations.QUARTER);
		builder.add(note);
		note.setPitch(Pitch.of(Pitch.Base.A, 0, 4));

		final Chord chord = builder.build();
		assertEquals(4, chord.getNoteCount());
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.B, 0, 4), Durations.QUARTER)));
	}

	@Test
	void testIterator() {
		final ChordBuilder builder = new ChordBuilder(getCMajorAsNoteBuilders());

		int builders = 0;
		for (NoteBuilder noteBuilder : builder) {
			builders++;
		}
		assertEquals(3, builders);
	}

	@Test
	void testSetDuration() {
		final ChordBuilder chordBuilder = new ChordBuilder(getCMajorAsNoteBuilders());
		chordBuilder.setDuration(Durations.HALF);

		for (NoteBuilder noteBuilder : chordBuilder) {
			assertEquals(Durations.HALF, noteBuilder.getDuration());
		}
		assertEquals(Durations.HALF, chordBuilder.getDuration());
	}

	@Test
	void testRemoveIf() {
		final ChordBuilder chordBuilder = new ChordBuilder(getCMajorAsNoteBuilders());
		chordBuilder.removeIf((NoteBuilder nb) -> nb.getPitch().equals(Pitch.of(Pitch.Base.C, 0, 4)));

		final Chord chord = chordBuilder.build();
		assertEquals(2, chord.getNoteCount());
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER)));
	}
}
