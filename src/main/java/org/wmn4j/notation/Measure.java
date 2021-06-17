/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.MeasureIterator;
import org.wmn4j.notation.access.Offset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Represents a measure. A measure may contain multiple voices that are referred
 * to using voice numbers. This class is immutable. Use the MeasureBuilder class
 * for easier creation of Measures.
 * <p>
 * This class is immutable.
 */
public final class Measure implements Iterable<Durational> {

	private final int number;
	private final SortedMap<Integer, List<Durational>> voices;
	private final MeasureAttributes measureAttr;

	/**
	 * Returns a measure with the given values.
	 *
	 * @param number       number of the measure
	 * @param noteVoices   the notes on the different voices of the measure
	 * @param timeSig      TimeSignature of the measure
	 * @param keySig       KeySignature in effect in the measure
	 * @param rightBarLine barline on the right side (left side is NONE by default)
	 * @param clef         Clef in effect in the measure
	 * @return a measure with the given values
	 */
	public static Measure of(int number, Map<Integer, List<Durational>> noteVoices, TimeSignature timeSig,
			KeySignature keySig,
			Barline rightBarLine, Clef clef) {
		return new Measure(number, noteVoices, MeasureAttributes.of(timeSig, keySig, rightBarLine, clef));
	}

	/**
	 * Returns a measure with the given values.
	 *
	 * @param number     number of the measure
	 * @param noteVoices the notes on the different voices of the measure
	 * @param timeSig    TimeSignature of the measure
	 * @param keySig     KeySignature in effect in the measure
	 * @param clef       Clef in effect in the measure
	 * @return a measure with the given values
	 */
	public static Measure of(int number, Map<Integer, List<Durational>> noteVoices, TimeSignature timeSig,
			KeySignature keySig,
			Clef clef) {
		return new Measure(number, noteVoices, MeasureAttributes.of(timeSig, keySig, Barline.SINGLE, clef));
	}

	/**
	 * Returns a measure with the given values.
	 *
	 * @param number      number of the measure
	 * @param noteVoices  the notes on the different voices of the measure
	 * @param measureAttr the attributes of the measure
	 * @return a measure with the given values
	 */
	public static Measure of(int number, Map<Integer, List<Durational>> noteVoices, MeasureAttributes measureAttr) {
		return new Measure(number, noteVoices, measureAttr);
	}

	/**
	 * Returns a full measure rest with the given measure number and attributes.
	 *
	 * @param number            number of the measure
	 * @param measureAttributes the attributes of the measure
	 * @return a full measure rest with the given measure number and attributes
	 */
	public static Measure restMeasureOf(int number, MeasureAttributes measureAttributes) {
		return new Measure(number, Collections.emptyMap(), measureAttributes);
	}

	/**
	 * Returns a pickup measure with the given contents and measure attributes.
	 *
	 * @param noteVoices        noteVoices  the notes on the different voices of the measure
	 * @param measureAttributes the attributes of the measure
	 * @return a pickup measure with the given contents and measure attributes
	 */
	public static Measure pickupOf(Map<Integer, List<Durational>> noteVoices, MeasureAttributes measureAttributes) {
		return new Measure(0, noteVoices, measureAttributes);
	}

	/**
	 * Constructor.
	 *
	 * @param number      number of the measure.
	 * @param noteVoices  the notes on the different voices of the measure.
	 * @param measureAttr the attributes of the measure.
	 */
	private Measure(int number, Map<Integer, List<Durational>> noteVoices, MeasureAttributes measureAttr) {
		if (number < 0) {
			throw new IllegalArgumentException("Measure number cannot be negative");
		}

		this.number = number;
		final SortedMap<Integer, List<Durational>> voicesCopy = new TreeMap<>();

		for (Integer voiceNum : noteVoices.keySet()) {
			voicesCopy.put(voiceNum, Collections.unmodifiableList(new ArrayList<>(noteVoices.get(voiceNum))));
		}

		this.voices = Collections.unmodifiableSortedMap(voicesCopy);

		this.measureAttr = Objects.requireNonNull(measureAttr);

		if (this.number < 0) {
			throw new IllegalArgumentException("Measure number must be at least 0");
		}
	}

	/**
	 * Returns the voice numbers in this measure in ascending order.
	 * Voice numbers are not necessarily consecutive and do not need begin from 0.
	 *
	 * @return list of the voice numbers used in this measure.
	 */
	public List<Integer> getVoiceNumbers() {
		return new ArrayList<>(this.voices.keySet());
	}

	/**
	 * Returns the number of durational notation elements in the voice with the
	 * given number.
	 *
	 * @param voiceNumber the voice for which the number of elements is returned
	 * @return the number of elements in the voice with voiceNumber
	 */
	public int getVoiceSize(int voiceNumber) {
		return this.voices.get(voiceNumber).size();
	}

	/**
	 * Returns the number of voices in this measure.
	 *
	 * @return number of voices in this measure
	 */
	public int getVoiceCount() {
		return this.voices.keySet().size();
	}

	/**
	 * Returns true if this measure has only a single voice.
	 *
	 * @return true if this measure only has one voice, false otherwise.
	 */
	public boolean isSingleVoice() {
		return this.getVoiceCount() == 1;
	}

