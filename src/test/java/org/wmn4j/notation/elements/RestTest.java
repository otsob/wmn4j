/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Otso Björklund
 */
public class RestTest {

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
