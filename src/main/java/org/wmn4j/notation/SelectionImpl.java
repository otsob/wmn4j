/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.PositionalIterator;
import org.wmn4j.notation.access.Selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of Selection.
 */
final class SelectionImpl implements Selection {

	private final Score score;
	private final List<Integer> partIndices;
	private final int first;
	private final int last;

	SelectionImpl(Score score, int first, int last) {
		this(score, first, last, Collections.emptyList());
	}

	SelectionImpl(Score score, int first, int last, Collection<Integer> partIndices) {
		this.score = Objects.requireNonNull(score);
		this.first = first;
		this.last = last;

		this.partIndices = createPartIndexList(partIndices);

		if (first > last) {
			throw new IllegalArgumentException(
					"Range ending measure number must be at least the starting measure number");
		}

		if (!score.hasPickupMeasure() && first == 0) {
			throw new IllegalArgumentException(
					"Range begins in pickup measure but score has no pickup measure");
		}

		if (last > score.getMeasureCount() || last < 0) {
			throw new IllegalArgumentException("Range ending measure number is outside score");
		}

		if (first > score.getMeasureCount() || first < 0) {
			throw new IllegalArgumentException("Range starting measure number is outside score");
		}
	}

	private List<Integer> createPartIndexList(Collection<Integer> partIndices) {
		if (partIndices.isEmpty()) {
			List<Integer> indices = new ArrayList<>(score.getPartCount());
			for (int i = 0; i < score.getPartCount(); ++i) {
				indices.add(i);
			}

			return Collections.unmodifiableList(indices);

		} else {
			Set<Integer> distinctIndices = new HashSet<>(partIndices);
			List<Integer> indices = new ArrayList<>(distinctIndices);
			indices.sort(Integer::compareTo);

			for (Integer index : indices) {
				if (index < 0 || index >= score.getPartCount()) {
					throw new IllegalArgumentException(
							"Cannot select part with index " + index + " from score with " + score.getPartCount()
									+ " parts");
				}
			}

			return Collections.unmodifiableList(indices);
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
	public List<Integer> getPartIndices() {
		return partIndices;
	}

	@Override
	public PositionalIterator positionalIterator() {
		return new PartwisePositionalIterator(this.score, this.first, this.last, this.partIndices);
	}

	@Override
	public Selection subSelection(int firstMeasure, int lastMeasure) {
		return new SelectionImpl(this.score, firstMeasure, lastMeasure, partIndices);
	}

	@Override
	public Selection subSelection(Collection<Integer> partIndices) {
		return new SelectionImpl(this.score, this.first, this.last, partIndices);
	}

	@Override
	public Iterator<Durational> iterator() {
		return positionalIterator();
	}
}
