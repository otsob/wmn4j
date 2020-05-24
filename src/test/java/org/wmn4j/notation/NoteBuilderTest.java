/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class NoteBuilderTest {

	@Test
	void testWhenCreatingFromNoteThenCorrectValuesAreSet() {
		final Note noteWithoutArticulations = Note.of(Pitch.Base.A, -1, 2, Duration.of(1, 12));
		NoteBuilder builderWithoutArticulations = new NoteBuilder(noteWithoutArticulations);

		assertEquals(noteWithoutArticulations.getPitch(), builderWithoutArticulations.getPitch());
		assertEquals(noteWithoutArticulations.getDuration(), builderWithoutArticulations.getDuration());
		assertEquals(noteWithoutArticulations.getArticulations(), builderWithoutArticulations.getArticulations());
		assertEquals(noteWithoutArticulations, builderWithoutArticulations.build());

		final Note noteWithArticulations = Note.of(Pitch.of(Pitch.Base.A, 1, 3), Duration.of(1, 16),
				EnumSet.of(Articulation.ACCENT, Articulation.STACCATO));
		NoteBuilder builderWithArticulations = new NoteBuilder(noteWithArticulations);

		assertEquals(noteWithArticulations.getPitch(), builderWithArticulations.getPitch());
		assertEquals(noteWithArticulations.getDuration(), builderWithArticulations.getDuration());
		assertEquals(noteWithArticulations.getArticulations(), builderWithArticulations.getArticulations());
		assertEquals(noteWithArticulations, builderWithArticulations.build());
	}

	@Test
	void testBuildingBasicNote() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final Note note = builder.build();
		assertFalse(note.hasArticulations());
		assertFalse(note.hasNotations());
		assertFalse(note.isTied());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), note);
	}

	@Test
	void testBuildingNoteWithAllAttributes() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);

		builder.connectWith(Notation.of(Notation.Type.SLUR),
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
		assertTrue(note.beginsNotation(Notation.Type.SLUR));
	}

	@Test
	void testBuildingTiedNotes() {
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
	void testCopyConstructor() {
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
	void testCopyConstructorArticulations() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);

		final NoteBuilder copy = new NoteBuilder(builder);

		builder.addArticulation(Articulation.FERMATA);
		assertEquals(1, copy.getArticulations().size());
		assertTrue(copy.getArticulations().contains(Articulation.STACCATO));
	}

	@Test
	void testCopyConstructorsFollowingTiedIsCopiedAsWell() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final NoteBuilder following = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.HALF);
		builder.addTieToFollowing(following);

		final NoteBuilder copy = new NoteBuilder(builder);
		assertNotSame(builder.getFollowingTied().get(), copy.getFollowingTied().get());
		assertEquals(Durations.HALF, copy.getFollowingTied().get().getDuration());
	}

	@Test
	void testBuildingWitMultipleNotesInSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(firstNote.endsNotation(Notation.Type.SLUR));
		assertEquals(secondNote, firstNote.getConnection(slur).get().getFollowingNote().get());

		assertTrue(secondNote.hasNotation(Notation.Type.SLUR));
		assertFalse(secondNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(thirdNote, secondNote.getConnection(slur).get().getFollowingNote().get());

		assertTrue(thirdNote.endsNotation(Notation.Type.SLUR));
		assertTrue(thirdNote.getConnection(slur).get().getFollowingNote().isEmpty());
	}

	@Test
	void testBuildingWithMultipleNotesWithTiesInSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		first.addTieToFollowing(second);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(firstNote.endsNotation(Notation.Type.SLUR));
		assertEquals(1, firstNote.getNotations().size());
		assertEquals(secondNote, firstNote.getConnection(slur).get().getFollowingNote().get());

		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());

		assertTrue(secondNote.hasNotation(Notation.Type.SLUR));
		assertEquals(1, secondNote.getNotations().size());
		assertFalse(secondNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(thirdNote, secondNote.getConnection(slur).get().getFollowingNote().get());

		assertFalse(thirdNote.isTied());
		assertEquals(1, thirdNote.getNotations().size());
		assertTrue(thirdNote.endsNotation(Notation.Type.SLUR));
		assertTrue(thirdNote.getConnection(slur).get().getFollowingNote().isEmpty());
	}

	@Test
	void testBuildingWithSlurAndGlissandoAndTie() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		final Notation glissando = Notation.of(Notation.Type.GLISSANDO);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		first.connectWith(glissando, second);
		second.connectWith(glissando, third);

		first.addTieToFollowing(second);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(firstNote.endsNotation(Notation.Type.SLUR));

		assertTrue(firstNote.beginsNotation(Notation.Type.GLISSANDO));
		assertFalse(firstNote.endsNotation(Notation.Type.GLISSANDO));

		assertEquals(2, firstNote.getNotations().size());
		assertEquals(secondNote, firstNote.getConnection(slur).get().getFollowingNote().get());
		assertEquals(secondNote, firstNote.getConnection(glissando).get().getFollowingNote().get());

		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());

		assertEquals(2, secondNote.getNotations().size());
		assertTrue(secondNote.hasNotation(Notation.Type.SLUR));
		assertFalse(secondNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(thirdNote, secondNote.getConnection(slur).get().getFollowingNote().get());

		assertTrue(secondNote.hasNotation(Notation.Type.GLISSANDO));
		assertFalse(secondNote.beginsNotation(Notation.Type.GLISSANDO));
		assertFalse(secondNote.endsNotation(Notation.Type.GLISSANDO));
		assertEquals(thirdNote, secondNote.getConnection(glissando).get().getFollowingNote().get());

		assertFalse(thirdNote.isTied());
		assertEquals(2, thirdNote.getNotations().size());

		assertTrue(thirdNote.endsNotation(Notation.Type.SLUR));
		assertTrue(thirdNote.getConnection(slur).get().getFollowingNote().isEmpty());

		assertTrue(thirdNote.endsNotation(Notation.Type.GLISSANDO));
		assertTrue(thirdNote.getConnection(glissando).get().getFollowingNote().isEmpty());
	}

	@Test
	void testBuildingWithLoopedSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
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
	void testBuildingWithLoopedTie() {
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
