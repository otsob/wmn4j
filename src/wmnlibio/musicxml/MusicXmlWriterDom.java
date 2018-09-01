/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibio.musicxml;

import java.io.File;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;

import wmnlibnotation.iterators.PartWiseScoreIterator;
import wmnlibnotation.iterators.ScoreIterator;
import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.MultiStaffPart;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Part;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Rest;
import wmnlibnotation.noteobjects.Score;
import wmnlibnotation.noteobjects.SingleStaffPart;

/**
 * 
 * @author Otso Björklund
 */
class MusicXmlWriterDom implements MusicXmlWriter {

	private Document doc;
	private final Score score;
	private final SortedMap<String, Part> partIdMap = new TreeMap<>();
	private final int divisions;

	public MusicXmlWriterDom(Score score) {
		this.score = score;
		this.divisions = computeDivisions(this.score);
	}

	@Override
	public void writeToFile(String path) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			// root elements
			this.doc = docBuilder.newDocument();
			Element rootElement = this.doc.createElement(MusicXmlTags.SCORE_PARTWISE);
			this.doc.appendChild(rootElement);

			// staff elements
			writePartList(rootElement);

			for (String partId : this.partIdMap.keySet()) {
				writePart(this.partIdMap.get(partId), rootElement, partId);
			}

			// write the content into xml file
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();

			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			DOMImplementation domImpl = doc.getImplementation();
			DocumentType doctype = domImpl.createDocumentType("doctype", "-//Recordare//DTD MusicXML 3.0 Partwise//EN",
					"http://www.musicxml.org/dtds/partwise.dtd");
			transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, doctype.getPublicId());
			transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, doctype.getSystemId());

			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));

			// Output to console for testing
			// StreamResult result = new StreamResult(System.out);

			transformer.transform(source, result);

			System.out.println("File saved!");
			this.doc = null;

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
	}

	private int computeDivisions(Score score) {

		ScoreIterator iter = new PartWiseScoreIterator(score);
		Duration shortest = Durations.QUARTER;

		while (iter.hasNext()) {
			Duration dur = iter.next().getDuration();
			if (dur.shorterThan(shortest))
				shortest = dur;
		}

		return 1;
	}

	private void writePartList(Element scoreRoot) {
		Element partList = doc.createElement(MusicXmlTags.PART_LIST);

		for (int i = 0; i < this.score.getPartCount(); ++i) {
			Element partElement = doc.createElement(MusicXmlTags.PLIST_SCORE_PART);
			String partId = "P" + (i + 1);
			Part part = this.score.getPart(i);
			this.partIdMap.put(partId, part);

			partElement.setAttribute(MusicXmlTags.PART_ID, partId);
			Element partName = doc.createElement(MusicXmlTags.PART_NAME);
			partName.setTextContent(part.getName());
			partElement.appendChild(partName);

			partList.appendChild(partElement);
		}

		scoreRoot.appendChild(partList);
	}

	private void writePart(Part part, Element scoreRoot, String partId) {
		Element partElement;

		if (part.isMultiStaff()) {
			partElement = createMultiStaffPart((MultiStaffPart) part, partId);
		} else {
			partElement = createSingleStaffPart((SingleStaffPart) part, partId);
		}

		scoreRoot.appendChild(partElement);
	}

	private Element createSingleStaffPart(SingleStaffPart part, String partId) {
		Element partElement = doc.createElement(MusicXmlTags.PART);
		partElement.setAttribute(MusicXmlTags.PART_ID, partId);

		Measure prevMeasure = null;

		for (Measure measure : part) {
			partElement.appendChild(createMeasureElement(measure, prevMeasure));
		}

		return partElement;
	}

	private Element createMultiStaffPart(MultiStaffPart part, String partId) {
		return null;
	}

	private Element createMeasureElement(Measure measure, Measure prevMeasure) {
		Element measureElement = doc.createElement(MusicXmlTags.MEASURE);
		measureElement.setAttribute(MusicXmlTags.MEASURE_NUM, Integer.toString(measure.getNumber()));

		if (prevMeasure == null) {
			Element attributes = createMeasureAttributesElement(measure, null);
		}

		List<Integer> voiceNumber = measure.getVoiceNumbers();
		for (Integer voiceNumbers : voiceNumber) {
			for (Durational dur : measure.getVoice(voiceNumbers)) {
				if (dur instanceof Rest)
					measureElement.appendChild(createRestElement((Rest) dur, voiceNumbers, 0));

				if (dur instanceof Note)
					measureElement.appendChild(createNoteElement((Note) dur, voiceNumbers, 0, false));

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

	private Element createMeasureAttributesElement(Measure measure, Measure prevMeasure) {
		Element attrElement = this.doc.createElement(MusicXmlTags.MEASURE_ATTRIBUTES);

		if (prevMeasure == null || !measure.getClef().equals(prevMeasure.getClef())) {

		}

		if (prevMeasure == null || measure.getKeySignature() != prevMeasure.getKeySignature()) {

		}

		if (prevMeasure == null || measure.getTimeSignature() != prevMeasure.getTimeSignature()) {

		}

		return attrElement;
	}

	private Element createNoteElement(Note note, int voice, int staff, boolean chordTag) {
		Element noteElement = this.doc.createElement(MusicXmlTags.NOTE);
		noteElement.appendChild(createPitchElement(note.getPitch()));
		noteElement.appendChild(createDurationElement(note.getDuration()));

		// TODO: Write other attributes.
		if (chordTag) {
			Element chordElement = this.doc.createElement(MusicXmlTags.NOTE_CHORD);
			noteElement.appendChild(chordElement);
		}

		return noteElement;
	}

	private Element createDurationElement(Duration duration) {
		Element durationElement = this.doc.createElement(MusicXmlTags.NOTE_DURATION);
		int durValue = 1;

		durationElement.setTextContent(Integer.toString(durValue));

		return durationElement;
	}

	private Element createPitchElement(Pitch pitch) {
		Element pitchElement = this.doc.createElement(MusicXmlTags.NOTE_PITCH);
		Element step = this.doc.createElement(MusicXmlTags.PITCH_STEP);
		step.setTextContent(pitch.getBase().toString());
		pitchElement.appendChild(step);

		Element alter = this.doc.createElement(MusicXmlTags.PITCH_ALTER);
		alter.setTextContent(Integer.toString(pitch.getAlter()));
		pitchElement.appendChild(alter);

		Element octave = this.doc.createElement(MusicXmlTags.PITCH_OCT);
		octave.setTextContent(Integer.toString(pitch.getOctave()));
		pitchElement.appendChild(octave);

		return pitchElement;
	}

	private Element createRestElement(Rest rest, int voice, int staff) {
		Element restElement = this.doc.createElement(MusicXmlTags.NOTE);
		restElement.appendChild(this.doc.createElement(MusicXmlTags.NOTE_REST));
		restElement.appendChild(createDurationElement(rest.getDuration()));
		// TODO: Set voice and staff

		return restElement;
	}

}
