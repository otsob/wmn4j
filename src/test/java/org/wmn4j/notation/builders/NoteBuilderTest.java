/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.Articulation;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Marking;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class NoteBuilderTest {

	public NoteBuilderTest() {
	}

	@Test
	public void testBuildingBasicNote() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final Note note = builder.build();
		assertFalse(note.hasArticulations());
		assertFalse(note.hasMarkings());
		assertFalse(note.isTied());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), note);
	}

	@Test
	public void testBuildingNoteWithAllAttributes() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);

		builder.connectWith(Marking.of(Marking.Type.SLUR),
				new NoteBuilder(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));

		final Note tiedNote = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);

		builder.setTiedTo(tiedNote);

		final Note expected = Note
				.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER, EnumSet.of(Articulation.STACCATO));

		final Note note = builder.build();
		assertTrue(expected.equalsInPitchAndDuration(note));
		assertFalse(expected.equals(note));
		assertTrue(note.isTied());
		assertTrue(note.isTiedToFollowing());
		assertFalse(note.isTiedFromPrevious());
		assertTrue(note.begins(Marking.Type.SLUR));
	}

	@Test
	public void testBuildingTiedNotes() {
		final NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER);
		final NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		first.addTieToFollowing(second);
		second.addTieToFollowing(third);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertEquals(Pitch.of(Pitch.Base.C, 0, 4), firstNote.getPitch());
		assertTrue(firstNote.isTiedToFollowing());
		assertFalse(firstNote.isTiedFromPrevious());
		assertEquals(Pitch.of(Pitch.Base.D, 0, 4), firstNote.getFollowingTiedNote().get().getPitch());

		assertEquals(Pitch.of(Pitch.Base.D, 0, 4), secondNote.getPitch());
		assertTrue(secondNote.isTiedToFollowing());
		assertTrue(secondNote.isTiedFromPrevious());
		assertEquals(Pitch.of(Pitch.Base.E, 0, 4), secondNote.getFollowingTiedNote().get().getPitch());

		assertEquals(Pitch.of(Pitch.Base.E, 0, 4), thirdNote.getPitch());
		assertFalse(thirdNote.isTiedToFollowing());
		assertTrue(thirdNote.isTiedFromPrevious());
	}

	@Test
	public void testCopyConstructor() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final NoteBuilder copy = new NoteBuilder(builder);
		assertNotSame(builder, copy);

		assertEquals(Durations.QUARTER, copy.getDuration());
		assertEquals(Pitch.of(Pitch.Base.C, 0, 4), copy.getPitch());

		builder.setDuration(Durations.HALF);
		assertEquals(Durations.QUARTER, copy.getDuration());

		builder.setPitch(Pitch.of(Pitch.Base.D, 0, 4));
		assertEquals(Pitch.of(Pitch.Base.C, 0, 4), copy.getPitch());
	}

	@Test
	public void testCopyConstructorArticulations() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);

		final NoteBuilder copy = new NoteBuilder(builder);

		builder.addArticulation(Articulation.FERMATA);
		assertEquals(1, copy.getArticulations().size());
		assertTrue(copy.getArticulations().contains(Articulation.STACCATO));
	}

	@Test
	public void testCopyConstructorsFollowingTiedIsCopiedAsWell() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final NoteBuilder following = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.HALF);
		builder.addTieToFollowing(following);

		final NoteBuilder copy = new NoteBuilder(builder);
		assertNotSame(builder.getFollowingTied().get(), copy.getFollowingTied().get());
		assertEquals(Durations.HALF, copy.getFollowingTied().get().getDuration());
	}

	@Test
	public void testBuildingWitMultipleNotesInSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Marking slur = Marking.of(Marking.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.begins(Marking.Type.SLUR));
		assertFalse(firstNote.ends(Marking.Type.SLUR));
		assertEquals(secondNote, firstNote.getMarkingConnection(slur).get().getFollowingNote().get());

		assertTrue(secondNote.hasMarking(Marking.Type.SLUR));
		assertFalse(secondNote.begins(Marking.Type.SLUR));
		assertFalse(secondNote.ends(Marking.Type.SLUR));
		assertEquals(thirdNote, secondNote.getMarkingConnection(slur).get().getFollowingNote().get());

		assertTrue(thirdNote.ends(Marking.Type.SLUR));
		assertTrue(thirdNote.getMarkingConnection(slur).get().getFollowingNote().isEmpty());
	}

	@Test
	public void testBuildingWithMultipleNotesWithTiesInSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Marking slur = Marking.of(Marking.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		first.addTieToFollowing(second);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.begins(Marking.Type.SLUR));
		assertFalse(firstNote.ends(Marking.Type.SLUR));
		assertEquals(1, firstNote.getMarkings().size());
		assertEquals(secondNote, firstNote.getMarkingConnection(slur).get().getFollowingNote().get());

		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());

		assertTrue(secondNote.hasMarking(Marking.Type.SLUR));
		assertEquals(1, secondNote.getMarkings().size());
		assertFalse(secondNote.begins(Marking.Type.SLUR));
		assertFalse(secondNote.ends(Marking.Type.SLUR));
		assertEquals(thirdNote, secondNote.getMarkingConnection(slur).get().getFollowingNote().get());

		assertFalse(thirdNote.isTied());
		assertEquals(1, thirdNote.getMarkings().size());
		assertTrue(thirdNote.ends(Marking.Type.SLUR));
		assertTrue(thirdNote.getMarkingConnection(slur).get().getFollowingNote().isEmpty());
	}

	@Test
	public void testBuildingWithSlurAndGlissandoAndTie() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Marking slur = Marking.of(Marking.Type.SLUR);
		final Marking glissando = Marking.of(Marking.Type.GLISSANDO);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		first.connectWith(glissando, second);
		second.connectWith(glissando, third);

		first.addTieToFollowing(second);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.begins(Marking.Type.SLUR));
		assertFalse(firstNote.ends(Marking.Type.SLUR));

		assertTrue(firstNote.begins(Marking.Type.GLISSANDO));
		assertFalse(firstNote.ends(Marking.Type.GLISSANDO));

		assertEquals(2, firstNote.getMarkings().size());
		assertEquals(secondNote, firstNote.getMarkingConnection(slur).get().getFollowingNote().get());
		assertEquals(secondNote, firstNote.getMarkingConnection(glissando).get().getFollowingNote().get());

		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());

		assertEquals(2, secondNote.getMarkings().size());
		assertTrue(secondNote.hasMarking(Marking.Type.SLUR));
		assertFalse(secondNote.begins(Marking.Type.SLUR));
		assertFalse(secondNote.ends(Marking.Type.SLUR));
		assertEquals(thirdNote, secondNote.getMarkingConnection(slur).get().getFollowingNote().get());

		assertTrue(secondNote.hasMarking(Marking.Type.GLISSANDO));
		assertFalse(secondNote.begins(Marking.Type.GLISSANDO));
		assertFalse(secondNote.ends(Marking.Type.GLISSANDO));
		assertEquals(thirdNote, secondNote.getMarkingConnection(glissando).get().getFollowingNote().get());

		assertFalse(thirdNote.isTied());
		assertEquals(2, thirdNote.getMarkings().size());

		assertTrue(thirdNote.ends(Marking.Type.SLUR));
		assertTrue(thirdNote.getMarkingConnection(slur).get().getFollowingNote().isEmpty());

		assertTrue(thirdNote.ends(Marking.Type.GLISSANDO));
		assertTrue(thirdNote.getMarkingConnection(glissando).get().getFollowingNote().isEmpty());
	}

	@Test
	public void testBuildingWithLoopedSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Marking slur = Marking.of(Marking.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		// Create a circular dependency
		third.connectWith(slur, first);

		try {
			first.build();
			fail("Trying to build notes with a circular slur dependency did not result in exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}

	@Test
	public void testBuildingWithLoopedTie() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		first.addTieToFollowing(second);
		second.addTieToFollowing(third);

		// Create a circular dependency
		third.addTieToFollowing(first);

		try {
			first.build();
			fail("Trying to build notes with a circular tie dependency did not result in exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}
}
