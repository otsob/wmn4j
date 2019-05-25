/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
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
import java.util.HashSet;
import java.util.List;
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
	public void writeToFile(String path) {
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
			final StreamResult result = new StreamResult(new File(path));

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

		final Optional<Element> attributes = createMeasureAttributesElement(measure, prevMeasure);
		attributes.ifPresent(attrElement -> measureElement.appendChild(attrElement));

		final List<Integer> voiceNumber = measure.getVoiceNumbers();
		for (Integer voiceNumbers : voiceNumber) {
			for (Durational dur : measure.getVoice(voiceNumbers)) {
				if (dur instanceof Rest) {
					measureElement.appendChild(createRestElement((Rest) dur, voiceNumbers, 0));
				}

				if (dur instanceof Note) {
					measureElement.appendChild(createNoteElement((Note) dur, voiceNumbers, 0, false));
				}

				if (dur instanceof Chord) {
					boolean useChordTag = false;
					for (Note note : (Chord) dur) {
						measureElement.appendChild(createNoteElement(note, voiceNumbers, 0, useChordTag));
						useChordTag = true;
					}
				}
			}
			// Backup
		}

		return measureElement;
	}

	private Optional<Element> createMeasureAttributesElement(Measure measure, Measure prevMeasure) {
		final Element attrElement = doc.createElement(MusicXmlTags.MEASURE_ATTRIBUTES);

		//Divisions
		if (prevMeasure == null) {
			attrElement.appendChild(createDivisionsElement());
		}

		//Key signature
		if (!measure.getKeySignature().equals(keySigInEffect)) {
			attrElement.appendChild(createKeySignatureElement(measure));
			this.keySigInEffect = measure.getKeySignature();
		}

		//Time signature
		if (!measure.getTimeSignature().equals(timeSigInEffect)) {
			attrElement.appendChild(createTimeSignatureElement(measure));
			this.timeSigInEffect = measure.getTimeSignature();
		}

		//Clef
		if (!measure.getClef().equals(clefInEffect)) {
			attrElement.appendChild(createClefElement(measure));
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

	private Element createKeySignatureElement(Measure measure) {
		Element keySigElement = doc.createElement(MusicXmlTags.MEAS_ATTR_KEY);
		Element fifthsElement = doc.createElement(MusicXmlTags.MEAS_ATTR_KEY_FIFTHS);

		int fifths = measure.getKeySignature().getNumberOfSharps();
		if (fifths == 0) {
			fifths = measure.getKeySignature().getNumberOfFlats() * -1;
		}
		fifthsElement.setTextContent(Integer.toString(fifths));

		keySigElement.appendChild(fifthsElement);

		return keySigElement;
	}

	private Element createTimeSignatureElement(Measure measure) {
		Element timeSigElement = doc.createElement(MusicXmlTags.MEAS_ATTR_TIME);
		Element beatsElement = doc.createElement(MusicXmlTags.MEAS_ATTR_BEATS);
		Element beatTypeElement = doc.createElement(MusicXmlTags.MEAS_ATTR_BEAT_TYPE);

		beatsElement.setTextContent(Integer.toString(measure.getTimeSignature().getBeatCount()));
		beatTypeElement.setTextContent(Integer.toString(measure.getTimeSignature().getBeatDuration().getDenominator()));

		timeSigElement.appendChild(beatsElement);
		timeSigElement.appendChild(beatTypeElement);

		return timeSigElement;
	}

	private Element createClefElement(Measure measure) {
		Element clefElement = doc.createElement(MusicXmlTags.CLEF);
		Element signElement = doc.createElement(MusicXmlTags.CLEF_SIGN);

		switch (measure.getClef().getSymbol()) {
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
				throw new IllegalStateException("Unexpected clef symbol: " + measure.getClef().getSymbol());
		}

		Element lineElement = doc.createElement(MusicXmlTags.CLEF_LINE);
		lineElement.setTextContent(Integer.toString(measure.getClef().getLine()));

		clefElement.appendChild(signElement);
		clefElement.appendChild(lineElement);

		return clefElement;
	}

	private Element createNoteElement(Note note, int voice, int staff, boolean chordTag) {
		final Element noteElement = this.doc.createElement(MusicXmlTags.NOTE);
		noteElement.appendChild(createPitchElement(note.getPitch()));
		noteElement.appendChild(createDurationElement(note.getDuration()));

		// TODO: Write other attributes.
		if (chordTag) {
			final Element chordElement = this.doc.createElement(MusicXmlTags.NOTE_CHORD);
			noteElement.appendChild(chordElement);
		}

		return noteElement;
	}

	private Element createDurationElement(Duration duration) {
		final Element durationElement = this.doc.createElement(MusicXmlTags.NOTE_DURATION);
		final int durValue = divisionCountOf(duration);

		durationElement.setTextContent(Integer.toString(durValue));

		return durationElement;
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
		// TODO: Set voice and staff

		return restElement;
	}

}
