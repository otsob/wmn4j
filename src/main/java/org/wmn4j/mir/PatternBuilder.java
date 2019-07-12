/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for building {@link Pattern} instances.
 */
public final class PatternBuilder {

	private static final int DEFAULT_VOICE_NUMBER = 0;
	private final Map<Integer, List<Durational>> voices;
	private boolean isMonophonic = true;

	/**
	 * Constructor that creates an empty builder.
	 */
	public PatternBuilder() {
		voices = new HashMap<>();
	}

	/**
	 * Adds the given durational to the default voice in this builder. This method is intended for constructing
	 * monophonic patterns, the {@link PatternBuilder#addToVoice addToVoice} method can be used for constructing
	 * polyphonic patterns.
	 *
	 * @param durational the durational that is added to the default voice in this builder
	 */
	public void add(Durational durational) {
		addToVoice(durational, DEFAULT_VOICE_NUMBER);
	}

	/**
	 * Adds the given durational to the voice with the given number. This method is intended for constructing polyphonic
	 * patterns, the {@link PatternBuilder#add add} method can be used for constructing
	 * monophonic patterns.
	 *
	 * @param durational the durational that is added to the given voice in this builder
	 * @param voice      the number of the voice to which the durational is added
	 */
	public void addToVoice(Durational durational, int voice) {
		if (!voices.containsKey(voice)) {
			voices.put(voice, new ArrayList<>());
		}

		if (durational instanceof Chord || voices.keySet().size() > 1) {
			isMonophonic = false;
		}

		voices.get(voice).add(durational);
	}

	/**
	 * Returns true if this builder only has monophonic contents, that is, there are no simultaneously sounding notes
	 * set in the contents of this builder.
	 *
	 * @return true if this builder only has monophonic contents
	 */
	public boolean isMonophonic() {
		return isMonophonic;
	}

	/**
	 * Returns a pattern instance with the contents set into this builder.
	 *
	 * @return a pattern instance with the contents set into this builder
	 */
	public Pattern build() {
		return Pattern.of(voices.get(DEFAULT_VOICE_NUMBER));
	}
}
