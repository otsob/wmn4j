/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Generic implementation class for chord consisting of Pitched objects.
 * This class is immutable.
 *
 * @param <T> the type of pitched object this chord contains
 */
final class GenericChord<T extends OptionallyPitched> implements Iterable<T> {

	private final List<T> pitchedElements;

	GenericChord(Collection<T> pitchedElements) {
		final List<T> elementsCopy = new ArrayList<T>(Objects.requireNonNull(pitchedElements));

		if (elementsCopy.isEmpty()) {
			throw new IllegalArgumentException("Chord cannot be constructed with an empty List of elements");
		}

		elementsCopy.sort(OptionallyPitched::compareByPitch);
		this.pitchedElements = Collections.unmodifiableList(elementsCopy);
	}

	/**
	 * Returns the {@link OptionallyPitched} type at the given index counting from lowest pitch in
	 * this {@link GenericChord}.
	 *
	 * @param fromLowest index of pitched, 0 being the lowest pitched in the chord
	 * @return the pitched from index fromLowest
	 * @throws IllegalArgumentException if fromLowest is smaller than 0 or at least
	 *                                  the number of pitched elements in this Chord
	 */
	T getNote(int fromLowest) {
		if (fromLowest < 0 || fromLowest >= this.pitchedElements.size()) {
			throw new IllegalArgumentException(
					"Tried to get pitched with invalid index: " + fromLowest + "from chord: " + this);
		}

		return this.pitchedElements.get(fromLowest);
	}

	/**
	 * Returns the lowest pitched in this chord.
	 *
	 * @return the pitched with the lowest pitch in this Chord.
	 */
	T getLowestNote() {
		return this.getNote(0);
	}

	/**
	 * Returns the highest pitched in this chord.
	 *
	 * @return the pitched with the highest pitch in this Chord.
	 */
	T getHighestNote() {
		return this.getNote(this.pitchedElements.size() - 1);
	}

	/**
	 * Returns the number of pitched elements in this chord.
	 *
	 * @return number of pitched elements in this chord.
	 */
	int getNoteCount() {
		return this.pitchedElements.size();
	}

	/**
	 * Returns a chord with the pitched elements of this chord and the given pitched.
	 *
	 * @param pitched the pitched that is added
	 * @return a chord with the pitched elements of this and the added pitched
	 */
	GenericChord<T> add(T pitched) {
		final ArrayList<T> pitchedList = new ArrayList<T>(this.pitchedElements);
		pitchedList.add(pitched);
		return new GenericChord<>(pitchedList);
	}

	/**
	 * Returns a Chord with the given pitched removed.
	 *
	 * @param pitched pitched to be removed
	 * @return a chord without the given pitched
	 */
	GenericChord<T> remove(T pitched) {
		final ArrayList<T> pitchedList = new ArrayList<T>(this.pitchedElements);
		pitchedList.remove(pitched);
		return new GenericChord<>(pitchedList);
	}

	/**
	 * Returns true if this chord contains the given pitch.
	 *
	 * @param pitch pitch whose presence in this chord is checked
	 * @return true if this contains the given pitch, false otherwise
	 */
	boolean contains(Pitch pitch) {
		for (T pitched : this.pitchedElements) {
			if (pitched.hasPitch() && pitched.getPitch().get().equals(pitch)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns true if this contains the given pitched.
	 *
	 * @param pitched the pitched whose presence in this chord is checked
	 * @return true if this contains the given pitched, false otherwise
	 */
	boolean contains(T pitched) {
		return this.pitchedElements.contains(pitched);
	}

	/**
	 * Returns a chord with the given pitch removed.
	 *
	 * @param pitch pitch of the pitched to be removed
	 * @return a chord without a pitched with the given pitch
	 */
	GenericChord<T> remove(Pitch pitch) {
		if (this.contains(pitch)) {
			final List<T> newNotes = new ArrayList<T>(this.pitchedElements);
			newNotes.removeIf(pitched -> Objects.equals(pitched.getPitch().orElse(null), (pitch)));
			return new GenericChord<>(newNotes);
		}

		return this;
	}

	/**
	 * Returns true if the given Object is equal to this.
	 *
	 * @param o Object against which this is compared for equality.
	 * @return true if o is an instance of Chord and contains all and no other pitched elements
	 * than the ones in this Chord.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof GenericChord)) {
			return false;
		}

		final GenericChord<?> other = (GenericChord<?>) o;
		return this.pitchedElements.equals(other.pitchedElements);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 61 * hash + Objects.hashCode(this.pitchedElements);
		return hash;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("[");

		for (int i = 0; i < this.pitchedElements.size(); ++i) {
			strBuilder.append(this.pitchedElements.get(i).toString());

			if (i != this.pitchedElements.size() - 1) {
				strBuilder.append(",");
			}
		}
		strBuilder.append("]");
		return strBuilder.toString();
	}

	@Override
	public Iterator<T> iterator() {
		return this.pitchedElements.iterator();
	}

	Stream<T> stream() {
		return pitchedElements.stream();
	}
}
