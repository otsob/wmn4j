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

	private static final int OFFSET_PLACES = 8;
	private static final double ROUNDING_FACTOR = Math.pow(10, OFFSET_PLACES);

	/**
	 * The raw offset value is only used for computations internally. Comparisons and
	 * hashes use the rounded offset value.
	 */
	private final double rawOnset;
	private final double roundedOnset;
	private final double pitch;
	private final int hash;

	public Point2D(double offset, double pitch) {
		this.rawOnset = offset;
		/*
		 * This rounding is necessary to ensure that values close to each other
		 * are considered equal and produce the same hash.
		 * This rounding method is slightly flawed and will not work
		 * if greater precision is needed. Due to the limited types of
		 * offsets that duration values should produce, this is expected
		 * to work well enough.
		 */
		this.roundedOnset = Math.round(rawOnset * ROUNDING_FACTOR) / ROUNDING_FACTOR;
		this.pitch = pitch;
		this.hash = computeHash();
	}

	@Override
	public int getDimensionality() {
		return 2;
	}

	@Override
	public Point2D add(Point2D other) {
		final double offsetSum = rawOnset + other.rawOnset;
		final double pitchSum = pitch + other.pitch;

		return new Point2D(offsetSum, pitchSum);
	}

	@Override
	public Point2D subtract(Point2D other) {
		final double offsetDifference = rawOnset - other.rawOnset;
		final double pitchDifference = pitch - other.pitch;

		return new Point2D(offsetDifference, pitchDifference);
	}

	@Override
	public int compareTo(Point2D other) {

		final int offsetComparison = Double.compare(roundedOnset, other.roundedOnset);
		if (offsetComparison != 0) {
			return offsetComparison;
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

		final long offsetBits = Double.doubleToRawLongBits(roundedOnset);
		final int firstOffsetPart = (int) (offsetBits >> 32);
		hash += firstOffsetPart * HASH_MULTIPLIER_1;
		final int secondOffsetPart = (int) offsetBits;
		hash += secondOffsetPart * HASH_MULTIPLIER_2;

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
