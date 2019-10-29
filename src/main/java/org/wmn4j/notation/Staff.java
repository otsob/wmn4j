/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Represents a staff in a score.
 * <p>
 * This class is immutable.
 */
public final class Staff implements Iterable<Measure> {

	/**
	 * Type of staff.
	 */
	public enum Type {
		/**
		 * Normal five line staff.
		 */
		NORMAL,
		/**
		 * A single line percussion staff.
		 */
		SINGLE_LINE
	}

	private final List<Measure> measures;
	private final Type type;

	/**
	 * Returns a staff with the given measures.
	 *
	 * @param measures the measures in the staff
	 * @return a staff with the given measures
	 */
	public static Staff of(List<Measure> measures) {
		return new Staff(measures);
	}

	/**
	 * Constructor.
	 *
	 * @param measures the measures in the staff.
	 */
	private Staff(List<Measure> measures) {
		// TODO: Add constructor that allows setting staff type.
		this.type = Type.NORMAL;

		final List<Measure> tmpMeasures = new ArrayList<>();

		// Add placeholder for pickup if there is none.
		if (measures.get(0).getNumber() != 0) {
			tmpMeasures.add(null);
		}

		tmpMeasures.addAll(measures);

		this.measures = Collections.unmodifiableList(tmpMeasures);
	}

	/**
	 * Returns the measure with the given number.
	 *
	 * @param number the number of the measure to get from this staff
	 * @return the measure with the given number
	 */
	public Measure getMeasure(int number) {
		if (!this.hasPickupMeasure() && number < 1 || number > getFullMeasureCount()) {
			throw new NoSuchElementException("No measure with number " + number + " in staff");
		}

		return this.measures.get(number);
	}

	/**
	 * Returns the number of measures in this staff. Includes the pickup measure if
	 * one is present.
	 *
	 * @return the number of measures in this staff
	 */
	public int getMeasureCount() {
		int measureCount = this.getFullMeasureCount();

		if (this.hasPickupMeasure()) {
			++measureCount;
		}

		return measureCount;
	}

	/**
	 * Returns the number of full measures in this staff. Pickup measure is
	 * excluded.
	 *
	 * @return the number of full measures in this staff
	 */
	public int getFullMeasureCount() {
		return this.measures.size() - 1;
	}

	/**
	 * Returns true if this staff contains a pickup measure.
	 *
	 * @return true if this staff begins with a pickup measure, false otherwise
	 */
	public boolean hasPickupMeasure() {
		return this.measures.get(0) != null;
	}

	/**
	 * Returns the type of this staff.
	 *
	 * @return type of this staff
	 */
	public Type getType() {
		return this.type;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Staff: ").append("\n");

		for (Measure measure : this) {
			strBuilder.append(measure.toString()).append("\n");
		}

		return strBuilder.toString();
	}

	@Override
	public Iterator<Measure> iterator() {
		final Iterator<Measure> iter = this.measures.iterator();

		// If there is no pickup measure go to next
		if (!this.hasPickupMeasure()) {
			iter.next();
		}

		return iter;
	}
}
