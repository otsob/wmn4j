/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NotationTest {

	private final Note testNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 4), Durations.EIGHTH);

	@Test
	void testBeginningOf() {
		final Notation notation = Notation.of(Notation.Type.GLISSANDO);
		Notation.Connection beginningArticulation = Notation.Connection.beginningOf(notation,
				testNote);
		assertEquals(notation, beginningArticulation.getNotation());
		assertTrue(beginningArticulation.isBeginning());
		assertFalse(beginningArticulation.isEnd());
		assertEquals(Notation.Type.GLISSANDO, beginningArticulation.getType());
		Optional<Note> followingNote = beginningArticulation.getFollowingNote();
		assertTrue(followingNote.isPresent());
		assertEquals(testNote, followingNote.get());
	}

	@Test
	void testOf() {
		final Notation notation = Notation.of(Notation.Type.GLISSANDO);
		Notation.Connection middleArticulation = Notation.Connection.of(notation,
				testNote);
		assertEquals(notation, middleArticulation.getNotation());
		assertFalse(middleArticulation.isBeginning());
		assertFalse(middleArticulation.isEnd());
		assertEquals(Notation.Type.GLISSANDO, middleArticulation.getType());
		Optional<Note> followingNote = middleArticulation.getFollowingNote();
		assertTrue(followingNote.isPresent());
		assertEquals(testNote, followingNote.get());
	}

	@Test
	void testEndOf() {
		final Notation notation = Notation.of(Notation.Type.GLISSANDO);
		Notation.Connection middleArticulation = Notation.Connection.endOf(notation);
		assertEquals(notation, middleArticulation.getNotation());
		assertFalse(middleArticulation.isBeginning());
		assertTrue(middleArticulation.isEnd());
		assertEquals(Notation.Type.GLISSANDO, middleArticulation.getType());
		Optional<Note> followingNote = middleArticulation.getFollowingNote();
		assertFalse(followingNote.isPresent());
	}

	@Test
	void testGetFollowingNotes() {
		final Notation slur = Notation.of(Notation.Type.SLUR);
		final Notation firstTie = Notation.of(Notation.Type.TIE);
		final Notation secondTie = Notation.of(Notation.Type.TIE);
		final Note third = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Notation.Connection.endOf(slur), Notation.Connection.endOf(secondTie)));
		final Note second = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(),
				List.of(Notation.Connection.of(slur, third),
						Notation.Connection.beginningOf(secondTie, third),
						Notation.Connection.endOf(firstTie)));
		final Note first = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Notation.Connection.beginningOf(slur, second),
						Notation.Connection.beginningOf(secondTie, third),
						Notation.Connection.beginningOf(firstTie, second)));

		assertEquals(second, first.getConnection(slur).get().getFollowingNote().get());
		assertEquals(third, second.getConnection(slur).get().getFollowingNote().get());

		Optional<Notation.Connection> slurBeginning = first.getConnection(slur);
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
		final Notation slur = Notation.of(Notation.Type.SLUR);
		final Note third = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Notation.Connection.endOf(slur)));
		final Note second = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Notation.Connection.of(slur, third)));
		final Note first = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), List.of(Notation.Connection.beginningOf(slur, second)));

		assertTrue(
				Notation.of(Notation.Type.SLUR).getNotesStartingFrom(first).isEmpty(),
				"Incorrectly returned notes for a different slur");

		assertEquals(3, slur.getNotesStartingFrom(first).size());
		assertEquals(2, slur.getNotesStartingFrom(second).size());
		assertEquals(1, slur.getNotesStartingFrom(third).size());

		Collection<Note> allNotesInSlur = slur.getNotesStartingFrom(first);
		assertTrue(allNotesInSlur.contains(first));
		assertTrue(allNotesInSlur.contains(first));
		assertTrue(allNotesInSlur.contains(first));
	}
}
