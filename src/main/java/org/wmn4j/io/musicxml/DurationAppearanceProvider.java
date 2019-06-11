package org.wmn4j.io.musicxml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durations;

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
	 * @param duration the duration for which the appearance elements are returned
	 * @param document the document to which the elements are meant to be added
	 * @return the elements that are needed to define the appearance of the duration in music notation
	 */
	Collection<Element> getAppearanceElements(Duration duration, Document document) {

		final Collection<Element> elements = new ArrayList<>();
		if (basicDurationAppearances.containsKey(duration)) {
			final Element typeElement = document.createElement(MusicXmlTags.NOTE_DURATION_TYPE);
			typeElement.setTextContent(basicDurationAppearances.get(duration));
			elements.add(typeElement);
		}

		return elements;
	}
}
