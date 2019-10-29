/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Objects;

/**
 * Represents a rest.
 * <p>
 * This class is immutable.
 */
public final class Rest implements Durational {
	private final Duration duration;

	/**
	 * Returns a rest with the given duration.
	 *
	 * @param duration the duration of the rest
	 * @return Rest with specified duration
	 * @throws NullPointerException if duration is null
	 */
	public static Rest of(Duration duration) {

		// TODO: Use interner pattern for caching.
		return new Rest(Objects.requireNonNull(duration));
	}

	/**
	 * Private constructor. Use the static method
	 * {@link #of(Duration) getRest} to get a {@link Rest}
	 * object.
	 *
	 * @param duration the duration of the rest.
	 */
	private Rest(Duration duration) {
		this.duration = duration;
	}

	/**
	 * Returns the duration of this rest.
	 *
	 * @return the duration of this rest
	 */
	@Override
	public Duration getDuration() {
		return this.duration;
	}

	@Override
	public boolean isRest() {
		return true;
	}

	@Override
	public String toString() {
		return "R" + this.duration.toString();
	}

	/**
	 * Returns true if the given object is a rest with the same duration as this
	 * rest.
	 *
	 * @param o Object against which this is compared for equality.
	 * @return true if Object o is a Rest and the Duration of o is equal to the
	 * Duration of this.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Rest)) {
			return false;
		}

		final Rest other = (Rest) o;

		return this.duration.equals(other.duration);
	}

	@Override
	public int hashCode() {
		final int hash = 3;
		return hash;
	}
}
