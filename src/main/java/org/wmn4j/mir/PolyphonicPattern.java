/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Class that represents a polyphonic pattern. In a polyphonic pattern there can be
 * multiple voices and chords.
 */
final class PolyphonicPattern implements Pattern {

	private static final Integer DEFAULT_VOICE_NUMBER = 1;

	private final Map<Integer, List<Durational>> voices;

	PolyphonicPattern(Map<Integer, List<? extends Durational>> voices) {
		Map<Integer, List<Durational>> voicesCopy = new HashMap<>();

		for (Integer voiceNumber : voices.keySet()) {
			List<Durational> voiceContents = new ArrayList<>();
			voiceContents.addAll(voices.get(voiceNumber));
			if (voiceContents.isEmpty()) {
				throw new IllegalArgumentException("Cannot create pattern with empty voice");
			}
			voicesCopy.put(voiceNumber, Collections.unmodifiableList(voiceContents));
		}

		this.voices = Collections.unmodifiableMap(voicesCopy);
		if (this.voices.isEmpty()) {
			throw new IllegalArgumentException("Cannot create polyphonic pattern from empty voices");
		}
		if (this.voices.keySet().size() == 1) {
			final Integer voiceNumber = this.voices.keySet().iterator().next();
			final boolean isMonophonic = this.voices.get(voiceNumber).stream()
					.noneMatch(durational -> durational instanceof Chord);
			if (isMonophonic) {
				throw new IllegalArgumentException("Trying to create a polyphonic pattern with monophonic contents");
			}
		}
	}

	PolyphonicPattern(List<? extends Durational> voice) {
		List<Durational> voiceCopy = Collections.unmodifiableList(new ArrayList<>(voice));
		if (voiceCopy.isEmpty()) {
			throw new IllegalArgumentException("Cannot create pattern from empty voice");
		}

		Map<Integer, List<Durational>> voicesCopy = new HashMap<>();
		voicesCopy.put(DEFAULT_VOICE_NUMBER, voiceCopy);
		voices = Collections.unmodifiableMap(voicesCopy);
	}

	@Override
	public List<Durational> getContents() {
		return voices.values().stream().flatMap(voice -> voice.stream()).collect(Collectors.toList());
	}

	@Override
	public boolean isMonophonic() {
		return false;
	}

	@Override
	public int getNumberOfVoices() {
		return voices.size();
	}

	@Override
	public List<Integer> getVoiceNumbers() {
		List<Integer> voiceNumbers = new ArrayList<>(voices.keySet());
		voiceNumbers.sort(Integer::compareTo);
		return voiceNumbers;
	}

	@Override
	public List<Durational> getVoice(int voiceNumber) {
		return voices.get(voiceNumber);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Pattern)) {
			return false;
		}

		return containsEqualVoices((Pattern) o, List::equals);
	}

	private boolean containsEqualVoices(Pattern other,
			BiFunction<List<Durational>, List<Durational>, Boolean> voiceEquality) {
		if (other.getNumberOfVoices() != getNumberOfVoices()) {
			return false;
		}

		for (List<Durational> voice : voices.values()) {
			boolean isVoicePresentInOther = false;

			for (Integer voiceNumber : other.getVoiceNumbers()) {
				List<Durational> voiceInOther = other.getVoice(voiceNumber);
				if (voiceEquality.apply(voice, voiceInOther)) {
					isVoicePresentInOther = true;
					break;
				}
			}

			if (!isVoicePresentInOther) {
				return false;
			}
		}

		for (Integer voiceNumber : other.getVoiceNumbers()) {
			List<Durational> voiceInOther = other.getVoice(voiceNumber);

			if (voices.values().stream().noneMatch(voice -> voiceEquality.apply(voice, voiceInOther))) {
				return false;
			}
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsInPitch(wmnlibmir.Pattern)
	 */
	@Override

	public boolean equalsInPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsEnharmonically(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsEnharmonically(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsTranspositionally(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsTranspositionally(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsInDurations(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInDurations(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(voices);
	}
}
