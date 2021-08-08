/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.TimeSignature;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Implements a MusicXML reader using the Stax Cursor API.
 */
final class StaxReader implements MusicXmlReader {

	private static final String PARSING_FAILURE = "Failed to parse XML: ";

	private static final int MIN_STAFF_NUMBER = SingleStaffPart.STAFF_NUMBER;
	private static final int DEFAULT_STAFF_COUNT = 1;

	private static final Logger LOG = LoggerFactory.getLogger(StaxReader.class);

	private final boolean validateInput;
	private final Path path;
	private final Map<String, PartBuilder> partBuilders;

	private XMLStreamReader reader;
	private InputStream inputStream;
	private ScoreBuilder scoreBuilder;
	private Score score;

	private int currentDivisions;

	private PartBuilder currentPartBuilder;
	private MeasureBuilder currentMeasureBuilder;
	private DurationalBuilder currentDurationalBuilder;

	private int currentStaff;
	private int currentVoice;

	private int beats;
	private int beatDivisions;

	private String clefSign;
	private int clefLine;

	private String barStyle;
	private String repeatDirection;

	private String currentStep;
	private int currentAlter;
	private int currentOctave;

	StaxReader(Path path, boolean validate) {
		this.path = Objects.requireNonNull(path);
		this.validateInput = validate;
		this.partBuilders = new HashMap<>();
	}

	@Override
	public Score readScore() throws IOException, ParsingFailureException {
		if (this.score == null) {
			this.score = readScoreBuilder().build();
		}

		return score;
	}

	@Override
	public ScoreBuilder readScoreBuilder() throws IOException, ParsingFailureException {
		if (scoreBuilder == null) {
			this.scoreBuilder = new ScoreBuilder();
			fillScoreBuilder();
		}

		return this.scoreBuilder;
	}

	private void fillScoreBuilder() throws IOException, ParsingFailureException {
		this.reader = createStreamReader(this.path);
		try {
			consumeUntil(tag -> {
				switch (tag) {
					case Tags.WORK:
						consumeWorkElem();
						break;
					case Tags.IDENTIFICATION:
						consumeIdentificationElem();
						break;
					case Tags.MOVEMENT_TITLE:
						consumeMovementTitleElem();
						break;
					case Tags.MOVEMENT_NUMBER:
						consumeMovementNumberElem();
						break;
					case Tags.PART_LIST:
						consumePartListElem();
						break;
					case Tags.PART:
						consumePartElem();
						break;
					case Tags.SCORE_PARTWISE:
						// Do nothing
						break;
					default:
						skipElement();
				}
			}, Tags.SCORE_PARTWISE);

			// Ensure the reader is at the end of document and close the reader.
			while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				reader.next();
			}
			reader.close();

		} catch (XMLStreamException e) {
			throw new ParsingFailureException(PARSING_FAILURE + e.getMessage());
		}

