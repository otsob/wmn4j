/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.pattern_discovery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.wmn4j.notation.iterators.PartWiseScoreIterator;
import org.wmn4j.notation.iterators.ScorePosition;
import org.wmn4j.notation.noteobjects.Chord;
import org.wmn4j.notation.noteobjects.Durational;
import org.wmn4j.notation.noteobjects.Measure;
import org.wmn4j.notation.noteobjects.Note;
import org.wmn4j.notation.noteobjects.Score;

/**
 *
 * @author Otso Björklund
 */
public class PointSet {

	private final List<NoteEventVector> points;

	public PointSet(List<NoteEventVector> points) {
		this.points = new ArrayList<>(points);
	}

	public PointSet(Score score) {
		this.points = this.pointsFromScore(score);
	}

	public void sortLexicographically() {
		this.points.sort(Comparator.naturalOrder());
	}

	public int size() {
		return this.points.size();
	}

	public NoteEventVector get(int index) {
		return this.points.get(index);
	}

	private List<NoteEventVector> pointsFromScore(Score score) {

		PartWiseScoreIterator scoreIterator = new PartWiseScoreIterator(score);
		ScorePosition prevPos = null;
		double offsetToEndOfLastMeasure = 0.0;
		double offsetWithinMeasure = 0.0;
		List<NoteEventVector> noteEvents = new ArrayList<>();

		while (scoreIterator.hasNext()) {
			Durational dur = scoreIterator.next();
			ScorePosition pos = scoreIterator.positionOfPrevious();

			// Part changes
			if (prevPos != null && prevPos.getPartNumber() != pos.getPartNumber()) {
				offsetToEndOfLastMeasure = 0.0;
				offsetWithinMeasure = 0.0;
			} // Measure changes.
			else if (prevPos != null && prevPos.getMeasureNumber() != pos.getMeasureNumber()) {
				Measure prevMeasure = score.getPart(prevPos.getPartNumber()).getMeasure(prevPos.getStaffNumber(),
						prevPos.getMeasureNumber());
				double prevMeasureDuration = prevMeasure.getTimeSignature().getTotalDuration().toDouble();
				offsetToEndOfLastMeasure += prevMeasureDuration;
				offsetWithinMeasure = 0.0;
			} // Voice or staff changes
			else if (prevPos != null && (prevPos.getVoiceNumber() != pos.getVoiceNumber()
					|| prevPos.getStaffNumber() != pos.getStaffNumber())) {
				offsetWithinMeasure = 0.0;
			}

			if (isOnset(dur)) {
				double totalOffset = offsetToEndOfLastMeasure + offsetWithinMeasure;
				Durational atPosition = score.getAtPosition(pos);

				if (atPosition instanceof Note) {
					double pitch = ((Note) atPosition).getPitch().toInt();
					double[] components = { totalOffset, pitch };
					noteEvents.add(new NoteEventVector(components, pos));
				} else {
					Chord chord = (Chord) atPosition;
					for (Note note : chord) {
						double pitch = note.getPitch().toInt();
						double[] components = { totalOffset, pitch };
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
		if (dur.isRest())
			return false;

		if (dur instanceof Note) {
			Note note = (Note) dur;
			if (note.isTiedFromPrevious())
				return false;
		}

		return true;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();

		for (NoteEventVector vec : this.points) {
			strBuilder.append(vec).append("\n");
		}

		return strBuilder.toString();
	}
}
