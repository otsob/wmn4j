/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.pattern_discovery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Score;
import org.wmn4j.notation.iterators.PartWiseScoreIterator;
import org.wmn4j.notation.iterators.Position;

/**
 *
 * @author Otso Björklund
 */
class PointSet {

	private final List<NoteEventVector> points;

	PointSet(List<NoteEventVector> points) {
		this.points = new ArrayList<>(points);
	}

	PointSet(Score score) {
		this.points = this.pointsFromScore(score);
	}

	void sortLexicographically() {
		this.points.sort(Comparator.naturalOrder());
	}

	int size() {
		return this.points.size();
	}

	NoteEventVector get(int index) {
		return this.points.get(index);
	}

	private List<NoteEventVector> pointsFromScore(Score score) {

		final PartWiseScoreIterator scoreIterator = new PartWiseScoreIterator(score);
		Position prevPos = null;
		double offsetToEndOfLastMeasure = 0.0;
		double offsetWithinMeasure = 0.0;
		final List<NoteEventVector> noteEvents = new ArrayList<>();

		while (scoreIterator.hasNext()) {
			final Durational dur = scoreIterator.next();
			final Position pos = scoreIterator.getPositionOfPrevious();

			// Part changes
			if (prevPos != null && prevPos.getPartIndex() != pos.getPartIndex()) {
				offsetToEndOfLastMeasure = 0.0;
				offsetWithinMeasure = 0.0;
			} else if (prevPos != null && prevPos.getMeasureNumber() != pos.getMeasureNumber()) {
				// Measure changes.
				final Measure prevMeasure = score.getPart(prevPos.getPartIndex()).getMeasure(prevPos.getStaffNumber(),
						prevPos.getMeasureNumber());
				final double prevMeasureDuration = prevMeasure.getTimeSignature().getTotalDuration().toDouble();
				offsetToEndOfLastMeasure += prevMeasureDuration;
				offsetWithinMeasure = 0.0;
			} else if (prevPos != null && (prevPos.getVoiceNumber() != pos.getVoiceNumber()
					|| prevPos.getStaffNumber() != pos.getStaffNumber())) {
				// Voice or staff changes
				offsetWithinMeasure = 0.0;
			}

			if (isOnset(dur)) {
				final double totalOffset = offsetToEndOfLastMeasure + offsetWithinMeasure;
				final Durational atPosition = score.getAt(pos);

				if (atPosition instanceof Note) {
					final double pitch = ((Note) atPosition).getPitch().toInt();
					final double[] components = { totalOffset, pitch };
					noteEvents.add(new NoteEventVector(components, pos));
				} else {
					final Chord chord = (Chord) atPosition;
					for (Note note : chord) {
						final double pitch = note.getPitch().toInt();
						final double[] components = { totalOffset, pitch };
						noteEvents.add(new NoteEventVector(components, pos));
					}
				}
			}

			// Update
			offsetWithinMeasure += dur.getDuration().toDouble();
			prevPos = pos;
		}

		return noteEvents;
	}

	private boolean isOnset(Durational dur) {
		if (dur.isRest()) {
			return false;
		}

		if (dur instanceof Note) {
			final Note note = (Note) dur;
			if (note.isTiedFromPrevious()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();

		for (NoteEventVector vec : this.points) {
			strBuilder.append(vec).append("\n");
		}

		return strBuilder.toString();
	}
}
