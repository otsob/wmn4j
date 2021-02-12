/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.PositionalIterator;
import org.wmn4j.notation.access.Selection;

import java.util.Iterator;

/**
 * Implementation of Selection.
 */
final class SelectionImpl implements Selection {

	private final Score score;
	private final int first;
	private final int last;

	SelectionImpl(Score score, int first, int last) {
		this.score = score;
		this.first = first;
		this.last = last;

		if (first > last) {
			throw new IllegalArgumentException(
					"Range ending measure number must be at least the starting measure number");
		}

		if (!score.hasPickupMeasure() && first == 0) {
			throw new IllegalArgumentException(
					"Range begins in pickup measure but score has no pickup measure");
		}

		if (score != null) {
			if (last > score.getMeasureCount() || last < 0) {
				throw new IllegalArgumentException("Range ending measure number is outside score");
			}

			if (first > score.getMeasureCount() || first < 0) {
				throw new IllegalArgumentException("Range starting measure number is outside score");
			}
		}
	}

	@Override
	public int getFirst() {
		return first;
	}

	@Override
	public int getLast() {
		return last;
	}

	@Override
	public PositionalIterator positionalIterator() {
		return new PartwisePositionalIterator(this.score, this.first, this.last);
	}

	@Override
	public Selection subSelection(int firstMeasure, int lastMeasure) {
		return new SelectionImpl(this.score, firstMeasure, lastMeasure);
	}

	@Override
	public Iterator<Durational> iterator() {
		return new PartwisePositionalIterator(this.score, this.first, this.last);
	}
}
