/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;

/**
 * Interface for builders that build {@link Durational} objects.
 */
public interface DurationalBuilder {

	/**
	 * Returns a durational notation element with the values set in the builder.
	 *
	 * @return a durational notation element with the values set in the builder
	 */
	Durational build();

	/**
	 * Returns the duration set in this builder.
	 *
	 * @return the duration set in this builder
	 */
	Duration getDuration();
}
