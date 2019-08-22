/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoteEventVectorTest {

	@Test
	void testSimpleAddition() {
		final NoteEventVector vector1 = new NoteEventVector(0, 0, 0).add(new NoteEventVector(1, 1, 1));
		assertEquals(new NoteEventVector(1, 1, 1), vector1);

		final NoteEventVector vector2 = new NoteEventVector(-1, -2, -3);
		final NoteEventVector expectedSum = new NoteEventVector(0, -1, -2);
		assertEquals(expectedSum, vector2.add(new NoteEventVector(1, 1, 1)));
	}

	@Test
	void testAdditionWithFractionalOffsets() {
		final NoteEventVector tripletOffset = new NoteEventVector(1.0 / 3.0, 0, 0);
		final NoteEventVector quarterOffset = new NoteEventVector(1.0, 0, 0);
		final NoteEventVector halfOffset = tripletOffset.add(tripletOffset).add(quarterOffset).add(tripletOffset);
		assertEquals(new NoteEventVector(2.0, 0, 0), halfOffset);

		final NoteEventVector tripletsThatShouldSumToFour = tripletOffset.add(tripletOffset).add(tripletOffset)
				.add(tripletOffset).add(tripletOffset).add(tripletOffset).add(tripletOffset).add(tripletOffset)
				.add(tripletOffset).add(tripletOffset).add(tripletOffset).add(tripletOffset);

		assertEquals(new NoteEventVector(4.0, 0, 0), tripletsThatShouldSumToFour);

		final NoteEventVector quintupletOffset = new NoteEventVector(1.0 / 5.0, 0, 0);
		final NoteEventVector tripletsAndQuintupletsThatShouldSumToTwo = quintupletOffset.add(quintupletOffset)
				.add(quintupletOffset).add(quintupletOffset).add(quintupletOffset).add(tripletOffset)
				.add(tripletOffset).add(tripletOffset);

		assertEquals(new NoteEventVector(2.0, 0, 0), tripletsAndQuintupletsThatShouldSumToTwo);

		final NoteEventVector allOnes = new NoteEventVector(1, 1, 1);
		final NoteEventVector offsetWithOneFifth = allOnes.add(allOnes).add(new NoteEventVector(1.0 / 5.0, 1, 1));

		assertEquals(new NoteEventVector(2.2, 3, 3), offsetWithOneFifth);

		final NoteEventVector quarterAndTwoTripletsOffsetWithLargeOffset = tripletOffset.add(tripletOffset)
				.add(quarterOffset)
				.add(new NoteEventVector(100000, 0, 0));

		assertEquals(new NoteEventVector(100001.666666666666666, 0, 0), quarterAndTwoTripletsOffsetWithLargeOffset);
	}

	@Test
	void testSimpleSubtraction() {
		final NoteEventVector vector1 = new NoteEventVector(1, 1, 1).subtract(new NoteEventVector(1, 1, 1));
		assertEquals(new NoteEventVector(0, 0, 0), vector1);

		final NoteEventVector vector2 = new NoteEventVector(1, 2, 3);
		final NoteEventVector expectedDifference = new NoteEventVector(0, -1, -2);
		assertEquals(expectedDifference, new NoteEventVector(1, 1, 1).subtract(vector2));
	}

	@Test
	void testSubtractionWithFractionalOffsets() {
		final NoteEventVector tripletOffset = new NoteEventVector(1.0 / 3.0, 0, 0);
		final NoteEventVector expectedToBeZero = new NoteEventVector(1.0, 0, 0).subtract(tripletOffset)
				.subtract(tripletOffset).subtract(tripletOffset);
		assertEquals(new NoteEventVector(0, 0, 0), expectedToBeZero);

		final NoteEventVector allOnes = new NoteEventVector(1, 0, 0);
		final NoteEventVector quarterMinusQuintuplet = allOnes
				.subtract(new NoteEventVector(1.0 / 5.0, 0, 0));

		assertEquals(new NoteEventVector(0.8, 0, 0), quarterMinusQuintuplet);

		final NoteEventVector quintupletOffset = new NoteEventVector(0.2, 0, 0);

		final NoteEventVector dottedQuarterOffset = new NoteEventVector(3.5, 0, 0).subtract(tripletOffset)
				.subtract(tripletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(tripletOffset);

		assertEquals(new NoteEventVector(1.5, 0, 0), dottedQuarterOffset);
	}

	@Test
	void testCompareTo() {
		final NoteEventVector vec1 = new NoteEventVector(0, 0, 0);
		final NoteEventVector vec2 = new NoteEventVector(0, 1, 2);

		assertTrue(vec1.compareTo(vec1) == 0);
		assertTrue(vec1.compareTo(vec2) < 0);
		assertTrue(vec2.compareTo(vec1) > 0);
	}

	@Test
	void testEquals() {
		assertEquals(new NoteEventVector(0, 0, 0), new NoteEventVector(0, 0, 0));
		assertEquals(new NoteEventVector(0, 1, 0), new NoteEventVector(0, 1, 0));
		assertEquals(new NoteEventVector(0, 0, 1), new NoteEventVector(0, 0, 1));

		assertNotEquals(new NoteEventVector(0, 1, 1), new NoteEventVector(1, 1, 1));
		assertNotEquals(new NoteEventVector(1, 0, 1), new NoteEventVector(1, 1, 1));
		assertNotEquals(new NoteEventVector(1, 1, 0), new NoteEventVector(1, 1, 1));

		assertNotEquals(new NoteEventVector(0.00000001, 1, 1), new NoteEventVector(0, 1, 1));
		assertNotEquals(new NoteEventVector(100000.000003, 1, 1), new NoteEventVector(100000.000004, 1, 1));
	}

	@Test
	void testHashCode() {
		final NoteEventVector oneThirdOffset = new NoteEventVector(1.0 / 3.0, 0, 0);
		final NoteEventVector oneOffset = new NoteEventVector(1.0, 0, 0);
		final NoteEventVector sumOfThirdsAndOne = oneThirdOffset.add(oneThirdOffset).add(oneOffset).add(oneThirdOffset);
		assertEquals(new NoteEventVector(2.0, 0, 0).hashCode(), sumOfThirdsAndOne.hashCode());

		final NoteEventVector shouldSumToFour = oneThirdOffset.add(oneThirdOffset).add(oneThirdOffset)
				.add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset)
				.add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset);

		assertEquals(new NoteEventVector(4.0, 0, 0).hashCode(), shouldSumToFour.hashCode());
	}
}
