/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.techniques;

import java.util.Objects;
import java.util.Optional;

/**
 * Represents a textual technique.
 */
final class TextualTechnique extends Technique {

	private final String text;

	TextualTechnique(Type type, String text) {
		super(type);
		this.text = Objects.requireNonNull(text);
	}

	@Override
	public Optional<String> getText() {
		return Optional.of(text);
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
		TextualTechnique that = (TextualTechnique) o;
		return text.equals(that.text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), text);
	}

	@Override
	public String toString() {
		return super.toString() + "-" + text;
	}
}
