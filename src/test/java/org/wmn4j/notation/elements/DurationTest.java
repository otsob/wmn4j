/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DurationTest {

	@Test
	void testGetDurationWithValidParameter() {
		final Duration duration = Duration.of(1, 4);
		assertTrue(duration != null);
		assertTrue(duration.getNumerator() == 1);
		assertTrue(duration.getDenominator() == 4);
	}

	@Test
	void testGetDurationWithInvalidParameter() {
		try {
			final Duration duration = Duration.of(-1, 2);
			fail("No exception was thrown. Expected: IllegalArgumentException");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
		try {
			final Duration duration = Duration.of(1, 0);
			fail("No exception was thrown. Expected: IllegalArgumentException");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
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
	void testMultiplyBy() {
		assertEquals(Durations.QUARTER, Durations.EIGHTH.multiplyBy(2));
		assertEquals(Durations.QUARTER, Durations.EIGHTH_TRIPLET.multiplyBy(3));
		assertEquals(Durations.EIGHTH.addDot(), Durations.SIXTEENTH.multiplyBy(3));
	}

	@Test
	void testDivideBy() {
		assertEquals(Durations.EIGHTH, Durations.QUARTER.divideBy(2));
		assertEquals(Durations.QUARTER, Durations.WHOLE.divideBy(4));
		assertEquals(Duration.of(1, 20), Durations.QUARTER.divideBy(5));
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
		assertEquals(Duration.of(3, 8), Durations.QUARTER.addDot());
		assertEquals(Duration.of(3, 4), Durations.HALF.addDot());
	}

	@Test
	void testSumOf() {
		List<Duration> durations = new ArrayList<>();
		final int numOfQuarters = 4;
		for (int i = 0; i < numOfQuarters; ++i) {
			durations.add(Durations.QUARTER);
		}

		assertEquals(Durations.QUARTER.multiplyBy(numOfQuarters),
				Duration.sumOf(durations), "Four quarters did not add to whole note.");

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
		assertEquals(Durations.WHOLE, Duration.sumOf(durations), "Mixed durations did not add to whole note.");
	}
}
