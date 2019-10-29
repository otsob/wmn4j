/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Objects;

/**
 * Represents a clef. Clefs have a symbol which tells the shape of the clef and
 * position which is the line on which the center of the clef is situated. For
 * example, the center of a G-symbol clef is the part of the clef that denotes
 * G4.
 */
public final class Clef {

	/**
	 * Represents the type of clef symbol.
	 */
	public enum Symbol {
		/**
		 * The G clef.
		 */
		G,

		/**
		 * The F clef.
		 */
		F,

		/**
		 * The C clef.
		 */
		C,

		/**
		 * The percussion clef.
		 */
		PERCUSSION
	}

	private final Symbol symbol;
	// The the center of the clef counted from bottom.
	private final int line;

	/**
	 * Returns an instance with the given symbol at the given line.
	 *
	 * @throws IllegalArgumentException if line is smaller than 1
	 * @throws NullPointerException     if symbol is null
	 * @param symbol the symbol of the clef
	 * @param line   counting from the bottom line, the line on which the clef is
	 *               centered
	 * @return a clef with the specified properties.
	 */
	public static Clef of(Symbol symbol, int line) {

		if (line < 1) {
			throw new IllegalArgumentException("line is smaller than 1");
		}

		return new Clef(Objects.requireNonNull(symbol), line);
	}

	/**
	 * Returns the symbol of this clef.
	 * @return the symbol of this clef
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * Returns the line of the staff (counted from bottom up) on which the clef is.
	 * @return the line of the staff on which the clef is.
	 */
	public int getLine() {
		return line;
	}

	private Clef(Symbol symbol, int line) {
		this.symbol = symbol;
		this.line = line;
	}

	@Override
	public String toString() {
		return this.symbol + "-clef(" + this.line + ")";
	}

	/**
	 * Returns true if this clef is equal to the given object.
	 *
	 * @param o the Object against which this is compared for equality
	 * @return true if o is an instance of Clef and has the same symbol and position
	 *         as this
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Clef)) {
			return false;
		}

		final Clef other = (Clef) o;
		return this.symbol == other.symbol && this.line == other.line;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 71 * hash + Objects.hashCode(this.symbol);
		hash = 71 * hash + this.line;
		return hash;
	}
}
