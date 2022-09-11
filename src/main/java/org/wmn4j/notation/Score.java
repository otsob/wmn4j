/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.wmn4j.notation.access.Position;
import org.wmn4j.notation.access.PositionIterator;
import org.wmn4j.notation.access.Selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * Represents a score.
 * <p>
 * This class is immutable.
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
		 * The title of the movement.
		 */
		MOVEMENT_TITLE,

		/**
		 * The subtitle of the score.
		 */
		SUBTITLE,

		/**
		 * The composer name.
		 */
		COMPOSER,

		/**
		 * The arranger.
		 */
		ARRANGER
	}

	private final Map<Attribute, String> scoreAttr;
	private final List<Part> parts;

	private final int fullMeasureCount;
	private final int measureCount;

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

		this.measureCount = this.parts.stream().mapToInt(part -> part.getMeasureCount()).max().getAsInt();
		this.fullMeasureCount = this.parts.stream().mapToInt(part -> part.getFullMeasureCount()).max().getAsInt();
	}

	/**
	 * Returns the number of full measures in this score.
	 * <p>
	 * Pickup measures are not counted, that is, the number of measures
	 * is the same as the largest measure number in the score.
	 *
	 * @return the number of full measures in this score
	 */
	public int getFullMeasureCount() {
		return fullMeasureCount;
	}

	/**
	 * Returns the number of measures in this score.
	 * <p>
	 * Pickup measures are included in the counte, that is, the number of measures
	 * is the same as the largest measure number in the score plus a possible pickup
	 * measure.
	 *
	 * @return the number of full measures in this score
	 */
	public int getMeasureCount() {
		return measureCount;
	}

	/**
	 * Returns true if this score has a pickup measure.
	 *
	 * @return true if this score has a pickup measure, false otherwise
	 */
	public boolean hasPickupMeasure() {
		return fullMeasureCount < measureCount;
	}

	/**
	 * Returns the main title of this score.
	 * If no title is set, then returns empty.
	 *
	 * @return the main title of this score
	 */
	public Optional<String> getTitle() {
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
	 * Returns the part at the index.
	 *
	 * @param index the number of the part in this score
	 * @return the part at the index
	 */
	public Part getPart(int index) {
		return this.parts.get(index);
	}

	/**
	 * Returns true if the given attribute is set in this score.
	 *
	 * @param attribute the attribute whose presence is checked
	 * @return true if the given attribute is set in this score
	 */
	public boolean hasAttribute(Attribute attribute) {
		return !scoreAttr.getOrDefault(attribute, "").isEmpty();
	}

	/**
	 * Returns the value of the given attribute.
	 * If the attribute is not present, then returns empty.
	 *
	 * @param attribute the type of the attribute
	 * @return the value of the attribute if the attribute is present
	 */
	public Optional<String> getAttribute(Attribute attribute) {
		return Optional.ofNullable(scoreAttr.getOrDefault(attribute, null));
	}

	/**
	 * Returns the durational notation object at the given position.
	 *
	 * @param position the position from which to get the element
	 * @return the notation object with duration at the given position
	 * @throws NoSuchElementException if the position is not found in this score
	 */
	public Durational getAt(Position position) throws NoSuchElementException {
		final Part part = this.parts.get(position.getPartIndex());
		final Measure measure = part.getMeasure(position.getStaffNumber(), position.getMeasureNumber());
		Durational dur = measure.get(position.getVoiceNumber(), position.getIndexInVoice());

		if (position.isInChord()) {
			if (dur.isChord()) {
				final Chord chord = dur.toChord();
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
		strBuilder.append("Score ").append(getTitle().orElse("")).append("\n");

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

	/**
	 * Returns an iterator that iterates the durational notation objects in
	 * partwise order.
	 * <p>
	 * Starts by iterating
	 * through the part with the smallest number. Iterates through parts starting
	 * from smallest measure number. Iterates through measure voice by voice.
	 *
	 * @return an iterator that iterates the durational notation objects in
	 * partwise order
	 */
	public PositionIterator partwiseIterator() {
		return new PartwisePositionIterator(this, hasPickupMeasure() ? 0 : 1, getFullMeasureCount());
	}

	private int getFirstMeasureNumber() {
		return hasPickupMeasure() ? 0 : 1;
	}

	/**
	 * Returns this score as a {@link Selection}.
	 *
	 * @return this score as a {@link Selection}
	 */
	public Selection toSelection() {
		return new SelectionImpl(this, getFirstMeasureNumber(), getFullMeasureCount());
	}

	/**
	 * Returns a range of measures of all parts in this score.
	 *
	 * @param firstMeasure the number of the first measure included in the range
	 * @param lastMeasure  the number of the last measure included in the range
	 * @return a range of measures of all parts in this score
	 */
	public Selection selectRange(int firstMeasure, int lastMeasure) {
		return new SelectionImpl(this, firstMeasure, lastMeasure);
	}

	/**
	 * Returns a selection of the score containing the parts at the given
	 * indices.
	 *
	 * @param partIndices the indices of the parts in this Score to be included in the selection
	 * @return a selection of the score containing the parts at the given
	 * * indices
	 */
	public Selection selectParts(Collection<Integer> partIndices) {
		return new SelectionImpl(this, getFirstMeasureNumber(), getFullMeasureCount(), partIndices);
	}
}
