/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jnotation.noteobjects;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Class containing the attributes of measures that typically remain unchanged
 * from one measure to the next.
 * 
 * @author Otso Björklund
 */
public class MeasureAttributes {

	private final TimeSignature timeSig;
	private final KeySignature keySig;
	private final Barline rightBarline;
	private final Barline leftBarline;
	private final Clef clef;
	private final Map<Duration, Clef> clefChanges;

	public static MeasureAttributes getMeasureAttr(TimeSignature timeSig, KeySignature keySig, Barline rightBarline,
			Clef clef) {
		return getMeasureAttr(timeSig, keySig, rightBarline, Barline.NONE, clef);
	}

	public static MeasureAttributes getMeasureAttr(TimeSignature timeSig, KeySignature keySig, Barline rightBarline,
			Barline leftBarline, Clef clef) {
		return getMeasureAttr(timeSig, keySig, rightBarline, leftBarline, clef, null);
	}

	public static MeasureAttributes getMeasureAttr(TimeSignature timeSig, KeySignature keySig, Barline rightBarline,
			Barline leftBarline, Clef clef, Map<Duration, Clef> clefChanges) {
		// TODO: Potentially use interner pattern or similar for caching.
		return new MeasureAttributes(timeSig, keySig, rightBarline, leftBarline, clef, clefChanges);
	}

	private MeasureAttributes(TimeSignature timeSig, KeySignature keySig, Barline rightBarline, Barline leftBarline,
			Clef clef, Map<Duration, Clef> clefChanges) {
		this.timeSig = Objects.requireNonNull(timeSig);
		this.keySig = Objects.requireNonNull(keySig);
		this.rightBarline = Objects.requireNonNull(rightBarline);
		this.leftBarline = Objects.requireNonNull(leftBarline);
		this.clef = Objects.requireNonNull(clef);

		if (clefChanges != null && !clefChanges.isEmpty())
			this.clefChanges = new HashMap<>(clefChanges);
		else
			this.clefChanges = Collections.<Duration, Clef>emptyMap();
	}

	public TimeSignature getTimeSignature() {
		return this.timeSig;
	}

	public KeySignature getKeySignature() {
		return this.keySig;
	}

	public Barline getRightBarline() {
		return this.rightBarline;
	}

	public Barline getLeftBarline() {
		return this.leftBarline;
	}

	public Clef getClef() {
		return this.clef;
	}

	public boolean containsClefChanges() {
		return !this.clefChanges.isEmpty();
	}

	public Map<Duration, Clef> getClefChanges() {
		return Collections.unmodifiableMap(this.clefChanges);
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MeasureAttributes))
			return false;

		MeasureAttributes other = (MeasureAttributes) o;

		if (!this.timeSig.equals(other.timeSig))
			return false;

		if (!this.keySig.equals(other.keySig))
			return false;

		if (!this.rightBarline.equals(other.rightBarline))
			return false;

		if (!this.leftBarline.equals(other.leftBarline))
			return false;

		if (!this.clef.equals(other.clef))
			return false;

		return true;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
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
