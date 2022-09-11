/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.access.Position;
import org.wmn4j.notation.access.PositionIterator;
import org.wmn4j.notation.access.Positional;
import org.wmn4j.notation.access.Selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
	void testToSelection() {
		final int partCount = 3;
		final int measureCount = 4;
		final Score score = Score.of(getTestAttributes(), getTestParts(partCount, measureCount));
		final Selection selection = score.toSelection();

		assertEquals(1, selection.getFirst());
		assertEquals(measureCount, selection.getLast());

		Set<Integer> iteratedParts = new HashSet<>();
		Set<Integer> iteratedMeasures = new HashSet<>();

		PositionIterator iterator = selection.partwiseIterator();
		while (iterator.hasNext()) {
			iterator.next();
			final Position position = iterator.getPositionOfPrevious();
			iteratedParts.add(position.getPartIndex());
			iteratedMeasures.add(position.getMeasureNumber());
		}

		assertTrue(iteratedParts.contains(0));
		assertTrue(iteratedParts.contains(1));
		assertTrue(iteratedParts.contains(2));

		assertTrue(iteratedMeasures.contains(1));
		assertTrue(iteratedMeasures.contains(2));
		assertTrue(iteratedMeasures.contains(3));
		assertTrue(iteratedMeasures.contains(4));
	}

	@Test
	void testSelectRange() {
		final int partCount = 3;
		final int measureCount = 4;
		final Score score = Score.of(getTestAttributes(), getTestParts(partCount, measureCount));
		final Selection selection = score.selectRange(2, 3);

		assertEquals(2, selection.getFirst());
		assertEquals(3, selection.getLast());

		Set<Integer> iteratedParts = new HashSet<>();
		Set<Integer> iteratedMeasures = new HashSet<>();

		PositionIterator iterator = selection.partwiseIterator();
		while (iterator.hasNext()) {
			iterator.next();
			final Position position = iterator.getPositionOfPrevious();
			iteratedParts.add(position.getPartIndex());
			iteratedMeasures.add(position.getMeasureNumber());
		}

		assertTrue(iteratedParts.contains(0));
		assertTrue(iteratedParts.contains(1));
		assertTrue(iteratedParts.contains(2));

		assertTrue(iteratedMeasures.contains(2));
		assertTrue(iteratedMeasures.contains(3));
	}

	@Test
	void testSelectParts() {
		final int partCount = 3;
		final int measureCount = 4;
		final Score score = Score.of(getTestAttributes(), getTestParts(partCount, measureCount));

		List<Integer> partIndices = new ArrayList<>();
		partIndices.add(0);
		partIndices.add(1);

		final Selection selection = score.selectParts(partIndices);

		assertEquals(1, selection.getFirst());
		assertEquals(4, selection.getLast());

		Set<Integer> iteratedParts = new HashSet<>();
		Set<Integer> iteratedMeasures = new HashSet<>();

		PositionIterator iterator = selection.partwiseIterator();
		while (iterator.hasNext()) {
			iterator.next();
			final Position position = iterator.getPositionOfPrevious();
			iteratedParts.add(position.getPartIndex());
			iteratedMeasures.add(position.getMeasureNumber());
		}

		assertEquals(2, iteratedParts.size());
		assertTrue(iteratedParts.contains(0));
		assertTrue(iteratedParts.contains(1));

		assertEquals(4, iteratedMeasures.size());
		assertTrue(iteratedMeasures.contains(1));
		assertTrue(iteratedMeasures.contains(2));
		assertTrue(iteratedMeasures.contains(3));
		assertTrue(iteratedMeasures.contains(4));
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
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.musicxml");

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
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.musicxml");
		assertTrue(score != null);

		final PositionIterator iterator = score.partwiseIterator();
		while (iterator.hasNext()) {
			final Durational elem = iterator.next();
			final Position position = iterator.getPositionOfPrevious();
			assertEquals(elem, score.getAt(position));
		}
	}

	@Test
	void testPartwiseEnumeration() {
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.musicxml");
		assertTrue(score != null);

		final PositionIterator iterator = score.partwiseIterator();
		for (var positional : score.enumeratePartwise()) {
			final Durational elem = positional.durational();
			final Position position = positional.position();
			assertEquals(elem, score.getAt(position));
		}
	}

	@Test
	void testDurationalStreamProvidesAllElements() {
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.musicxml");
		final var allDurationals = score.durationalStream().collect(Collectors.toList());
		assertEquals(6 + 9 + 13, allDurationals.size());

		final var allRests = score.durationalStream().filter(Durational::isRest).collect(Collectors.toList());
		assertEquals(5, allRests.size());

		final var allNotes = score.durationalStream().filter(Durational::isNote).collect(Collectors.toList());
		assertEquals(23, allNotes.size());
	}

	@Test
	void testPositionalStreamProvidesAllElements() {
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.musicxml");
		final var allPositionals = score.positionalStream().collect(Collectors.toList());
		assertEquals(6 + 9 + 13, allPositionals.size());

		final var allRests = score.positionalStream()
				.map(Positional::durational)
				.filter(Durational::isRest)
				.collect(Collectors.toList());
		assertEquals(5, allRests.size());

		final var allNotes = score.positionalStream()
				.map(Positional::durational)
				.filter(Durational::isNote)
				.collect(Collectors.toList());
		assertEquals(23, allNotes.size());
	}

	@Test
	void testGetAtPositionInChord() {
		final Score score = TestHelper.readScore("musicxml/positionInChord.musicxml");
		assertTrue(score != null);

		// Get the middle note (E) from the chord in the score.
		final Position position = new Position(0, 1, 1, 1, 1, 1);
		final Note noteInChord = (Note) score.getAt(position);
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.HALF), noteInChord);
	}

}
