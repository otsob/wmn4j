/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.wmn4j.notation.Duration;

import java.util.Objects;
import java.util.Optional;

/**
 * A wrapper class for associating a notation element with an offset.
 * The offset is a {@link Duration} or empty if the offset is zero.
 * <p>
 * Offsets are compared by their offset durations.
 *
 * @param <T> the type of the notation element
 */
public final class Offset<T> implements Comparable<Offset<?>> {

	private final T element;
	private final Duration offsetDuration;

	/**
	 * Creates a new offset notation element.
	 *
	 * @param element        the notation element that is offset, must be non-null
	 * @param offsetDuration the amount of offset, can be null for zero offset
	 */
	public Offset(T element, Duration offsetDuration) {
		this.element = Objects.requireNonNull(element);
		this.offsetDuration = offsetDuration;
	}

	/**
	 * Returns the duration of the offset if the duration is non-zero, otherwise returns empty.
	 *
	 * @return the duration of the offset if the duration is non-zero, otherwise returns empty
	 */
	public Optional<Duration> getDuration() {
		return Optional.ofNullable(offsetDuration);
	}

	/**
	 * Returns the notation element that is offset.
	 *
	 * @return the notation element that is offset
	 */
	public T get() {
		return element;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Offset<?>)) {
			return false;
		}

		Offset<?> offset1 = (Offset<?>) o;

		return Objects.equals(element, offset1.element)
				&& Objects.equals(offsetDuration, offset1.offsetDuration);
	}

	@Override
	public int hashCode() {
		return Objects.hash(element, offsetDuration);
	}

	@Override
	public int compareTo(Offset<?> o) {
		if (Objects.equals(offsetDuration, o.offsetDuration)) {
			return 0;
		}

		if (offsetDuration == null && o.offsetDuration != null) {
			return -1;
		}

		if (offsetDuration != null && o.offsetDuration == null) {
			return 1;
		}

		return offsetDuration.compareTo(o.offsetDuration);
	}

	@Override
	public String toString() {
		return element + " at offset " + offsetDuration;
	}
}
