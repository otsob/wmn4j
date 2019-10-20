/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

/**
 * Vector representation of a note event. NoteEventVectors are
 * ordered lexicographically.
 * <p>
 * This class is immutable.
 */
final class NoteEventVector implements Comparable<NoteEventVector> {

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
	private final double rawOffset;
	private final double roundedOffset;
	private final int pitch;
	private final int part;
	private final int hash;

	NoteEventVector(double offset, int pitch, int part) {
		this.rawOffset = offset;
		/*
		 * This rounding is necessary to ensure that values close to each other
		 * are considered equal and produce the same hash.
		 * This rounding method is slightly flawed and will not work
		 * if greater precision is needed. Due to the limited types of
		 * offsets that duration values should produce, this is expected
		 * to work well enough.
		 */
		this.roundedOffset = Math.round(rawOffset * ROUNDING_FACTOR) / ROUNDING_FACTOR;
		this.pitch = pitch;
		this.part = part;
		this.hash = computeHash();
	}

	double getRoundedOffset() {
		return roundedOffset;
	}

	int getPitch() {
		return pitch;
	}

	int getPart() {
		return part;
	}

	NoteEventVector add(NoteEventVector other) {
		final double offsetSum = rawOffset + other.rawOffset;
		final int pitchSum = pitch + other.getPitch();
		final int partSum = part + other.getPart();

		return new NoteEventVector(offsetSum, pitchSum, partSum);
	}

	NoteEventVector subtract(NoteEventVector other) {
		final double offsetDifference = rawOffset - other.rawOffset;
		final int pitchDifference = pitch - other.getPitch();
		final int partDifference = part - other.getPart();

		return new NoteEventVector(offsetDifference, pitchDifference, partDifference);
	}

	@Override
	public int compareTo(NoteEventVector other) {

		final int offsetComparison = Double.compare(roundedOffset, other.roundedOffset);
		if (offsetComparison != 0) {
			return offsetComparison;
		}

		final int pitchComparison = Integer.compare(pitch, other.getPitch());
		if (pitchComparison != 0) {
			return pitchComparison;
		}

		return Integer.compare(part, other.getPart());
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof NoteEventVector)) {
			return false;
		}

		final NoteEventVector other = (NoteEventVector) o;
		return this.compareTo(other) == 0;
	}

	private int computeHash() {
		/*
		 * Implements the Multilinear family of hash functions
		 * (see Lemire, Daniel and Kaser, Owen: Strongly Universal String Hashing is Fast.
		 * The Computer Journal, 57(11):1624–1638, 2014).
		 */
		long hash = HASH_MULTIPLIER_0;

		final long bits = Double.doubleToRawLongBits(roundedOffset);
		final int firstOffsetPart = (int) (bits >> 32);
		hash += firstOffsetPart * HASH_MULTIPLIER_1;
		final int secondOffsetPart = (int) bits;
		hash += secondOffsetPart * HASH_MULTIPLIER_2;

		hash += pitch * HASH_MULTIPLIER_3;
		hash += part * HASH_MULTIPLIER_4;

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
		strBuilder.append("(").append(roundedOffset).append(separator).append(pitch).append(separator).append(part)
				.append(")");

		return strBuilder.toString();
	}
}
