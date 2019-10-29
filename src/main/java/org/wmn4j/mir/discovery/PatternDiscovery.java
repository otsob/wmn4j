package org.wmn4j.mir.discovery;

import org.wmn4j.mir.Pattern;
import org.wmn4j.mir.PatternPosition;
import org.wmn4j.notation.Score;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents the result of pattern discovery on score.
 * <p>
 * Pattern discovery is the process of finding musically important patterns,
 * such as patterns that are repeated often, from a score.
 */
public interface PatternDiscovery {

	/**
	 * Returns the repeated patterns found by this pattern discovery.
	 * <p>
	 * Each returned collection of patterns contains all occurrences of the same or similar
	 * pattern found by this pattern discovery.
	 *
	 * @return the repeated patterns found by this pattern discovery
	 */
	default Collection<Collection<Pattern>> getPatterns() {
		Collection<Collection<PatternPosition>> allPatternPositions = getPatternPositions();
		Collection<Collection<Pattern>> allPatterns = new ArrayList<>(allPatternPositions.size());

		final Score score = getScore();

		for (Collection<PatternPosition> positions : allPatternPositions) {
			Collection<Pattern> patterns = new ArrayList<>(positions.size());
			for (PatternPosition position : positions) {
				patterns.add(score.getAt(position));
			}

			allPatterns.add(patterns);
		}

		return allPatterns;
	}

	/**
	 * Returns the positions of the repeated patterns found by this pattern discovery.
	 * <p>
	 * Each returned collection of positions contains all positions of occurrences of the same or similar
	 * pattern found by this pattern discovery.
	 *
	 * @return the repeated patterns found by this pattern discovery
	 */
	Collection<Collection<PatternPosition>> getPatternPositions();

	/**
	 * Returns the score to which this pattern discovery applies.
	 *
	 * @return the score to which this pattern discovery applies
	 */
	Score getScore();
}
