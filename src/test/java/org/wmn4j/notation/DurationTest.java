/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DurationTest {

	@Test
	void testGetDurationWithValidParameter() {
		final Duration duration = Duration.of(1, 4);
		assertTrue(duration != null);
		assertEquals(1, duration.getNumerator());
		assertEquals(4, duration.getDenominator());
		assertEquals(0, duration.getDotCount());
	}

	@Test
	void testCreateDurationWithInvalidParameter() {
		assertThrows(Exception.class, () -> Duration.of(-1, 2));
		assertThrows(Exception.class, () -> Duration.of(1, 0));
		assertThrows(Exception.class, () -> Duration.of(1, 2, -1, 1));
		assertThrows(Exception.class, () -> Duration.of(1, 2, 0, 0));
	}

	@Test
	void testEquals() {
		final Duration quarter = Duration.of(1, 4);
		assertTrue(quarter.equals(quarter));
		assertTrue(quarter.equals(Durations.QUARTER));
		assertTrue(quarter.equals(Duration.of(1, 4)));

		final Duration anotherQuarter = Duration.of(2, 8);
		assertTrue(quarter.equals(anotherQuarter));
		assertTrue(quarter.equals(Durations.QUARTER));
		assertTrue(quarter.equals(Duration.of(1, 4)));

		final Duration notQuarter = Duration.of(1, 8);
		assertFalse(notQuarter.equals(quarter));

		assertFalse(Durations.EIGHTH_TRIPLET.equals(Durations.THIRTYSECOND));
	}

	@Test
	void testRationalNumberReduced() {
		final Duration quarter = Duration.of(3, 12);
		assertEquals(1, quarter.getNumerator());
		assertEquals(4, quarter.getDenominator());

		final Duration quintuplet = Duration.of(5, 100);
		assertEquals(1, quintuplet.getNumerator());
		assertEquals(20, quintuplet.getDenominator());
	}

	@Test
	void testToString() {
		assertEquals("(1/4)", Duration.of(1, 4).toString());
		assertEquals("(1/8)", Durations.EIGHTH.toString());
		assertEquals("(1/16)", Duration.of(1, 16).toString());
	}

	@Test
	void testToDouble() {
		assertEquals(0.25, Durations.QUARTER.toDouble(), 1e-6);
		assertEquals(0.5, Durations.HALF.toDouble(), 1e-6);
		assertFalse(0.49 == Durations.HALF.toDouble());
	}

	@Test
	void testAdd() {
		assertEquals(Durations.EIGHTH, Durations.SIXTEENTH.add(Durations.SIXTEENTH));
		assertEquals(Durations.QUARTER,
				Durations.EIGHTH_TRIPLET.add(Durations.EIGHTH_TRIPLET.add(Durations.EIGHTH_TRIPLET)));
		assertEquals(Duration.of(1, 8), Duration.of(3, 32).add(Duration.of(1, 32)));
	}

	@Test
	void testSubtract() {
		assertEquals(Durations.EIGHTH, Durations.QUARTER.subtract(Durations.EIGHTH));
		assertEquals(Durations.QUARTER.addDot(), Durations.HALF.subtract(Durations.EIGHTH));
		assertEquals(Duration.of(2, 12), Durations.QUARTER.subtract(Durations.EIGHTH_TRIPLET));
	}

	@Test
	void testMultiply() {
		assertEquals(Durations.QUARTER, Durations.EIGHTH.multiply(2));
		assertEquals(Durations.QUARTER, Durations.EIGHTH_TRIPLET.multiply(3));
		assertEquals(Durations.EIGHTH.addDot(), Durations.SIXTEENTH.multiply(3));
	}

	@Test
	void testDivide() {
		assertEquals(Durations.EIGHTH, Durations.QUARTER.divide(2));
		assertEquals(Durations.QUARTER, Durations.WHOLE.divide(4));
		assertEquals(Duration.of(1, 20), Durations.QUARTER.divide(5));
	}

	@Test
	void testLongerThan() {
		assertTrue(Durations.QUARTER.isLongerThan(Durations.EIGHTH));
		assertFalse(Durations.EIGHTH_TRIPLET.isLongerThan(Durations.EIGHTH));
	}

	@Test
	void testShorterThan() {
		assertTrue(Durations.SIXTEENTH.isShorterThan(Durations.EIGHTH));
		assertFalse(Durations.EIGHTH_TRIPLET.isShorterThan(Durations.THIRTYSECOND));
	}

	@Test
	void testCompareTo() {
		assertEquals(0, Durations.EIGHTH.compareTo(Durations.EIGHTH));
		assertTrue(0 > Durations.QUARTER.compareTo(Durations.HALF));
		assertTrue(0 < Durations.HALF.compareTo(Durations.QUARTER));
		assertTrue(0 > Durations.QUARTER.compareTo(Durations.QUARTER.addDot()));
		assertTrue(0 < Durations.QUARTER.compareTo(Durations.EIGHTH_TRIPLET.addDot()));
	}

	@Test
	void testAddDot() {
		final Duration dottedQuarter = Durations.QUARTER.addDot();
		assertEquals(Duration.of(3, 8), dottedQuarter);
		assertEquals(1, dottedQuarter.getDotCount());

		final Duration doubleDottedQuarter = dottedQuarter.addDot();
		assertEquals(Duration.of(7, 16), doubleDottedQuarter);
		assertEquals(2, doubleDottedQuarter.getDotCount());

		final Duration tripleDottedHalf = Durations.HALF.addDot().addDot().addDot();
		assertEquals(Duration.of(15, 16), tripleDottedHalf);
		assertEquals(3, tripleDottedHalf.getDotCount());

		Duration multiDottedTriplet = Durations.EIGHTH_TRIPLET;
		Duration expected = Durations.EIGHTH_TRIPLET;

		for (int dots = 1; dots < 5; dots++) {
			multiDottedTriplet = multiDottedTriplet.addDot();
			expected = expected.add(Durations.EIGHTH_TRIPLET.divide((int) Math.pow(2, dots)));

			assertEquals(dots, multiDottedTriplet.getDotCount());
			assertEquals(expected, multiDottedTriplet);
		}
	}

	@Test
	void testRemoveDot() {
		assertEquals(Durations.EIGHTH, Durations.EIGHTH.removeDot());

		final Duration quarter = Durations.QUARTER.addDot().removeDot();
		assertEquals(Durations.QUARTER, quarter);
		assertEquals(0, quarter.getDotCount());

		final Duration dottedQuarter = Durations.QUARTER.addDot().addDot().removeDot();
		assertEquals(Duration.of(3, 8), dottedQuarter);
		assertEquals(1, dottedQuarter.getDotCount());

		final Duration doubleDottedHalf = Durations.HALF.addDot().addDot().addDot().removeDot();
		assertEquals(Duration.of(7, 8), doubleDottedHalf);
		assertEquals(2, doubleDottedHalf.getDotCount());
	}

	@Test
	void testRemoveDots() {
		assertEquals(Durations.EIGHTH, Durations.EIGHTH.removeDots());

		Duration quarter = Durations.QUARTER.addDot().removeDots();
		assertEquals(Durations.QUARTER, quarter);
		assertEquals(0, quarter.getDotCount());

		quarter = Durations.QUARTER.addDot().addDot().removeDots();
		assertEquals(Durations.QUARTER, quarter);
		assertEquals(0, quarter.getDotCount());

		final Duration quintuplet = Durations.QUARTER.divide(5).addDot().addDot().addDot().removeDots();
		assertEquals(Durations.QUARTER.divide(5), quintuplet);
		assertEquals(0, quintuplet.getDotCount());
	}

	@Test
	void testCorrectTupletDivisorIsReturnedAfterDivision() {
		final Duration quarter = Durations.QUARTER;
		assertEquals(1, quarter.getTupletDivisor());
		assertEquals(1, quarter.divide(2).getTupletDivisor());
		assertEquals(3, quarter.divide(3).getTupletDivisor());
		assertEquals(7, quarter.divide(7).getTupletDivisor());
		assertEquals(3, quarter.divide(2).divide(3).getTupletDivisor());
	}

	@Test
	void testCorrectTupletDivisorIsReturnedAfterAddingDots() {
		final Duration triplet = Durations.EIGHTH_TRIPLET;
		assertEquals(3, triplet.getTupletDivisor());
		assertEquals(3, triplet.addDot().getTupletDivisor());
		assertEquals(3, triplet.addDot().addDot().getTupletDivisor());
		assertEquals(3, triplet.addDot().removeDot().getTupletDivisor());
		assertEquals(3, triplet.addDot().removeDots().getTupletDivisor());
	}

	@Test
	void testCorrectDotCountIsReturnedAfterDivision() {
		final Duration dottedEight = Durations.EIGHTH.addDot();
		assertEquals(1, dottedEight.getDotCount());
		assertEquals(1, dottedEight.divide(2).getDotCount());
		assertEquals(1, dottedEight.divide(3).getDotCount());
	}

	@Test
	void testSum() {
		List<Duration> durations = new ArrayList<>();
		final int numOfQuarters = 4;
		for (int i = 0; i < numOfQuarters; ++i) {
			durations.add(Durations.QUARTER);
		}

		assertEquals(Durations.QUARTER.multiply(numOfQuarters),
				Duration.sum(durations), "Four quarters did not add to whole note.");

		durations = new ArrayList<>();
		durations.add(Durations.EIGHTH);
		durations.add(Durations.SIXTEENTH);
		durations.add(Durations.SIXTEENTH);
		durations.add(Durations.QUARTER.addDot());
		durations.add(Durations.SIXTEENTH_TRIPLET);
		durations.add(Durations.SIXTEENTH_TRIPLET);
		durations.add(Durations.SIXTEENTH_TRIPLET);
		durations.add(Durations.EIGHTH_TRIPLET);
		durations.add(Durations.EIGHTH_TRIPLET);
		durations.add(Durations.EIGHTH_TRIPLET);
		assertEquals(Durations.WHOLE, Duration.sum(durations), "Mixed durations did not add to whole note.");
	}
}
