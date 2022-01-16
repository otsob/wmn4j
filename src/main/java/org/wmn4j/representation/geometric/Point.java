/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.representation.geometric;

/**
 * Point (vector) type for representing note events.
 * <p>
 * The points are generic and the operations on them are only defined
 * for the same type of points (e.g., it is not possible to add points of
 * different types together).
 * <p>
 * Classes implementing this interface are required to be immutable.
 *
 * @param <T> The concrete instantiation of the point type.
 */
public interface Point<T extends Point> extends Comparable<T> {

	/**
	 * Returns the dimensionality of this point.
	 *
	 * @return the dimensionality of this point
	 */
	int getDimensionality();

	/**
	 * Returns the sum of this point with the given point.
	 *
	 * @param point the point that is added to this point to compute the sum
	 * @return the sum of this point with the given point
	 */
	T add(T point);

	/**
	 * Returns the difference of this point with the given point.
	 *
	 * @param point the point that is subtracted from this point to compute the difference
	 * @return the difference of this point with the given point
	 */
	T subtract(T point);
}
