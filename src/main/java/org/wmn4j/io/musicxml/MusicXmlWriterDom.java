/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.KeySignature;
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
import java.util.ArrayList;
import java.util.Collections;
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
	private final int divisions;

	private KeySignature keySigInEffect;
	private TimeSignature timeSigInEffect;
	private Clef clefInEffect;

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
			final Element rootElement = this.doc.createElement(MusicXmlTags.SCORE_PARTWISE);
			rootElement.setAttribute(MusicXmlTags.MUSICXML_VERSION, MUSICXML_VERSION_NUMBER);
			this.doc.appendChild(rootElement);

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

		Measure prevMeasure = null;
		timeSigInEffect = null;
		keySigInEffect = null;
		clefInEffect = null;

		for (Measure measure : part) {
			partElement.appendChild(createMeasureElement(measure, prevMeasure));
			prevMeasure = measure;
		}

		return partElement;
	}

	private Element createMultiStaffPart(MultiStaffPart part, String partId) {
		return null;
	}

	private Element createMeasureElement(Measure measure, Measure prevMeasure) {
		final Element measureElement = doc.createElement(MusicXmlTags.MEASURE);
		measureElement.setAttribute(MusicXmlTags.MEASURE_NUM, Integer.toString(measure.getNumber()));

		//Left barline
		if (!measure.getLeftBarline().equals(Barline.SINGLE) && !measure.getLeftBarline().equals(Barline.NONE)) {
			measureElement.appendChild(
					createBarlineElement(measure.getLeftBarline(), MusicXmlTags.BARLINE_LOCATION_LEFT));
		}

		//Attributes
		final Optional<Element> attributes = createMeasureAttributesElement(measure, prevMeasure);
		attributes.ifPresent(attrElement -> measureElement.appendChild(attrElement));

		//Set up handling possible mid-measure clef changes
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
					measureElement.appendChild(createRestElement((Rest) dur, voiceNumber, 0));
				}

				if (dur instanceof Note) {
					measureElement.appendChild(createNoteElement((Note) dur, voiceNumber, 0, false));
				}

				if (dur instanceof Chord) {
					boolean useChordTag = false;
					for (Note note : (Chord) dur) {
						measureElement.appendChild(createNoteElement(note, voiceNumber, 0, useChordTag));
						useChordTag = true;
					}
				}

				// Handle mid-measure clef changes
				handleMidMeasureClefChanges(measureElement, undealtClefChanges, cumulatedDuration);

			}

			// In case of multiple voices, backup to the beginning of the measure
			// Do not backup if it is the last voice to be handled
			voicesHandled++;
			if (voicesHandled < measure.getVoiceCount()) {
				measureElement.appendChild(createBackupElement(cumulatedDuration));
			} else {
				if (!measure.isPickup()
						&& cumulatedDuration.isShorterThan(measure.getTimeSignature().getTotalDuration())) {
					Duration duration = measure.getTimeSignature().getTotalDuration().subtract(cumulatedDuration);
					measureElement.appendChild(createForwardElement(duration));
				}
			}
		}

		//Right barline
		if (!measure.getRightBarline().equals(Barline.SINGLE) && !measure.getRightBarline().equals(Barline.NONE)) {
			measureElement.appendChild(
					createBarlineElement(measure.getRightBarline(), MusicXmlTags.BARLINE_LOCATION_RIGHT));
		}

		return measureElement;
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

	private Optional<Element> createMeasureAttributesElement(Measure measure, Measure prevMeasure) {
		final Element attrElement = doc.createElement(MusicXmlTags.MEASURE_ATTRIBUTES);

		//Divisions
		if (prevMeasure == null) {
			attrElement.appendChild(createDivisionsElement());
		}

		//Key signature
		if (!measure.getKeySignature().equals(keySigInEffect)) {
			attrElement.appendChild(createKeySignatureElement(measure.getKeySignature()));
			this.keySigInEffect = measure.getKeySignature();
		}

		//Time signature
		if (!measure.getTimeSignature().equals(timeSigInEffect)) {
			attrElement.appendChild(createTimeSignatureElement(measure.getTimeSignature()));
			this.timeSigInEffect = measure.getTimeSignature();
		}

		//Clef
		if (!measure.getClef().equals(clefInEffect)) {
			attrElement.appendChild(createClefElement(measure.getClef()));
			this.clefInEffect = measure.getClef();
		}

		if (attrElement.hasChildNodes()) {
			return Optional.of(attrElement);
		} else {
			return Optional.empty();
		}
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

	private Element createClefElement(Clef clef) {
		Element clefElement = doc.createElement(MusicXmlTags.CLEF);
		Element signElement = doc.createElement(MusicXmlTags.CLEF_SIGN);

		switch (clef.getSymbol()) {
			case G:
				signElement.setTextContent("G");
				break;
			case F:
				signElement.setTextContent("F");
				break;
			case C:
				signElement.setTextContent("C");
				break;
			case PERCUSSION:
				signElement.setTextContent("percussion");
				break;
			default:
				throw new IllegalStateException("Unexpected clef symbol: " + clef.getSymbol());
		}

		Element lineElement = doc.createElement(MusicXmlTags.CLEF_LINE);
		lineElement.setTextContent(Integer.toString(clef.getLine()));

		clefElement.appendChild(signElement);
		clefElement.appendChild(lineElement);

		return clefElement;
	}

	private void handleMidMeasureClefChanges(
			Element measureElement, Map<Duration, Clef> undealtClefChanges, Duration cumulatedDuration) {

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
				Element clefElement = createClefElement(undealtClefChanges.get(offset));

				attributesElement.appendChild(clefElement);
				measureElement.appendChild(attributesElement);

				clefInEffect = undealtClefChanges.get(offset);

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

	private Element createNoteElement(Note note, int voice, int staff, boolean chordTag) {
		final Element noteElement = this.doc.createElement(MusicXmlTags.NOTE);

		// TODO: Write other attributes.
		//Chord
		if (chordTag) {
			final Element chordElement = this.doc.createElement(MusicXmlTags.NOTE_CHORD);
			noteElement.appendChild(chordElement);
		}

		//Pitch
		noteElement.appendChild(createPitchElement(note.getPitch()));

		//Duration
		noteElement.appendChild(createDurationElement(note.getDuration()));

		//Voice
		noteElement.appendChild(createVoiceElement(voice));

		return noteElement;
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

	private Element createRestElement(Rest rest, int voice, int staff) {
		final Element restElement = this.doc.createElement(MusicXmlTags.NOTE);
		restElement.appendChild(this.doc.createElement(MusicXmlTags.NOTE_REST));
		restElement.appendChild(createDurationElement(rest.getDuration()));

		// TODO: Set staff
		//Voice
		restElement.appendChild(createVoiceElement(voice));

		return restElement;
	}

}
