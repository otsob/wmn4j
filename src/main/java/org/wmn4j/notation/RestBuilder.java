/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Objects;

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
	 * Constructor that creates a builder with the duration of the given rest.
	 *
	 * @param rest the rest to whose duration this builder is set
	 */
	public RestBuilder(Rest rest) {
		setDuration(rest.getDuration());
	}

	/**
	 * Set the duration in this builder.
	 *
	 * @param duration the duration that is set to this builder
	 */
	@Override
	public void setDuration(Duration duration) {
		this.duration = Objects.requireNonNull(duration);
	}

	@Override
	public Duration getDuration() {
		return this.duration;
	}

	@Override
	public Rest build() {
		return Rest.of(this.duration);
	}
}
