/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.search;

import org.wmn4j.representation.geometric.Point;
import org.wmn4j.representation.geometric.PointPattern;
import org.wmn4j.representation.geometric.PointSet;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements a point set pattern matching algorithm that is generic in terms of point dimensionality.
 * Based on the exact matching algorithm of:
 * Esko Ukkonen, Kjell Lemström, and Veli Mäkinen (2003).
 * "Geometric Algorithms for Transposition Invariant Content-Based Music Retrieval", ISMIR 2003.
 */
final class PointSetPatternMatch {

	/**
	 * Returns indices of all time-shifted and transposed matches of the query pattern in the given point-set.
	 *
	 * @param pointSet the point set that is searched
	 * @param query    the query
	 * @param <T>      point type
	 * @return indices of all time-shifted and transposed matches of the query pattern in the given point-set
	 */
	static <T extends Point<T>> List<List<Integer>> findMatches(PointSet<T> pointSet, PointPattern<T> query) {

		List<List<Integer>> matchingIndices = new ArrayList<>();

		for (int i = 0; i < pointSet.size() - query.size(); ++i) {
			final var translator = pointSet.get(i).subtract(query.get(0));
			final var translated = query.translate(translator);

			final List<Integer> indices = new ArrayList<>(translated.size());

			int queryIndex = 0;

			for (int scanIndex = i; scanIndex < pointSet.size(); ++scanIndex) {
				final var queryPoint = translated.get(queryIndex);
				final var point = pointSet.get(scanIndex);
				if (point.equals(queryPoint)) {
					indices.add(scanIndex);
					++queryIndex;
				}

				if (queryPoint.compareTo(point) < 0 || queryIndex >= translated.size()) {
					// If there translated point of the query is lexicographically smaller than the current
					// point, then no matching point can be found from the point-set.
					break;
				}
			}

			if (indices.size() == query.size()) {
				matchingIndices.add(indices);
			}
		}

		return matchingIndices;
	}

	private PointSetPatternMatch() {
		throw new UnsupportedOperationException("Not meant to be instantiated");
	}
}
