/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.wmn4j.representation.geometric.Point;
import org.wmn4j.representation.geometric.PointPattern;
import org.wmn4j.representation.geometric.PointSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the SIATECHF algorithm.
 */
final class Siatechf {

	/**
	 * Utility class for keeping track of a pair of indices.
	 */
	private static class IndexPair {
		private final int first;
		private final int second;

		IndexPair(int first, int second) {
			this.first = first;
			this.second = second;
		}

		int getFirst() {
			return first;
		}

		int getSecond() {
			return second;
		}
	}

	/**
	 * Returns all TECs in the given point set whose compression ratio exceeds the
	 * given minimum.
	 *
	 * @param pointSet            the point set for which tecs are computed
	 * @param minCompressionRatio the minimum compression ratio of the given TECs
	 * @return all TECs in the given point set whose compression ratio exceeds the
	 * given minimum
	 */
	static <T extends Point<T>> List<Tec<T>> computeMtpTecs(PointSet<T> pointSet, double minCompressionRatio) {

		final Map<T, List<IndexPair>> mtpMap = computeMtpMap(pointSet);

		final List<Tec<T>> tecs = new ArrayList<>();
		final Set<PointPattern<T>> vectorizedPatterns = new HashSet<>();

		for (T difference : mtpMap.keySet()) {
			final PointPattern<T> pattern = computeMtp(mtpMap.get(difference), pointSet);
			final PointPattern<T> vectorizedPattern = pattern.vectorized();

			if (!vectorizedPatterns.contains(vectorizedPattern)) {
				if (upperBoundOnCompressionRatio(pattern, mtpMap) >= minCompressionRatio) {
					final List<T> translators = findTranslators(pattern, mtpMap, pointSet);
					if (compressionRatio(pattern, translators) >= minCompressionRatio) {
						tecs.add(new Tec<>(pattern, translators));
					}
				}
				vectorizedPatterns.add(vectorizedPattern);
			}
		}

		return tecs;
	}

	private static <T extends Point<T>> PointPattern<T> computeMtp(List<IndexPair> mtpIndexPairs,
			PointSet<T> pointSet) {
		final List<T> patternPoints = new ArrayList<>();
		for (IndexPair indexPair : mtpIndexPairs) {
			patternPoints.add(pointSet.get(indexPair.getFirst()));
		}

		return new PointPattern<>(patternPoints);
	}

	private static <T extends Point<T>> Map<T, List<IndexPair>> computeMtpMap(PointSet<T> pointSet) {
		final Map<T, List<IndexPair>> mtpMap = new HashMap<>();

		for (int i = 0; i < pointSet.size() - 1; ++i) {

			final T origin = pointSet.get(i);

			for (int j = i + 1; j < pointSet.size(); ++j) {
				final T diff = pointSet.get(j).subtract(origin);

				if (!mtpMap.containsKey(diff)) {
					mtpMap.put(diff, new ArrayList<>());
				}

				mtpMap.get(diff).add(new IndexPair(i, j));
			}
		}

		return mtpMap;
	}

	private static <T extends Point<T>> List<T> findTranslators(PointPattern<T> pattern,
			Map<T, List<IndexPair>> mtpMap, PointSet<T> pointSet) {

		if (pattern.size() == 1) {
			final List<T> translators = new ArrayList<>();
			for (int i = 0; i < pointSet.size(); ++i) {
				translators.add(pointSet.get(i).subtract(pattern.get(0)));
			}

			return translators;
		}

		List<Integer> targetIndices = new ArrayList<>();
		final PointPattern<T> vectorizedPattern = pattern.vectorized();

		for (IndexPair indexPair : mtpMap.get(vectorizedPattern.get(0))) {
			targetIndices.add(indexPair.getSecond());
		}

		for (int i = 1; i < vectorizedPattern.size(); ++i) {
			final List<IndexPair> indexPairs = mtpMap.get(vectorizedPattern.get(i));
			final List<Integer> newTargetIndices = new ArrayList<>();

			int j = 0;
			int k = 0;

			while (j < targetIndices.size() && k < indexPairs.size()) {
				if (targetIndices.get(j).equals(indexPairs.get(k).getFirst())) {
					newTargetIndices.add(indexPairs.get(k).getSecond());
					++j;
					++k;
				} else if (targetIndices.get(j) < indexPairs.get(k).getFirst()) {
					++j;
				} else if (targetIndices.get(j) > indexPairs.get(k).getFirst()) {
					++k;
				}
			}

			targetIndices = newTargetIndices;
		}

		final List<T> translators = new ArrayList<>();
		final T lastPoint = pattern.get(pattern.size() - 1);

		for (Integer i : targetIndices) {
			translators.add(pointSet.get(i).subtract(lastPoint));
		}

		return translators;
	}

	/**
	 * Returns an upper bound on the upper bound of the compression ratio of the pattern
	 * based on the number of translatable points.
	 *
	 * @param pattern the pattern for which the upper bound is computed
	 * @param mtpMap  the map of MTPs index pairs in the input point set
	 * @return an upper bound on the upper bound of the compression ratio of the pattern
	 */
	private static <T extends Point<T>> double upperBoundOnCompressionRatio(PointPattern<T> pattern,
			Map<T, List<IndexPair>> mtpMap) {

		final int patternSize = pattern.size();
		if (patternSize == 1) {
			return 1.0;
		}

		final double occurrenceUpperBound = mtpMap.get(pattern.get(patternSize - 1).subtract(pattern.get(0))).size();
		final double coverageUpperBound = occurrenceUpperBound * patternSize;

		return coverageUpperBound / (patternSize + occurrenceUpperBound - 1);
	}

	/**
	 * Returns the compression ratio of the TEC for the given pattern and translators.
	 *
	 * @param pattern     the pattern
	 * @param translators the translators (including the zero vector).
	 * @return the compression ratio of the TEC for the given pattern and translators
	 */
	private static <T extends Point<T>> double compressionRatio(PointPattern<T> pattern, List<T> translators) {
		final Set<T> coveredSet = new HashSet<>(pattern.size() + translators.size());

		for (T translator : translators) {
			for (T point : pattern) {
				coveredSet.add(point.add(translator));
			}
		}

		return (double) coveredSet.size() / (pattern.size() + translators.size() - 1);
	}

	private Siatechf() {
		// Not meant to be instantiated
		throw new AssertionError();
	}
}
