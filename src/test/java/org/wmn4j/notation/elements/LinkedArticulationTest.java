/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.Test;
import org.wmn4j.notation.elements.LinkedArticulation.Marking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LinkedArticulationTest {

	private final Note testNote = Note.of(Pitch.of(Pitch.Base.C, 1, 4), Durations.EIGHT);

	@Test
	public void testBeginningOf() {
		final Marking marking = Marking.of(Marking.Type.GLISSANDO);
		LinkedArticulation beginningArticulation = LinkedArticulation.beginningOf(marking,
				testNote);
		assertEquals(marking, beginningArticulation.getMarking());
		assertTrue(beginningArticulation.isBeginning());
		assertFalse(beginningArticulation.isEnd());
		assertEquals(Marking.Type.GLISSANDO, beginningArticulation.getType());
		Optional<Note> followingNote = beginningArticulation.getFollowingNote();
		assertTrue(followingNote.isPresent());
		assertEquals(testNote, followingNote.get());
	}

	@Test
	public void testOf() {
		final Marking marking = Marking.of(Marking.Type.GLISSANDO);
		LinkedArticulation middleArticulation = LinkedArticulation.of(marking,
				testNote);
		assertEquals(marking, middleArticulation.getMarking());
		assertFalse(middleArticulation.isBeginning());
		assertFalse(middleArticulation.isEnd());
		assertEquals(Marking.Type.GLISSANDO, middleArticulation.getType());
		Optional<Note> followingNote = middleArticulation.getFollowingNote();
		assertTrue(followingNote.isPresent());
		assertEquals(testNote, followingNote.get());
	}

	@Test
	public void testEndOf() {
		final Marking marking = Marking.of(Marking.Type.GLISSANDO);
		LinkedArticulation middleArticulation = LinkedArticulation.endOf(marking);
		assertEquals(marking, middleArticulation.getMarking());
		assertFalse(middleArticulation.isBeginning());
		assertTrue(middleArticulation.isEnd());
		assertEquals(Marking.Type.GLISSANDO, middleArticulation.getType());
		Optional<Note> followingNote = middleArticulation.getFollowingNote();
		assertFalse(followingNote.isPresent());
	}

	@Test
	public void testGetFollowingNotes() {
		final Marking slur = Marking.of(Marking.Type.SLUR);
		final Note third = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT,
				Collections.emptySet(), List.of(LinkedArticulation.endOf(slur)), null, false);
		final Note second = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT,
				Collections.emptySet(), List.of(LinkedArticulation.of(slur, third)), null, false);
		final Note first = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT,
				Collections.emptySet(), List.of(LinkedArticulation.beginningOf(slur, second)), null, false);

		assertEquals(second, first.getLinkedArticulation(slur).get().getFollowingNote().get());
		assertEquals(third, second.getLinkedArticulation(slur).get().getFollowingNote().get());

		Optional<LinkedArticulation> slurBeginning = first.getLinkedArticulation(slur);
		assertTrue(slurBeginning.isPresent());

		List<Note> followingNotes = new ArrayList<>();
		for (Note followingNote : slurBeginning.get().getFollowingNotes()) {
			followingNotes.add(followingNote);
		}

		assertEquals(second, followingNotes.get(0));
		assertEquals(third, followingNotes.get(1));
	}
}
