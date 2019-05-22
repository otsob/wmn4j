/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ChordTest {

	private final List<Note> cMajorNotes;
	private final Chord cMajor;
	private final Chord dMajor;
	private final Chord fMinor;
	private final Chord dMinorMaj9;

	public ChordTest() {
		this.cMajorNotes = new ArrayList<>();
		this.cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		this.cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER));
		this.cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER));
		this.cMajor = Chord.of(this.cMajorNotes);

		final List<Note> dMajorNotes = new ArrayList<>();
		dMajorNotes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 3), Durations.QUARTER));
		dMajorNotes.add(Note.of(Pitch.of(Pitch.Base.F, 1, 3), Durations.QUARTER));
		dMajorNotes.add(Note.of(Pitch.of(Pitch.Base.A, 0, 3), Durations.QUARTER));
		this.dMajor = Chord.of(dMajorNotes);

		final List<Note> fMinorNotes = new ArrayList<>();
		fMinorNotes.add(Note.of(Pitch.of(Pitch.Base.F, 0, 4), Durations.QUARTER));
		fMinorNotes.add(Note.of(Pitch.of(Pitch.Base.A, -1, 4), Durations.QUARTER));
		fMinorNotes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER));
		this.fMinor = Chord.of(fMinorNotes);

		final ArrayList<Note> DminorMaj9Notes = new ArrayList<>();
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHT));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.F, 0, 4), Durations.EIGHT));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.A, 0, 4), Durations.EIGHT));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.C, 1, 5), Durations.EIGHT));
		DminorMaj9Notes.add(Note.of(Pitch.of(Pitch.Base.E, 0, 5), Durations.EIGHT));
		this.dMinorMaj9 = Chord.of(DminorMaj9Notes);
	}

	@Test
	public void testChordImmutable() {

		final List<Note> notes = new ArrayList<>(this.cMajorNotes);
		final Chord cMaj = Chord.of(notes);
		assertEquals(this.cMajor, cMaj);

		// Test that modifying the list of notes used to create the chord does not
		// modify the chord.
		notes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHT));
		assertEquals(3, cMaj.getNoteCount());
		assertEquals(this.cMajor, cMaj);
	}

	@Test
	public void testGetNoteOrderCorrect() {
		for (int i = 0; i < cMajor.getNoteCount(); ++i) {
			if (i != 0) {
				assertFalse(cMajor.getNote(i - 1).getPitch().isHigherThan(cMajor.getNote(i).getPitch()));
			}
		}
	}

	@Test
	public void testVarargsFactory() {
		final Note C4 = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT);
		final Note E4 = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT);
		final Chord dyad = Chord.of(C4, E4);
		assertEquals(2, dyad.getNoteCount());
		assertTrue(dyad.contains(E4));
		assertTrue(dyad.contains(C4));
	}

	@Test
	public void testGetNoteDurationsSameForAllNotes() {
		final Duration duration = this.dMinorMaj9.getDuration();
		for (int i = 0; i < this.dMinorMaj9.getNoteCount(); ++i) {
			assertEquals(duration, this.dMinorMaj9.getNote(i).getDuration());
		}

		try {
			final Note a = Note.of(Pitch.of(Pitch.Base.A, 0, 3), Durations.EIGHT);
			final Note b = Note.of(Pitch.of(Pitch.Base.A, 0, 3), Durations.QUARTER);
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
	public void testContainsPitch() {
		assertTrue(this.cMajor.contains(Pitch.of(Pitch.Base.C, 0, 4)));
		assertFalse(this.cMajor.contains(Pitch.of(Pitch.Base.C, 1, 4)));
	}

	@Test
	public void testContainstNote() {
		assertTrue(this.cMajor.contains(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));
		assertFalse(this.cMajor.contains(Note.of(Pitch.of(Pitch.Base.C, -1, 4), Durations.QUARTER)));
		final HashSet<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		final Note staccatoC5 = Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER, articulations);
		final Chord staccatoC = this.cMajor.add(staccatoC5);
		assertTrue(staccatoC.contains(staccatoC5));
		assertFalse(staccatoC.contains(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER)));
		assertFalse(this.cMajor.contains(staccatoC5));
	}

	@Test
	public void testGetDuration() {
		assertNotEquals(Durations.SIXTEENTH, this.cMajor.getDuration());
		assertEquals(Durations.QUARTER, this.dMajor.getDuration());
	}

	@Test
	public void testGetNote() {
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER), this.cMajor.getNote(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.A, 0, 3), Durations.QUARTER), this.dMajor.getNote(2));
	}

	@Test
	public void testGetLowestNote() {
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), this.cMajor.getLowestNote());
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, 0, 4), Durations.QUARTER), this.fMinor.getLowestNote());
	}

	@Test
	public void testGetHighestNote() {
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER), this.cMajor.getHighestNote());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER), this.fMinor.getHighestNote());
	}

	@Test
	public void testGetNoteCount() {
		assertEquals(3, this.cMajor.getNoteCount());
		assertEquals(5, this.dMinorMaj9.getNoteCount());
	}

	@Test
	public void testEquals() {
		final ArrayList<Note> cMajorNotes = new ArrayList<>();
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER));
		final Chord cMaj = Chord.of(cMajorNotes);
		assertTrue(this.cMajor.equals(cMaj));
		assertTrue(cMaj.equals(this.cMajor));
		assertTrue(this.cMajor.equals(this.cMajor));
		assertFalse(this.cMajor.equals(this.fMinor));
	}

	@Test
	public void testToString() {
		assertEquals("[C4(1/4),E4(1/4),G4(1/4)]", this.cMajor.toString());
		assertEquals("[D3(1/4),F#3(1/4),A3(1/4)]", this.dMajor.toString());
	}

	@Test
	public void testAddNote() {
		final Chord cMaj = this.cMajor.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER));
		assertFalse(this.cMajor.equals(cMaj));
		assertEquals(4, cMaj.getNoteCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER), cMaj.getHighestNote());

		try {
			final Chord illegalCMaj = this.cMajor.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT));
			fail("Failed to throw expected exception");
		} catch (final IllegalArgumentException e) {
			// Pass because exception is expected
		}
	}

	@Test
	public void testHasArticulations() {
		List<Note> cMajorCopy = new ArrayList<>(this.cMajorNotes);
		Set<Articulation> articulations = EnumSet.of(Articulation.ACCENT);
		cMajorCopy.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER, articulations));
		Chord chordWithAccent = Chord.of(cMajorCopy);
		assertTrue(chordWithAccent.hasArticulations());
		assertFalse(cMajor.hasArticulations());
	}

	@Test
	public void testHasArticulation() {
		List<Note> cMajorNotesWithAccentAndStaccato = new ArrayList<>();
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER, EnumSet.of(Articulation.ACCENT)));
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER, EnumSet.of(Articulation.STACCATO)));
		cMajorNotesWithAccentAndStaccato.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER));
		Chord cMajorWithArticulations = Chord.of(cMajorNotesWithAccentAndStaccato);

		assertTrue(cMajorWithArticulations.hasArticulation(Articulation.STACCATO));
		assertTrue(cMajorWithArticulations.hasArticulation(Articulation.ACCENT));
		assertFalse(cMajorWithArticulations.hasArticulation(Articulation.TENUTO));

		assertFalse(cMajor.hasArticulation(Articulation.STACCATO));
		assertFalse(cMajor.hasArticulation(Articulation.ACCENT));
	}

	@Test
	public void testGetArticulations() {
		List<Note> cMajorNotesWithAccentAndStaccato = new ArrayList<>();
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER, EnumSet.of(Articulation.ACCENT)));
		cMajorNotesWithAccentAndStaccato
				.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER, EnumSet.of(Articulation.STACCATO)));
		cMajorNotesWithAccentAndStaccato.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER));
		Chord cMajorWithArticulations = Chord.of(cMajorNotesWithAccentAndStaccato);

		Collection<Articulation> articulations = cMajorWithArticulations.getArticulations();
		assertEquals(2, articulations.size());
		assertTrue(articulations.contains(Articulation.STACCATO));
		assertTrue(articulations.contains(Articulation.ACCENT));
		assertFalse(articulations.contains(Articulation.TENUTO));

		assertTrue(cMajor.getArticulations().isEmpty());
	}

	@Test
	public void testRemoveNote() {
		final Chord D_FSharp = this.dMajor.remove(Note.of(Pitch.of(Pitch.Base.A, 0, 3), Durations.QUARTER));
		assertFalse(this.dMajor.equals(D_FSharp));
		assertEquals(2, D_FSharp.getNoteCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, 1, 3), Durations.QUARTER), D_FSharp.getHighestNote());
	}

	@Test
	public void testRemovePitch() {
		final Chord D_FSharp = this.dMajor.remove(Pitch.of(Pitch.Base.A, 0, 3));
		assertFalse(this.dMajor.equals(D_FSharp));
		assertEquals(2, D_FSharp.getNoteCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, 1, 3), Durations.QUARTER), D_FSharp.getHighestNote());
	}

	@Test
	public void testIteration() {
		final ArrayList<Note> cMajorNotes = new ArrayList<>();
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER));
		cMajorNotes.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER));

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
			assertTrue("Iterator violated immutability of Chord", cMaj.contains(note));
		}
	}
}
