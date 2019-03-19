/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a part in a score.
 */
public interface Part extends Iterable<Measure> {

	/**
	 * Attribute types that a part can have.
	 */
	enum Attribute {
		NAME, ABBREVIATED_NAME
	};

	/**
	 * Returns the name of this part.
	 *
	 * @return name of this part
	 */
	String getName();

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
	 * Returns the given attribute. If the attribute is not present, returns an
	 * empty string.
	 *
	 * @param attribute the attribute to return from this part
	 * @return the value of the attribute, or an empty string if the attribute is
	 *         not set
	 */
	String getAttribute(Attribute attribute);

	/**
	 * Returns the number of measures in this part. The count is based on the
	 * measure numbers, so even if a part has multiple staves its measure count is
	 * the largest measure number.
	 *
	 * @return number of measures in the part. If there is a pickup measure, it is
	 *         included in the count.
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
	 * @throws NoSuchElementException if there is no staff of measure with the given
	 *                                number in this part
	 */
	Measure getMeasure(int staffNumber, int measureNumber) throws NoSuchElementException;

	/**
	 * Returns a part iterator that can be used to iterate through the measures in
	 * this part.
	 *
	 * @return an iterator that can be used to iterate through the measures in this
	 *         part
	 */
	Part.Iter getPartIterator();

	/**
	 * Interface for Part Iterators.
	 */
	public interface Iter extends Iterator<Measure> {

		/**
		 * Returns the staff number of the measure that was returned on the previous
		 * call of next.
		 *
		 * @return the staff number of the previous measure
		 */
		int getStaffNumberOfPrevious();

		/**
		 * Returns the measure number of the measure that was returned on the previous
		 * call of next.
		 *
		 * @return the measure number of the previous measure
		 */
		int getMeasureNumberOfPrevious();
	}
}