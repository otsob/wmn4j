/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MultiStaffPart;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.SingleStaffPart;
import org.wmn4j.notation.elements.Staff;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Builder for building {@link Part} objects.
 */
public class PartBuilder {

	private final Map<Integer, List<MeasureBuilder>> staveContents = new HashMap<>();
	private final Map<Part.Attribute, String> partAttributes = new HashMap<>();
	private static final int SINGLE_STAFF_NUMBER = SingleStaffPart.STAFF_NUMBER;

	/**
	 * Constructor.
	 *
	 * @param name the name of the part to be built.
	 */
	public PartBuilder(String name) {
		this.partAttributes.put(Part.Attribute.NAME, name);
	}

	/**
	 * Returns the number of staves set in this builder.
	 *
	 * @return the number of staves in this builder
	 */
	public int getStaffCount() {
		return this.staveContents.size();
	}

	/**
	 * Returns the number of measures in the longest staff in this builder.
	 * <p>
	 * For a single staff part the returned number is the number of measures. For a part with multiple staves
	 * the returned number is the number of measures in the staff with the largest number of measures.
	 *
	 * @return the number of measures in the longest staff in this builder
	 */
	public int getMeasureCount() {
		return staveContents.values().stream().map(staff -> staff.size()).max(Integer::compareTo).orElse(0);
	}

	/**
	 * Adds a {@link MeasureBuilder} to this builder. This is used for building
	 * parts with a single staff.
	 *
	 * @param measureBuilder the measureBuilder that is added to the end of the
	 *                       staff
	 * @return reference to this
	 */
	public PartBuilder add(MeasureBuilder measureBuilder) {
		this.addToStaff(SINGLE_STAFF_NUMBER, measureBuilder);
		return this;
	}

	/**
	 * Adds a {@link MeasureBuilder} to the staff with the given number. This is
	 * used for building parts with multiple staves.
	 *
	 * @param staffNumber    the number of the staff to which measureBuilder is
	 *                       added
	 * @param measureBuilder the measureBuilder that is added to the end of the
	 *                       staff
	 * @return reference to this
	 */
	public PartBuilder addToStaff(int staffNumber, MeasureBuilder measureBuilder) {
		if (!this.staveContents.containsKey(staffNumber)) {
			this.staveContents.put(staffNumber, new ArrayList<>());
		}

		this.staveContents.get(staffNumber).add(Objects.requireNonNull(measureBuilder));
		return this;
	}

	/**
	 * Sets the given attribute to the given value.
	 *
	 * @param attribute the attribute to be set
	 * @param value     the value that will be set for the attribute
	 */
	public void setAttribute(Part.Attribute attribute, String value) {
		this.partAttributes.put(attribute, value);
	}

	/**
	 * Returns the name set in this builder.
	 *
	 * @return the name set in this builder
	 */
	public String getName() {
		return this.partAttributes.get(Part.Attribute.NAME);
	}

	private List<Measure> getBuiltMeasures(List<MeasureBuilder> builders) {
		return builders.stream().map(measureBuilder -> measureBuilder.build(false, false)).collect(Collectors.toList());
	}

	/**
	 * Returns a part with the contents set in this builder.
	 *
	 * @return a part with the contents set in this builder
	 */
	public Part build() {
		if (this.staveContents.size() == 1) {
			return SingleStaffPart.of(this.partAttributes,
					getBuiltMeasures(this.staveContents.get(SINGLE_STAFF_NUMBER)));
		} else {
			padShorterStavesWithRestMeasures();
			final Map<Integer, Staff> staves = new HashMap<>();
			for (int staffNumber : this.staveContents.keySet()) {
				staves.put(staffNumber, Staff.of(getBuiltMeasures(this.staveContents.get(staffNumber))));
			}

			return MultiStaffPart.of(this.partAttributes, staves);
		}
	}

	private void padShorterStavesWithRestMeasures() {
		Optional<List<MeasureBuilder>> longestStaffOpt = staveContents.values().stream()
				.max(Comparator.comparing(List::size));

		if (longestStaffOpt.isPresent()) {
			final List<MeasureBuilder> longestStaff = longestStaffOpt.get();
			final int longestStaffMeasureCount = longestStaff.size();

			for (List<MeasureBuilder> staffContents : staveContents.values()) {

				for (int i = staffContents.size(); i < longestStaffMeasureCount; ++i) {
					staffContents.add(MeasureBuilder.withAttributesOf(longestStaff.get(i)));
				}
			}
		}
	}
}
