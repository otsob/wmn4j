/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Otso Björklund
 */
class PointPattern {

	private final List<NoteEventVector> points;
	private final int hash;

	PointPattern(List<NoteEventVector> points) {
		this.points = points;
		this.hash = computeHash();
	}

	int getSize() {
		return this.points.size();
	}

	List<NoteEventVector> getPoints() {
		return Collections.unmodifiableList(this.points);
	}

	PointPattern getVectorizedRepresentation() {
		final List<NoteEventVector> vecPoints = new ArrayList<>();

		for (int i = 1; i < this.points.size(); ++i) {
			vecPoints.add(this.points.get(i).subtract(this.points.get(i - 1)));
		}

		return new PointPattern(vecPoints);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof PointPattern)) {
			return false;
		}

		final PointPattern other = (PointPattern) o;
		final List<NoteEventVector> otherPoints = other.getPoints();
		if (this.points.size() != otherPoints.size()) {
			return false;
		}

		for (int i = 0; i < this.points.size(); ++i) {
			if (!this.points.get(i).equals(otherPoints.get(i))) {
				return false;
			}
		}

		return true;
	}

	private int computeHash() {
		int multiplierIndex = 0;
		long hash = RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);

		for (NoteEventVector point : this.points) {
			final long bits = Double.doubleToRawLongBits(point.getRoundedOffset());
			final int firstOffsetPart = (int) (bits >> 32);
			hash += firstOffsetPart * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);
			final int secondOffsetPart = (int) bits;
			hash += secondOffsetPart * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);

			hash += point.getPitch() * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);
			hash += point.getPart() * RandomMultipliers.INSTANCE.getMultiplier(multiplierIndex++);
		}

		return (int) hash;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}
}
