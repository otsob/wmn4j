/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.access.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
		assertEquals(3, position.getSize());
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
		assertEquals(6, position.getSize());

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
}
