/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;
import org.wmn4j.notation.elements.Articulation;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.MultiNoteArticulation;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.iterators.ScorePosition;

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
	
	@Test
	public void testCopyConstructor() {
		NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder copy = new NoteBuilder(builder);
		assertNotSame(builder, copy);
		
		assertEquals(Durations.QUARTER, copy.getDuration());
		assertEquals(Pitch.getPitch(Pitch.Base.C, 0, 4), copy.getPitch());
		
		builder.setDuration(Durations.HALF);
		assertEquals(Durations.QUARTER, copy.getDuration());
		
		builder.setPitch(Pitch.getPitch(Pitch.Base.D, 0, 4));
		assertEquals(Pitch.getPitch(Pitch.Base.C, 0, 4), copy.getPitch());
	}
	
	@Test
	public void testCopyConstructorArticulations() {
		NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);
		
		NoteBuilder copy = new NoteBuilder(builder);
		
		builder.addArticulation(Articulation.FERMATA);
		assertTrue(copy.getArticulations().size() == 1);
		assertTrue(copy.getArticulations().contains(Articulation.STACCATO));
	}
	
	@Test
	public void testCopyConstructorMultiNoteArticulations() {
		NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		MultiNoteArticulation glissando = new MultiNoteArticulation(MultiNoteArticulation.Type.GLISSANDO);
		builder.addMultiNoteArticulation(glissando);
		
		NoteBuilder copy = new NoteBuilder(builder);
		
		builder.addMultiNoteArticulation(new MultiNoteArticulation(MultiNoteArticulation.Type.SLUR));
		assertTrue(copy.getMultiNoteArticulations().size() == 1);
		assertTrue(copy.getMultiNoteArticulations().contains(glissando));
	}
	
	@Test
	public void testCopyConstructorsFollowingTiedIsCopiedAsWell() {
		NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder following = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
		builder.addTieToFollowing(following);
		
		NoteBuilder copy = new NoteBuilder(builder);
		assertNotSame(builder.getFollowingTied().get(), copy.getFollowingTied().get());
		assertEquals(Durations.HALF, copy.getFollowingTied().get().getDuration());
	}

}
