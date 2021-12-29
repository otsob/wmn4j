/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents a pattern consisting of pitch event points.
 * <p>
 * This class is effectively immutable assuming the constructor is correctly used.
 */
public final class PointPattern<T extends Point<T>> implements Iterable<T> {

	private final List<T> points;
	private final int hash;

	/**
	 * Constructor that takes ownership of the points passed to this.
	 * NOTE: No other part of code should use the given list of points after
	 * a call to this constructor.
	 *
	 * @param points the points that the new pattern contains
	 */
	public PointPattern(List<T> points) {
		this.points = points;
		this.hash = computeHash();
	}

	/**
	 * Returns the number of points in this pattern.
	 *
	 * @return the number of points in this pattern
	 */
	public int size() {
		return this.points.size();
	}

	/**
	 * Returns the point at the given index.
	 *
	 * @param index index from which point is returned
	 * @return the point at the given index
	 */
	public T get(int index) {
		return this.points.get(index);
	}

	/**
	 * Returns the vectorized representation of this pattern.
	 * The vectorized representation consists of the difference
	 * vectors between consecutive points.
	 *
	 * @return the vectorized representation of this pattern
	 */
	public PointPattern<T> vectorized() {

		if (size() < 2) {
			return new PointPattern<T>(Collections.emptyList());
		}

		final List<T> vecPoints = new ArrayList<>();

		for (int i = 1; i < this.points.size(); ++i) {
			vecPoints.add(this.points.get(i).subtract(this.points.get(i - 1)));
		}

		return new PointPattern<>(vecPoints);
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
		return Objects.hash(this.points);
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public Iterator<T> iterator() {
		return points.iterator();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		builder.append(String.join(", ", points.stream().map(T::toString).collect(Collectors.toList())));
		builder.append("}");

		return builder.toString();
	}
}
