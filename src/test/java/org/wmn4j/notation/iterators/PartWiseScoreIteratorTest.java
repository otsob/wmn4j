/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.iterators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Score;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PartWiseScoreIteratorTest {

	private Score score = null;
	private PartWiseScoreIterator iter = null;

	PartWiseScoreIteratorTest() {
		this.score = TestHelper.readScore("musicxml/scoreIteratorTesting.xml");
	}

	@BeforeEach
	void setUp() throws Exception {
		assertNotNull(score);
		this.iter = new PartWiseScoreIterator(this.score);
	}

	private Durational moveIterSteps(int steps) {
		Durational next = null;
		for (int i = 0; i < steps; ++i) {
			next = this.iter.next();
		}
		return next;
	}

	@Test
	void testHasNext() {
		// There are 28 Durationals in the test score.
		for (int i = 0; i < 28; ++i) {
			assertTrue(this.iter.hasNext(), "Should have had next at " + i + "th iteration");
			this.iter.next();
		}

		assertFalse(this.iter.hasNext());
	}

	@Test
	void testNext() {
		// Start at first note of top part.
		Durational next = moveIterSteps(1);
		assertTrue(next instanceof Note);
		Note n = (Note) next;
		assertEquals(Pitch.of(Pitch.Base.C, 0, 4), n.getPitch());
		assertEquals(Durations.QUARTER, n.getDuration());

		// Move to the rest in the first measure of top part.
		next = moveIterSteps(2);
		assertTrue(next.isRest());
		assertEquals(Durations.QUARTER, next.getDuration());

		// Move to first measure of bottom part;
		next = moveIterSteps(16);
		assertTrue(next.isRest());
		assertEquals(Durations.WHOLE, next.getDuration());

		// Move to first not of last measure of top staff of bottom part.
		next = moveIterSteps(5);
		assertTrue(next instanceof Note);
		n = (Note) next;
		assertEquals(Pitch.of(Pitch.Base.C, 0, 4), n.getPitch());
		assertEquals(Durations.QUARTER, n.getDuration());
	}

	@Test
	void testCurrentPosition() {
		final int topPartNumber = 0;
		final int bottomPartNumber = 1;

		// Start at first note of top part.
		Durational next = moveIterSteps(1);
		final Position first = new Position(topPartNumber, 1, 1, 1, 0);
		assertEquals(first, this.iter.getPositionOfPrevious());

		// Move to the rest in the first measure of top part.
		next = moveIterSteps(2);
		final Position second = new Position(topPartNumber, 1, 1, 1, 2);
		assertEquals(second, this.iter.getPositionOfPrevious());

		// Move to first measure of bottom part;
		next = moveIterSteps(16);
		final Position third = new Position(bottomPartNumber, 1, 1, 1, 0);
		assertEquals(third, this.iter.getPositionOfPrevious());

		// Move to first note of last measure of top staff of bottom part.
		next = moveIterSteps(5);
		final Position fourth = new Position(bottomPartNumber, 1, 3, 1, 0);
		assertEquals(fourth, this.iter.getPositionOfPrevious());
	}
}
