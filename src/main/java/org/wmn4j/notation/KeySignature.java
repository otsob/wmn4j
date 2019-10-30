/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents a key signature.
 * <p>
 * This class is immutable.
 */
public final class KeySignature {
	private final List<Pitch.Base> sharps;
	private final List<Pitch.Base> flats;

	/**
	 * Returns a key signature with the given sharps and flats. For common key
	 * signatures use the ones defined in {@link KeySignatures}. This is mostly
	 * intended for creating custom key signatures.
	 *
	 * @param sharps the Pitch.Base names that should be raised. For example, for
	 *               G-major this list consists only of Pitch.Base.F.
	 * @param flats  the Pitch.Base names that should be flattened. For example, for
	 *               F-major this list consists only of Pitch.Base.B.
	 * @return a key signature with the given sharps and flats
	 * @throws IllegalArgumentException if the same Pitch.Base is in both sharps and
	 *                                  flats.
	 */
	public static KeySignature of(List<Pitch.Base> sharps, List<Pitch.Base> flats) {
		return new KeySignature(sharps, flats);
	}

	/**
	 * Constructor for KeySignature. For common key signatures use the ones defined
	 * in {@link KeySignatures}. This is mostly intended for creating custom key
	 * signatures.
	 *
	 * @param sharps the Pitch.Base names that should be raised. For example, for
	 *               G-major this list consists only of Pitch.Base.F.
	 * @param flats  the Pitch.Base names that should be flattened. For example, for
	 *               F-major this list consists only of Pitch.Base.B.
	 * @throws IllegalArgumentException if the same Pitch.Base is in both sharps and
	 *                                  flats.
	 */
	private KeySignature(List<Pitch.Base> sharps, List<Pitch.Base> flats) {
		if (sharps != null && !sharps.isEmpty()) {
			this.sharps = new ArrayList<>(sharps);
		} else {
			this.sharps = Collections.emptyList();
		}
		if (flats != null && !flats.isEmpty()) {
			this.flats = new ArrayList<>(flats);
		} else {
			this.flats = Collections.emptyList();
		}

		// Check that there are no conflicts and throw exception if there are.
		for (Pitch.Base sharp : this.sharps) {
			if (this.flats.contains(sharp)) {
				throw new IllegalArgumentException(sharp + " is both in sharps and in flats.");
			}
		}

		for (Pitch.Base flat : this.flats) {
			if (this.sharps.contains(flat)) {
				throw new IllegalArgumentException(flat + " is both in flats and in sharps.");
			}
		}
	}

	/**
	 * Returns the number of sharps in this key signature.
	 *
	 * @return the number of sharps in this key signature
	 */
	public int getSharpCount() {
		return this.sharps.size();
	}

	/**
	 * Returns the number of flats in this key signature.
	 *
	 * @return the number of flats in this key signature
	 */
	public int getFlatCount() {
		return this.flats.size();
	}

	/**
	 * Returns the sharps in this key signature.
	 *
	 * @return the sharps in this key signature.
	 */
	public List<Pitch.Base> getSharps() {
		return new ArrayList<>(this.sharps);
	}

	/**
	 * Returns the flats in this key signature.
	 *
	 * @return the flats in this key signature
	 */
	public List<Pitch.Base> getFlats() {
		return new ArrayList<>(this.flats);
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("KeySig(");
		if (!this.sharps.isEmpty()) {
			strBuilder.append("sharps: ");
			for (Pitch.Base sharp : this.sharps) {
				strBuilder.append(sharp).append("#").append(" ");
			}
		}

		if (!this.flats.isEmpty()) {
			strBuilder.append("flats: ");
			for (Pitch.Base flat : this.flats) {
				strBuilder.append(flat).append("b").append(" ");
			}
		}
		if (strBuilder.charAt(strBuilder.length() - 1) == ' ') {
			strBuilder.deleteCharAt(strBuilder.length() - 1);
		}

		strBuilder.append(")");
		return strBuilder.toString();
	}

	/**
	 * Returns true if this is equal to the given object.
	 *
	 * @param o Object against which this is compared for equality.
	 * @return true if Object o is of class KeySignature and has the same sharps and
	 * flats as this. false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof KeySignature)) {
			return false;
		}

		final KeySignature other = (KeySignature) o;
		return this.sharps.equals(other.sharps) && this.flats.equals(other.flats);
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 41 * hash + Objects.hashCode(this.sharps);
		hash = 41 * hash + Objects.hashCode(this.flats);
		return hash;
	}
}
