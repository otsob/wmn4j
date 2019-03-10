/*
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
 * Class for building {@link Measure} objects. The methods {@link #isFull()
 * isFull} and {@link #isVoiceFull(int) isVoiceFull} should be used for checking
 * if the durations add up to the correct amount to fill up the measure
 * according to the specified time signature.
 *
 * Default values: TimeSignature : 4/4 KeySignature : C-major/a-minor Clef: G
 * Barlines (right and left): Single. No clef changes.
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
	 * Create a measure builder with the given attributes.
	 *
	 * @param number            measure number for measure being built
	 * @param measureAttributes attributes for the measure
	 */
	public MeasureBuilder(int number, MeasureAttributes measureAttributes) {
		this.voices = new HashMap<>();
		this.number = number;

		this.timeSig = measureAttributes.getTimeSignature();
		this.keySig = measureAttributes.getKeySignature();
		this.clef = measureAttributes.getClef();
		this.leftBarline = measureAttributes.getLeftBarline();
		this.rightBarline = measureAttributes.getRightBarline();
		this.clefChanges = new HashMap<>(measureAttributes.getClefChanges());
	}

	/**
	 * Constructor.
	 *
	 * @param number measure number for measure being built
	 */
	public MeasureBuilder(int number) {
		this.voices = new HashMap<>();
		this.number = number;
	}

	/**
	 * Returns the measure number set in this builder.
	 *
	 * @return the measure number set in this builder
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Sets the measure number in this builder.
	 *
	 * @param number the measure number to set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setNumber(int number) {
		this.number = number;
		return this;
	}

	/**
	 * Returns the time signature set in this builder.
	 *
	 * @return time signature set for this builder
	 */
	public TimeSignature getTimeSignature() {
		return this.timeSig;
	}

	/**
	 * Sets the time signature for this builder.
	 *
	 * @param timeSignature the time signature set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setTimeSignature(TimeSignature timeSignature) {
		this.timeSig = timeSignature;
		return this;
	}

	/**
	 * Returns the key signature set in this builder.
	 *
	 * @return key signature that is set in this builder
	 */
	public KeySignature getKeySignature() {
		return this.keySig;
	}

	/**
	 * Sets the key signature for this builder.
	 *
	 * @param keySignature key signature set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setKeySignature(KeySignature keySignature) {
		this.keySig = keySignature;
		return this;
	}

	/**
	 * Returns the clef set in this builder.
	 *
	 * @return the clef set in this builder
	 */
	public Clef getClef() {
		return this.clef;
	}

	/**
	 * Sets the clef in this builder.
	 *
	 * @param clef the clef to be set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setClef(Clef clef) {
		this.clef = clef;
		return this;
	}

	/**
	 * Returns the left barline set in this builder.
	 *
	 * @return left barline set in this builder
	 */
	public Barline getLeftBarline() {
		return this.leftBarline;
	}

	/**
	 * Sets the left barline in this builder.
	 *
	 * @param leftBarline left barline to set for this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setLeftBarline(Barline leftBarline) {
		this.leftBarline = leftBarline;
		return this;
	}

	/**
	 * Returns the right barline set in this builder.
	 *
	 * @return the right barline set in this builder
	 */
	public Barline getRightBarline() {
		return rightBarline;
	}

	/**
	 * Sets the right barline in this builder.
	 *
	 * @param rightBarline right barline to be set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setRightBarline(Barline rightBarline) {
		this.rightBarline = rightBarline;
		return this;
	}

	/**
	 * Returns the clef changes in this builder. The keys in the map are the offset
	 * values of the clef changes measured from the beginning of the measure.
	 *
	 * @return clef changes currently set for this builder. Durations are offsets
	 *         from the beginning of the measure.
	 */
	public Map<Duration, Clef> getClefChanges() {
		return this.clefChanges;
	}

	/**
	 * Adds a clef change at the given offset.
	 *
	 * @param offset offset of clef change from beginning of measure
	 * @param clef   clef starting from offset
	 * @return reference to this builder
	 */
	public MeasureBuilder addClefChange(Duration offset, Clef clef) {
		this.clefChanges.put(offset, clef);
		return this;
	}

	/**
	 * Add new empty voice to this builder.
	 *
	 * @return reference to this builder.
	 */
	public MeasureBuilder addVoice() {
		this.voices.put(this.voices.keySet().size(), new ArrayList<>());
		return this;
	}

	/**
	 * Add possibly voice to this builder.
	 *
	 * @param voice new voice to be added to this
	 * @return reference to this builder
	 */
	public MeasureBuilder addVoice(List<DurationalBuilder> voice) {
		this.voices.put(this.voices.keySet().size(), voice);
		return this;
	}

	/**
	 * Append a {@link DurationalBuilder} to the voice with the given number. If
	 * voice does not exist it is created.
	 *
	 * @param voice   number of voice to which builder is appended
	 * @param builder builder object to be appended to voice
	 * @return reference to this builder
	 */
	public MeasureBuilder addToVoice(int voice, DurationalBuilder builder) {

		if (!this.voices.keySet().contains(voice)) {
			this.voices.put(voice, new ArrayList<>());
		}

		this.voices.get(voice).add(builder);
		return this;
	}

	/**
	 * Returns the number of voices in this builder.
	 *
	 * @return number or voices in this builder
	 */
	public int getNumberOfVoices() {
		return this.voices.size();
	}

	/**
	 * Returns the voice numbers set in this builder. Voice numbers do not have to
	 * be contiguous.
	 *
	 * @return the set of voice numbers in this builder
	 */
	public Set<Integer> getVoiceNumbers() {
		return Collections.unmodifiableSet(this.voices.keySet());
	}

	/**
	 * Sets the element at specified location to given value.
	 *
	 * @param voice   the number of the voice to be modified
	 * @param index   the index in the voice
	 * @param builder element to be placed in index on voice
	 */
	public void setElement(int voice, int index, DurationalBuilder builder) {
		this.voices.get(voice).set(index, builder);
	}

	/**
	 * Returns the builder at the given position.
	 *
	 * @param voice the number of the voice from which to get the object
	 * @param index index in the voice
	 * @return reference to the builder at the position specified by the parameters
	 */
	public DurationalBuilder get(int voice, int index) {
		return this.voices.get(voice).get(index);
	}

	/**
	 * Returns the sum of durations in a voice.
	 *
	 * @param voice the index of the voice
	 * @return sum of the durations of the in the voice
	 */
	public Duration totalDurationOfVoice(int voice) {
		final List<Duration> durations = new ArrayList<>();
		for (DurationalBuilder d : this.voices.get(voice)) {
			durations.add(d.getDuration());
		}

		return Duration.sumOf(durations);
	}

	/**
	 * Returns true if the voice with the given number is full. A voice is
	 * considered full when the durations in it are enough to fill a measure with
	 * the time signature set in this builder.
	 *
	 * @param voice index of voice that is checked
	 * @return true if the durations in the voice add up to fill a measure
	 */
	public boolean isVoiceFull(int voice) {
		final Duration voiceDuration = this.totalDurationOfVoice(voice);
		return !voiceDuration.isShorterThan(this.timeSig.getTotalDuration());
	}

	/**
	 * Returns true if any voice in this builder is full.
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

	private Map<Integer, List<Durational>> getBuiltVoices() {

		// TODO: Check that voices are full. If not, pad them with rests.

		final Map<Integer, List<Durational>> builtVoices = new HashMap<>();
		for (Integer voiceNumber : this.getVoiceNumbers()) {
			final List<DurationalBuilder> builders = this.voices.get(voiceNumber);
			builtVoices.put(voiceNumber, builders.stream().map(DurationalBuilder::build).collect(Collectors.toList()));
		}

		return builtVoices;
	}

	/**
	 * Returns a {@link Measure} with the values set in this builder.
	 *
	 * @return a measure with the values set in this builder
	 */
	public Measure build() {
		final MeasureAttributes measureAttr = MeasureAttributes.of(this.timeSig, this.keySig,
				this.rightBarline, this.leftBarline, this.clef, this.clefChanges);

		return new Measure(this.number, this.getBuiltVoices(), measureAttr);
	}
}
