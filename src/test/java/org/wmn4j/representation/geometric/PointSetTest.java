/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.TimeSignatures;
import org.wmn4j.notation.access.Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PointSetTest {

	/*
	 * Returns the expected points in the point set representation
	 * of "musicxml/pattern_discovery/basic_point_set_test.musicxml"
	 */
	private List<Point2D> getExpectedVectorsForBasicTest() {
		List<Point2D> vectors = new ArrayList<>();
		double offset = 0.0;

		vectors.add(new Point2D(offset, 60));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new Point2D(offset, 62));
		offset += Durations.EIGHTH.toDouble();

		vectors.add(new Point2D(offset, 64));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new Point2D(offset, 65));
		offset += Durations.SIXTEENTH.toDouble();

		vectors.add(new Point2D(offset, 65));
		offset += Durations.SIXTEENTH.toDouble();

		vectors.add(new Point2D(offset, 65));
		offset += Durations.EIGHTH.divide(3).toDouble();

		vectors.add(new Point2D(offset, 65));
		offset += Durations.EIGHTH.divide(3).toDouble();

		vectors.add(new Point2D(offset, 65));

		// Second measure
		offset = TimeSignatures.FOUR_FOUR.getTotalDuration().toDouble();

		vectors.add(new Point2D(offset, 60));
		vectors.add(new Point2D(offset, 64));
		vectors.add(new Point2D(offset, 67));
		vectors.add(new Point2D(offset, 70));

		offset += Durations.HALF.toDouble();
		vectors.add(new Point2D(offset, 60));

		return vectors;
	}

	@Test
	void testGivenScoreWithSingleStaffPointSetHasCorrectPoints() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/basic_point_set_test.musicxml");
		assertNotNull(score);

		final PointSet<Point2D> pointSet = PointSet.fromScore(score);
		assertEquals(13, pointSet.size());

		final List<Point2D> expectedVectors = getExpectedVectorsForBasicTest();

		assertEquals(expectedVectors.get(0), pointSet.get(0));
		assertEquals(expectedVectors.get(1), pointSet.get(1));
		assertEquals(expectedVectors.get(2), pointSet.get(2));
		assertEquals(expectedVectors.get(3), pointSet.get(3));
		assertEquals(expectedVectors.get(4), pointSet.get(4));
		assertEquals(expectedVectors.get(5), pointSet.get(5));
		assertEquals(expectedVectors.get(6), pointSet.get(6));
		assertEquals(expectedVectors.get(7), pointSet.get(7));
		assertEquals(expectedVectors.get(8), pointSet.get(8));
		assertEquals(expectedVectors.get(9), pointSet.get(9));
		assertEquals(expectedVectors.get(10), pointSet.get(10));
		assertEquals(expectedVectors.get(11), pointSet.get(11));
		assertEquals(expectedVectors.get(12), pointSet.get(12));
	}

	@Test
	void testGivenScoreWithSingleStaffPointSetReturnsCorrectPositions() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/basic_point_set_test.musicxml");
		assertNotNull(score);

		final PointSet<Point2D> pointSet = PointSet.fromScore(score);
		assertEquals(13, pointSet.size());

		final List<Point2D> expectedVectors = getExpectedVectorsForBasicTest();

		assertEquals(new Position(0, 1, 1, 0), pointSet.getPosition(expectedVectors.get(0)).get());
		assertEquals(new Position(0, 1, 1, 1), pointSet.getPosition(expectedVectors.get(1)).get());
		assertEquals(new Position(0, 1, 1, 2), pointSet.getPosition(expectedVectors.get(2)).get());
		assertEquals(new Position(0, 1, 1, 4), pointSet.getPosition(expectedVectors.get(3)).get());
		assertEquals(new Position(0, 1, 1, 5), pointSet.getPosition(expectedVectors.get(4)).get());
		assertEquals(new Position(0, 1, 1, 6), pointSet.getPosition(expectedVectors.get(5)).get());
		assertEquals(new Position(0, 1, 1, 7), pointSet.getPosition(expectedVectors.get(6)).get());
		assertEquals(new Position(0, 1, 1, 8), pointSet.getPosition(expectedVectors.get(7)).get());

		// Second measure
		assertEquals(new Position(0, 1, 2, 1, 0, 0), pointSet.getPosition(expectedVectors.get(8)).get());
		assertEquals(new Position(0, 1, 2, 1, 0, 1), pointSet.getPosition(expectedVectors.get(9)).get());
		assertEquals(new Position(0, 1, 2, 1, 0, 2), pointSet.getPosition(expectedVectors.get(10)).get());
		assertEquals(new Position(0, 1, 2, 1, 0, 3), pointSet.getPosition(expectedVectors.get(11)).get());
		assertEquals(new Position(0, 2, 1, 1), pointSet.getPosition(expectedVectors.get(12)).get());
	}

	/*
	 * Returns the expected points in the point set representation
	 * of "musicxml/pattern_discovery/multipart_point_set_test.musicxml"
	 */
	private List<Point2D> getExpectedVectorsForMultiPartTest() {
		List<Point2D> vectors = new ArrayList<>();
		double offset = 0.0;

		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 2).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 3).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5).toInt()));
		offset += Durations.HALF.toDouble();

		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 5).toInt()));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 2).toInt()));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 3).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4).toInt()));
		vectors.add(new Point2D(offset, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5).toInt()));

		return vectors;
	}

	@Test
	void testGivenScoreWithMultiplePartsPointSetReturnsCorrectPositions() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/multipart_point_set_test.musicxml");
		assertNotNull(score);

		final PointSet<Point2D> pointSet = PointSet.fromScore(score);
		assertEquals(14, pointSet.size());

		List<Point2D> expected = getExpectedVectorsForMultiPartTest();

		assertEquals(expected.get(0), pointSet.get(0));
		assertEquals(expected.get(1), pointSet.get(1));
		assertEquals(expected.get(2), pointSet.get(2));
		assertEquals(expected.get(3), pointSet.get(3));
		assertEquals(expected.get(4), pointSet.get(4));
		assertEquals(expected.get(5), pointSet.get(5));
		assertEquals(expected.get(6), pointSet.get(6));
		assertEquals(expected.get(7), pointSet.get(7));
		assertEquals(expected.get(8), pointSet.get(8));
		assertEquals(expected.get(9), pointSet.get(9));
		assertEquals(expected.get(10), pointSet.get(10));
		assertEquals(expected.get(11), pointSet.get(11));
		assertEquals(expected.get(12), pointSet.get(12));
		assertEquals(expected.get(13), pointSet.get(13));
	}

	@Test
	void testGivenScoreWithMultiplePartsPointSetHasCorrectPoints() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/multipart_point_set_test.musicxml");
		assertNotNull(score);

		final PointSet<Point2D> pointSet = PointSet.fromScore(score);
		assertEquals(14, pointSet.size());

		List<Point2D> expected = getExpectedVectorsForMultiPartTest();

		assertEquals(new Position(3, 1, 2, 0), pointSet.getPosition(expected.get(0)).get());
		assertEquals(new Position(3, 1, 1, 0), pointSet.getPosition(expected.get(1)).get());
		assertEquals(new Position(1, 1, 1, 0), pointSet.getPosition(expected.get(2)).get());
		assertEquals(new Position(2, 1, 1, 0), pointSet.getPosition(expected.get(3)).get());
		assertEquals(new Position(0, 1, 1, 0), pointSet.getPosition(expected.get(4)).get());

		assertEquals(new Position(3, 1, 1, 1), pointSet.getPosition(expected.get(5)).get());
		assertEquals(new Position(1, 1, 1, 1), pointSet.getPosition(expected.get(6)).get());
		assertEquals(new Position(2, 1, 1, 1), pointSet.getPosition(expected.get(7)).get());
		assertEquals(new Position(0, 1, 1, 1), pointSet.getPosition(expected.get(8)).get());

		assertEquals(new Position(3, 2, 2, 0), pointSet.getPosition(expected.get(9)).get());
		assertEquals(new Position(3, 2, 1, 1), pointSet.getPosition(expected.get(10)).get());
		assertEquals(new Position(1, 2, 1, 1), pointSet.getPosition(expected.get(11)).get());
		assertEquals(new Position(2, 2, 1, 1), pointSet.getPosition(expected.get(12)).get());
		assertEquals(new Position(0, 2, 1, 1), pointSet.getPosition(expected.get(13)).get());
	}
}
