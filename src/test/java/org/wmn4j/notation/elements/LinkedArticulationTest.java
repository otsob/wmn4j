/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.wmn4j.notation.elements.LinkedArticulation.Marking;

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
}
