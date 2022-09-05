/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.apache.commons.math3.fraction.Fraction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.ChordSymbol;
import org.wmn4j.notation.ChordSymbolBuilder;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.ConnectableBuilder;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.GraceNoteBuilder;
import org.wmn4j.notation.Lyric;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Ornament;
import org.wmn4j.notation.Ornamental;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.PitchName;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;
import org.wmn4j.notation.TimeSignature;
import org.wmn4j.notation.directions.Direction;
import org.wmn4j.notation.techniques.Technique;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.stax.StAXSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.zip.ZipFile;

/**
 * Implements a MusicXML reader using the Stax Cursor API.
 */
final class StaxReader implements MusicXmlReader {

	private static final String MUSICXML_V4_0_SCHEMA_PATH = "org/wmn4j/io/musicxml/musicxml.xsd";
	private static final Logger LOG = LoggerFactory.getLogger(StaxReader.class);
	private static final String COMPRESSED_EXTENSION = "mxl";
	private static final Set<String> VALID_EXTENSIONS = Set.of("xml", "musicxml", COMPRESSED_EXTENSION);

	private final boolean validateInput;
	private final Path path;
	private final Map<String, PartBuilder> partBuilders;

	private XMLStreamReader reader;
	private InputStream inputStream;
	private ZipFile compressedFile;
	private ScoreBuilder scoreBuilder;
	private Score score;

	private PartContext partContext;

	// Divisions are defined within a part but can still be shared between
	// multiple parts, so it's part of the context of the whole score.
	private int currentDivisions;
	private DurationalBuilder currentDurationalBuilder;
	private ConnectableBuilder currentConnectableBuilder;
	private int currentDurDivisions;
	private int currentDotCount;
	private int currentTupletDivisor;

	private String mxlMainFilePath;

	private int beats;
	private int beatDivisions;
	private TimeSignature.Symbol timeSymbol;

	private Clef.Symbol clefSymbol;
	private int clefLine;

	private String barStyle;
	private String repeatDirection;

	private String currentStep;
	private int currentAlter;
	private boolean isCurrentUnpitched;
	private int currentOctave;

	private Pitch currentPitch;

	private Technique currentTechnique;
	private String currentTechniqueText;
	private int currentChordExtensionValue;
	private String currentChordExtensionType;
	private Map<Technique.AdditionalValue, Object> currentAdditionalTechValues;

	private boolean isClosed;

	private final NotationReadResolver notationResolver = new NotationReadResolver();

	StaxReader(Path path, boolean validate) {
		this.path = Objects.requireNonNull(path);
		this.validateInput = validate;
		this.partBuilders = new HashMap<>();
		this.isClosed = false;
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
			try {
				fillScoreBuilder();
			} catch (SAXException e) {
				throw new ParsingFailureException(getParsingFailureMessage(e.getMessage()));
			}
		}

