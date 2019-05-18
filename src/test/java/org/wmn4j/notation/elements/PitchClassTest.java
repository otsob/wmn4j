/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PitchClassTest {

	public PitchClassTest() {
	}

	@Test
	public void testToInt() {
		assertEquals(0, PitchClass.C.toInt());
		assertEquals(3, PitchClass.DSHARP_EFLAT.toInt());
		assertEquals(11, PitchClass.B.toInt());
	}

	@Test
	public void testFromInt() {
		assertEquals(PitchClass.C, PitchClass.fromInt(12));
		assertEquals(PitchClass.CSHARP_DFLAT, PitchClass.fromInt(25));
		assertEquals(PitchClass.B, PitchClass.fromInt(11 + 12 * 2));
		assertEquals(PitchClass.G, PitchClass.fromInt(7 + 12 * 5));
	}

}
