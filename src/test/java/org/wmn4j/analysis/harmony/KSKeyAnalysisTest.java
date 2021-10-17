/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.Key;
import org.wmn4j.notation.Score;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KSKeyAnalysisTest {

	@Test
	void testGivenScoreWithOnlyCThenCMajorIsReturned() {
		final Score score = TestHelper.readScore("musicxml/singleC.musicxml");
		assertEquals(Key.C_MAJOR, KSKeyAnalysis.of(score).getKey());
	}

	@Test
	void testGivenScoreWithDMinorChordThenDMinorIsReturned() {
		final Score score = TestHelper.readScore("musicxml/harmonic_analysis/key_analysis_test.musicxml");
		assertEquals(Key.D_MINOR, KSKeyAnalysis.of(score).getKey());
	}

	@Test
	void testGivenExcerptWithMoreFSharpButLongerDurationOfAThenAMajorIsReturned() {
		final Score score = TestHelper.readScore("musicxml/harmonic_analysis/expected_to_be_a_major.musicxml");
		assertEquals(Key.A_MAJOR, KSKeyAnalysis.of(score).getKey());
	}
}
