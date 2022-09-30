/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.search;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.mir.PatternBuilder;
import org.wmn4j.mir.PatternPosition;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.access.Position;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointSetSearchTest {

	static final Score TEST_SCORE = TestHelper.readScore("musicxml/search/pattern_search_test.musicxml");

	@Test
	void testGivenQueryWithTwoOccurrencesThenBothAreFound() {
		final var query = new PatternBuilder()
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new RestBuilder(Durations.EIGHTH))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH))
				.build();

		final var search = PointSetSearch.of(TEST_SCORE);

		final var positions = search.findPositions(query);
		assertEquals(2, positions.size());

		assertTrue(positions.contains(new PatternPosition(Arrays.asList(
				new Position(0, 1, 1, 1, 0),
				new Position(0, 1, 1, 1, 1),
				new Position(0, 1, 1, 1, 2),
				new Position(0, 1, 1, 1, 4)))));

		assertTrue(positions.contains(new PatternPosition(Arrays.asList(
				new Position(0, 2, 2, 1, 0),
				new Position(0, 2, 2, 1, 2),
				new Position(0, 2, 2, 1, 4),
				new Position(0, 2, 2, 1, 7)))));

		final var occurrences = search.findOccurrences(query);
		assertEquals(2, occurrences.size());

		assertTrue(occurrences.contains(new PatternBuilder()
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new RestBuilder(Durations.EIGHTH))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH))
				.build()));

		assertTrue(occurrences.contains(new PatternBuilder()
				.add(new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH))
				.add(new RestBuilder(Durations.EIGHTH))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH))
				.add(new RestBuilder(Durations.EIGHTH))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH))
				.add(new RestBuilder(Durations.EIGHTH))
				.add(new RestBuilder(Durations.EIGHTH))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH))
				.build()));
	}

	@Test
	void testGivenQueryWithoutOccurrencesThenNoneAreFound() {
		final var query = new PatternBuilder()
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 5), Durations.QUARTER))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 5), Durations.QUARTER))
				.add(new RestBuilder(Durations.EIGHTH))
				.add(new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH))
				.build();

		final var search = PointSetSearch.of(TEST_SCORE);

		final var positions = search.findPositions(query);
		assertTrue(positions.isEmpty());

		final var occurrences = search.findOccurrences(query);
		assertTrue(occurrences.isEmpty());
	}

}