		// Close the input stream.
		inputStream.close();
	}

	private XMLStreamReader createStreamReader(Path path) throws IOException, ParsingFailureException {
		final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();

		try {
			inputStream = new FileInputStream(path.toFile());
			return xmlInputFactory.createXMLStreamReader(inputStream);
		} catch (XMLStreamException e) {
			throw new ParsingFailureException(PARSING_FAILURE + e.getMessage());
		} catch (FileNotFoundException e) {
			throw new IOException("File " + path + " not found");
		}
	}

	private void consumeText(Consumer<String> consumer) throws XMLStreamException {
		if (reader.next() == XMLStreamConstants.CHARACTERS) {
			final String text = reader.getText();
			if (text != null && !text.isBlank()) {
				consumer.accept(text);
			}
		}
	}

	private void skipElement() throws XMLStreamException {
		String tag = reader.getLocalName();
		int event = reader.getEventType();
		while (event != XMLStreamConstants.END_ELEMENT || !Objects.equals(tag, reader.getLocalName())) {
			event = reader.next();
		}
	}

	private boolean isEndTag(int event, String tag) {
		return event == XMLStreamConstants.END_ELEMENT && Objects.equals(tag, reader.getLocalName());
	}

	@FunctionalInterface
	interface ElementConsumer {
		/**
		 * Consumes an internal element of a containing element and stops the cursor
		 * at the end tag of the consumed internal element.
		 *
		 * @param tag the tag of the element to be consumed
		 * @throws XMLStreamException
		 */
		void consume(String tag) throws XMLStreamException;
	}

	/**
	 * Consumes elements until the end tag and applies consumer to all start elements.
	 *
	 * @param consumer the consumer that is applied to all starting elements
	 * @param endTag   the ending tag that stops the consumption
	 * @throws XMLStreamException
	 */
	private void consumeUntil(ElementConsumer consumer, String endTag) throws XMLStreamException {
		while (reader.hasNext()) {
			final int event = reader.next();

			if (event == XMLStreamConstants.START_ELEMENT) {
				consumer.consume(reader.getLocalName());
			}

			if (isEndTag(event, endTag)) {
				return;
			}
		}
	}

	private void consumeWorkElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.WORK_TITLE:
					consumeText(text -> this.scoreBuilder.setAttribute(Score.Attribute.TITLE, text));
					break;
				// Fall through for elements that are currently not supported
				case Tags.WORK_NUMBER:
				case Tags.OPUS:
				default:
					skipElement();
					break;
			}
		}, Tags.WORK);
	}

	private void consumeMovementTitleElem() throws XMLStreamException {
		consumeText(text -> scoreBuilder.setAttribute(Score.Attribute.MOVEMENT_TITLE, text));
	}

	private void consumeMovementNumberElem() throws XMLStreamException {
		// Not currently supported
		skipElement();
	}

	private void consumeIdentificationElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.CREATOR:
					consumeText(text -> this.scoreBuilder.setAttribute(Score.Attribute.COMPOSER, text));
					break;
				// Fall through for elements that are currently not supported
				case Tags.RIGHTS:
				case Tags.ENCODING:
				case Tags.SOURCE:
				case Tags.RELATION:
				case Tags.MISCELLANEOUS:
				default:
					skipElement();
			}
		}, Tags.IDENTIFICATION);
	}

	private void consumePartListElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.SCORE_PART:
					consumePartInfoElem();
					break;
				// Fall through for elements that are currently not supported
				case Tags.PART_GROUP:
				default:
					skipElement();
			}
		}, Tags.PART_LIST);

	}

	private void consumePartInfoElem() throws XMLStreamException {
		final String partId = reader.getAttributeValue(0);
		final PartBuilder partBuilder = new PartBuilder();
		partBuilders.put(partId, partBuilder);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.PART_NAME:
					consumeText(text -> partBuilder.setAttribute(Part.Attribute.NAME, text));
					break;
				case Tags.PART_ABBREVIATION:
					consumeText(text -> partBuilder.setAttribute(Part.Attribute.ABBREVIATED_NAME, text));
					break;
				// Fall through for elements that are currently not supported
				case Tags.IDENTIFICATION:
				case Tags.PART_LINK:
				case Tags.PART_NAME_DISPLAY:
				case Tags.PART_ABBREVIATION_DISPLAY:
				case Tags.GROUP:
				default:
					skipElement();
			}
		}, Tags.SCORE_PART);
	}

	private void consumePartElem() throws XMLStreamException {
		final String partId = reader.getAttributeValue(0);
		this.currentPartBuilder = this.partBuilders.get(partId);

		consumeUntil(tag -> {
			if (Tags.MEASURE.equals(tag)) {
				consumeMeasureElem();
			}
		}, Tags.PART);

		this.scoreBuilder.addPart(this.currentPartBuilder);
	}

	private void consumeMeasureElem() throws XMLStreamException {
		this.currentMeasureBuilder = new MeasureBuilder();
		final int measureNumber = Integer.parseInt(reader.getAttributeValue(0));
		this.currentMeasureBuilder.setNumber(measureNumber);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.NOTE:
					consumeNoteElem();
					break;
				case Tags.BACKUP:
					break;
				case Tags.FORWARD:
					break;
				case Tags.DIRECTION:
					break;
				case Tags.ATTRIBUTES:
					consumeAttributesElem();
					break;
				case Tags.BARLINE:
					consumeBarlineElem();
					break;
				// Fall through for elements that are currently not supported
				case Tags.HARMONY:
				case Tags.FIGURED_BASS:
				default:
					skipElement();
			}
		}, Tags.MEASURE);

		this.currentPartBuilder.add(this.currentMeasureBuilder);
	}

	private void consumeNoteElem() throws XMLStreamException {
		currentDurationalBuilder = new NoteBuilder();

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.DURATION:
					consumeText(text -> {
						final int divisions = Integer.parseInt(text);
						final Duration duration = Durations.QUARTER.divide(currentDivisions).multiply(divisions);
						currentDurationalBuilder.setDuration(duration);
					});
					break;
				case Tags.PITCH:
					consumePitchElem();
					break;
				case Tags.VOICE:
					consumeText(text -> currentVoice = Integer.parseInt(text));
					break;
				case Tags.STAFF:
					break;
				// Fall through for elements that are currently not supported
				case Tags.ACCIDENTAL:
				case Tags.BEAM:
				case Tags.CHORD:
				case Tags.CUE:
				case Tags.DOT:
				case Tags.GRACE:
				case Tags.INSTRUMENT:
				case Tags.LYRIC:
				case Tags.NOTATIONS:
				case Tags.NOTEHEAD_TEXT:
				case Tags.NOTEHEAD:
				case Tags.REST:
				case Tags.STEM:
				case Tags.TIME_MODIFICATION:
				case Tags.TYPE:
				case Tags.UNPITCHED:
				default:
					skipElement();

			}
		}, Tags.NOTE);

		if (currentDurationalBuilder instanceof NoteBuilder) {
			((NoteBuilder) currentDurationalBuilder).setPitch(
					Pitch.of(Transforms.stepToPitchBase(currentStep), Transforms.alterToAccidental(currentAlter),
							currentOctave));
		}
		currentMeasureBuilder.addToVoice(currentVoice, currentDurationalBuilder);
	}

	private void consumePitchElem() throws XMLStreamException {
		// Alter is optional, so it needs to be set to zero to account for the absence of the element.
		currentAlter = 0;

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.STEP:
					consumeText(text -> currentStep = text);
					break;
				case Tags.ALTER:
					consumeText(text -> currentAlter = Integer.parseInt(text));
				case Tags.OCTAVE:
					consumeText(text -> currentOctave = Integer.parseInt(text));
			}
		}, Tags.PITCH);
	}

	private void consumeAttributesElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.DIVISIONS:
					consumeText(text -> currentDivisions = Integer.parseInt(text));
					break;
				case Tags.KEY:
					consumeKeyElem();
					break;
				case Tags.TIME:
					consumeTimeElem();
					break;
				case Tags.STAVES:
					break;
				case Tags.CLEF:
					consumeClefElem();
					break;
				// Fall through for elements that are currently not supported
				case Tags.PART_SYMBOL:
				case Tags.INSTRUMENTS:
				case Tags.STAFF_DETAILS:
				case Tags.TRANSPOSE:
				case Tags.FOR_PART:
				case Tags.DIRECTIVE:
				case Tags.MEASURE_STYLE:
				default:
					skipElement();
			}

		}, Tags.ATTRIBUTES);
	}

	private void consumeKeyElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.FIFTHS:
					consumeText(
							text -> currentMeasureBuilder.setKeySignature(
									Transforms.fifthsToKeySig(Integer.parseInt(text))));
					break;
				// Fall through for elements that are currently not supported
				case Tags.MODE:
				case Tags.CANCEL:
				case Tags.KEY_STEP:
				case Tags.KEY_ALTER:
				case Tags.KEY_ACCIDENTAL:
				case Tags.KEY_OCTAVE:
				default:
					skipElement();
			}

		}, Tags.KEY);
	}

	private void consumeTimeElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.BEATS:
					consumeText(text -> beats = Integer.parseInt(text));
					break;
				case Tags.BEAT_TYPE:
					consumeText(text -> beatDivisions = Integer.parseInt(text));
					break;
				// Fall through for elements that are currently not supported
				case Tags.INTERCHANGEABLE:
				case Tags.SENZA_MISURA:
				default:
					skipElement();
			}
		}, Tags.TIME);

		currentMeasureBuilder.setTimeSignature(TimeSignature.of(beats, beatDivisions));
	}

	private void consumeBarlineElem() throws XMLStreamException {
		final String barlineLocation = reader.getAttributeValue(null, Tags.LOCATION);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.BAR_STYLE:
					consumeText(text -> barStyle = text);
					break;
				case Tags.REPEAT:
					repeatDirection = reader.getAttributeValue(null, Tags.DIRECTION);
					break;
				// Fall through for elements that are currently not supported
				case Tags.WAVY_LINE:
				case Tags.SEGNO:
				case Tags.CODA:
				case Tags.FERMATA:
				case Tags.ENDING:
				default:
					skipElement();
			}

		}, Tags.BARLINE);

		Barline barline = Transforms.barStyleToBarline(barStyle, repeatDirection);
		if (Objects.equals(barlineLocation, Tags.LEFT)) {
			currentMeasureBuilder.setLeftBarline(barline);
		} else {
			currentMeasureBuilder.setRightBarline(barline);
		}
	}

	private void consumeClefElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.SIGN:
					consumeText(text -> clefSign = text);
					break;
				case Tags.LINE:
					consumeText(text -> clefLine = Integer.parseInt(text));
					break;
				// Fall through for elements that are currently not supported
				case Tags.CLEF_OCTAVE_CHANGE:
				default:
					skipElement();
			}

		}, Tags.CLEF);

		currentMeasureBuilder.setClef(Clef.of(Transforms.signToClefSymbol(clefSign), clefLine));
	}

}
