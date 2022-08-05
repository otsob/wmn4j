/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChordBuilderTest {

	private List<NoteBuilder> getCMajorAsNoteBuilders() {
		final NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		final List<NoteBuilder> cMajor = new ArrayList<>();
		cMajor.add(first);
		cMajor.add(second);
		cMajor.add(third);

		return cMajor;
	}

	@Test
	void testWhenChordBuilderCreatedFromChordThenCorrectValuesAreSet() {
		final Chord cMajor = Chord.of(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));

		ChordBuilder cMajorBuilder = new ChordBuilder(cMajor);
		assertEquals(cMajor, cMajorBuilder.build());
	}

	@Test
	void testConstructorWithListOfNoteBuilders() {
		final ChordBuilder builder = new ChordBuilder(getCMajorAsNoteBuilders());

		final Chord chord = builder.build();
		assertEquals(3, chord.getNoteCount());
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)));
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
		chordBuilder.removeIf(
				(NoteBuilder nb) -> nb.getPitch().equals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4)));

		final Chord chord = chordBuilder.build();
		assertEquals(2, chord.getNoteCount());
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)));
		assertTrue(chord.contains(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)));
	}
}
