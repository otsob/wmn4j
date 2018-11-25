/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.KeySignature;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MeasureAttributes;
import org.wmn4j.notation.elements.TimeSignature;
import org.wmn4j.notation.elements.TimeSignatures;

/**
 * Class for building <code>Measure</code> objects. The builder does not ensure
 * that the <code>DurationalBuilder</code> objects in the builder fill up
 * exactly a measure that has the set time signature. The methods
 * {@link #isFull() isFull} and {@link #isVoiceFull(int) isVoiceFull} should be
 * used for checking if the durations add up to the correct
 * 
 * Default values: TimeSignature : 4/4 KeySignature : C-major/a-minor Clef: G
 * Barlines (right and left): Single. No clef changes.
 * 
 * @author Otso Björklund
 */
public class MeasureBuilder {

	private int number;
	// TODO: Keep track of voice durations in some way to make checking if measure
	// is full faster.
	private final Map<Integer, List<DurationalBuilder>> voices;

	private TimeSignature timeSig = TimeSignatures.FOUR_FOUR;
	private KeySignature keySig = KeySignatures.CMAJ_AMIN;
	private Clef clef = Clefs.G;
	private Barline leftBarline = Barline.NONE;
	private Barline rightBarline = Barline.SINGLE;
	private Map<Duration, Clef> clefChanges = new HashMap<>();

	// TODO: If MeasureAttributes are given, then use those.

	/**
	 * Create a <code>MeasureBuilder</code> with the given
	 * <code>MeasureAttributes</code>.
	 * 
	 * @param number
	 *            Measure number for measure being built.
	 * @param measureAttr
	 *            MeasureAttributes for measure.
	 */
	public MeasureBuilder(int number, MeasureAttributes measureAttr) {
		this.voices = new HashMap<>();
		this.number = number;

		this.timeSig = measureAttr.getTimeSignature();
		this.keySig = measureAttr.getKeySignature();
		this.clef = measureAttr.getClef();
		this.leftBarline = measureAttr.getLeftBarline();
		this.rightBarline = measureAttr.getRightBarline();
		this.clefChanges = new HashMap<>(measureAttr.getClefChanges());
	}

	/**
	 * @param number
	 *            Measure number for measure being built.
	 */
	public MeasureBuilder(int number) {
		this.voices = new HashMap<>();
		this.number = number;
	}

	/**
	 * @return Measure number.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * @param number
	 *            measure number for the measure that can be built.
	 * @return reference to this builder.
	 */
	public MeasureBuilder setNumber(int number) {
		this.number = number;
		return this;
	}

	/**
	 * @return time signature currently set for this builder.
	 */
	public TimeSignature getTimeSig() {
		return this.timeSig;
	}

	/**
	 * @param timeSig
	 *            time signature for the measure that can be built.
	 * @return reference to this builder.
	 */
	public MeasureBuilder setTimeSig(TimeSignature timeSig) {
		this.timeSig = timeSig;
		return this;
	}

	/**
	 * @return key signature that is currently set for this builder.
	 */
	public KeySignature getKeySig() {
		return this.keySig;
	}

	/**
	 * @param keySig
	 *            key signature for the measure that can be built.
	 * @return reference to this builder.
	 */
	public MeasureBuilder setKeySig(KeySignature keySig) {
		this.keySig = keySig;
		return this;
	}

	/**
	 * @return clef currently set for this builder.
	 */
	public Clef getClef() {
		return this.clef;
	}

	/**
	 * @param clef
	 *            clef for the measure that can be built.
	 * @return reference to this builder.
	 */
	public MeasureBuilder setClef(Clef clef) {
		this.clef = clef;
		return this;
	}

	/**
	 * @return left barline currently set for this builder.
	 */
	public Barline getLeftBarline() {
		return this.leftBarline;
	}

	/**
	 * @param leftBarline
	 *            left barline for the measure that can be built.
	 * @return reference to this builder.
	 */
	public MeasureBuilder setLeftBarline(Barline leftBarline) {
		this.leftBarline = leftBarline;
		return this;
	}

	/**
	 * @return right barline currently set for this builder.
	 */
	public Barline getRightBarline() {
		return rightBarline;
	}

	/**
	 * @param rightBarline
	 *            right barline for the measure that can be built.
	 * @return reference to this builder.
	 */
	public MeasureBuilder setRightBarline(Barline rightBarline) {
		this.rightBarline = rightBarline;
		return this;
	}

	/**
	 * @return clef changes currently set for this builder. Durations are offsets
	 *         from the beginning of the measure.
	 */
	public Map<Duration, Clef> getClefChanges() {
		return this.clefChanges;
	}

	/**
	 * Add clef change at offset.
	 * 
	 * @param offset
	 *            Offset of clef change from beginning of measure.
	 * @param clef
	 *            clef starting from offset.
	 * @return reference to this builder.
	 */
	public MeasureBuilder addClefChange(Duration offset, Clef clef) {
		this.clefChanges.put(offset, clef);
		return this;
	}

