/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jmir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wmn4jnotation.iterators.ScorePosition;

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
