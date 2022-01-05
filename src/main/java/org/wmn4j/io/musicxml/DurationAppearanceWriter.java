/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durations;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides a mapping from {@link Duration} to MusicXML note duration appearance elements, such as type and dot
 * and writes them using a given writer.
 */
enum DurationAppearanceWriter {

	INSTANCE;

	private final Map<Duration, String> basicDurationAppearances;

	DurationAppearanceWriter() {
		basicDurationAppearances = createBasicDurationAppearances();
	}

	private Map<Duration, String> createBasicDurationAppearances() {
		Map<Duration, String> appearences = new HashMap<>();
		appearences.put(Duration.of(1, 1024), Tags.NOTE_1024TH);
		appearences.put(Duration.of(1, 512), Tags.NOTE_512TH);
		appearences.put(Duration.of(1, 256), Tags.NOTE_256TH);
		appearences.put(Duration.of(1, 128), Tags.NOTE_128TH);
		appearences.put(Duration.of(1, 64), Tags.NOTE_64TH);
		appearences.put(Duration.of(1, 32), Tags.NOTE_32TH);
		appearences.put(Durations.SIXTEENTH, Tags.NOTE_16TH);
		appearences.put(Durations.EIGHTH, Tags.NOTE_EIGHTH);
		appearences.put(Durations.QUARTER, Tags.NOTE_QUARTER);
		appearences.put(Durations.HALF, Tags.NOTE_HALF);
		appearences.put(Durations.WHOLE, Tags.NOTE_WHOLE);
		appearences.put(Duration.of(2, 1), Tags.NOTE_BREVE);
		appearences.put(Duration.of(4, 1), Tags.NOTE_LONG);
		appearences.put(Duration.of(8, 1), Tags.NOTE_MAXIMA);

		return Collections.unmodifiableMap(appearences);
	}

	void writeAppearanceElements(Duration duration, XMLStreamWriter writer) throws XMLStreamException {
		final int dotCount = duration.getDotCount();
		final int tupletDivisor = duration.getTupletDivisor();
		final Duration basicDurationType = duration.removeDots().multiply(tupletDivisor);

		if (tupletDivisor == 1) {
			writeDurationType(writer, basicDurationType);
		} else {
			writeTupletElementsIfNeeded(duration, writer);
		}

		// Add dots
		for (int i = 0; i < dotCount; ++i) {
			StaxScoreWriter.writeValue(writer, Tags.DOT, "");
		}
	}

	private void writeTupletElementsIfNeeded(Duration duration, XMLStreamWriter writer) throws XMLStreamException {

		final int denominator = duration.getDenominator();
		int tupletNotesThatFitInTheDividedDuration = duration.getTupletDivisor();
		Duration showTypeDuration = getShowableDurationTypeForTuplet(denominator);

		if (basicDurationAppearances.containsKey(showTypeDuration)) {
			writeDurationType(writer, showTypeDuration);

			final Duration durationThatIsSplitByTuplet = duration.multiply(tupletNotesThatFitInTheDividedDuration);

			final int normalNotesThatWouldFitInTheSplitDuration =
					(durationThatIsSplitByTuplet.getNumerator() * showTypeDuration.getDenominator())
							/ durationThatIsSplitByTuplet.getDenominator();

			writer.writeStartElement(Tags.TIME_MODIFICATION);

			StaxScoreWriter.writeValue(writer, Tags.ACTUAL_NOTES,
					Integer.toString(tupletNotesThatFitInTheDividedDuration));

			StaxScoreWriter.writeValue(writer, Tags.NORMAL_NOTES,
					Integer.toString(normalNotesThatWouldFitInTheSplitDuration));

			writer.writeEndElement();
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

	private void writeDurationType(XMLStreamWriter writer, Duration duration) throws XMLStreamException {
		String type = basicDurationAppearances.get(duration);
		if (type != null && !type.isBlank()) {
			StaxScoreWriter.writeValue(writer, Tags.TYPE, type);
		}
	}
}
