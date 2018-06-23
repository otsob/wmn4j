/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class for representing a part consisting of a single staff in a score. This
 * class is immutable.
 * 
 * @author Otso Björklund
 */
public class SingleStaffPart implements Part {

	public static final int STAFF_NUMBER = 1;
	private final Map<Part.Attribute, String> partAttributes;
	private final Staff staff;

	/**
	 * @param name
	 *            the name of the part.
	 * @param measures
	 *            the measures in this part.
	 */
	public SingleStaffPart(String name, List<Measure> measures) {
		this.staff = new Staff(measures);
		Map<Part.Attribute, String> attributes = new HashMap<>();
		attributes.put(Attribute.NAME, name);
		this.partAttributes = Collections.unmodifiableMap(attributes);
	}

	/**
	 * @param partAttributes
	 *            a map of attributes to be set for this part.
	 * @param measures
	 *            the measures in this part.
	 */
	public SingleStaffPart(Map<Part.Attribute, String> partAttributes, List<Measure> measures) {
		this.staff = new Staff(measures);
		this.partAttributes = Collections.unmodifiableMap(new HashMap<>(partAttributes));
	}

	@Override
	public String getName() {
		return this.getPartAttribute(Attribute.NAME);
	}

	@Override
	public boolean isMultiStaff() {
		return false;
	}

	@Override
	public int getStaffCount() {
		return 1;
	}

	/**
	 * Get the <code>Measure</code> with the given number.
	 * 
	 * @param number
	 *            number of measure.
	 * @return measure with the given number.
	 */
	public Measure getMeasure(int number) {
		return this.staff.getMeasure(number);
	}

	@Override
	public Measure getMeasure(int staffNumber, int measureNumber) {
		return this.getMeasure(measureNumber);
	}

	/**
	 * @return the only staff in this part.
	 */
	public Staff getStaff() {
		return this.staff;
	}

	@Override
	public Part.Iter getPartIterator() {
		return new SingleStaffPart.Iter(this);
	}

	@Override
	public Iterator<Measure> iterator() {
		return this.getPartIterator();
	}

	@Override
	public String getPartAttribute(Attribute attribute) {
		if (this.partAttributes.containsKey(attribute))
			return this.partAttributes.get(attribute);
		else
			return "";
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
		StringBuilder builder = new StringBuilder();
		builder.append("Part: ");

		for (Attribute attr : this.partAttributes.keySet())
			builder.append(attr).append(": ").append(this.partAttributes.get(attr));

		for (Measure m : this)
			builder.append("\n").append(m.toString());

		return builder.toString();
	}

	/**
	 * Iterator for <code>SingleStaffPart</code>. Iterates through measures starting
	 * from smallest measure number. Does not support removing.
	 */
	public static class Iter implements Part.Iter {

		private final Iterator<Measure> staffIterator;
		private int prevMeasureNumber = 0;

		public Iter(SingleStaffPart part) {
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
			if (!this.hasNext())
				throw new NoSuchElementException();

			Measure next = this.staffIterator.next();
			this.prevMeasureNumber = next.getNumber();
			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Removing is not supported.");
		}
	}
}
