/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.mir.Pattern;
import org.wmn4j.mir.PatternPosition;
import org.wmn4j.notation.access.Position;
import org.wmn4j.notation.access.PositionalIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ScoreTest {

	public static final String SCORE_NAME = "TestScore";
	private static final String SUBTITLE = "Score subtitle";
	public static final String COMPOSER_NAME = "TestComposer";
	private static final String MOVEMENT_NAME = "TestMovement";
	private static final String ARRANGER = "Test Arranger";

	public static Map<Score.Attribute, String> getTestAttributes() {
		final Map<Score.Attribute, String> attributes = new HashMap<>();
		attributes.put(Score.Attribute.TITLE, SCORE_NAME);
		attributes.put(Score.Attribute.MOVEMENT_TITLE, MOVEMENT_NAME);
		attributes.put(Score.Attribute.SUBTITLE, SUBTITLE);
		attributes.put(Score.Attribute.COMPOSER, COMPOSER_NAME);
		attributes.put(Score.Attribute.ARRANGER, ARRANGER);
		return attributes;
	}

	static List<Part> getTestParts(int partCount, int measureCount) {
		final List<Part> parts = new ArrayList<>();

		for (int p = 1; p <= partCount; ++p) {
			final PartBuilder partBuilder = new PartBuilder("Part" + p);
			for (int m = 1; m <= measureCount; ++m) {
				partBuilder.add(TestHelper.getTestMeasureBuilder(m));
			}

			parts.add(partBuilder.build());
		}

		return parts;
	}

	@Test
	void testHasAttribute() {
		final Map<Score.Attribute, String> attributes = new HashMap<>();
		attributes.put(Score.Attribute.TITLE, SCORE_NAME);

		final Score score = Score.of(attributes, getTestParts(1, 1));
		assertTrue(score.hasAttribute(Score.Attribute.TITLE));
		assertFalse(score.hasAttribute(Score.Attribute.COMPOSER));
	}

	@Test
	void testGetAttribute() {
		final Score score = Score.of(getTestAttributes(), getTestParts(5, 5));
		assertEquals(SCORE_NAME, score.getAttribute(Score.Attribute.TITLE).get());
		assertEquals(SUBTITLE, score.getAttribute(Score.Attribute.SUBTITLE).get());
		assertEquals(COMPOSER_NAME, score.getAttribute(Score.Attribute.COMPOSER).get());
		assertEquals(ARRANGER, score.getAttribute(Score.Attribute.ARRANGER).get());
		assertEquals(MOVEMENT_NAME, score.getAttribute(Score.Attribute.MOVEMENT_TITLE).get());
	}

	@Test
	void testGetMeasureCountAndPickupMeasure() {
		final Score score = Score.of(getTestAttributes(), getTestParts(5, 5));
		assertEquals(5, score.getFullMeasureCount());
		assertEquals(5, score.getFullMeasureCount());
		assertFalse(score.hasPickupMeasure());
	}

	@Test
	void testImmutability() {
		final Map<Score.Attribute, String> attributes = getTestAttributes();
		final List<Part> parts = getTestParts(5, 5);

		final Score score = Score.of(attributes, parts);
		assertEquals(5, score.getPartCount(), "Number of parts was incorrect before trying to modify.");
		parts.add(parts.get(0));
		assertEquals(5, score.getPartCount(), "Adding part to the list used for creating score changed score.");

		assertEquals(SCORE_NAME, score.getTitle().get(), "Score title was incorrect before trying to modify");
		attributes.put(Score.Attribute.TITLE, "ModifiedName");
		assertEquals(SCORE_NAME, score.getTitle().get(),
				"Score title was changed by modifying map used for creating score");
	}

	@Test
	void testIterator() {
		final int partCount = 10;
		final int measureCount = 10;
		final Score score = Score.of(getTestAttributes(), getTestParts(partCount, measureCount));

		int parts = 0;

		for (Part p : score) {
			assertEquals(measureCount, p.getMeasureCount());
			++parts;
		}

		assertEquals(partCount, parts, "Iterated through a wrong number of parts");

		final Iterator<Part> iter = score.iterator();
		iter.next();
		try {
			iter.remove();
			fail("Iterator supports removing, immutability violated");
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}
	}

	@Test
	void testGetAtPositionLimits() {
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.xml");

		try {
			score.getAt(new Position(0, 1, 1, 5, 0));
			fail("Did not throw exception");
		} catch (final Exception e) {
			assertTrue(e instanceof NoSuchElementException, "Exception: " + e + " is of incorrect type");
		}

		// Test first note.
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				score.getAt(new Position(0, 1, 1, 1, 0)));

		// Test last note.
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 3), Durations.WHOLE),
				score.getAt(new Position(1, 2, 3, 2, 0)));
	}

	@Test
	void testIteratorAndGetAtPosition() {
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.xml");
		assertTrue(score != null);

		final PositionalIterator iterator = score.partwiseIterator();
		while (iterator.hasNext()) {
			final Durational elem = iterator.next();
			final Position position = iterator.getPositionOfPrevious();
			assertEquals(elem, score.getAt(position));
		}
	}

	@Test
	void testGetAtPositionInChord() {
		final Score score = TestHelper.readScore("musicxml/positionInChord.xml");
		assertTrue(score != null);

		// Get the middle note (E) from the chord in the score.
		final Position position = new Position(0, 1, 1, 1, 1, 1);
		final Note noteInChord = (Note) score.getAt(position);
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.HALF), noteInChord);
	}

	@Test
	void testGivenSinglePartSingleVoiceContentWhenGettingWithPatternPositionCorrectPatternsAreReturned() {
		final Score score = TestHelper.readScore("musicxml/basic_pattern_position_test.xml");

		List<Position> simplePositions = new ArrayList<>();
		simplePositions.add(new Position(0, 1, 1, 0));
		simplePositions.add(new Position(0, 1, 1, 1));

		Pattern twoFirstNotes = score.getAt(new PatternPosition(simplePositions));
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

		Pattern patternWithGaps = score.getAt(new PatternPosition(positionsWithGap));
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
		final Score score = TestHelper.readScore("musicxml/basic_pattern_position_test.xml");

		List<Position> positionsWithChord = new ArrayList<>();
		positionsWithChord.add(new Position(0, 2, 1, 2));
		positionsWithChord.add(new Position(0, 2, 1, 3));

		Pattern patternWithChord = score.getAt(new PatternPosition(positionsWithChord));
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

		Pattern patternWithinChord = score.getAt(new PatternPosition(positionsWithinChord));
		assertEquals(2, patternWithinChord.size());

		final int withinChordVoiceNumber = patternWithinChord.getVoiceNumbers().get(0);

		assertEquals(Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH),
				patternWithinChord.get(withinChordVoiceNumber, 0));
		assertEquals(Chord.of(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH),
				Note.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4, Durations.EIGHTH)),
				patternWithinChord.get(withinChordVoiceNumber, 1));
	}

	@Test
	void testGivenScoreWithMultistaffPartsWithMultipleVoicesThenGetAtPatternReturnsCorrectPatterns() {
		final Score score = TestHelper.readScore("musicxml/multi_part_pattern_position_test.xml");

		List<Position> positionsAcrossParts = new ArrayList<>();
		positionsAcrossParts.add(new Position(0, 1, 1, 1, 1));
		positionsAcrossParts.add(new Position(0, 1, 2, 2, 0));

		positionsAcrossParts.add(new Position(1, 1, 1, 1, 1));
		positionsAcrossParts.add(new Position(1, 1, 3, 2, 0));

		positionsAcrossParts.add(new Position(1, 2, 4, 1, 0));

		final Pattern patternWithNotesAcrossParts = score.getAt(new PatternPosition(positionsAcrossParts));
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
		final Score score = TestHelper.readScore("musicxml/multi_part_pattern_position_test.xml");

		List<Position> positionsWithOverlappingVoices = new ArrayList<>();
		positionsWithOverlappingVoices.add(new Position(0, 1, 2, 1, 0));
		positionsWithOverlappingVoices.add(new Position(0, 1, 2, 2, 0));

		positionsWithOverlappingVoices.add(new Position(1, 1, 3, 1, 2));
		positionsWithOverlappingVoices.add(new Position(1, 1, 3, 2, 0));

		positionsWithOverlappingVoices.add(new Position(1, 2, 3, 1, 0));

		final Pattern patternFromOverlappingVoices = score.getAt(new PatternPosition(positionsWithOverlappingVoices));

		assertEquals(5, patternFromOverlappingVoices.getVoiceCount());

		assertEquals(1, patternFromOverlappingVoices.getVoiceSize(1));
		assertEquals(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.QUARTER), patternFromOverlappingVoices.get(1, 0));

		assertEquals(1, patternFromOverlappingVoices.getVoiceSize(2));
		assertEquals(Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5, Durations.HALF), patternFromOverlappingVoices.get(2, 0));

		assertEquals(4, patternFromOverlappingVoices.getVoiceSize(3));

		assertEquals(Rest.of(Durations.HALF.addDot()), patternFromOverlappingVoices.get(3, 0));
		assertEquals(Rest.of(Durations.SIXTEENTH), patternFromOverlappingVoices.get(3, 1));
		assertEquals(Rest.of(Durations.SIXTEENTH), patternFromOverlappingVoices.get(3, 2));
		assertTrue(Note.of(Pitch.Base.D, Pitch.Accidental.FLAT, 5, Durations.EIGHTH)
				.equalsInPitchAndDuration((Note) patternFromOverlappingVoices.get(3, 3)));

		assertEquals(2, patternFromOverlappingVoices.getVoiceSize(4));

		assertEquals(Rest.of(Durations.HALF.addDot()), patternFromOverlappingVoices.get(4, 0));
		assertEquals(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.HALF), patternFromOverlappingVoices.get(4, 1));

		assertEquals(2, patternFromOverlappingVoices.getVoiceSize(5));

		assertEquals(Rest.of(Durations.HALF.addDot()), patternFromOverlappingVoices.get(5, 0));
		assertTrue(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 3, Durations.QUARTER)
				.equalsInPitchAndDuration((Note) patternFromOverlappingVoices.get(5, 1)));
	}

	@Test
	void testGivenPatternPositionsOutsideScoreNoSuchElementExceptionIsThrown() {
		final Score score = TestHelper.readScore("musicxml/multi_part_pattern_position_test.xml");

		assertThrows(NoSuchElementException.class, () -> score.getAt(new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(3, 1, 2, 1, 0)))),
				"No exception thrown for pattern position with invalid part index");

		assertThrows(NoSuchElementException.class, () -> score.getAt(new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 2, 2, 1, 0)))),
				"No exception thrown for pattern position with invalid staff number");

		assertThrows(NoSuchElementException.class, () -> score.getAt(new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 7, 1, 0)))),
				"No exception thrown for pattern position with invalid measure number");

		assertThrows(NoSuchElementException.class, () -> score.getAt(new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 2, 3, 0)))),
				"No exception thrown for pattern position with invalid voice number");

		assertThrows(NoSuchElementException.class, () -> score.getAt(new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 2, 2, 2)))),
				"No exception thrown for pattern position with invalid index in voice");

		assertThrows(NoSuchElementException.class, () -> score.getAt(new PatternPosition(
						Arrays.asList(new Position(0, 1, 1, 1, 0),
								new Position(0, 1, 2, 1, 0, 0)))),
				"No exception thrown for pattern position with invalid index in chord");
	}
}
