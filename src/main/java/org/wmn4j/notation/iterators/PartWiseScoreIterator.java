/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Score;

/**
 * Iterates through a <code>Score</code> in part wise order. Starts by iterating
 * through the part with the smallest number. Iterates through parts starting
 * from smallest measure number. Iterates through measure voice by voice.
 *
 * @author Otso Björklund
 */
public class PartWiseScoreIterator implements ScoreIterator {

	private final Iterator<Part> scoreIterator;

	private Part.Iter currentPartIterator;
	private Measure.Iter currentMeasureIterator;
	private Part prevPart;
	private int prevPartIndex;
	private int prevStaffNumber;
	private int prevMeasureNumber;
	private int prevVoice;
	private int prevIndex;

	/**
	 * @param score The score that this iterates through.
	 */
	public PartWiseScoreIterator(Score score) {
		this.scoreIterator = score.iterator();
		// TODO: Consider a better way to handle iterating Parts and part index
		// handling.
		this.prevPart = this.scoreIterator.next();
		this.prevPartIndex = 0;
		this.currentPartIterator = this.prevPart.getPartIterator();
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
				this.currentPartIterator = this.prevPart.getPartIterator();
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
	public ScorePosition getPositionOfPrevious() {
		if (this.prevPart == null) {
			throw new IllegalStateException("no previous position available because next has not been called yet");
		}

		return new ScorePosition(this.prevPartIndex, this.prevStaffNumber, this.prevMeasureNumber, this.prevVoice,
				this.prevIndex);
	}
}
