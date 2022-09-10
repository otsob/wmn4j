/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link org.wmn4j.notation.ChordSymbol} instances.
 * <p>
 * This class is not thread-safe.
 */
public final class ChordSymbolBuilder {

	private PitchName root;
	private PitchName bass;
	private ChordSymbol.Base base;

	private final List<ChordSymbol.Extension> extensions;

	/**
	 * Constructs and empty build with no fields set.
	 */
	public ChordSymbolBuilder() {
		extensions = new ArrayList<>();
	}

	/**
	 * Returns the root note set in this builder.
	 *
	 * @return the root note set in this builder
	 */
	public PitchName getRoot() {
		return root;
	}

	/**
	 * Sets the root note in this builder.
	 *
	 * @param root the root note to set in this builder
	 * @return reference to this
	 */
	public ChordSymbolBuilder setRoot(PitchName root) {
		this.root = root;
		return this;
	}

	/**
	 * Returns the bass note set in this builder.
	 *
	 * @return the bass note set in this builder
	 */
	public PitchName getBass() {
		return bass;
	}

	/**
	 * Sets the bass note set in this builder.
	 *
	 * @param bass the bass note to set
	 * @return reference to this
	 */
	public ChordSymbolBuilder setBass(PitchName bass) {
		this.bass = bass;
		return this;
	}

	/**
	 * Returns the base triad/dyad type set in this builder.
	 *
	 * @return the base triad/dyad type set in this builder
	 */
	public ChordSymbol.Base getBase() {
		return base;
	}

	/**
	 * Sets the base triad/dyad type of this builder.
	 *
	 * @param base the base triad/dyad type
	 * @return reference to this
	 */
	public ChordSymbolBuilder setBase(ChordSymbol.Base base) {
		this.base = base;
		return this;
	}

	/**
	 * Adds the given extension to this builder.
	 *
	 * @param extension the extension to add to this builder
	 * @return reference to this
	 */
	public ChordSymbolBuilder addExtension(ChordSymbol.Extension extension) {
		extensions.add(extension);
		return this;
	}

	/**
	 * Returns a chord symbol with the values set in this builder.
	 *
	 * @return a chord symbol with the values set in this builder
	 */
	public ChordSymbol build() {
		return ChordSymbol.of(base, root, bass, extensions);
	}
}
