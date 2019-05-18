/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class KeyTest {

	@Test
	public void testGetKeySignature() {
		assertTrue(Key.C_MAJOR.getKeySignature().equals(KeySignatures.CMAJ_AMIN));
		assertTrue(Key.DFLAT_MAJOR.getKeySignature().equals(KeySignatures.DFLATMAJ_BFLATMIN));
		assertTrue(Key.D_MAJOR.getKeySignature().equals(KeySignatures.DMAJ_BMIN));
		assertTrue(Key.EFLAT_MAJOR.getKeySignature().equals(KeySignatures.EFLATMAJ_CMIN));
		assertTrue(Key.E_MAJOR.getKeySignature().equals(KeySignatures.EMAJ_CSHARPMIN));
		assertTrue(Key.F_MAJOR.getKeySignature().equals(KeySignatures.FMAJ_DMIN));
		assertTrue(Key.FSHARP_MAJOR.getKeySignature().equals(KeySignatures.FSHARPMAJ_DSHARPMIN));
		assertTrue(Key.G_MAJOR.getKeySignature().equals(KeySignatures.GMAJ_EMIN));
		assertTrue(Key.AFLAT_MAJOR.getKeySignature().equals(KeySignatures.AFLATMAJ_FMIN));
		assertTrue(Key.A_MAJOR.getKeySignature().equals(KeySignatures.AMAJ_FSHARPMIN));
		assertTrue(Key.BFLAT_MAJOR.getKeySignature().equals(KeySignatures.BFLATMAJ_GMIN));
		assertTrue(Key.B_MAJOR.getKeySignature().equals(KeySignatures.BMAJ_GSHARPMIN));

		assertTrue(Key.C_MINOR.getKeySignature().equals(KeySignatures.EFLATMAJ_CMIN));
		assertTrue(Key.CSHARP_MINOR.getKeySignature().equals(KeySignatures.EMAJ_CSHARPMIN));
		assertTrue(Key.D_MINOR.getKeySignature().equals(KeySignatures.FMAJ_DMIN));
		assertTrue(Key.DSHARP_MINOR.getKeySignature().equals(KeySignatures.FSHARPMAJ_DSHARPMIN));
		assertTrue(Key.E_MINOR.getKeySignature().equals(KeySignatures.GMAJ_EMIN));
		assertTrue(Key.F_MINOR.getKeySignature().equals(KeySignatures.AFLATMAJ_FMIN));
		assertTrue(Key.FSHARP_MINOR.getKeySignature().equals(KeySignatures.AMAJ_FSHARPMIN));
		assertTrue(Key.G_MINOR.getKeySignature().equals(KeySignatures.BFLATMAJ_GMIN));
		assertTrue(Key.GSHARP_MINOR.getKeySignature().equals(KeySignatures.BMAJ_GSHARPMIN));
		assertTrue(Key.A_MINOR.getKeySignature().equals(KeySignatures.CMAJ_AMIN));
		assertTrue(Key.BFLAT_MINOR.getKeySignature().equals(KeySignatures.DFLATMAJ_BFLATMIN));
		assertTrue(Key.B_MINOR.getKeySignature().equals(KeySignatures.DMAJ_BMIN));
	}
}
