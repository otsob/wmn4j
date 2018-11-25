/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;
import org.wmn4j.notation.builders.NoteBuilder;
import org.wmn4j.notation.iterators.ScorePosition;
import org.wmn4j.notation.noteobjects.Articulation;
import org.wmn4j.notation.noteobjects.Durations;
import org.wmn4j.notation.noteobjects.MultiNoteArticulation;
import org.wmn4j.notation.noteobjects.Note;
import org.wmn4j.notation.noteobjects.Pitch;

public class NoteBuilderTest {

	public NoteBuilderTest() {
	}

	@Test
	public void testBuildingBasicNote() {
		NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		Note note = builder.build();
		assertFalse(note.hasArticulations());
		assertFalse(note.hasMultiNoteArticulations());
		assertFalse(note.isTied());
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), note);
	}

	@Test
	public void testBuildingNoteWithAllAttributes() {
		NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);
		builder.addMultiNoteArticulation(new MultiNoteArticulation(MultiNoteArticulation.Type.SLUR));

		Note tiedNote = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		ScorePosition position = new ScorePosition(1, 1, 1, 1);

		builder.setTiedTo(tiedNote);

		Note expected = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER,
				EnumSet.of(Articulation.STACCATO));

		Note note = builder.build();
		assertEquals(expected, note);
		assertTrue(note.isTied());
		assertTrue(note.isTiedToFollowing());
		assertFalse(note.isTiedFromPrevious());
		assertTrue(note.hasMultiNoteArticulations());
	}

	@Test
	public void testBuildingTiedNotes() {
		NoteBuilder first = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.QUARTER);

		first.addTieToFollowing(second);
		second.addTieToFollowing(third);

		Note firstNote = first.build();
		Note secondNote = second.build();
		Note thirdNote = third.build();

		assertEquals(Pitch.getPitch(Pitch.Base.C, 0, 4), firstNote.getPitch());
		assertTrue(firstNote.isTiedToFollowing());
		assertFalse(firstNote.isTiedFromPrevious());
		assertEquals(Pitch.getPitch(Pitch.Base.D, 0, 4), firstNote.getFollowingTiedNote().get().getPitch());

		assertEquals(Pitch.getPitch(Pitch.Base.D, 0, 4), secondNote.getPitch());
		assertTrue(secondNote.isTiedToFollowing());
		assertTrue(secondNote.isTiedFromPrevious());
		assertEquals(Pitch.getPitch(Pitch.Base.E, 0, 4), secondNote.getFollowingTiedNote().get().getPitch());

		assertEquals(Pitch.getPitch(Pitch.Base.E, 0, 4), thirdNote.getPitch());
		assertFalse(thirdNote.isTiedToFollowing());
		assertTrue(thirdNote.isTiedFromPrevious());
	}

}
