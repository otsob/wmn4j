/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ChordTest {

	private final List<Note> cMajorNotes;
	private final Chord cMajor;
	private final Chord dMajor;
	private final Chord fMinor;
	private final Chord dMinorMaj9;

	ChordTest() {
		this.cMajorNotes = new ArrayList<>();
		this.cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		this.cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		this.cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		this.cMajor = Chord.of(this.cMajorNotes);

		final List<Note> dMajorNotes = new ArrayList<>();
		dMajorNotes.add(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 3), Durations.QUARTER));
		dMajorNotes.add(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3), Durations.QUARTER));
		dMajorNotes.add(Note.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.QUARTER));
		this.dMajor = Chord.of(dMajorNotes);

		final List<Note> fMinorNotes = new ArrayList<>();
		fMinorNotes.add(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		fMinorNotes.add(Note.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.FLAT, 4), Durations.QUARTER));
		fMinorNotes.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER));
		this.fMinor = Chord.of(fMinorNotes);

		final ArrayList<Note> DminorMaj9Notes = new ArrayList<>();
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 5), Durations.EIGHTH));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH));
		this.dMinorMaj9 = Chord.of(DminorMaj9Notes);
	}

	@Test
	void testChordImmutable() {

		final List<Note> notes = new ArrayList<>(this.cMajorNotes);
		final Chord cMaj = Chord.of(notes);
		assertEquals(this.cMajor, cMaj);

		// Test that modifying the list of notes used to create the chord does not
		// modify the chord.
		notes.add(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));
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
	void testVarargsFactory() {
		final Note C4 = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
		final Note E4 = Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
		final Chord dyad = Chord.of(C4, E4);
		assertEquals(2, dyad.getNoteCount());
		assertTrue(dyad.contains(E4));
		assertTrue(dyad.contains(C4));
	}

	@Test
	void testGetNoteDurationsSameForAllNotes() {
		final Duration duration = this.dMinorMaj9.getDuration();
		for (int i = 0; i < this.dMinorMaj9.getNoteCount(); ++i) {
			assertEquals(duration, this.dMinorMaj9.getNote(i).getDuration());
		}

		try {
			final Note a = Note.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.EIGHTH);
			final Note b = Note.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.QUARTER);
			final ArrayList<Note> notes = new ArrayList<>();
			notes.add(a);
			notes.add(b);
			Chord.of(notes);
			fail("Failed to throw IllegalArgumentException when "
					+ "creating chord with notes whose durations are not the same");
		} catch (final IllegalArgumentException e) {
			assertTrue(true);
		}
	}

	@Test
	void testContainsPitch() {
		assertTrue(this.cMajor.contains(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4)));
		assertFalse(this.cMajor.contains(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 4)));
	}

	@Test
	void testContainstNote() {
		assertTrue(this.cMajor.contains(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)));
		assertFalse(this.cMajor.contains(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.FLAT, 4), Durations.QUARTER)));
		final HashSet<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		final Note staccatoC5 = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER, articulations);
		final Chord staccatoC = this.cMajor.add(staccatoC5);
		assertTrue(staccatoC.contains(staccatoC5));
		assertFalse(staccatoC.contains(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER)));
		assertFalse(this.cMajor.contains(staccatoC5));
	}

	@Test
	void testGetDuration() {
		assertNotEquals(Durations.SIXTEENTH, this.cMajor.getDuration());
		assertEquals(Durations.QUARTER, this.dMajor.getDuration());
	}

	@Test
	public void testGetNote() {
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER), this.cMajor.getNote(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.QUARTER), this.dMajor.getNote(2));
	}

	@Test
	void testGetLowestNote() {
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER), this.cMajor.getLowestNote());
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), Durations.QUARTER), this.fMinor.getLowestNote());
	}

	@Test
	void testGetHighestNote() {
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER), this.cMajor.getHighestNote());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER), this.fMinor.getHighestNote());
	}

	@Test
	void testGetNoteCount() {
		assertEquals(3, this.cMajor.getNoteCount());
		assertEquals(5, this.dMinorMaj9.getNoteCount());
	}

	@Test
	void testEquals() {
		final ArrayList<Note> cMajorNotes = new ArrayList<>();
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		final Chord cMaj = Chord.of(cMajorNotes);
		assertTrue(this.cMajor.equals(cMaj));
		assertTrue(cMaj.equals(this.cMajor));
		assertTrue(this.cMajor.equals(this.cMajor));
		assertFalse(this.cMajor.equals(this.fMinor));
	}

	@Test
	void testToString() {
		assertEquals("[C4(1/4),E4(1/4),G4(1/4)]", this.cMajor.toString());
		assertEquals("[D3(1/4),F#3(1/4),A3(1/4)]", this.dMajor.toString());
	}

	@Test
	void testAddNote() {
		final Chord cMaj = this.cMajor.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER));
		assertFalse(this.cMajor.equals(cMaj));
		assertEquals(4, cMaj.getNoteCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER), cMaj.getHighestNote());

		try {
			final Chord illegalCMaj = this.cMajor.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH));
			fail("Failed to throw expected exception");
		} catch (final IllegalArgumentException e) {
			// Pass because exception is expected
		}
	}

	@Test
	void testHasArticulations() {
		List<Note> cMajorCopy = new ArrayList<>(this.cMajorNotes);
		Set<Articulation> articulations = EnumSet.of(Articulation.ACCENT);
		cMajorCopy.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER, articulations));
		Chord chordWithAccent = Chord.of(cMajorCopy);
		assertTrue(chordWithAccent.hasArticulations());
		assertFalse(cMajor.hasArticulations());
	}

	@Test
	void testHasArticulation() {
		List<Note> cMajorNotesWithAccentAndStaccato = new ArrayList<>();
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, EnumSet.of(Articulation.ACCENT)));
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, EnumSet.of(Articulation.STACCATO)));
		cMajorNotesWithAccentAndStaccato.add(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		Chord cMajorWithArticulations = Chord.of(cMajorNotesWithAccentAndStaccato);

		assertTrue(cMajorWithArticulations.hasArticulation(Articulation.STACCATO));
		assertTrue(cMajorWithArticulations.hasArticulation(Articulation.ACCENT));
		assertFalse(cMajorWithArticulations.hasArticulation(Articulation.TENUTO));

		assertFalse(cMajor.hasArticulation(Articulation.STACCATO));
		assertFalse(cMajor.hasArticulation(Articulation.ACCENT));
	}

	@Test
	void testGetArticulations() {
		List<Note> cMajorNotesWithAccentAndStaccato = new ArrayList<>();
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, EnumSet.of(Articulation.ACCENT)));
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, EnumSet.of(Articulation.STACCATO)));
		cMajorNotesWithAccentAndStaccato.add(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		Chord cMajorWithArticulations = Chord.of(cMajorNotesWithAccentAndStaccato);

		Collection<Articulation> articulations = cMajorWithArticulations.getArticulations();
		assertEquals(2, articulations.size());
		assertTrue(articulations.contains(Articulation.STACCATO));
		assertTrue(articulations.contains(Articulation.ACCENT));
		assertFalse(articulations.contains(Articulation.TENUTO));

		assertTrue(cMajor.getArticulations().isEmpty());
	}

	@Test
	void testRemoveNote() {
		final Chord D_FSharp = this.dMajor.remove(Note.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3), Durations.QUARTER));
		assertFalse(this.dMajor.equals(D_FSharp));
		assertEquals(2, D_FSharp.getNoteCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3), Durations.QUARTER), D_FSharp.getHighestNote());
	}

	@Test
	void testRemovePitch() {
		final Chord D_FSharp = this.dMajor.remove(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3));
		assertFalse(this.dMajor.equals(D_FSharp));
		assertEquals(2, D_FSharp.getNoteCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3), Durations.QUARTER), D_FSharp.getHighestNote());
	}

	@Test
	void testIteration() {
		final ArrayList<Note> cMajorNotes = new ArrayList<>();
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));

		int noteCount = 0;
		for (Note note : this.cMajor) {
			assertEquals(cMajorNotes.get(noteCount), note);
			++noteCount;
			assertTrue(cMajorNotes.contains(note));
		}

		final Chord cMaj = Chord.of(cMajorNotes);
		final Iterator<Note> iterator = cMaj.iterator();
		iterator.next();
		try {
			iterator.remove();
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}

		for (Note note : cMajorNotes) {
			assertTrue(cMaj.contains(note), "Iterator violated immutability of Chord");
		}
	}
}
