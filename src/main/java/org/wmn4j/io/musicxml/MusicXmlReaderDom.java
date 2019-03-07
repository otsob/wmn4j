/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wmn4j.io.IOLogger;
import org.wmn4j.notation.builders.ChordBuilder;
import org.wmn4j.notation.builders.MeasureBuilder;
import org.wmn4j.notation.builders.NoteBuilder;
import org.wmn4j.notation.builders.PartBuilder;
import org.wmn4j.notation.builders.RestBuilder;
import org.wmn4j.notation.builders.ScoreBuilder;
import org.wmn4j.notation.elements.Articulation;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.KeySignature;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Score;
import org.wmn4j.notation.elements.SingleStaffPart;
import org.wmn4j.notation.elements.TimeSignature;
import org.xml.sax.SAXException;

/**
 * A parser for MusicXML files.
 *
 * @author Otso Björklund
 */
class MusicXmlReaderDom implements MusicXmlReader {

	private static final int MIN_STAFF_NUMBER = SingleStaffPart.STAFF_NUMBER;
	private static final int DEFAULT_STAFF_COUNT = 1;

	private final IOLogger logger = new IOLogger();
	private final boolean validateInput;

	/**
	 * Constructor that allows setting validation.
	 *
	 * @param validateInput Whether this validates MusicXML files given as input.
	 */
	MusicXmlReaderDom(boolean validateInput) {
		this.validateInput = validateInput;
	}

	private DocumentBuilder createAndConfigureDocBuilder() throws ParserConfigurationException {

		// TODO: Check the parser configuration.
		final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		dbf.setNamespaceAware(true);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		return dbf.newDocumentBuilder();
	}

	private boolean isMusicXmlFileValid(File musicXmlFile) {

		// TODO: Make this work.
		final SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			final Schema schema = schemaFactory.newSchema(MusicXmlReader.SCHEMA.toFile());

			final Validator validator = schema.newValidator();
			validator.validate(new StreamSource(musicXmlFile));
		} catch (IOException | SAXException e) {
			this.logger.log(musicXmlFile.toString() + " is NOT valid reason:" + e);
			return false;
		}

