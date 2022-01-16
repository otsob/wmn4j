/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Note;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a pattern consisting of pitch event points.
 * <p>
 * This class is effectively immutable assuming the constructor is correctly used.
 *
 * @param <T> the point type to use for the pattern
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
	 * Returns a point pattern created from the given {@link Pattern}.
	 *
	 * @param pattern the pattern from which the point pattern is created
	 * @return a point pattern created from the given {@link Pattern}
	 */
	public static PointPattern<Point2D> from(Pattern pattern) {
		return new PointPattern<>(patternToPoints(pattern));
	}

	private static List<Point2D> patternToPoints(Pattern pattern) {
		Set<Point2D> points = new HashSet<>();

		for (var voice : pattern.getVoiceNumbers()) {
			double offset = 0.0;
			for (Durational dur : pattern.getVoice(voice)) {

				if (dur instanceof Note) {
					final Point2D point = new Point2D(offset, ((Note) dur).getPitch().toInt());
					points.add(point);
				} else if (dur instanceof Chord) {
					final Chord chord = (Chord) dur;
					for (Note note : chord) {
						final Point2D point = new Point2D(offset, note.getPitch().toInt());
						points.add(point);
					}
				}

				offset += dur.getDuration().toDouble();
			}
		}
		List<Point2D> sortedPoints = new ArrayList<>(points);
		Collections.sort(sortedPoints);
		return sortedPoints;
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
