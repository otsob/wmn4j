/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.wmn4j.notation.builders.NoteBuilder;

public class NoteTest {

	@Test
	public void testEquals() {
		final Note A1 = Note.of(Pitch.Base.A, 0, 1, Durations.QUARTER);
		final Note A1differentDur = Note.of(Pitch.Base.A, 0, 1, Durations.EIGHT);
		final Note A1Copy = Note.of(Pitch.Base.A, 0, 1, Durations.QUARTER);
		final Note B1 = Note.of(Pitch.Base.B, 0, 1, Durations.QUARTER);
		final Note Asharp1 = Note.of(Pitch.Base.A, 1, 1, Durations.QUARTER);
		final Note C4 = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);

		assertTrue(A1.equals(A1));
		assertTrue(A1.equals(A1Copy));
		assertTrue(A1Copy.equals(A1));
		assertFalse(A1.equals(A1differentDur));
		assertFalse(A1.equals(B1));
		assertFalse(A1.equals(Asharp1));
		assertTrue(C4.equals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));

		final Pitch pitch = Pitch.of(Pitch.Base.C, 0, 1);
		final HashSet<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		final Note note1 = Note.of(pitch, Durations.EIGHT, articulations);
		articulations.add(Articulation.TENUTO);
		final Note note2 = Note.of(pitch, Durations.EIGHT, articulations);
		final Note note3 = Note.of(pitch, Durations.EIGHT, articulations);

		assertFalse(note1.equals(Note.of(pitch, Durations.EIGHT)));
		assertFalse(note1.equals(note2));
		assertTrue(note2.equals(note2));
		assertTrue(note2.equals(note3));
	}

	@Test
	public void testCreatingInvalidNote() {

		try {
			Note.of(Pitch.Base.C, 5, 1, Durations.QUARTER);
			fail("No exception was thrown. Expected: IllegalArgumentException");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			Note.of(Pitch.Base.C, 0, 11, Durations.QUARTER);
			fail("No exception was thrown. Expected: IllegalArgumentException");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			Note.of(Pitch.Base.C, 0, 1, null);
			fail("No exception was thrown. Expected: IllegalArgumentException");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	@Test
	public void testHasArticulation() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, 0, 1);
		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		assertTrue(Note.of(pitch, Durations.EIGHT, articulations).hasArticulation(Articulation.STACCATO));
		assertFalse(Note.of(pitch, Durations.EIGHT).hasArticulation(Articulation.STACCATO));
	}

	@Test
	public void testHasArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, 0, 1);
		final HashSet<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		assertTrue(Note.of(pitch, Durations.EIGHT, articulations).hasArticulations());
		assertFalse(Note.of(pitch, Durations.EIGHT).hasArticulations());
	}

	@Test
	public void testGetArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, 0, 1);
		assertTrue(Note.of(pitch, Durations.EIGHT).getArticulations().isEmpty());

		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		articulations.add(Articulation.TENUTO);
		final Note note = Note.of(pitch, Durations.EIGHT, articulations);

		final Set<Articulation> artic = note.getArticulations();
		assertEquals(2, artic.size());
		assertTrue(artic.contains(Articulation.STACCATO));
		assertTrue(artic.contains(Articulation.TENUTO));
		try {
			artic.remove(Articulation.STACCATO);
			fail("Removing articulation succeeded, immutability violated");
		} catch (final Exception e) {
			/* Do nothing */ }

		assertTrue(note.hasArticulation(Articulation.STACCATO));
	}

	@Test
	public void testTies() {
		final NoteBuilder firstBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final NoteBuilder secondBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT);
		firstBuilder.addTieToFollowing(secondBuilder);

		final Note secondNote = secondBuilder.build();
		assertTrue(secondNote.isTied());
		assertTrue(!secondNote.getFollowingTiedNote().isPresent());
		assertTrue(secondNote.isTiedFromPrevious());

		final Note firstNote = firstBuilder.build();
		assertTrue(firstNote.isTied());
		assertTrue(firstNote.getFollowingTiedNote() != null);
		assertFalse(firstNote.isTiedFromPrevious());

	}

	@Test
	public void testTiedDuration() {
		final Note untied = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		assertEquals(Durations.QUARTER, untied.getTiedDuration());

		final NoteBuilder firstBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final NoteBuilder secondBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT);
		firstBuilder.addTieToFollowing(secondBuilder);

		Note firstNote = firstBuilder.build();
		Note secondNote = secondBuilder.build();

		assertEquals(Durations.QUARTER.addDot(), firstNote.getTiedDuration());
		assertEquals(Durations.EIGHT, secondNote.getTiedDuration());

		firstBuilder.clearCache();
		secondBuilder.clearCache();

		final NoteBuilder thirdBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT);
		secondBuilder.addTieToFollowing(thirdBuilder);

		firstNote = firstBuilder.build();
		secondNote = secondBuilder.build();
		final Note thirdNote = thirdBuilder.build();

		assertEquals(Durations.HALF, firstNote.getTiedDuration());
		assertEquals(Durations.QUARTER, secondNote.getTiedDuration());
		assertEquals(Durations.EIGHT, thirdNote.getTiedDuration());
	}
}
