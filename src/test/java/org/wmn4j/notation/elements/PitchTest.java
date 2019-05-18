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
public class PitchTest {

	public PitchTest() {
	}

	@Test
	public void testToInt() {
		assertEquals(12, Pitch.of(Pitch.Base.C, 0, 0).toInt());
		assertEquals(60, Pitch.of(Pitch.Base.C, 0, 4).toInt());
		assertEquals(66, Pitch.of(Pitch.Base.F, 1, 4).toInt());
		assertEquals(51, Pitch.of(Pitch.Base.E, -1, 3).toInt());
	}

	@Test
	public void testGetPitchClass() {
		assertEquals(PitchClass.C, Pitch.of(Pitch.Base.C, 0, 5).getPitchClass());
		assertEquals(PitchClass.GSHARP_AFLAT, Pitch.of(Pitch.Base.A, -1, 3).getPitchClass());
		assertEquals(PitchClass.B, Pitch.of(Pitch.Base.B, 0, 2).getPitchClass());
	}

	@Test
	public void testGetPCNumber() {
		assertEquals(0, Pitch.of(Pitch.Base.C, 0, 5).getPitchClassNumber());
		assertEquals(8, Pitch.of(Pitch.Base.A, -1, 3).getPitchClassNumber());
		assertEquals(11, Pitch.of(Pitch.Base.B, 0, 2).getPitchClassNumber());
	}

	@Test
	public void testToString() {
		assertEquals("F0", Pitch.of(Pitch.Base.F, 0, 0).toString());
		assertEquals("Eb2", Pitch.of(Pitch.Base.E, -1, 2).toString());
		assertEquals("G#3", Pitch.of(Pitch.Base.G, 1, 3).toString());
		assertEquals("Abb2", Pitch.of(Pitch.Base.A, -2, 2).toString());
		assertEquals("D##6", Pitch.of(Pitch.Base.D, 2, 6).toString());
	}

	@Test
	public void testEquals() {
		assertTrue(Pitch.of(Pitch.Base.C, 0, 2).equals(Pitch.of(Pitch.Base.C, 0, 2)));
		assertTrue(Pitch.of(Pitch.Base.C, 1, 3).equals(Pitch.of(Pitch.Base.C, 1, 3)));
		assertTrue(Pitch.of(Pitch.Base.C, -1, 2).equals(Pitch.of(Pitch.Base.C, -1, 2)));

		assertFalse(Pitch.of(Pitch.Base.C, 0, 2).equals(Pitch.of(Pitch.Base.D, 0, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, 0, 2).equals(Pitch.of(Pitch.Base.C, 1, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, -1, 3).equals(Pitch.of(Pitch.Base.D, -1, 2)));
	}

	@Test
	public void testEqualsEnharmonically() {
		assertTrue(Pitch.of(Pitch.Base.C, 1, 2).equalsEnharmonically(Pitch.of(Pitch.Base.C, 1, 2)));
		assertTrue(Pitch.of(Pitch.Base.C, 1, 2).equalsEnharmonically(Pitch.of(Pitch.Base.D, -1, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, 1, 2).equalsEnharmonically(Pitch.of(Pitch.Base.D, -1, 3)));
	}

	@Test
	public void testHigherThan() {
		assertTrue(Pitch.of(Pitch.Base.C, 0, 3).isHigherThan(Pitch.of(Pitch.Base.C, 0, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, 0, 1).isHigherThan(Pitch.of(Pitch.Base.C, 0, 1)));
		assertFalse(Pitch.of(Pitch.Base.C, 0, 2).isHigherThan(Pitch.of(Pitch.Base.C, 0, 3)));
	}

	@Test
	public void testLowerThan() {
		assertTrue(Pitch.of(Pitch.Base.C, 0, 2).isLowerThan(Pitch.of(Pitch.Base.C, 1, 2)));
		assertFalse(Pitch.of(Pitch.Base.E, 1, 4).isLowerThan(Pitch.of(Pitch.Base.D, 0, 4)));
		assertFalse(Pitch.of(Pitch.Base.C, 0, 4).isLowerThan(Pitch.of(Pitch.Base.C, 0, 3)));
	}

	@Test
	public void testCompareTo() {
		assertTrue(0 == Pitch.of(Pitch.Base.C, 0, 2).compareTo(Pitch.of(Pitch.Base.C, 0, 2)));
		assertTrue(0 > Pitch.of(Pitch.Base.C, -1, 2).compareTo(Pitch.of(Pitch.Base.C, 0, 2)));
		assertTrue(0 < Pitch.of(Pitch.Base.E, 0, 3).compareTo(Pitch.of(Pitch.Base.D, 1, 3)));
	}
}
