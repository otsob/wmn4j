/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.PartIterator;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents a part in a score.
 * <p>
 * Implementations of this class are expected to be thread-safe.
 */
public interface Part extends Iterable<Measure> {

	/**
	 * Attribute types that a part can have.
	 */
	enum Attribute {
		NAME, ABBREVIATED_NAME
	}

	/**
	 * Returns the name of this part or empty if the part has
	 * no name.
	 *
	 * @return name of this part
	 */
	Optional<String> getName();

	/**
	 * Returns true if this part has multiple staves.
	 *
	 * @return true if this part has multiple staves
	 */
	boolean isMultiStaff();

	/**
	 * Returns the number of staves in this part.
	 *
	 * @return number of staves in this part
	 */
	int getStaffCount();

	/**
	 * Returns the numbers of the staves in this part in ascending order.
	 *
	 * @return the numbers of the staves in this part in ascending order
	 */
	List<Integer> getStaffNumbers();

	/**
	 * Returns the given attribute. If the attribute is not present, returns
	 * empty.
	 *
	 * @param attribute the attribute to return from this part
	 * @return the value of the attribute, or empty if the attribute is
	 * not set
	 */
	Optional<String> getAttribute(Attribute attribute);

	/**
	 * Returns true if this part has a pickup measure.
	 *
	 * @return true if this part has a pickup measure, false otherwise
	 */
	default boolean hasPickupMeasure() {
		return getMeasureCount() > getFullMeasureCount();
	}

	/**
	 * Returns the number of measures in this part. The count is based on the
	 * measure numbers, so even if a part has multiple staves its measure count is
	 * the largest measure number.
	 *
	 * @return number of measures in the part. If there is a pickup measure, it is
	 * included in the count.
	 */
	int getMeasureCount();

	/**
	 * Returns the the number of complete measures. Does not include the measure
	 * number if there is one.
	 *
	 * @return the number of measures excluding the pickup measure
	 */
	int getFullMeasureCount();

	/**
	 * Returns the measure with the given number from the staff with the given
	 * number.
	 *
	 * @param staffNumber   the number of the staff from which the get the measure
	 * @param measureNumber the number of the measure
	 * @return the measure with measureNumber from the staff with staffNumber
	 * @throws NoSuchElementException if there is no staff or measure with the given
	 *                                number in this part
	 */
	Measure getMeasure(int staffNumber, int measureNumber) throws NoSuchElementException;

	/**
	 * Returns a part iterator that can be used to iterate through the measures in
	 * this part.
	 *
	 * @return an iterator that can be used to iterate through the measures in this
	 * part
	 */
	PartIterator getPartIterator();

	/**
	 * Returns a part iterator that can be used to iterate through the measures in
	 * this part.
	 *
	 * @param firstMeasure the measure number of the first measure to be included in iteration
	 * @param lastMeasure  the measure number of the last measure to be included in iteration
	 * @return an iterator that can be used to iterate through the measures in this
	 * part
	 */
	PartIterator getPartIterator(int firstMeasure, int lastMeasure);

	@Override
	default Iterator<Measure> iterator() {
		return getPartIterator();
	}
}
