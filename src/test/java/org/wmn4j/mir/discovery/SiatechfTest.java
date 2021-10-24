/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.Score;

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
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/monophonic_pattern_discovery_test.musicxml");
		assertNotNull(score);

		final PointSet pointSet = new PointSet(score);
		Collection<Tec> tecs = Siatechf.computeMtpTecs(pointSet, 0.0);
		final Collection<List<PointPattern>> expandedTecs = tecs.stream().map(this::expandTec)
				.collect(Collectors.toList());

		assertEquals(5, tecs.size());
		assertTecInCollection(new Tec(
				new PointPattern(Arrays.asList(new NoteEventVector(0.0, 60, 0),
						new NoteEventVector(0.125, 62, 0),
						new NoteEventVector(0.5, 60, 0),
						new NoteEventVector(0.625, 62, 0))),
				Arrays.asList(new NoteEventVector(0.0, 0, 0),
						new NoteEventVector(0.125, 2, 0))), expandedTecs);

		assertTecInCollection(new Tec(
				new PointPattern(Arrays.asList(new NoteEventVector(0.0, 60, 0),
						new NoteEventVector(0.125, 62, 0),
						new NoteEventVector(0.25, 64, 0))),
				Arrays.asList(new NoteEventVector(0.0, 0, 0),
						new NoteEventVector(0.5, 0, 0))), expandedTecs);

		assertTecInCollection(new Tec(
				new PointPattern(Arrays.asList(new NoteEventVector(0.0, 60, 0),
						new NoteEventVector(0.125, 62, 0))),
				Arrays.asList(new NoteEventVector(0.0, 0, 0),
						new NoteEventVector(0.125, 2, 0),
						new NoteEventVector(0.5, 0, 0),
						new NoteEventVector(0.625, 2, 0))), expandedTecs);

		assertTecInCollection(new Tec(
				new PointPattern(Arrays.asList(new NoteEventVector(0.0, 60, 0),
						new NoteEventVector(0.5, 60, 0))),
				Arrays.asList(new NoteEventVector(0.0, 0, 0),
						new NoteEventVector(0.125, 2, 0),
						new NoteEventVector(0.25, 4, 0))), expandedTecs);

		assertTecInCollection(new Tec(
				new PointPattern(Arrays.asList(new NoteEventVector(0.0, 60, 0))),
				Arrays.asList(new NoteEventVector(0.0, 0, 0),
						new NoteEventVector(0.125, 2, 0),
						new NoteEventVector(0.25, 4, 0),
						new NoteEventVector(0.5, 0, 0),
						new NoteEventVector(0.625, 2, 0),
						new NoteEventVector(0.75, 4, 0))), expandedTecs);
	}

	private void assertTecInCollection(Tec tec, Collection<List<PointPattern>> expandedTecs) {
		final List<PointPattern> expandedTec = expandTec(tec);
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
	private List<PointPattern> expandTec(Tec tec) {
		List<PointPattern> patterns = new ArrayList<>(tec.getTranslators().size());

		for (NoteEventVector translator : tec.getTranslators()) {

			List<NoteEventVector> translatedPatternPoints = new ArrayList<>(tec.getPattern().size());
			for (NoteEventVector patternPoint : tec.getPattern()) {
				translatedPatternPoints.add(patternPoint.add(translator));
			}

			patterns.add(new PointPattern(translatedPatternPoints));
		}

		return patterns;
	}

	@Test
	void testGivenSimpleRepeatedMotifsAndNonZeroCompressionRatioThenSiatechfReturnsCorrectTecs() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/monophonic_pattern_discovery_test.musicxml");
		assertNotNull(score);

		final PointSet pointSet = new PointSet(score);
		Collection<Tec> tecs = Siatechf.computeMtpTecs(pointSet, 6.0 / 4.0 - 1e-7);
		final Collection<List<PointPattern>> expandedTecs = tecs.stream().map(this::expandTec)
				.collect(Collectors.toList());

		assertEquals(2, tecs.size());

		assertTecInCollection(new Tec(
				new PointPattern(Arrays.asList(new NoteEventVector(0.0, 60, 0),
						new NoteEventVector(0.5, 60, 0))),
				Arrays.asList(new NoteEventVector(0.0, 0, 0),
						new NoteEventVector(0.125, 2, 0),
						new NoteEventVector(0.25, 4, 0))), expandedTecs);

		assertTecInCollection(new Tec(
				new PointPattern(Arrays.asList(new NoteEventVector(0.0, 60, 0),
						new NoteEventVector(0.125, 62, 0),
						new NoteEventVector(0.25, 64, 0))),
				Arrays.asList(new NoteEventVector(0.0, 0, 0),
						new NoteEventVector(0.5, 0, 0))), expandedTecs);
	}
}
