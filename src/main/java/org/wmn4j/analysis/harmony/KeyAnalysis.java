/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.wmn4j.notation.elements.Key;

/**
 * Represents the results of a key analysis.
 * Key analysis implementations can be eager or lazy in the computation
 * of the results. Implementations of this interface are required to be
 * thread-safe.
 */
public interface KeyAnalysis {

	/**
	 * Returns the key of the analysed section of music.
	 *
	 * @return the key of the analysed section of music
	 */
	Key getKey();
}
