/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.builders.PartBuilder;
import org.wmn4j.notation.iterators.PartWiseScoreIterator;
import org.wmn4j.notation.iterators.ScoreIterator;
import org.wmn4j.notation.iterators.ScorePosition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class ScoreTest {

	public static final String SCORE_NAME = "TestScore";
	public static final String SUBTITLE = "Score subtitle";
	public static final String COMPOSER_NAME = "TestComposer";
	public static final String MOVEMENT_NAME = "TestMovement";
	public static final String ARRANGER = "Test Arranger";

	public static Map<Score.Attribute, String> getTestAttributes() {
		final Map<Score.Attribute, String> attributes = new HashMap<>();
		attributes.put(Score.Attribute.TITLE, SCORE_NAME);
		attributes.put(Score.Attribute.MOVEMENT_TITLE, MOVEMENT_NAME);
		attributes.put(Score.Attribute.SUBTITLE, SUBTITLE);
		attributes.put(Score.Attribute.COMPOSER, COMPOSER_NAME);
		attributes.put(Score.Attribute.ARRANGER, ARRANGER);
		return attributes;
	}

	public static List<Part> getTestParts(int partCount, int measureCount) {
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
	public void testHasAttribute() {
		final Map<Score.Attribute, String> attributes = new HashMap<>();
		attributes.put(Score.Attribute.TITLE, SCORE_NAME);

		final Score score = Score.of(attributes, getTestParts(1, 1));
		assertTrue(score.hasAttribute(Score.Attribute.TITLE));
		assertFalse(score.hasAttribute(Score.Attribute.COMPOSER));
	}

	@Test
	public void testGetAttribute() {
		final Score score = Score.of(getTestAttributes(), getTestParts(5, 5));
		assertEquals(SCORE_NAME, score.getAttribute(Score.Attribute.TITLE));
		assertEquals(SUBTITLE, score.getAttribute(Score.Attribute.SUBTITLE));
		assertEquals(COMPOSER_NAME, score.getAttribute(Score.Attribute.COMPOSER));
		assertEquals(ARRANGER, score.getAttribute(Score.Attribute.ARRANGER));
		assertEquals(MOVEMENT_NAME, score.getAttribute(Score.Attribute.MOVEMENT_TITLE));
	}

	@Test
	public void testImmutability() {
		final Map<Score.Attribute, String> attributes = getTestAttributes();
		final List<Part> parts = getTestParts(5, 5);

		final Score score = Score.of(attributes, parts);
		assertEquals(5, score.getPartCount(), "Number of parts was incorrect before trying to modify.");
		parts.add(parts.get(0));
		assertEquals(5, score.getPartCount(), "Adding part to the list used for creating score changed score.");

		assertEquals(SCORE_NAME, score.getTitle(), "Score title was incorrect before trying to modify");
		attributes.put(Score.Attribute.TITLE, "ModifiedName");
		assertEquals(SCORE_NAME, score.getTitle(), "Score title was changed by modifying map used for creating score");

		final List<Part> scoreParts = score.getParts();
		try {
			scoreParts.add(parts.get(0));
		} catch (final Exception e) {
			/* Do nothing */
		}
		assertEquals(5, score.getPartCount(), "Number of parts changed in score");
	}

	@Test
	public void testIterator() {
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
	public void testGetAtPositionLimits() {
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.xml");

		try {
			score.getAtPosition(new ScorePosition(0, 1, 1, 5, 0));
			fail("Did not throw exception");
		} catch (final Exception e) {
			assertTrue(e instanceof NoSuchElementException, "Exception: " + e + " is of incorrect type");
		}

		// Test first note.
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER),
				score.getAtPosition(new ScorePosition(0, 1, 1, 1, 0)));

		// Test last note.
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 3), Durations.WHOLE),
				score.getAtPosition(new ScorePosition(1, 2, 3, 2, 0)));
	}

	@Test
	public void testIteratorAndGetAtPosition() {
		final Score score = TestHelper.readScore("musicxml/scoreIteratorTesting.xml");
		assertTrue(score != null);

		final ScoreIterator iterator = new PartWiseScoreIterator(score);
		while (iterator.hasNext()) {
			final Durational elem = iterator.next();
			final ScorePosition position = iterator.getPositionOfPrevious();
			assertEquals(elem, score.getAtPosition(position));
		}
	}

	@Test
	public void testGetAtPositionInChord() {
		final Score score = TestHelper.readScore("musicxml/positionInChord.xml");
		assertTrue(score != null);

		// Get the middle note (E) from the chord in the score.
		final ScorePosition position = new ScorePosition(0, 1, 1, 1, 1, 1);
		final Note noteInChord = (Note) score.getAtPosition(position);
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.HALF), noteInChord);
	}
}
