/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;
import org.wmn4j.mir.Pattern;
import org.wmn4j.mir.PatternPosition;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.Score;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GeometricPatternDiscoveryTest {

	@Test
	void testGivenRepeatedMotifsAndZeroCompressionRatioThenSiatechfReturnsCorrectTecs() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/monophonic_pattern_discovery_test.xml");
		assertNotNull(score);

		final PatternDiscovery patternDiscovery = GeometricPatternDiscovery.withSiatechf(score, 0.0);

		final Collection<Collection<Pattern>> patterns = patternDiscovery.getPatterns();
		final Collection<Collection<PatternPosition>> positions = patternDiscovery.getPatternPositions();

		assertEquals(5, patterns.size());
		assertEquals(patterns.size(), positions.size());

		assertExpectedPatternCollectionFoundInPatterns(
				Arrays.asList(
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						))
				), patterns);

		assertExpectedPatternCollectionFoundInPatterns(
				Arrays.asList(
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						))
				), patterns);

		assertExpectedPatternCollectionFoundInPatterns(
				Arrays.asList(
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						))
				), patterns);

		assertExpectedPatternCollectionFoundInPatterns(
				Arrays.asList(
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						))
				), patterns);

		assertExpectedPatternCollectionFoundInPatterns(
				Arrays.asList(
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						))
				), patterns);
	}

	private void assertExpectedPatternCollectionFoundInPatterns(Collection<Pattern> expected,
			Collection<Collection<Pattern>> allPatterns) {

		boolean found = false;

		for (Collection<Pattern> patterns : allPatterns) {
			if (patterns.size() == expected.size()) {
				Iterator<Pattern> patternsIterator = patterns.iterator();
				Iterator<Pattern> expectedIterator = expected.iterator();

				boolean patternCollectionsMatch = true;

				while (patternsIterator.hasNext() && expectedIterator.hasNext()) {
					if (!patternsIterator.next().equals(expectedIterator.next())) {
						patternCollectionsMatch = false;
						break;
					}
				}

				if (patternCollectionsMatch) {
					found = true;
					break;
				}
			}
		}

		assertTrue(found, expected + " not found");
	}

	@Test
	void testGivenRepeatedMotifsAndSmallCompressionRatioThenSiatechfReturnsCorrectTecs() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/monophonic_pattern_discovery_test.xml");
		assertNotNull(score);

		final PatternDiscovery patternDiscovery = GeometricPatternDiscovery.withSiatechf(score, 6.0 / 4.0 - 1e-7);

		final Collection<Collection<Pattern>> patterns = patternDiscovery.getPatterns();
		final Collection<Collection<PatternPosition>> positions = patternDiscovery.getPatternPositions();

		assertEquals(2, patterns.size());
		assertEquals(patterns.size(), positions.size());

		assertExpectedPatternCollectionFoundInPatterns(
				Arrays.asList(
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						))
				), patterns);

		assertExpectedPatternCollectionFoundInPatterns(
				Arrays.asList(
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						)),
						Pattern.of(Arrays.asList(
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Rest.of(Durations.EIGHTH),
								Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH)
						))
				), patterns);
	}

}
