/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.wmn4j.notation.iterators.Position;

/**
 *
 * @author Otso Björklund
 */
class NoteEventVector implements Comparable<NoteEventVector> {

	private final double[] components;
	private final int hash;
	private final Position position;

	NoteEventVector(double[] components) {
		this.components = new double[components.length];
		System.arraycopy(components, 0, this.components, 0, components.length);
		this.position = null;
		this.hash = computeHash();
	}

	NoteEventVector(double[] components, Position position) {
		this.components = new double[components.length];
		System.arraycopy(components, 0, this.components, 0, components.length);
		this.position = position;
		this.hash = computeHash();
	}

	int getDimensionality() {
		return this.components.length;
	}

	double getComponent(int index) {
		return this.components[index];
	}

	boolean hasPosition() {
		return this.position != null;
	}

	Position getPosition() {
		return this.position;
	}

	NoteEventVector add(NoteEventVector other) {
		final double[] sumComponents = new double[this.getDimensionality()];

		for (int i = 0; i < sumComponents.length; ++i) {
			sumComponents[i] = this.components[i] + other.getComponent(i);
		}

		return new NoteEventVector(sumComponents);
	}

	NoteEventVector subtract(NoteEventVector other) {
		final double[] diffComponents = new double[this.getDimensionality()];

		for (int i = 0; i < diffComponents.length; ++i) {
			diffComponents[i] = this.components[i] - other.getComponent(i);
		}

		return new NoteEventVector(diffComponents);
	}

	@Override
	public int compareTo(NoteEventVector other) {
		for (int i = 0; i < this.getDimensionality(); ++i) {
			// TODO: Consider checking if doubles just really close to each other
			if (this.getComponent(i) < other.getComponent(i)) {
				return -1;
			}
			if (this.getComponent(i) > other.getComponent(i)) {
				return 1;
			}
		}

		return 0;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}

		if (!(o instanceof NoteEventVector)) {
			return false;
		}

		final NoteEventVector other = (NoteEventVector) o;
		if (other.getDimensionality() != this.getDimensionality()) {
			return false;
		}

		return this.compareTo(other) == 0;
	}

	private int computeHash() {
		// TODO: Improbe the hash function
		int multiplierIndex = 0;
		long hash = RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);

		for (int i = 0; i < this.components.length; ++i) {
			final long bits = Double.doubleToRawLongBits(this.components[i]);
			final int first = (int) (bits >> 32);
			hash += first * RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);
			final int second = (int) bits;
			hash += second * RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);
		}

		return (int) hash;
	}

	@Override
	public int hashCode() {
		return this.hash;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("(");
		for (int i = 0; i < this.components.length - 1; ++i) {
			strBuilder.append(Double.toString(this.components[i])).append(", ");
		}
		strBuilder.append(Double.toString(this.components[this.components.length - 1])).append(")");

		if (this.position != null) {
			strBuilder.append(" at ").append(position);
		}

		return strBuilder.toString();
	}
}
