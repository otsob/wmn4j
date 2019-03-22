/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.EnumSet;

import org.junit.Test;
import org.wmn4j.notation.elements.Articulation;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.LinkedArticulation;
import org.wmn4j.notation.elements.LinkedArticulation.Marking;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;

public class NoteBuilderTest {

	public NoteBuilderTest() {
	}

	@Test
	public void testBuildingBasicNote() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final Note note = builder.build();
		assertFalse(note.hasArticulations());
		assertFalse(note.hasLinkedArticulations());
		assertFalse(note.isTied());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), note);
	}

	@Test
	public void testBuildingNoteWithAllAttributes() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);
		builder.addLinkedArticulation(LinkedArticulation
				.of(Marking.of(Marking.Type.GLISSANDO), Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));

		final Note tiedNote = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);

		builder.setTiedTo(tiedNote);

		final Note expected = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER,
				EnumSet.of(Articulation.STACCATO));

		final Note note = builder.build();
		assertEquals(expected, note);
		assertTrue(note.isTied());
		assertTrue(note.isTiedToFollowing());
		assertFalse(note.isTiedFromPrevious());
		assertTrue(note.hasLinkedArticulations());
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
		assertTrue(copy.getArticulations().size() == 1);
		assertTrue(copy.getArticulations().contains(Articulation.STACCATO));
	}

	@Test
	public void testCopyConstructorLinkedArticulations() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
		final LinkedArticulation glissando = LinkedArticulation
				.of(Marking.of(Marking.Type.GLISSANDO), Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		builder.addLinkedArticulation(glissando);

		final NoteBuilder copy = new NoteBuilder(builder);

		builder.addLinkedArticulation(LinkedArticulation
				.of(Marking.of(Marking.Type.GLISSANDO), Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));
		assertTrue(copy.getLinkedArticulations().size() == 1);
		assertTrue(copy.getLinkedArticulations().contains(glissando));
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

}
