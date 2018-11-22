/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jnotation.builders;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import wmn4jnotation.TestHelper;
import wmn4jnotation.builders.PartBuilder;
import wmn4jnotation.builders.ScoreBuilder;
import wmn4jnotation.noteobjects.Score;
import wmn4jnotation.noteobjects.ScoreTest;

/**
 *
 * @author Otso Björklund
 */
public class ScoreBuilderTest {

	public ScoreBuilderTest() {
	}

	public static List<PartBuilder> getTestPartBuilders(int partCount, int measureCount) {
		List<PartBuilder> partBuilders = new ArrayList<>();

		for (int p = 1; p <= partCount; ++p) {
			PartBuilder partBuilder = new PartBuilder("Part" + p);
			for (int m = 1; m <= measureCount; ++m) {
				partBuilder.add(TestHelper.getTestMeasureBuilder(m));
			}

			partBuilders.add(partBuilder);
		}

		return partBuilders;
	}

	@Test
	public void testBuildingScore() {
		ScoreBuilder builder = new ScoreBuilder();
		Map<Score.Attribute, String> attributes = ScoreTest.getTestAttributes();
		List<PartBuilder> partBuilders = getTestPartBuilders(5, 5);

		for (Score.Attribute attr : attributes.keySet())
			builder.setAttribute(attr, attributes.get(attr));

		for (PartBuilder partBuilder : partBuilders)
			builder.addPart(partBuilder);

		Score score = builder.build();
		assertEquals(ScoreTest.SCORE_NAME, score.getName());
		assertEquals(ScoreTest.COMPOSER_NAME, score.getAttribute(Score.Attribute.COMPOSER));

		assertEquals(5, score.getPartCount());
	}
}
