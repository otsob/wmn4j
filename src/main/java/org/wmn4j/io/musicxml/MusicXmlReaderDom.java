/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.ConnectableBuilder;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.GraceNoteBuilder;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Ornament;
import org.wmn4j.notation.Ornamental;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.TimeSignature;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A parser for MusicXML files.
 */
final class MusicXmlReaderDom implements MusicXmlReader {

	private static final int MIN_STAFF_NUMBER = SingleStaffPart.STAFF_NUMBER;
	private static final int DEFAULT_STAFF_COUNT = 1;
	private static final String MUSICXML_V3_1_SCHEMA_PATH = "org/wmn4j/io/musicxml/musicxml.xsd";

	private static final Logger LOG = LoggerFactory.getLogger(MusicXmlReaderDom.class);

	private final boolean validateInput;
	private final Path path;

	/**
	 * Constructor that allows setting validation.
	 *
	 * @param validateInput Whether this validates MusicXML files given as input
	 * @param path          the path of the file that this reader is created for
	 */
	MusicXmlReaderDom(Path path, boolean validateInput) {
		this.validateInput = validateInput;
		this.path = path;
	}

	private DocumentBuilder createAndConfigureDocBuilder() throws ParserConfigurationException {

		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		return dbf.newDocumentBuilder();
	}

	private boolean isMusicXmlFileValid(File musicXmlFile) throws IOException {
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			ClassLoader classLoader = getClass().getClassLoader();
			final Schema schema = schemaFactory.newSchema(classLoader.getResource(MUSICXML_V3_1_SCHEMA_PATH));
			final Validator validator = schema.newValidator();
			validator.validate(new StreamSource(musicXmlFile));
		} catch (SAXException e) {
			LOG.warn(musicXmlFile.toString() + " is not valid MusicXML:", e);
			return false;
		}

