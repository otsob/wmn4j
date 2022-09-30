/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.search;

import org.wmn4j.mir.Pattern;
import org.wmn4j.mir.PatternPosition;

import java.util.Collection;

/**
 * Represents a search on a score.
 * <p>
 * The type of search results that are produced depend on the algorithm in use.
 * <p>
 * Implementations of this interface are required to be thread-safe.
 */
public interface Search {

	/**
	 * Returns the positions of occurrences of the query pattern.
	 *
	 * @param query the query to search for
	 * @return the positions of all occurrences of the query pattern
	 */
	Collection<PatternPosition> findPositions(Pattern query);

	/**
	 * Returns occurrences of the given query as patterns.
	 *
	 * @param query the query to search for
	 * @return occurrences of the given query as patterns
	 */
	Collection<Pattern> findOccurrences(Pattern query);
}