	/**
	 * Add new empty voice to this <code>MeasureBuilder</code>.
	 * 
	 * @return reference to this builder.
	 */
	public MeasureBuilder addVoice() {
		this.voices.put(this.voices.keySet().size(), new ArrayList<>());
		return this;
	}

	/**
	 * Add possibly non-empty voice to this <code>MeasureBuilder</code>.
	 * 
	 * @param voice
	 *            new voice to be added to this.
	 * @return reference to this builder.
	 */
	public MeasureBuilder addVoice(List<DurationalBuilder> voice) {
		this.voices.put(this.voices.keySet().size(), voice);
		return this;
	}

	/**
	 * Append <code>DurationalBuilder</code> object to voice with index
	 * <code>voice</code>. If voice does not exist it is created.
	 * 
	 * @param voice
	 *            index of voice to which builder is appended.
	 * @param builder
	 *            DurationalBuilder object to be appended to voice.
	 * @return reference to this builder.
	 */
	public MeasureBuilder addToVoice(int voice, DurationalBuilder builder) {

		if (!this.voices.keySet().contains(voice))
			this.voices.put(voice, new ArrayList<>());

		this.voices.get(voice).add(builder);
		return this;
	}

	/**
	 * @return number or voices in this builder.
	 */
	public int getNumberOfVoices() {
		return this.voices.size();
	}

	/**
	 * Get the voice numbers. Voice numbers do not have to be contiguous.
	 * 
	 * @return the set of voice numbers in this builder.
	 */
	public Set<Integer> getVoiceNumbers() {
		return Collections.unmodifiableSet(this.voices.keySet());
	}

	/**
	 * Set the element at specified location to given value.
	 * 
	 * @param voice
	 *            the number of the voice to be modified.
	 * @param index
	 *            the index in the voice.
	 * @param builder
	 *            element to be placed in index on voice.
	 */
	public void setElement(int voice, int index, DurationalBuilder builder) {
		this.voices.get(voice).set(index, builder);
	}

	/**
	 * Get a reference to the <code>DurationalBuilder</code> at the specified
	 * position.
	 * 
	 * @param voice
	 *            the number of the voice from which to get the object.
	 * @param index
	 *            index in the voice.
	 * @return reference to the <code>DurationalBuilder</code> at the position
	 *         specified by the parameters.
	 */
	public DurationalBuilder get(int voice, int index) {
		return this.voices.get(voice).get(index);
	}

	/**
	 * Get the sum of durations on a voice.
	 * 
	 * @param voice
	 *            the index of the voice.
	 * @return Sum of the durations of the <code>Durational</code> objects on the
	 *         voice.
	 */
	public Duration totalDurationOfVoice(int voice) {
		List<Duration> durations = new ArrayList<>();
		for (DurationalBuilder d : this.voices.get(voice))
			durations.add(d.getDuration());

		return Duration.sumOf(durations);
	}

	/**
	 * Check if voice is full. A voice is considered full when it contains
	 * <code>DurationalBuilder</code> objects whose combined duration is enough to
	 * fill a measure that has the time signature that is set for this builder.
	 * 
	 * @param voice
	 *            index of voice that is checked.
	 * @return true if the durations in the voice add up to fill a measure. False
	 *         otherwise.
	 */
	public boolean isVoiceFull(int voice) {
		Duration voiceDuration = this.totalDurationOfVoice(voice);
		return !voiceDuration.shorterThan(this.timeSig.getTotalDuration());
	}

	/**
	 * Check if any voice in this builder is full.
	 * 
	 * @return true if even a single voice is full. False otherwise.
	 */
	public boolean isFull() {
		for (int voice = 0; voice < this.voices.size(); ++voice) {
			if (this.isVoiceFull(voice)) {
				return true;
			}
		}

		return false;
	}

	private List<Durational> buildVoice(List<DurationalBuilder> buildersForVoice) {
		List<Durational> voice = new ArrayList<>();

		buildersForVoice.forEach((builder) -> voice.add(builder.build()));

		return voice;
	}

	private Map<Integer, List<Durational>> getBuiltVoices() {

		// TODO: Check that voices are full. If not, pad them with rests.

		Map<Integer, List<Durational>> builtVoices = new HashMap<>();
		for (Integer voiceNumber : this.getVoiceNumbers()) {
			List<DurationalBuilder> builders = this.voices.get(voiceNumber);
			builtVoices.put(voiceNumber, builders.stream().map(DurationalBuilder::build).collect(Collectors.toList()));
		}

		return builtVoices;
	}

	/**
	 * Create a <code>Measure</code> with the contents of this builder.
	 * 
	 * @return Measure that has the set attributes and contains
	 *         <code>Durational</code> objects built using the contained
	 *         <code>DurationalBuilder</code> objects.
	 */
	public Measure build() {
		MeasureAttributes measureAttr = MeasureAttributes.getMeasureAttr(this.timeSig, this.keySig, this.rightBarline,
				this.leftBarline, this.clef, this.clefChanges);

		return new Measure(this.number, this.getBuiltVoices(), measureAttr);
	}
}
