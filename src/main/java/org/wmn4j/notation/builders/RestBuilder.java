/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import java.util.Objects;

import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Rest;

/**
 * Class for building <code>Rest</code> objects.
 *
 * @author Otso Björklund
 */
public final class RestBuilder implements DurationalBuilder {

	private Duration duration;

	/**
	 * Create a new instance.
	 *
	 * @param duration The
	 */
	public RestBuilder(Duration duration) {
		setDuration(duration);
	}

	/**
	 * Set the duration of the <code>Rest</code>.
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
