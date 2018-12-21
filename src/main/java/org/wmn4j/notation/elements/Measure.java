/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

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
 * Class that defines a measure. A measure may contain multiple voices that are
 * referred to using voice numbers. This class is immutable. Use the
 * MeasureBuilder class for easier creation of Measures.
 *
 * @author Otso Björklund
 */
public class Measure implements Iterable<Durational> {

	private final int number;
	private final SortedMap<Integer, List<Durational>> voices;
	private final MeasureAttributes measureAttr;

	/**
	 * @param number       number of the measure.
	 * @param noteVoices   the notes on the different voices of the measure.
	 * @param timeSig      TimeSignature of the measure.
	 * @param keySig       KeySignature in effect in the measure.
	 * @param rightBarLine barline on the right side (left side is NONE by default).
	 * @param clef         Clef in effect in the measure.
	 */
	public Measure(int number, Map<Integer, List<Durational>> noteVoices, TimeSignature timeSig, KeySignature keySig,
			Barline rightBarLine, Clef clef) {
		this(number, noteVoices, MeasureAttributes.getMeasureAttr(timeSig, keySig, rightBarLine, clef));
	}

	/**
	 * @param number     number of the measure.
	 * @param noteVoices the notes on the different voices of the measure.
	 * @param timeSig    TimeSignature of the measure.
	 * @param keySig     KeySignature in effect in the measure.
	 * @param clef       Clef in effect in the measure.
	 */
	public Measure(int number, Map<Integer, List<Durational>> noteVoices, TimeSignature timeSig, KeySignature keySig,
			Clef clef) {
		this(number, noteVoices, MeasureAttributes.getMeasureAttr(timeSig, keySig, Barline.SINGLE, clef));
	}

