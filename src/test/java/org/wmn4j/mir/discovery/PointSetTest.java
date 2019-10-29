package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.TimeSignatures;
import org.wmn4j.notation.iterators.Position;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class PointSetTest {

	/*
	 * Returns the expected points in the point set representation
	 * of "musicxml/pattern_discovery/basic_point_set_test.xml"
	 */
	private List<NoteEventVector> getExpectedVectorsForBasicTest() {
		List<NoteEventVector> vectors = new ArrayList<>();
		double offset = 0.0;

		vectors.add(new NoteEventVector(offset, 60, 0));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new NoteEventVector(offset, 62, 0));
		offset += Durations.EIGHTH.toDouble();

		vectors.add(new NoteEventVector(offset, 64, 0));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new NoteEventVector(offset, 65, 0));
		offset += Durations.SIXTEENTH.toDouble();

		vectors.add(new NoteEventVector(offset, 65, 0));
		offset += Durations.SIXTEENTH.toDouble();

		vectors.add(new NoteEventVector(offset, 65, 0));
		offset += Durations.EIGHTH.divideBy(3).toDouble();

		vectors.add(new NoteEventVector(offset, 65, 0));
		offset += Durations.EIGHTH.divideBy(3).toDouble();

		vectors.add(new NoteEventVector(offset, 65, 0));

		// Second measure
		offset = TimeSignatures.FOUR_FOUR.getTotalDuration().toDouble();

		vectors.add(new NoteEventVector(offset, 60, 0));
		vectors.add(new NoteEventVector(offset, 64, 0));
		vectors.add(new NoteEventVector(offset, 67, 0));
		vectors.add(new NoteEventVector(offset, 70, 0));

		offset += Durations.HALF.toDouble();
		vectors.add(new NoteEventVector(offset, 60, 0));

		return vectors;
	}

	@Test
	void testGivenScoreWithSingleStaffPointSetHasCorrectPoints() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/basic_point_set_test.xml");
		assertNotNull(score);

		final PointSet pointSet = new PointSet(score);
		assertEquals(13, pointSet.size());

		final List<NoteEventVector> expectedVectors = getExpectedVectorsForBasicTest();

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
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/basic_point_set_test.xml");
		assertNotNull(score);

		final PointSet pointSet = new PointSet(score);
		assertEquals(13, pointSet.size());

		final List<NoteEventVector> expectedVectors = getExpectedVectorsForBasicTest();

		assertEquals(new Position(0, 1, 1, 0), pointSet.getPosition(expectedVectors.get(0)));
		assertEquals(new Position(0, 1, 1, 1), pointSet.getPosition(expectedVectors.get(1)));
		assertEquals(new Position(0, 1, 1, 2), pointSet.getPosition(expectedVectors.get(2)));
		assertEquals(new Position(0, 1, 1, 4), pointSet.getPosition(expectedVectors.get(3)));
		assertEquals(new Position(0, 1, 1, 5), pointSet.getPosition(expectedVectors.get(4)));
		assertEquals(new Position(0, 1, 1, 6), pointSet.getPosition(expectedVectors.get(5)));
		assertEquals(new Position(0, 1, 1, 7), pointSet.getPosition(expectedVectors.get(6)));
		assertEquals(new Position(0, 1, 1, 8), pointSet.getPosition(expectedVectors.get(7)));

		// Second measure
		assertEquals(new Position(0, 1, 2, 1, 0, 0), pointSet.getPosition(expectedVectors.get(8)));
		assertEquals(new Position(0, 1, 2, 1, 0, 1), pointSet.getPosition(expectedVectors.get(9)));
		assertEquals(new Position(0, 1, 2, 1, 0, 2), pointSet.getPosition(expectedVectors.get(10)));
		assertEquals(new Position(0, 1, 2, 1, 0, 3), pointSet.getPosition(expectedVectors.get(11)));
		assertEquals(new Position(0, 2, 1, 1), pointSet.getPosition(expectedVectors.get(12)));
	}

	/*
	 * Returns the expected points in the point set representation
	 * of "musicxml/pattern_discovery/multipart_point_set_test.xml"
	 */
	private List<NoteEventVector> getExpectedVectorsForMultiPartTest() {
		List<NoteEventVector> vectors = new ArrayList<>();
		double offset = 0.0;

		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.G, 0, 2).toInt(), 3));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.G, 0, 3).toInt(), 3));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.C, 0, 4).toInt(), 1));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.G, 0, 4).toInt(), 2));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.C, 0, 5).toInt(), 0));
		offset += Durations.HALF.toDouble();

		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.A, 0, 3).toInt(), 3));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.D, 0, 4).toInt(), 1));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.A, 0, 4).toInt(), 2));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.D, 0, 5).toInt(), 0));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.A, 0, 2).toInt(), 3));
		offset += Durations.QUARTER.toDouble();

		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.G, 0, 3).toInt(), 3));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.C, 0, 4).toInt(), 1));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.G, 0, 4).toInt(), 2));
		vectors.add(new NoteEventVector(offset, Pitch.of(Pitch.Base.C, 0, 5).toInt(), 0));

		return vectors;
	}

	@Test
	void testGivenScoreWithMultiplePartsPointSetReturnsCorrectPositions() {
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/multipart_point_set_test.xml");
		assertNotNull(score);

		final PointSet pointSet = new PointSet(score);
		assertEquals(14, pointSet.size());

		List<NoteEventVector> expected = getExpectedVectorsForMultiPartTest();

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
		final Score score = TestHelper.readScore("musicxml/pattern_discovery/multipart_point_set_test.xml");
		assertNotNull(score);

		final PointSet pointSet = new PointSet(score);
		assertEquals(14, pointSet.size());

		List<NoteEventVector> expected = getExpectedVectorsForMultiPartTest();

		assertEquals(new Position(3, 1, 2, 0), pointSet.getPosition(expected.get(0)));
		assertEquals(new Position(3, 1, 1, 0), pointSet.getPosition(expected.get(1)));
		assertEquals(new Position(1, 1, 1, 0), pointSet.getPosition(expected.get(2)));
		assertEquals(new Position(2, 1, 1, 0), pointSet.getPosition(expected.get(3)));
		assertEquals(new Position(0, 1, 1, 0), pointSet.getPosition(expected.get(4)));

		assertEquals(new Position(3, 1, 1, 1), pointSet.getPosition(expected.get(5)));
		assertEquals(new Position(1, 1, 1, 1), pointSet.getPosition(expected.get(6)));
		assertEquals(new Position(2, 1, 1, 1), pointSet.getPosition(expected.get(7)));
		assertEquals(new Position(0, 1, 1, 1), pointSet.getPosition(expected.get(8)));

		assertEquals(new Position(3, 2, 2, 0), pointSet.getPosition(expected.get(9)));
		assertEquals(new Position(3, 2, 1, 1), pointSet.getPosition(expected.get(10)));
		assertEquals(new Position(1, 2, 1, 1), pointSet.getPosition(expected.get(11)));
		assertEquals(new Position(2, 2, 1, 1), pointSet.getPosition(expected.get(12)));
		assertEquals(new Position(0, 2, 1, 1), pointSet.getPosition(expected.get(13)));
	}
}
