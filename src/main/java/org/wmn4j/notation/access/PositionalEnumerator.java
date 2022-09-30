/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import java.util.Iterator;

/**
 * Represents an enumeration of durational elements, pairing durational elements with their
 * positions.
 */
public final class PositionalEnumerator implements Iterable<Positional> {

	private final PositionIterator iterator;

	/**
	 * Creates a new enumerator from the given iterator.
	 *
	 * @param iterator the iterator from which the enumerator is created
	 */
	public PositionalEnumerator(PositionIterator iterator) {
		this.iterator = iterator;
	}

	private final class PositionalIterator implements Iterator<Positional> {
		private final PositionIterator iter;

		private PositionalIterator(PositionIterator iterator) {
			this.iter = iterator;
		}

		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public Positional next() {
			return new Positional(iter.next(), iter.getPositionOfPrevious());
		}
	}

	@Override
	public Iterator<Positional> iterator() {
		return new PositionalIterator(iterator);
	}
}
