/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Otso Björklund
 */
public class PitchClassTest {

	public PitchClassTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Test
	public void testToInt() {
		assertEquals(0, PitchClass.C.toInt());
		assertEquals(3, PitchClass.DSHARP_EFLAT.toInt());
		assertEquals(11, PitchClass.B.toInt());
	}

	/**
	 * Test of fromInt method, of class PitchClass.
	 */
	@Test
	public void testFromInt() {
		assertEquals(PitchClass.C, PitchClass.fromInt(12));
		assertEquals(PitchClass.CSHARP_DFLAT, PitchClass.fromInt(25));
		assertEquals(PitchClass.B, PitchClass.fromInt(11 + 12 * 2));
		assertEquals(PitchClass.G, PitchClass.fromInt(7 + 12 * 5));
	}

}
