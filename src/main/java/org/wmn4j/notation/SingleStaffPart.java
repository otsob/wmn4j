/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.PartIterator;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents a part with a single staff in a score.
 * <p>
 * This class is immutable.
 */
public final class SingleStaffPart implements Part {

	private final Map<Part.Attribute, String> partAttributes;
	private final Staff staff;

	/**
	 * Returns a part with a single staff with the given name and staff.
	 *
	 * @param name  the name of the part
	 * @param staff the staff of this part
	 * @return a part with a single staff with the given name and measures
	 */
	public static SingleStaffPart of(String name, Staff staff) {
		final Map<Part.Attribute, String> attributes = new HashMap<>();
		attributes.put(Attribute.NAME, name);
		return new SingleStaffPart(attributes, staff);
	}

	/**
	 * Returns a part with a single staff with the given attributes and staff.
	 *
	 * @param partAttributes a map of attributes to be set for this part
	 * @param staff          the staff of this part
	 * @return a part with a single staff with the given attributes and measures
	 */
	public static SingleStaffPart of(Map<Part.Attribute, String> partAttributes, Staff staff) {
		return new SingleStaffPart(partAttributes, staff);
	}

	/**
	 * Constructor.
	 *
	 * @param partAttributes a map of attributes to be set for this part
	 * @param staff          the staff of this part
	 */
	private SingleStaffPart(Map<Part.Attribute, String> partAttributes, Staff staff) {
		this.staff = staff;
		this.partAttributes = Collections.unmodifiableMap(new HashMap<>(partAttributes));
	}

	@Override
	public Optional<String> getName() {
		return this.getAttribute(Attribute.NAME);
	}

	@Override
	public boolean isMultiStaff() {
		return false;
	}

	@Override
	public int getStaffCount() {
		return 1;
	}

	@Override
	public List<Integer> getStaffNumbers() {
		return Collections.singletonList(DEFAULT_STAFF_NUMBER);
	}

	/**
	 * Returns the measure with the given number.
	 *
	 * @param number number of measure to return
	 * @return measure with the given number
	 */
	public Measure getMeasure(int number) {
		return this.staff.getMeasure(number);
	}

	@Override
	public Measure getMeasure(int staffNumber, int measureNumber) {
		if (staffNumber != DEFAULT_STAFF_NUMBER) {
			throw new NoSuchElementException("No staff with number " + staffNumber + " in a single staff part");
		}
		return this.getMeasure(measureNumber);
	}

	/**
	 * Returns the staff in this part.
	 *
	 * @return the only staff in this part
	 */
	public Staff getStaff() {
		return this.staff;
	}

	@Override
	public PartIterator getPartIterator() {
		return new SingleStaffPart.Iter(this, hasPickupMeasure() ? 0 : 1, getFullMeasureCount());
	}

	@Override
	public PartIterator getPartIterator(int firstMeasure, int lastMeasure) {
		return new SingleStaffPart.Iter(this, firstMeasure, lastMeasure);
	}

	@Override
	public Optional<String> getAttribute(Attribute attribute) {
		return Optional.ofNullable(partAttributes.getOrDefault(attribute, null));
	}

	@Override
	public int getMeasureCount() {
		return this.staff.getMeasureCount();
	}

	@Override
	public int getFullMeasureCount() {
		return this.staff.getFullMeasureCount();
	}

	@Override
	public String toString() {
		final StringBuilder builder = new StringBuilder();
		builder.append("Part: ");

		for (Attribute attr : this.partAttributes.keySet()) {
			builder.append(attr).append(": ").append(this.partAttributes.get(attr));
		}

		for (Measure m : this) {
			builder.append("\n").append(m.toString());
		}

		return builder.toString();
	}

	class Iter implements PartIterator {

		private final Staff staff;
		private int nextMeasureNumber;
		private final int lastMeasureNumber;

		Iter(SingleStaffPart part, int firstMeasureNumber, int lastMeasureNumber) {
			this.nextMeasureNumber = firstMeasureNumber;
			this.lastMeasureNumber = lastMeasureNumber;
			this.staff = part.getStaff();
		}

		@Override
		public int getStaffNumberOfPrevious() {
			return Part.DEFAULT_STAFF_NUMBER;
		}

		@Override
		public int getMeasureNumberOfPrevious() {
			return this.nextMeasureNumber - 1;
		}

		@Override
		public boolean hasNext() {
			return this.nextMeasureNumber <= this.lastMeasureNumber;
		}

		@Override
		public Measure next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			return this.staff.getMeasure(nextMeasureNumber++);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Removing is not supported.");
		}
	}
}
