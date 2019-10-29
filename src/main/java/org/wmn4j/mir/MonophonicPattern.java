/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Pitched;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A class for representing monophonic musical patterns. In a monophonic pattern
 * no notes occur simultaneously. The pattern cannot contain chords and does not
 * consist of multiple voices. This class is immutable.
 */
final class MonophonicPattern implements Pattern {

	static final int SINGLE_VOICE_NUMBER = 1;
	private static final List<Integer> SINGLE_VOICE_NUMBER_LIST = Collections.singletonList(SINGLE_VOICE_NUMBER);

	private final List<Durational> contents;
	private final String name;
	private final SortedSet<String> labels;

	MonophonicPattern(List<? extends Durational> contents, String name, Set<String> labels) {
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
		this.name = name;
		this.labels = Collections.unmodifiableSortedSet(new TreeSet<>(labels));
	}

	MonophonicPattern(List<? extends Durational> contents, String name) {
		this(contents, name, Collections.emptySet());
	}

	MonophonicPattern(List<? extends Durational> contents) {
		this(contents, null, Collections.emptySet());
	}

	@Override
	public List<Durational> getContents() {
		return this.contents;
	}

	@Override
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	@Override
	public SortedSet<String> getLabels() {
		return labels;
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
	public int getNumberOfVoices() {
		return 1;
	}

	@Override
	public List<Integer> getVoiceNumbers() {
		return SINGLE_VOICE_NUMBER_LIST;
	}

	@Override
	public List<Durational> getVoice(int voiceNumber) {
		if (voiceNumber != SINGLE_VOICE_NUMBER) {
			throw new NoSuchElementException("No voice in pattern with number " + voiceNumber);
		}

		return getContents();
	}

	@Override
	public boolean hasLabel(String label) {
		return labels.contains(label);
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
	public boolean equalsTranspositionally(Pattern other) {
		if (!other.isMonophonic()) {
			return false;
		}

		return createIntervalNumberList(getContents()).equals(createIntervalNumberList(other.getContents()));
	}

	/*
	 * Returns a list of the intervals between consecutive pitches in the contents list. The intervals
	 * are expressed as integers denoting how many half-steps the interval consists of.
	 */
	private static List<Integer> createIntervalNumberList(List<Durational> contents) {
		List<Pitched> pitchedElements = contents.stream().filter(durational -> durational instanceof Pitched)
				.map(durational -> (Pitched) durational).collect(Collectors.toList());

		// If size is at most 1, then there are no intervals between adjacent notes of the pattern.
		if (pitchedElements.size() <= 1) {
			return Collections.emptyList();
		}

		List<Integer> intervalNumbers = new ArrayList<>();
		int previous = pitchedElements.get(0).getPitch().toInt();

		for (int i = 1; i < pitchedElements.size(); ++i) {
			final int current = pitchedElements.get(i).getPitch().toInt();
			intervalNumbers.add(current - previous);
			previous = current;
		}

		return intervalNumbers;
	}

	@Override
	public boolean equalsInDurations(Pattern other) {
		if (other.isMonophonic()) {
			final List<Durational> contentsOfThis = getContents();
			final List<Durational> contentsOfOther = other.getContents();
			return areVoicesEqualInDurations(contentsOfThis, contentsOfOther);
		}

		return false;
	}

	static boolean areVoicesEqualInDurations(List<Durational> voiceA, List<Durational> voiceB) {
		if (voiceA.size() == voiceB.size()) {
			for (int i = 0; i < voiceA.size(); ++i) {

				final Durational durationalInThis = voiceA.get(i);
				final Durational durationalInOther = voiceB.get(i);

				final boolean bothAreOfSameType =
						(durationalInThis.isRest() && durationalInOther.isRest()) || (!durationalInThis.isRest()
								&& !durationalInOther.isRest());

				if (!bothAreOfSameType) {
					return false;
				}

				if (!voiceA.get(i).getDuration().equals(voiceB.get(i).getDuration())) {
					return false;
				}
			}

			return true;
		}

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
