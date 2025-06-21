/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import org.junit.jupiter.api.Test;
import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class PointPatternTest {

	private List<Point2D> getTestPoints() {
		List<Point2D> points = new ArrayList<>();

		points.add(new Point2D(0.33, 50));
		points.add(new Point2D(5, 64));
		points.add(new Point2D(27, 12));
		points.add(new Point2D(39.125, 91));
		points.add(new Point2D(30003, 3));

		return points;
	}

	@Test
	void testPointPatternHasCorrectContents() {
		List<Point2D> points = getTestPoints();
		final PointPattern<Point2D> pattern = new PointPattern<>(points);

		assertEquals(5, pattern.size());
		assertEquals(points.get(0), pattern.get(0));
		assertEquals(points.get(1), pattern.get(1));
		assertEquals(points.get(2), pattern.get(2));
		assertEquals(points.get(3), pattern.get(3));
		assertEquals(points.get(4), pattern.get(4));
	}

	@Test
	void testGivenMonophonicPatternCorrectPointPatternsIsCreated() {
		List<Durational> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4, Durations.HALF));
		notes.add(Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.HALF));
		notes.add(Rest.of(Durations.EIGHTH));
		notes.add(Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4, Durations.QUARTER));
		notes.add(Note.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4, Durations.QUARTER));

		final Pattern monophonicPattern = Pattern.of(notes);
		final PointPattern<Point2D> pointPattern = PointPattern.from(monophonicPattern);
		assertEquals(4, pointPattern.size());
		assertEquals(new Point2D(0.0, 60.0), pointPattern.get(0));
		assertEquals(new Point2D(0.5, 62.0), pointPattern.get(1));
		assertEquals(new Point2D(1.125, 64.0), pointPattern.get(2));
		assertEquals(new Point2D(1.375, 65.0), pointPattern.get(3));

	}

	@Test
	void testGivenPolyphonicPatternCorrectPointPatternsIsCreated() {
		final Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		List<Durational> voice1 = new ArrayList<>();
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 4), Durations.QUARTER));
		voice1.add(Rest.of(Durations.QUARTER));
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 4), Durations.QUARTER));

		List<Durational> voice2 = new ArrayList<>();
		voice2.add(Rest.of(Durations.HALF));
		voice2.add(Chord.of(
				Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.HALF),
				Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.SHARP, 4), Durations.HALF),
				Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.HALF)));
		voice2.add(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));

		voices.put(1, voice1);
		voices.put(2, voice2);

		final Pattern patternWithMultipleVoices = Pattern.of(voices);
		final PointPattern<Point2D> pointPattern = PointPattern.from(patternWithMultipleVoices);
		assertEquals(7, pointPattern.size());
		assertEquals(new Point2D(0.0, 60.0), pointPattern.get(0));
		assertEquals(new Point2D(0.25, 61.0), pointPattern.get(1));
		assertEquals(new Point2D(0.5, 62.0), pointPattern.get(2));
		assertEquals(new Point2D(0.5, 63.0), pointPattern.get(3));
		assertEquals(new Point2D(0.5, 64.0), pointPattern.get(4));

		assertEquals(new Point2D(0.75, 61.0), pointPattern.get(5));
		assertEquals(new Point2D(1.0, 62.0), pointPattern.get(6));
	}

	@Test
	void testEqualsAndHashCode() {
		final PointPattern<Point2D> patternA = new PointPattern<>(getTestPoints());
		final PointPattern<Point2D> patternB = new PointPattern<>(getTestPoints());

		assertEquals(patternA, patternA);
		assertEquals(patternA, patternB);
		assertEquals(patternB, patternA);

		assertEquals(patternA.hashCode(), patternB.hashCode());

		List<Point2D> points = getTestPoints();
		points.add(1, new Point2D(5, 64));

		final PointPattern<Point2D> patternC = new PointPattern<>(points);
		assertNotEquals(patternA, patternC);
	}

	@Test
	void testVectorizedRepresentation() {
		List<Point2D> points = getTestPoints();
		final PointPattern<Point2D> pattern = new PointPattern<>(points);
		final PointPattern<Point2D> vectorizedPattern = pattern.vectorized();

		assertEquals(4, vectorizedPattern.size());

		assertEquals(points.get(1).subtract(points.get(0)), vectorizedPattern.get(0));
		assertEquals(points.get(2).subtract(points.get(1)), vectorizedPattern.get(1));
		assertEquals(points.get(3).subtract(points.get(2)), vectorizedPattern.get(2));
		assertEquals(points.get(4).subtract(points.get(3)), vectorizedPattern.get(3));

		final PointPattern<Point2D> singletonPattern = new PointPattern<>(
				Collections.singletonList(new Point2D(0.33, 50)));

		assertEquals(0, singletonPattern.vectorized().size());
	}

	@Test
	void testTranslation() {
		List<Point2D> points = getTestPoints();
		final PointPattern<Point2D> pattern = new PointPattern<>(points);
		final var translated = pattern.translate(new Point2D(1.0, 1.0));

		final var expected = new PointPattern<>(Arrays.asList(
				new Point2D(1.33, 51),
				new Point2D(6, 65),
				new Point2D(28, 13),
				new Point2D(40.125, 92),
				new Point2D(30004, 4)));

		assertEquals(expected, translated);
	}
}
