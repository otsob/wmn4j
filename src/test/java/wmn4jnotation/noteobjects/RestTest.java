/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jnotation.noteobjects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import wmn4jnotation.noteobjects.Durations;
import wmn4jnotation.noteobjects.Rest;

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
		assertTrue(Rest.getRest(Durations.EIGHT).getDuration().equals(Durations.EIGHT));
		assertFalse(Rest.getRest(Durations.QUARTER).getDuration().equals(Durations.EIGHT));
	}

	@Test
	public void testToString() {
		assertEquals("R(1/4)", Rest.getRest(Durations.QUARTER).toString());
		assertEquals("R(1/12)", Rest.getRest(Durations.EIGHT_TRIPLET).toString());
	}

	@Test
	public void testEquals() {
		Rest quarter = Rest.getRest(Durations.QUARTER);
		Rest half = Rest.getRest(Durations.HALF);

		assertTrue(quarter.equals(Rest.getRest(Durations.QUARTER)));
		assertTrue(Rest.getRest(Durations.QUARTER).equals(quarter));
		assertFalse(quarter.equals(half));
	}
}
