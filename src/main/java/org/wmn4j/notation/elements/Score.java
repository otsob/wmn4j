/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.wmn4j.notation.iterators.ScorePosition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Represents a score. This class is immutable.
 */
public final class Score implements Iterable<Part> {

	/**
	 * Type for the different text attributes a score can have.
	 */
	public enum Attribute {
		/**
		 * The main title of the score.
		 */
		TITLE,
		/**
		 * The composer name.
		 */
		COMPOSER,

		/**
		 * The arranger.
		 */
		ARRANGER,
		/**
		 * The year of publication.
		 */
		YEAR
	}

	private final Map<Attribute, String> scoreAttr;
	private final List<Part> parts;

	/**
	 * Returns a score with the given attributes and parts.
	 *
	 * @param attributes the attributes of the score
	 * @param parts      the parts in the score
	 * @return a score with the given attributes and parts
	 */
	public static Score of(Map<Attribute, String> attributes, List<Part> parts) {
		return new Score(attributes, parts);
	}

	/**
	 * Constructor.
	 *
	 * @param attributes the attributes of the score
	 * @param parts      the parts in the score
	 */
	private Score(Map<Attribute, String> attributes, List<Part> parts) {
		this.parts = Collections.unmodifiableList(new ArrayList<>(parts));
		this.scoreAttr = Collections.unmodifiableMap(new HashMap<>(attributes));

		if (this.parts.isEmpty()) {
			throw new IllegalArgumentException("Cannot create score: parts is empty");
		}
	}

	/**
	 * Returns the main title of this score.
	 *
	 * @return the main title of this score
	 */
	public String getTitle() {
		return this.getAttribute(Attribute.TITLE);
	}

	/**
	 * Returns the number of parts in this score.
	 *
	 * @return number of parts in this score
	 */
	public int getPartCount() {
		return this.parts.size();
	}

	/**
	 * Returns the parts in this score.
	 *
	 * @return the parts in this score
	 */
	public List<Part> getParts() {
		return this.parts;
	}

	/**
	 * Returns the part at the index.
	 *
	 * @param index the number of the part in this score
	 * @return the part at the index
	 */
	public Part getPart(int index) {
		return this.parts.get(index);
	}

	/**
	 * Returns the value of the given attribute.
	 *
	 * @param attribute the type of the attribute
	 * @return the text associated with attribute if the attribute is present. Empty
	 * string otherwise.
	 */
	public String getAttribute(Attribute attribute) {
		if (this.scoreAttr.containsKey(attribute)) {
			return this.scoreAttr.get(attribute);
		}

		return "";
	}

	/**
	 * Returns the durational notation object at the given position.
	 *
	 * @param position the position from which to get the element
	 * @return the notation object with duration at the given position
	 * @throws NoSuchElementException if the position is not found in this score
	 */
	public Durational getAtPosition(ScorePosition position) throws NoSuchElementException {
		final Part part = this.parts.get(position.getPartNumber());
		final Measure measure = part.getMeasure(position.getStaffNumber(), position.getMeasureNumber());
		Durational dur = measure.get(position.getVoiceNumber(), position.getIndexInVoice());

		if (position.isInChord()) {
			if (dur instanceof Chord) {
				final Chord chord = (Chord) dur;
				dur = chord.getNote(position.getIndexInChord());
			} else {
				throw new NoSuchElementException("The element at the position is not a Chord.");
			}
		}

		return dur;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Score ").append(getTitle()).append("\n");

		for (int i = 0; i < parts.size(); ++i) {
			strBuilder.append(parts.get(i).toString());
			strBuilder.append("\n\n");
		}

		return strBuilder.toString();
	}

	@Override
	public Iterator<Part> iterator() {
		return this.parts.iterator();
	}
}
