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
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;
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

	private static final Logger LOG = LoggerFactory.getLogger(StaxReader.class);

	private final boolean validateInput;
	private final Path path;
	private final Map<String, PartBuilder> partBuilders;

	private XMLStreamReader reader;
	private InputStream inputStream;
	private ScoreBuilder scoreBuilder;
	private Score score;

	private PartContext partContext;

	// Divisions are defined within a part but can still be shared between
	// multiple parts, so it's part of the context of the whole score.
	private int currentDivisions;
	private DurationalBuilder currentDurationalBuilder;

	private int beats;
	private int beatDivisions;
	private TimeSignature.Symbol timeSymbol;

	private Clef.Symbol clefSymbol;
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
		if (score == null) {
			score = readScoreBuilder().build();
		}

		return score;
	}

	@Override
	public ScoreBuilder readScoreBuilder() throws IOException, ParsingFailureException {
		if (scoreBuilder == null) {
			scoreBuilder = new ScoreBuilder();
			fillScoreBuilder();
		}

		return scoreBuilder;
	}

	private void fillScoreBuilder() throws IOException, ParsingFailureException {
		reader = createStreamReader(path);
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
					consumeText(text -> scoreBuilder.setAttribute(Score.Attribute.TITLE, text));
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
					consumeText(text -> scoreBuilder.setAttribute(Score.Attribute.COMPOSER, text));
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
		partContext = new PartContext(partBuilders.get(partId));

		consumeUntil(tag -> {
			if (Tags.MEASURE.equals(tag)) {
				consumeMeasureElem();
			}
		}, Tags.PART);

		scoreBuilder.addPart(partContext.getPartBuilder());
	}

	private void consumeOffsetElem(String element) throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.DURATION:
					if (element.equals(Tags.FORWARD)) {
						consumeText(text ->
								partContext.addForwardDuration(divisionsToDuration(text)));
					} else if (element.equals(Tags.BACKUP)) {
						consumeText(text ->
								partContext.addBackupDuration(divisionsToDuration(text)));
					}
					break;
				case Tags.STAFF:
					LOG.warn("Encountered element {} with staff number. This is not supported currently.", element);
					break;
				default:
					skipElement();
			}
		}, element);
	}

	private void consumeMeasureElem() throws XMLStreamException {
		partContext.setMeasureNumber(Integer.parseInt(reader.getAttributeValue(0)));

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.NOTE:
					consumeNoteElem();
					break;
				case Tags.BACKUP:
					consumeOffsetElem(Tags.BACKUP);
					break;
				case Tags.FORWARD:
					consumeOffsetElem(Tags.FORWARD);
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

		partContext.finishMeasureElement();
	}

	private Duration divisionsToDuration(String divisionsString) {
		final int divisions = Integer.parseInt(divisionsString);
		return Durations.QUARTER.divide(currentDivisions).multiply(divisions);
	}

	private void consumeNoteElem() throws XMLStreamException {
		currentDurationalBuilder = new NoteBuilder();
		partContext.setChordTag(false);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.DURATION:
					consumeText(text -> currentDurationalBuilder.setDuration(divisionsToDuration(text)));
					break;
				case Tags.PITCH:
					consumePitchElem();
					break;
				case Tags.VOICE:
					consumeText(text -> partContext.setVoice(Integer.parseInt(text)));
					break;
				case Tags.STAFF:
					consumeText(text -> partContext.setStaff(Integer.parseInt(text)));
					break;
				case Tags.REST:
					final Duration duration = currentDurationalBuilder.getDuration();
					currentDurationalBuilder = new RestBuilder();
					if (duration != null) {
						currentDurationalBuilder.setDuration(duration);
					}
					break;
				case Tags.CHORD:
					partContext.setChordTag(true);
					break;
				// Fall through for elements that are currently not supported
				case Tags.ACCIDENTAL:
				case Tags.BEAM:
				case Tags.CUE:
				case Tags.DOT:
				case Tags.GRACE:
				case Tags.INSTRUMENT:
				case Tags.LYRIC:
				case Tags.NOTATIONS:
				case Tags.NOTEHEAD_TEXT:
				case Tags.NOTEHEAD:
				case Tags.STEM:
				case Tags.TIME_MODIFICATION:
				case Tags.TYPE:
				case Tags.UNPITCHED:
				default:
					skipElement();

			}
		}, Tags.NOTE);

		if (currentDurationalBuilder instanceof NoteBuilder) {
			NoteBuilder currentNoteBuilder = (NoteBuilder) currentDurationalBuilder;
			currentNoteBuilder.setPitch(
					Pitch.of(Transforms.stepToPitchBase(currentStep), Transforms.alterToAccidental(currentAlter),
							currentOctave));

			partContext.updateChordBuffer(currentNoteBuilder);
		} else {
			partContext.updateChordBuffer(null);
			partContext.getMeasureBuilder().addToVoice(partContext.getVoice(), currentDurationalBuilder);
		}
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
							text -> partContext.getMeasureBuilder()
									.setKeySignature(Transforms.fifthsToKeySig(Integer.parseInt(text))));
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
		timeSymbol = Transforms.symbolStringToTimeSigSymbol(reader.getAttributeValue(null, Tags.SYMBOL));

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

		final Duration beatDuration = Duration.of(1, beatDivisions);
		partContext.getMeasureBuilder().setTimeSignature(TimeSignature.of(beats, beatDuration, timeSymbol));
	}

	private void consumeBarlineElem() throws XMLStreamException {
		final String barlineLocation = reader.getAttributeValue(null, Tags.LOCATION);
		// Empty the barstyle so that it either gets set to a value or the
		// default single style barline is used.
		barStyle = "";
		repeatDirection = "";

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
			partContext.getMeasureBuilder().setLeftBarline(barline);
		} else {
			partContext.getMeasureBuilder().setRightBarline(barline);
		}
	}

	private void consumeClefElem() throws XMLStreamException {
		clefLine = 0;
		final String clefStaffString = reader.getAttributeValue(null, Tags.NUMBER);
		final int clefStaff = clefStaffString == null ? 0 : Integer.parseInt(clefStaffString);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.SIGN:
					consumeText(text -> clefSymbol = Transforms.signToClefSymbol(text));
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

		// Zero indicates that clef line was not explicitly set so
		// default valuea is to be used.
		if (clefLine == 0) {
			clefLine = clefSymbol.getDefaultLine();
		}

		partContext.addClef(Clef.of(clefSymbol, clefLine), clefStaff);
	}

}
