/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jnotation.iterators;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import wmn4jnotation.TestHelper;
import wmn4jnotation.iterators.PartWiseScoreIterator;
import wmn4jnotation.iterators.ScorePosition;
import wmn4jnotation.noteobjects.Durational;
import wmn4jnotation.noteobjects.Durations;
import wmn4jnotation.noteobjects.Note;
import wmn4jnotation.noteobjects.Pitch;
import wmn4jnotation.noteobjects.Score;

/**
 *
 * @author Otso Björklund
 */
public class PartWiseScoreIteratorTest {

	private Score score = null;
	private PartWiseScoreIterator iter = null;

	public PartWiseScoreIteratorTest() {
		this.score = TestHelper.readScore("musicxml/scoreIteratorTesting.xml");
	}

	@Before
	public void setUp() throws Exception {
		assertTrue("Failed to read score", score != null);
		this.iter = new PartWiseScoreIterator(this.score);
	}

	private Durational moveIterSteps(int steps) {
		Durational next = null;
		for (int i = 0; i < steps; ++i) {
			next = this.iter.next();
			System.out.println(next);
			System.out.println(this.iter.positionOfPrevious());
		}
		return next;
	}

	@Test
	public void testHasNext() {
		// There are 28 Durationals in the test score.
		for (int i = 0; i < 28; ++i) {
			assertTrue("Should have had next at " + i + "th iteration", this.iter.hasNext());
			this.iter.next();
		}

		assertFalse(this.iter.hasNext());
	}

	@Test
	public void testNext() {
		// Start at first note of top part.
		Durational next = moveIterSteps(1);
		assertTrue(next instanceof Note);
		Note n = (Note) next;
		assertEquals(Pitch.getPitch(Pitch.Base.C, 0, 4), n.getPitch());
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
		assertEquals(Pitch.getPitch(Pitch.Base.C, 0, 4), n.getPitch());
		assertEquals(Durations.QUARTER, n.getDuration());
	}

	@Test
	public void testCurrentPosition() {
		final int topPartNumber = 0;
		final int bottomPartNumber = 1;

		// Start at first note of top part.
		Durational next = moveIterSteps(1);
		ScorePosition first = new ScorePosition(topPartNumber, 1, 1, 1, 0);
		assertEquals(first, this.iter.positionOfPrevious());

		// Move to the rest in the first measure of top part.
		next = moveIterSteps(2);
		ScorePosition second = new ScorePosition(topPartNumber, 1, 1, 1, 2);
		assertEquals(second, this.iter.positionOfPrevious());

		// Move to first measure of bottom part;
		next = moveIterSteps(16);
		ScorePosition third = new ScorePosition(bottomPartNumber, 1, 1, 1, 0);
		assertEquals(third, this.iter.positionOfPrevious());

		// Move to first note of last measure of top staff of bottom part.
		next = moveIterSteps(5);
		ScorePosition fourth = new ScorePosition(bottomPartNumber, 1, 3, 1, 0);
		assertEquals(fourth, this.iter.positionOfPrevious());
	}
}
