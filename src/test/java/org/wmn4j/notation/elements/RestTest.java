/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Rest;

/**
 *
 * @author Otso Björklund
 */
public class RestTest {

	public RestTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Test
	public void testGetDuration() {
		assertTrue(Rest.of(Durations.EIGHT).getDuration().equals(Durations.EIGHT));
		assertFalse(Rest.of(Durations.QUARTER).getDuration().equals(Durations.EIGHT));
	}

	@Test
	public void testToString() {
		assertEquals("R(1/4)", Rest.of(Durations.QUARTER).toString());
		assertEquals("R(1/12)", Rest.of(Durations.EIGHT_TRIPLET).toString());
	}

	@Test
	public void testEquals() {
		final Rest quarter = Rest.of(Durations.QUARTER);
		final Rest half = Rest.of(Durations.HALF);

		assertTrue(quarter.equals(Rest.of(Durations.QUARTER)));
		assertTrue(Rest.of(Durations.QUARTER).equals(quarter));
		assertFalse(quarter.equals(half));
	}
}
