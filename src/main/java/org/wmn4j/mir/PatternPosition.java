/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.iterators.Position;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Represents the position of a {@link org.wmn4j.mir.Pattern} in a {@link org.wmn4j.notation.elements.Score}.
 * This class is immutable.
 */
public final class PatternPosition {

	private final SortedMap<Integer, Set<Position>> positionsPerPart;

	/**
	 * Creates a new pattern position with the given positions.
	 *
	 * @param positions the positions that make up the position of the pattern in a score
	 */
	public PatternPosition(Collection<Position> positions) {
		this.positionsPerPart = partitionByPart(positions);
	}

	private SortedMap<Integer, Set<Position>> partitionByPart(Collection<Position> positions) {
		SortedMap<Integer, Set<Position>> partitionedPositions = new TreeMap<>();

		for (Position position : positions) {
			final Integer partIndex = position.getPartIndex();

			if (!partitionedPositions.containsKey(partIndex)) {
				partitionedPositions.put(partIndex, new HashSet<>());
			}

			partitionedPositions.get(partIndex).add(position);
		}

		return Collections.unmodifiableSortedMap(partitionedPositions);
	}

	/**
	 * Returns the number of notation elements the referred pattern contains.
	 *
	 * @return the number of notation elements the referred pattern contains
	 */
	public int getSize() {
		return (int) positionsPerPart.values().stream().flatMap(Set::stream).count();
	}

	/**
	 * Returns the indices of the parts referred to by this pattern position.
	 *
	 * @return the indices of the parts referred to by this pattern position
	 */
	public SortedSet<Integer> getPartIndices() {
		return new TreeSet<>(positionsPerPart.keySet());
	}

	/**
	 * Returns the positions in the part with the given index referred to by this pattern position.
	 *
	 * @param partIndex the index of the part for which the positions are returned
	 * @return the positions in the part with the given index referred to by this pattern position
	 */
	public Set<Position> getPositions(int partIndex) {
		return positionsPerPart.get(partIndex);
	}

	/**
	 * Returns the numbers of all measures referred to by this pattern position.
	 *
	 * @return the numbers of all measures referred to by this pattern position
	 */
	public SortedSet<Integer> getMeasureNumbers() {
		return positionsPerPart.values().stream().flatMap(positions -> positions.stream())
				.map(Position::getMeasureNumber).collect(
						Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the numbers of measures in the part with the given index referred to by this pattern position.
	 *
	 * @param partIndex the index of the part for which the referred measure numbers are returned
	 * @return the numbers of measures in the part with the given number referred to by this pattern position
	 */
	public SortedSet<Integer> getMeasureNumbers(int partIndex) {
		return positionsPerPart.get(partIndex).stream().map(Position::getMeasureNumber).collect(
				Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns the numbers of staves in the part with the given index referred to by this pattern position.
	 *
	 * @param partIndex the index of the part for which the referred measure numbers are returned
	 * @return the numbers of measures in the part with the given number referred to by this pattern position
	 */
	public SortedSet<Integer> getStaffNumbers(int partIndex) {
		return positionsPerPart.get(partIndex).stream().map(Position::getStaffNumber).collect(
				Collectors.toCollection(TreeSet::new));
	}

	/**
	 * Returns true if this pattern position contains a reference to the notation element located in the given position.
	 *
	 * @param position the position whose presence in this pattern position is checked
	 * @return true if this pattern position contains a reference to the notation element located in the given position
	 */
	public boolean contains(Position position) {
		final Integer partIndex = position.getPartIndex();

		if (positionsPerPart.containsKey(partIndex)) {
			return positionsPerPart.get(partIndex).contains(position);
		}

		return false;
	}
}
