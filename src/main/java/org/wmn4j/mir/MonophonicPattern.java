/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Pitched;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
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
	public int getVoiceCount() {
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

		return contents;
	}

	@Override
	public Durational get(int voiceNumber, int index) {
		if (voiceNumber != SINGLE_VOICE_NUMBER) {
			throw new NoSuchElementException("No voice with number " + voiceNumber + " in pattern");
		}
		return contents.get(index);

	}

	@Override
	public int getVoiceSize(int voiceNumber) {
		return contents.size();

	}

	@Override
	public boolean hasLabel(String label) {
		return labels.contains(label);
	}

	@Override
	public boolean equalsInPitch(Pattern other) {
		if (other.isMonophonic()) {
			List<Pitch> pitchesOfOther = toPitchList(other);
			List<Pitch> pitchesOfThis = toPitchList(contents);

			return pitchesOfThis.equals(pitchesOfOther);
		}

		return false;
	}

	private static List<Pitch> toPitchList(Iterable<Durational> contents) {

		List<Pitch> pitches = new ArrayList<>();
		for (Durational dur : contents) {
			if (dur instanceof Pitched) {
				pitches.add(((Pitched) dur).getPitch());
			}
		}

		return pitches;
	}

	@Override
	public boolean equalsEnharmonically(Pattern other) {
		if (other.isMonophonic()) {
			List<Integer> pitchNumbersOfOther = toPitchList(other).stream()
					.map(pitch -> pitch.toInt()).collect(
							Collectors.toList());

			List<Integer> pitchNumbersOfThis = toPitchList(contents).stream().map(pitch -> pitch.toInt()).collect(
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

		return createIntervalNumberList(contents).equals(createIntervalNumberList(other));
	}

	/*
	 * Returns a list of the intervals between consecutive pitches in the contents list. The intervals
	 * are expressed as integers denoting how many half-steps the interval consists of.
	 */
	private static List<Integer> createIntervalNumberList(Iterable<Durational> contents) {
		List<Pitched> pitchedElements = new ArrayList<>();

		for (Durational dur : contents) {
			if (dur instanceof Pitched) {
				pitchedElements.add((Pitched) dur);
			}
		}

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
			return areVoicesEqualInDurations(this, other);
		}

		return false;
	}

	@Override
	public int size() {
		return contents.size();
	}

	static boolean areVoicesEqualInDurations(Iterable<Durational> voiceA, Iterable<Durational> voiceB) {
		Iterator<Durational> iterA = voiceA.iterator();
		Iterator<Durational> iterB = voiceB.iterator();

		while (iterA.hasNext() && iterB.hasNext()) {

			final Durational durationalInThis = iterA.next();
			final Durational durationalInOther = iterB.next();

			final boolean bothAreOfSameType =
					(durationalInThis.isRest() && durationalInOther.isRest()) || (!durationalInThis.isRest()
							&& !durationalInOther.isRest());

			if (!bothAreOfSameType) {
				return false;
			}

			if (!durationalInThis.getDuration().equals(durationalInOther.getDuration())) {
				return false;
			}
		}

		// Check that both iterators are finished, otherwise the number of elements is not equal.
		return iterA.hasNext() == iterB.hasNext();
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

		return PolyphonicPattern.iterablesEquals(this, other);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(contents);
	}

	@Override
	public Iterator<Durational> iterator() {
		return contents.iterator();
	}
}
