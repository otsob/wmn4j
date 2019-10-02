/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Score;
import org.wmn4j.notation.iterators.PartWiseScoreIterator;
import org.wmn4j.notation.iterators.Position;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Point set representation of a score.
 * The points are sorted lexicographically in the point set.
 * <p>
 * This class is immutable.
 */
final class PointSet {

	private final List<NoteEventVector> points;
	private final Map<NoteEventVector, Position> positions;

	PointSet(Score score) {
		this.positions = new HashMap<>();
		this.points = this.pointsFromScore(score);
	}

	int size() {
		return this.points.size();
	}

	Position getPosition(NoteEventVector vector) {
		return positions.get(vector);
	}

	NoteEventVector get(int index) {
		return this.points.get(index);
	}

	private List<NoteEventVector> pointsFromScore(Score score) {

		final PartWiseScoreIterator scoreIterator = new PartWiseScoreIterator(score);
		Position prevPos = null;
		double fullMeasuresOffset = 0.0;
		double offsetWithinMeasure = 0.0;
		final List<NoteEventVector> noteEvents = new ArrayList<>();

		while (scoreIterator.hasNext()) {
			final Durational dur = scoreIterator.next();
			final Position pos = scoreIterator.getPositionOfPrevious();

			// Part changes
			if (prevPos != null && prevPos.getPartIndex() != pos.getPartIndex()) {
				fullMeasuresOffset = 0.0;
				offsetWithinMeasure = 0.0;
			} else if (prevPos != null && prevPos.getMeasureNumber() != pos.getMeasureNumber()) {
				// Measure changes.
				final Measure prevMeasure = score.getPart(prevPos.getPartIndex()).getMeasure(prevPos.getStaffNumber(),
						prevPos.getMeasureNumber());
				final double prevMeasureDuration = prevMeasure.getTimeSignature().getTotalDuration().toDouble();
				fullMeasuresOffset += prevMeasureDuration;
				offsetWithinMeasure = 0.0;
			} else if (prevPos != null && (prevPos.getVoiceNumber() != pos.getVoiceNumber()
					|| prevPos.getStaffNumber() != pos.getStaffNumber())) {
				// Voice or staff changes
				offsetWithinMeasure = 0.0;
			}

			if (hasOnset(dur)) {
				final double totalOffset = fullMeasuresOffset + offsetWithinMeasure;

				if (dur instanceof Note) {
					final int pitch = ((Note) dur).getPitch().toInt();
					final NoteEventVector vector = new NoteEventVector(totalOffset, pitch, pos.getPartIndex());
					noteEvents.add(vector);
					positions.put(vector, pos);
				} else {
					final Chord chord = (Chord) dur;
					for (int chordIndex = 0; chordIndex < chord.getNoteCount(); ++chordIndex) {
						final int pitch = chord.getNote(chordIndex).getPitch().toInt();
						Position positionInChord = new Position(pos.getPartIndex(), pos.getStaffNumber(),
								pos.getMeasureNumber(), pos.getVoiceNumber(), pos.getIndexInVoice(), chordIndex);
						final NoteEventVector vector = new NoteEventVector(totalOffset, pitch,
								positionInChord.getPartIndex());
						noteEvents.add(vector);
						positions.put(vector, positionInChord);
					}
				}
			}

			// Update
			offsetWithinMeasure += dur.getDuration().toDouble();
			prevPos = pos;
		}

		noteEvents.sort(NoteEventVector::compareTo);
		return noteEvents;
	}

	private boolean hasOnset(Durational dur) {
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
