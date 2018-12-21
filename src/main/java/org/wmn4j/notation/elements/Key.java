/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Major and minor keys.
 *
 * @author Otso Björklund
 */
public enum Key {
	C_MAJOR(KeySignatures.CMAJ_AMIN),
	DFLAT_MAJOR(KeySignatures.DFLATMAJ_BFLATMIN),
	D_MAJOR(KeySignatures.DMAJ_BMIN),
	EFLAT_MAJOR(KeySignatures.EFLATMAJ_CMIN),
	E_MAJOR(KeySignatures.EMAJ_CSHARPMIN),
	F_MAJOR(KeySignatures.FMAJ_DMIN),
	FSHARP_MAJOR(KeySignatures.FSHARPMAJ_DSHARPMIN),
	G_MAJOR(KeySignatures.GMAJ_EMIN),
	AFLAT_MAJOR(KeySignatures.AFLATMAJ_FMIN),
	A_MAJOR(KeySignatures.AMAJ_FSHARPMIN),
	BFLAT_MAJOR(KeySignatures.BFLATMAJ_GMIN),
	B_MAJOR(KeySignatures.BMAJ_GSHARPMIN),

	C_MINOR(KeySignatures.EFLATMAJ_CMIN),
	CSHARP_MINOR(KeySignatures.EMAJ_CSHARPMIN),
	D_MINOR(KeySignatures.FMAJ_DMIN),
	DSHARP_MINOR(KeySignatures.FSHARPMAJ_DSHARPMIN),
	E_MINOR(KeySignatures.GMAJ_EMIN),
	F_MINOR(KeySignatures.AFLATMAJ_FMIN),
	FSHARP_MINOR(KeySignatures.AMAJ_FSHARPMIN),
	G_MINOR(KeySignatures.BFLATMAJ_GMIN),
	GSHARP_MINOR(KeySignatures.BMAJ_GSHARPMIN),
	A_MINOR(KeySignatures.CMAJ_AMIN),
	BFLAT_MINOR(KeySignatures.DFLATMAJ_BFLATMIN),
	B_MINOR(KeySignatures.DMAJ_BMIN);

	private final KeySignature keySignature;

	Key(KeySignature keySignature) {
		this.keySignature = keySignature;
	}

	public KeySignature getKeySignature() {
		return this.keySignature;
	}
}
