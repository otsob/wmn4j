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
	public static final Clef G = Clef.getClef(Clef.Type.G, 2);

	/**
	 * The basic F clef.
	 */
	public static final Clef F = Clef.getClef(Clef.Type.F, 4);

	/**
	 * The basic alto clef.
	 */
	public static final Clef ALTO = Clef.getClef(Clef.Type.C, 3);

	/**
	 * The basic percussion clef.
	 */
	public static final Clef PERCUSSION = Clef.getClef(Clef.Type.PERCUSSION, 3);

	private Clefs() {
		// Not meant to be instantiated
		throw new AssertionError();
	}
}
