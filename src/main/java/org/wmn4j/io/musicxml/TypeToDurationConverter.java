/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durations;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

enum TypeToDurationConverter {
	INSTANCE;

	private final Map<String, Duration> noteTypeDurations;

	TypeToDurationConverter() {
		noteTypeDurations = createBasicDurationAppearances();
	}

	private Map<String, Duration> createBasicDurationAppearances() {
		Map<String, Duration> types = new HashMap<>();
		types.put(MusicXmlTags.NOTE_TYPE_1024TH, Duration.of(1, 1024));
		types.put(MusicXmlTags.NOTE_TYPE_512TH, Duration.of(1, 512));
		types.put(MusicXmlTags.NOTE_TYPE_256TH, Duration.of(1, 256));
		types.put(MusicXmlTags.NOTE_TYPE_128TH, Duration.of(1, 128));
		types.put(MusicXmlTags.NOTE_TYPE_64TH, Duration.of(1, 64));
		types.put(MusicXmlTags.NOTE_TYPE_32TH, Duration.of(1, 32));
		types.put(MusicXmlTags.NOTE_TYPE_16TH, Durations.SIXTEENTH);
		types.put(MusicXmlTags.NOTE_TYPE_EIGHTH, Durations.EIGHTH);
		types.put(MusicXmlTags.NOTE_TYPE_QUARTER, Durations.QUARTER);
		types.put(MusicXmlTags.NOTE_TYPE_HALF, Durations.HALF);
		types.put(MusicXmlTags.NOTE_TYPE_WHOLE, Durations.WHOLE);
		types.put(MusicXmlTags.NOTE_TYPE_BREVE, Duration.of(2, 1));
		types.put(MusicXmlTags.NOTE_TYPE_LONG, Duration.of(4, 1));
		types.put(MusicXmlTags.NOTE_TYPE_MAXIMA, Duration.of(8, 1));

		return Collections.unmodifiableMap(types);
	}

	/**
	 * Returns the duration corresponding to the note type string. Defaults to null if no such type exists.
	 *
	 * @param typeString MusicXML note type string
	 * @return the duration corresponding to the note type string or null if no such type exists
	 */
	Duration getDuration(String typeString) {
		return noteTypeDurations.getOrDefault(typeString, null);
	}
}
