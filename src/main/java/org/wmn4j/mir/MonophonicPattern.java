/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A class for representing monophonic musical patterns. In a monophonic pattern
 * no notes occur simultaneously. The pattern cannot contain chords and does not
 * consist of multiple voices. This class is immutable.
 */
final class MonophonicPattern implements Pattern {

	private final List<Durational> contents;

	/**
	 * Constructor.
	 *
	 * @param contents non-empty list containing the notation elements of the pattern in temporal order
	 */
	MonophonicPattern(List<Durational> contents) {
		this.contents = Collections.unmodifiableList(new ArrayList<>(contents));
		if (this.contents == null) {
			throw new NullPointerException("Cannot create pattern with null contents");
		}
		if (this.contents.isEmpty()) {
			throw new IllegalArgumentException("Cannot create pattern with empty contents");
		}
		if (this.contents.stream().anyMatch((dur) -> (dur instanceof Chord))) {
			throw new IllegalArgumentException("Contents contain a Chord. Contents must be monophonic");
		}
	}

	@Override
	public List<Durational> getContents() {
		return this.contents;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		for (Durational dur : this.contents) {
			strBuilder.append(dur.toString());
		}

		return strBuilder.toString();
	}

	@Override
	public boolean isMonophonic() {
		return true;
	}

	@Override
	public boolean equalsInPitch(Pattern other) {
		if (other.isMonophonic()) {
			List<Pitch> pitchesOfOther = toPitchList(other.getContents());
			List<Pitch> pitchesOfThis = toPitchList(getContents());

			return pitchesOfThis.equals(pitchesOfOther);
		}

		return false;
	}

	private static List<Pitch> toPitchList(List<Durational> contents) {
		return contents.stream()
				.filter(durational -> durational instanceof Note)
				.map(durational -> ((Note) durational).getPitch())
				.collect(Collectors.toList());
	}

	@Override
	public boolean equalsEnharmonically(Pattern other) {
		if (other.isMonophonic()) {
			List<Integer> pitchNumbersOfOther = toPitchList(other.getContents()).stream()
					.map(pitch -> pitch.toInt()).collect(
							Collectors.toList());

			List<Integer> pitchNumbersOfThis = toPitchList(getContents()).stream().map(pitch -> pitch.toInt()).collect(
					Collectors.toList());

			return pitchNumbersOfOther.equals(pitchNumbersOfThis);
		}

		return false;
	}

	@Override
	public boolean equalsInTransposedPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equalsInRhythm(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equalsInOnsets(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Pattern)) {
			return false;
		}

		Pattern other = (Pattern) o;

		if (!other.isMonophonic()) {
			return false;
		}

		return getContents().equals(other.getContents());
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(contents);
	}
}
