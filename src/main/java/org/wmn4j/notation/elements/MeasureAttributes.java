/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
	private final Map<Duration, Clef> clefChanges;

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
			Barline rightBarline,
			Clef clef) {
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
			Barline rightBarline,
			Barline leftBarline, Clef clef) {
		return of(timeSignature, keySignature, rightBarline, leftBarline, clef, null);
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
	 * @return an instance with the given values
	 */
	public static MeasureAttributes of(TimeSignature timeSignature, KeySignature keySignature,
			Barline rightBarline,
			Barline leftBarline, Clef clef, Map<Duration, Clef> clefChanges) {
		// TODO: Potentially use interner pattern or similar for caching.
		return new MeasureAttributes(timeSignature, keySignature, rightBarline, leftBarline, clef, clefChanges);
	}

	private MeasureAttributes(TimeSignature timeSig, KeySignature keySig, Barline rightBarline, Barline leftBarline,
			Clef clef, Map<Duration, Clef> clefChanges) {
		this.timeSig = Objects.requireNonNull(timeSig);
		this.keySig = Objects.requireNonNull(keySig);
		this.rightBarline = Objects.requireNonNull(rightBarline);
		this.leftBarline = Objects.requireNonNull(leftBarline);
		this.clef = Objects.requireNonNull(clef);

		if (clefChanges != null && !clefChanges.isEmpty()) {
			this.clefChanges = new HashMap<>(clefChanges);
		} else {
			this.clefChanges = Collections.<Duration, Clef>emptyMap();
		}
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
	 * Returns the clef chagnes specified in the attributes. The keys in the map are
	 * the offsets of the clef changes from the beginning of the measure.
	 *
	 * @return the clef chagnes specified in the attributes
	 */
	public Map<Duration, Clef> getClefChanges() {
		return Collections.unmodifiableMap(this.clefChanges);
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

		return true;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append(this.timeSig).append(", ").append(this.keySig).append(", ").append("left barline: ")
				.append(this.leftBarline).append(", ").append("right barline: ").append(this.rightBarline).append(", ")
				.append("Clef: ").append(this.clef).append(", ");

		strBuilder.append("Clef changes: ");
		if (this.containsClefChanges()) {
			for (Duration d : this.clefChanges.keySet()) {
				strBuilder.append(this.clefChanges.get(d)).append(" at ").append(d);
			}
		} else {
			strBuilder.append("None");
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
		return hash;
	}
}
