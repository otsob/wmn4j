/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import org.wmn4j.utils.RandomMultipliers;

/**
 * A 2-dimensional note event point with double-type components.
 */
public final class Point2D implements Point<Point2D> {

	private static final long HASH_MULTIPLIER_0 = RandomMultipliers.INSTANCE.getMultiplier(0);
	private static final long HASH_MULTIPLIER_1 = RandomMultipliers.INSTANCE.getMultiplier(1);
	private static final long HASH_MULTIPLIER_2 = RandomMultipliers.INSTANCE.getMultiplier(2);
	private static final long HASH_MULTIPLIER_3 = RandomMultipliers.INSTANCE.getMultiplier(3);
	private static final long HASH_MULTIPLIER_4 = RandomMultipliers.INSTANCE.getMultiplier(4);

	private static final int ONSET_PLACES = 8;
	private static final double ROUNDING_FACTOR = Math.pow(10, ONSET_PLACES);

	/**
	 * The raw onset value is only used for computations internally. Comparisons and
	 * hashes use the rounded onset value.
	 */
	private final double rawOnset;
	private final double roundedOnset;
	private final double pitch;
	private final int hash;

	/**
	 * Constructs a new Point2D.
	 *
	 * @param onset the onset time of a note event
	 * @param pitch the pitch number of a note event
	 */
	public Point2D(double onset, double pitch) {
		this.rawOnset = onset;
		/*
		 * This rounding is necessary to ensure that values close to each other
		 * are considered equal and produce the same hash.
		 * This rounding method is slightly flawed and will not work
		 * if greater precision is needed. Due to the limited types of
		 * onsets that duration values should produce, this is expected
		 * to work well enough.
		 */
		this.roundedOnset = Math.round(rawOnset * ROUNDING_FACTOR) / ROUNDING_FACTOR;
		this.pitch = pitch;
		this.hash = computeHash();
	}

	/**
	 * Returns the number representing the pitch dimension.
	 *
	 * @return the number representing the pitch dimension
	 */
	public double getPitch() {
		return pitch;
	}

	/**
	 * Returns the number representing onset dimension.
	 *
	 * @return the number representing onset dimension
	 */
	public double getOnset() {
		return roundedOnset;
	}

	@Override
	public int getDimensionality() {
		return 2;
	}

	@Override
	public Point2D add(Point2D other) {
		final double onsetSum = rawOnset + other.rawOnset;
		final double pitchSum = pitch + other.pitch;

		return new Point2D(onsetSum, pitchSum);
	}

	@Override
	public Point2D subtract(Point2D other) {
		final double onsetDifference = rawOnset - other.rawOnset;
		final double pitchDifference = pitch - other.pitch;

		return new Point2D(onsetDifference, pitchDifference);
	}

	@Override
	public int compareTo(Point2D other) {

		final int onsetComparison = Double.compare(roundedOnset, other.roundedOnset);
		if (onsetComparison != 0) {
			return onsetComparison;
		}

		return Double.compare(pitch, other.pitch);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof Point2D)) {
			return false;
		}

		final Point2D other = (Point2D) o;
		return this.compareTo(other) == 0;
	}

	private int computeHash() {
		/*
		 * Implements the Multilinear family of hash functions
		 * (see Lemire, Daniel and Kaser, Owen: Strongly Universal String Hashing is Fast.
		 * The Computer Journal, 57(11):1624-1638, 2014).
		 */
		long hash = HASH_MULTIPLIER_0;

		final long onsetBits = Double.doubleToRawLongBits(roundedOnset);
		final int firstonsetPart = (int) (onsetBits >> 32);
		hash += firstonsetPart * HASH_MULTIPLIER_1;
		final int secondonsetPart = (int) onsetBits;
		hash += secondonsetPart * HASH_MULTIPLIER_2;

		final long pitchBits = Double.doubleToRawLongBits(roundedOnset);
		final int firstPitchPart = (int) (pitchBits >> 32);
		hash += firstPitchPart * HASH_MULTIPLIER_3;
		final int secondPitchPart = (int) pitchBits;
		hash += secondPitchPart * HASH_MULTIPLIER_4;

		return (int) hash;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		final String separator = ", ";
		strBuilder.append("(").append(roundedOnset).append(separator).append(pitch).append(")");

		return strBuilder.toString();
	}
}
