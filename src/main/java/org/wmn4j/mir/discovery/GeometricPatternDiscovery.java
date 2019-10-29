/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.wmn4j.mir.PatternPosition;
import org.wmn4j.notation.Score;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Implements repeated pattern discovery using point set representation of
 * music and compression ratio of repeated patterns with the SIATECHF algorithm [1].
 * <p>
 * [1] Björklund, Otso.
 * Improving the running time of repeated pattern discovery in multidimensional representations of music.
 * Master's thesis, University of Helsinki. 2015.
 * https://helda.helsinki.fi/handle/10138/273479
 * <p>
 * NOTE: The current implementation of GeometricPatternDiscovery is not thread-safe. The SIATECHF algorithm
 * also has quadratic space complexity so running this on large scores will require a very large heap size.
 */
public final class GeometricPatternDiscovery implements PatternDiscovery {

	private final Collection<Collection<PatternPosition>> positions;
	private final Score score;

	/**
	 * Returns the results of running pattern discovery with SIATECHF algorithm.
	 * <p>
	 * SIATECHF works by constructing a point set representation of the given score and
	 * finding maximal translatable patterns and their translational equivalence classes.
	 * Only those equivalence classes are returned whose compression ratio exceeds the given value.
	 * <p>
	 * Compression ratio is the number of notes covered by the occurrences of the pattern divided the number of points
	 * required to represent all the pattern occurrences. Patterns whose occurrences have little overlap, consists
	 * of multiple notes and occur frequently have high compression ratio values. It is recommended to avoid
	 * values less than 2.0 for compression ratio as otherwise a large number of uninteresting repeated patterns
	 * will be returned. Using compression ratio 0.0 will return all translational equivalence classes in
	 * the point set representation and values smaller than 0.0 are not defined.
	 *
	 * @param score            the score for which repeated pattern discovery is performed
	 * @param compressionRatio the required non-negative minimum compression ratio for the returned patterns
	 * @return the results of running pattern discovery with SIATECH algorithm
	 */
	public static PatternDiscovery withSiatechf(Score score, double compressionRatio) {
		if (compressionRatio < 0.0) {
			throw new IllegalArgumentException("Compression ratio must be non-negative, was " + compressionRatio);
		}

		final PointSet pointSet = new PointSet(score);
		final Collection<Tec> tecs = Siatechf.computeMtpTecs(pointSet, compressionRatio);
		final Collection<Collection<PatternPosition>> allPatterns = new ArrayList<>(tecs.size());
		for (Tec tec : tecs) {
			final Collection<PatternPosition> patternPositions = new ArrayList<>(tec.getTranslators().size() + 1);

			final PointPattern pattern = tec.getPattern();

			for (NoteEventVector translator : tec.getTranslators()) {
				patternPositions.add(pointSet.getPosition(pattern, translator));
			}

			allPatterns.add(patternPositions);
		}

		return new GeometricPatternDiscovery(allPatterns, score);
	}

	private GeometricPatternDiscovery(Collection<Collection<PatternPosition>> positions, Score score) {
		this.positions = positions;
		this.score = score;
	}

	@Override
	public Collection<Collection<PatternPosition>> getPatternPositions() {
		return positions;
	}

	@Override
	public Score getScore() {
		return score;
	}
}
