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
 *
 * @author Otso Björklund
 */
public class PatternOccurrence {

	private final List<ScorePosition> positions;

	public PatternOccurrence(List<ScorePosition> positions) {
		this.positions = Collections.unmodifiableList(new ArrayList<>(positions));
	}

	public List<ScorePosition> getPositions() {
		return this.positions;
	}
}
