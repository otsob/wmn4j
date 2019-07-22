/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.wmn4j.Wmn4j;
import org.wmn4j.notation.elements.Articulation;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.KeySignature;
import org.wmn4j.notation.elements.Marking;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MultiStaffPart;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;
import org.wmn4j.notation.elements.Score;
import org.wmn4j.notation.elements.SingleStaffPart;
import org.wmn4j.notation.elements.TimeSignature;
import org.wmn4j.notation.iterators.PartWiseScoreIterator;
import org.wmn4j.notation.iterators.ScoreIterator;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class MusicXmlWriterDom implements MusicXmlWriter {

	private Document doc;
	private final String MUSICXML_VERSION_NUMBER = "3.1";
	private final Score score;
	private final SortedMap<String, Part> partIdMap = new TreeMap<>();
	private MarkingResolver markingResolver;
	private final int divisions;

	MusicXmlWriterDom(Score score) {
		this.score = score;
		this.divisions = computeDivisions(this.score);
	}

	@Override
	public void writeToFile(Path path) {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			this.doc = docBuilder.newDocument();
			this.markingResolver = new MarkingResolver(this.doc);

			final Element rootElement = this.doc.createElement(MusicXmlTags.SCORE_PARTWISE);
			rootElement.setAttribute(MusicXmlTags.MUSICXML_VERSION, MUSICXML_VERSION_NUMBER);
			this.doc.appendChild(rootElement);

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
			final DOMImplementation domImpl = doc.getImplementation();
			final DocumentType doctype = domImpl.createDocumentType("doctype",
					"-//Recordare//DTD MusicXML " + MUSICXML_VERSION_NUMBER + " Partwise//EN",
					"http://www.musicxml.org/dtds/partwise.dtd");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

			final DOMSource source = new DOMSource(doc);
			final StreamResult result = new StreamResult(new File(path.toString()));

			transformer.transform(source, result);

			this.doc = null;

		} catch (final ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (final TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private int computeDivisions(Score score) {
		Set<Integer> denominators = new HashSet<>();

		final ScoreIterator iter = new PartWiseScoreIterator(score);
		while (iter.hasNext()) {
			denominators.add(iter.next().getDuration().getDenominator());
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

	private void writeScoreAttributes(Element rootElement) {
		if (!score.getTitle().isEmpty()) {
			final Element workTitleElement = doc.createElement(MusicXmlTags.SCORE_WORK_TITLE);
			workTitleElement.setTextContent(score.getTitle());

			final Element workElement = doc.createElement(MusicXmlTags.SCORE_WORK);
			workElement.appendChild(workTitleElement);
			rootElement.appendChild(workElement);
		}

		if (!score.getAttribute(Score.Attribute.MOVEMENT_TITLE).isEmpty()) {
			final Element movementTitleElement = doc.createElement(MusicXmlTags.SCORE_MOVEMENT_TITLE);
			movementTitleElement.setTextContent(score.getAttribute(Score.Attribute.MOVEMENT_TITLE));
			rootElement.appendChild(movementTitleElement);
		}

		final Element identificationElement = createIdentificationElement();
		if (identificationElement.hasChildNodes()) {
			rootElement.appendChild(identificationElement);
		}
	}

	private Element createIdentificationElement() {
		final Element identificationElement = doc.createElement(MusicXmlTags.SCORE_IDENTIFICATION);

		if (!score.getAttribute(Score.Attribute.COMPOSER).isEmpty()) {
			final Element composerElement = doc.createElement(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
			composerElement.setAttribute(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR_TYPE,
					MusicXmlTags.SCORE_IDENTIFICATION_COMPOSER);

			composerElement.setTextContent(score.getAttribute(Score.Attribute.COMPOSER));
			identificationElement.appendChild(composerElement);
		}

		if (!score.getAttribute(Score.Attribute.ARRANGER).isEmpty()) {
			final Element arrangerElement = doc.createElement(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
			arrangerElement.setAttribute(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR_TYPE,
					MusicXmlTags.SCORE_IDENTIFICATION_ARRANGER);

			arrangerElement.setTextContent(score.getAttribute(Score.Attribute.ARRANGER));
			identificationElement.appendChild(arrangerElement);
		}

		identificationElement.appendChild(createEncodingElement());

		return identificationElement;
	}

	private Element createEncodingElement() {

		final Element encodingElement = doc.createElement(MusicXmlTags.ENCODING);
		final Element softwareElement = doc.createElement(MusicXmlTags.SOFTWARE);
		softwareElement.setTextContent(Wmn4j.getNameWithVersion());
		encodingElement.appendChild(softwareElement);

		final Element encodingDateElement = doc.createElement(MusicXmlTags.ENCODING_DATE);
		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		encodingDateElement.setTextContent(dateFormat.format(new Date()));

		encodingElement.appendChild(encodingDateElement);

		return encodingElement;
	}

	private void writePartList(Element scoreRoot) {
		final Element partList = doc.createElement(MusicXmlTags.PART_LIST);

		for (int i = 0; i < this.score.getPartCount(); ++i) {
			final Element partElement = doc.createElement(MusicXmlTags.PLIST_SCORE_PART);
			final String partId = "P" + (i + 1);
			final Part part = this.score.getPart(i);
			this.partIdMap.put(partId, part);

			partElement.setAttribute(MusicXmlTags.PART_ID, partId);
			final Element partName = doc.createElement(MusicXmlTags.PART_NAME);
			partName.setTextContent(part.getName());
			partElement.appendChild(partName);

			if (!part.getAttribute(Part.Attribute.ABBREVIATED_NAME).isEmpty()) {
				final Element abbreviatedPartName = doc.createElement(MusicXmlTags.PART_NAME_ABBREVIATION);
				abbreviatedPartName.setTextContent(part.getAttribute(Part.Attribute.ABBREVIATED_NAME));
				partElement.appendChild(abbreviatedPartName);
			}

			partList.appendChild(partElement);
		}

		scoreRoot.appendChild(partList);
	}

	private void writePart(Part part, Element scoreRoot, String partId) {
		final Element partElement;

		if (part.isMultiStaff()) {
			partElement = createMultiStaffPart((MultiStaffPart) part, partId);
		} else {
			partElement = createSingleStaffPart((SingleStaffPart) part, partId);
		}

		scoreRoot.appendChild(partElement);
	}

	private Element createSingleStaffPart(SingleStaffPart part, String partId) {
		final Element partElement = doc.createElement(MusicXmlTags.PART);
		partElement.setAttribute(MusicXmlTags.PART_ID, partId);

		Measure previousMeasure = null;

		for (Measure measure : part) {
			partElement.appendChild(createMeasureElement(measure, previousMeasure));
			previousMeasure = measure;
		}

		return partElement;
	}

	private Element createMultiStaffPart(MultiStaffPart part, String partId) {
		final Element partElement = doc.createElement(MusicXmlTags.PART);
		partElement.setAttribute(MusicXmlTags.PART_ID, partId);

		Map<Integer, Measure> previousMeasures = Collections.emptySortedMap();

		List<Integer> staffNumbers = part.getStaffNumbers();
		final int firstMeasureNumber = part.getStaff(staffNumbers.get(0)).hasPickupMeasure() ? 0 : 1;

		for (int measureNumber = firstMeasureNumber; measureNumber <= part.getFullMeasureCount(); ++measureNumber) {
			SortedMap<Integer, Measure> measures = new TreeMap<>();

			for (Integer staffNumber : staffNumbers) {
				measures.put(staffNumber, part.getMeasure(staffNumber, measureNumber));
			}

			partElement.appendChild(createMultiStaffMeasureElement(measures, previousMeasures));
			previousMeasures = measures;
		}

		return partElement;
	}

	private Element createMeasureElement(Measure measure, Measure previousMeasure) {
		final Element measureElement = doc.createElement(MusicXmlTags.MEASURE);
		measureElement.setAttribute(MusicXmlTags.MEASURE_NUM, Integer.toString(measure.getNumber()));

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
		final Element measureElement = doc.createElement(MusicXmlTags.MEASURE);

		final Measure measureForSharedValues = measures.values().iterator().next();

		measureElement.setAttribute(MusicXmlTags.MEASURE_NUM, Integer.toString(measureForSharedValues.getNumber()));

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

	public void fillMeasureElement(Element measureElement, Integer staffNumber, Measure measure) {
		Map<Duration, Clef> undealtClefChanges = new HashMap<>(measure.getClefChanges());

		//Notes
		final List<Integer> voiceNumbers = measure.getVoiceNumbers();

		int voicesHandled = 0;
		for (Integer voiceNumber : voiceNumbers) {

			Duration cumulatedDuration = null;
			for (Durational dur : measure.getVoice(voiceNumber)) {
				if (cumulatedDuration == null) {
					cumulatedDuration = dur.getDuration();
				} else {
					cumulatedDuration = cumulatedDuration.add(dur.getDuration());
				}

				if (dur instanceof Rest) {
					measureElement.appendChild(createRestElement((Rest) dur, voiceNumber, staffNumber));
				}

				if (dur instanceof Note) {
					measureElement.appendChild(createNoteElement((Note) dur, voiceNumber, staffNumber, false));
				}

				if (dur instanceof Chord) {
					boolean useChordTag = false;
					for (Note note : (Chord) dur) {
						measureElement
								.appendChild(createNoteElement(note, voiceNumber, staffNumber, useChordTag));
						useChordTag = true;
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
						&& cumulatedDuration.isShorterThan(measure.getTimeSignature().getTotalDuration())) {
					Duration duration = measure.getTimeSignature().getTotalDuration()
							.subtract(cumulatedDuration);
					measureElement.appendChild(createForwardElement(duration));
				}
			}
		}
	}

	private Element createBarlineElement(Barline barline, String location) {
		Element barlineElement = doc.createElement(MusicXmlTags.BARLINE);
		barlineElement.setAttribute(MusicXmlTags.BARLINE_LOCATION, location);

		Element barStyleElement = doc.createElement(MusicXmlTags.BARLINE_STYLE);
		Element repeatDir = null;
		switch (barline) {
			case DOUBLE:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_LIGHT_LIGHT);
				break;
			case REPEAT_LEFT:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_HEAVY_LIGHT);
				repeatDir = doc.createElement(MusicXmlTags.BARLINE_REPEAT);
				repeatDir.setAttribute(MusicXmlTags.BARLINE_REPEAT_DIR, MusicXmlTags.BARLINE_REPEAT_DIR_FORWARD);
				break;
			case REPEAT_RIGHT:
				barStyleElement.setTextContent(MusicXmlTags.BARLINE_STYLE_LIGHT_HEAVY);
				repeatDir = doc.createElement(MusicXmlTags.BARLINE_REPEAT);
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
		final Element attrElement = doc.createElement(MusicXmlTags.MEASURE_ATTRIBUTES);

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
		final Element attrElement = doc.createElement(MusicXmlTags.MEASURE_ATTRIBUTES);

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
		final Element stavesElement = doc.createElement(MusicXmlTags.MEAS_ATTR_STAVES);
		stavesElement.setTextContent(Integer.toString(staffCount));
		return stavesElement;
	}

	private Element createDivisionsElement() {
		Element divisionsElement = doc.createElement(MusicXmlTags.MEAS_ATTR_DIVS);
		divisionsElement.setTextContent(Integer.toString(divisions));
		return divisionsElement;
	}

	private Element createKeySignatureElement(KeySignature keySignature) {
		Element keySigElement = doc.createElement(MusicXmlTags.MEAS_ATTR_KEY);
		Element fifthsElement = doc.createElement(MusicXmlTags.MEAS_ATTR_KEY_FIFTHS);

		int fifths = keySignature.getNumberOfSharps();
		if (fifths == 0) {
			fifths = keySignature.getNumberOfFlats() * -1;
		}
		fifthsElement.setTextContent(Integer.toString(fifths));

		keySigElement.appendChild(fifthsElement);

		return keySigElement;
	}

	private Element createTimeSignatureElement(TimeSignature timeSignature) {
		Element timeSigElement = doc.createElement(MusicXmlTags.MEAS_ATTR_TIME);
		Element beatsElement = doc.createElement(MusicXmlTags.MEAS_ATTR_BEATS);
		Element beatTypeElement = doc.createElement(MusicXmlTags.MEAS_ATTR_BEAT_TYPE);

		beatsElement.setTextContent(Integer.toString(timeSignature.getBeatCount()));
		beatTypeElement.setTextContent(Integer.toString(timeSignature.getBeatDuration().getDenominator()));

		timeSigElement.appendChild(beatsElement);
		timeSigElement.appendChild(beatTypeElement);

		return timeSigElement;
	}

	private Element createClefElement(Clef clef, Integer staffNumber) {
		Element clefElement = doc.createElement(MusicXmlTags.CLEF);
		Element signElement = doc.createElement(MusicXmlTags.CLEF_SIGN);

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

		Element lineElement = doc.createElement(MusicXmlTags.CLEF_LINE);
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
				Element attributesElement = doc.createElement(MusicXmlTags.MEASURE_ATTRIBUTES);
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
		Element backupElement = doc.createElement(MusicXmlTags.MEASURE_BACKUP);
		Element durationElement = doc.createElement(MusicXmlTags.NOTE_DURATION);
		durationElement.setTextContent(Integer.toString(divisionCountOf(duration)));
		backupElement.appendChild(durationElement);
		return backupElement;
	}

	private Element createForwardElement(Duration duration) {
		Element forwardElement = doc.createElement(MusicXmlTags.MEASURE_FORWARD);
		Element durationElement = doc.createElement(MusicXmlTags.NOTE_DURATION);
		durationElement.setTextContent(Integer.toString(divisionCountOf(duration)));
		forwardElement.appendChild(durationElement);
		return forwardElement;
	}

	private Element createNoteElement(Note note, int voiceNumber, Integer staffNumber, boolean chordTag) {
		final Element noteElement = this.doc.createElement(MusicXmlTags.NOTE);

		// TODO: Write other attributes.
		//Chord
		if (chordTag) {
			final Element chordElement = this.doc.createElement(MusicXmlTags.NOTE_CHORD);
			noteElement.appendChild(chordElement);
		}

		// Pitch
		noteElement.appendChild(createPitchElement(note.getPitch()));

		// Duration
		noteElement.appendChild(createDurationElement(note.getDuration()));

		// Voice
		noteElement.appendChild(createVoiceElement(voiceNumber));

		// Appearance
		addDurationAppearanceElementsToNoteElement(noteElement, note.getDuration());

		// Staff
		if (staffNumber != null) {
			noteElement.appendChild(createStaffElement(staffNumber));
		}

		// Notations (musical notations, e.g. articulations, dynamics, fermata, slur)
		createNotationsElement(note).ifPresent(notationsElement -> noteElement.appendChild(notationsElement));

		return noteElement;
	}

	private Element createStaffElement(Integer staffNumber) {
		final Element staffElement = doc.createElement(MusicXmlTags.NOTE_STAFF);
		staffElement.setTextContent(staffNumber.toString());
		return staffElement;
	}

	private Element createDurationElement(Duration duration) {
		final Element durationElement = this.doc.createElement(MusicXmlTags.NOTE_DURATION);
		final int durValue = divisionCountOf(duration);

		durationElement.setTextContent(Integer.toString(durValue));

		return durationElement;
	}

	private Element createVoiceElement(int voice) {
		Element voiceElement = doc.createElement(MusicXmlTags.NOTE_VOICE);
		voiceElement.setTextContent(Integer.toString(voice));
		return voiceElement;
	}

	private int divisionCountOf(Duration duration) {
		return ((this.divisions * Durations.QUARTER.getDenominator())
				/ duration.getDenominator())
				* duration.getNumerator();
	}

	private Element createPitchElement(Pitch pitch) {
		final Element pitchElement = this.doc.createElement(MusicXmlTags.NOTE_PITCH);
		final Element step = this.doc.createElement(MusicXmlTags.PITCH_STEP);
		step.setTextContent(pitch.getBase().toString());
		pitchElement.appendChild(step);

		final Element alter = this.doc.createElement(MusicXmlTags.PITCH_ALTER);
		alter.setTextContent(Integer.toString(pitch.getAlter()));
		pitchElement.appendChild(alter);

		final Element octave = this.doc.createElement(MusicXmlTags.PITCH_OCT);
		octave.setTextContent(Integer.toString(pitch.getOctave()));
		pitchElement.appendChild(octave);

		return pitchElement;
	}

	private Optional<Element> createNotationsElement(Note note) {
		final Element notationsElement = this.doc.createElement(MusicXmlTags.NOTATIONS);

		// Articulations & fermata
		if (note.hasArticulations()) {
			if (note.hasArticulation(Articulation.FERMATA)) {
				notationsElement.appendChild(this.doc.createElement(MusicXmlTags.FERMATA));
			}

			// Don't create articulations element if fermata is the only articulation
			if (!(note.getArticulations().size() == 1 && note.hasArticulation(Articulation.FERMATA))) {
				notationsElement.appendChild(createArticulationsElement(note.getArticulations()));
			}
		}

		// Markings
		if (note.hasMarkings()) {
			for (Marking marking : note.getMarkings()) {
				if (note.getMarkingConnection(marking).get().isBeginning()) {
					notationsElement.appendChild(markingResolver.createStartElement(marking));
				}

				if (note.getMarkingConnection(marking).get().isEnd()) {
					notationsElement.appendChild(markingResolver.createStopElement(marking));
				}
			}
		}

		if (notationsElement.hasChildNodes()) {
			return Optional.of(notationsElement);
		} else {
			return Optional.empty();
		}
	}

	private Element createArticulationsElement(Collection<Articulation> articulations) {
		final Element articulationsElement = this.doc.createElement(MusicXmlTags.NOTE_ARTICULATIONS);

		for (Articulation articulation : articulations) {
			switch (articulation) {
				case ACCENT:
					articulationsElement.appendChild(this.doc.createElement(MusicXmlTags.ACCENT));
					break;
				case STACCATO:
					articulationsElement.appendChild(this.doc.createElement(MusicXmlTags.STACCATO));
					break;
				case TENUTO:
					articulationsElement.appendChild(this.doc.createElement(MusicXmlTags.TENUTO));
					break;
				case FERMATA:
					break;
				default:
					throw new IllegalStateException("Unexpected articulation: " + articulation);
			}
		}

		return articulationsElement;
	}

	private Element createRestElement(Rest rest, int voice, Integer staffNumber) {
		final Element restElement = this.doc.createElement(MusicXmlTags.NOTE);
		restElement.appendChild(this.doc.createElement(MusicXmlTags.NOTE_REST));
		restElement.appendChild(createDurationElement(rest.getDuration()));

		// Add voice
		restElement.appendChild(createVoiceElement(voice));

		// Add duration appearance
		addDurationAppearanceElementsToNoteElement(restElement, rest.getDuration());

		// Staff
		if (staffNumber != null) {
			restElement.appendChild(createStaffElement(staffNumber));
		}

		return restElement;
	}

	private void addDurationAppearanceElementsToNoteElement(Element noteElement, Duration duration) {
		DurationAppearanceProvider.INSTANCE.getAppearanceElements(duration, this.doc)
				.forEach(element -> noteElement.appendChild(element));
	}

	private static class MarkingResolver {
		private static final int MAX_MARKING_NUMBER = 6;
		private static final Map<Marking.Type, String> MARKING_TYPES = createMarkingTypes();

		private static Map<Marking.Type, String> createMarkingTypes() {
			Map<Marking.Type, String> markingTypes = new HashMap<>();
			markingTypes.put(Marking.Type.SLUR, MusicXmlTags.SLUR);
			markingTypes.put(Marking.Type.GLISSANDO, MusicXmlTags.GLISSANDO);
			return Collections.unmodifiableMap(markingTypes);
		}

		private final Document document;
		private Integer nextAvailableMarkingNumber = Integer.valueOf(1);

		MarkingResolver(Document document) {
			this.document = document;
		}

		private Map<Marking, Integer> unresolvedMarkings = new HashMap<>();
		private Set<Integer> usedMarkingNumbers = new HashSet<>(MAX_MARKING_NUMBER);

		Element createStartElement(Marking marking) {
			Element markingElement = document.createElement(MARKING_TYPES.get(marking.getType()));

			final Integer markingNumber = getNextAvailableMarkingNumber();
			unresolvedMarkings.put(marking, markingNumber);

			markingElement.setAttribute(MusicXmlTags.MARKING_NUMBER, markingNumber.toString());

			markingElement.setAttribute(MusicXmlTags.MARKING_TYPE, MusicXmlTags.MARKING_TYPE_START);
			return markingElement;
		}

		Element createStopElement(Marking marking) {
			Element markingElement = document.createElement(MARKING_TYPES.get(marking.getType()));

			markingElement.setAttribute(MusicXmlTags.MARKING_NUMBER, unresolvedMarkings.get(marking).toString());

			usedMarkingNumbers.remove(unresolvedMarkings.get(marking));
			unresolvedMarkings.remove(marking);

			markingElement.setAttribute(MusicXmlTags.MARKING_TYPE, MusicXmlTags.MARKING_TYPE_STOP);

			return markingElement;
		}

		private Integer getNextAvailableMarkingNumber() {
			Integer availableNumber = nextAvailableMarkingNumber;
			usedMarkingNumbers.add(availableNumber);

			for (int next = 1; next <= MAX_MARKING_NUMBER; ++next) {
				if (!usedMarkingNumbers.contains(next)) {
					nextAvailableMarkingNumber = next;
					break;
				}
			}

			return availableNumber;
		}
	}
}
