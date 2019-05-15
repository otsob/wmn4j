/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Unit tests for Duration class.
 *
 * @author Otso Björklund
 */
public class DurationTest {

	public DurationTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Test
	public void testGetDurationWithValidParameter() {
		final Duration duration = Duration.of(1, 4);
		assertTrue(duration != null);
		assertTrue(duration.getNumerator() == 1);
		assertTrue(duration.getDenominator() == 4);
	}

	@Test
	public void testGetDurationWithInvalidParameter() {
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
	public void testEquals() {
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

		assertFalse(Durations.EIGHT_TRIPLET.equals(Durations.THIRTYSECOND));
	}

	@Test
	public void testRationalNumberReduced() {
		final Duration quarter = Duration.of(3, 12);
		assertEquals(1, quarter.getNumerator());
		assertEquals(4, quarter.getDenominator());

		final Duration quintuplet = Duration.of(5, 100);
		assertEquals(1, quintuplet.getNumerator());
		assertEquals(20, quintuplet.getDenominator());
	}

	@Test
	public void testToString() {
		assertEquals("(1/4)", Duration.of(1, 4).toString());
		assertEquals("(1/8)", Durations.EIGHT.toString());
		assertEquals("(1/16)", Duration.of(1, 16).toString());
	}

	@Test
	public void testToDouble() {
		assertEquals(0.25, Durations.QUARTER.toDouble(), 1e-6);
		assertEquals(0.5, Durations.HALF.toDouble(), 1e-6);
		assertFalse(0.49 == Durations.HALF.toDouble());
	}

	@Test
	public void testAdd() {
		assertEquals(Durations.EIGHT, Durations.SIXTEENTH.add(Durations.SIXTEENTH));
		assertEquals(Durations.QUARTER,
				Durations.EIGHT_TRIPLET.add(Durations.EIGHT_TRIPLET.add(Durations.EIGHT_TRIPLET)));
		assertEquals(Duration.of(1, 8), Duration.of(3, 32).add(Duration.of(1, 32)));
	}

	@Test
	public void testSubtract() {
		assertEquals(Durations.EIGHT, Durations.QUARTER.subtract(Durations.EIGHT));
		assertEquals(Durations.QUARTER.addDot(), Durations.HALF.subtract(Durations.EIGHT));
		assertEquals(Duration.of(2, 12), Durations.QUARTER.subtract(Durations.EIGHT_TRIPLET));
	}

	@Test
	public void testMultiplyBy() {
		assertEquals(Durations.QUARTER, Durations.EIGHT.multiplyBy(2));
		assertEquals(Durations.QUARTER, Durations.EIGHT_TRIPLET.multiplyBy(3));
		assertEquals(Durations.EIGHT.addDot(), Durations.SIXTEENTH.multiplyBy(3));
	}

	@Test
	public void testDivideBy() {
		assertEquals(Durations.EIGHT, Durations.QUARTER.divideBy(2));
		assertEquals(Durations.QUARTER, Durations.WHOLE.divideBy(4));
		assertEquals(Duration.of(1, 20), Durations.QUARTER.divideBy(5));
	}

	@Test
	public void testLongerThan() {
		assertTrue(Durations.QUARTER.isLongerThan(Durations.EIGHT));
		assertFalse(Durations.EIGHT_TRIPLET.isLongerThan(Durations.EIGHT));
	}

	@Test
	public void testShorterThan() {
		assertTrue(Durations.SIXTEENTH.isShorterThan(Durations.EIGHT));
		assertFalse(Durations.EIGHT_TRIPLET.isShorterThan(Durations.THIRTYSECOND));
	}

	@Test
	public void testCompareTo() {
		assertEquals(0, Durations.EIGHT.compareTo(Durations.EIGHT));
		assertTrue(0 > Durations.QUARTER.compareTo(Durations.HALF));
		assertTrue(0 < Durations.HALF.compareTo(Durations.QUARTER));
		assertTrue(0 > Durations.QUARTER.compareTo(Durations.QUARTER.addDot()));
		assertTrue(0 < Durations.QUARTER.compareTo(Durations.EIGHT_TRIPLET.addDot()));
	}

	@Test
	public void testAddDot() {
		assertEquals(Duration.of(3, 8), Durations.QUARTER.addDot());
		assertEquals(Duration.of(3, 4), Durations.HALF.addDot());
	}

	@Test
	public void testSumOf() {
		List<Duration> durations = new ArrayList<>();
		final int numOfQuarters = 4;
		for (int i = 0; i < numOfQuarters; ++i) {
			durations.add(Durations.QUARTER);
		}

		assertEquals("Four quarters did not add to whole note.", Durations.QUARTER.multiplyBy(numOfQuarters),
				Duration.sumOf(durations));

		durations = new ArrayList<>();
		durations.add(Durations.EIGHT);
		durations.add(Durations.SIXTEENTH);
		durations.add(Durations.SIXTEENTH);
		durations.add(Durations.QUARTER.addDot());
		durations.add(Durations.SIXTEENTH_TRIPLET);
		durations.add(Durations.SIXTEENTH_TRIPLET);
		durations.add(Durations.SIXTEENTH_TRIPLET);
		durations.add(Durations.EIGHT_TRIPLET);
		durations.add(Durations.EIGHT_TRIPLET);
		durations.add(Durations.EIGHT_TRIPLET);
		assertEquals("Mixed durations did not add to whole note.", Durations.WHOLE, Duration.sumOf(durations));
	}
}
