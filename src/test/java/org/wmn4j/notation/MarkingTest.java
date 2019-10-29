/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Marking;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MarkingTest {

	private final Note testNote = Note.of(Pitch.of(Pitch.Base.C, 1, 4), Durations.EIGHTH);

	@Test
	void testBeginningOf() {
		final Marking marking = Marking.of(Marking.Type.GLISSANDO);
		Marking.Connection beginningArticulation = Marking.Connection.beginningOf(marking,
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
	void testOf() {
		final Marking marking = Marking.of(Marking.Type.GLISSANDO);
		Marking.Connection middleArticulation = Marking.Connection.of(marking,
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
	void testEndOf() {
		final Marking marking = Marking.of(Marking.Type.GLISSANDO);
		Marking.Connection middleArticulation = Marking.Connection.endOf(marking);
		assertEquals(marking, middleArticulation.getMarking());
		assertFalse(middleArticulation.isBeginning());
		assertTrue(middleArticulation.isEnd());
		assertEquals(Marking.Type.GLISSANDO, middleArticulation.getType());
		Optional<Note> followingNote = middleArticulation.getFollowingNote();
		assertFalse(followingNote.isPresent());
	}

	@Test
	void testGetFollowingNotes() {
		final Marking slur = Marking.of(Marking.Type.SLUR);
		final Note third = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Marking.Connection.endOf(slur)), null, false);
		final Note second = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Marking.Connection.of(slur, third)), null, false);
		final Note first = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Marking.Connection.beginningOf(slur, second)), null, false);

		assertEquals(second, first.getMarkingConnection(slur).get().getFollowingNote().get());
		assertEquals(third, second.getMarkingConnection(slur).get().getFollowingNote().get());

		Optional<Marking.Connection> slurBeginning = first.getMarkingConnection(slur);
		assertTrue(slurBeginning.isPresent());

		List<Note> followingNotes = new ArrayList<>();
		for (Note followingNote : slurBeginning.get().getFollowingNotes()) {
			followingNotes.add(followingNote);
		}

		assertEquals(second, followingNotes.get(0));
		assertEquals(third, followingNotes.get(1));
	}

	@Test
	void testGetAffectedStartingFrom() {
		final Marking slur = Marking.of(Marking.Type.SLUR);
		final Note third = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Marking.Connection.endOf(slur)), null, false);
		final Note second = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Marking.Connection.of(slur, third)), null, false);
		final Note first = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Marking.Connection.beginningOf(slur, second)), null, false);

		assertTrue(
				Marking.of(Marking.Type.SLUR).getAffectedStartingFrom(first).isEmpty(),
				"Incorrectly returned notes for a different slur");

		assertEquals(3, slur.getAffectedStartingFrom(first).size());
		assertEquals(2, slur.getAffectedStartingFrom(second).size());
		assertEquals(1, slur.getAffectedStartingFrom(third).size());

		Collection<Note> allNotesInSlur = slur.getAffectedStartingFrom(first);
		assertTrue(allNotesInSlur.contains(first));
		assertTrue(allNotesInSlur.contains(first));
		assertTrue(allNotesInSlur.contains(first));
	}
}
