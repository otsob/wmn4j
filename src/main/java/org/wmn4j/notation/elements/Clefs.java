/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Common clefs.
 */
public final class Clefs {
	/**
	 * The basic G clef.
	 */
	public static final Clef G = Clef.getClef(Clef.Symbol.G, 2);

	/**
	 * The basic F clef.
	 */
	public static final Clef F = Clef.getClef(Clef.Symbol.F, 4);

	/**
	 * The basic alto clef.
	 */
	public static final Clef ALTO = Clef.getClef(Clef.Symbol.C, 3);

	/**
	 * The basic percussion clef.
	 */
	public static final Clef PERCUSSION = Clef.getClef(Clef.Symbol.PERCUSSION, 3);

	private Clefs() {
		// Not meant to be instantiated
		throw new AssertionError();
	}
}
