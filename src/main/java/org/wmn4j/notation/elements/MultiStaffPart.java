/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a score part with multiple staves such as are often used with
 * keyboard instruments. This class is immutable.
 */
public final class MultiStaffPart implements Part {

	private final Map<Part.Attribute, String> partAttributes;
	private final SortedMap<Integer, Staff> staves;

	/**
	 * Returns a part with multiple staves with the given values. The staves are
	 * associated with numbers which are to be given as keys in the map parameter.
	 *
	 * @param name   the name of the part
	 * @param staves the staves in the part
	 * @return a part with multiple staves with the given values
	 */
	public static MultiStaffPart of(String name, Map<Integer, Staff> staves) {
		Map<Part.Attribute, String> attributes = new HashMap<>();
		return new MultiStaffPart(attributes, staves);
	}

	/**
	 * Returns a part with multiple staves with the given values. The staves are
	 * associated with numbers which are to be given as keys in the map parameter.
	 *
	 * @param attributes the attributes of the part
	 * @param staves     the staves in the part
	 * @return a part with multiple staves with the given values
	 */
	public static MultiStaffPart of(Map<Part.Attribute, String> attributes, Map<Integer, Staff> staves) {
		return new MultiStaffPart(attributes, staves);
	}

	/**
	 * Constructor. The staves are associated with numbers which are to be given as
	 * keys in the map parameter.
	 *
	 * @param attributes the attributes of the part
	 * @param staves     the staves in the part
	 */
	private MultiStaffPart(Map<Part.Attribute, String> attributes, Map<Integer, Staff> staves) {
		this.partAttributes = Collections.unmodifiableMap(new HashMap<>(attributes));
		this.staves = Collections.unmodifiableSortedMap(new TreeMap<>(staves));
	}

	@Override
	public String getName() {
		return this.getPartAttribute(Part.Attribute.NAME);
	}

	@Override
	public boolean isMultiStaff() {
		return true;
	}

	@Override
	public int getStaffCount() {
		return this.staves.keySet().size();
	}

	@Override
	public int getMeasureCount() {
		return this.staves.get(this.getStaffNumbers().get(0)).getMeasureCount();
	}

	@Override
	public int getFullMeasureCount() {
		return this.staves.get(this.getStaffNumbers().get(0)).getFullMeasureCount();
	}

	/**
	 * Returns the {@link Staff} with the given number in this part.
	 *
	 * @param number number of staff
	 * @return the staff associated with the number
	 */
	public Staff getStaff(int number) {
		return this.staves.get(number);
	}

	/**
	 * Returns the numbers in this part that are used to denote the staves.
	 *
	 * @return the staff numbers in this part
	 */
	public List<Integer> getStaffNumbers() {
		return new ArrayList<>(this.staves.keySet());
	}

	@Override
	public String getPartAttribute(Attribute attribute) {
		if (this.partAttributes.containsKey(attribute)) {
			return this.partAttributes.get(attribute);
		} else {
			return "";
		}
	}

	@Override
	public Measure getMeasure(int staffNumber, int measureNumber) {
		return this.staves.get(staffNumber).getMeasure(measureNumber);
	}

	@Override
	public Part.Iter getPartIterator() {
		return new MultiStaffPart.Iter(this);
	}

	@Override
	public Iterator<Measure> iterator() {
		return this.getPartIterator();
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

	/**
	 * Iterator for {@link MultiStaffPart}. Iterates through the measures by going
	 * through all staves for a certain measure number before going on to the next
	 * measure. Staves are iterated through from smallest staff number to greatest.
	 * Does not support removing.
	 */
	public static class Iter implements Part.Iter {
		private final MultiStaffPart part;
		private int keyIndex;
		private final List<Integer> keys;
		private int measureNumber;
		private int prevStaffNumber = 0;
		private int prevMeasureNumber = 0;

		/**
		 * Constructor.
		 *
		 * @param part the part for which the iterator is created
		 */
		public Iter(MultiStaffPart part) {
			this.part = part;
			this.keyIndex = 0;
			this.keys = this.part.getStaffNumbers();
			this.measureNumber = 1;

			// If there is a pickup measure start from measure 0.
			if (this.part.staves.get(this.keys.get(0)).hasPickupMeasure()) {
				this.measureNumber = 0;
			}
		}

		@Override
		public int getStaffNumberOfPrevious() {
			return this.prevStaffNumber;
		}

		@Override
		public int getMeasureNumberOfPrevious() {
			return this.prevMeasureNumber;
		}

		@Override
		public boolean hasNext() {
			return this.measureNumber <= this.part.getFullMeasureCount();
		}

		@Override
		public Measure next() {
			Measure measure = null;

			if (this.hasNext()) {
				this.prevStaffNumber = this.keys.get(keyIndex);
				this.prevMeasureNumber = this.measureNumber;
				measure = this.part.staves.get(this.prevStaffNumber).getMeasure(this.prevMeasureNumber);

				++keyIndex;
				if (keyIndex == this.keys.size()) {
					keyIndex = 0;
					++this.measureNumber;
				}
			} else {
				throw new NoSuchElementException();
			}

			return measure;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Removing not supported.");
		}
	}
}