	/**
	 * @param number      number of the measure.
	 * @param noteVoices  the notes on the different voices of the measure.
	 * @param measureAttr the attributes of the measure.
	 */
	public Measure(int number, Map<Integer, List<Durational>> noteVoices, MeasureAttributes measureAttr) {
		this.number = number;
		SortedMap<Integer, List<Durational>> voicesCopy = new TreeMap<>();

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
	 * Get the voice numbers in this measure. Voice numbers are not necessarily
	 * consecutive and do not begin from 0.
	 *
	 * @return list of the voice numbers used in this measure.
	 */
	public List<Integer> getVoiceNumbers() {
		return new ArrayList<>(this.voices.keySet());
	}

	/**
	 * Get a voiceNumber of the measure.
	 *
	 * @param voiceNumber the number of the voice.
	 * @return the voiceNumber at the given index voiceNumber.
	 */
	public List<Durational> getVoice(int voiceNumber) {
		return this.voices.get(voiceNumber);
	}

	/**
	 * @param voiceNumber the voice for which the number of elements is returned
	 * @return the number of elements on the voice with voiceNumber.
	 */
	public int getVoiceSize(int voiceNumber) {
		return this.voices.get(voiceNumber).size();
	}

	/**
	 * @return number of voices in this measure.
	 */
	public int getVoiceCount() {
		return this.voices.keySet().size();
	}

	/**
	 * @return true if this measure only has one voice, false otherwise.
	 */
	public boolean isSingleVoice() {
		return this.getVoiceCount() == 1;
	}

	/**
	 * @return the number of this measure.
	 */
	public int getNumber() {
		return this.number;
	}

	/**
	 * @return TimeSignature in effect in this measure.
	 */
	public TimeSignature getTimeSignature() {
		return this.measureAttr.getTimeSignature();
	}

	/**
	 * @return KeySignature in effect in this measure.
	 */
	public KeySignature getKeySignature() {
		return this.measureAttr.getKeySignature();
	}

	/**
	 * @return right barline of this measure.
	 */
	public Barline getRightBarline() {
		return this.measureAttr.getRightBarline();
	}

	/**
	 * @return left barline of this measure.
	 */
	public Barline getLeftBarline() {
		return this.measureAttr.getLeftBarline();
	}

	/**
	 * @return The clef in effect in the beginning of this measure.
	 */
	public Clef getClef() {
		return this.measureAttr.getClef();
	}

	/**
	 * @return a Map of clef changes in this measure, where the Duration key is the
	 *         offset counted from the beginning of the measure.
	 */
	public Map<Duration, Clef> getClefChanges() {
		return this.measureAttr.getClefChanges();
	}

	/**
	 * @return true if there are clef changes in this measure, false otherwise.
	 */
	public boolean containsClefChanges() {
		return this.measureAttr.containsClefChanges();
	}

	/**
	 * @return true if this <code>Measure</code> is a pickup measure.
	 */
	public boolean isPickUp() {
		return this.getNumber() == 0;
	}

	/**
	 * Returns the <code>Durational</code> at the given index on the given voice
	 * number.
	 *
	 * @param voiceNumber Number of the voice from which to get the element.
	 * @param index       index of element on the voice.
	 * @return <code>Durational</code> at the given index on the given voice.
	 * @throws NoSuchElementException if there is no voice with the given number of
	 *                                if the index is out of range
	 */
	public Durational get(int voiceNumber, int index) throws NoSuchElementException {
		if (!this.voices.keySet().contains(voiceNumber)) {
			throw new NoSuchElementException();
		}

		List<Durational> voice = this.voices.get(voiceNumber);
		if (index < 0 || index >= voice.size()) {
			throw new NoSuchElementException();
		}

		return voice.get(index);
	}

	/**
	 * String representation of <code>Measure</code>. This is subject to change.
	 *
	 * @return string representation of measure.
	 */
	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
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

	/**
	 * @return iterator that goes through the Measure voice wise.
	 */
	@Override
	public Iterator<Durational> iterator() {
		return this.getMeasureIterator();
	}

	/**
	 * Get an iterator as <code>Measure.Iter</code>.
	 *
	 * @return an iterator of type <code>Measure.Iter</code>.
	 */
	public Measure.Iter getMeasureIterator() {
		return new Iter(this);
	}

	/**
	 * Iterator for <code>Durational</code> objects in a <code>Measure</code>. The
	 * iterator iterates through the notes in the Measure voice by voice going from
	 * the earliest Durational in the voice to the last on each voice. The order of
	 * voices is unspecified. The iterator does not support removing.
	 */
	public static class Iter implements Iterator<Durational> {
		private final List<Integer> voiceNumbers;
		private final Measure measure;
		private int voiceNumberIndex = 0;
		private int positionInVoice = 0;
		private int prevVoiceNumber = 0;
		private int prevPositionInVoice = 0;

		public Iter(Measure measure) {
			this.measure = measure;
			this.voiceNumbers = measure.getVoiceNumbers();
		}

		/**
		 * @return The voice of the <code>Durational</code> that was returned by the
		 *         last call of {@link #next() next}. If next has not been called,
		 *         return value is useless.
		 */
		public int getVoiceOfPrevious() {
			return this.prevVoiceNumber;
		}

		/**
		 * @return The index of the <code>Durational</code> that was returned by the
		 *         last call of {@link #next() next}. If next has not been called,
		 *         return value is useless.
		 */
		public int getIndexOfPrevious() {
			return this.prevPositionInVoice;
		}

		@Override
		public boolean hasNext() {
			if (voiceNumberIndex >= this.voiceNumbers.size()) {
				return false;
			}

			int voiceNumber = this.voiceNumbers.get(this.voiceNumberIndex);
			return !this.measure.getVoice(voiceNumber).isEmpty();
		}

		@Override
		public Durational next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			this.prevVoiceNumber = this.voiceNumbers.get(this.voiceNumberIndex);
			List<Durational> currentVoice = this.measure.getVoice(this.prevVoiceNumber);
			this.prevPositionInVoice = this.positionInVoice;
			Durational next = currentVoice.get(this.prevPositionInVoice);

			++this.positionInVoice;
			if (this.positionInVoice == currentVoice.size()) {
				++this.voiceNumberIndex;
				this.positionInVoice = 0;
			}

			return next;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("Removing not supported.");
		}
	}
}
