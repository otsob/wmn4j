/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.iterators.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the position of a {@link org.wmn4j.mir.Pattern} in a {@link org.wmn4j.notation.elements.Score}.
 * This class is immutable.
 */
public class PatternPosition {

	private final List<Position> positions;

	/**
	 * Constructor.
	 *
	 * @param positions the positions in which the elements of the pattern occur in
	 *                  a score.
	 */
	public PatternPosition(List<Position> positions) {
		this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
	}

	/**
	 * Returns the positions in which the elements of the pattern occur in a score.
	 *
	 * @return the positions in which the elements of the pattern occur in a score
	 */
	public List<Position> getPositions() {
		return this.positions;
	}
}
