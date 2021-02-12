/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.access.PositionalIterator;
import org.wmn4j.notation.access.Selection;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SelectionImplTest {

	private final Score testScore = TestHelper.readScore("musicxml/selection_test.musicxml");

	@Test
	void givenInvalidRangeExceptionIsThrown() {
		assertThrows(IllegalArgumentException.class, () -> new SelectionImpl(testScore, -1, 2));
		assertThrows(IllegalArgumentException.class, () -> new SelectionImpl(testScore, 1, -1));
		assertThrows(IllegalArgumentException.class, () -> new SelectionImpl(testScore, 0, 1));
		assertThrows(IllegalArgumentException.class, () -> new SelectionImpl(testScore, 3, 2));
	}

	@Test
	void givenSelectionOfFullScoreStartAndEndAreCorrect() {
		final Selection fullSelection = new SelectionImpl(testScore, 1, testScore.getMeasureCount());
		assertEquals(1, fullSelection.getFirst());
		assertEquals(testScore.getMeasureCount(), fullSelection.getLast());

		final Selection lastTwoMeasures = new SelectionImpl(testScore, 5, 6);
		assertEquals(5, lastTwoMeasures.getFirst());
		assertEquals(6, lastTwoMeasures.getLast());
	}

	@Test
	void givenSelectionOfFullScoreAllDurationalsAreIterated() {
		final Selection fullSelection = new SelectionImpl(testScore, 1, testScore.getMeasureCount());
		int count = 0;
		for (Durational durational : fullSelection) {
			++count;
		}

		assertEquals(3 + 6 + 12 + 6 + 3 + 3, count);
	}

	@Test
	void givenRangeOfScoreAllDurationalsAreIterated() {
		final Selection fullSelection = new SelectionImpl(testScore, 3, 5);
		int count = 0;
		for (Durational durational : fullSelection) {
			++count;
		}

		assertEquals(12 + 6 + 3, count);
	}

	@Test
	void givenSelectionOfFullScoreAllPositionsAreIterated() {
		final Selection fullSelection = new SelectionImpl(testScore, 1, testScore.getMeasureCount());
		final PositionalIterator iterator = fullSelection.positionalIterator();
		final Set<Integer> measureNumbers = new HashSet<>();
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			measureNumbers.add(iterator.getPositionOfPrevious().getMeasureNumber());
			++count;
		}

		assertEquals(6, measureNumbers.size());
		assertTrue(measureNumbers.contains(1));
		assertTrue(measureNumbers.contains(2));
		assertTrue(measureNumbers.contains(3));
		assertTrue(measureNumbers.contains(4));
		assertTrue(measureNumbers.contains(5));
		assertTrue(measureNumbers.contains(6));

		assertEquals(3 + 6 + 12 + 6 + 3 + 3, count);
	}

	@Test
	void givenRangeOfScoreAllPositionsAreIterated() {
		final Selection fullSelection = new SelectionImpl(testScore, 3, 5);
		final PositionalIterator iterator = fullSelection.positionalIterator();
		final Set<Integer> measureNumbers = new HashSet<>();
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			measureNumbers.add(iterator.getPositionOfPrevious().getMeasureNumber());
			++count;
		}

		assertEquals(3, measureNumbers.size());
		assertTrue(measureNumbers.contains(3));
		assertTrue(measureNumbers.contains(4));
		assertTrue(measureNumbers.contains(5));

		assertEquals(12 + 6 + 3, count);
	}

	@Test
	void givenSelectionOfFullScoreSubRangeHasCorrectContent() {
		final Selection fullSelection = new SelectionImpl(testScore, 1, testScore.getMeasureCount());

		final Selection subSelection = fullSelection.subSelection(2, 3);

		assertEquals(2, subSelection.getFirst());
		assertEquals(3, subSelection.getLast());

		final PositionalIterator iterator = subSelection.positionalIterator();
		final Set<Integer> measureNumbers = new HashSet<>();
		int count = 0;
		while (iterator.hasNext()) {
			iterator.next();
			measureNumbers.add(iterator.getPositionOfPrevious().getMeasureNumber());
			++count;
		}

		assertEquals(2, measureNumbers.size());
		assertTrue(measureNumbers.contains(2));
		assertTrue(measureNumbers.contains(3));

		assertEquals(6 + 12, count);
	}
}
