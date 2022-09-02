/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Objects;

/**
 * Represents the spelled out name of a pitch.
 * Pitch names differ from {@link PitchClass} in that enharmonically equivalent
 * pitches are *not* equal as pitch names. Unlike {@link Pitch}, pitch names do
 * not contain octave information.
 * <p>
 * This class is immutable.
 */
public final class PitchName {

	private final Pitch.Base base;
	private final Pitch.Accidental accidental;

	/**
	 * Returns a pitch name with the given base letter and accidental.
	 *
	 * @param base       the pitch base name
	 * @param accidental the accidental of the pitch name
	 * @return a pitch name with the given base letter and accidental
	 */
	public static PitchName of(Pitch.Base base, Pitch.Accidental accidental) {
		return new PitchName(base, accidental);
	}

	private PitchName(Pitch.Base base, Pitch.Accidental accidental) {
		this.base = Objects.requireNonNull(base);
		this.accidental = Objects.requireNonNull(accidental);
	}

	/**
	 * Returns the base letter of this pitch name.
	 *
	 * @return the base letter of this pitch name
	 */
	public Pitch.Base getBase() {
		return base;
	}

	/**
	 * Returns the accidental of this pitch name.
	 *
	 * @return the accidental of this pitch name
	 */
	public Pitch.Accidental getAccidental() {
		return accidental;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		PitchName pitchName = (PitchName) o;
		return base == pitchName.base && accidental == pitchName.accidental;
	}

	@Override
	public int hashCode() {
		return Objects.hash(base, accidental);
	}

	@Override
	public String toString() {
		StringBuilder pitchName = new StringBuilder();
		pitchName.append(base);

		final int alter = accidental.getAlterationInt();
		if (alter >= 0) {
			for (int i = 0; i < alter; ++i) {
				pitchName.append("#");
			}
		} else {
			for (int i = 0; i > alter; --i) {
				pitchName.append("b");
			}
		}

		return pitchName.toString();
	}
}
