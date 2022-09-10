/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PitchTest {

	@Test
	void testToInt() {
		assertEquals(12, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 0).toInt());
		assertEquals(60, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4).toInt());
		assertEquals(66, Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 4).toInt());
		assertEquals(51, Pitch.of(Pitch.Base.E, Pitch.Accidental.FLAT, 3).toInt());
	}

	@Test
	void testGetPitchClass() {
		assertEquals(PitchClass.C, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5).getPitchClass());
		assertEquals(PitchClass.GSHARP_AFLAT, Pitch.of(Pitch.Base.A, Pitch.Accidental.FLAT, 3).getPitchClass());
		assertEquals(PitchClass.B, Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 2).getPitchClass());
	}

	@Test
	void testGetPitchName() {
		assertEquals(PitchName.of(Pitch.Base.C, Pitch.Accidental.NATURAL),
				Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5).getPitchName());
		assertEquals(PitchName.of(Pitch.Base.A, Pitch.Accidental.FLAT),
				Pitch.of(Pitch.Base.A, Pitch.Accidental.FLAT, 3).getPitchName());
		assertNotEquals(PitchName.of(Pitch.Base.A, Pitch.Accidental.SHARP),
				Pitch.of(Pitch.Base.B, Pitch.Accidental.FLAT, 2).getPitchName());
	}

	@Test
	void testGetPCNumber() {
		assertEquals(0, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5).getPitchClassNumber());
		assertEquals(8, Pitch.of(Pitch.Base.A, Pitch.Accidental.FLAT, 3).getPitchClassNumber());
		assertEquals(11, Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 2).getPitchClassNumber());
	}

	@Test
	void testToString() {
		assertEquals("F0", Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 0).toString());
		assertEquals("Eb2", Pitch.of(Pitch.Base.E, Pitch.Accidental.FLAT, 2).toString());
		assertEquals("G#3", Pitch.of(Pitch.Base.G, Pitch.Accidental.SHARP, 3).toString());
		assertEquals("Abb2", Pitch.of(Pitch.Base.A, Pitch.Accidental.DOUBLE_FLAT, 2).toString());
		assertEquals("D##6", Pitch.of(Pitch.Base.D, Pitch.Accidental.DOUBLE_SHARP, 6).toString());
	}

	@Test
	void testEquals() {
		assertTrue(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)
				.equals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)));
		assertTrue(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 3)
				.equals(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 3)));
		assertTrue(Pitch.of(Pitch.Base.C, Pitch.Accidental.FLAT, 2)
				.equals(Pitch.of(Pitch.Base.C, Pitch.Accidental.FLAT, 2)));

		assertFalse(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)
				.equals(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)
				.equals(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, Pitch.Accidental.FLAT, 3)
				.equals(Pitch.of(Pitch.Base.D, Pitch.Accidental.FLAT, 2)));
	}

	@Test
	void testEqualsEnharmonically() {
		assertTrue(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 2)
				.equalsEnharmonically(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 2)));
		assertTrue(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 2)
				.equalsEnharmonically(Pitch.of(Pitch.Base.D, Pitch.Accidental.FLAT, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 2)
				.equalsEnharmonically(Pitch.of(Pitch.Base.D, Pitch.Accidental.FLAT, 3)));
	}

	@Test
	void testHigherThan() {
		assertTrue(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 3)
				.isHigherThan(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)));
		assertFalse(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1)
				.isHigherThan(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1)));
		assertFalse(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)
				.isHigherThan(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 3)));
	}

	@Test
	void testLowerThan() {
		assertTrue(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)
				.isLowerThan(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 2)));
		assertFalse(Pitch.of(Pitch.Base.E, Pitch.Accidental.SHARP
				, 4).isLowerThan(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4)));
		assertFalse(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4)
				.isLowerThan(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 3)));
	}

	@Test
	void testCompareTo() {
		assertTrue(0 == Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)
				.compareTo(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)));
		assertTrue(0 > Pitch.of(Pitch.Base.C, Pitch.Accidental.FLAT, 2)
				.compareTo(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2)));
		assertTrue(0 < Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 3)
				.compareTo(Pitch.of(Pitch.Base.D, Pitch.Accidental.SHARP, 3)));
	}
}
