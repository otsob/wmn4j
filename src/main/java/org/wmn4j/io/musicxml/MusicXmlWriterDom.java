/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.wmn4j.Wmn4j;
import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.GraceNote;
import org.wmn4j.notation.GraceNoteChord;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.MultiStaffPart;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Ornament;
import org.wmn4j.notation.Ornamental;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignature;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.math.BigInteger;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Abstract super class for classes that write to MusicXML by populating
 * the Document Object Model first.
 */
abstract class MusicXmlWriterDom implements MusicXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(MusicXmlWriterDom.class);
	private static final String MUSICXML_VERSION_NUMBER = "3.1";

	private final Document doc;
	private final SortedMap<String, Part> partIdMap = new TreeMap<>();
	private final NotationResolver notationResolver;

	MusicXmlWriterDom() {
		this.doc = createDocument();
		this.notationResolver = new NotationResolver(getDocument());
	}

	private static Document createDocument() {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			return docBuilder.newDocument();
		} catch (ParserConfigurationException exception) {
			LOG.error("Parser configuration failed: ", exception);
			return null;
		}
	}

	protected final Document getDocument() {
		return doc;
	}

	protected final void addPartWithId(String id, Part part) {
		partIdMap.put(id, part);
	}

	protected abstract int getDivisions();

	protected String getAnnotation(Measure measure) {
		return "";
	}

	@Override
	public void write(Path path) {
		try {
			final Element rootElement = getDocument().createElement(MusicXmlTags.SCORE_PARTWISE);
			rootElement.setAttribute(MusicXmlTags.MUSICXML_VERSION, MUSICXML_VERSION_NUMBER);
			getDocument().appendChild(rootElement);

			// Add the score attribute elements
			writeScoreAttributes(rootElement);

			// staff elements
			writePartList(rootElement);

			for (String partId : this.partIdMap.keySet()) {
				writePart(this.partIdMap.get(partId), rootElement, partId);
			}

			// write the content into xml file
			final TransformerFactory transformerFactory = TransformerFactory.newInstance();
			final Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			final DOMImplementation domImpl = getDocument().getImplementation();
			final DocumentType doctype = domImpl.createDocumentType("doctype",
					"-//Recordare//DTD MusicXML " + MUSICXML_VERSION_NUMBER + " Partwise//EN",
					"http://www.musicxml.org/dtds/partwise.dtd");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

			final DOMSource source = new DOMSource(doc);
			final StreamResult result = new StreamResult(new File(path.toString()));

			transformer.transform(source, result);
		} catch (final TransformerException tfe) {
			LOG.error("Configuring transformer failed:", tfe);
		}
	}

	protected final int computeDivisions(Iterator<Durational> durationalIterator) {
		Set<Integer> denominators = new HashSet<>();

		while (durationalIterator.hasNext()) {
			denominators.add(durationalIterator.next().getDuration().getDenominator());
		}

		//Find lowest common denominator of all the different denominators of Durationals in the Score
		//Start with the denominator of a quarter, because in MusicXML divisions are set in terms of
		//divisions per quarter note
		int lcd = Durations.QUARTER.getDenominator();
		for (Integer denominator : denominators) {
			lcd = (lcd * denominator)
					/ BigInteger.valueOf(lcd).gcd(BigInteger.valueOf(denominator)).intValue();
		}

		return lcd / Durations.QUARTER.getDenominator();
	}

	protected abstract void writeScoreAttributes(Element rootElement);

	protected abstract Element createIdentificationElement();

	protected abstract void writePartList(Element scoreRoot);

	protected void writePart(Part part, Element scoreRoot, String partId) {
		final Element partElement;

		if (part.isMultiStaff()) {
			partElement = createMultiStaffPart((MultiStaffPart) part, partId);
		} else {
			partElement = createSingleStaffPart((SingleStaffPart) part, partId);
		}

		scoreRoot.appendChild(partElement);
	}

	protected final Element createEncodingElement() {

		final Element encodingElement = getDocument().createElement(MusicXmlTags.ENCODING);
		final Element softwareElement = getDocument().createElement(MusicXmlTags.SOFTWARE);
		softwareElement.setTextContent(Wmn4j.getNameWithVersion());
		encodingElement.appendChild(softwareElement);

		final Element encodingDateElement = getDocument().createElement(MusicXmlTags.ENCODING_DATE);
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		encodingDateElement.setTextContent(dateFormat.format(new Date()));

		encodingElement.appendChild(encodingDateElement);

		return encodingElement;
	}

	protected Element createSingleStaffPart(SingleStaffPart part, String partId) {
		final Element partElement = getDocument().createElement(MusicXmlTags.PART);
		partElement.setAttribute(MusicXmlTags.PART_ID, partId);

		Measure previousMeasure = null;

		for (Measure measure : part) {
			partElement.appendChild(createMeasureElement(measure, previousMeasure));
			previousMeasure = measure;
		}

		return partElement;
	}

	protected Element createMultiStaffPart(MultiStaffPart part, String partId) {
		final Element partElement = getDocument().createElement(MusicXmlTags.PART);
		partElement.setAttribute(MusicXmlTags.PART_ID, partId);

		Map<Integer, Measure> previousMeasures = Collections.emptySortedMap();

		List<Integer> staffNumbers = part.getStaffNumbers();
		final int firstMeasureNumber = part.getStaff(staffNumbers.get(0)).hasPickupMeasure() ? 0 : 1;

		for (int measureNumber = firstMeasureNumber; measureNumber <= part.getFullMeasureCount(); ++measureNumber) {
			SortedMap<Integer, Measure> measures = new TreeMap<>();

			for (Integer staffNumber : staffNumbers) {
				Staff staff = part.getStaff(staffNumber);
				if (measureNumber <= staff.getFullMeasureCount()) {
					measures.put(staffNumber, part.getMeasure(staffNumber, measureNumber));
				}
			}

			partElement.appendChild(createMultiStaffMeasureElement(measures, previousMeasures));
			previousMeasures = measures;
		}

		return partElement;
	}

	private Element createMeasureElement(Measure measure, Measure previousMeasure) {
		final Element measureElement = getDocument().createElement(MusicXmlTags.MEASURE);
		measureElement.setAttribute(MusicXmlTags.MEASURE_NUM, Integer.toString(measure.getNumber()));

		addNewSystemStartAndAnnotationsIfNeeded(measureElement, measure);

		//Left barline
		if (!measure.getLeftBarline().equals(Barline.SINGLE) && !measure.getLeftBarline().equals(Barline.NONE)) {
			measureElement.appendChild(
					createBarlineElement(measure.getLeftBarline(), MusicXmlTags.BARLINE_LOCATION_LEFT));
		}

		//Attributes
		final Optional<Element> attributes = createMeasureAttributesElement(measure, previousMeasure);
		attributes.ifPresent(attrElement -> measureElement.appendChild(attrElement));

		//Set up handling possible mid-measure clef changes
		Map<Duration, Clef> undealtClefChanges = new HashMap<>(measure.getClefChanges());

		fillMeasureElement(measureElement, null, measure);

		//Right barline
		if (!measure.getRightBarline().equals(Barline.SINGLE) && !measure.getRightBarline().equals(Barline.NONE)) {
			measureElement.appendChild(
					createBarlineElement(measure.getRightBarline(), MusicXmlTags.BARLINE_LOCATION_RIGHT));
		}

		return measureElement;
	}

	private Element createMultiStaffMeasureElement(Map<Integer, Measure> measures, Map<Integer,
			Measure> previousMeasures) {
		final Element measureElement = getDocument().createElement(MusicXmlTags.MEASURE);

		final Measure measureForSharedValues = measures.values().iterator().next();

		measureElement.setAttribute(MusicXmlTags.MEASURE_NUM, Integer.toString(measureForSharedValues.getNumber()));

		addNewSystemStartAndAnnotationsIfNeeded(measureElement, measureForSharedValues);

		//Left barline
		if (!measureForSharedValues.getLeftBarline().equals(Barline.SINGLE) && !measureForSharedValues
				.getLeftBarline()
				.equals(Barline.NONE)) {
			measureElement.appendChild(
					createBarlineElement(measureForSharedValues.getLeftBarline(),
							MusicXmlTags.BARLINE_LOCATION_LEFT));
		}

		//Attributes
		final Optional<Element> attributes = createMeasureAttributesElement(measures, previousMeasures);
		attributes.ifPresent(attrElement -> measureElement.appendChild(attrElement));

		final Integer maxStaffNumber = measures.keySet().stream().max(Integer::compareTo).orElseThrow();

		for (Integer staffNumber : measures.keySet()) {

			final Measure measure = measures.get(staffNumber);

			//Set up handling possible mid-measure clef changes
			fillMeasureElement(measureElement, staffNumber, measure);

			// For all except last staff, backup to the beginning by the duration of measure.
			if (!staffNumber.equals(maxStaffNumber)) {
				measureElement.appendChild(createBackupElement(measure.getTimeSignature().getTotalDuration()));
			}
		}

		//Right barline
		if (!measureForSharedValues.getRightBarline().equals(Barline.SINGLE) && !measureForSharedValues
				.getRightBarline().equals(Barline.NONE)) {
			measureElement.appendChild(
					createBarlineElement(measureForSharedValues.getRightBarline(),
							MusicXmlTags.BARLINE_LOCATION_RIGHT));
		}

		return measureElement;
	}

	private void addNewSystemStartAndAnnotationsIfNeeded(Element measureElement, Measure measure) {
		if (startNewSystem(measure)) {
			Element printElement = getDocument().createElement(MusicXmlTags.PRINT);
			printElement.setAttribute(MusicXmlTags.NEW_SYSTEM, MusicXmlTags.YES);
			measureElement.appendChild(printElement);
		}

		final String annotation = getAnnotation(measure);
		if (!annotation.isEmpty()) {
			Element directionElement = getDocument().createElement(MusicXmlTags.DIRECTION);
			directionElement.setAttribute(MusicXmlTags.DIRECTION_PLACEMENT, MusicXmlTags.DIRECTION_ABOVE);
			Element directionType = getDocument().createElement(MusicXmlTags.DIRECTION_TYPE);
			Element wordsElement = getDocument().createElement(MusicXmlTags.DIRECTION_WORDS);
			wordsElement.setTextContent(annotation);
			directionType.appendChild(wordsElement);
			directionElement.appendChild(directionType);
			measureElement.appendChild(directionElement);
		}
	}

	public void fillMeasureElement(Element measureElement, Integer staffNumber, Measure measure) {
		Map<Duration, Clef> undealtClefChanges = new HashMap<>(measure.getClefChanges());

		//Notes
		final List<Integer> voiceNumbers = measure.getVoiceNumbers();

		int voicesHandled = 0;
		for (Integer voiceNumber : voiceNumbers) {

			Duration cumulatedDuration = null;
			for (int indexInVoice = 0; indexInVoice < measure.getVoiceSize(voiceNumber); ++indexInVoice) {
				Durational dur = measure.get(voiceNumber, indexInVoice);
				if (cumulatedDuration == null) {
					cumulatedDuration = dur.getDuration();
				} else {
					cumulatedDuration = cumulatedDuration.add(dur.getDuration());
				}

				final List<Duration> expressibleDurations = dur.getDuration()
						.decompose(measure.getTimeSignature().getTotalDuration());

				for (Duration expressibleDuration : expressibleDurations) {
					if (dur instanceof Rest) {
						measureElement.appendChild(createRestElement(expressibleDuration, voiceNumber, staffNumber));
					}

					if (dur instanceof Note) {
						addNoteElementsToMeasure((Note) dur, measureElement, expressibleDuration, voiceNumber,
								staffNumber, false);
					}

					if (dur instanceof Chord) {
						boolean useChordTag = false;
						for (Note note : (Chord) dur) {
							addNoteElementsToMeasure(note, measureElement, expressibleDuration, voiceNumber,
									staffNumber, useChordTag);
							useChordTag = true;
						}
					}
				}

				// Handle mid-measure clef changes
				handleMidMeasureClefChanges(measureElement, undealtClefChanges, cumulatedDuration, staffNumber);
			}

			// In case of multiple voices, backup to the beginning of the measure
			// Do not backup if it is the last voice to be handled
			voicesHandled++;
			if (voicesHandled < measure.getVoiceCount()) {
				measureElement.appendChild(createBackupElement(cumulatedDuration));
			} else {
				if (!measure.isPickup()
						&& cumulatedDuration != null && cumulatedDuration
						.isShorterThan(measure.getTimeSignature().getTotalDuration())) {
					Duration duration = measure.getTimeSignature().getTotalDuration()
							.subtract(cumulatedDuration);
					measureElement.appendChild(createForwardElement(duration));
				}
			}
		}
	}

	private Element createBarlineElement(Barline barline, String location) {
		Element barlineElement = getDocument().createElement(MusicXmlTags.BARLINE);
		barlineElement.setAttribute(MusicXmlTags.BARLINE_LOCATION, location);

		Element barStyleElement = getDocument().createElement(MusicXmlTags.BARLINE_STYLE);
		Element repeatDir = null;
		switch (barline) {
			case DOUBLE:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_LIGHT_LIGHT);
				break;
			case REPEAT_LEFT:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_HEAVY_LIGHT);
				repeatDir = getDocument().createElement(MusicXmlTags.BARLINE_REPEAT);
				repeatDir.setAttribute(MusicXmlTags.BARLINE_REPEAT_DIR, MusicXmlTags.BARLINE_REPEAT_DIR_FORWARD);
				break;
			case REPEAT_RIGHT:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_LIGHT_HEAVY);
				repeatDir = getDocument().createElement(MusicXmlTags.BARLINE_REPEAT);
				repeatDir.setAttribute(MusicXmlTags.BARLINE_REPEAT_DIR, MusicXmlTags.BARLINE_REPEAT_DIR_BACKWARD);
				break;
			case FINAL:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_LIGHT_HEAVY);
				break;
			case DASHED:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_DASHED);
				break;
			case THICK:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_HEAVY);
				break;
			case INVISIBLE:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_INVISIBLE);
				break;
			default:
				throw new IllegalStateException("Unexpected barline type: " + barline);
		}

		barlineElement.appendChild(barStyleElement);
		if (repeatDir != null) {
			barlineElement.appendChild(repeatDir);
		}

		return barlineElement;
	}

	private Optional<Element> createMeasureAttributesElement(Measure measure, Measure previousMeasure) {
		final Element attrElement = getDocument().createElement(MusicXmlTags.MEASURE_ATTRIBUTES);

		// Add divisions and all attributes
		if (previousMeasure == null) {
			attrElement.appendChild(createDivisionsElement());
			attrElement.appendChild(createKeySignatureElement(measure.getKeySignature()));
			attrElement.appendChild(createTimeSignatureElement(measure.getTimeSignature()));
			attrElement.appendChild(createClefElement(measure.getClef(), null));
		} else {

			//Key signature
			if (!measure.getKeySignature().equals(previousMeasure.getKeySignature())) {
				attrElement.appendChild(createKeySignatureElement(measure.getKeySignature()));
			}

			//Time signature
			if (!measure.getTimeSignature().equals(previousMeasure.getTimeSignature())) {
				attrElement.appendChild(createTimeSignatureElement(measure.getTimeSignature()));
			}

			//Clef
			if (!measure.getClef().equals(getLastClefInEffect(previousMeasure))) {
				attrElement.appendChild(createClefElement(measure.getClef(), null));
			}
		}

		if (attrElement.hasChildNodes()) {
			return Optional.of(attrElement);
		} else {
			return Optional.empty();
		}
	}

	private Optional<Element> createMeasureAttributesElement(Map<Integer, Measure> measures,
			Map<Integer, Measure> previousMeasures) {
		final Element attrElement = getDocument().createElement(MusicXmlTags.MEASURE_ATTRIBUTES);

		if (!previousMeasures.isEmpty()) {
			for (Integer staffNumber : measures.keySet()) {
				final Measure measure = measures.get(staffNumber);
				final Measure previousMeasure = previousMeasures.getOrDefault(staffNumber, null);

				// Add divisions and all attributes
				if (previousMeasure == null) {
					attrElement.appendChild(createDivisionsElement());
					attrElement.appendChild(createKeySignatureElement(measure.getKeySignature()));
					attrElement.appendChild(createTimeSignatureElement(measure.getTimeSignature()));
					attrElement.appendChild(createClefElement(measure.getClef(), staffNumber));
				} else {

					//Key signature
					if (!measure.getKeySignature().equals(previousMeasure.getKeySignature())) {
						attrElement.appendChild(createKeySignatureElement(measure.getKeySignature()));
					}

					//Time signature
					if (!measure.getTimeSignature().equals(previousMeasure.getTimeSignature())) {
						attrElement.appendChild(createTimeSignatureElement(measure.getTimeSignature()));
					}

					//Clef
					if (!measure.getClef().equals(getLastClefInEffect(previousMeasure))) {
						attrElement.appendChild(createClefElement(measure.getClef(), staffNumber));
					}
				}
			}
		} else {
			final Measure measure = measures.values().iterator().next();
			attrElement.appendChild(createDivisionsElement());
			attrElement.appendChild(createKeySignatureElement(measure.getKeySignature()));
			attrElement.appendChild(createTimeSignatureElement(measure.getTimeSignature()));

			attrElement.appendChild(createStavesElement(measures.size()));

			for (Integer staffNumber : measures.keySet()) {
				attrElement.appendChild(createClefElement(measures.get(staffNumber).getClef(), staffNumber));
			}
		}

		if (attrElement.hasChildNodes()) {
			return Optional.of(attrElement);
		} else {
			return Optional.empty();
		}
	}

	private Clef getLastClefInEffect(Measure measure) {
		if (!measure.containsClefChanges()) {
			return measure.getClef();
		}

		return measure.getClefChanges().get(measure.getClefChanges().lastKey());
	}

	private Element createStavesElement(int staffCount) {
		final Element stavesElement = getDocument().createElement(MusicXmlTags.MEAS_ATTR_STAVES);
		stavesElement.setTextContent(Integer.toString(staffCount));
		return stavesElement;
	}

	private Element createDivisionsElement() {
		Element divisionsElement = getDocument().createElement(MusicXmlTags.MEAS_ATTR_DIVS);
		divisionsElement.setTextContent(Integer.toString(getDivisions()));
		return divisionsElement;
	}

	private Element createKeySignatureElement(KeySignature keySignature) {
		Element keySigElement = getDocument().createElement(MusicXmlTags.MEAS_ATTR_KEY);
		Element fifthsElement = getDocument().createElement(MusicXmlTags.MEAS_ATTR_KEY_FIFTHS);

		int fifths = keySignature.getSharpCount();
		if (fifths == 0) {
			fifths = keySignature.getFlatCount() * -1;
		}
		fifthsElement.setTextContent(Integer.toString(fifths));

		keySigElement.appendChild(fifthsElement);

		return keySigElement;
	}

	private Element createTimeSignatureElement(TimeSignature timeSignature) {
		Element timeSigElement = getDocument().createElement(MusicXmlTags.MEAS_ATTR_TIME);
		if (!showTimeSignature()) {
			timeSigElement.setAttribute(MusicXmlTags.PRINT_OBJECT, MusicXmlTags.NO);
		}

		Element beatsElement = getDocument().createElement(MusicXmlTags.MEAS_ATTR_BEATS);
		Element beatTypeElement = getDocument().createElement(MusicXmlTags.MEAS_ATTR_BEAT_TYPE);

		beatsElement.setTextContent(Integer.toString(timeSignature.getBeatCount()));
		beatTypeElement.setTextContent(Integer.toString(timeSignature.getBeatDuration().getDenominator()));

		timeSigElement.appendChild(beatsElement);
		timeSigElement.appendChild(beatTypeElement);

		switch (timeSignature.getSymbol()) {
			case COMMON:
				timeSigElement.setAttribute(MusicXmlTags.MEAS_ATTR_TIME_SYMBOL, MusicXmlTags.MEAS_ATTR_TIME_COMMON);
				break;
			case CUT_TIME:
				timeSigElement.setAttribute(MusicXmlTags.MEAS_ATTR_TIME_SYMBOL, MusicXmlTags.MEAS_ATTR_TIME_CUT);
				break;
			case BEAT_NUMBER_ONLY:
				timeSigElement.setAttribute(MusicXmlTags.MEAS_ATTR_TIME_SYMBOL, MusicXmlTags.MEAS_ATTR_TIME_NUMERATOR);
				break;
			case BEAT_DURATION_AS_NOTE:
				timeSigElement.setAttribute(MusicXmlTags.MEAS_ATTR_TIME_SYMBOL, MusicXmlTags.MEAS_ATTR_TIME_NOTE);
				break;
			case BEAT_DURATION_AS_DOTTED_NOTE:
				timeSigElement
						.setAttribute(MusicXmlTags.MEAS_ATTR_TIME_SYMBOL, MusicXmlTags.MEAS_ATTR_TIME_DOTTED_NOTE);
				break;
			case NUMERIC: // Fall through to default
			default:
				// Do not set symbol if NUMERIC
		}

		return timeSigElement;
	}

	private Element createClefElement(Clef clef, Integer staffNumber) {
		Element clefElement = getDocument().createElement(MusicXmlTags.CLEF);
		Element signElement = getDocument().createElement(MusicXmlTags.CLEF_SIGN);

		switch (clef.getSymbol()) {
			case G:
				signElement.setTextContent(MusicXmlTags.CLEF_G);
				break;
			case F:
				signElement.setTextContent(MusicXmlTags.CLEF_F);
				break;
			case C:
				signElement.setTextContent(MusicXmlTags.CLEF_C);
				break;
			case PERCUSSION:
				signElement.setTextContent(MusicXmlTags.CLEF_PERC);
				break;
			default:
				throw new IllegalStateException("Unexpected clef symbol: " + clef.getSymbol());
		}

		if (staffNumber != null) {
			clefElement.setAttribute(MusicXmlTags.CLEF_STAFF, staffNumber.toString());
		}

		Element lineElement = getDocument().createElement(MusicXmlTags.CLEF_LINE);
		lineElement.setTextContent(Integer.toString(clef.getLine()));

		clefElement.appendChild(signElement);
		clefElement.appendChild(lineElement);

		return clefElement;
	}

	private void handleMidMeasureClefChanges(Element measureElement, Map<Duration, Clef> undealtClefChanges,
			Duration cumulatedDuration, Integer staffNumber) {

		List<Duration> offsets = new ArrayList<>(undealtClefChanges.keySet());
		Collections.sort(offsets);
		for (Duration offset : offsets) {

			if (offset.isShorterThan(cumulatedDuration) || offset.equals(cumulatedDuration)) {

				// Backup
				if (!offset.equals(cumulatedDuration)) {
					Element backupElement = createBackupElement(cumulatedDuration.subtract(offset));
					measureElement.appendChild(backupElement);
				}

				// Clef
				Element attributesElement = getDocument().createElement(MusicXmlTags.MEASURE_ATTRIBUTES);
				Element clefElement = createClefElement(undealtClefChanges.get(offset), staffNumber);

				attributesElement.appendChild(clefElement);
				measureElement.appendChild(attributesElement);

				undealtClefChanges.remove(offset);

				// Forward
				if (!offset.equals(cumulatedDuration)) {
					Element forwardElement = createForwardElement(cumulatedDuration.subtract(offset));
					measureElement.appendChild(forwardElement);
				}

			}

		}
	}

	private Element createBackupElement(Duration duration) {
		Element backupElement = getDocument().createElement(MusicXmlTags.MEASURE_BACKUP);
		Element durationElement = getDocument().createElement(MusicXmlTags.NOTE_DURATION);
		durationElement.setTextContent(Integer.toString(divisionCountOf(duration)));
		backupElement.appendChild(durationElement);
		return backupElement;
	}

	private Element createForwardElement(Duration duration) {
		Element forwardElement = getDocument().createElement(MusicXmlTags.MEASURE_FORWARD);
		Element durationElement = getDocument().createElement(MusicXmlTags.NOTE_DURATION);
		durationElement.setTextContent(Integer.toString(divisionCountOf(duration)));
		forwardElement.appendChild(durationElement);
		return forwardElement;
	}

	private void addNoteElementsToMeasure(Note note, Element measureNode, Duration expressibleDuration,
			int voiceNumber, Integer staffNumber, boolean chordTag) {
		final Element noteElement = getDocument().createElement(MusicXmlTags.NOTE);

		// Add any preceding grace notes to measure.
		addOrnamentalNoteElementsToMeasure(note, Ornament.Type.GRACE_NOTES, measureNode, voiceNumber, staffNumber);

		//Chord
		if (chordTag) {
			final Element chordElement = getDocument().createElement(MusicXmlTags.NOTE_CHORD);
			noteElement.appendChild(chordElement);
		}

		// Pitch
		noteElement.appendChild(createPitchElement(note.getPitch()));

		// Duration
		noteElement.appendChild(createDurationElement(expressibleDuration));

		// Voice
		noteElement.appendChild(createVoiceElement(voiceNumber));

		// Appearance
		addDurationAppearanceElementsToNoteElement(noteElement, expressibleDuration);

		// Staff
		if (staffNumber != null) {
			noteElement.appendChild(createStaffElement(staffNumber));
		}

		// Notations (musical notations, e.g. articulations, dynamics, fermata, slur)
		createNotationsElement(note, note.getArticulations(), note.getNotations(), note.getOrnaments())
				.ifPresent(notationsElement -> noteElement.appendChild(notationsElement));

		measureNode.appendChild(noteElement);

		// Add any succeeding grace notes to measure.
		addOrnamentalNoteElementsToMeasure(note, Ornament.Type.SUCCEEDING_GRACE_NOTES, measureNode, voiceNumber,
				staffNumber);
	}

	private void addOrnamentalNoteElementsToMeasure(Note note, Ornament.Type graceNoteType, Element measureNode,
			int voiceNumber, Integer staffNumber) {

		Optional<Ornament> graceNoteOrnament = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(graceNoteType)).findAny();

		List<Ornamental> ornamentals = Collections.emptyList();

		if (graceNoteOrnament.isPresent()) {
			ornamentals = graceNoteOrnament.get().getOrnamentalNotes();
		}

		for (Ornamental ornamental : ornamentals) {
			if (ornamental instanceof GraceNote) {
				writeGraceNoteToMeasure(measureNode, voiceNumber, staffNumber, (GraceNote) ornamental, false);
			} else if (ornamental instanceof GraceNoteChord) {
				boolean useChordElement = false;
				for (GraceNote graceNote : (GraceNoteChord) ornamental) {
					writeGraceNoteToMeasure(measureNode, voiceNumber, staffNumber, graceNote, useChordElement);
					useChordElement = true;
				}
			}
		}
	}

	private void writeGraceNoteToMeasure(Element measureNode, int voiceNumber, Integer staffNumber,
			GraceNote graceNote, boolean useChordElement) {
		final Element graceNoteElement = getDocument().createElement(MusicXmlTags.NOTE);

		final Element graceElement = getDocument().createElement(MusicXmlTags.NOTE_GRACE_NOTE);
		if (graceNote.getType().equals(Ornamental.Type.ACCIACCATURA)) {
			graceElement.setAttribute(MusicXmlTags.NOTE_GRACE_SLASH, MusicXmlTags.YES);
		}

		graceNoteElement.appendChild(graceElement);

		if (useChordElement) {
			final Element chordElement = getDocument().createElement(MusicXmlTags.NOTE_CHORD);
			graceNoteElement.appendChild(chordElement);
		}

		graceNoteElement.appendChild(createPitchElement(graceNote.getPitch()));
		graceNoteElement.appendChild(createVoiceElement(voiceNumber));
		addDurationAppearanceElementsToNoteElement(graceNoteElement, graceNote.getDisplayableDuration());

		// Staff
		if (staffNumber != null) {
			graceNoteElement.appendChild(createStaffElement(staffNumber));
		}

		// Notations (musical notations, e.g. articulations, dynamics, fermata, slur)
		createNotationsElement(graceNote, graceNote.getArticulations(), graceNote.getNotations(),
				graceNote.getOrnaments())
				.ifPresent(notationsElement -> graceNoteElement.appendChild(notationsElement));

		measureNode.appendChild(graceNoteElement);
	}

	private Element createStaffElement(Integer staffNumber) {
		final Element staffElement = getDocument().createElement(MusicXmlTags.NOTE_STAFF);
		staffElement.setTextContent(staffNumber.toString());
		return staffElement;
	}

	private Element createDurationElement(Duration duration) {
		final Element durationElement = getDocument().createElement(MusicXmlTags.NOTE_DURATION);
		final int durValue = divisionCountOf(duration);

		durationElement.setTextContent(Integer.toString(durValue));

		return durationElement;
	}

	private Element createVoiceElement(int voice) {
		Element voiceElement = getDocument().createElement(MusicXmlTags.NOTE_VOICE);
		voiceElement.setTextContent(Integer.toString(voice));
		return voiceElement;
	}

	private int divisionCountOf(Duration duration) {
		return ((getDivisions() * Durations.QUARTER.getDenominator())
				/ duration.getDenominator())
				* duration.getNumerator();
	}

	protected boolean showTimeSignature() {
		return true;
	}

	protected boolean startNewSystem(Measure measure) {
		return false;
	}

	private Element createPitchElement(Pitch pitch) {
		final Element pitchElement = getDocument().createElement(MusicXmlTags.NOTE_PITCH);
		final Element step = getDocument().createElement(MusicXmlTags.PITCH_STEP);
		step.setTextContent(pitch.getBase().toString());
		pitchElement.appendChild(step);

		final Element alter = getDocument().createElement(MusicXmlTags.PITCH_ALTER);
		alter.setTextContent(Integer.toString(pitch.getAccidental().getAlterationInt()));
		pitchElement.appendChild(alter);

		final Element octave = getDocument().createElement(MusicXmlTags.PITCH_OCT);
		octave.setTextContent(Integer.toString(pitch.getOctave()));
		pitchElement.appendChild(octave);

		return pitchElement;
	}

	private Optional<Element> createNotationsElement(Notation.Connectable connectable,
			Collection<Articulation> articulations,
			Collection<Notation> notations, Collection<Ornament> ornaments) {
		final Element notationsElement = getDocument().createElement(MusicXmlTags.NOTATIONS);

		// Articulations & fermata
		if (!articulations.isEmpty()) {
			if (articulations.contains(Articulation.FERMATA)) {
				notationsElement.appendChild(getDocument().createElement(MusicXmlTags.FERMATA));
			}

			// Don't create articulations element if fermata is the only articulation
			if (!(articulations.size() == 1 && articulations.contains(Articulation.FERMATA))) {
				notationsElement.appendChild(createArticulationsElement(articulations));
			}
		}

		// Notations
		for (Notation notation : notations) {
			Notation.Type type = notation.getType();

			if (NotationResolver.NOTATION_TYPES_WITH_START_STOP.containsKey(type)) {
				if (connectable.getConnection(notation).get().isBeginning()) {
					notationsElement.appendChild(notationResolver.createStartElement(notation));
				}

				if (connectable.getConnection(notation).get().isEnd()) {
					Element notationStop = notationResolver.createStopElement(notation);
					if (notationStop != null) {
						notationsElement.appendChild(notationStop);
					}
				}
			}

			if (NotationResolver.ARPEGGIATION_TYPES.containsKey(type)) {
				notationsElement.appendChild(notationResolver.createArpeggiationElement(notation));
			}
		}

		if (!ornaments.isEmpty()) {

			Element ornamentsElement = getDocument().createElement(MusicXmlTags.ORNAMENTS);

			for (Ornament ornament : ornaments) {
				Ornament.Type type = ornament.getType();
				if (!type.equals(Ornament.Type.GRACE_NOTES) && !type.equals(Ornament.Type.SUCCEEDING_GRACE_NOTES)) {
					ornamentsElement.appendChild(createOrnamentElement(type));
				}
			}

			if (ornamentsElement.hasChildNodes()) {
				notationsElement.appendChild(ornamentsElement);
			}
		}

		if (notationsElement.hasChildNodes()) {
			return Optional.of(notationsElement);
		} else {
			return Optional.empty();
		}
	}

	private Element createOrnamentElement(Ornament.Type ornamentType) {

		String elementName = null;
		int tremoloLines = 0;

		switch (ornamentType) {
			case DELAYED_INVERTED_TURN:
				elementName = MusicXmlTags.DELAYED_INVERTED_TURN;
				break;
			case DELAYED_TURN:
				elementName = MusicXmlTags.DELAYED_TURN;
				break;
			case INVERTED_MORDENT:
				elementName = MusicXmlTags.INVERTED_MORDENT;
				break;
			case INVERTED_TURN:
				elementName = MusicXmlTags.INVERTED_TURN;
				break;
			case MORDENT:
				elementName = MusicXmlTags.MORDENT;
				break;
			case SINGLE_TREMOLO:
				elementName = MusicXmlTags.TREMOLO;
				tremoloLines = 1;
				break;
			case DOUBLE_TREMOLO:
				elementName = MusicXmlTags.TREMOLO;
				tremoloLines = 2;
				break;
			case TRIPLE_TREMOLO:
				elementName = MusicXmlTags.TREMOLO;
				tremoloLines = 3;
				break;
			case TRILL:
				elementName = MusicXmlTags.TRILL_MARK;
				break;
			case TURN:
				elementName = MusicXmlTags.TURN;
				break;
		}

		Element element = getDocument().createElement(elementName);

		if (tremoloLines > 0) {
			element.setTextContent(Integer.toString(tremoloLines));
		}

		return element;
	}

	private Element createArticulationsElement(Collection<Articulation> articulations) {
		final Element articulationsElement = getDocument().createElement(MusicXmlTags.NOTE_ARTICULATIONS);

		for (Articulation articulation : articulations) {
			switch (articulation) {
				case ACCENT:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.ACCENT));
					break;
				case BREATH_MARK:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.BREATH_MARK));
					break;
				case CAESURA:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.CAESURA));
					break;
				case FERMATA:
					// Fermata is not treated as an articulation in MusicXml, so fermatas ar handled elsewhere.
					break;
				case SLIDE_IN_DOWN:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.SLIDE_IN_DOWN));
					break;
				case SLIDE_IN_UP:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.SLIDE_IN_UP));
					break;
				case SLIDE_OUT_DOWN:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.SLIDE_OUT_DOWN));
					break;
				case SLIDE_OUT_UP:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.SLIDE_OUT_UP));
					break;
				case SPICCATO:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.SPICCATO));
					break;
				case STACCATISSIMO:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.STACCATISSIMO));
					break;
				case STACCATO:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.STACCATO));
					break;
				case STRESS:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.STRESS));
					break;
				case STRONG_ACCENT:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.STRONG_ACCENT));
					break;
				case TENUTO:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.TENUTO));
					break;
				case TENUTO_STACCATO:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.TENUTO_STACCATO));
					break;
				case UNSTRESS:
					articulationsElement.appendChild(getDocument().createElement(MusicXmlTags.UNSTRESS));
					break;
				default:
					throw new IllegalStateException("Unexpected articulation: " + articulation);
			}
		}

		return articulationsElement;
	}

	private Element createRestElement(Duration expressibleRestDuration, int voice, Integer staffNumber) {
		final Element restElement = getDocument().createElement(MusicXmlTags.NOTE);
		restElement.appendChild(getDocument().createElement(MusicXmlTags.NOTE_REST));
		restElement.appendChild(createDurationElement(expressibleRestDuration));

		// Add voice
		restElement.appendChild(createVoiceElement(voice));

		// Add duration appearance
		addDurationAppearanceElementsToNoteElement(restElement, expressibleRestDuration);

		// Staff
		if (staffNumber != null) {
			restElement.appendChild(createStaffElement(staffNumber));
		}

		return restElement;
	}

	private void addDurationAppearanceElementsToNoteElement(Element noteElement, Duration expressibleDuration) {
		DurationAppearanceProvider.INSTANCE.getAppearanceElements(expressibleDuration, getDocument())
				.forEach(element -> noteElement.appendChild(element));
	}

	private static class NotationResolver {
		private static final int MAX_NOTATION_NUMBER = 6;
		private static final Map<Notation.Type, String> NOTATION_TYPES_WITH_START_STOP = createNotationTypes();
		private static final Map<Notation.Type, String> ARPEGGIATION_TYPES = createArpeggiationTypes();
		private static final Map<Notation.Style, String> NOTATION_STYLES = createNotationStyles();

		private static Map<Notation.Type, String> createNotationTypes() {
			Map<Notation.Type, String> notationTypes = new HashMap<>();
			notationTypes.put(Notation.Type.TIE, MusicXmlTags.TIED);
			notationTypes.put(Notation.Type.SLUR, MusicXmlTags.SLUR);
			// Slide type is ignored here as it is practically same as glissando
			notationTypes.put(Notation.Type.GLISSANDO, MusicXmlTags.GLISSANDO);
			notationTypes.put(Notation.Type.NON_ARPEGGIATE, MusicXmlTags.NON_ARPEGGIATE);
			return Collections.unmodifiableMap(notationTypes);
		}

		private static Map<Notation.Type, String> createArpeggiationTypes() {
			Map<Notation.Type, String> arpeggiationTypes = new HashMap<>();
			arpeggiationTypes.put(Notation.Type.ARPEGGIATE, MusicXmlTags.ARPEGGIATE);
			arpeggiationTypes.put(Notation.Type.ARPEGGIATE_DOWN, MusicXmlTags.ARPEGGIATE);
			arpeggiationTypes.put(Notation.Type.ARPEGGIATE_UP, MusicXmlTags.ARPEGGIATE);
			return Collections.unmodifiableMap(arpeggiationTypes);
		}

		private static Map<Notation.Style, String> createNotationStyles() {
			Map<Notation.Style, String> notationStyles = new HashMap<>();
			notationStyles.put(Notation.Style.SOLID, MusicXmlTags.NOTATION_LINE_SOLID);
			notationStyles.put(Notation.Style.DASHED, MusicXmlTags.NOTATION_LINE_DASHED);
			notationStyles.put(Notation.Style.DOTTED, MusicXmlTags.NOTATION_LINE_DOTTED);
			notationStyles.put(Notation.Style.WAVY, MusicXmlTags.NOTATION_LINE_WAVY);
			return Collections.unmodifiableMap(notationStyles);
		}

		private final Document document;
		private Integer nextAvailableNotationNumber = Integer.valueOf(1);

		NotationResolver(Document document) {
			this.document = document;
		}

		private Map<Notation, Integer> unresolvedNotations = new HashMap<>();
		private Set<Integer> usedNotationNumbers = new HashSet<>(MAX_NOTATION_NUMBER);

		Element createArpeggiationElement(Notation notation) {
			Notation.Type type = notation.getType();
			Element notationElement = document.createElement(ARPEGGIATION_TYPES.get(type));

			final Integer notationNumber = getNextAvailableNotationNumber();
			unresolvedNotations.put(notation, notationNumber);

			notationElement.setAttribute(MusicXmlTags.NOTATION_NUMBER, notationNumber.toString());

			if (type.equals(Notation.Type.ARPEGGIATE_DOWN)) {
				notationElement.setAttribute(MusicXmlTags.ARPEGGIO_DIRECTION, MusicXmlTags.ARPEGGIO_DIRECTION_DOWN);
			}

			if (type.equals(Notation.Type.ARPEGGIATE_UP)) {
				notationElement.setAttribute(MusicXmlTags.ARPEGGIO_DIRECTION, MusicXmlTags.ARPEGGIO_DIRECTION_UP);
			}

			return notationElement;
		}

		Element createStartElement(Notation notation) {
			Element notationElement = document.createElement(NOTATION_TYPES_WITH_START_STOP.get(notation.getType()));

			final Integer notationNumber = getNextAvailableNotationNumber();
			unresolvedNotations.put(notation, notationNumber);

			notationElement.setAttribute(MusicXmlTags.NOTATION_NUMBER, notationNumber.toString());

			if (notation.getType().equals(Notation.Type.NON_ARPEGGIATE)) {
				notationElement.setAttribute(MusicXmlTags.NOTATION_TYPE, MusicXmlTags.NON_ARPEGGIATE_BOTTOM);
			} else {
				notationElement.setAttribute(MusicXmlTags.NOTATION_TYPE, MusicXmlTags.NOTATION_TYPE_START);
				if (NOTATION_STYLES.containsKey(notation.getStyle())) {
					notationElement.setAttribute(MusicXmlTags.NOTATION_LINE_TYPE,
							NOTATION_STYLES.get(notation.getStyle()));
				}
			}
			return notationElement;
		}

		Element createStopElement(Notation notation) {
			Element notationElement = null;

			if (unresolvedNotations.containsKey(notation)) {
				notationElement = document.createElement(NOTATION_TYPES_WITH_START_STOP.get(notation.getType()));

				notationElement
						.setAttribute(MusicXmlTags.NOTATION_NUMBER, unresolvedNotations.get(notation).toString());

				usedNotationNumbers.remove(unresolvedNotations.get(notation));
				unresolvedNotations.remove(notation);

				if (notation.getType().equals(Notation.Type.NON_ARPEGGIATE)) {
					notationElement.setAttribute(MusicXmlTags.NOTATION_TYPE, MusicXmlTags.NON_ARPEGGIATE_TOP);
				} else {
					notationElement.setAttribute(MusicXmlTags.NOTATION_TYPE, MusicXmlTags.NOTATION_TYPE_STOP);
					if (NOTATION_STYLES.containsKey(notation.getStyle())) {
						notationElement.setAttribute(MusicXmlTags.NOTATION_LINE_TYPE,
								NOTATION_STYLES.get(notation.getStyle()));
					}
				}
			}

			return notationElement;
		}

		private Integer getNextAvailableNotationNumber() {
			Integer availableNumber = nextAvailableNotationNumber;
			usedNotationNumbers.add(availableNumber);

			for (int next = 1; next <= MAX_NOTATION_NUMBER; ++next) {
				if (!usedNotationNumbers.contains(next)) {
					nextAvailableNotationNumber = next;
					break;
				}
			}

			return availableNumber;
		}
	}
}
