/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.Durational;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for building {@link Pattern} instances.
 */
public final class PatternBuilder {

	private static final int DEFAULT_VOICE_NUMBER = MonophonicPattern.SINGLE_VOICE_NUMBER;
	private final Map<Integer, List<DurationalBuilder>> voices;
	private boolean isMonophonic = true;
	private String name = null;
	private Set<String> labels = new HashSet<>();

	/**
	 * Constructor that creates an empty builder.
	 */
	public PatternBuilder() {
		voices = new HashMap<>();
	}

	/**
	 * Adds the given durational builder to the default voice in this builder. This method is intended for constructing
	 * patterns with a single voice, the {@link PatternBuilder#addToVoice addToVoice} method can be used for
	 * constructing polyphonic patterns with multiple voices.
	 *
	 * @param builder the durational builder that is added to the default voice in this builder
	 */
	public void add(DurationalBuilder builder) {
		addToVoice(builder, DEFAULT_VOICE_NUMBER);
	}

	/**
	 * Adds the given durational builder to the voice with the given number. This method is intended for constructing
	 * polyphonic patterns, the {@link PatternBuilder#add add} method can be used for constructing
	 * monophonic patterns.
	 *
	 * @param durationalBuilder the durational builder that is added to the given voice in this builder
	 * @param voice             the number of the voice to which the durational builder is added
	 */
	public void addToVoice(DurationalBuilder durationalBuilder, int voice) {
		if (!voices.containsKey(voice)) {
			voices.put(voice, new ArrayList<>());
		}

		if (durationalBuilder instanceof ChordBuilder || voices.keySet().size() > 1) {
			isMonophonic = false;
		}

		voices.get(voice).add(durationalBuilder);
	}

	/**
	 * Returns true if this builder only has monophonic contents, that is, there is only single voice in the pattern
	 * and there are no simultaneously sounding notes set in the contents of this builder.
	 *
	 * @return true if this builder has monophonic contents
	 */
	public boolean isMonophonic() {
		return isMonophonic;
	}

	/**
	 * Sets the name of the pattern to be built.
	 *
	 * @param name the non-null name of the pattern to be built
	 */
	public void setName(String name) {
		this.name = Objects.requireNonNull(name);
	}

	/**
	 * Adds the given label to this builder.
	 *
	 * @param label the label to be added to this builder.
	 */
	public void addLabel(String label) {
		this.labels.add(label);
	}

	/**
	 * Returns a pattern instance with the contents set into this builder.
	 *
	 * @return a pattern instance with the contents set into this builder
	 */
	public Pattern build() {
		if (voices.isEmpty()) {
			throw new IllegalStateException("Cannot build a pattern without any contents");
		}

		if (voices.size() == 1) {
			final Integer voiceNumber = voices.keySet().iterator().next();
			return Pattern.of(buildVoice(voices.get(voiceNumber)), name, labels);
		}

		Map<Integer, List<? extends Durational>> builtVoices = new HashMap<>(voices.size());
		for (Integer voiceNumber : voices.keySet()) {
			builtVoices.put(voiceNumber, buildVoice(voices.get(voiceNumber)));
		}

		return Pattern.of(builtVoices, name, labels);
	}

	private List<Durational> buildVoice(List<DurationalBuilder> builders) {
		return builders.stream().map(builder -> builder.build()).collect(Collectors.toList());
	}
}
