/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.techniques;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a more complex technique marking, e.g., an artificial harmonic.
 */
final class ComplexTechnique extends Technique {
	private final Map<AdditionalValue, Object> additionalValues;

	ComplexTechnique(Type type, Map<AdditionalValue, Object> additionalValues) {
		super(type);
		this.additionalValues = Map.copyOf(additionalValues);
	}

	@Override
	public <T> Optional<T> getValue(AdditionalValue value, Class<T> valueType) {
		return Optional.ofNullable(valueType.cast(additionalValues.getOrDefault(value, null)));
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
		ComplexTechnique that = (ComplexTechnique) o;
		return additionalValues.equals(that.additionalValues);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), additionalValues);
	}

	@Override
	public String toString() {
		return super.toString() + "-" + additionalValues.toString();
	}
}
