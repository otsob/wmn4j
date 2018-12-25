/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Interface for parts in a score. The class <code>PartBuilder</code> can be
 * used for constructing <code>Part</code> objects.
 *
 * @author Otso Björklund
 */
public interface Part extends Iterable<Measure> {

	/**
	 * Attribute types that a <code>Part</code> can have.
	 */
	enum Attribute {
	NAME, ABBR_NAME
	};

	/**
	 * @return name of this <code>Part</code>.
	 */
	String getName();

	/**
	 * @return true if this <code>Part</code> has multiple staves. False otherwise.
	 */
	boolean isMultiStaff();

	/**
	 * @return number of staves in this part.
	 */
	int getStaffCount();

	/**
	 * @param attribute the Attribute to get from this <code>Part</code>.
	 * @return the String associated with the attribute, or an empty string if the
	 *         attribute is not set.
	 */
	String getPartAttribute(Attribute attribute);

	/**
	 * Get the number of measures in the part. The count is based on the measure
	 * numbers, so even if a part has multiple staves its measure count is the
	 * largest measure number.
	 *
	 * @return number of measures in the part. If there is a pickup measure, it is
	 *         included in the count.
	 */
	int getMeasureCount();

	/**
	 * Get the number of complete measures.
	 *
	 * @return the number of measures excluding the pickup measure.
	 */
	int getFullMeasureCount();

	/**
	 * Returns the measure with the staff and measure number.
	 *
	 * @param staffNumber   Number of staff. Is not used when accessing a single
	 *                      staff part.
	 * @param measureNumber the number of the measure
	 * @return the measure with measureNumber from the staff with staffNumber
	 * @throws NoSuchElementException if there is no staff of measure with the given
	 *                                number in this part
	 */
	Measure getMeasure(int staffNumber, int measureNumber) throws NoSuchElementException;

	/**
	 * @return An iterator that implements the Part.Iter interface.
	 */
	Part.Iter getPartIterator();

	/**
	 * Interface for Part Iterators.
	 */
	public interface Iter extends Iterator<Measure> {

		/**
		 * @return The number of the <code>Staff</code> from which the
		 *         <code>Measure</code> on the previous call of next was returned.
		 */
		int getStaffNumberOfPrevious();

		/**
		 * @return The number of the <code>Measure</code> that was returned by the
		 *         previous call of next.
		 */
		int getMeasureNumberOfPrevious();
	}
}
