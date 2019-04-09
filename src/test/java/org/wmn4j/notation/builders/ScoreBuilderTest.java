/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import org.junit.Test;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Score;
import org.wmn4j.notation.elements.ScoreTest;
import org.wmn4j.notation.elements.TimeSignatures;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ScoreBuilderTest {

	public ScoreBuilderTest() {
	}

	public static List<PartBuilder> getTestPartBuilders(int partCount, int measureCount) {
		final List<PartBuilder> partBuilders = new ArrayList<>();

		for (int p = 1; p <= partCount; ++p) {
			final PartBuilder partBuilder = new PartBuilder("Part" + p);
			for (int m = 1; m <= measureCount; ++m) {
				partBuilder.add(TestHelper.getTestMeasureBuilder(m));
			}

			partBuilders.add(partBuilder);
		}

		return partBuilders;
	}

	@Test
	public void testBuildingScore() {
		final ScoreBuilder builder = new ScoreBuilder();
		final Map<Score.Attribute, String> attributes = ScoreTest.getTestAttributes();
		final List<PartBuilder> partBuilders = getTestPartBuilders(5, 5);

		for (Score.Attribute attr : attributes.keySet()) {
			builder.setAttribute(attr, attributes.get(attr));
		}

		for (PartBuilder partBuilder : partBuilders) {
			builder.addPart(partBuilder);
		}

		final Score score = builder.build();
		assertEquals(ScoreTest.SCORE_NAME, score.getTitle());
		assertEquals(ScoreTest.COMPOSER_NAME, score.getAttribute(Score.Attribute.COMPOSER));

		assertEquals(5, score.getPartCount());
	}

	@Test
	public void testPartsAreOfEqualLengthWhenBuilt() {
		final List<PartBuilder> partBuilders = getTestPartBuilders(3, 1);
		PartBuilder first = partBuilders.get(0);
		first.add(new MeasureBuilder(2));

		MeasureBuilder thirdMeasureBuilder = new MeasureBuilder(3);
		thirdMeasureBuilder.setTimeSignature(TimeSignatures.THREE_FOUR);
		first.add(thirdMeasureBuilder);

		MeasureBuilder fourthMeasureBuilder = new MeasureBuilder(4);
		fourthMeasureBuilder.setTimeSignature(TimeSignatures.FOUR_FOUR);
		first.add(fourthMeasureBuilder);

		PartBuilder multiStaffPartBuilder = partBuilders.get(1);
		multiStaffPartBuilder.addToStaff(2, new MeasureBuilder(1));

		ScoreBuilder scoreBuilder = new ScoreBuilder();
		scoreBuilder.setAttribute(Score.Attribute.TITLE, "test score");

		scoreBuilder.addPart(first);
		scoreBuilder.addPart(multiStaffPartBuilder);
		scoreBuilder.addPart(partBuilders.get(2));

		Score score = scoreBuilder.build();

		Part firstPart = score.getPart(0);
		assertEquals(4, firstPart.getMeasureCount());
		assertEquals(1, firstPart.getStaffCount());
		assertEquals(TimeSignatures.FOUR_FOUR, firstPart.getMeasure(1, 1).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, firstPart.getMeasure(1, 2).getTimeSignature());
		assertEquals(TimeSignatures.THREE_FOUR, firstPart.getMeasure(1, 3).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, firstPart.getMeasure(1, 4).getTimeSignature());

		Part secondPart = score.getPart(1);
		assertEquals(4, secondPart.getMeasureCount());
		assertEquals(2, secondPart.getStaffCount());
		assertEquals(TimeSignatures.FOUR_FOUR, secondPart.getMeasure(1, 1).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, secondPart.getMeasure(1, 2).getTimeSignature());
		assertEquals(TimeSignatures.THREE_FOUR, secondPart.getMeasure(1, 3).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, secondPart.getMeasure(1, 4).getTimeSignature());

		assertEquals(TimeSignatures.FOUR_FOUR, secondPart.getMeasure(2, 1).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, secondPart.getMeasure(2, 2).getTimeSignature());
		assertEquals(TimeSignatures.THREE_FOUR, secondPart.getMeasure(2, 3).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, secondPart.getMeasure(2, 4).getTimeSignature());

		Part thirdPart = score.getPart(2);
		assertEquals(4, thirdPart.getMeasureCount());
		assertEquals(1, thirdPart.getStaffCount());
		assertEquals(TimeSignatures.FOUR_FOUR, thirdPart.getMeasure(1, 1).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, thirdPart.getMeasure(1, 2).getTimeSignature());
		assertEquals(TimeSignatures.THREE_FOUR, thirdPart.getMeasure(1, 3).getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, thirdPart.getMeasure(1, 4).getTimeSignature());
	}
}
