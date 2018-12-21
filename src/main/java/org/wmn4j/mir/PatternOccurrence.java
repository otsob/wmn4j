/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wmn4j.notation.iterators.ScorePosition;

/**
 * Class that represents an occurrence of a pattern in a score.
 */
public class PatternOccurrence {

	private final List<ScorePosition> positions;

	/**
	 * Constructor.
	 *
	 * @param positions the positions in which the elements of the pattern occur in
	 *                  a score.
	 */
	public PatternOccurrence(List<ScorePosition> positions) {
		this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
	}

	/**
	 * Returns the positions in which the elements of the pattern occur in a score.
	 *
	 * @return the positions in which the elements of the pattern occur in a score
	 */
	public List<ScorePosition> getPositions() {
		return this.positions;
	}
}
