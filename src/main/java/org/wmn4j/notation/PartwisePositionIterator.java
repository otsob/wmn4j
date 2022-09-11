/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.MeasureIterator;
import org.wmn4j.notation.access.PartIterator;
import org.wmn4j.notation.access.Position;
import org.wmn4j.notation.access.PositionIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Iterates through a {@link Score} in part wise order. Starts by iterating
 * through the part with the smallest number. Iterates through parts starting
 * from smallest measure number. Iterates through measure voice by voice.
 * <p>
 * Instances of this class are not thread-safe.
 */
final class PartwisePositionIterator implements PositionIterator {

	private static final int NEXT_NOT_CALLED_INDEX = -1;
	private final Score score;

	private final int firstMeasure;
	private final int lastMeasure;

	private final Iterator<Integer> partIndexIterator;
	private PartIterator currentPartIterator;
	private MeasureIterator currentMeasureIterator;
	private int currentPartIndex;

	// These variables keep track of what was the position
	// of the durational returned by last call of next.
	private int prevPartIndex;
	private int prevStaffNumber;
	private int prevMeasureNumber;
	private int prevVoice;
	private int prevIndex;

	PartwisePositionIterator(Score score, int firstMeasure, int lastMeasure) {
		this(score, firstMeasure, lastMeasure, Collections.emptyList());
	}

	/**
	 * Constructor.
	 *
	 * @param score        the score that this iterates through
	 * @param firstMeasure the number of the first measure to be included in iteration
	 * @param lastMeasure  the number of the last measure to be included in iteration
	 * @param partIndices  the indices of the parts included in iteration
	 */
	PartwisePositionIterator(Score score, int firstMeasure, int lastMeasure, List<Integer> partIndices) {
		this.score = score;
		this.firstMeasure = firstMeasure;
		this.lastMeasure = lastMeasure;

		if (partIndices == null || partIndices.isEmpty()) {
			this.partIndexIterator = score.toSelection().getPartIndices().iterator();
		} else {
			this.partIndexIterator = new ArrayList<>(partIndices).iterator();
		}

		this.currentPartIndex = this.partIndexIterator.next();
		this.prevPartIndex = NEXT_NOT_CALLED_INDEX;
		this.currentPartIterator = score.getPart(this.currentPartIndex)
				.getPartIterator(this.firstMeasure, this.lastMeasure);
		this.currentMeasureIterator = this.currentPartIterator.next().getMeasureIterator();

		findNextMeasureWithContent();
	}

	@Override
	public boolean hasNext() {
		// Current measure iterator should always be kept at
		// the next measure with content. Measure iterator
		// hasNext should only return false when there is nothing
		// more to iterate in the score.
		return currentMeasureIterator.hasNext();
	}

	@Override
	public Durational next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		final Durational next = currentMeasureIterator.next();
		prevPartIndex = currentPartIndex;
		prevStaffNumber = currentPartIterator.getStaffNumberOfPrevious();
		prevMeasureNumber = currentPartIterator.getMeasureNumberOfPrevious();
		prevVoice = currentMeasureIterator.getVoiceOfPrevious();
		prevIndex = currentMeasureIterator.getIndexOfPrevious();

		findNextMeasureWithContent();

		return next;
	}

	private void findNextMeasureWithContent() {
		while (!currentMeasureIterator.hasNext()) {
			if (currentPartIterator.hasNext()) {
				currentMeasureIterator = currentPartIterator.next().getMeasureIterator();
			} else if (partIndexIterator.hasNext()) {
				currentPartIndex = partIndexIterator.next();
				currentPartIterator = score.getPart(currentPartIndex).getPartIterator(firstMeasure, lastMeasure);
			} else {
				// Nothing more to iterate.
				break;
			}
		}
	}

	@Override
	public Position getPositionOfPrevious() {
		if (prevPartIndex == NEXT_NOT_CALLED_INDEX) {
			throw new IllegalStateException("no previous position available because next has not been called yet");
		}

		return new Position(this.prevPartIndex, this.prevStaffNumber, this.prevMeasureNumber, this.prevVoice,
				this.prevIndex);
	}
}
