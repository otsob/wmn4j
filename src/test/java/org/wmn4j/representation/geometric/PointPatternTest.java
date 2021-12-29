/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PointPatternTest {

	private List<Point2D> getTestPoints() {
		List<Point2D> points = new ArrayList<>();

		points.add(new Point2D(0.33, 50));
		points.add(new Point2D(5, 64));
		points.add(new Point2D(27, 12));
		points.add(new Point2D(39.125, 91));
		points.add(new Point2D(30003, 3));

		return points;
	}

	@Test
	void testPointPatternHasCorrectContents() {
		List<Point2D> points = getTestPoints();
		final PointPattern<Point2D> pattern = new PointPattern<>(points);

		assertEquals(5, pattern.size());
		assertEquals(points.get(0), pattern.get(0));
		assertEquals(points.get(1), pattern.get(1));
		assertEquals(points.get(2), pattern.get(2));
		assertEquals(points.get(3), pattern.get(3));
		assertEquals(points.get(4), pattern.get(4));
	}

	@Test
	void testEqualsAndHashCode() {
		final PointPattern<Point2D> patternA = new PointPattern<>(getTestPoints());
		final PointPattern<Point2D> patternB = new PointPattern<>(getTestPoints());

		assertEquals(patternA, patternA);
		assertEquals(patternA, patternB);
		assertEquals(patternB, patternA);

		assertEquals(patternA.hashCode(), patternB.hashCode());

		List<Point2D> points = getTestPoints();
		points.add(1, new Point2D(5, 64));

		final PointPattern<Point2D> patternC = new PointPattern<>(points);
		assertNotEquals(patternA, patternC);
	}

	@Test
	void testVectorizedRepresentation() {
		List<Point2D> points = getTestPoints();
		final PointPattern<Point2D> pattern = new PointPattern<>(points);
		final PointPattern<Point2D> vectorizedPattern = pattern.vectorized();

		assertEquals(4, vectorizedPattern.size());

		assertEquals(points.get(1).subtract(points.get(0)), vectorizedPattern.get(0));
		assertEquals(points.get(2).subtract(points.get(1)), vectorizedPattern.get(1));
		assertEquals(points.get(3).subtract(points.get(2)), vectorizedPattern.get(2));
		assertEquals(points.get(4).subtract(points.get(3)), vectorizedPattern.get(3));

		final PointPattern<Point2D> singletonPattern = new PointPattern<>(
				Collections.singletonList(new Point2D(0.33, 50)));

		assertEquals(0, singletonPattern.vectorized().size());
	}
}
