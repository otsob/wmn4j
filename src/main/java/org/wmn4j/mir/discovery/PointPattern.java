/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.wmn4j.utils.RandomMultipliers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a geometric pattern consisting of points.
 * <p>
 * This class is immutable.
 */
final class PointPattern implements Iterable<NoteEventVector> {

	private static final PointPattern EMPTY = new PointPattern(Collections.emptyList());

	private final List<NoteEventVector> points;
	private final int hash;

	/**
	 * Constructor that takes ownership of the points passed to this.
	 *
	 * @param points the points that the new pattern contains
	 */
	PointPattern(List<NoteEventVector> points) {
		this.points = points;
		this.hash = computeHash();
	}

	int size() {
		return this.points.size();
	}

	NoteEventVector get(int index) {
		return this.points.get(index);
	}

	/**
	 * Returns the vectorized representation of this pattern.
	 * The vectorized representation consists of the difference
	 * vectors between consecutive points.
	 *
	 * @return the vectorized representation of this pattern
	 */
	PointPattern vectorized() {

		if (size() < 2) {
			return EMPTY;
		}

		final List<NoteEventVector> vecPoints = new ArrayList<>();

		for (int i = 1; i < this.points.size(); ++i) {
			vecPoints.add(this.points.get(i).subtract(this.points.get(i - 1)));
		}

		return new PointPattern(vecPoints);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof PointPattern)) {
			return false;
		}

		final PointPattern other = (PointPattern) o;
		if (this.size() != other.size()) {
			return false;
		}

		return this.points.equals(other.points);
	}

	private int computeHash() {
		/*
		 * Implements the Multilinear family of hash functions
		 * (see Lemire, Daniel and Kaser, Owen: Strongly Universal String Hashing is Fast.
		 * The Computer Journal, 57(11):1624–1638, 2014).
		 */
		int multiplierIndex = 0;
		long hash = RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);

		for (NoteEventVector point : this.points) {
			final long bits = Double.doubleToRawLongBits(point.getRoundedOffset());
			final int firstOffsetPart = (int) (bits >> 32);
			hash += firstOffsetPart * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);
			final int secondOffsetPart = (int) bits;
			hash += secondOffsetPart * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);

			hash += point.getPitch() * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);
			hash += point.getPart() * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);
		}

		return (int) hash;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public Iterator<NoteEventVector> iterator() {
		return points.iterator();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(", ", points.stream().map(NoteEventVector::toString).collect(Collectors.toList())));
		builder.append("}");

		return builder.toString();
	}

}
