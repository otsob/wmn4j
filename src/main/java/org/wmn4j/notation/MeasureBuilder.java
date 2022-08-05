/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.Offset;
import org.wmn4j.notation.directions.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Class for building {@link Measure} objects. The methods {@link #isFull()
 * isFull} and {@link #isFull(int) isVoiceFull} should be used for checking if
 * the durations add up to the correct amount to fill up the measure according
 * to the specified time signature.
 * <p>
 * Instances of this class are not thread-safe.
 */
public class MeasureBuilder {

	private int number;
	private final Map<Integer, List<DurationalBuilder>> voices;
	private final MeasureAttributesBuilder attributesBuilder;
	private final MeasureAttributes initialMeasureAttributes;

	/**
	 * Returns a measure builder with the measure number and attributes of the given measure builder.
	 *
	 * @param builder the builder whose attributes are set to this builder
	 * @return a measure builder with the attributes of the given measure builder
	 */
	public static MeasureBuilder withAttributesOf(MeasureBuilder builder) {
		return new MeasureBuilder(builder.number, builder.getAttributes());
	}

	/**
	 * Creates an empty builder.
	 */
	public MeasureBuilder() {
		this.voices = new HashMap<>();
		this.attributesBuilder = new MeasureAttributesBuilder();
		this.initialMeasureAttributes = null;
	}

	/**
	 * Create a measure builder with the given attributes.
	 *
	 * @param number            measure number for measure being built
	 * @param measureAttributes attributes for the measure
	 */
	public MeasureBuilder(int number, MeasureAttributes measureAttributes) {
		this.voices = new HashMap<>();
		this.number = number;
		this.attributesBuilder = new MeasureAttributesBuilder(measureAttributes);
		this.initialMeasureAttributes = measureAttributes;
	}

	/**
	 * Constructor.
	 *
	 * @param number measure number for measure being built
	 */
	public MeasureBuilder(int number) {
		this.voices = new HashMap<>();
		this.number = number;
		this.attributesBuilder = new MeasureAttributesBuilder();
		this.initialMeasureAttributes = null;
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
		return attributesBuilder.timeSignature;
	}

	/**
	 * Sets the time signature for this builder.
	 *
	 * @param timeSignature the time signature set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setTimeSignature(TimeSignature timeSignature) {
		attributesBuilder.timeSignature = timeSignature;
		return this;
	}

	/**
	 * Returns the key signature set in this builder.
	 *
	 * @return key signature that is set in this builder
	 */
	public KeySignature getKeySignature() {
		return attributesBuilder.keySignature;
	}

	/**
	 * Sets the key signature for this builder.
	 *
	 * @param keySignature key signature set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setKeySignature(KeySignature keySignature) {
		attributesBuilder.keySignature = keySignature;
		return this;
	}

	/**
	 * Returns the clef set in this builder.
	 *
	 * @return the clef set in this builder
	 */
	public Clef getClef() {
		return attributesBuilder.clef;
	}

	/**
	 * Sets the clef in this builder.
	 *
	 * @param clef the clef to be set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setClef(Clef clef) {
		attributesBuilder.clef = clef;
		return this;
	}

	/**
	 * Returns the left barline set in this builder.
	 *
	 * @return left barline set in this builder
	 */
	public Barline getLeftBarline() {
		return attributesBuilder.leftBarline;
	}

	/**
	 * Sets the left barline in this builder.
	 *
	 * @param leftBarline left barline to set for this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setLeftBarline(Barline leftBarline) {
		attributesBuilder.leftBarline = leftBarline;
		return this;
	}

	/**
	 * Returns the right barline set in this builder.
	 *
	 * @return the right barline set in this builder
	 */
	public Barline getRightBarline() {
		return attributesBuilder.rightBarline;
	}

	/**
	 * Sets the right barline in this builder.
	 *
	 * @param rightBarline right barline to be set in this builder
	 * @return reference to this builder
	 */
	public MeasureBuilder setRightBarline(Barline rightBarline) {
		attributesBuilder.rightBarline = rightBarline;
		return this;
	}

	/**
	 * Returns the clef changes set in this builder.
	 * <p>
	 * The placement of clef changes are represented using {@link Offset} types,
	 * where the placement of the clef change is measured by an offset from the
	 * beginning of the measure. The collection is not in any particular order.
	 *
	 * @return the clef changes specified in the attributes
	 */
	public Collection<Offset<Clef>> getClefChanges() {
		return attributesBuilder.clefChanges;
	}

	/**
	 * Returns the directions set in this builder.
	 * <p>
	 * The placement of directions are represented using {@link Offset} types,
	 * where the placement of the directions is measured by an offset from the
	 * beginning of the measure. The collection is not in any particular order.
	 *
	 * @return the directions set in this builder
	 */
	public Collection<Offset<Direction>> getDirections() {
		return attributesBuilder.directions;
	}

	/**
	 * Adds a clef change at the given offset.
	 *
	 * @param offset offset of clef change from beginning of measure
	 * @param clef   clef starting from offset
	 * @return reference to this builder
	 */
	public MeasureBuilder addClefChange(Duration offset, Clef clef) {
		attributesBuilder.clefChanges.add(new Offset<>(clef, offset));
		return this;
	}

	/**
	 * Adds a {@link Direction} with the given offset.
	 *
	 * @param offset    the duration of the offset, can be null if direction is not offset
	 * @param direction the direction to add at given offset
	 * @return reference to this builder
	 */
	public MeasureBuilder addDirection(Duration offset, Direction direction) {
		attributesBuilder.directions.add(new Offset<>(direction, offset));
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
	public int getVoiceCount() {
		return this.voices.size();
	}

	/**
	 * Returns the voice numbers set in this builder. Voice numbers do not have to
	 * be contiguous.
	 *
	 * @return the set of voice numbers in this builder
	 */
	public List<Integer> getVoiceNumbers() {
		return new ArrayList<>(this.voices.keySet());
	}

	/**
	 * Sets the element at specified location to given value.
	 *
	 * @param voice   the number of the voice to be modified
	 * @param index   the index in the voice
	 * @param builder element to be placed in index on voice
	 * @return reference to this
	 */
	public MeasureBuilder setElement(int voice, int index, DurationalBuilder builder) {
		this.voices.get(voice).set(index, builder);
		return this;
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

		return Duration.sum(durations);
	}

	/**
	 * Returns true if the voice with the given number is full. A voice is
	 * considered full when the durations in it are enough to fill a measure with
	 * the time signature set in this builder. The voice may contain more durational
	 * elements than fit in a measure with the time signature set in this builder.
	 *
	 * @param voice the number of voice that is checked
	 * @return true if the durations in the voice add up to fill a measure
	 */
	public boolean isFull(int voice) {
		final Duration voiceDuration = this.totalDurationOfVoice(voice);
		return !voiceDuration.isShorterThan(attributesBuilder.timeSignature.getTotalDuration());
	}

	/**
	 * Returns true if any voice in this builder is full. A voice is considered full
	 * when the durations in it are enough to fill a measure with the time signature
	 * set in this builder. The voice may contain more durational elements than fit
	 * in a measure with the time signature set in this builder.
	 *
	 * @return true if even a single voice is full. False otherwise.
	 */
	public boolean isFull() {
		return voices.keySet().stream().anyMatch(voiceNumber -> isFull(voiceNumber));
	}

	/**
	 * Returns true if the total duration of the durational elements in the voice
	 * with the given number is more than can fit into a measure with the time
	 * signature set in this builder.
	 *
	 * @param voice the number of the voice that is checked
	 * @return true if the total duration of the durational elements in the voice
	 * exceed what can fit in the measure
	 */
	public boolean isOverflowing(int voice) {
		return totalDurationOfVoice(voice).isLongerThan(getTimeSignature().getTotalDuration());
	}

	/**
	 * Returns true if any voice in this builder is overflowing. A voice is
	 * overflowing if the total duration of the durational elements in the voice is
	 * more than can fit into a measure with the time signature set in this builder.
	 *
	 * @return true if any voice in this builder is overflowing
	 */
	public boolean isOverflowing() {
		return voices.keySet().stream().anyMatch(voiceNumber -> isOverflowing(voiceNumber));
	}

	/**
	 * Trims the excess durations from this builder. The durational elements that
	 * start from this builder and would continue onto the next measure are reduced
	 * in duration to fit into a measure with the time signature set in this
	 * builder.
	 */
	public void trim() {
		voices.keySet().stream().filter(voice -> isOverflowing(voice)).forEach(voice -> trimVoice(voice));
	}

	private void trimVoice(int voice) {
		List<DurationalBuilder> voiceContents = voices.get(voice);

		Duration cumulatedDuration = null;
		final Duration maxTotalDuration = getTimeSignature().getTotalDuration();

		for (int i = 0; i < voiceContents.size(); ++i) {
			DurationalBuilder builder = voiceContents.get(i);
			Duration duration = builder.getDuration();
			Duration cumulatedWithCurrent = cumulatedDuration == null ? duration
					: cumulatedDuration.add(duration);

			// If this is true, the builder at index i fills the voice to be exactly of the
			// duration specified by the time signature. The rest of the builders are
			// discarded.
			if (cumulatedWithCurrent.equals(maxTotalDuration)) {
				voiceContents.subList(i + 1, voiceContents.size()).clear();
				break;
			}

			// If this is true, the builder at index i fills the voice to exceed the
			// duration specified by the time signature so the duration needs to be
			// reduced. The rest of the builders are discarded.
			if (cumulatedWithCurrent.isLongerThan(maxTotalDuration)) {
				final Duration allowedDuration = cumulatedDuration == null ? maxTotalDuration
						: maxTotalDuration.subtract(cumulatedDuration);
				builder.setDuration(allowedDuration);
				voiceContents.subList(i + 1, voiceContents.size()).clear();
				break;
			}

			cumulatedDuration = cumulatedWithCurrent;
		}
	}

	private Map<Integer, List<Durational>> buildVoices(boolean padWithRests) {

		final Map<Integer, List<Durational>> builtVoices = new HashMap<>();
		for (Integer voiceNumber : this.getVoiceNumbers()) {
			final List<DurationalBuilder> buildersInVoice = this.voices.get(voiceNumber);

			if (padWithRests) {
				final Duration totalDurationOfVoice = totalDurationOfVoice(voiceNumber);

				final Duration timeSignatureDuration = getTimeSignature().getTotalDuration();

				if (!totalDurationOfVoice.equals(timeSignatureDuration)
						&& totalDurationOfVoice.isShorterThan(timeSignatureDuration)) {
					final Duration missingDuration = timeSignatureDuration.subtract(totalDurationOfVoice);
					buildersInVoice.add(new RestBuilder(missingDuration));
				}
			}

			builtVoices.put(voiceNumber, buildersInVoice.stream().map(DurationalBuilder::build)
					.collect(Collectors.toList()));
		}

		return builtVoices;
	}

	private MeasureAttributes getAttributes() {
		if (attributesBuilder.equalsInContent(initialMeasureAttributes)) {
			return initialMeasureAttributes;
		}

		return attributesBuilder.build();
	}

	/**
	 * Returns a {@link Measure} with the values set in this builder. Voices that do
	 * not contain enough durational notation elements are padded with rests at the
	 * end. If any voice has a total duration that exceeds what can fit into a
	 * measure with the time signature set in this builder, then those voices are
	 * trimmed by discarding the exceeding durations. Any durational that partly
	 * fits in the measure is reduced in duration to fit into the built measure.
	 *
	 * @return a measure with the values set in this builder
	 */
	public Measure build() {
		return build(true, true);
	}

	/**
	 * Returns a {@link Measure} with the values set in this builder.
	 *
	 * @param padWithRests set this to true to pad the voices that do not fill the
	 *                     measure with rests
	 * @param trim         set this to true to trim the durational elements whose
	 *                     durations exceed what can fit into the measure
	 * @return a measure with the values set in this builder
	 */
	public Measure build(boolean padWithRests, boolean trim) {
		if (trim) {
			this.trim();
		}

		return Measure.of(this.number, this.buildVoices(padWithRests), getAttributes());
	}

	private final class MeasureAttributesBuilder {
		private TimeSignature timeSignature;
		private KeySignature keySignature;
		private Clef clef;
		private Barline leftBarline;
		private Barline rightBarline;
		private Set<Offset<Clef>> clefChanges;
		private Set<Offset<Direction>> directions;

		private MeasureAttributesBuilder() {
			this.timeSignature = TimeSignatures.FOUR_FOUR;
			this.keySignature = KeySignatures.CMAJ_AMIN;
			this.clef = Clefs.G;
			this.leftBarline = Barline.NONE;
			this.rightBarline = Barline.SINGLE;
			this.clefChanges = new HashSet<>();
			this.directions = new HashSet<>();
		}

		private MeasureAttributesBuilder(MeasureAttributes measureAttributes) {
			this.timeSignature = measureAttributes.getTimeSignature();
			this.keySignature = measureAttributes.getKeySignature();
			this.clef = measureAttributes.getClef();
			this.leftBarline = measureAttributes.getLeftBarline();
			this.rightBarline = measureAttributes.getRightBarline();
			this.clefChanges = new HashSet<>(measureAttributes.getClefChanges());
			this.directions = new HashSet<>(measureAttributes.getDirections());
		}

		private MeasureAttributes build() {
			return MeasureAttributes
					.of(timeSignature, keySignature, rightBarline, leftBarline, clef, clefChanges, directions);
		}

		private boolean equalsInContent(MeasureAttributes measureAttributes) {
			if (measureAttributes == null) {
				return false;
			}

			if (!timeSignature.equals(measureAttributes.getTimeSignature())) {
				return false;
			}

			if (!keySignature.equals(measureAttributes.getKeySignature())) {
				return false;
			}

			if (!clef.equals(measureAttributes.getClef())) {
				return false;
			}

			if (!leftBarline.equals(measureAttributes.getLeftBarline())) {
				return false;
			}

			if (!rightBarline.equals(measureAttributes.getRightBarline())) {
				return false;
			}

			if (!clefChanges.equals(measureAttributes.getClefChanges())) {
				return false;
			}

			if (!directions.equals(measureAttributes.getDirections())) {
				return false;
			}

			return true;
		}
	}
}
