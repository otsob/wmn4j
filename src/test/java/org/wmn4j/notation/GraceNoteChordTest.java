/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GraceNoteChordTest {

	private final List<GraceNote> cMajorNotes;
	private final GraceNoteChord cMajor;
	private final GraceNoteChord dMajor;
	private final GraceNoteChord fMinor;
	private final GraceNoteChord dMinorMaj9;

	GraceNoteChordTest() {
		this.cMajorNotes = new ArrayList<>();
		this.cMajorNotes
				.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
						.build());
		this.cMajorNotes
				.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
						.build());
		this.cMajorNotes
				.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
						.build());
		this.cMajor = GraceNoteChord.of(this.cMajorNotes);

		final List<GraceNote> dMajorNotes = new ArrayList<>();
		dMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 3), Durations.QUARTER)
				.build());
		dMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3), Durations.QUARTER)
				.build());
		dMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.QUARTER)
				.build());
		this.dMajor = GraceNoteChord.of(dMajorNotes);

		final List<GraceNote> fMinorNotes = new ArrayList<>();
		fMinorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
				.build());
		fMinorNotes
				.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.FLAT, 4), Durations.QUARTER).build());
		fMinorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER)
				.build());
		this.fMinor = GraceNoteChord.of(fMinorNotes);

		final ArrayList<GraceNote> DminorMaj9Notes = new ArrayList<>();
		DminorMaj9Notes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
				.build());
		DminorMaj9Notes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
				.build());
		DminorMaj9Notes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
				.build());
		DminorMaj9Notes
				.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 5), Durations.EIGHTH).build());
		DminorMaj9Notes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH)
				.build());
		this.dMinorMaj9 = GraceNoteChord.of(DminorMaj9Notes);
	}

	@Test
	void testGraceNoteChordImmutable() {

		final List<GraceNote> notes = new ArrayList<>(this.cMajorNotes);
		final GraceNoteChord cMaj = GraceNoteChord.of(notes);
		assertEquals(this.cMajor, cMaj);

		// Test that modifying the list of notes used to create the chord does not
		// modify the chord.
		notes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH).build());
		assertEquals(3, cMaj.getNoteCount());
		assertEquals(this.cMajor, cMaj);
	}

	@Test
	void testGetNoteOrderCorrect() {
		for (int i = 0; i < cMajor.getNoteCount(); ++i) {
			if (i != 0) {
				assertFalse(cMajor.getNote(i - 1).getPitch().isHigherThan(cMajor.getNote(i).getPitch()));
			}
		}
	}

	@Test
	void testGivenGraceNotesWithDifferentDurationTypesThenExceptionIsThrown() {
		final GraceNote a = new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3),
				Durations.EIGHTH).build();
		final GraceNote b = new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3),
				Durations.QUARTER).build();
		final ArrayList<GraceNote> notes = new ArrayList<>();
		notes.add(a);
		notes.add(b);
		assertThrows(IllegalArgumentException.class, () -> GraceNoteChord.of(notes));
	}

	@Test
	void testGivenGraceNotesWithDifferentTypesThenExceptionIsThrown() {
		final GraceNoteBuilder builderA = new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3),
				Durations.QUARTER);
		builderA.setGraceNoteType(Ornamental.Type.ACCIACCATURA);
		final GraceNote a = builderA.build();
		final GraceNote b = new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3),
				Durations.QUARTER).build();
		final ArrayList<GraceNote> notes = new ArrayList<>();
		notes.add(a);
		notes.add(b);
		assertThrows(IllegalArgumentException.class, () -> GraceNoteChord.of(notes));
	}

	@Test
	void testContainsPitch() {
		assertTrue(this.cMajor.contains(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4)));
		assertFalse(this.cMajor.contains(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 4)));
	}

	@Test
	void testGetDisplayableDuration() {
		assertNotEquals(Durations.SIXTEENTH, this.cMajor.getDisplayableDuration());
		assertEquals(Durations.QUARTER, this.dMajor.getDisplayableDuration());
	}

	@Test
	public void testGetNote() {
		assertEquals(
				new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER).build(),
				this.cMajor.getNote(1));
		assertEquals(
				new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.QUARTER).build(),
				this.dMajor.getNote(2));
	}

	@Test
	void testGetLowestNote() {
		assertEquals(
				new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER).build(),
				this.cMajor.getLowestNote());
		assertEquals(
				new GraceNoteBuilder(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), Durations.QUARTER).build(),
				this.fMinor.getLowestNote());
	}

	@Test
	void testGetHighestNote() {
		assertEquals(
				new GraceNoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER).build(),
				this.cMajor.getHighestNote());
		assertEquals(
				new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER).build(),
				this.fMinor.getHighestNote());
	}

	@Test
	void testGetNoteCount() {
		assertEquals(3, this.cMajor.getNoteCount());
		assertEquals(5, this.dMinorMaj9.getNoteCount());
	}

	@Test
	void testEquals() {
		final ArrayList<GraceNote> cMajorNotes = new ArrayList<>();
		cMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
				.build());
		cMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
				.build());
		cMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
				.build());
		final GraceNoteChord cMaj = GraceNoteChord.of(cMajorNotes);
		assertTrue(this.cMajor.equals(cMaj));
		assertTrue(cMaj.equals(this.cMajor));
		assertTrue(this.cMajor.equals(this.cMajor));
		assertFalse(this.cMajor.equals(this.fMinor));
	}

	@Test
	void testAddNote() {
		final GraceNoteChord cMaj = this.cMajor
				.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER)
						.build());
		assertFalse(this.cMajor.equals(cMaj));
		assertEquals(4, cMaj.getNoteCount());
		assertEquals(
				new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER).build(),
				cMaj.getHighestNote());

		assertThrows(IllegalArgumentException.class, () -> this.cMajor
				.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH)
						.build()));
	}

	@Test
	void testHasArticulations() {
		List<GraceNote> cMajorCopy = new ArrayList<>(this.cMajorNotes);
		GraceNoteBuilder builder = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5),
				Durations.QUARTER);
		builder.addArticulation(Articulation.ACCENT);
		cMajorCopy.add(builder.build());
		GraceNoteChord chordWithAccent = GraceNoteChord.of(cMajorCopy);
		assertTrue(chordWithAccent.hasArticulations());
		assertFalse(cMajor.hasArticulations());
	}

	@Test
	void testRemoveNote() {
		final GraceNoteChord D_FSharp = this.dMajor
				.remove(new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.QUARTER)
						.build());
		assertFalse(this.dMajor.equals(D_FSharp));
		assertEquals(2, D_FSharp.getNoteCount());
		assertEquals(new GraceNoteBuilder(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3), Durations.QUARTER).build(),
				D_FSharp.getHighestNote());
	}

	@Test
	void testRemovePitch() {
		final GraceNoteChord D_FSharp = this.dMajor.remove(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3));
		assertFalse(this.dMajor.equals(D_FSharp));
		assertEquals(2, D_FSharp.getNoteCount());
		assertEquals(new GraceNoteBuilder(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3), Durations.QUARTER).build(),
				D_FSharp.getHighestNote());
	}

	@Test
	void testIteration() {
		final ArrayList<GraceNote> cMajorNotes = new ArrayList<>();
		cMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
				.build());
		cMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
				.build());
		cMajorNotes.add(new GraceNoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)
				.build());

		int noteCount = 0;
		for (GraceNote note : this.cMajor) {
			assertEquals(cMajorNotes.get(noteCount), note);
			++noteCount;
			assertTrue(cMajorNotes.contains(note));
		}

		final GraceNoteChord cMaj = GraceNoteChord.of(cMajorNotes);
		final Iterator<GraceNote> iterator = cMaj.iterator();
		iterator.next();
		try {
			iterator.remove();
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}

		for (GraceNote note : cMajorNotes) {
			assertTrue(cMaj.contains(note), "Iterator violated immutability of GraceNoteChord");
		}
	}
}
