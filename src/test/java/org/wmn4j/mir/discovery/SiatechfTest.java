/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.Score;
import org.wmn4j.representation.geometric.Point2D;
import org.wmn4j.representation.geometric.PointPattern;
import org.wmn4j.representation.geometric.PointSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SiatechfTest {

	@Test
	void testGivenSimpleRepeatedMotifsAndZeroCompressionRatioThenSiatechfReturnsAllTecs() {
		final Score score = TestHelper.readScore(
				"musicxml/pattern_discovery/monophonic_pattern_discovery_test.musicxml");
		assertNotNull(score);

		final PointSet<Point2D> pointSet = PointSet.from(score);
		var tecs = Siatechf.computeMtpTecs(pointSet, 0.0);
		final var expandedTecs = tecs.stream().map(this::expandTec).collect(Collectors.toList());

		assertEquals(5, tecs.size());
		assertTecInCollection(new Tec<>(
				new PointPattern<>(Arrays.asList(new Point2D(0.0, 60),
						new Point2D(0.125, 62),
						new Point2D(0.5, 60),
						new Point2D(0.625, 62))),
				Arrays.asList(new Point2D(0.0, 0),
						new Point2D(0.125, 2))), expandedTecs);

		assertTecInCollection(new Tec<>(
				new PointPattern<>(Arrays.asList(new Point2D(0.0, 60),
						new Point2D(0.125, 62),
						new Point2D(0.25, 64))),
				Arrays.asList(new Point2D(0.0, 0),
						new Point2D(0.5, 0))), expandedTecs);

		assertTecInCollection(new Tec<>(
				new PointPattern<>(Arrays.asList(new Point2D(0.0, 60),
						new Point2D(0.125, 62))),
				Arrays.asList(new Point2D(0.0, 0),
						new Point2D(0.125, 2),
						new Point2D(0.5, 0),
						new Point2D(0.625, 2))), expandedTecs);

		assertTecInCollection(new Tec<>(
				new PointPattern<>(Arrays.asList(new Point2D(0.0, 60),
						new Point2D(0.5, 60))),
				Arrays.asList(new Point2D(0.0, 0),
						new Point2D(0.125, 2),
						new Point2D(0.25, 4))), expandedTecs);

		assertTecInCollection(new Tec<>(
				new PointPattern<>(Arrays.asList(new Point2D(0.0, 60))),
				Arrays.asList(new Point2D(0.0, 0),
						new Point2D(0.125, 2),
						new Point2D(0.25, 4),
						new Point2D(0.5, 0),
						new Point2D(0.625, 2),
						new Point2D(0.75, 4))), expandedTecs);
	}

	private void assertTecInCollection(Tec<Point2D> tec, Collection<List<PointPattern<Point2D>>> expandedTecs) {
		final List<PointPattern<Point2D>> expandedTec = expandTec(tec);
		assertTrue(expandedTecs.stream().anyMatch(teca -> teca.equals(expandedTec)),
				"TEC " + tec + " not found.");
	}

	/*
	 * The TECs cannot be compared in the pattern and translations representation
	 * as the representations are not the same on every execution of the algorithm
	 * due to randomized hash functions.
	 * The TECs need to be compared by expanding them to all translated instances
	 * of the pattern.
	 */
	private List<PointPattern<Point2D>> expandTec(Tec<Point2D> tec) {
		List<PointPattern<Point2D>> patterns = new ArrayList<>(tec.getTranslators().size());

		for (Point2D translator : tec.getTranslators()) {

			List<Point2D> translatedPatternPoints = new ArrayList<>(tec.getPattern().size());
			for (Point2D patternPoint : tec.getPattern()) {
				translatedPatternPoints.add(patternPoint.add(translator));
			}

			patterns.add(new PointPattern<>(translatedPatternPoints));
		}

		return patterns;
	}

	@Test
	void testGivenSimpleRepeatedMotifsAndNonZeroCompressionRatioThenSiatechfReturnsCorrectTecs() {
		final Score score = TestHelper.readScore(
				"musicxml/pattern_discovery/monophonic_pattern_discovery_test.musicxml");
		assertNotNull(score);

		final PointSet<Point2D> pointSet = PointSet.from(score);
		var tecs = Siatechf.computeMtpTecs(pointSet, 6.0 / 4.0 - 1e-7);
		final var expandedTecs = tecs.stream().map(this::expandTec).collect(Collectors.toList());

		assertEquals(2, tecs.size());

		assertTecInCollection(new Tec<>(
				new PointPattern<>(Arrays.asList(new Point2D(0.0, 60),
						new Point2D(0.5, 60))),
				Arrays.asList(new Point2D(0.0, 0),
						new Point2D(0.125, 2),
						new Point2D(0.25, 4))), expandedTecs);

		assertTecInCollection(new Tec<>(
				new PointPattern<>(Arrays.asList(new Point2D(0.0, 60),
						new Point2D(0.125, 62),
						new Point2D(0.25, 64))),
				Arrays.asList(new Point2D(0.0, 0),
						new Point2D(0.5, 0))), expandedTecs);
	}
}
