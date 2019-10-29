package org.wmn4j.notation.access;

import org.wmn4j.notation.Measure;

import java.util.Iterator;

/**
 * Iterator for iterating through the measures in a part. The measures are iterated in their temporal order, i.e., the
 * order of measure numbers. For parts with multiple staves the measures with the same number
 * are iterated starting from the staff with the smallest number. Does not support removing.
 * <p>
 * Instances of this class are not thread-safe.
 */
public interface PartIterator extends Iterator<Measure> {

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

	@Override
	default void remove() {
		throw new UnsupportedOperationException("Removing not supported.");
	}
}
