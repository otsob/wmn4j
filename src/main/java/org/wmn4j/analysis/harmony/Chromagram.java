/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.wmn4j.notation.elements.PitchClass;

import java.text.DecimalFormat;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a chromagram (i.e. pitch class profile) in twelve-tone equal-temperament (TET). A chromagram is a
 * 12-dimensional vector that has a non-negative value associated with each pitch class. Chromagrams can be used
 * for characterising harmony.
 * <p>
 * This class is immutable.
 */
public final class Chromagram {

	private final Map<PitchClass, Double> profile;

	/**
	 * Returns a chromagram with the given mapping of pitch classes to non-negative values.
	 * If a pitch class is not present in the mapping, then the value of that
	 * pitch class defaults to zero.
	 *
	 * @param profile the values for the pitch classes
	 * @return a chromagram with the given mapping of pitch classes to values
	 */
	public static Chromagram of(Map<PitchClass, Double> profile) {
		return new Chromagram(Objects.requireNonNull(profile));
	}

	private Chromagram(Map<PitchClass, Double> profile) {
		if (profile.values().stream().anyMatch(value -> value < 0.0)) {
			throw new IllegalArgumentException("All values in a chromagram must be non-negative.");
		}

		this.profile = new EnumMap(profile);
	}

	/**
	 * Returns the value associated with the given pitch class.
	 *
	 * @param pc pitch class for which the value is returned
	 * @return the value associated with the given pitch class
	 */
	public double getValue(PitchClass pc) {
		return this.profile.getOrDefault(pc, 0.0);
	}

	/**
	 * Returns the correlation between this and the given chromagram. Uses
	 * <a href="http://en.wikipedia.org/wiki/Pearson_correlation_coefficient">
	 * Pearson correlation coefficient</a> formula for a sample. Normalizing the
	 * profiles does not affect correlation.
	 *
	 * @param other the chromagram with which the correlation is returned
	 * @return the correlation between this and the given chromagram
	 */
	public double correlation(Chromagram other) {

		double averageOfA = 0.0;
		double averageOfB = 0.0;

		for (PitchClass pc : PitchClass.values()) {
			averageOfA += getValue(pc);
			averageOfB += other.getValue(pc);
		}

		averageOfA /= PitchClass.values().length;
		averageOfB /= PitchClass.values().length;

		double numerator = 0.0;
		double denomA = 0.0;
		double denomB = 0.0;

		for (PitchClass pc : PitchClass.values()) {
			final double diffA = getValue(pc) - averageOfA;
			final double diffB = other.getValue(pc) - averageOfB;

			numerator += diffA * diffB;
			denomA += Math.pow(diffA, 2.0);
			denomB += Math.pow(diffB, 2.0);
		}

		final double denominator = Math.sqrt(denomA * denomB);
		return numerator / denominator;
	}

	@Override
	public String toString() {
		final DecimalFormat df = new DecimalFormat("0.0000");
		final StringBuilder strBuilder = new StringBuilder();

		for (PitchClass pc : this.profile.keySet()) {
			strBuilder.append(pc).append(": ").append(df.format(this.profile.get(pc))).append(", ");
		}

		strBuilder.replace(strBuilder.length() - 2, strBuilder.length(), "");
		return strBuilder.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Chromagram)) {
			return false;
		}

		Chromagram other = (Chromagram) o;
		return this.profile.equals(other.profile);
	}

	@Override
	public int hashCode() {
		return Objects.hash(profile);
	}
}
