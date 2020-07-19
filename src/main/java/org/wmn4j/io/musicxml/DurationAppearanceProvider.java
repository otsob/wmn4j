/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a mapping from {@link Duration} to MusicXML note duration appearance elements, such as type and dot.
 */
enum DurationAppearanceProvider {

	INSTANCE;

	private final Map<Duration, String> basicDurationAppearances;

	DurationAppearanceProvider() {
		basicDurationAppearances = createBasicDurationAppearances();
	}

	private Map<Duration, String> createBasicDurationAppearances() {
		Map<Duration, String> appearences = new HashMap<>();
		appearences.put(Duration.of(1, 1024), MusicXmlTags.NOTE_TYPE_1024TH);
		appearences.put(Duration.of(1, 512), MusicXmlTags.NOTE_TYPE_512TH);
		appearences.put(Duration.of(1, 256), MusicXmlTags.NOTE_TYPE_256TH);
		appearences.put(Duration.of(1, 128), MusicXmlTags.NOTE_TYPE_128TH);
		appearences.put(Duration.of(1, 64), MusicXmlTags.NOTE_TYPE_64TH);
		appearences.put(Duration.of(1, 32), MusicXmlTags.NOTE_TYPE_32TH);
		appearences.put(Durations.SIXTEENTH, MusicXmlTags.NOTE_TYPE_16TH);
		appearences.put(Durations.EIGHTH, MusicXmlTags.NOTE_TYPE_EIGHTH);
		appearences.put(Durations.QUARTER, MusicXmlTags.NOTE_TYPE_QUARTER);
		appearences.put(Durations.HALF, MusicXmlTags.NOTE_TYPE_HALF);
		appearences.put(Durations.WHOLE, MusicXmlTags.NOTE_TYPE_WHOLE);
		appearences.put(Duration.of(2, 1), MusicXmlTags.NOTE_TYPE_BREVE);
		appearences.put(Duration.of(4, 1), MusicXmlTags.NOTE_TYPE_LONG);
		appearences.put(Duration.of(8, 1), MusicXmlTags.NOTE_TYPE_MAXIMA);

		return Collections.unmodifiableMap(appearences);
	}

	/**
	 * Returns the elements that are needed to define the appearance of the duration in music notation.
	 *
	 * @param duration the expressible duration for which the appearance elements are returned
	 * @param document the document to which the elements are meant to be added
	 * @return the elements that are needed to define the appearance of the duration in music notation
	 */
	Collection<Element> getAppearanceElements(Duration duration, Document document) {

		final Collection<Element> elements = new ArrayList<>();
		final int dotCount = duration.getDotCount();
		final int tupletDivisor = duration.getTupletDivisor();
		final Duration basicDurationType = duration.removeDots().multiply(tupletDivisor);

		if (tupletDivisor == 1) {
			elements.add(getBasicDurationType(basicDurationType, document));
		} else {
			addTupletElementsIfNeeded(elements, duration, document);
		}

		// Add dots
		for (int i = 0; i < dotCount; ++i) {
			elements.add(document.createElement(MusicXmlTags.DOT));
		}

		return elements;
	}

	private void addTupletElementsIfNeeded(Collection<Element> elements, Duration duration, Document document) {

		final int denominator = duration.getDenominator();
		int tupletNotesThatFitInTheDividedDuration = duration.getTupletDivisor();
		Duration showTypeDuration = getShowableDurationTypeForTuplet(denominator);

		if (basicDurationAppearances.containsKey(showTypeDuration)) {
			elements.add(getBasicDurationType(showTypeDuration, document));

			final Duration durationThatIsSplitByTuplet = duration.multiply(tupletNotesThatFitInTheDividedDuration);

			final int normalNotesThatWouldFitInTheSplitDuration =
					(durationThatIsSplitByTuplet.getNumerator() * showTypeDuration.getDenominator())
							/ durationThatIsSplitByTuplet.getDenominator();

			final Element timeModificationElement = document.createElement(MusicXmlTags.TIME_MODIFICATION);

			final Element actualNotesElement = document.createElement(MusicXmlTags.TIME_MODIFICATION_ACTUAL_NOTES);
			actualNotesElement.setTextContent(Integer.toString(tupletNotesThatFitInTheDividedDuration));
			timeModificationElement.appendChild(actualNotesElement);

			final Element normalNotesElement = document.createElement(MusicXmlTags.TIME_MODIFICATION_NORMAL_NOTES);
			normalNotesElement.setTextContent(Integer.toString(normalNotesThatWouldFitInTheSplitDuration));
			timeModificationElement.appendChild(normalNotesElement);

			elements.add(timeModificationElement);
		}
	}

	private Duration getShowableDurationTypeForTuplet(int denominator) {

		// The symbol used for a tuplet is typically the basic duration (quarter, eighth, etc.) that is the shortest
		// basic duration that is longer than the tuplet duration. This can be found by finding the greatest power
		// of two that is smaller than the denominator of the tuplet duration.
		int greatestPowerOfTwoUnderDenominator = 1;

		while (greatestPowerOfTwoUnderDenominator < denominator) {
			greatestPowerOfTwoUnderDenominator *= 2;
		}

		greatestPowerOfTwoUnderDenominator /= 2;

		return Duration.of(1, greatestPowerOfTwoUnderDenominator);
	}

	private Element getBasicDurationType(Duration duration, Document document) {
		final Element typeElement = document.createElement(MusicXmlTags.NOTE_DURATION_TYPE);
		typeElement.setTextContent(basicDurationAppearances.get(duration));
		return typeElement;
	}

}
