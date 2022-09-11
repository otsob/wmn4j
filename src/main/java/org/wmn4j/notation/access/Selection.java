/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Part;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Represents a selection of notation objects from a score.
 * <p>
 * A selection can be a range of measures from a score, or a selection of measures
 * from specific parts of a score.
 * <p>
 * Implementations of this class are immutable.
 */
public interface Selection extends Iterable<Durational> {

	/**
	 * Returns the measure number from which the selection range
	 * starts (inclusive).
	 *
	 * @return the measure number from which the selection range
	 * starts  (inclusive)
	 */
	int getFirst();

	/**
	 * Returns the measure number to which the selection range ends (inclusive).
	 *
	 * @return the measure number to which the selection range ends (inclusive)
	 */
	int getLast();

	/**
	 * Returns the indices of the parts in this selection in ascending order.
	 *
	 * @return the indices of the parts in this selection in ascending order
	 */
	List<Integer> getPartIndices();

	/**
	 * Returns the part at the given index.
	 *
	 * @param index the index of the part
	 * @return the part at the given index
	 */
	Part getPart(int index);

	/**
	 * Returns an iterator that also provides access to the positions of the {@link Durational}
	 * objects in this selection. The iterator iterates the selection by going through each
	 * part before moving on to the next part.
	 *
	 * @return an iterator that also provides access to the positions
	 */
	PositionIterator partwiseIterator();

	/**
	 * Returns a selection of measures from this selection.
	 *
	 * @param firstMeasure the measure number of the first measure included in the selection
	 * @param lastMeasure  the measure number of the last measure included in the selection
	 * @return a subrange of measures from this selection
	 */
	Selection subSelection(int firstMeasure, int lastMeasure);

	/**
	 * Returns a selection or parts from this selection.
	 *
	 * @param partIndices the indices of the parts to select
	 * @return a selection or parts from this selection
	 */
	Selection subSelection(Collection<Integer> partIndices);

	/**
	 * Returns an enumeration of the durational elements in this selection.
	 * <p>
	 * The enumeration is similar to pairing indices with elements (e.g. enumerate in Python),
	 * but instead of indices the positions are handled using {@link Position} types.
	 * <p>
	 * The contents of the selection are iterated in partwise order (see {@link Selection#partwiseIterator}).
	 *
	 * @return an enumeration of the durational elements in this selection
	 */
	default Iterable<Positional> enumeratePartwise() {
		return new PositionalEnumerator(partwiseIterator());
	}

	/**
	 * Returns a stream of the durational elements in this selection.
	 *
	 * @return a stream of the durational elements in this selection
	 */
	default Stream<Durational> durationalStream() {
		return StreamSupport.stream(spliterator(), false);
	}

	/**
	 * Returns a stream of positional for the elements in this selection.
	 *
	 * @return a stream of positional for the elements in this selection
	 */
	default Stream<Positional> positionalStream() {
		return StreamSupport.stream(enumeratePartwise().spliterator(), false);
	}
}
