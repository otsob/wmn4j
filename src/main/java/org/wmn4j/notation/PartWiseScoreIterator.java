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
import org.wmn4j.notation.access.PositionalIterator;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates through a {@link Score} in part wise order. Starts by iterating
 * through the part with the smallest number. Iterates through parts starting
 * from smallest measure number. Iterates through measure voice by voice.
 * <p>
 * Instances of this class are not thread-safe.
 */
final class PartWiseScoreIterator implements PositionalIterator {

	private final Iterator<Part> scoreIterator;

	private PartIterator currentPartIterator;
	private MeasureIterator currentMeasureIterator;
	private Part prevPart;
	private int prevPartIndex;
	private int prevStaffNumber;
	private int prevMeasureNumber;
	private int prevVoice;
	private int prevIndex;

	private final int firstMeasure;
	private final int lastMeasure;

	/**
	 * Constructor.
	 *
	 * @param score        the score that this iterates through
	 * @param firstMeasure the number of the first measure to be included in iteration
	 * @param lastMeasure  the number of the last measure to be included in iteration
	 */
	PartWiseScoreIterator(Score score, int firstMeasure, int lastMeasure) {
		this.scoreIterator = score.iterator();
		this.prevPart = this.scoreIterator.next();
		this.prevPartIndex = 0;
		this.firstMeasure = firstMeasure;
		this.lastMeasure = lastMeasure;
		this.currentPartIterator = this.prevPart.getPartIterator(this.firstMeasure, this.lastMeasure);
		this.currentMeasureIterator = this.currentPartIterator.next().getMeasureIterator();
	}

	@Override
	public boolean hasNext() {
		return this.currentMeasureIterator.hasNext() || this.currentPartIterator.hasNext()
				|| this.scoreIterator.hasNext();
	}

	@Override
	public Durational next() {
		if (!this.hasNext()) {
			throw new NoSuchElementException();
		}

		if (!this.currentMeasureIterator.hasNext()) {
			if (!this.currentPartIterator.hasNext()) {
				this.prevPart = this.scoreIterator.next();
				++this.prevPartIndex;
				this.currentPartIterator = this.prevPart.getPartIterator(this.firstMeasure, this.lastMeasure);
			}

			this.currentMeasureIterator = this.currentPartIterator.next().getMeasureIterator();
		}

		final Durational next = this.currentMeasureIterator.next();
		this.prevStaffNumber = this.currentPartIterator.getStaffNumberOfPrevious();
		this.prevMeasureNumber = this.currentPartIterator.getMeasureNumberOfPrevious();
		this.prevVoice = this.currentMeasureIterator.getVoiceOfPrevious();
		this.prevIndex = this.currentMeasureIterator.getIndexOfPrevious();

		return next;
	}

	@Override
	public Position getPositionOfPrevious() {
		if (this.prevPart == null) {
			throw new IllegalStateException("no previous position available because next has not been called yet");
		}

		return new Position(this.prevPartIndex, this.prevStaffNumber, this.prevMeasureNumber, this.prevVoice,
				this.prevIndex);
	}
}
