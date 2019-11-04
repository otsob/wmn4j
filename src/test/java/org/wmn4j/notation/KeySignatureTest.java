/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class KeySignatureTest {

	KeySignatureTest() {
	}

	@Test
	void testGetKeySigExceptions() {
		try {
			final List<Pitch.Base> sharps = Arrays.asList(Pitch.Base.C);
			final KeySignature illegalCustomKeySig = KeySignature.of(sharps, sharps);
			fail("A KeySignature with the same note as sharp and flat was created without exception.");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException, "Exception was of incorrect type.");
		}
	}

	@Test
	void testGetNumSharps() {
		assertEquals(0, KeySignatures.CMAJ_AMIN.getSharpCount());
		assertEquals(1, KeySignatures.GMAJ_EMIN.getSharpCount());
		assertEquals(2, KeySignatures.DMAJ_BMIN.getSharpCount());
	}

	@Test
	void testGetNumFlats() {
		assertEquals(0, KeySignatures.CMAJ_AMIN.getFlatCount());
		assertEquals(4, KeySignatures.AFLATMAJ_FMIN.getFlatCount());
	}

	@Test
	void testEquals() {
		assertTrue(KeySignatures.CMAJ_AMIN.equals(KeySignatures.CMAJ_AMIN));
		assertTrue(KeySignatures.EMAJ_CSHARPMIN.equals(KeySignatures.EMAJ_CSHARPMIN));
		assertTrue(KeySignatures.EFLATMAJ_CMIN.equals(KeySignatures.EFLATMAJ_CMIN));

		final KeySignature customSig = KeySignature.of(Arrays.asList(Pitch.Base.C), Arrays.asList(Pitch.Base.B));
		assertTrue(customSig.equals(KeySignature.of(Arrays.asList(Pitch.Base.C), Arrays.asList(Pitch.Base.B))));

		assertFalse(KeySignatures.CMAJ_AMIN.equals(KeySignatures.FMAJ_DMIN));
	}

}
