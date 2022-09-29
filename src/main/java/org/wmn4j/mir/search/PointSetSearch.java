/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.search;

import org.wmn4j.mir.Pattern;
import org.wmn4j.mir.PatternPosition;
import org.wmn4j.notation.Score;
import org.wmn4j.representation.geometric.Point2D;
import org.wmn4j.representation.geometric.PointPattern;
import org.wmn4j.representation.geometric.PointSet;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Implements a search using point-set pattern matching.
 * <p>
 * Based on the exact matching algorithm presented in:
 * Esko Ukkonen, Kjell Lemström, and Veli Mäkinen:
 * "Geometric Algorithms for Transposition Invariant Content-Based Music Retrieval", ISMIR 2003.
 * <p>
 * This class is immutable.
 */
public final class PointSetSearch implements Search {

	private final Score score;
	private final PointSet<Point2D> pointSet;

	/**
	 * Returns a search instance for the given score.
	 *
	 * @param score the score for which to create the search
	 * @return a search instance for the given score
	 */
	public static PointSetSearch of(Score score) {
		return new PointSetSearch(score);
	}

	private PointSetSearch(Score score) {
		this.score = Objects.requireNonNull(score);
		this.pointSet = PointSet.from(score);
	}

	@Override
	public Collection<PatternPosition> findPositions(Pattern query) {
		final var indices = PointSetPatternMatch.findMatches(pointSet, PointPattern.from(query));
		return indices.stream().map(indexList -> new PatternPosition(
						indexList.stream().map(index -> pointSet.getPosition(index)).collect(Collectors.toList())))
				.collect(Collectors.toList());
	}

	@Override
	public Collection<Pattern> findOccurrences(Pattern query) {
		final var positions = findPositions(query);
		return positions.stream().map(position -> position.getFrom(score)).collect(Collectors.toList());
	}
}
