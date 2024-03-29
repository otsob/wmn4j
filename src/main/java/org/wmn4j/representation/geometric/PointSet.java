/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

import org.wmn4j.mir.PatternPosition;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.access.Position;
import org.wmn4j.notation.access.PositionIterator;
import org.wmn4j.notation.access.Selection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Point set representation of a score.
 * The points are sorted lexicographically in the point set.
 * <p>
 * This class is immutable.
 *
 * @param <T> the point type to use in for the point set
 */
public final class PointSet<T extends Point<T>> {

	private record PointPosition<T extends Point<T>>(T point, Position position) {
	}

	private final List<PointPosition<T>> pointPositions;
	private final Map<T, Position> positionMap;

	/**
	 * Returns a 2 dimensional point set (with double components) created for the given score.
	 *
	 * @param score the score from which the point set is created
	 * @return 2 dimensional point set created for the given score
	 */
	public static PointSet<Point2D> from(Score score) {
		return from(score.toSelection());
	}

	/**
	 * Returns a 2 dimensional point set (with double components) created for the given selection.
	 *
	 * @param selection the selection from which the point set is created
	 * @return 2 dimensional point set created for the given selection
	 */
	public static PointSet<Point2D> from(Selection selection) {
		final PositionIterator positionIterator = selection.partwiseIterator();
		Position prevPos = null;
		double fullMeasuresOffset = 0.0;
		double offsetWithinMeasure = 0.0;

		final List<PointPosition<Point2D>> pointPositions = new ArrayList<>();

		while (positionIterator.hasNext()) {
			final Durational dur = positionIterator.next();
			final Position pos = positionIterator.getPositionOfPrevious();

			// Part changes
			if (prevPos != null && prevPos.getPartIndex() != pos.getPartIndex()) {
				fullMeasuresOffset = 0.0;
				offsetWithinMeasure = 0.0;
			} else if (prevPos != null && prevPos.getMeasureNumber() != pos.getMeasureNumber()) {
				// Measure changes.
				final Measure prevMeasure = selection.getPart(prevPos.getPartIndex())
						.getMeasure(prevPos.getStaffNumber(),
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

				if (dur.isNote()) {
					final Note note = dur.toNote();
					if (note.hasPitch()) {
						final int pitch = dur.toNote().getPitch().get().toInt();
						final Point2D vector = new Point2D(totalOffset, pitch);
						pointPositions.add(new PointPosition<>(vector, pos));
					}
				} else if (dur.isChord()) {
					final Chord chord = dur.toChord();
					for (int chordIndex = 0; chordIndex < chord.getNoteCount(); ++chordIndex) {
						final Note note = chord.getNote(chordIndex);
						if (note.hasPitch()) {
							final int pitch = chord.getNote(chordIndex).getPitch().get().toInt();
							Position positionInChord = new Position(pos.getPartIndex(), pos.getStaffNumber(),
									pos.getMeasureNumber(), pos.getVoiceNumber(), pos.getIndexInVoice(), chordIndex);
							final Point2D vector = new Point2D(totalOffset, pitch);
							pointPositions.add(new PointPosition<>(vector, positionInChord));
						}
					}
				}
			}

			// Update
			offsetWithinMeasure += dur.getDuration().toDouble();
			prevPos = pos;
		}

		pointPositions.sort(Comparator.comparing(a -> a.point));
		return new PointSet<>(pointPositions);
	}

	private static boolean hasOnset(Durational dur) {
		if (dur.isRest()) {
			return false;
		}

		if (dur.isNote()) {
			if (dur.toNote().isTiedFromPrevious()) {
				return false;
			}
		}

		return true;
	}

	PointSet(List<PointPosition<T>> pointPositions) {
		this.pointPositions = pointPositions;

		final Map<T, Position> positionMap = new HashMap<>();
		for (var pointPosition : pointPositions) {
			positionMap.put(pointPosition.point, pointPosition.position);
		}

		this.positionMap = positionMap;
	}

	/**
	 * Returns the number of points in the point set.
	 *
	 * @return the number of points in the point set
	 */
	public int size() {
		return this.pointPositions.size();
	}

	/**
	 * Returns the score position (see {@link Position}) of the point in the score
	 * if the point exists in the point set. Otherwise, returns empty.
	 *
	 * @param point the point for which the position ois returned if it's present
	 * @return the score position if the point exists in the point set
	 */
	public Optional<Position> getPosition(T point) {
		return Optional.ofNullable(positionMap.getOrDefault(point, null));
	}

	/**
	 * Returns the position corresponding to the point at the given index.
	 *
	 * @param index the index of the point show position is returned
	 * @return the position corresponding to the point at the given index
	 */
	public Position getPosition(int index) {
		return pointPositions.get(index).position;
	}

	/**
	 * Returns the position of the translated pattern in the score (see {@link PatternPosition}) if
	 * all points in the point pattern are present in this point set. Otherwise, returns empty.
	 *
	 * @param pattern    the point pattern for which the pattern position is returned
	 * @param translator a translator by which the points are translated
	 * @return the position of the translated pattern in the score if present, otherwise, empty
	 */
	public Optional<PatternPosition> getPosition(PointPattern<T> pattern, T translator) {
		List<Position> positions = new ArrayList<>(pattern.size());

		for (T point : pattern) {
			final T translated = point.add(translator);
			var position = getPosition(translated);
			if (position.isEmpty()) {
				return Optional.empty();
			}

			positions.add(position.get());
		}

		return Optional.of(new PatternPosition(positions));
	}

	/**
	 * Returns the point in the given index.
	 *
	 * @param index the index of the point to return
	 * @return the point in the given index
	 */
	public T get(int index) {
		return this.pointPositions.get(index).point;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();

		for (var pointPosition : this.pointPositions) {
			strBuilder.append(pointPosition.point).append("\n");
		}

		return strBuilder.toString();
	}
}
