/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.Offset;
import org.wmn4j.notation.directions.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Represents the attributes of a measure that are often implicit in the
 * measure, such as clef in effect, time signature, key signature and so on.
 */
public final class MeasureAttributes {

	private final TimeSignature timeSig;
	private final KeySignature keySig;
	private final Barline rightBarline;
	private final Barline leftBarline;
	private final Clef clef;
	private final List<Offset<Clef>> clefChanges;
	private final List<Offset<Direction>> directions;

	/**
	 * Returns an instance with the given values. The left barline type will be set
	 * to none.
	 *
	 * @param timeSignature the time signature
	 * @param keySignature  the key signature
	 * @param rightBarline  the type of the right barline
	 * @param clef          the clef in effect at the beginning of the measure
	 * @return an instance with the given values
	 */
	public static MeasureAttributes of(TimeSignature timeSignature, KeySignature keySignature,
			Barline rightBarline, Clef clef) {
		return of(timeSignature, keySignature, rightBarline, Barline.NONE, clef);
	}

	/**
	 * Returns an instance with the given values.
	 *
	 * @param timeSignature the time signature
	 * @param keySignature  the key signature
	 * @param rightBarline  the type of the right barline
	 * @param leftBarline   the type of the left barline
	 * @param clef          the clef in effect at the beginning of the measure
	 * @return an instance with the given values
	 */
	public static MeasureAttributes of(TimeSignature timeSignature, KeySignature keySignature,
			Barline rightBarline, Barline leftBarline, Clef clef) {
		return of(timeSignature, keySignature, rightBarline, leftBarline, clef, null, null);
	}

	/**
	 * Returns an instance with the given values.
	 *
	 * @param timeSignature the time signature
	 * @param keySignature  the key signature
	 * @param rightBarline  the type of the right barline
	 * @param leftBarline   the type of the left barline
	 * @param clef          the clef in effect at the beginning of the measure
	 * @param clefChanges   the clef changes within the measure
	 * @param directions    the direction markings within the measure
	 * @return an instance with the given values
	 */
	public static MeasureAttributes of(TimeSignature timeSignature, KeySignature keySignature,
			Barline rightBarline, Barline leftBarline, Clef clef, Collection<Offset<Clef>> clefChanges,
			Collection<Offset<Direction>> directions) {
		// TODO: Potentially use interner pattern or similar for caching.
		return new MeasureAttributes(timeSignature, keySignature, rightBarline, leftBarline, clef, clefChanges,
				directions);
	}

	private MeasureAttributes(TimeSignature timeSig, KeySignature keySig, Barline rightBarline, Barline leftBarline,
			Clef clef, Collection<Offset<Clef>> clefChanges, Collection<Offset<Direction>> directions) {

		this.timeSig = Objects.requireNonNull(timeSig);
		this.keySig = Objects.requireNonNull(keySig);
		this.rightBarline = Objects.requireNonNull(rightBarline);
		this.leftBarline = Objects.requireNonNull(leftBarline);
		this.clef = Objects.requireNonNull(clef);

		this.clefChanges = createSortedCopy(clefChanges);
		this.directions = createSortedCopy(directions);
	}

	private static <T> List<Offset<T>> createSortedCopy(Collection<Offset<T>> offsetElements) {
		if (offsetElements != null && !offsetElements.isEmpty()) {
			List<Offset<T>> sortedElements = new ArrayList<>(offsetElements);
			sortedElements.sort(Offset::compareTo);
			return Collections.unmodifiableList(sortedElements);
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the time signature specified in these attributes.
	 *
	 * @return the time signature specified in these attributes
	 */
	public TimeSignature getTimeSignature() {
		return this.timeSig;
	}

	/**
	 * Returns the key signature specified in these attributes.
	 *
	 * @return the key signature specified in these attributes
	 */
	public KeySignature getKeySignature() {
		return this.keySig;
	}

	/**
	 * Returns the type of the right barline specified in these attributes.
	 *
	 * @return the type of the right barline specified in these attributes
	 */
	public Barline getRightBarline() {
		return this.rightBarline;
	}

	/**
	 * Returns the type of the left barline specified in these attributes.
	 *
	 * @return the type of the left barline specified in these attributes
	 */
	public Barline getLeftBarline() {
		return this.leftBarline;
	}

	/**
	 * Returns the clef in effect at the beginning of the measure.
	 *
	 * @return the clef in effect at the beginning of the measure
	 */
	public Clef getClef() {
		return this.clef;
	}

	/**
	 * Returns true if there are clef changes specified in the attributes.
	 *
	 * @return true if there are clef changes specified in the attributes
	 */
	public boolean containsClefChanges() {
		return !this.clefChanges.isEmpty();
	}

	/**
	 * Returns the clef changes specified in the attributes.
	 * <p>
	 * The placement of clef changes are represented using {@link Offset} types,
	 * where the placement of the clef change is measured by an offset from the
	 * beginning of th measure. The list is sorted in ascending order of offset.
	 *
	 * @return the clef changes specified in the attributes
	 */
	public List<Offset<Clef>> getClefChanges() {
		return clefChanges;
	}

	/**
	 * Returns true if this measure attributes instance contains directions.
	 *
	 * @return true if this measure attributes instance contains directions
	 */
	public boolean containsDirections() {
		return !this.directions.isEmpty();
	}

	/**
	 * Returns an unmodifiable view of the directions in these attributes.
	 * <p>
	 * The directions are sorted in ascending order of offset duration.
	 * The offsets are measured from the beginning of the measure.
	 *
	 * @return an unmodifiable view of the directions in these attributes
	 */
	public List<Offset<Direction>> getDirections() {
		return this.directions;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MeasureAttributes)) {
			return false;
		}

		final MeasureAttributes other = (MeasureAttributes) o;

		if (!this.timeSig.equals(other.timeSig)) {
			return false;
		}

		if (!this.keySig.equals(other.keySig)) {
			return false;
		}

		if (!this.rightBarline.equals(other.rightBarline)) {
			return false;
		}

		if (!this.leftBarline.equals(other.leftBarline)) {
			return false;
		}

		if (!this.clef.equals(other.clef)) {
			return false;
		}

		if (!this.clefChanges.equals(other.clefChanges)) {
			return false;
		}

		return this.directions.equals(other.directions);
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(this.timeSig).append(", ").append(this.keySig).append(", ").append("left barline: ")
				.append(this.leftBarline).append(", ").append("right barline: ").append(this.rightBarline).append(", ")
				.append("Clef: ").append(this.clef).append(", ");

		final String separator = " - ";

		if (this.containsClefChanges()) {
			strBuilder.append("\nClef changes: ");
			for (Offset<Clef> clefChange : this.clefChanges) {
				strBuilder.append(clefChange).append(separator);
			}
		}

		if (this.containsDirections()) {
			strBuilder.append("\nDirections: ");
			for (Offset<Direction> direction : this.directions) {
				strBuilder.append(direction).append(separator);
			}
		}

		return strBuilder.append(".").toString();
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 83 * hash + Objects.hashCode(this.timeSig);
		hash = 83 * hash + Objects.hashCode(this.keySig);
		hash = 83 * hash + Objects.hashCode(this.rightBarline);
		hash = 83 * hash + Objects.hashCode(this.leftBarline);
		hash = 83 * hash + Objects.hashCode(this.clef);
		hash = 83 * hash + Objects.hashCode(this.clefChanges);
		hash = 83 * hash + Objects.hashCode(this.directions);
		return hash;
	}
}
