/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.access.Position;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternPositionTest {

	@Test
	void testGivenPositionsWithinOnePartThenCorrectPatternPositionIsCreated() {
		final int partIndex = 0;
		List<Position> positions = new ArrayList<>();
		positions.add(new Position(partIndex, 1, 1, 1, 1));
		positions.add(new Position(partIndex, 1, 2, 2, 1, 0));
		positions.add(new Position(partIndex, 1, 5, 2, 1, 0));

		final PatternPosition position = new PatternPosition(positions);
		assertEquals(3, position.size());
		assertEquals(3, position.getPositions(partIndex).size());

		assertEquals(1, position.getStaffNumbers(partIndex).size());
		assertTrue(position.getStaffNumbers(partIndex).contains(1));

		assertTrue(position.contains(new Position(partIndex, 1, 1, 1, 1)));
		assertTrue(position.contains(new Position(partIndex, 1, 2, 2, 1, 0)));
		assertTrue(position.contains(new Position(partIndex, 1, 5, 2, 1, 0)));

		assertFalse(position.contains(new Position(2, 1, 5, 2, 1, 0)));
		assertFalse(position.contains(new Position(partIndex, 2, 5, 2, 1, 0)));
		assertFalse(position.contains(new Position(partIndex, 1, 4, 2, 1, 0)));
		assertFalse(position.contains(new Position(partIndex, 1, 2, 3, 1, 0)));

		assertEquals(1, position.getPartIndices().size());
		assertTrue(position.getPartIndices().contains(partIndex));

		assertEquals(3, position.getMeasureNumbers().size());
		assertTrue(position.getMeasureNumbers().contains(1));
		assertTrue(position.getMeasureNumbers().contains(2));
		assertTrue(position.getMeasureNumbers().contains(5));

		assertEquals(position.getMeasureNumbers(), position.getMeasureNumbers(partIndex));
	}

	@Test
	void testGivenPositionsAcrossMultiplePartsThenCorrectPatternPositionIsCreated() {
		List<Position> positions = new ArrayList<>();
		positions.add(new Position(1, 1, 1, 1, 1));

		positions.add(new Position(2, 1, 2, 2, 1, 0));
		positions.add(new Position(2, 2, 2, 2, 1, 0));

		positions.add(new Position(3, 1, 2, 2, 1, 0));
		positions.add(new Position(3, 1, 4, 2, 1, 0));
		positions.add(new Position(3, 1, 6, 2, 1, 0));

		final PatternPosition position = new PatternPosition(positions);
		assertEquals(6, position.size());

		assertEquals(1, position.getStaffNumbers(1).size());
		assertTrue(position.getStaffNumbers(1).contains(1));

		assertEquals(2, position.getStaffNumbers(2).size());
		assertTrue(position.getStaffNumbers(2).contains(1));
		assertTrue(position.getStaffNumbers(2).contains(2));

		assertEquals(1, position.getStaffNumbers(3).size());
		assertTrue(position.getStaffNumbers(3).contains(1));

		assertEquals(1, position.getPositions(1).size());
		assertEquals(2, position.getPositions(2).size());
		assertEquals(3, position.getPositions(3).size());

		assertTrue(position.contains(new Position(1, 1, 1, 1, 1)));
		assertTrue(position.contains(new Position(2, 1, 2, 2, 1, 0)));
		assertTrue(position.contains(new Position(2, 2, 2, 2, 1, 0)));
		assertTrue(position.contains(new Position(3, 1, 2, 2, 1, 0)));
		assertTrue(position.contains(new Position(3, 1, 4, 2, 1, 0)));
		assertTrue(position.contains(new Position(3, 1, 6, 2, 1, 0)));

		assertFalse(position.contains(new Position(4, 1, 2, 2, 1, 0)));
		assertFalse(position.contains(new Position(3, 2, 4, 2, 1, 0)));
		assertFalse(position.contains(new Position(2, 1, 3, 2, 1, 0)));

		final SortedSet<Integer> partIndices = position.getPartIndices();
		assertEquals(3, partIndices.size());

		assertTrue(partIndices.contains(1));
		assertTrue(partIndices.contains(2));
		assertTrue(partIndices.contains(3));

		assertEquals(1, partIndices.first());
		assertEquals(3, partIndices.last());

		final SortedSet<Integer> allMeasureNumbers = position.getMeasureNumbers();
		assertEquals(4, allMeasureNumbers.size());
		assertTrue(allMeasureNumbers.contains(1));
		assertTrue(allMeasureNumbers.contains(2));
		assertTrue(allMeasureNumbers.contains(4));
		assertTrue(allMeasureNumbers.contains(6));

		final SortedSet<Integer> firstPartMeasureNumbers = position.getMeasureNumbers(1);
		assertEquals(1, firstPartMeasureNumbers.size());
		assertTrue(firstPartMeasureNumbers.contains(1));

		final SortedSet<Integer> secondPartMeasureNumbers = position.getMeasureNumbers(2);
		assertEquals(1, secondPartMeasureNumbers.size());
		assertTrue(secondPartMeasureNumbers.contains(2));

		final SortedSet<Integer> thirdPartMeasureNumbers = position.getMeasureNumbers(3);
		assertEquals(3, thirdPartMeasureNumbers.size());
		assertTrue(thirdPartMeasureNumbers.contains(2));
		assertTrue(thirdPartMeasureNumbers.contains(4));
		assertTrue(thirdPartMeasureNumbers.contains(6));
	}

	@Test
	void testGivenSinglePartSingleVoiceContentWhenGettingWithPatternPositionCorrectPatternsAreReturned() {
		final Score score = TestHelper.readScore("musicxml/basic_pattern_position_test.musicxml");

		List<Position> simplePositions = new ArrayList<>();
		simplePositions.add(new Position(0, 1, 1, 0));
		simplePositions.add(new Position(0, 1, 1, 1));

		Pattern twoFirstNotes = new PatternPosition(simplePositions).getFrom(score);
		assertEquals(2, twoFirstNotes.size());
		assertEquals(1, twoFirstNotes.getVoiceCount());

		final int twoFirstVoiceNumber = twoFirstNotes.getVoiceNumbers().get(0);

		assertEquals(Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4, Durations.QUARTER),
				twoFirstNotes.get(twoFirstVoiceNumber, 0));
		assertEquals(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.QUARTER),
				twoFirstNotes.get(twoFirstVoiceNumber, 1));

		List<Position> positionsWithGap = new ArrayList<>();
		positionsWithGap.add(new Position(0, 1, 1, 0));
		positionsWithGap.add(new Position(0, 1, 1, 3));
		positionsWithGap.add(new Position(0, 3, 1, 0));

		Pattern patternWithGaps = new PatternPosition(positionsWithGap).getFrom(score);
		assertEquals(6, patternWithGaps.size());
		assertEquals(1, patternWithGaps.getVoiceCount());

		final int withGapsVoiceNumber = patternWithGaps.getVoiceNumbers().get(0);

		assertEquals(Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4, Durations.QUARTER),
				patternWithGaps.get(withGapsVoiceNumber, 0));
		assertEquals(Rest.of(Durations.QUARTER), patternWithGaps.get(withGapsVoiceNumber, 1));
		assertEquals(Rest.of(Durations.QUARTER), patternWithGaps.get(withGapsVoiceNumber, 2));
		assertEquals(Note.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4, Durations.QUARTER),
				patternWithGaps.get(withGapsVoiceNumber, 3));

		assertEquals(Rest.of(Durations.WHOLE), patternWithGaps.get(withGapsVoiceNumber, 4));

		assertEquals(Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4, Durations.WHOLE),
				patternWithGaps.get(withGapsVoiceNumber, 5));
	}

	@Test
	void testGivenSingleVoiceContentWhenGettingWithPatternPositionsWithChordsCorrectPatternsAreReturned() {
		final Score score = TestHelper.readScore("musicxml/basic_pattern_position_test.musicxml");

		List<Position> positionsWithChord = new ArrayList<>();
		positionsWithChord.add(new Position(0, 2, 1, 2));
		positionsWithChord.add(new Position(0, 2, 1, 3));

		Pattern patternWithChord = new PatternPosition(positionsWithChord).getFrom(score);
		assertEquals(2, patternWithChord.size());

		final int withChordVoiceNumber = patternWithChord.getVoiceNumbers().get(0);

		assertEquals(Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH),
				patternWithChord.get(withChordVoiceNumber, 0));
		assertEquals(Chord.of(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH),
						Note.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH),
						Note.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH)),
				patternWithChord.get(withChordVoiceNumber, 1));

		List<Position> positionsWithinChord = new ArrayList<>();
		positionsWithinChord.add(new Position(0, 2, 1, 2));
		positionsWithinChord.add(new Position(0, 1, 2, 1, 3, 0));
		positionsWithinChord.add(new Position(0, 1, 2, 1, 3, 1));

		Pattern patternWithinChord = new PatternPosition(positionsWithinChord).getFrom(score);
		assertEquals(2, patternWithinChord.size());

		final int withinChordVoiceNumber = patternWithinChord.getVoiceNumbers().get(0);

		assertEquals(Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH),
				patternWithinChord.get(withinChordVoiceNumber, 0));
		assertEquals(Chord.of(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH),
						Note.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH)),
				patternWithinChord.get(withinChordVoiceNumber, 1));
	}

	@Test
	void testGivenEqualPatternPositionsThenEqualsReturnsTrue() {
		final var posA = new PatternPosition(Arrays.asList(new Position(0, 1, 1, 1, 1)));
		final var posB = new PatternPosition(Arrays.asList(new Position(0, 1, 1, 1, 1)));
		assertEquals(posA, posA);
		assertEquals(posA, posB);
		assertEquals(posA.hashCode(), posB.hashCode());
	}

	@Test
	void testGivenInequalPatternPositionsThenEqualsReturnsFalse() {
		final var posA = new PatternPosition(Arrays.asList(new Position(0, 1, 1, 1, 1)));
		final var posB = new PatternPosition(Arrays.asList(new Position(0, 1, 1, 2, 1)));
		assertNotEquals(posA, posB);
	}

	@Test
	void testGivenScoreWithMultistaffPartsWithMultipleVoicesThenGetAtPatternReturnsCorrectPatterns() {
		final Score score = TestHelper.readScore("musicxml/multi_part_pattern_position_test.musicxml");

		List<Position> positionsAcrossParts = new ArrayList<>();
		positionsAcrossParts.add(new Position(0, 1, 1, 1, 1));
		positionsAcrossParts.add(new Position(0, 1, 2, 2, 0));

		positionsAcrossParts.add(new Position(1, 1, 1, 1, 1));
		positionsAcrossParts.add(new Position(1, 1, 3, 2, 0));

		positionsAcrossParts.add(new Position(1, 2, 4, 1, 0));

		final Pattern patternWithNotesAcrossParts = new PatternPosition(positionsAcrossParts).getFrom(score);
		assertEquals(3, patternWithNotesAcrossParts.getVoiceCount());

		assertEquals(3, patternWithNotesAcrossParts.getVoiceSize(1));

		assertEquals(Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4, Durations.QUARTER),
				patternWithNotesAcrossParts.get(1, 0));
		assertEquals(Rest.of(Durations.QUARTER), patternWithNotesAcrossParts.get(1, 1));
		assertEquals(Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5, Durations.HALF),
				patternWithNotesAcrossParts.get(1, 2));

		assertEquals(4, patternWithNotesAcrossParts.getVoiceSize(2));

		assertEquals(Note.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4, Durations.QUARTER),
				patternWithNotesAcrossParts.get(2, 0));
		assertEquals(Rest.of(Durations.QUARTER), patternWithNotesAcrossParts.get(2, 1));
		assertEquals(Rest.of(Durations.QUARTER.multiply(3)), patternWithNotesAcrossParts.get(2, 2));
		assertEquals(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.HALF),
				patternWithNotesAcrossParts.get(2, 3));

		assertEquals(4, patternWithNotesAcrossParts.getVoiceSize(3));

		assertEquals(Rest.of(Durations.HALF), patternWithNotesAcrossParts.get(3, 0));
		assertEquals(Rest.of(Durations.HALF.addDot()), patternWithNotesAcrossParts.get(3, 1));
		assertEquals(Rest.of(Durations.HALF.addDot()), patternWithNotesAcrossParts.get(3, 2));
		assertEquals(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 3, Durations.HALF),
				patternWithNotesAcrossParts.get(3, 3));
	}

	@Test
	void testGivenScoreWithMultistaffPartsAndPositionsFromOverlappingVoicesThenGetAtPatternReturnsCorrectPatterns() {
		final Score score = TestHelper.readScore("musicxml/multi_part_pattern_position_test.musicxml");

		List<Position> positionsWithOverlappingVoices = new ArrayList<>();
		positionsWithOverlappingVoices.add(new Position(0, 1, 2, 1, 0));
		positionsWithOverlappingVoices.add(new Position(0, 1, 2, 2, 0));

		positionsWithOverlappingVoices.add(new Position(1, 1, 3, 1, 2));
		positionsWithOverlappingVoices.add(new Position(1, 1, 3, 2, 0));

		positionsWithOverlappingVoices.add(new Position(1, 2, 3, 1, 0));

		final Pattern patternFromOverlappingVoices = new PatternPosition(positionsWithOverlappingVoices).getFrom(score);

		assertEquals(5, patternFromOverlappingVoices.getVoiceCount());

		assertEquals(1, patternFromOverlappingVoices.getVoiceSize(1));
		assertEquals(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.QUARTER),
				patternFromOverlappingVoices.get(1, 0));

		assertEquals(1, patternFromOverlappingVoices.getVoiceSize(2));
		assertEquals(Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5, Durations.HALF),
				patternFromOverlappingVoices.get(2, 0));

		assertEquals(4, patternFromOverlappingVoices.getVoiceSize(3));

		assertEquals(Rest.of(Durations.HALF.addDot()), patternFromOverlappingVoices.get(3, 0));
		assertEquals(Rest.of(Durations.SIXTEENTH), patternFromOverlappingVoices.get(3, 1));
		assertEquals(Rest.of(Durations.SIXTEENTH), patternFromOverlappingVoices.get(3, 2));
		assertTrue(Note.of(Pitch.Base.D, Pitch.Accidental.FLAT, 5, Durations.EIGHTH)
				.equalsInPitchAndDuration((Note) patternFromOverlappingVoices.get(3, 3)));

		assertEquals(2, patternFromOverlappingVoices.getVoiceSize(4));

		assertEquals(Rest.of(Durations.HALF.addDot()), patternFromOverlappingVoices.get(4, 0));
		assertEquals(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.HALF),
				patternFromOverlappingVoices.get(4, 1));

		assertEquals(2, patternFromOverlappingVoices.getVoiceSize(5));

		assertEquals(Rest.of(Durations.HALF.addDot()), patternFromOverlappingVoices.get(5, 0));
		assertTrue(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 3, Durations.QUARTER)
				.equalsInPitchAndDuration((Note) patternFromOverlappingVoices.get(5, 1)));
	}

	@Test
	void testGivenPatternPositionsOutsideScoreNoSuchElementExceptionIsThrown() {
		final Score score = TestHelper.readScore("musicxml/multi_part_pattern_position_test.musicxml");

		assertThrows(NoSuchElementException.class, () -> new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(3, 1, 2, 1, 0)))
						.getFrom(score),
				"No exception thrown for pattern position with invalid part index");

		assertThrows(NoSuchElementException.class, () -> new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 2, 2, 1, 0)))
						.getFrom(score),
				"No exception thrown for pattern position with invalid staff number");

		assertThrows(NoSuchElementException.class, () -> new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 7, 1, 0))).getFrom(score),
				"No exception thrown for pattern position with invalid measure number");

		assertThrows(NoSuchElementException.class, () -> new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 2, 3, 0))).getFrom(score),
				"No exception thrown for pattern position with invalid voice number");

		assertThrows(NoSuchElementException.class, () -> new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 2, 2, 2))).getFrom(score),
				"No exception thrown for pattern position with invalid index in voice");

		assertThrows(NoSuchElementException.class, () -> new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 2, 1, 0, 0))).getFrom(score),
				"No exception thrown for pattern position with invalid index in chord");
	}
}
