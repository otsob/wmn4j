/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PointPatternTest {

	private List<NoteEventVector> getTestPoints() {
		List<NoteEventVector> points = new ArrayList<>();

		points.add(new NoteEventVector(0.33, 50, 0));
		points.add(new NoteEventVector(5, 64, 1));
		points.add(new NoteEventVector(27, 12, 0));
		points.add(new NoteEventVector(39.125, 91, 2));
		points.add(new NoteEventVector(30003, 3, 2));

		return points;
	}

	@Test
	void testPointPatternHasCorrectContents() {
		List<NoteEventVector> points = getTestPoints();
		final PointPattern pattern = new PointPattern(points);

		assertEquals(5, pattern.size());
		assertEquals(points.get(0), pattern.get(0));
		assertEquals(points.get(1), pattern.get(1));
		assertEquals(points.get(2), pattern.get(2));
		assertEquals(points.get(3), pattern.get(3));
		assertEquals(points.get(4), pattern.get(4));
	}

	@Test
	void testEqualsAndHashCode() {
		final PointPattern patternA = new PointPattern(getTestPoints());
		final PointPattern patternB = new PointPattern(getTestPoints());

		assertEquals(patternA, patternA);
		assertEquals(patternA, patternB);
		assertEquals(patternB, patternA);

		assertEquals(patternA.hashCode(), patternB.hashCode());

		List<NoteEventVector> points = getTestPoints();
		points.add(1, new NoteEventVector(5, 64, 1));

		final PointPattern patternC = new PointPattern(points);
		assertNotEquals(patternA, patternC);
	}

	@Test
	void testVectorizedRepresentation() {
		List<NoteEventVector> points = getTestPoints();
		final PointPattern pattern = new PointPattern(points);
		final PointPattern vectorizedPattern = pattern.vectorized();

		assertEquals(4, vectorizedPattern.size());

		assertEquals(points.get(1).subtract(points.get(0)), vectorizedPattern.get(0));
		assertEquals(points.get(2).subtract(points.get(1)), vectorizedPattern.get(1));
		assertEquals(points.get(3).subtract(points.get(2)), vectorizedPattern.get(2));
		assertEquals(points.get(4).subtract(points.get(3)), vectorizedPattern.get(3));

		final PointPattern singletonPattern = new PointPattern(
				Collections.singletonList(new NoteEventVector(0.33, 50, 0)));

		assertEquals(0, singletonPattern.vectorized().size());
	}
}
