/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Represents builders that can be used for building ornamental notes and ornamental chords.
 */
public interface OrnamentalBuilder {

	/**
	 * Returns an ornamental note or chord from the contents of this builder.
	 *
	 * @return an ornamental note or chord from the contents of this builder
	 */
	Ornamental build();
}
