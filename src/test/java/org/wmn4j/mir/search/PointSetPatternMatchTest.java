/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.search;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.Score;
import org.wmn4j.representation.geometric.Point2D;
import org.wmn4j.representation.geometric.PointPattern;
import org.wmn4j.representation.geometric.PointSet;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PointSetPatternMatchTest {

	static final PointSet<Point2D> POINT_SET = readPointSet();

	private static PointSet<Point2D> readPointSet() {
		final Score score = TestHelper.readScore("musicxml/search/pattern_search_test.musicxml");
		return PointSet.from(score);
	}

	@Test
	void testGivenPatternInPointSetThenMatchesFound() {
		PointPattern<Point2D> query = new PointPattern<>(Arrays.asList(
				new Point2D(0.0, 72.0),
				new Point2D(0.25, 74.0),
				new Point2D(0.5, 72.0),
				new Point2D(0.875, 72.0)));

		final var matches = PointSetPatternMatch.findMatches(POINT_SET, query);

		assertEquals(2, matches.size());
		assertEquals(Arrays.asList(0, 1, 2, 3), matches.get(0));
		assertEquals(Arrays.asList(4, 6, 8, 9), matches.get(1));
	}

	@Test
	void testGivenPatternNotInPointSetThenNoMatchesFound() {
		PointPattern<Point2D> query = new PointPattern<>(Arrays.asList(
				new Point2D(0.0, 72.0),
				new Point2D(0.25, 74.0),
				new Point2D(0.375, 72.0)));

		final var matches = PointSetPatternMatch.findMatches(POINT_SET, query);
		assertTrue(matches.isEmpty());
	}
}
