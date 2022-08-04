/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.techniques;

import java.util.Objects;
import java.util.OptionalInt;

/**
 * Represents a technique marking with a number in it.
 */
final class NumericTechnique extends Technique {
	private final int number;

	NumericTechnique(Type type, int number) {
		super(type);
		this.number = number;
	}

	@Override
	public OptionalInt getNumber() {
		return OptionalInt.of(number);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}
		NumericTechnique that = (NumericTechnique) o;
		return number == that.number;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), number);
	}
}