		return scoreBuilder;
	}

	@Override
	public void close() throws IOException {
		if (!isClosed) {
			isClosed = true;

			if (compressedFile != null) {
				compressedFile.close();
			}

			try {
				if (reader != null) {
					reader.close();
				}
			} catch (XMLStreamException e) {
				throw new IOException("Failed to close reader");
			}

			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	private void fillScoreBuilder() throws IOException, ParsingFailureException, SAXException {
		if (validateInput) {
			validate();
		}
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

			// Ensure the reader is at the end of document.
			while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
				reader.next();
			}
		} catch (XMLStreamException e) {
			throw new ParsingFailureException(getParsingFailureMessage(e.getMessage()));
		}

		// Close all IO resources
		close();
	}

	private void validate() throws IOException, ParsingFailureException {
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		ClassLoader classLoader = getClass().getClassLoader();
		try {
			Schema schema = factory.newSchema(classLoader.getResource(MUSICXML_V4_0_SCHEMA_PATH));
			Validator validator = schema.newValidator();
			final XMLStreamReader validationReader = createStreamReader(path);
			validator.validate(new StAXSource(validationReader));
			validationReader.close();
		} catch (SAXException | XMLStreamException e) {
			throw new ParsingFailureException(getParsingFailureMessage(e.getMessage()));
		}

		inputStream.close();
	}

	private boolean isCompressed(Path path, String extension) throws IOException {
		return Objects.equals(Files.probeContentType(path), CompressedMxl.COMPRESSED_CONTENT_TYPE) || Objects.equals(
				COMPRESSED_EXTENSION, extension);
	}

	private String getExtension(Path path) {
		final String[] split = path.toString().split("\\.");
		return split[split.length - 1];
	}

	private XMLStreamReader createStreamReader(Path path) throws IOException, ParsingFailureException {
		final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		final String extension = getExtension(path);
		if (validateInput && !VALID_EXTENSIONS.contains(extension)) {
			throw new ParsingFailureException(
					"Not a valid file extension for MusicXML, must be one of " + VALID_EXTENSIONS);
		}

		try {
			if (isCompressed(path, extension)) {
				inputStream = getStreamToZipEntry(path);
			} else {
				inputStream = new FileInputStream(path.toFile());
			}

			return xmlInputFactory.createXMLStreamReader(new BufferedInputStream(inputStream));
		} catch (XMLStreamException e) {
			throw new ParsingFailureException(getParsingFailureMessage(e.getMessage()));
		} catch (FileNotFoundException e) {
			throw new IOException("File " + path + " not found");
		}
	}

	private void findMainMusicXmlFile(ZipFile zipFile) throws IOException, XMLStreamException {
		inputStream = zipFile.getInputStream(zipFile.getEntry(CompressedMxl.META_INF_PATH));
		final XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		reader = xmlInputFactory.createXMLStreamReader(new BufferedInputStream(inputStream));

		while (reader.getEventType() != XMLStreamConstants.END_DOCUMENT) {
			consumeUntil(tag -> {
				final String path = reader.getAttributeValue(null, CompressedMxl.FULL_PATH_ATTR);
				final String mediaType = reader.getAttributeValue(null, CompressedMxl.MEDIA_TYPE_ATTR);

				if (mediaType == null || CompressedMxl.UNCOMPRESSED_CONTENT_TYPE.equals(mediaType)) {
					mxlMainFilePath = path;
				}

			}, CompressedMxl.ROOTFILE_TAG);
		}

		reader.close();
		reader = null;

		inputStream.close();
		inputStream = null;
	}

	private InputStream getStreamToZipEntry(Path path) throws ParsingFailureException, IOException, XMLStreamException {
		compressedFile = new ZipFile(path.toString());
		findMainMusicXmlFile(compressedFile);
		var entries = compressedFile.entries();
		while (entries.hasMoreElements()) {
			final var entry = entries.nextElement();
			if (entry.getName().equals(mxlMainFilePath)) {
				return compressedFile.getInputStream(entry);
			}
		}

		throw new ParsingFailureException("Invalid compressed MusicXML file: missing MusicXML content.");
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

	@FunctionalInterface interface ElementConsumer {
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
					final String type = reader.getAttributeValue(null, Tags.TYPE);
					consumeText(text -> scoreBuilder.setAttribute(Transforms.creatorTypeToAttribute(type), text));
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
		notationResolver.reset(partContext);

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
						consumeText(text -> partContext.addForwardDuration(divisionsToDuration(text)));
					} else if (element.equals(Tags.BACKUP)) {
						consumeText(text -> partContext.addBackupDuration(divisionsToDuration(text)));
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

	private void updateMeasureNumber() {
		try {
			final String measureNumberStr = reader.getAttributeValue(null, Tags.NUMBER).replaceAll("\\D", "");
			partContext.setMeasureNumber(Integer.parseInt(measureNumberStr));
		} catch (NumberFormatException e) {
			// If measure "number" does not contain a number at all, just increment the number.
			partContext.incrementMeasureNumber();
		}
	}

	private void consumeMeasureElem() throws XMLStreamException {
		updateMeasureNumber();

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
					consumeDirectionElem();
					break;
				case Tags.ATTRIBUTES:
					consumeAttributesElem();
					break;
				case Tags.BARLINE:
					consumeBarlineElem();
					break;
				case Tags.HARMONY:
					consumeHarmonyElem();
					break;
				// Fall through for elements that are currently not supported
				case Tags.FIGURED_BASS:
				default:
					skipElement();
			}
		}, Tags.MEASURE);

		partContext.finishMeasureElement();
	}

	private void consumeHarmonyElem() throws XMLStreamException {
		final ChordSymbolBuilder builder = new ChordSymbolBuilder();
		final StringBuilder offsetString = new StringBuilder();

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.ROOT:
					consumeUntil(
							rootTag -> {
								if (rootTag.equals(Tags.ROOT_STEP)) {
									consumeText(text -> currentStep = text);
								}
								if (rootTag.equals(Tags.ROOT_ALTER)) {
									consumeText(text -> currentAlter = Integer.parseInt(text));
								}

							}, Tags.ROOT);

					builder.setRoot(PitchName.of(Transforms.stepToPitchBase(currentStep),
							Transforms.alterToAccidental(currentAlter)));
					break;
				case Tags.BASS:
					consumeUntil(
							bassTag -> {
								if (Tags.BASS_STEP.equals(bassTag)) {
									consumeText(text -> currentStep = text);
								}
								if (Tags.BASS_ALTER.equals(bassTag)) {
									consumeText(text -> currentAlter = Integer.parseInt(text));
								}

							}, Tags.BASS);

					builder.setBass(PitchName.of(Transforms.stepToPitchBase(currentStep),
							Transforms.alterToAccidental(currentAlter)));
					break;
				case Tags.KIND:
					consumeChordKind(builder);
					break;
				case Tags.DEGREE:
					consumeChordDegreeElem(builder);
					break;
				case Tags.OFFSET:
					consumeText(text -> offsetString.append(text));
					break;
				case Tags.STAFF:
					consumeText(text -> partContext.setStaff(Integer.parseInt(text)));
					break;
				default:
					skipElement();
					break;
			}
		}, Tags.HARMONY);

		Fraction offset = Fraction.ZERO;
		if (!offsetString.isEmpty()) {
			offset = divisionsToFraction(offsetString.toString());
		}

		partContext.addChordSymbol(builder, offset);
	}

	private void consumeChordDegreeElem(final ChordSymbolBuilder builder) throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.DEGREE_VALUE:
					consumeText(text -> currentChordExtensionValue = Integer.parseInt(text));
					break;
				case Tags.DEGREE_ALTER:
					consumeText(text -> currentAlter = Integer.parseInt(text));
					break;
				case Tags.DEGREE_TYPE:
					consumeText(text -> currentChordExtensionType = text);
					break;
				default:
					break;
			}
		}, Tags.DEGREE);

		final var extensionType = Transforms.chordDegreeTypeToExtensionType(currentChordExtensionType);
		final var accidental = Transforms.alterToAccidental(currentAlter);
		final var degree = currentChordExtensionValue;
		builder.addExtension(ChordSymbol.extension(extensionType, accidental, degree));
	}

	private void consumeChordKind(final ChordSymbolBuilder builder) throws XMLStreamException {
		final StringBuilder kindTextBuilder = new StringBuilder();
		consumeText(text -> kindTextBuilder.append(text));
		final var kindText = kindTextBuilder.toString();

		Transforms.setKindToChordBuilder(builder, kindText);
	}

	private void consumeDirectionElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.DIRECTION_TYPE:
					consumeDirectionTypeElem();
					break;
				case Tags.OFFSET:
					consumeText(text -> partContext.setDirectionOffset(divisionsToFraction(text)));
					break;
				case Tags.STAFF:
					consumeText(text -> partContext.setDirectionStaff(Integer.parseInt(text)));
					break;
				default:
					skipElement();
			}

		}, Tags.DIRECTION);

		partContext.addDirection();
	}

	private void consumeDirectionTypeElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.WORDS:
					partContext.setDirectionType(Direction.Type.TEXT);
					consumeText(text -> partContext.setDirectionText(text));
					break;
				// Only text directions (words tag) are currently supported.
				default:
					skipElement();
			}

		}, Tags.DIRECTION_TYPE);
	}

	private Duration divisionsToDuration(String divisionsString) {
		final int divisions = Integer.parseInt(divisionsString);
		return Durations.QUARTER.divide(currentDivisions).multiply(divisions);
	}

	private Fraction divisionsToFraction(String divisionsString) {
		final int divisions = Integer.parseInt(divisionsString);
		Fraction duration = new Fraction(1, 4);
		return duration.divide(currentDivisions).multiply(divisions);
	}

	private void consumeNoteElem() throws XMLStreamException {
		currentDurationalBuilder = new NoteBuilder();
		currentConnectableBuilder = (NoteBuilder) currentDurationalBuilder;
		partContext.setChordTag(false);
		currentDotCount = 0;
		currentTupletDivisor = 1;

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.DURATION:
					consumeText(text -> currentDurDivisions = Integer.parseInt(text));
					break;
				case Tags.PITCH:
				case Tags.UNPITCHED:
					consumePitchElem(tag);
					break;
				case Tags.VOICE:
					consumeText(text -> partContext.setVoice(Integer.parseInt(text)));
					break;
				case Tags.STAFF:
					consumeText(text -> partContext.setStaff(Integer.parseInt(text)));
					break;
				case Tags.REST:
					currentDurationalBuilder = new RestBuilder();
					break;
				case Tags.CHORD:
					partContext.setChordTag(true);
					break;
				case Tags.NOTATIONS:
					consumeNotationsElem();
					break;
				case Tags.DOT:
					++currentDotCount;
					break;
				case Tags.TIME_MODIFICATION:
					consumeTimeModificationElem();
					break;
				case Tags.GRACE:
					final GraceNoteBuilder graceNoteBuilder = GraceNoteBuilder.moveFrom(
							(NoteBuilder) currentDurationalBuilder);
					Ornamental.Type type = Ornamental.Type.GRACE_NOTE;
					if (Objects.equals(reader.getAttributeValue(null, Tags.SLASH), Tags.YES)) {
						type = Ornamental.Type.ACCIACCATURA;
					}
					graceNoteBuilder.setGraceNoteType(type);
					currentConnectableBuilder = graceNoteBuilder;
					break;
				case Tags.TYPE:
					if (currentConnectableBuilder instanceof GraceNoteBuilder) {
						consumeText(text -> currentDurationalBuilder.setDuration(Transforms.noteTypeToDuration(text)));
					} else {
						skipElement();
					}
					break;
				case Tags.LYRIC:
					consumeLyricElement();
					break;
				// Fall through for elements that are currently not supported
				case Tags.ACCIDENTAL:
				case Tags.BEAM:
				case Tags.CUE:
				case Tags.INSTRUMENT:
				case Tags.NOTEHEAD_TEXT:
				case Tags.NOTEHEAD:
				case Tags.STEM:
				default:
					skipElement();

			}
		}, Tags.NOTE);

		if (currentConnectableBuilder instanceof NoteBuilder) {
			final int numerator = currentDurDivisions;
			final int denominator = 4 * currentDivisions;
			currentDurationalBuilder.setDuration(
					Duration.of(numerator, denominator, currentDotCount, currentTupletDivisor));
		} else if (currentDurationalBuilder.getDuration() == null) {
			// Default to eight note for displayable duration of grace note if the type has not been
			// explicitly specified.
			currentDurationalBuilder.setDuration(Durations.EIGHTH);
		}

		if (currentDurationalBuilder instanceof NoteBuilder) {
			final NoteBuilder currentNoteBuilder = (NoteBuilder) currentDurationalBuilder;

			if (isCurrentUnpitched) {
				currentNoteBuilder.setUnpitched().setDisplayPitch(currentPitch);
			} else {
				currentNoteBuilder.setPitch(currentPitch);
			}

			if (currentConnectableBuilder instanceof NoteBuilder) {
				partContext.updateChordBuffer(currentNoteBuilder);
				if (partContext.hasGraceNotes()) {
					partContext.addPreceedingOrnamentals(currentNoteBuilder);
				}
			} else if (currentConnectableBuilder instanceof GraceNoteBuilder graceNoteBuilder) {
				partContext.flushLyricBuffersTo(graceNoteBuilder);
				partContext.updateOrnamentalBuffer(graceNoteBuilder);
			}

			notationResolver.endNotations(currentConnectableBuilder);
			notationResolver.startOrContinueNotations(currentConnectableBuilder);
			notationResolver.continueOngoingNotations(currentConnectableBuilder);
		} else {
			partContext.updateChordBuffer(currentDurationalBuilder);
			partContext.getMeasureBuilder().addToVoice(partContext.getVoice(), currentDurationalBuilder);
		}
	}

	private void consumeLyricElement() throws XMLStreamException {
		final String line = reader.getAttributeValue(null, Tags.NUMBER);
		final int lineNumber = line == null ? 0 : Integer.parseInt(line);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.SYLLABIC:
					consumeText(text -> partContext.setLyricType(lineNumber, Transforms.syllabicToLyricType(text)));
					break;
				case Tags.ELISION:
					partContext.setLyricType(lineNumber, Lyric.Type.ELIDED);
					partContext.addLyricText(lineNumber, Lyric.ELISION_SEPARATOR);
					break;
				case Tags.TEXT:
					consumeText(text -> partContext.addLyricText(lineNumber, text));
					break;
				case Tags.EXTEND:
					String type = reader.getAttributeValue(null, Tags.TYPE);
					if (type != null && (type.equals(Tags.CONTINUE) || type.equals(Tags.STOP))) {
						partContext.setLyricType(lineNumber, Lyric.Type.EXTENSION);
					} else {
						partContext.setLyricType(lineNumber, Lyric.Type.EXTENDED);
						partContext.startExtendedLyric(lineNumber);
					}
					break;
				default:
					skipElement();
			}
		}, Tags.LYRIC);
	}

	private void consumeNotationsElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.ARTICULATIONS:
					consumeArticulations();
					break;
				case Tags.FERMATA:
					if (currentDurationalBuilder instanceof NoteBuilder) {
						((NoteBuilder) currentDurationalBuilder).addArticulation(Articulation.FERMATA);
					}
					break;
				case Tags.ORNAMENTS:
					consumeOrnamentsElem();
					break;
				case Tags.TECHNICAL:
					consumePlayingTechniques();
					break;
				default:
					if (Tags.CONNECTED_NOTATIONS.contains(tag)) {
						readConnectedNotationElemAttributes(tag);
					}

					skipElement();
			}
		}, Tags.NOTATIONS);
	}

	private void consumePlayingTechniques() throws XMLStreamException {
		currentTechnique = null;

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.HARMON_MUTE:
					consumeHarmonMuteTechnical();
					break;
				case Tags.HARMONIC:
					consumeHarmonicNote();
					break;
				case Tags.BEND:
					consumeBendElement();
					break;
				case Tags.HOLE:
					consumeHoleElement();
					break;
				case Tags.ARROW:
					consumeArrowElement();
					break;
				default:
					consumeBasicTechnical(tag);
			}

			if (currentDurationalBuilder instanceof NoteBuilder && currentTechnique != null) {
				((NoteBuilder) currentDurationalBuilder).addTechnique(currentTechnique);
			}
		}, Tags.TECHNICAL);
	}

	private void consumeArrowElement() throws XMLStreamException {
		final Map<Technique.AdditionalValue, Object> arrowAttributes = new EnumMap<>(Technique.AdditionalValue.class);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.ARROW_DIRECTION:
					consumeText(text -> currentTechniqueText = text);
					arrowAttributes.put(Technique.AdditionalValue.ARROW_DIRECTION, currentTechniqueText);
					break;
				case Tags.ARROW_STYLE:
					consumeText(text -> currentTechniqueText = text);
					arrowAttributes.put(Technique.AdditionalValue.ARROW_STYLE, currentTechniqueText);
					break;
				case Tags.ARROWHEAD:
					arrowAttributes.put(Technique.AdditionalValue.ARROWHEAD, Boolean.TRUE);
					break;
				case Tags.CIRCULAR_ARROW:
					consumeText(text -> currentTechniqueText = text);
					arrowAttributes.put(Technique.AdditionalValue.CIRCULAR_ARROW, currentTechniqueText);
					break;
				default:
					break;
			}

		}, Tags.ARROW);

		currentTechnique = Technique.of(Technique.Type.ARROW, arrowAttributes);
	}

	private void consumeHoleElement() throws XMLStreamException {
		final Map<Technique.AdditionalValue, Object> holeAttributes = new EnumMap<>(Technique.AdditionalValue.class);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.HOLE_SHAPE:
					consumeText(text -> currentTechniqueText = text);
					holeAttributes.put(Technique.AdditionalValue.WIND_HOLE_SHAPE, currentTechniqueText);
					break;
				case Tags.HOLE_CLOSED:
					consumeText(text -> currentTechniqueText = text);
					holeAttributes.put(Technique.AdditionalValue.WIND_HOLE_POSITION,
							Transforms.textToOpeningType(currentTechniqueText));
					break;
				case Tags.HOLE_TYPE:
					consumeText(text -> currentTechniqueText = text);
					holeAttributes.put(Technique.AdditionalValue.WIND_HOLE_TYPE, currentTechniqueText);
				default:
					break;
			}
		}, Tags.HOLE);

		currentTechnique = Technique.of(Technique.Type.HOLE, holeAttributes);
	}

	private void consumeBendElement() throws XMLStreamException {
		final Map<Technique.AdditionalValue, Object> bendAttributes = new EnumMap<>(Technique.AdditionalValue.class);

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.BEND_ALTER:
					consumeText(text -> currentTechniqueText = text);
					var alter = Double.parseDouble(currentTechniqueText);
					bendAttributes.put(Technique.AdditionalValue.BEND_SEMITONES, alter);
					break;
				case Tags.PRE_BEND:
					bendAttributes.put(Technique.AdditionalValue.PRE_BEND, Boolean.TRUE);
					break;
				case Tags.RELEASE:
					Duration offset = divisionsToDuration(reader.getAttributeValue(null, Tags.OFFSET));
					bendAttributes.put(Technique.AdditionalValue.BEND_RELEASE, offset);
					break;
				case Tags.WITH_BAR:
					consumeText(text -> currentTechniqueText = text);
					bendAttributes.put(Technique.AdditionalValue.BEND_WITH_BAR, currentTechniqueText);
					break;
				default:
					break;
			}

		}, Tags.BEND);

		currentTechnique = Technique.of(Technique.Type.BEND, bendAttributes);
	}

	private void consumeHarmonicNote() throws XMLStreamException {
		consumeUntil(harmonicTag -> {
			switch (harmonicTag) {
				case Tags.ARTIFICIAL:
					partContext.setArtificialHarmonic();
					break;
				case Tags.BASE_PITCH:
					partContext.setArtificialHarmonicBasePitch(currentPitch);
					break;
				case Tags.TOUCHING_PITCH:
					partContext.setArtificialHarmonicTouchingPitch(currentPitch);
					break;
				case Tags.SOUNDING_PITCH:
					partContext.setArtificialHarmonicSoundingPitch(currentPitch);
					break;
				case Tags.NATURAL:
					currentTechnique = Technique.of(Technique.Type.HARMONIC,
							Map.of(Technique.AdditionalValue.IS_NATURAL_HARMONIC, Boolean.TRUE));
					break;
				default:
					break;
			}

		}, Tags.HARMONIC);
	}

	private void consumeHarmonMuteTechnical() throws XMLStreamException {
		consumeUntil(harmonTag -> {
			consumeText(text -> currentTechniqueText = text);
			final var opening = Transforms.textToOpeningType(currentTechniqueText);
			currentTechnique = Technique.of(Technique.Type.HARMON_MUTE,
					Map.of(Technique.AdditionalValue.HARMON_MUTE_POSITION,
							opening));
		}, Tags.HARMON_MUTE);
	}

	private void consumeBasicTechnical(String tag) throws XMLStreamException {
		final var type = Transforms.tagToTechniqueType(tag);

		if (type == null) {
			LOG.warn("Could not find type for technical tag {}", tag);
			return;
		}

		currentTechniqueText = null;
		consumeText(text -> currentTechniqueText = text);

		if (currentTechniqueText != null) {
			try {
				currentTechnique = Technique.of(type, Integer.parseInt(currentTechniqueText));
			} catch (NumberFormatException e) {
				currentTechnique = Technique.of(type, currentTechniqueText);
			}
		} else {
			currentTechnique = Technique.of(type);
		}
	}

	private void consumeOrnamentsElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.TREMOLO:
					consumeText(text -> {
						final int tremololines = Integer.parseInt(text);

						if (currentDurationalBuilder instanceof NoteBuilder) {
							((NoteBuilder) currentDurationalBuilder).addOrnament(
									Transforms.lineNumbersToTremolo(tremololines));
						}
					});
					break;
				default:
					Ornament ornament = Transforms.tagToOrnament(tag);
					if (ornament != null) {
						if (currentDurationalBuilder instanceof NoteBuilder) {
							((NoteBuilder) currentDurationalBuilder).addOrnament(ornament);
						}
					}
			}

		}, Tags.ORNAMENTS);
	}

	private void consumeTimeModificationElem() throws XMLStreamException {
		consumeUntil(tag -> {
			if (tag.equals(Tags.ACTUAL_NOTES)) {
				consumeText(text -> currentTupletDivisor = Integer.parseInt(text));
			} else {
				skipElement();
			}
		}, Tags.TIME_MODIFICATION);
	}

	private void readConnectedNotationElemAttributes(String notationTag) {
		final String elementRoleType = reader.getAttributeValue(null, Tags.TYPE);
		final String arpeggioDirection = reader.getAttributeValue(null, Tags.DIRECTION);
		final Notation.Type type = Transforms.stringToNotationType(notationTag, arpeggioDirection);
		final Notation.Style style = Transforms.stringToNotationStyle(reader.getAttributeValue(null, Tags.LINE_TYPE));
		final int notationNumber = NotationReadResolver.parseNotationNumber(
				reader.getAttributeValue(null, Tags.NUMBER));
		if (!(currentDurationalBuilder instanceof ConnectableBuilder)) {
			LOG.warn("Trying to read connected notations when current builder is not of correct type");
			return;
		}
		if (elementRoleType != null) {
			switch (elementRoleType) {
				case Tags.START: // Fall through. Start and bottom attributes explicitly start a notation.
				case Tags.BOTTOM:
				case Tags.CONTINUE:
					notationResolver.addNotationToStartOrContinue(notationNumber, type, style);
					break;
				case Tags.STOP: // Fall through: Stop and top are the attributes that explicitly end a notation.
				case Tags.TOP:
					notationResolver.addNotationToEnd(notationNumber, type);
					break;
			}
		} else {
			notationResolver.addNotationToStartOrContinue(notationNumber, type, style);
		}
	}

	private void consumeArticulations() throws XMLStreamException {
		consumeUntil(tag -> {
			final Articulation articulation = Transforms.stringToArticulation(tag);
			if (articulation != null && (currentDurationalBuilder instanceof NoteBuilder)) {
				((NoteBuilder) currentDurationalBuilder).addArticulation(articulation);
			}
		}, Tags.ARTICULATIONS);
	}

	private void consumePitchElem(String pitchTag) throws XMLStreamException {
		// Alter is optional, so it needs to be set to zero to account for the absence of the element.
		currentAlter = 0;
		if (pitchTag.equals(Tags.PITCH)) {
			isCurrentUnpitched = false;
		} else if (pitchTag.equals(Tags.UNPITCHED)) {
			isCurrentUnpitched = true;
		}

		consumeUntil(tag -> {
			switch (tag) {
				case Tags.STEP:
				case Tags.DISPLAY_STEP:
					consumeText(text -> currentStep = text);
					break;
				case Tags.ALTER:
					consumeText(text -> currentAlter = Integer.parseInt(text));
				case Tags.OCTAVE:
				case Tags.DISPLAY_OCTAVE:
					consumeText(text -> currentOctave = Integer.parseInt(text));
			}
		}, pitchTag);

		currentPitch = Pitch.of(Transforms.stepToPitchBase(currentStep),
				Transforms.alterToAccidental(currentAlter), currentOctave);
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
				case Tags.STAFF_DETAILS:
					consumeStaffDetails();
					break;
				// Fall through for elements that are currently not supported
				case Tags.PART_SYMBOL:
				case Tags.INSTRUMENTS:
				case Tags.TRANSPOSE:
				case Tags.FOR_PART:
				case Tags.DIRECTIVE:
				case Tags.MEASURE_STYLE:
				default:
					skipElement();
			}

		}, Tags.ATTRIBUTES);
	}

	private void consumeStaffDetails() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.STAFF_LINES:
					consumeText(text -> partContext.setStaffLines(Integer.parseInt(text)));
					break;
				default:
					break;

			}
		}, Tags.STAFF_DETAILS);
	}

	private void consumeKeyElem() throws XMLStreamException {
		consumeUntil(tag -> {
			switch (tag) {
				case Tags.FIFTHS:
					consumeText(text -> partContext.getMeasureBuilder()
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

	private String getParsingFailureMessage(String message) {
		return "Parsing of " + path + " failed with: " + message;
	}

}
