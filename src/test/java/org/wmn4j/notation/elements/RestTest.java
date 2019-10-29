/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestTest {

	@Test
	void testGetDuration() {
		assertTrue(Rest.of(Durations.EIGHTH).getDuration().equals(Durations.EIGHTH));
		assertFalse(Rest.of(Durations.QUARTER).getDuration().equals(Durations.EIGHTH));
	}

	@Test
	void testToString() {
		assertEquals("R(1/4)", Rest.of(Durations.QUARTER).toString());
		assertEquals("R(1/12)", Rest.of(Durations.EIGHTH_TRIPLET).toString());
	}

	@Test
	void testEquals() {
		final Rest quarter = Rest.of(Durations.QUARTER);
		final Rest half = Rest.of(Durations.HALF);

		assertTrue(quarter.equals(Rest.of(Durations.QUARTER)));
		assertTrue(Rest.of(Durations.QUARTER).equals(quarter));
		assertFalse(quarter.equals(half));
	}
}
