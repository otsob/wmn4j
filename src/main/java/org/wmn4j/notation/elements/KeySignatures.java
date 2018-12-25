/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.Arrays;

/**
 * Collection of typical key signatures associated with major and minor keys.
 *
 * @author Otso Björklund
 */
public final class KeySignatures {
	/**
	 * The key signature for C major or a minor.
	 */
	public static final KeySignature CMAJ_AMIN = new KeySignature(null, null);

	/**
	 * The key signature for G major or e minor.
	 */
	public static final KeySignature GMAJ_EMIN = new KeySignature(Arrays.asList(Pitch.Base.F), null);

	/**
	 * The key signature for D major or b minor.
	 */
	public static final KeySignature DMAJ_BMIN = new KeySignature(Arrays.asList(Pitch.Base.F, Pitch.Base.C), null);

	/**
	 * The key signature for A major or f# minor.
	 */
	public static final KeySignature AMAJ_FSHARPMIN = new KeySignature(
			Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G), null);

	/**
	 * The key signature for E major or c# minor.
	 */
	public static final KeySignature EMAJ_CSHARPMIN = new KeySignature(
			Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D), null);

	/**
	 * The key signature for B major or g# minor.
	 */
	public static final KeySignature BMAJ_GSHARPMIN = new KeySignature(
			Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D, Pitch.Base.A), null);

	/**
	 * The key signature for F# major or d# minor.
	 */
	public static final KeySignature FSHARPMAJ_DSHARPMIN = new KeySignature(
			Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D, Pitch.Base.A, Pitch.Base.E), null);

	/**
	 * The key signature for F major or d minor.
	 */
	public static final KeySignature FMAJ_DMIN = new KeySignature(null, Arrays.asList(Pitch.Base.B));

	/**
	 * The key signature for Bb major or g minor.
	 */
	public static final KeySignature BFLATMAJ_GMIN = new KeySignature(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E));

	/**
	 * The key signature for Eb major or c minor.
	 */
	public static final KeySignature EFLATMAJ_CMIN = new KeySignature(null,
			Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A));

	/**
	 * The key signature for Ab major or f minor.
	 */
	public static final KeySignature AFLATMAJ_FMIN = new KeySignature(null,
			Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D));

	/**
	 * The key signature for Db major or bb minor.
	 */
	public static final KeySignature DFLATMAJ_BFLATMIN = new KeySignature(null,
			Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D, Pitch.Base.G));

	/**
	 * The key signature for Gb major or eb minor.
	 */
	public static final KeySignature GFLATMAJ_EFLATMIN = new KeySignature(null,
			Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D, Pitch.Base.G, Pitch.Base.C));

	private KeySignatures() {
		// Not meant to be instantiated.
		throw new AssertionError();
	}
}