		return true;
	}

	@Override
	public Score readScore(Path filePath) throws IOException {
		return scoreBuilderFromFile(filePath).build();
	}

	@Override
	public ScoreBuilder scoreBuilderFromFile(Path filePath) throws IOException {
		final ScoreBuilder scoreBuilder = new ScoreBuilder();
		final File musicXmlFile = filePath.toFile();

		if (this.validateInput && !isMusicXmlFileValid(musicXmlFile)) {
			throw new IOException("File at " + filePath.toString() + " is not a valid MusicXML file");
		}

		try {
			final DocumentBuilder docBuilder = createAndConfigureDocBuilder();

			try {
				final Document musicXmlDoc = docBuilder.parse(musicXmlFile);
				readScoreToBuilder(scoreBuilder, musicXmlDoc);
			} catch (final SAXException ex) {
				throw new IOException("Parsing failed with exception " + ex.toString());
			}
		} catch (final ParserConfigurationException e) {
			throw new IOException("Parser configuration failed");
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
		// TODO: Extend this when the attributes of a score are increased.
		final Node movementTitle = doc.getElementsByTagName(MusicXmlTags.SCORE_MOVEMENT_TITLE).item(0);
		if (movementTitle != null) {
			scoreBuilder.setAttribute(Score.Attribute.NAME, movementTitle.getTextContent());
		}

		final Node identification = doc.getElementsByTagName(MusicXmlTags.SCORE_IDENTIFICATION).item(0);

		DocHelper.findChild(identification, MusicXmlTags.SCORE_IDENTIFICATION_CREATOR)
				.ifPresent(creatorNode -> scoreBuilder.setAttribute(Score.Attribute.COMPOSER,
						creatorNode.getTextContent()));
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
	 * @param partsList
	 * @return Map with part ids as keys and corresponding PartBuilders as values.
	 */
	private Map<String, PartBuilder> createPartBuilders(Node partsList) {
		final Map<String, PartBuilder> partBuilders = new HashMap<>();

		// Read part attributes.
		if (partsList != null) {
			final NodeList scoreParts = partsList.getChildNodes();
			for (int i = 0; i < scoreParts.getLength(); ++i) {
				final Node child = scoreParts.item(i);
				if (child.getNodeName().equals(MusicXmlTags.PLIST_SCORE_PART)) {
					final String partId = child.getAttributes().getNamedItem(MusicXmlTags.PART_ID).getTextContent();

					final String partName = DocHelper.findChild(child, MusicXmlTags.PART_NAME)
							.map(partNameNode -> partNameNode.getTextContent())
							.orElse(partId);

					partBuilders.put(partId, new PartBuilder(partName));
					// TODO: read the other part attributes into the partBuilder.
				}
			}
		}

		return partBuilders;
	}

	/**
	 * Go through the measures in the part and add them to the PartBuilder.
	 */
	private void readMeasuresIntoPartBuilder(PartBuilder partBuilder, Node partNode) {

		final int staves = getNumberOfStaves(partNode);
		final Map<Integer, Context> contexts = new HashMap<>();

		// Create the context containers for the staves.
		for (int staffNumber = MIN_STAFF_NUMBER; staffNumber < staves + MIN_STAFF_NUMBER; ++staffNumber) {
			contexts.put(staffNumber, new Context());
		}

		// Read measure node by node, create measure and add to list
		final NodeList measureNodes = partNode.getChildNodes();

		// Used for keeping track of possible tie beginnings.
		final TieBeginningContainer tieBeginnings = new TieBeginningContainer();

		for (int i = 0; i < measureNodes.getLength(); ++i) {
			final Node measureNode = measureNodes.item(i);

			// Make sure that the node really is a measure node.
			if (measureNode.getNodeName().equals(MusicXmlTags.MEASURE)) {
				readMeasureIntoPartBuilder(partBuilder, measureNode, contexts, staves, tieBeginnings);
			}
		}
	}

	/**
	 * Find the number of staves in the part.
	 */
	private int getNumberOfStaves(Node partNode) {

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
			int staves, TieBeginningContainer tieBuffer) {

		final int measureNumber = Integer
				.parseInt(measureNode.getAttributes().getNamedItem(MusicXmlTags.MEASURE_NUM).getTextContent());

		final Map<Integer, MeasureBuilder> measureBuilders = new HashMap<>();
		final Map<Integer, ChordBuffer> chordBuffers = new HashMap<>();
		final Map<Integer, List<Duration>> offsets = new HashMap<>();
		final Map<Integer, Clef> lastClefs = new HashMap<>();

		// Initialize the helper data structures.
		for (int staff = SingleStaffPart.STAFF_NUMBER; staff <= staves; ++staff) {
			measureBuilders.put(staff, new MeasureBuilder(measureNumber));
			chordBuffers.put(staff, new ChordBuffer());
			offsets.put(staff, new ArrayList<>());
			lastClefs.put(staff, contexts.get(staff).getClef());
		}

		final NodeList measureChildren = measureNode.getChildNodes();
		for (int i = 0; i < measureChildren.getLength(); ++i) {
			final Node node = measureChildren.item(i);

			// Handle measure attributes that occur in the beginning of measure
			if (offsets.get(1).isEmpty() && node.getNodeName().equals(MusicXmlTags.MEASURE_ATTRIBUTES)) {
				updateContexts(node, contexts);

				for (Integer staff : contexts.keySet()) {
					lastClefs.put(staff, contexts.get(staff).getClef());
				}
			}

			// Handle note element
			if (node.getNodeName().equals(MusicXmlTags.NOTE) && !isGraceNote(node)) {
				handleNoteElement(node, measureBuilders, chordBuffers, offsets, contexts, tieBuffer);
			}

			// Handle clef changes
			if (node.getNodeName().equals(MusicXmlTags.MEASURE_ATTRIBUTES)) {
				addClefChange(node, measureBuilders, offsets, lastClefs);
			}

			// Handle barlines
			if (node.getNodeName().equals(MusicXmlTags.BARLINE)) {
				addBarline(node, measureBuilders);
			}
		}

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

	private void addClefChange(Node node, Map<Integer, MeasureBuilder> measureBuilders,
			Map<Integer, List<Duration>> offsets, Map<Integer, Clef> lastClefs) {
		Clef clef = null;
		final Optional<Node> clefNode = DocHelper.findChild(node, MusicXmlTags.CLEF);
		int clefStaff = 1;

		if (clefNode.isPresent()) {
			clef = MusicXmlReaderDom.this.getClef(clefNode.get());
			final NamedNodeMap clefAttributes = clefNode.get().getAttributes();
			final Node clefStaffNode = clefAttributes.getNamedItem(MusicXmlTags.CLEF_STAFF);
			if (clefStaffNode != null) {
				clefStaff = Integer.parseInt(clefStaffNode.getTextContent());
			}
		}
		if (clef != null && !offsets.get(clefStaff).isEmpty()) {
			final Duration cumulatedDur = Duration.sumOf(offsets.get(clefStaff));
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
	 * Handle a note element. A note element can be a rest, note, grace note, or a
	 * note in a chord.
	 */
	private void handleNoteElement(Node node, Map<Integer, MeasureBuilder> measureBuilders,
			Map<Integer, ChordBuffer> chordBuffers, Map<Integer, List<Duration>> offsets,
			Map<Integer, Context> contexts, TieBeginningContainer tieBeginnings) {
		final int staffNumber = DocHelper.findChild(node, MusicXmlTags.NOTE_STAFF)
				.map(staffNode -> Integer.parseInt(staffNode.getTextContent()))
				.orElse(MIN_STAFF_NUMBER);

		final Context context = contexts.get(staffNumber);
		final MeasureBuilder builder = measureBuilders.get(staffNumber);

		final int voice = getVoice(node);
		final Duration duration = getDuration(node, context.getDivisions());
		offsets.get(staffNumber).add(duration);

		if (isRest(node)) {
			chordBuffers.get(staffNumber).contentsToBuilder(builder);
			builder.addToVoice(voice, new RestBuilder(duration));
		} else {
			final Pitch pitch = getPitch(node);
			final NoteBuilder noteBuilder = new NoteBuilder(pitch, duration);

			// Handle ties
			if (endsTie(node)) {
				final NoteBuilder tieBeginner = tieBeginnings.popMatchingBeginningFromStaff(staffNumber, noteBuilder);
				if (tieBeginner != null) {
					tieBeginner.addTieToFollowing(noteBuilder);
				} else {
					noteBuilder.setIsTiedFromPrevious(true);
				}
			}

			if (startsTie(node)) {
				tieBeginnings.addToStaff(staffNumber, noteBuilder);
			}

			DocHelper.findChild(node, MusicXmlTags.NOTATIONS)
					.ifPresent(notationsNode -> addNotations(notationsNode, noteBuilder));

			if (hasChordTag(node)) {
				chordBuffers.get(staffNumber).addNote(noteBuilder, voice);
			} else {
				chordBuffers.get(staffNumber).contentsToBuilder(builder);
				chordBuffers.get(staffNumber).addNote(noteBuilder, voice);
			}
		}
	}

	private void addNotations(Node notationsNode, NoteBuilder noteBuilder) {
		final Optional<Node> articulationsNode = DocHelper.findChild(notationsNode, MusicXmlTags.NOTE_ARTICULATIONS);

		if (articulationsNode.isPresent()) {
			for (int i = 0; i < articulationsNode.get().getChildNodes().getLength(); ++i) {
				final Node articulationNode = articulationsNode.get().getChildNodes().item(i);
				final Articulation articulation = getArticulation(articulationNode.getNodeName());
				if (articulation != null) {
					noteBuilder.addArticulation(articulation);
				} else {
					this.logger.log("No Articulation for node with name " + articulationNode.getNodeName());
				}
			}
		}

		DocHelper.findChild(notationsNode, MusicXmlTags.FERMATA)
				.ifPresent(fermataNode -> noteBuilder.addArticulation(Articulation.FERMATA));
	}

	private Articulation getArticulation(String articulationString) {
		switch (articulationString) {
		case MusicXmlTags.ACCENT:
			return Articulation.ACCENT;
		case MusicXmlTags.STACCATO:
			return Articulation.STACCATO;
		case MusicXmlTags.TENUTO:
			return Articulation.TENUTO;
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
			final int beats = DocHelper.findChild(timeSigNode.get(), MusicXmlTags.MEAS_ATTR_BEATS)
					.map(beatsNode -> Integer.parseInt(beatsNode.getTextContent()))
					.orElse(null);

			final int beatType = DocHelper.findChild(timeSigNode.get(), MusicXmlTags.MEAS_ATTR_BEAT_TYPE)
					.map(beatTypeNode -> Integer.parseInt(beatTypeNode.getTextContent()))
					.orElse(null);

			final Node staffNumberNode = timeSigNode.get().getAttributes()
					.getNamedItem(MusicXmlTags.MEAS_ATTR_STAFF_NUMBER);
			if (staffNumberNode != null) {
				final int staffNumberAttr = Integer.parseInt(staffNumberNode.getTextContent());
				if (staffNumberAttr == staffNumber) {
					timeSig = TimeSignature.getTimeSignature(beats, beatType);
				} else {
					timeSig = previous.getTimeSig();
				}
			} else {
				timeSig = TimeSignature.getTimeSignature(beats, beatType);
			}
		} else {
			timeSig = previous.getTimeSig();
		}

		return timeSig;
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

		return Clef.getClef(type, clefLine);
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

	private boolean isGraceNote(Node noteNode) {
		return DocHelper.findChild(noteNode, MusicXmlTags.NOTE_GRACE_NOTE).isPresent();
	}

	private boolean hasChordTag(Node noteNode) {
		return DocHelper.findChild(noteNode, MusicXmlTags.NOTE_CHORD).isPresent();
	}

	private boolean startsTie(Node noteNode) {
		return hasTieWithType(noteNode, MusicXmlTags.TIE_START);
	}

	private boolean endsTie(Node noteNode) {
		return hasTieWithType(noteNode, MusicXmlTags.TIE_STOP);
	}

	private boolean hasTieWithType(Node noteNode, String tieType) {
		final List<Node> tieNodes = DocHelper.findChildren(noteNode, MusicXmlTags.TIE);
		if (tieNodes.isEmpty()) {
			return false;
		}

		return tieNodes.stream().anyMatch((tieNode) -> tieNode.getAttributes().getNamedItem(MusicXmlTags.TIE_TYPE)
				.getTextContent().equals(tieType));

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

			pitch = Pitch.getPitch(pitchBase, alter, octave);
		} else {
			final Optional<Node> unpitchedNode = DocHelper.findChild(noteNode, MusicXmlTags.NOTE_UNPITCHED);
			if (unpitchedNode.isPresent()) {
				final Optional<Node> stepNode = DocHelper.findChild(unpitchedNode.get(), MusicXmlTags.UNPITCHED_STEP);
				final Optional<Node> octaveNode = DocHelper.findChild(unpitchedNode.get(),
						MusicXmlTags.UNPITCHED_OCTAVE);

				if (stepNode.isPresent() && octaveNode.isPresent()) {
					final Pitch.Base pitchBase = getPitchBase(stepNode.get());
					final int octave = Integer.parseInt(octaveNode.get().getTextContent());
					pitch = Pitch.getPitch(pitchBase, 0, octave);
				}
			}
		}

		return pitch;
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

	private Duration getDuration(Node noteNode, int divisions) {
		final Optional<Node> durationNode = DocHelper.findChild(noteNode, MusicXmlTags.NOTE_DURATION);
		if (durationNode.isPresent()) {
			final int nominator = Integer.parseInt(durationNode.get().getTextContent());
			// In MusicXml divisions is the number of parts into which a quarter note
			// is divided. Therefore divisions needs to be multiplied by 4.
			return Duration.getDuration(nominator, divisions * 4);
		}

		return null;
	}

	/**
	 * Class for handling the reading of chords.
	 */
	private class ChordBuffer {
		private final List<NoteBuilder> chordBuffer = new ArrayList<>();
		private int voice;

		ChordBuffer() {
		}

		void addNote(NoteBuilder noteBuilder, int voice) {
			this.chordBuffer.add(noteBuilder);
			this.voice = voice;
		}

		void contentsToBuilder(MeasureBuilder builder) {
			if (!this.chordBuffer.isEmpty()) {
				if (this.chordBuffer.size() > 1) {
					final List<NoteBuilder> notes = new ArrayList<>();
					for (NoteBuilder noteBuilder : this.chordBuffer) {
						notes.add(noteBuilder);
					}

					builder.addToVoice(this.voice, new ChordBuilder(notes));
				} else if (this.chordBuffer.size() == 1) {
					final NoteBuilder noteBuilder = this.chordBuffer.get(0);
					builder.addToVoice(this.voice, noteBuilder);
				}

				this.chordBuffer.clear();
			}
		}
	}

	/**
	 * Class for keeping track of all the context dependent information that
	 * continue from measure to measure.
	 */
	private class Context {

		private int divisions;
		private KeySignature keySig;
		private TimeSignature timeSig;
		private Clef clef;

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
	}

	/**
	 * Class for keeping track of tie beginning NoteBuilders.
	 */
	private class TieBeginningContainer {

		private final Map<Integer, List<NoteBuilder>> tieStarts = new HashMap<>();

		void addToStaff(int staffNumber, NoteBuilder builder) {
			if (!this.tieStarts.containsKey(staffNumber)) {
				this.tieStarts.put(staffNumber, new ArrayList<>());
			}

			this.tieStarts.get(staffNumber).add(builder);
		}

		NoteBuilder popMatchingBeginningFromStaff(int staff, NoteBuilder builder) {
			NoteBuilder matching = null;

			if (this.tieStarts.keySet().contains(staff)) {
				for (int i = 0; i < this.tieStarts.get(staff).size(); ++i) {
					final NoteBuilder b = this.tieStarts.get(staff).get(i);
					if (b.getPitch().equals(builder.getPitch())) {
						matching = this.tieStarts.get(staff).remove(i);
						break;
					}
				}
			}

			return matching;
		}
	}
}
