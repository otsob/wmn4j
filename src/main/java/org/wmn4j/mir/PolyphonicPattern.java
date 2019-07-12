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

/**
 * Class that represents a polyphonic pattern. In a polyphonic pattern there can be
 * multiple voices and chords.
 */
final class PolyphonicPattern implements Pattern {

	private final Integer DEFAULT_VOICE_NUMBER = 1;

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
		return null;
	}

	@Override
	public boolean isMonophonic() {
		return false;
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
}