		return true;
	}

	@Override
	public Score readScore() throws IOException, ParsingFailureException {
		return readScoreBuilder().build();
	}

	@Override
	public ScoreBuilder readScoreBuilder() throws IOException, ParsingFailureException {
		final ScoreBuilder scoreBuilder = new ScoreBuilder();
		final File musicXmlFile = path.toFile();

		if (this.validateInput && !isMusicXmlFileValid(musicXmlFile)) {
			throw new ParsingFailureException(path.toString() + " is not a valid MusicXML file");
		}

		try {
			final DocumentBuilder docBuilder = createAndConfigureDocBuilder();

			try {
				final Document musicXmlDoc = docBuilder.parse(musicXmlFile);
				readScoreToBuilder(scoreBuilder, musicXmlDoc);
			} catch (final SAXException ex) {
				throw new ParsingFailureException("Parsing failed: " + ex.getMessage());
			}
		} catch (final ParserConfigurationException e) {
			throw new ParsingFailureException("Parser configuration failed: " + e.getMessage());
		}

		return scoreBuilder;
	}

	/**
	 * Create a Score from a MusicXML document.
	 */
	private void readScoreToBuilder(ScoreBuilder scoreBuilder, Document doc) {
		readScoreAttributesToBuilder(scoreBuilder, doc);
		readPartsIntoBuilder(scoreBuilder, doc);
	}

	/**
	 * Read the attributes of the score from the Document and add them to the
	 * ScoreBuilder.
	 */
	private void readScoreAttributesToBuilder(ScoreBuilder scoreBuilder, Document doc) {

		Node workNode = doc.getElementsByTagName(MusicXmlTags.SCORE_WORK).item(0);
		if (workNode != null) {
			Optional<Node> titleNode = DocHelper.findChild(workNode, MusicXmlTags.SCORE_WORK_TITLE);
			if (titleNode.isPresent()) {
				scoreBuilder.setAttribute(Score.Attribute.TITLE, titleNode.get().getTextContent());
			}
		}

		Node movementTitleNode = doc.getElementsByTagName(MusicXmlTags.SCORE_MOVEMENT_TITLE).item(0);
		if (movementTitleNode != null) {
			scoreBuilder.setAttribute(Score.Attribute.MOVEMENT_TITLE, movementTitleNode.getTextContent());
		}

		Node identificationNode = doc.getElementsByTagName(MusicXmlTags.SCORE_IDENTIFICATION).item(0);

		if (identificationNode != null) {
			List<Node> creatorNodes = DocHelper
					.findChildren(identificationNode, MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);

			for (Node creatorNode : creatorNodes) {
				final Optional<String> creatorType = DocHelper
						.getAttributeValue(creatorNode, MusicXmlTags.SCORE_IDENTIFICATION_CREATOR_TYPE);

				if (creatorType.isPresent()) {
					if (creatorType.get().equals(MusicXmlTags.SCORE_IDENTIFICATION_COMPOSER)) {
						scoreBuilder.setAttribute(Score.Attribute.COMPOSER, creatorNode.getTextContent());
					}

					if (creatorType.get().equals(MusicXmlTags.SCORE_IDENTIFICATION_ARRANGER)) {
						scoreBuilder.setAttribute(Score.Attribute.ARRANGER, creatorNode.getTextContent());
					}
				}
			}
		}
	}

	/**
	 * Go through the parts defined in the MusicXML Document and add the parts to
	 * the ScoreBuilder.
	 */
	private void readPartsIntoBuilder(ScoreBuilder scoreBuilder, Document doc) {

		final Map<String, PartBuilder> partBuilders = createPartBuilders(
				doc.getElementsByTagName(MusicXmlTags.PART_LIST).item(0));

		// Read measures into part builders.
		final NodeList partNodes = doc.getElementsByTagName(MusicXmlTags.PART);
		for (int i = 0; i < partNodes.getLength(); ++i) {
			final Node partNode = partNodes.item(i);
			final String partId = partNode.getAttributes().getNamedItem(MusicXmlTags.PART_ID).getTextContent();
			final PartBuilder partBuilder = partBuilders.get(partId);

			readMeasuresIntoPartBuilder(partBuilder, partNode);
			scoreBuilder.addPart(partBuilder);
		}
	}

	/**
	 * Creates empty part builders from the parts list in the MusicXML file.
	 */
	private Map<String, PartBuilder> createPartBuilders(Node partsList) {
		final Map<String, PartBuilder> partBuilders = new HashMap<>();

		// Read part attributes.
		if (partsList != null) {
			for (Node partInfoNode : DocHelper.findChildren(partsList, MusicXmlTags.PLIST_SCORE_PART)) {
				Optional<String> partId = DocHelper.getAttributeValue(partInfoNode, MusicXmlTags.PART_ID);
				if (partId.isEmpty()) {
					LOG.warn("Part info is missing part id: ", partInfoNode);
					continue;
				}

				final String partName = DocHelper.findChild(partInfoNode, MusicXmlTags.PART_NAME)
						.map(partNameNode -> partNameNode.getTextContent())
						.orElse(partId.get());

				final PartBuilder partBuilder = new PartBuilder(partName);

				DocHelper.findChild(partInfoNode, MusicXmlTags.PART_NAME_ABBREVIATION)
						.ifPresent(abbreviaterPartNameNode -> partBuilder.setAttribute(Part.Attribute.ABBREVIATED_NAME,
								abbreviaterPartNameNode.getTextContent()));

				partBuilders.put(partId.get(), partBuilder);
			}
		}

		return partBuilders;
	}

	/**
	 * Go through the measures in the part and add them to the PartBuilder.
	 */
	private void readMeasuresIntoPartBuilder(PartBuilder partBuilder, Node partNode) {

		final int staves = getStaffCount(partNode);
		final Map<Integer, Context> contexts = new HashMap<>();

		// Create the context containers for the staves.
		for (int staffNumber = MIN_STAFF_NUMBER; staffNumber < staves + MIN_STAFF_NUMBER; ++staffNumber) {
			contexts.put(staffNumber, new Context());
		}

		// Read measure node by node, create measure and add to list
		final NodeList measureNodes = partNode.getChildNodes();

		// Used for keeping track of and resolving possible connected notations.
		final ConnectedNotations connectedNotations = new ConnectedNotations();

		for (int i = 0; i < measureNodes.getLength(); ++i) {
			final Node measureNode = measureNodes.item(i);

			// Make sure that the node really is a measure node.
			if (measureNode.getNodeName().equals(MusicXmlTags.MEASURE)) {
				readMeasureIntoPartBuilder(partBuilder, measureNode, contexts, staves, connectedNotations);
			}
		}
	}

	/**
	 * Find the number of staves in the part.
	 */
	private int getStaffCount(Node partNode) {

		// If the number of staves is not defined in the first attributes node,
		// then use default.
		int staves = DEFAULT_STAFF_COUNT;

		// Find first attributes node and check if it has staves defined.
		final NodeList measureNodes = partNode.getChildNodes();
		for (int i = 0; i < measureNodes.getLength(); ++i) {
			final Node measureNode = measureNodes.item(i);

			if (measureNode.getNodeName().equals(MusicXmlTags.MEASURE)) {
				staves = DocHelper.findChild(measureNode, MusicXmlTags.MEASURE_ATTRIBUTES)
						.flatMap(attributesNode -> DocHelper.findChild(attributesNode, MusicXmlTags.MEAS_ATTR_STAVES))
						.map(stavesNode -> Integer.parseInt(stavesNode.getTextContent()))
						.orElse(DEFAULT_STAFF_COUNT);
				break;
			}
		}

		return staves;
	}

	/**
	 * Read the measure information from the Node, create a Measure from the node
	 * and add it to the PartBuilder.
	 */
	private void readMeasureIntoPartBuilder(PartBuilder partBuilder, Node measureNode, Map<Integer, Context> contexts,
			int staves, ConnectedNotations connectedNotations) {

		final int measureNumber = Integer
				.parseInt(measureNode.getAttributes().getNamedItem(MusicXmlTags.MEASURE_NUM).getTextContent());

		final Map<Integer, MeasureBuilder> measureBuilders = new HashMap<>();
		final Map<Integer, ChordBuffer> chordBuffers = new HashMap<>();
		final Map<Integer, List<Duration>> offsets = new HashMap<>();
		final Map<Integer, Clef> lastClefs = new HashMap<>();

		// Initialize the helper data structures.
		for (int staff = MIN_STAFF_NUMBER; staff <= staves; ++staff) {
			measureBuilders.put(staff, new MeasureBuilder(measureNumber));
			chordBuffers.put(staff, new ChordBuffer());
			offsets.put(staff, new ArrayList<>());
			lastClefs.put(staff, contexts.get(staff).getClef());
		}

		Duration backupDuration = null;
		int staffNumberOfPreviousNote = MIN_STAFF_NUMBER;

		final NodeList measureChildren = measureNode.getChildNodes();
		for (int i = 0; i < measureChildren.getLength(); ++i) {
			final Node node = measureChildren.item(i);

			if (node.getNodeName()
					.equals(MusicXmlTags.MEASURE_ATTRIBUTES)) {

				// Handle measure attributes that occur in the beginning of measure
				final boolean noNotesReadYet = offsets.values().stream()
						.allMatch(offsetValues -> offsetValues.isEmpty());

				if (noNotesReadYet) {
					updateContexts(node, contexts);

					for (Integer staff : contexts.keySet()) {
						lastClefs.put(staff, contexts.get(staff).getClef());
					}
				} else {
					// Handle clef change
					addClefChange(node, measureBuilders, offsets, lastClefs, backupDuration, staffNumberOfPreviousNote);
				}
			}

			// Handle backup element
			if (node.getNodeName().equals(MusicXmlTags.MEASURE_BACKUP)) {
				backupDuration = getBackupDuration(node, contexts.get(staffNumberOfPreviousNote));
				Context context = contexts.get(staffNumberOfPreviousNote);
				context.setGraceNotesToNoteBuilder(context.getPreviousBuilder(), false);
			}

			// Handle note element
			if (node.getNodeName().equals(MusicXmlTags.NOTE)) {
				staffNumberOfPreviousNote = readNoteElementIntoMeasureBuilder(node, measureBuilders, chordBuffers,
						offsets, contexts,
						connectedNotations);
			}

			// Handle barlines
			if (node.getNodeName().equals(MusicXmlTags.BARLINE)) {
				addBarline(node, measureBuilders);
			}
		}

		// If there are any grace notes at the end of the measure they are attached to
		// the last note builder as succeeding grace notes.
		Context lastContext = contexts.get(staffNumberOfPreviousNote);
		lastContext.setGraceNotesToNoteBuilder(lastContext.getPreviousBuilder(), false);

		// Set the rest of the measure attributes and add to partBuilder
		for (int staffNumber = MIN_STAFF_NUMBER; staffNumber < staves + MIN_STAFF_NUMBER; ++staffNumber) {
			final MeasureBuilder builder = measureBuilders.get(staffNumber);
			final Context context = contexts.get(staffNumber);

			chordBuffers.get(staffNumber).contentsToBuilder(builder);

			builder.setClef(context.getClef());
			builder.setTimeSignature(context.getTimeSig());
			builder.setKeySignature(context.getKeySig());
			context.setClef(lastClefs.get(staffNumber));

			partBuilder.addToStaff(staffNumber, builder);
		}
	}

	private Duration getBackupDuration(Node backupNode, Context context) {
		Duration backupDuration = getDuration(backupNode, context.getDivisions());
		// In case the backup is to the beginning of the measure, do not consider it.
		if (backupDuration != null && backupDuration.equals(context.getTimeSig().getTotalDuration())) {
			backupDuration = null;
		}

		return backupDuration;
	}

	private void addClefChange(Node node, Map<Integer, MeasureBuilder> measureBuilders,
			Map<Integer, List<Duration>> offsets, Map<Integer, Clef> lastClefs, Duration backupDuration,
			int staffNumberOfPreviousNote) {
		Clef clef = null;
		final Optional<Node> clefNode = DocHelper.findChild(node, MusicXmlTags.CLEF);
		int clefStaff = staffNumberOfPreviousNote;

		if (clefNode.isPresent()) {
			clef = MusicXmlReaderDom.this.getClef(clefNode.get());
			final NamedNodeMap clefAttributes = clefNode.get().getAttributes();
			final Node clefStaffNode = clefAttributes.getNamedItem(MusicXmlTags.CLEF_STAFF);
			if (clefStaffNode != null) {
				clefStaff = Integer.parseInt(clefStaffNode.getTextContent());
			}
		}
		if (clef != null && !offsets.get(clefStaff).isEmpty()) {
			Duration cumulatedDur = Duration.sum(offsets.get(clefStaff));
			if (backupDuration != null) {
				cumulatedDur = cumulatedDur.subtract(backupDuration);
			}

			measureBuilders.get(clefStaff).addClefChange(cumulatedDur, clef);
			lastClefs.put(clefStaff, clef);
		}
	}

	private void addBarline(Node node, Map<Integer, MeasureBuilder> measureBuilders) {
		final Barline barline = getBarline(node);

		// Barlines are not staff specific, so a barline node affects all staves in
		// measure.
		final Node locationNode = node.getAttributes().getNamedItem(MusicXmlTags.BARLINE_LOCATION);
		if (locationNode != null && locationNode.getTextContent().equals(MusicXmlTags.BARLINE_LOCATION_LEFT)) {
			for (Integer staff : measureBuilders.keySet()) {
				measureBuilders.get(staff).setLeftBarline(barline);
			}
		} else {
			for (Integer staff : measureBuilders.keySet()) {
				measureBuilders.get(staff).setRightBarline(barline);
			}
		}
	}

	/**
	 * Read a note element. A note element can be a rest, note, grace note, or a
	 * note in a chord.
	 */
	private int readNoteElementIntoMeasureBuilder(Node noteNode, Map<Integer, MeasureBuilder> measureBuilders,
			Map<Integer, ChordBuffer> chordBuffers, Map<Integer, List<Duration>> offsets,
			Map<Integer, Context> contexts, ConnectedNotations connectedNotations) {
		final int staffNumber = DocHelper.findChild(noteNode, MusicXmlTags.NOTE_STAFF)
				.map(staffNode -> Integer.parseInt(staffNode.getTextContent()))
				.orElse(MIN_STAFF_NUMBER);

		final Context context = contexts.get(staffNumber);
		final MeasureBuilder builder = measureBuilders.get(staffNumber);

		final int voice = getVoice(noteNode);
		final Optional<Node> graceNode = DocHelper.findChild(noteNode, MusicXmlTags.NOTE_GRACE_NOTE);
		final boolean isGraceNote = graceNode.isPresent();
		final Duration duration;

		if (!isGraceNote) {
			duration = getDuration(noteNode, context.getDivisions());
			offsets.get(staffNumber).add(duration);
		} else {
			duration = getGraceNoteDisplayableDuration(noteNode);
		}

		if (isRest(noteNode)) {
			chordBuffers.get(staffNumber).contentsToBuilder(builder);
			builder.addToVoice(voice, new RestBuilder(duration));
		} else {
			final Pitch pitch = getPitch(noteNode);
			ChordBuffer buffer = chordBuffers.get(staffNumber);

			if (isGraceNote) {
				final GraceNoteBuilder graceNoteBuilder = new GraceNoteBuilder(pitch, duration);

				Optional<String> slashType = DocHelper
						.getAttributeValue(graceNode.get(), MusicXmlTags.NOTE_GRACE_SLASH);

				if (slashType.isPresent() && slashType.get().equals(MusicXmlTags.YES)) {
					graceNoteBuilder.setGraceNoteType(Ornamental.Type.ACCIACCATURA);
				}

				context.addGraceNoteToBuffer(graceNoteBuilder);

				resolveTiesAndNotations(voice, noteNode, connectedNotations, graceNoteBuilder, buffer, context);
			} else {
				final NoteBuilder noteBuilder = new NoteBuilder(pitch, duration);

				context.setGraceNotesToNoteBuilder(noteBuilder, true);

				if (hasChordTag(noteNode)) {
					buffer.addNote(noteBuilder, voice);
				} else {
					buffer.contentsToBuilder(builder);
					buffer.addNote(noteBuilder, voice);
				}

				resolveTiesAndNotations(voice, noteNode, connectedNotations, noteBuilder, buffer, context);
				context.setPreviousBuilder(noteBuilder);
			}
		}

		return staffNumber;
	}

	private Duration getGraceNoteDisplayableDuration(Node graceNoteNode) {
		Optional<Node> typeNode = DocHelper.findChild(graceNoteNode, MusicXmlTags.NOTE_DURATION_TYPE);
		if (typeNode.isEmpty()) {
			LOG.error("Grace note in MusicXML without note type defined. Defaulting to eighth note.");
			return Durations.EIGHTH;
		}

		return TypeToDurationConverter.INSTANCE.getDuration(typeNode.get().getTextContent());
	}

	private void resolveTiesAndNotations(int voiceNumber, Node noteNode, ConnectedNotations connectedNotations,
			GraceNoteBuilder graceNoteBuilder, ChordBuffer buffer, Context context) {

		resolveTiesAndNotations(voiceNumber, noteNode, graceNoteBuilder,
				connectedNotations, buffer, articulation -> graceNoteBuilder.addArticulation(articulation),
				ornament -> graceNoteBuilder.addOrnament(ornament), context);
	}

	private void resolveTiesAndNotations(int voiceNumber, Node noteNode, ConnectedNotations connectedNotations,
			NoteBuilder noteBuilder, ChordBuffer buffer, Context context) {

		resolveTiesAndNotations(voiceNumber, noteNode, noteBuilder,
				connectedNotations, buffer, articulation -> noteBuilder.addArticulation(articulation),
				ornament -> noteBuilder.addOrnament(ornament), context);
	}

	private void resolveTiesAndNotations(int voiceNumber, Node noteNode, ConnectableBuilder builder,
			ConnectedNotations connectedNotations, ChordBuffer buffer, Consumer<Articulation> articulationAdder,
			Consumer<Ornament> ornamentAdder, Context context) {
		final Optional<Node> notationsNodeOptional = DocHelper.findChild(noteNode, MusicXmlTags.NOTATIONS);

		if (notationsNodeOptional.isPresent()) {
			final Node notationsNode = notationsNodeOptional.get();
			addArticulations(notationsNode, articulationAdder);
			addOrnaments(notationsNode, ornamentAdder);
		}

		addNotations(voiceNumber, notationsNodeOptional, builder, connectedNotations, buffer, context);
	}

	private void addNotations(int voiceNumber, Optional<Node> notationsNode, ConnectableBuilder noteBuilder,
			ConnectedNotations connectedNotations, ChordBuffer buffer, Context context) {

		final Set<UnresolvedNotation> startedNotations = new HashSet<>();

		// Handle the beginnings and ends of notations if the notations node is present
		if (notationsNode.isPresent()) {
			for (Node notationNode : getNotationNodes(notationsNode.get())) {
				final Optional<String> notationPositionType = DocHelper
						.getAttributeValue(notationNode, MusicXmlTags.NOTATION_TYPE);

				// Notation number can be omitted in MusicXML. In that case should default to 1.
				final int notationNumber = DocHelper.getAttributeValue(notationNode, MusicXmlTags.NOTATION_NUMBER)
						.map(stringValue -> Integer.parseInt(stringValue)).orElse(1);

				final Notation.Type notationType = getNotationType(notationNode);
				final Notation.Style notationStyle = getNotationStyle(notationNode);

				if (notationPositionType.isPresent()) {
					final String type = notationPositionType.get();

					if (type.equals(MusicXmlTags.NOTATION_TYPE_START)
							|| type.equals(MusicXmlTags.NON_ARPEGGIATE_BOTTOM)) {
						UnresolvedNotation startedNotation = connectedNotations
								.createAndAddStartOfNotation(voiceNumber, notationNumber, notationType, notationStyle,
										noteBuilder);
						startedNotations.add(startedNotation);
					} else if (type.equals(MusicXmlTags.NOTATION_TYPE_STOP)
							|| type.equals(MusicXmlTags.NON_ARPEGGIATE_TOP)) {
						connectedNotations.endNotation(voiceNumber, notationNumber, notationType, noteBuilder, context);
					}
				} else {
					// The notations that do not specify start and stop are related to arpeggiation.
					// The notation objects are added to the ChordBuffer which will handle them.
					buffer.setArpeggiation(notationType);
				}
			}
		}

		if (connectedNotations.hasUnresolvedNotations(voiceNumber)) {
			connectedNotations.addToUnresolvedNotations(voiceNumber, noteBuilder, startedNotations);
		}
	}

	private void addOrnaments(Node notationsNode, Consumer<Ornament> ornamentAdder) {
		Optional<Node> ornamentsNodeOpt = DocHelper.findChild(notationsNode, MusicXmlTags.ORNAMENTS);
		if (ornamentsNodeOpt.isEmpty()) {
			return;
		}

		final Node ornamentsNode = ornamentsNodeOpt.get();
		for (int i = 0; i < ornamentsNode.getChildNodes().getLength(); i++) {
			Node ornamentNode = ornamentsNode.getChildNodes().item(i);
			Ornament ornament = nodeToOrnament(ornamentNode);
			if (ornament != null) {
				ornamentAdder.accept(ornament);
			}
		}
	}

	private Ornament nodeToOrnament(Node ornamentNode) {
		final String text = ornamentNode.getNodeName();

		switch (text) {
			case MusicXmlTags.DELAYED_INVERTED_TURN:
				return Ornament.of(Ornament.Type.DELAYED_INVERTED_TURN);
			case MusicXmlTags.DELAYED_TURN:
				return Ornament.of(Ornament.Type.DELAYED_TURN);
			case MusicXmlTags.INVERTED_MORDENT:
				return Ornament.of(Ornament.Type.INVERTED_MORDENT);
			case MusicXmlTags.INVERTED_TURN:
				return Ornament.of(Ornament.Type.INVERTED_TURN);
			case MusicXmlTags.MORDENT:
				return Ornament.of(Ornament.Type.MORDENT);
			case MusicXmlTags.TREMOLO:
				return createTremolo(ornamentNode);
			case MusicXmlTags.TRILL_MARK:
				return Ornament.of(Ornament.Type.TRILL);
			case MusicXmlTags.TURN:
				return Ornament.of(Ornament.Type.TURN);
		}

		return null;
	}

	private Ornament createTremolo(Node tremoloNode) {
		int tremoloLines = Integer.parseInt(tremoloNode.getTextContent());

		switch (tremoloLines) {
			case 1:
				return Ornament.of(Ornament.Type.SINGLE_TREMOLO);
			case 2:
				return Ornament.of(Ornament.Type.DOUBLE_TREMOLO);
			case 3:
				return Ornament.of(Ornament.Type.TRIPLE_TREMOLO);
		}

		return null;
	}

	private Collection<Node> getNotationNodes(Node notationsNode) {
		Collection<Node> notationNodes = new ArrayList<>();
		NodeList notationsChildren = notationsNode.getChildNodes();
		for (int i = 0; i < notationsChildren.getLength(); ++i) {
			Node childNode = notationsChildren.item(i);
			if (MusicXmlTags.CONNECTED_NOTATION_NODE_NAMES.contains(childNode.getNodeName())) {
				notationNodes.add(childNode);
			}
		}

		return notationNodes;
	}

	private Notation.Type getNotationType(Node notationNode) {
		switch (notationNode.getNodeName()) {
			case MusicXmlTags.TIED:
				return Notation.Type.TIE;
			case MusicXmlTags.SLUR:
				return Notation.Type.SLUR;
			case MusicXmlTags.GLISSANDO:
			case MusicXmlTags.SLIDE: // Slide is practically always glissando in MusicXML, fall through.
				return Notation.Type.GLISSANDO;
			case MusicXmlTags.ARPEGGIATE:
				Node directionNode = notationNode.getAttributes().getNamedItem(MusicXmlTags.ARPEGGIO_DIRECTION);
				if (directionNode == null) {
					return Notation.Type.ARPEGGIATE;
				}

				if (MusicXmlTags.ARPEGGIO_DIRECTION_DOWN.equals(directionNode.getTextContent())) {
					return Notation.Type.ARPEGGIATE_DOWN;
				}

				if (MusicXmlTags.ARPEGGIO_DIRECTION_UP.equals(directionNode.getTextContent())) {
					return Notation.Type.ARPEGGIATE_UP;
				}
			case MusicXmlTags.NON_ARPEGGIATE:
				return Notation.Type.NON_ARPEGGIATE;
		}

		LOG.warn("Tried to parse unsupported notation type: " + notationNode.getNodeName());
		return null;
	}

	private Notation.Style getNotationStyle(Node notationNode) {
		String notationAttribute = DocHelper.getAttributeValue(notationNode, MusicXmlTags.NOTATION_LINE_TYPE)
				.orElse("");

		switch (notationAttribute) {
			case MusicXmlTags.NOTATION_LINE_DASHED:
				return Notation.Style.DASHED;
			case MusicXmlTags.NOTATION_LINE_DOTTED:
				return Notation.Style.DOTTED;
			case MusicXmlTags.NOTATION_LINE_WAVY:
				return Notation.Style.WAVY;
			case MusicXmlTags.NOTATION_LINE_SOLID: // Fall through.
			default:
				return Notation.Style.SOLID;
		}
	}

	private void addArticulations(Node notationsNode, Consumer<Articulation> articulationAdder) {
		final Optional<Node> articulationsNode = DocHelper.findChild(notationsNode, MusicXmlTags.NOTE_ARTICULATIONS);

		if (articulationsNode.isPresent()) {
			for (int i = 0; i < articulationsNode.get().getChildNodes().getLength(); ++i) {
				final Node articulationNode = articulationsNode.get().getChildNodes().item(i);
				if (articulationNode.getNodeType() == Node.ELEMENT_NODE) {
					final Articulation articulation = getArticulation(articulationNode.getNodeName());
					if (articulation != null) {
						articulationAdder.accept(articulation);
					} else {
						LOG.warn("Articulation of type " + articulationNode.getNodeName() + " not supported");
					}
				}
			}
		}

		DocHelper.findChild(notationsNode, MusicXmlTags.FERMATA)
				.ifPresent(fermataNode -> articulationAdder.accept(Articulation.FERMATA));
	}

	private Articulation getArticulation(String articulationString) {
		switch (articulationString) {
			case MusicXmlTags.ACCENT:
				return Articulation.ACCENT;
			case MusicXmlTags.BREATH_MARK:
				return Articulation.BREATH_MARK;
			case MusicXmlTags.CAESURA:
				return Articulation.CAESURA;
			case MusicXmlTags.FERMATA:
				return Articulation.FERMATA;
			case MusicXmlTags.SLIDE_IN_DOWN:
				return Articulation.SLIDE_IN_DOWN;
			case MusicXmlTags.SLIDE_IN_UP:
				return Articulation.SLIDE_IN_UP;
			case MusicXmlTags.SLIDE_OUT_DOWN:
				return Articulation.SLIDE_OUT_DOWN;
			case MusicXmlTags.SLIDE_OUT_UP:
				return Articulation.SLIDE_OUT_UP;
			case MusicXmlTags.SPICCATO:
				return Articulation.SPICCATO;
			case MusicXmlTags.STACCATISSIMO:
				return Articulation.STACCATISSIMO;
			case MusicXmlTags.STACCATO:
				return Articulation.STACCATO;
			case MusicXmlTags.STRESS:
				return Articulation.STRESS;
			case MusicXmlTags.STRONG_ACCENT:
				return Articulation.STRONG_ACCENT;
			case MusicXmlTags.TENUTO:
				return Articulation.TENUTO;
			case MusicXmlTags.TENUTO_STACCATO:
				return Articulation.TENUTO_STACCATO;
			case MusicXmlTags.UNSTRESS:
				return Articulation.UNSTRESS;
		}

		return null;
	}

	private void updateContexts(Node attributesNode, Map<Integer, Context> contexts) {

		for (Integer staff : contexts.keySet()) {
			final Context context = contexts.get(staff);

			// Divisions are the same for all staves.
			context.setDivisions(getDivisions(attributesNode, context.getDivisions()));

			// TODO:
			// Time and key signatures can be different for different staves but
			// that's not necessarily handled in the attributes nodes.
			context.setTimeSig(getTimeSig(attributesNode, context, staff));
			context.setKeySig(getKeySig(attributesNode, context));
		}

		final List<Node> clefNodes = DocHelper.findChildren(attributesNode, MusicXmlTags.CLEF);

		for (Node clefNode : clefNodes) {
			int staffNumber = MIN_STAFF_NUMBER;
			final Node clefStaffNode = clefNode.getAttributes().getNamedItem(MusicXmlTags.CLEF_STAFF);
			if (clefStaffNode != null) {
				staffNumber = Integer.parseInt(clefStaffNode.getTextContent());
			}

			final Context context = contexts.get(staffNumber);
			context.setClef(MusicXmlReaderDom.this.getClef(clefNode));
		}
	}

	/**
	 * Get the number of divisions from the Node.
	 */
	private int getDivisions(Node attributesNode, int previousDivisions) {
		return DocHelper.findChild(attributesNode, MusicXmlTags.MEAS_ATTR_DIVS)
				.map(node -> Integer.parseInt(node.getTextContent()))
				.orElse(previousDivisions);
	}

	/**
	 * Get the KeySignature defined in the Node.
	 */
	private KeySignature getKeySig(Node attributesNode, Context previous) {
		return DocHelper.findChild(attributesNode, MusicXmlTags.MEAS_ATTR_KEY)
				.flatMap(keySigNode -> DocHelper.findChild(keySigNode, MusicXmlTags.MEAS_ATTR_KEY_FIFTHS))
				.map(fifthsNode -> keyFromAlterations(Integer.parseInt(fifthsNode.getTextContent())))
				.orElse(previous.getKeySig());
	}

	/**
	 * Get TimeSignature from Node if it is for staff with staffNumber.
	 */
	private TimeSignature getTimeSig(Node attributesNode, Context previous, int staffNumber) {
		final TimeSignature timeSig;
		final Optional<Node> timeSigNode = DocHelper.findChild(attributesNode, MusicXmlTags.MEAS_ATTR_TIME);
		if (timeSigNode.isPresent()) {
			TimeSignature.Symbol symbol = getTimeSigSymbol(timeSigNode.get());

			final int beats = DocHelper.findChild(timeSigNode.get(), MusicXmlTags.MEAS_ATTR_BEATS)
					.map(beatsNode -> Integer.parseInt(beatsNode.getTextContent()))
					.orElseThrow();

			final int beatDenominator = DocHelper.findChild(timeSigNode.get(), MusicXmlTags.MEAS_ATTR_BEAT_TYPE)
					.map(beatTypeNode -> Integer.parseInt(beatTypeNode.getTextContent()))
					.orElseThrow();

			final Duration beatType = Duration.of(1, beatDenominator);

			final Node staffNumberNode = timeSigNode.get().getAttributes()
					.getNamedItem(MusicXmlTags.MEAS_ATTR_STAFF_NUMBER);
			if (staffNumberNode != null) {
				final int staffNumberAttr = Integer.parseInt(staffNumberNode.getTextContent());
				if (staffNumberAttr == staffNumber) {
					timeSig = TimeSignature.of(beats, beatType, symbol);
				} else {
					timeSig = previous.getTimeSig();
				}
			} else {
				timeSig = TimeSignature.of(beats, beatType, symbol);
			}
		} else {
			timeSig = previous.getTimeSig();
		}

		return timeSig;
	}

	private TimeSignature.Symbol getTimeSigSymbol(Node timeSigNode) {
		Optional<String> timeSigSymbol = DocHelper.getAttributeValue(timeSigNode, MusicXmlTags.MEAS_ATTR_TIME_SYMBOL);

		TimeSignature.Symbol symbol = TimeSignature.Symbol.NUMERIC;

		if (timeSigSymbol.isPresent()) {
			switch (timeSigSymbol.get()) {
				case MusicXmlTags.MEAS_ATTR_TIME_COMMON:
					symbol = TimeSignature.Symbol.COMMON;
					break;
				case MusicXmlTags.MEAS_ATTR_TIME_CUT:
					symbol = TimeSignature.Symbol.CUT_TIME;
					break;
				case MusicXmlTags.MEAS_ATTR_TIME_NUMERATOR:
					symbol = TimeSignature.Symbol.BEAT_NUMBER_ONLY;
					break;
				case MusicXmlTags.MEAS_ATTR_TIME_NOTE:
					symbol = TimeSignature.Symbol.BEAT_DURATION_AS_NOTE;
					break;
				case MusicXmlTags.MEAS_ATTR_TIME_DOTTED_NOTE:
					symbol = TimeSignature.Symbol.BEAT_DURATION_AS_DOTTED_NOTE;
					break;
				default:
					symbol = TimeSignature.Symbol.NUMERIC;
			}
		}

		return symbol;
	}

	/**
	 * Get the KeySignature based on number of alterations.
	 */
	private KeySignature keyFromAlterations(int alterations) {
		switch (alterations) {
			case 0:
				return KeySignatures.CMAJ_AMIN;
			case 1:
				return KeySignatures.GMAJ_EMIN;
			case 2:
				return KeySignatures.DMAJ_BMIN;
			case 3:
				return KeySignatures.AMAJ_FSHARPMIN;
			case 4:
				return KeySignatures.EMAJ_CSHARPMIN;
			case 5:
				return KeySignatures.BMAJ_GSHARPMIN;
			case 6:
				return KeySignatures.FSHARPMAJ_DSHARPMIN;

			case -1:
				return KeySignatures.FMAJ_DMIN;
			case -2:
				return KeySignatures.BFLATMAJ_GMIN;
			case -3:
				return KeySignatures.EFLATMAJ_CMIN;
			case -4:
				return KeySignatures.AFLATMAJ_FMIN;
			case -5:
				return KeySignatures.DFLATMAJ_BFLATMIN;
			case -6:
				return KeySignatures.GFLATMAJ_EFLATMIN;
		}

		return KeySignatures.CMAJ_AMIN;
	}

	/**
	 * Get Clef from clefNode.
	 */
	private Clef getClef(Node clefNode) {
		final String clefName = DocHelper.findChild(clefNode, MusicXmlTags.CLEF_SIGN)
				.map(clefSignNode -> clefSignNode.getTextContent())
				.orElse(MusicXmlTags.CLEF_G);

		final int clefLine = DocHelper.findChild(clefNode, MusicXmlTags.CLEF_LINE)
				.map(clefLineNode -> Integer.parseInt(clefLineNode.getTextContent()))
				.orElse(3);

		Clef.Symbol type = Clef.Symbol.G;
		switch (clefName) {
			case MusicXmlTags.CLEF_G:
				type = Clef.Symbol.G;
				break;
			case MusicXmlTags.CLEF_F:
				type = Clef.Symbol.F;
				break;
			case MusicXmlTags.CLEF_C:
				type = Clef.Symbol.C;
				break;
			case MusicXmlTags.CLEF_PERC:
				type = Clef.Symbol.PERCUSSION;
				break;
		}

		return Clef.of(type, clefLine);
	}

	private Barline getBarline(Node barlineNode) {
		if (barlineNode != null) {
			final String barlineString = DocHelper.findChild(barlineNode, MusicXmlTags.BARLINE_STYLE)
					.map(barlineStyleNode -> barlineStyleNode.getTextContent())
					.orElse("");

			final Optional<Node> repeatNode = DocHelper.findChild(barlineNode, MusicXmlTags.BARLINE_REPEAT);

			switch (barlineString) {
				case MusicXmlTags.BARLINE_STYLE_DASHED:
					return Barline.DASHED;
				case MusicXmlTags.BARLINE_STYLE_HEAVY:
					return Barline.THICK;
				case MusicXmlTags.BARLINE_STYLE_HEAVY_LIGHT:
					return Barline.REPEAT_LEFT;
				case MusicXmlTags.BARLINE_STYLE_INVISIBLE:
					return Barline.INVISIBLE;
				case MusicXmlTags.BARLINE_STYLE_LIGHT_HEAVY: {
					if (!repeatNode.isPresent()) {
						return Barline.FINAL;
					} else {
						return Barline.REPEAT_RIGHT;
					}
				}
				case MusicXmlTags.BARLINE_STYLE_LIGHT_LIGHT:
					return Barline.DOUBLE;
				default:
					return Barline.SINGLE;
			}
		}

		return Barline.NONE;
	}

	private boolean hasChordTag(Node noteNode) {
		return DocHelper.findChild(noteNode, MusicXmlTags.NOTE_CHORD).isPresent();
	}

	private boolean isRest(Node noteNode) {
		return DocHelper.findChild(noteNode, MusicXmlTags.NOTE_REST).isPresent();
	}

	private int getVoice(Node noteNode) {
		return DocHelper.findChild(noteNode, MusicXmlTags.NOTE_VOICE)
				.map(voiceNode -> Integer.parseInt(voiceNode.getTextContent()))
				.orElse(1);
	}

	private Pitch getPitch(Node noteNode) {
		Pitch pitch = null;

		final Optional<Node> pitchNode = DocHelper.findChild(noteNode, MusicXmlTags.NOTE_PITCH);

		if (pitchNode.isPresent()) {
			final Pitch.Base pitchBase = DocHelper.findChild(pitchNode.get(), MusicXmlTags.PITCH_STEP)
					.map(stepNode -> getPitchBase(stepNode))
					.orElse(null);

			final int octave = DocHelper.findChild(pitchNode.get(), MusicXmlTags.PITCH_OCT)
					.map(octaveNode -> Integer.parseInt(octaveNode.getTextContent()))
					.orElse(0);

			final int alter = DocHelper.findChild(pitchNode.get(), MusicXmlTags.PITCH_ALTER)
					.map(alterNode -> Integer.parseInt(alterNode.getTextContent()))
					.orElse(0);

			pitch = Pitch.of(pitchBase, getAccidental(alter), octave);
		} else {
			final Optional<Node> unpitchedNode = DocHelper.findChild(noteNode, MusicXmlTags.NOTE_UNPITCHED);
			if (unpitchedNode.isPresent()) {
				final Optional<Node> stepNode = DocHelper.findChild(unpitchedNode.get(), MusicXmlTags.UNPITCHED_STEP);
				final Optional<Node> octaveNode = DocHelper.findChild(unpitchedNode.get(),
						MusicXmlTags.UNPITCHED_OCTAVE);

				if (stepNode.isPresent() && octaveNode.isPresent()) {
					final Pitch.Base pitchBase = getPitchBase(stepNode.get());
					final int octave = Integer.parseInt(octaveNode.get().getTextContent());
					pitch = Pitch.of(pitchBase, Pitch.Accidental.NATURAL, octave);
				}
			}
		}

		return pitch;
	}

	private Pitch.Accidental getAccidental(int alter) {
		switch (alter) {
			case 0:
				return Pitch.Accidental.NATURAL;
			case 1:
				return Pitch.Accidental.SHARP;
			case 2:
				return Pitch.Accidental.DOUBLE_SHARP;
			case -1:
				return Pitch.Accidental.FLAT;
			case -2:
				return Pitch.Accidental.DOUBLE_FLAT;
		}

		LOG.warn("Unsupported alter value for pitch " + alter);
		return Pitch.Accidental.NATURAL;
	}

	private Pitch.Base getPitchBase(Node stepNode) {
		final String pitchString = stepNode.getTextContent();

		if (pitchString != null) {
			switch (pitchString) {
				case "C":
					return Pitch.Base.C;
				case "D":
					return Pitch.Base.D;
				case "E":
					return Pitch.Base.E;
				case "F":
					return Pitch.Base.F;
				case "G":
					return Pitch.Base.G;
				case "A":
					return Pitch.Base.A;
				case "B":
					return Pitch.Base.B;
			}
		}

		return null;
	}

	private Duration getDuration(Node nodeWithDuration, int divisions) {
		final Optional<Node> durationNode = DocHelper.findChild(nodeWithDuration, MusicXmlTags.NOTE_DURATION);

		final int dotCount = DocHelper.findChildren(nodeWithDuration, MusicXmlTags.DOT).size();

		if (durationNode.isPresent()) {
			final int nominator = Integer.parseInt(durationNode.get().getTextContent());

			// Backup elements may have 0 duration, therefore this must be checked.
			if (nominator != 0) {

				int tupletDivisor = 1;
				final Optional<Node> timeModificationNode = DocHelper
						.findChild(nodeWithDuration, MusicXmlTags.TIME_MODIFICATION);
				if (timeModificationNode.isPresent()) {
					Optional<Node> actualNotes = DocHelper
							.findChild(timeModificationNode.get(), MusicXmlTags.TIME_MODIFICATION_ACTUAL_NOTES);
					if (actualNotes.isPresent()) {
						tupletDivisor = Integer.parseInt(actualNotes.get().getTextContent());
					}
				}

				// In MusicXml divisions is the number of parts into which a quarter note
				// is divided. Therefore divisions needs to be multiplied by 4.
				return Duration.of(nominator, divisions * 4, dotCount, tupletDivisor);
			}
		}

		return null;
	}

	/**
	 * Class for handling the reading of chords.
	 */
	private class ChordBuffer {
		private List<NoteBuilder> chordBuffer = new ArrayList<>();
		private int voice;
		private Notation.Type arpeggiation;

		ChordBuffer() {
		}

		void addNote(NoteBuilder noteBuilder, int voice) {
			this.chordBuffer.add(noteBuilder);
			this.voice = voice;
		}

		void contentsToBuilder(MeasureBuilder builder) {
			if (!this.chordBuffer.isEmpty()) {
				if (this.chordBuffer.size() > 1) {
					if (arpeggiation != null) {
						Notation arpeggio = Notation.of(arpeggiation);
						chordBuffer.sort(Comparator.comparing(NoteBuilder::getPitch));

						NoteBuilder prev = null;

						for (NoteBuilder note : chordBuffer) {
							if (prev != null) {
								prev.connectWith(arpeggio, note);
							}

							prev = note;
						}
					}

					builder.addToVoice(this.voice, new ChordBuilder(chordBuffer));
				} else if (this.chordBuffer.size() == 1) {
					final NoteBuilder noteBuilder = this.chordBuffer.get(0);
					builder.addToVoice(this.voice, noteBuilder);
				}

				this.chordBuffer = new ArrayList<>();
				this.arpeggiation = null;
			}
		}

		void setArpeggiation(Notation.Type arpeggiation) {
			this.arpeggiation = arpeggiation;
		}
	}

	/**
	 * Class for keeping track of all the context dependent information that
	 * continue from measure to measure.
	 */
	private class Context {

		private int divisions;
		private KeySignature keySig = KeySignatures.CMAJ_AMIN;
		private TimeSignature timeSig;
		private Clef clef;
		private List<GraceNoteBuilder> unconnectedGraceNotes = new ArrayList<>();
		private NoteBuilder previousBuilder;

		Context() {
		}

		int getDivisions() {
			return divisions;
		}

		void setDivisions(int divisions) {
			this.divisions = divisions;
		}

		KeySignature getKeySig() {
			return keySig;
		}

		void setKeySig(KeySignature keySig) {
			this.keySig = keySig;
		}

		TimeSignature getTimeSig() {
			return timeSig;
		}

		void setTimeSig(TimeSignature timeSig) {
			this.timeSig = timeSig;
		}

		Clef getClef() {
			return clef;
		}

		void setClef(Clef clef) {
			this.clef = clef;
		}

		void addGraceNoteToBuffer(GraceNoteBuilder graceNoteBuilder) {
			unconnectedGraceNotes.add(graceNoteBuilder);
		}

		void setAppoggiaturaTypes(NoteBuilder builder) {
			if (unconnectedGraceNotes.size() != 1) {
				return;
			}

			final GraceNoteBuilder graceNoteBuilder = unconnectedGraceNotes.get(0);
			final int interval = builder.getPitch().toInt() - graceNoteBuilder.getPitch().toInt();
			// Consider as an appoggiatura grace notes that are at most one whole step away
			// and have a duration type that is half of the principal notes's duration. Also if
			// the acciaccatura type has already been set, then the grace note cannot be an appoggiatura.
			if (Math.abs(interval) > 2
					|| !builder.getDuration().divide(2).equals(graceNoteBuilder.getDisplayableDuration())
					|| graceNoteBuilder.getGraceNoteType().equals(Ornamental.Type.ACCIACCATURA)) {
				return;
			}

			graceNoteBuilder.setGraceNoteType(Ornamental.Type.APPOGGIATURA);
		}

		void setGraceNotesToNoteBuilder(NoteBuilder builder, boolean isPreceding) {
			if (unconnectedGraceNotes.isEmpty()) {
				return;
			}

			if (isPreceding) {
				setAppoggiaturaTypes(builder);
				builder.setPrecedingGraceNotes(unconnectedGraceNotes);
			} else {
				builder.setSucceedingGraceNotes(unconnectedGraceNotes);
			}

			unconnectedGraceNotes = new ArrayList<>();
		}

		NoteBuilder getPreviousBuilder() {
			return previousBuilder;
		}

		void setPreviousBuilder(NoteBuilder previousBuilder) {
			this.previousBuilder = previousBuilder;
		}
	}

	/**
	 * Class for keeping track of connected notations such as ties, slurs etc.
	 */
	private class ConnectedNotations {

		private final Map<Integer, Collection<UnresolvedNotation>> unresolvedNotationsPerVoice = new HashMap<>();

		boolean hasUnresolvedNotations(int voiceNumber) {
			return unresolvedNotationsPerVoice.containsKey(voiceNumber) && !unresolvedNotationsPerVoice.get(voiceNumber)
					.isEmpty();
		}

		UnresolvedNotation createAndAddStartOfNotation(int voiceNumber, int notationNumber, Notation.Type notationType,
				Notation.Style notationStyle, ConnectableBuilder firstBuilder) {
			if (!unresolvedNotationsPerVoice.containsKey(voiceNumber)) {
				unresolvedNotationsPerVoice.put(voiceNumber, new ArrayList<>());
			}

			UnresolvedNotation unresolvedNotation = new UnresolvedNotation(notationNumber, notationType, notationStyle);
			unresolvedNotation.addNoteBuilder(firstBuilder);
			unresolvedNotationsPerVoice.get(voiceNumber).add(unresolvedNotation);
			return unresolvedNotation;
		}

		void addToUnresolvedNotations(int voiceNumber, ConnectableBuilder builder,
				Set<UnresolvedNotation> notationsToIgnore) {
			unresolvedNotationsPerVoice.getOrDefault(voiceNumber, Collections.emptyList()).stream()
					.filter(unresolvedNotation -> !notationsToIgnore.contains(unresolvedNotation))
					.forEach(unresolvedNotation -> unresolvedNotation.addNoteBuilder(builder));
		}

		void endNotation(int voiceNumber, int notationNumber, Notation.Type notationType,
				ConnectableBuilder lastBuilder, Context context) {
			final Collection<UnresolvedNotation> notationsForVoice = unresolvedNotationsPerVoice
					.getOrDefault(voiceNumber, Collections.emptyList());

			Optional<UnresolvedNotation> unresolvedNotationToEndOpt = notationsForVoice
					.stream()
					.filter(unresolvedNotation -> unresolvedNotation.getNotationType().equals(notationType)
							&& unresolvedNotation.getNotationNumber() == notationNumber)
					.findFirst();

			if (unresolvedNotationToEndOpt.isPresent()) {
				UnresolvedNotation unresolvedNotationToEnd = unresolvedNotationToEndOpt.get();
				unresolvedNotationToEnd.addNoteBuilder(lastBuilder);
				unresolvedNotationToEnd.resolve(context);
				notationsForVoice.remove(unresolvedNotationToEnd);

			} else {
				LOG.warn("Cannot correctly resolve a notation that was started in a different voice."
						+ " Trying to resolve notation in voice "
						+ voiceNumber + " with type " + notationType + " and number " + notationNumber);
			}
		}
	}

	/*
	 * Class for keeping track of unresolved notations.
	 */
	private class UnresolvedNotation {

		private final int notationNumber;
		private final Notation.Type notationType;
		private final Notation.Style notationStyle;
		private final List<ConnectableBuilder> connectedNoteBuilders;

		UnresolvedNotation(int notationNumber, Notation.Type notationType, Notation.Style notationStyle) {
			this.notationNumber = notationNumber;
			this.notationType = notationType;
			this.notationStyle = notationStyle;
			this.connectedNoteBuilders = new ArrayList<>();
		}

		int getNotationNumber() {
			return notationNumber;
		}

		Notation.Type getNotationType() {
			return notationType;
		}

		void addNoteBuilder(ConnectableBuilder builder) {
			connectedNoteBuilders.add(builder);
		}

		void resolve(Context context) {
			final Notation notation = Notation.of(notationType, notationStyle);
			final int indexOfLast = connectedNoteBuilders.size() - 1;
			for (int i = 0; i < indexOfLast; ++i) {
				ConnectableBuilder nextBuilder = connectedNoteBuilders.get(i + 1);
				if (nextBuilder instanceof NoteBuilder) {
					connectedNoteBuilders.get(i).connectWith(notation, (NoteBuilder) nextBuilder);
				} else if (nextBuilder instanceof GraceNoteBuilder) {
					connectedNoteBuilders.get(i).connectWith(notation, (GraceNoteBuilder) nextBuilder);
				}
			}

			// If the notation ends on a grace note, check if the grace notes should be
			// added as succeeding grace notes to a NoteBuilder.
			if (connectedNoteBuilders.get(indexOfLast) instanceof GraceNoteBuilder) {
				for (int i = indexOfLast; i >= 0; --i) {
					ConnectableBuilder builder = connectedNoteBuilders.get(i);
					if (builder instanceof NoteBuilder) {
						context.setGraceNotesToNoteBuilder((NoteBuilder) builder, false);
						break;
					}
				}
			}
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			if (!(o instanceof UnresolvedNotation)) {
				return false;
			}

			UnresolvedNotation other = (UnresolvedNotation) o;
			return notationNumber == other.notationNumber
					&& notationType.equals(other.notationType);
		}

		@Override
		public int hashCode() {
			return Objects.hash(notationNumber, notationType);
		}
	}
}
