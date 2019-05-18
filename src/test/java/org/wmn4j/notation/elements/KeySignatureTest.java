/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Otso Björklund
 */
public class KeySignatureTest {

	public KeySignatureTest() {
	}

	@Test
	public void testGetKeySigExceptions() {
		try {
			final List<Pitch.Base> sharps = Arrays.asList(Pitch.Base.C);
			final KeySignature illegalCustomKeySig = KeySignature.of(sharps, sharps);
			fail("A KeySignature with the same note as sharp and flat was created without exception.");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException, "Exception was of incorrect type.");
		}
	}

	@Test
	public void testGetNumSharps() {
		assertEquals(0, KeySignatures.CMAJ_AMIN.getNumberOfSharps());
		assertEquals(1, KeySignatures.GMAJ_EMIN.getNumberOfSharps());
		assertEquals(2, KeySignatures.DMAJ_BMIN.getNumberOfSharps());
	}

	@Test
	public void testGetNumFlats() {
		assertEquals(0, KeySignatures.CMAJ_AMIN.getNumberOfFlats());
		assertEquals(4, KeySignatures.AFLATMAJ_FMIN.getNumberOfFlats());
	}

	@Test
	public void testEquals() {
		assertTrue(KeySignatures.CMAJ_AMIN.equals(KeySignatures.CMAJ_AMIN));
		assertTrue(KeySignatures.EMAJ_CSHARPMIN.equals(KeySignatures.EMAJ_CSHARPMIN));
		assertTrue(KeySignatures.EFLATMAJ_CMIN.equals(KeySignatures.EFLATMAJ_CMIN));

		final KeySignature customSig = KeySignature.of(Arrays.asList(Pitch.Base.C), Arrays.asList(Pitch.Base.B));
		assertTrue(customSig.equals(KeySignature.of(Arrays.asList(Pitch.Base.C), Arrays.asList(Pitch.Base.B))));

		assertFalse(KeySignatures.CMAJ_AMIN.equals(KeySignatures.FMAJ_DMIN));
	}

}
