/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wmn4j.notation.iterators.Position;

/**
 * Class that represents an occurrence of a pattern in a score.
 */
public class PatternOccurrence {

	private final List<Position> positions;

	/**
	 * Constructor.
	 *
	 * @param positions the positions in which the elements of the pattern occur in
	 *                  a score.
	 */
	public PatternOccurrence(List<Position> positions) {
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
