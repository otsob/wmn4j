/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.pattern_discovery;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
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
		List<NoteEventVector> vecPoints = new ArrayList<>();

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

		PointPattern other = (PointPattern) o;
		List<NoteEventVector> otherPoints = other.getPoints();
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
		long hash = RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);

		for (NoteEventVector point : this.points) {
			for (int i = 0; i < point.getDimensionality(); ++i) {
				long bits = Double.doubleToRawLongBits(point.getComponent(i));
				int first = (int) (bits >> 32);
				hash += first * RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);
				int second = (int) bits;
				hash += second * RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);
			}
		}

		return (int) hash;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}
}