	/**
	 * Returns the measure number of this measure.
	 *
	 * @return the number of this measure.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * Returns the time signature in effect in this measure.
	 *
	 * @return the time signature in effect in this measure
	 */
	public TimeSignature getTimeSignature() {
		return this.measureAttr.getTimeSignature();
	}

	/**
	 * Returns the key signature in effect in this measure.
	 *
	 * @return the key signature in effect in this measure
	 */
	public KeySignature getKeySignature() {
		return this.measureAttr.getKeySignature();
	}

	/**
	 * Returns the barline on the right side of this measure.
	 *
	 * @return right barline of this measure.
	 */
	public Barline getRightBarline() {
		return this.measureAttr.getRightBarline();
	}

	/**
	 * Returns the barline on the left side of this measure. For example, this can
	 * be a barline that denotes the beginning of a repeat.
	 *
	 * @return left barline of this measure
	 */
	public Barline getLeftBarline() {
		return this.measureAttr.getLeftBarline();
	}

	/**
	 * Returns the clef in effect in the beginning of this measure.
	 *
	 * @return the clef in effect in the beginning of this measure
	 */
	public Clef getClef() {
		return this.measureAttr.getClef();
	}

	/**
	 * Returns the clef changes in this measure.
	 * <p>
	 * The placement of clef changes are represented using {@link Offset} types,
	 * where the placement of the clef change is measured by an offset from the
	 * beginning of the measure. The list is sorted in ascending order of offset.
	 *
	 * @return the clef changes in this measure.
	 */
	public List<Offset<Clef>> getClefChanges() {
		return this.measureAttr.getClefChanges();
	}

	/**
	 * Returns true if this measure contains clef changes.
	 *
	 * @return true if there are clef changes in this measure, false otherwise.
	 */
	public boolean containsClefChanges() {
		return this.measureAttr.containsClefChanges();
	}

	/**
	 * Returns true if this measure is a pickup measure.
	 *
	 * @return true if this measure is a pickup measure.
	 */
	public boolean isPickup() {
		return this.getNumber() == 0;
	}

	/**
	 * Returns the {@link Durational} at the given index on the given voice number.
	 *
	 * @param voiceNumber the number of the voice from which to get the element
	 * @param index       index of element on the voice
	 * @return the {@link Durational} at the given index on the given voice
	 * @throws NoSuchElementException if there is no voice with the given number of
	 *                                if the index is out of range
	 */
	public Durational get(int voiceNumber, int index) throws NoSuchElementException {
		if (!this.voices.keySet().contains(voiceNumber)) {
			throw new NoSuchElementException();
		}

		final List<Durational> voice = this.voices.get(voiceNumber);
		if (index < 0 || index >= voice.size()) {
			throw new NoSuchElementException();
		}

		return voice.get(index);
	}

	/**
	 * Returns true if this measure is a full measure rest.
	 *
	 * @return true if this measure is a full measure rest
	 */
	public boolean isFullMeasureRest() {
		return this.voices.isEmpty();
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Measure ").append(this.number).append(", ").append(this.measureAttr).append(":\n");

		for (Integer i : this.voices.keySet()) {
			strBuilder.append("Voice ").append(i).append(": ");
			for (int j = 0; j < voices.get(i).size(); ++j) {
				strBuilder.append(voices.get(i).get(j).toString());
				if (j != voices.get(i).size() - 1) {
					strBuilder.append(", ");
				}
			}
			strBuilder.append("\n");
		}

		return strBuilder.toString();
	}

	@Override
	public Iterator<Durational> iterator() {
		return this.getMeasureIterator();
	}

	/**
	 * Returns a measure iterator for this measure.
	 *
	 * @return a measure iterator for this measure
	 */
	public MeasureIterator getMeasureIterator() {
		return new Iter(this);
	}

	private static class Iter implements MeasureIterator {
		private final List<Integer> voiceNumbers;
		private final Measure measure;
		private int voiceNumberIndex = 0;
		private int positionInVoice = 0;
		private int prevVoiceNumber = 0;
		private int prevPositionInVoice = 0;

		/**
		 * Constructor.
		 *
		 * @param measure the measure for which the iterator is created
		 */
		Iter(Measure measure) {
			this.measure = measure;
			this.voiceNumbers = measure.getVoiceNumbers();
		}

		@Override
		public int getVoiceOfPrevious() {
			return this.prevVoiceNumber;
		}

		@Override
		public int getIndexOfPrevious() {
			return this.prevPositionInVoice;
		}

		@Override
		public boolean hasNext() {
			if (voiceNumberIndex >= this.voiceNumbers.size()) {
				return false;
			}

			final int voiceNumber = this.voiceNumbers.get(this.voiceNumberIndex);
			return this.measure.getVoiceSize(voiceNumber) > 0;
		}

		@Override
		public Durational next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			this.prevVoiceNumber = this.voiceNumbers.get(this.voiceNumberIndex);
			this.prevPositionInVoice = this.positionInVoice;
			final Durational next = measure.get(prevVoiceNumber, prevPositionInVoice);

			++this.positionInVoice;
			if (this.positionInVoice == measure.getVoiceSize(prevVoiceNumber)) {
				++this.voiceNumberIndex;
				this.positionInVoice = 0;
			}

			return next;
		}
	}
}
