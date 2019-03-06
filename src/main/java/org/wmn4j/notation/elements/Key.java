/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Major and minor keys.
 */
public enum Key {
	/**
	 * The C major key.
	 */
	C_MAJOR(KeySignatures.CMAJ_AMIN),

	/**
	 * The Db major key.
	 */
	DFLAT_MAJOR(KeySignatures.DFLATMAJ_BFLATMIN),

	/**
	 * The D major key.
	 */
	D_MAJOR(KeySignatures.DMAJ_BMIN),

	/**
	 * The Eb major key.
	 */
	EFLAT_MAJOR(KeySignatures.EFLATMAJ_CMIN),

	/**
	 * The E major key.
	 */
	E_MAJOR(KeySignatures.EMAJ_CSHARPMIN),

	/**
	 * The F major key.
	 */
	F_MAJOR(KeySignatures.FMAJ_DMIN),

	/**
	 * The F# major key.
	 */
	FSHARP_MAJOR(KeySignatures.FSHARPMAJ_DSHARPMIN),

	/**
	 * The G major key.
	 */
	G_MAJOR(KeySignatures.GMAJ_EMIN),

	/**
	 * The Ab major key.
	 */
	AFLAT_MAJOR(KeySignatures.AFLATMAJ_FMIN),

	/**
	 * The A major key.
	 */
	A_MAJOR(KeySignatures.AMAJ_FSHARPMIN),

	/**
	 * The Bb major key.
	 */
	BFLAT_MAJOR(KeySignatures.BFLATMAJ_GMIN),

	/**
	 * The B major key.
	 */
	B_MAJOR(KeySignatures.BMAJ_GSHARPMIN),

	/**
	 * The c minor key.
	 */
	C_MINOR(KeySignatures.EFLATMAJ_CMIN),

	/**
	 * The c# minor key.
	 */
	CSHARP_MINOR(KeySignatures.EMAJ_CSHARPMIN),

	/**
	 * The d minor key.
	 */
	D_MINOR(KeySignatures.FMAJ_DMIN),

	/**
	 * The d# minor key.
	 */
	DSHARP_MINOR(KeySignatures.FSHARPMAJ_DSHARPMIN),

	/**
	 * The e minor key.
	 */
	E_MINOR(KeySignatures.GMAJ_EMIN),

	/**
	 * The f minor key.
	 */
	F_MINOR(KeySignatures.AFLATMAJ_FMIN),

	/**
	 * The f# minor key.
	 */
	FSHARP_MINOR(KeySignatures.AMAJ_FSHARPMIN),

	/**
	 * The g minor key.
	 */
	G_MINOR(KeySignatures.BFLATMAJ_GMIN),

	/**
	 * The g# minor key.
	 */
	GSHARP_MINOR(KeySignatures.BMAJ_GSHARPMIN),

	/**
	 * The a minor key.
	 */
	A_MINOR(KeySignatures.CMAJ_AMIN),

	/**
	 * The bb minor key.
	 */
	BFLAT_MINOR(KeySignatures.DFLATMAJ_BFLATMIN),

	/**
	 * The b minor key.
	 */
	B_MINOR(KeySignatures.DMAJ_BMIN);

	private final KeySignature keySignature;

	Key(KeySignature keySignature) {
		this.keySignature = keySignature;
	}

	/**
	 * Returns the key signature associated with the key.
	 *
	 * @return the key signature associated with the key
	 */
	public KeySignature getKeySignature() {
		return this.keySignature;
	}
}
