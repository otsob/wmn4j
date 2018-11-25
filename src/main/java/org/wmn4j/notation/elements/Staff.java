/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class for representing a staff in a score. This class is immutable.
 * 
 * @author Otso Björklund
 */
public class Staff implements Iterable<Measure> {

	/**
	 * Type of staff.
	 */
	public enum Type {
		NORMAL, SINGLE_LINE
	};

	private final List<Measure> measures;
	private final Type type;

	/**
	 * Constructor
	 * 
	 * @param measures
	 *            the measures in the staff.
	 */
	public Staff(List<Measure> measures) {
		// TODO: Add constructor that allows setting staff type.
		this.type = Type.NORMAL;

		List<Measure> tmpMeasures = new ArrayList<>();

		// Add placeholder for pickup if there is none.
		if (measures.get(0).getNumber() != 0)
			tmpMeasures.add(null);

		tmpMeasures.addAll(measures);

		this.measures = Collections.unmodifiableList(tmpMeasures);
	}

	/**
	 * Get <code>Measure</code> by measure number.
	 * 
	 * @param number
	 *            the number of the measure to get from this staff.
	 * @return the measure with number
	 */
	public Measure getMeasure(int number) {
		if (!this.hasPickupMeasure() && number < 1)
			throw new NoSuchElementException();

		return this.measures.get(number);
	}

	/**
	 * @return number of measures in this <code>Staff</code>.
	 */
	public int getMeasureCount() {
		int measureCount = this.getFullMeasureCount();

		if (this.hasPickupMeasure())
			++measureCount;

		return measureCount;
	}

	/**
	 * @return number of full measures in this <code>Staff</code>. Pickup measure is
	 *         not included.
	 */
	public int getFullMeasureCount() {
		return this.measures.size() - 1;
	}

	/**
	 * @return List of measures in this staff in order from first measure to last.
	 */
	public List<Measure> getMeasures() {
		if (!this.hasPickupMeasure())
			return this.measures.subList(1, this.measures.size());

		return this.measures;
	}

	/**
	 * @return true if this staff begins with a pickup measure. false otherwise.
	 */
	public boolean hasPickupMeasure() {
		return this.measures.get(0) != null;
	}

	/**
	 * @return type of this <code>Staff</code>.
	 */
	public Type getType() {
		return this.type;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Staff: ").append("\n");

		for (Measure measure : this) {
			strBuilder.append(measure.toString()).append("\n");
		}

		return strBuilder.toString();
	}

	/**
	 * @return iterator that does not support modifying this <code>Staff</code>.
	 */
	@Override
	public Iterator<Measure> iterator() {
		Iterator<Measure> iter = this.measures.iterator();

		// If there is no pickup measure go to next
		if (!this.hasPickupMeasure())
			iter.next();

		return iter;
	}
}
