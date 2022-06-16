/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.PartIterator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a score part with multiple staves such as are often used with
 * keyboard instruments.
 * <p>
 * This class is immutable.
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
		attributes.put(Attribute.NAME, name);
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
	public Optional<String> getName() {
		return this.getAttribute(Part.Attribute.NAME);
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

	@Override
	public Staff getStaff(int number) {
		return this.staves.get(number);
	}

	@Override
	public List<Integer> getStaffNumbers() {
		return new ArrayList<>(this.staves.keySet());
	}

	@Override
	public Optional<String> getAttribute(Attribute attribute) {
		return Optional.ofNullable(partAttributes.getOrDefault(attribute, null));
	}

	@Override
	public Measure getMeasure(int staffNumber, int measureNumber) {
		if (!this.staves.containsKey(staffNumber)) {
			throw new NullPointerException("No staff with number " + staffNumber);
		}

		return this.staves.get(staffNumber).getMeasure(measureNumber);
	}

	@Override
	public PartIterator getPartIterator() {
		return new MultiStaffPart.Iter(this, hasPickupMeasure() ? 0 : 1, getFullMeasureCount());
	}

	@Override
	public PartIterator getPartIterator(int firstMeasure, int lastMeasure) {
		return new MultiStaffPart.Iter(this, firstMeasure, lastMeasure);
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
		private final MultiStaffPart part;
		private int keyIndex;
		private final List<Integer> keys;
		private int nextMeasureNumber;
		private int prevStaffNumber = 0;
		private int prevMeasureNumber = 0;

		private final int lastMeasure;

		/**
		 * Constructor.
		 *
		 * @param part the part for which the iterator is created
		 */
		Iter(MultiStaffPart part, int firstMeasure, int lastMeasure) {
			this.part = part;
			this.keyIndex = 0;
			this.keys = this.part.getStaffNumbers();
			this.nextMeasureNumber = firstMeasure;
			this.lastMeasure = lastMeasure;
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
			return this.nextMeasureNumber <= this.lastMeasure;
		}

		@Override
		public Measure next() {
			Measure measure = null;

			if (this.hasNext()) {
				this.prevStaffNumber = this.keys.get(keyIndex);
				this.prevMeasureNumber = this.nextMeasureNumber;
				measure = this.part.staves.get(this.prevStaffNumber).getMeasure(this.prevMeasureNumber);

				++keyIndex;
				if (keyIndex == this.keys.size()) {
					keyIndex = 0;
					++this.nextMeasureNumber;
				}
			} else {
				throw new NoSuchElementException();
			}

			return measure;
		}
	}
}
