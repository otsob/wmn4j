/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.PartIterator;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents a part with a single staff in a score. This class is immutable.
 */
public final class SingleStaffPart implements Part {

	/**
	 * The default staff number for the staff in a single staff part.
	 */
	public static final int STAFF_NUMBER = 1;

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
		return Collections.singletonList(STAFF_NUMBER);
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
		if (staffNumber != STAFF_NUMBER) {
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
		return new SingleStaffPart.Iter(this);
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

	private static class Iter implements PartIterator {

		private final Iterator<Measure> staffIterator;
		private int prevMeasureNumber = 0;

		Iter(SingleStaffPart part) {
			this.staffIterator = part.getStaff().iterator();
		}

		@Override
		public int getStaffNumberOfPrevious() {
			return SingleStaffPart.STAFF_NUMBER;
		}

		@Override
		public int getMeasureNumberOfPrevious() {
			return this.prevMeasureNumber;
		}

		@Override
		public boolean hasNext() {
			return this.staffIterator.hasNext();
		}

		@Override
		public Measure next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			final Measure next = this.staffIterator.next();
			this.prevMeasureNumber = next.getNumber();
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Removing is not supported.");
		}
	}
}
