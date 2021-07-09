/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.directions;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a playing direction.
 * <p>
 * Directions include tempo, dynamics, technique, and general textual directions.
 * <p>
 * This class is immutable.
 */
public final class Direction {

	private final Type type;
	private final String text;

	/**
	 * Defines the type of the direction.
	 */
	public enum Type {
		/**
		 * Text direction. Default type for directions that do not have a clear type such as tempo or dynamics.
		 */
		TEXT
	}

	/**
	 * Returns a direction of the given type.
	 *
	 * @param type the type of the direction
	 * @param text the text of the direction if this is a textual direction marking, otherwise can be null
	 * @return a direction of the given type
	 */
	public static Direction of(Type type, String text) {
		return new Direction(type, text);
	}

	private Direction(Type type, String text) {
		this.type = Objects.requireNonNull(type);
		this.text = text;
	}

	/**
	 * Returns the type of this direction.
	 *
	 * @return the type of this direction
	 */
	public Type getType() {
		return type;
	}

	/**
	 * Returns true if this is a textual direction marking.
	 * <p>
	 * For example, tempos can be indicated either as texts such as Allegro or as
	 * a symbol indicating beats per minute.
	 *
	 * @return true if this is a textual direction marking
	 */
	public boolean isTextual() {
		return text != null;
	}

	/**
	 * Returns the text of this direction if present, otherwise returns empty.
	 *
	 * @return the text of this direction if present, otherwise returns empty
	 */
	public Optional<String> getText() {
		return Optional.ofNullable(text);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Direction)) {
			return false;
		}

		Direction direction = (Direction) o;
		return Objects.equals(type, direction.type)
				&& Objects.equals(text, direction.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, text);
	}

	@Override
	public String toString() {
		return "Direction{ type=" + type + ", text=" + text + "}";
	}
}
