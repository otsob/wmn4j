/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import java.util.Objects;

import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Rest;

/**
 * Class for building {@link Rest} objects.
 */
public final class RestBuilder implements DurationalBuilder {

	private Duration duration;

	/**
	 * Constructor that creates a builder with the given duration value.
	 *
	 * @param duration the duration that is initially set to this builder
	 */
	public RestBuilder(Duration duration) {
		setDuration(duration);
	}

	/**
	 * Set the duration in this builder.
	 *
	 * @param duration the duration that is set to this builder
	 */
	public void setDuration(Duration duration) {
		this.duration = Objects.requireNonNull(duration);
	}

	@Override
	public Duration getDuration() {
		return this.duration;
	}

	@Override
	public Rest build() {
		return Rest.getRest(this.duration);
	}
}
