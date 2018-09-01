/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import wmnlibnotation.iterators.ScorePosition;

/**
 * Class that describes a score. This class is immutable.
 * <code>ScoreBuilder</code> can be used for creating <code>Score</code>
 * objects.
 * 
 * @author Otso Björklund
 */
public class Score implements Iterable<Part> {

	/**
	 * Type for the different text attributes a score can have.
	 */
	public enum Attribute {
		NAME, COMPOSER, ARRANGER, YEAR
	}

	private final Map<Attribute, String> scoreAttr;
	private final List<Part> parts;

	/**
	 * @param attributes
	 *            The attributes of the score.
	 * @param parts
	 *            The parts in the score.
	 */
	public Score(Map<Attribute, String> attributes, List<Part> parts) {
		this.parts = Collections.unmodifiableList(new ArrayList<>(parts));
		this.scoreAttr = Collections.unmodifiableMap(new HashMap<>(attributes));

		if (this.parts == null)
			throw new NullPointerException("Cannot create score: staves was null");
	}

	/**
	 * @return Name of this <code>Score</code>.
	 */
	public String getName() {
		return this.getAttribute(Attribute.NAME);
	}

	/**
	 * @return number of parts in this <code>Score</code>.
	 */
	public int getPartCount() {
		return this.parts.size();
	}

	/**
	 * @return the parts in this <code>Score</code> as list in no particular order.
	 */
	public List<Part> getParts() {
		return this.parts;
	}

	/**
	 * @param index
	 *            the number of the <code>Part</code> in the <code>Score</code>.
	 * @return <code>Part</code> associated with index.
	 */
	public Part getPart(int index) {
		return this.parts.get(index);
	}

	/**
	 * @param attribute
	 *            the type of the attribute.
	 * @return the text associated with attribute if the attribute is set. Empty
	 *         string otherwise.
	 */
	public String getAttribute(Attribute attribute) {
		if (this.scoreAttr.containsKey(attribute))
			return this.scoreAttr.get(attribute);

		return "";
	}

	/**
	 * Get the <code>Durational</code> at position.
	 * 
	 * @param position
	 *            the <code>ScorePosition</code> from which to get the element.
	 * @return the <code>Durational</code> at position.
	 * @throws NoSuchElementException
	 *             if the position is not found.
	 */
	public Durational getAtPosition(ScorePosition position) throws NoSuchElementException {
		Part part = this.parts.get(position.getPartNumber());
		Measure measure = part.getMeasure(position.getStaffNumber(), position.getMeasureNumber());
		Durational dur = measure.get(position.getVoiceNumber(), position.getIndexInVoice());

		if (position.isInChord()) {
			if (dur instanceof Chord) {
				Chord chord = (Chord) dur;
				dur = chord.getNote(position.getIndexInChord());
			} else {
				throw new NoSuchElementException("The element at the position is not a Chord.");
			}
		}

		return dur;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("Score ").append(getName()).append("\n");

		for (int i = 0; i < parts.size(); ++i) {
			strBuilder.append(parts.get(i).toString());
			strBuilder.append("\n\n");
		}

		return strBuilder.toString();
	}

	/**
	 * @return iterator that does not support modifying the <code>Score</code>.
	 */
	@Override
	public Iterator<Part> iterator() {
		return this.parts.iterator();
	}
}
