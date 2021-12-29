/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Point2DTest {

	@Test
	void testDimensionality() {
		final Point2D point = new Point2D(0, 0).add(new Point2D(1, 1));
		assertEquals(2, point.getDimensionality());
	}

	@Test
	void testSimpleAddition() {
		final Point2D vector1 = new Point2D(0, 0).add(new Point2D(1, 1));
		assertEquals(new Point2D(1, 1), vector1);

		final Point2D vector2 = new Point2D(-1, -2);
		final Point2D expectedSum = new Point2D(0, -1);
		assertEquals(expectedSum, vector2.add(new Point2D(1, 1)));
	}

	@Test
	void testAdditionWithFractionalOffsets() {
		final Point2D tripletOffset = new Point2D(1.0 / 3.0, 0);
		final Point2D quarterOffset = new Point2D(1.0, 0);
		final Point2D halfOffset = tripletOffset.add(tripletOffset).add(quarterOffset).add(tripletOffset);
		assertEquals(new Point2D(2.0, 0), halfOffset);

		final Point2D tripletsThatShouldSumToFour = tripletOffset.add(tripletOffset).add(tripletOffset)
				.add(tripletOffset).add(tripletOffset).add(tripletOffset).add(tripletOffset).add(tripletOffset)
				.add(tripletOffset).add(tripletOffset).add(tripletOffset).add(tripletOffset);

		assertEquals(new Point2D(4.0, 0), tripletsThatShouldSumToFour);

		final Point2D quintupletOffset = new Point2D(1.0 / 5.0, 0);
		final Point2D tripletsAndQuintupletsThatShouldSumToTwo = quintupletOffset.add(quintupletOffset)
				.add(quintupletOffset).add(quintupletOffset).add(quintupletOffset).add(tripletOffset)
				.add(tripletOffset).add(tripletOffset);

		assertEquals(new Point2D(2.0, 0), tripletsAndQuintupletsThatShouldSumToTwo);

		final Point2D allOnes = new Point2D(1, 1);
		final Point2D offsetWithOneFifth = allOnes.add(allOnes).add(new Point2D(1.0 / 5.0, 1));

		assertEquals(new Point2D(2.2, 3), offsetWithOneFifth);

		final Point2D quarterAndTwoTripletsOffsetWithLargeOffset = tripletOffset.add(tripletOffset)
				.add(quarterOffset)
				.add(new Point2D(100000, 0));

		assertEquals(new Point2D(100001.666666666666666, 0), quarterAndTwoTripletsOffsetWithLargeOffset);
	}

	@Test
	void testSimpleSubtraction() {
		final Point2D vector1 = new Point2D(1, 1).subtract(new Point2D(1, 1));
		assertEquals(new Point2D(0, 0), vector1);

		final Point2D vector2 = new Point2D(1, 2);
		final Point2D expectedDifference = new Point2D(0, -1);
		assertEquals(expectedDifference, new Point2D(1, 1).subtract(vector2));
	}

	@Test
	void testSubtractionWithFractionalOffsets() {
		final Point2D tripletOffset = new Point2D(1.0 / 3.0, 0);
		final Point2D expectedToBeZero = new Point2D(1.0, 0).subtract(tripletOffset)
				.subtract(tripletOffset).subtract(tripletOffset);
		assertEquals(new Point2D(0, 0), expectedToBeZero);

		final Point2D allOnes = new Point2D(1, 0);
		final Point2D quarterMinusQuintuplet = allOnes
				.subtract(new Point2D(1.0 / 5.0, 0));

		assertEquals(new Point2D(0.8, 0), quarterMinusQuintuplet);

		final Point2D quintupletOffset = new Point2D(0.2, 0);

		final Point2D dottedQuarterOffset = new Point2D(3.5, 0).subtract(tripletOffset)
				.subtract(tripletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(quintupletOffset)
				.subtract(tripletOffset);

		assertEquals(new Point2D(1.5, 0), dottedQuarterOffset);
	}

	@Test
	void testCompareTo() {
		final Point2D vec1 = new Point2D(0, 0);
		final Point2D vec2 = new Point2D(0, 1);

		assertTrue(vec1.compareTo(vec1) == 0);
		assertTrue(vec1.compareTo(vec2) < 0);
		assertTrue(vec2.compareTo(vec1) > 0);
	}

	@Test
	void testEquals() {
		assertEquals(new Point2D(0, 0), new Point2D(0, 0));
		assertEquals(new Point2D(0, 1), new Point2D(0, 1));
		assertEquals(new Point2D(0, 0), new Point2D(0, 0));

		assertNotEquals(new Point2D(0, 1), new Point2D(1, 1));
		assertNotEquals(new Point2D(1, 0), new Point2D(1, 1));

		assertNotEquals(new Point2D(0.00000001, 1), new Point2D(0, 1));
		assertNotEquals(new Point2D(100000.000003, 1), new Point2D(100000.000004, 1));
	}

	@Test
	void testHashCode() {
		final Point2D oneThirdOffset = new Point2D(1.0 / 3.0, 0);
		final Point2D oneOffset = new Point2D(1.0, 0);
		final Point2D sumOfThirdsAndOne = oneThirdOffset.add(oneThirdOffset).add(oneOffset).add(oneThirdOffset);
		assertEquals(new Point2D(2.0, 0).hashCode(), sumOfThirdsAndOne.hashCode());

		final Point2D shouldSumToFour = oneThirdOffset.add(oneThirdOffset).add(oneThirdOffset)
				.add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset)
				.add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset).add(oneThirdOffset);

		assertEquals(new Point2D(4.0, 0).hashCode(), shouldSumToFour.hashCode());
	}
}
