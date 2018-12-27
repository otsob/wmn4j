/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.pattern_discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Geometric algorithms for repeated pattern discovery.
 */
final class PatternAlgorithms {

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

	// Implements SIATECH
	static List<TEC> computeTecs(PointSet dataset) {

		dataset.sortLexicographically();
		final Map<NoteEventVector, List<IndexPair>> mtpMap = new HashMap<>();

		for (int i = 0; i < dataset.size() - 1; ++i) {

			final NoteEventVector origin = dataset.get(i);

			for (int j = i + 1; j < dataset.size(); ++j) {
				final NoteEventVector diff = dataset.get(j).subtract(origin);

				if (!mtpMap.containsKey(diff)) {
					mtpMap.put(diff, new ArrayList<>());
				}

				mtpMap.get(diff).add(new IndexPair(i, j));
			}
		}

		final List<TEC> tecs = new ArrayList<>();
		final Set<PointPattern> patternVecs = new HashSet<>();

		for (NoteEventVector diff : mtpMap.keySet()) {
			final List<IndexPair> indexPairs = mtpMap.get(diff);

			final List<NoteEventVector> patternPoints = new ArrayList<>();
			for (IndexPair indexPair : indexPairs) {
				patternPoints.add(dataset.get(indexPair.getFirst()));
			}

			final PointPattern pattern = new PointPattern(patternPoints);
			final PointPattern vec = pattern.getVectorizedRepresentation();

			if (!patternVecs.contains(vec)) {
				List<NoteEventVector> translators = new ArrayList<>();
				if (pattern.getPoints().size() == 1) {
					for (int i = 0; i < dataset.size(); ++i) {
						translators.add(pattern.getPoints().get(0).subtract(dataset.get(i)));
					}
				} else {
					translators = findTranslators(pattern, mtpMap, dataset);
				}

				tecs.add(new TEC(pattern, translators));
				patternVecs.add(vec);
			}
		}

		return tecs;
	}

	static List<NoteEventVector> findTranslators(PointPattern pattern,
			Map<NoteEventVector, List<IndexPair>> mtpMap, PointSet dataset) {
		List<Integer> pointIndices = new ArrayList<>();
		final List<NoteEventVector> vecPatternPoints = pattern.getVectorizedRepresentation().getPoints();
		NoteEventVector vec = vecPatternPoints.get(0);

		for (IndexPair indexPair : mtpMap.get(vec)) {
			pointIndices.add(indexPair.getSecond());
		}
		for (int i = 1; i < vecPatternPoints.size(); ++i) {
			vec = vecPatternPoints.get(i);
			final List<IndexPair> indexPairs = mtpMap.get(vec);
			final List<Integer> tmpPointIndices = new ArrayList<>();
			int j = 0;
			int k = 0;
			while (j < pointIndices.size() && k < indexPairs.size()) {
				if (pointIndices.get(j).equals(indexPairs.get(k).getFirst())) {
					tmpPointIndices.add(indexPairs.get(k).getSecond());
					++j;
					++k;
				} else if (pointIndices.get(j) < indexPairs.get(k).getFirst()) {
					++j;
				} else if (pointIndices.get(j) > indexPairs.get(k).getFirst()) {
					++k;
				}
			}
			pointIndices = tmpPointIndices;
		}

		final List<NoteEventVector> translators = new ArrayList<>();
		final NoteEventVector lastPoint = pattern.getPoints().get(pattern.getPoints().size() - 1);

		for (Integer i : pointIndices) {
			translators.add(dataset.get(i).subtract(lastPoint));
		}

		return translators;
	}

	private PatternAlgorithms() {
		// Not meant to be instantiated
		throw new AssertionError();
	}
}
