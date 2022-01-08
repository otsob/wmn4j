/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wmn4j.Wmn4j;
import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.GraceNote;
import org.wmn4j.notation.GraceNoteChord;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Ornament;
import org.wmn4j.notation.Ornamental;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.TimeSignature;
import org.wmn4j.notation.access.Offset;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

final class StaxScoreWriter implements MusicXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(StaxScoreWriter.class);

	private final Score score;
	private final List<String> partIds = new ArrayList<>();
	private XMLStreamWriter writer;
	private final int divisions;
	private NotationWriteResolver notationResolver;

	static void writeValue(XMLStreamWriter writer, String tag, String value) throws XMLStreamException {
		writer.writeStartElement(tag);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}

	StaxScoreWriter(Score score) {
		this.score = score;
		this.divisions = computeDivisions(score.partwiseIterator());
	}

	private String getDTD(String version) {
		StringBuilder builder = new StringBuilder();

		builder.append("<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML ");
		builder.append(version);
		builder.append(" Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">");

		return builder.toString();
	}

	@Override
	public void write(Path path) {

		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
			notationResolver = new NotationWriteResolver(writer);

			writer.writeStartDocument("UTF-8", "1.0");
			final String version = "4.0";
			writer.writeDTD(getDTD(version));

			writer.writeStartElement(Tags.SCORE_PARTWISE);
			writer.writeAttribute(Tags.VERSION, version);

			writeHead();
			writePartList();
			writeParts();

			// End the score-partwise element
			writer.writeEndElement();
			writer.writeEndDocument();

			writer.flush();
			writer.close();

			Files.writeString(path, indentXmlString(outputStream.toString(StandardCharsets.UTF_8)),
					StandardCharsets.UTF_8);
		} catch (XMLStreamException | IOException | TransformerException e) {
			LOG.error("Writing MusicXML failed with {}", e.getMessage());
		}
	}

	private String indentXmlString(String xml) throws TransformerException {

		Transformer transformer = TransformerFactory.newInstance().newTransformer();

		final String yes = "yes";
		transformer.setOutputProperty(OutputKeys.INDENT, yes);
		transformer.setOutputProperty(OutputKeys.STANDALONE, yes);

		StringWriter output = new StringWriter();
		transformer.transform(new StreamSource(new StringReader(xml)), new StreamResult(output));

		return output.toString();
	}

	private void writeValue(String tag, String value) throws XMLStreamException {
		writer.writeStartElement(tag);
		writer.writeCharacters(value);
		writer.writeEndElement();
	}

	private void writeHead() throws XMLStreamException {

		// Write work element
		final var title = score.getTitle();
		if (title.isPresent()) {
			writer.writeStartElement(Tags.WORK);
			writeValue(Tags.WORK_TITLE, title.get());
			writer.writeEndElement();
		}

		// Write movement title
		final var movementTitle = score.getAttribute(Score.Attribute.MOVEMENT_TITLE);
		if (movementTitle.isPresent()) {
			writeValue(Tags.MOVEMENT_TITLE, movementTitle.get());
		}

		// Write identification
		writeIdentification();
	}

	private void writeIdentification() throws XMLStreamException {
		writer.writeStartElement(Tags.IDENTIFICATION);
		final var composer = score.getAttribute(Score.Attribute.COMPOSER);
		if (composer.isPresent()) {
			writer.writeStartElement(Tags.CREATOR);
			writer.writeAttribute(Tags.TYPE, Tags.COMPOSER);
			writer.writeCharacters(composer.get());
			writer.writeEndElement();
		}

		final var arranger = score.getAttribute(Score.Attribute.ARRANGER);
		if (arranger.isPresent()) {
			writer.writeStartElement(Tags.CREATOR);
			writer.writeAttribute(Tags.TYPE, Tags.ARRANGER);
			writer.writeCharacters(arranger.get());
			writer.writeEndElement();
		}

		writer.writeStartElement(Tags.ENCODING);

		writeValue(Tags.SOFTWARE, Wmn4j.getNameWithVersion());

		final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		writeValue(Tags.ENCODING_DATE, dateFormat.format(new Date()));

		// End encoding
		writer.writeEndElement();
		// End identification
		writer.writeEndElement();
	}

	private void writePartList() throws XMLStreamException {
		writer.writeStartElement(Tags.PART_LIST);

		int partNumber = 1;

		for (Part part : score) {
			writer.writeStartElement(Tags.SCORE_PART);
			String partId = "P" + partNumber++;
			writer.writeAttribute(Tags.ID, partId);

			// Name is always required, default to empty string if not defined.
			final var name = part.getName().orElse("");

			writeValue(Tags.PART_NAME, name);

			final var abbrName = part.getAttribute(Part.Attribute.ABBREVIATED_NAME);
			if (abbrName.isPresent()) {
				writeValue(Tags.PART_ABBREVIATION, abbrName.get());
			}

			partIds.add(partId);
			writer.writeEndElement();
		}

		writer.writeEndElement();
	}

	private void writeParts() throws XMLStreamException {
		for (int i = 0; i < score.getPartCount(); ++i) {
			final String partId = partIds.get(i);
			final Part part = score.getPart(i);
			writePart(partId, part);
		}
	}

	private int computeDivisions(Iterator<Durational> durationalIterator) {
		Set<Integer> denominators = new HashSet<>();

		while (durationalIterator.hasNext()) {
			denominators.add(durationalIterator.next().getDuration().getDenominator());
		}

		// Find the lowest common denominator of all the different denominators of Durationals in the Score
		// Start with the denominator of a quarter, because in MusicXML divisions are set in terms of
		// divisions per quarter note
		int lcd = Durations.QUARTER.getDenominator();
		for (Integer denominator : denominators) {
			lcd = (lcd * denominator)
					/ BigInteger.valueOf(lcd).gcd(BigInteger.valueOf(denominator)).intValue();
		}

		return lcd / Durations.QUARTER.getDenominator();
	}

	private void writePart(String partId, Part part) throws XMLStreamException {
		writer.writeStartElement(Tags.PART);
		writer.writeAttribute(Tags.ID, partId);

		final var staffNumbers = part.getStaffNumbers();
		final int first = part.hasPickupMeasure() ? 0 : 1;
		final boolean isMultiStaff = part.isMultiStaff();

		for (int m = first; m <= part.getFullMeasureCount(); ++m) {
			writeMeasure(part, staffNumbers, first, isMultiStaff, m);
		}

		writer.writeEndElement();
	}

	private void writeMeasure(Part part, List<Integer> staffNumbers, int first, boolean isMultiStaff, int m)
			throws XMLStreamException {
		writer.writeStartElement(Tags.MEASURE);
		writer.writeAttribute(Tags.NUMBER, Integer.toString(m));
		final Integer lastStaff = staffNumbers.get(staffNumbers.size() - 1);

		for (var s : staffNumbers) {
			Measure prev = null;
			if (m > first) {
				prev = part.getMeasure(s, m - 1);
			}
			Measure current = part.getMeasure(s, m);
			writeBarline(current, true);

			writeMeasureAttributes(current, prev, isMultiStaff, s);

			final int backup = writeMeasureContents(current, s, isMultiStaff);
			if (!s.equals(lastStaff)) {
				writeOffset(backup, false);
			}

			writeBarline(current, false);
		}

		writer.writeEndElement();
	}

	private void writeOffset(int divisions, boolean isForward) throws XMLStreamException {
		final String tag = isForward ? Tags.FORWARD : Tags.BACKUP;
		writer.writeStartElement(tag);
		writeValue(Tags.DURATION, Integer.toString(divisions));
		writer.writeEndElement();
	}

	private void writeBarline(Measure measure, boolean isLeft) throws XMLStreamException {
		final var barline = isLeft ? measure.getLeftBarline() : measure.getRightBarline();
		if (!barline.equals(Barline.NONE)) {
			writer.writeStartElement(Tags.BARLINE);
			writer.writeAttribute(Tags.LOCATION, isLeft ? Tags.LEFT : Tags.RIGHT);

			writeValue(Tags.BAR_STYLE, Transforms.barlineStyleToString(barline));

			String repeatDirection = null;

			if (barline.equals(Barline.REPEAT_LEFT)) {
				repeatDirection = Tags.FORWARD;
			} else if (barline.equals(Barline.REPEAT_RIGHT)) {
				repeatDirection = Tags.BACKWARD;
			}

			if (repeatDirection != null) {
				writer.writeStartElement(Tags.REPEAT);
				writer.writeAttribute(Tags.DIRECTION, repeatDirection);
				writer.writeEndElement();
			}

			writer.writeEndElement();
		}
	}

	private void writeMeasureAttributes(Measure current, Measure previous, boolean isMultiStaff, Integer staffNumber)
			throws XMLStreamException {
		writer.writeStartElement(Tags.ATTRIBUTES);

		// Write divisions
		if (previous == null) {
			writeValue(Tags.DIVISIONS, Integer.toString(divisions));
		}

		// Write key sig
		final var keySig = current.getKeySignature();
		if (previous == null || !keySig.equals(previous.getKeySignature())) {
			writer.writeStartElement(Tags.KEY);
			final int fifths = keySig.getSharpCount() - keySig.getFlatCount();

			writeValue(Tags.FIFTHS, Integer.toString(fifths));

			writer.writeEndElement();
		}

		// Write time sig
		final var timeSig = current.getTimeSignature();
		if (previous == null || !timeSig.equals(previous.getTimeSignature())) {
			writer.writeStartElement(Tags.TIME);

			final var symbol = Transforms.timeSignatureTypeToString(timeSig.getSymbol());
			if (symbol != null) {
				writer.writeAttribute(Tags.SYMBOL, symbol);
			}

			writeValue(Tags.BEATS, Integer.toString(timeSig.getBeatCount()));

			writeValue(Tags.BEAT_TYPE, Integer.toString(timeSig.getBeatDuration().getDenominator()));

			writer.writeEndElement();
		}

		// Write clef
		final var clef = current.getClef();
		if (previous == null || !clef.equals(getLastClefInEffect(previous))) {
			writeClef(isMultiStaff, staffNumber, clef);
		}

		writer.writeEndElement();
	}

	private void writeClef(boolean isMultiStaff, Integer staffNumber, Clef clef) throws XMLStreamException {
		writer.writeStartElement(Tags.CLEF);

		if (isMultiStaff) {
			writer.writeAttribute(Tags.NUMBER, staffNumber.toString());
		}

		writeValue(Tags.SIGN, Transforms.clefSymbolToString(clef.getSymbol()));

		writeValue(Tags.LINE, Integer.toString(clef.getLine()));

		writer.writeEndElement();
	}

	private Clef getLastClefInEffect(Measure measure) {
		if (!measure.containsClefChanges()) {
			return measure.getClef();
		}

		final List<Offset<Clef>> clefChanges = measure.getClefChanges();
		return clefChanges.get(clefChanges.size() - 1).get();
	}

	private int writeMeasureContents(Measure measure, Integer staff, boolean isMultiStaff)
			throws XMLStreamException {

		final List<Offset<Clef>> undealtClefChanges = new ArrayList<>(measure.getClefChanges());

		final var voiceNumbers = measure.getVoiceNumbers();
		Duration offset = null;

		Integer staffNumber = isMultiStaff ? staff : null;
		final Integer lastVoice = voiceNumbers.get(voiceNumbers.size() - 1);

		for (Integer voice : voiceNumbers) {
			for (int i = 0; i < measure.getVoiceSize(voice); ++i) {
				final var durational = measure.get(voice, i);

				if (offset == null) {
					offset = durational.getDuration();
				} else {
					offset = offset.add(durational.getDuration());
				}

				if (durational.getDuration().hasExpression()) {
					writeDurational(staffNumber, voice, durational);
				} else {
					writeDecomposedDurationals(staffNumber, voice, durational, measure.getTimeSignature());
				}

				handleMidMeasureClefChanges(isMultiStaff, undealtClefChanges, offset, staffNumber);
			}

			// Create backup element always on change of voice apart from last voice.
			if (!voice.equals(lastVoice)) {
				writeOffset(toDivisionCount(offset), false);
				offset = null;
			}
		}

		return toDivisionCount(offset);
	}

	private void writeDurational(Integer staffNumber, Integer voice, Durational durational) throws XMLStreamException {
		if (durational instanceof Note) {
			writeNote((Note) durational, voice, staffNumber, false);
		} else if (durational instanceof Chord) {
			writeChord((Chord) durational, voice, staffNumber);
		} else if (durational instanceof Rest) {
			writeRest((Rest) durational, voice, staffNumber);
		}
	}

	private void writeDecomposedDurationals(Integer staffNumber, Integer voice, Durational durational,
			TimeSignature timeSig) throws XMLStreamException {

		final var decomposedDurations = durational.getDuration().decompose(timeSig.getTotalDuration());

		if (durational instanceof Note) {
			for (Duration duration : decomposedDurations) {
				NoteBuilder builder = new NoteBuilder((Note) durational);
				builder.setDuration(duration);
				writeNote(builder.build(), voice, staffNumber, false);
			}
		} else if (durational instanceof Chord) {
			for (Duration duration : decomposedDurations) {
				ChordBuilder builder = new ChordBuilder((Chord) durational);
				builder.setDuration(duration);
				writeChord(builder.build(), voice, staffNumber);
			}
		} else if (durational instanceof Rest) {
			for (Duration duration : decomposedDurations) {
				RestBuilder builder = new RestBuilder((Rest) durational);
				builder.setDuration(duration);
				writeRest(builder.build(), voice, staffNumber);
			}
		}
	}

	private void handleMidMeasureClefChanges(boolean isMultiStaff, List<Offset<Clef>> undealtClefChanges,
			Duration cumulatedDuration, Integer staffNumber) throws XMLStreamException {

		List<Offset<Clef>> handledClefChanges = new ArrayList<>();

		for (Offset<Clef> clefChange : undealtClefChanges) {

			// Backward elements are not required for clef changes at beginning of measure.
			if (clefChange.getDuration().isEmpty()) {
				continue;
			}

			final Duration offsetDuration = clefChange.getDuration().get();

			if (offsetDuration.isShorterThan(cumulatedDuration) || offsetDuration.equals(cumulatedDuration)) {

				// Backup
				if (!offsetDuration.equals(cumulatedDuration)) {
					final int backup = toDivisionCount(cumulatedDuration.subtract(offsetDuration));
					writeOffset(backup, false);
				}

				// Clef wrapped inside an attributes element
				writer.writeStartElement(Tags.ATTRIBUTES);
				writeClef(isMultiStaff, staffNumber, clefChange.get());
				writer.writeEndElement();

				// Forward
				if (!offsetDuration.equals(cumulatedDuration)) {
					final int forward = toDivisionCount(cumulatedDuration.subtract(offsetDuration));
					writeOffset(forward, true);
				}

				handledClefChanges.add(clefChange);
			}
		}

		undealtClefChanges.removeAll(handledClefChanges);
	}

	private int toDivisionCount(Duration duration) {
		return ((divisions * Durations.QUARTER.getDenominator()) / duration.getDenominator()) * duration.getNumerator();
	}

	private void writeNote(Note note, Integer voice, Integer staff, boolean addChordTag)
			throws XMLStreamException {

		writeGraceNotes(note, voice, staff, Ornament.Type.GRACE_NOTES);

		writer.writeStartElement(Tags.NOTE);

		if (addChordTag) {
			writer.writeStartElement(Tags.CHORD);
			writer.writeEndElement();
		}

		writePitch(note.getPitch());
		writeDuration(note.getDuration());
		writeVoice(voice);
		DurationAppearanceWriter.INSTANCE.writeAppearanceElements(note.getDuration(), writer);
		writeStaff(staff);
		writeNotations(note, note.getArticulations(), note.getNotations(), note.getOrnaments());

		writer.writeEndElement();

		writeGraceNotes(note, voice, staff, Ornament.Type.SUCCEEDING_GRACE_NOTES);
	}

	private void writeGraceNotes(Note note, Integer voice, Integer staff, Ornament.Type ornamentType)
			throws XMLStreamException {
		final var graceNotes = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(ornamentType))
				.findFirst();
		if (graceNotes.isPresent()) {
			final var ornamentalNotes = graceNotes.get().getOrnamentalNotes();
			for (var ornamental : ornamentalNotes) {
				if (ornamental instanceof GraceNote) {
					writeGraceNote((GraceNote) ornamental, voice, staff, false);
				} else if (ornamental instanceof GraceNoteChord) {
					writeGraceNoteChord((GraceNoteChord) ornamental, voice, staff);
				}
			}
		}
	}

	private void writeGraceNote(GraceNote note, Integer voice, Integer staff, boolean addChordTag)
			throws XMLStreamException {
		writer.writeStartElement(Tags.NOTE);

		writer.writeStartElement(Tags.GRACE);
		if (note.getType().equals(Ornamental.Type.ACCIACCATURA)) {
			writer.writeAttribute(Tags.SLASH, Tags.YES);
		}
		writer.writeEndElement();

		if (addChordTag) {
			writer.writeStartElement(Tags.CHORD);
			writer.writeEndElement();
		}

		writePitch(note.getPitch());
		writeVoice(voice);
		DurationAppearanceWriter.INSTANCE.writeAppearanceElements(note.getDisplayableDuration(), writer);
		writeStaff(staff);
		writeNotations(note, note.getArticulations(), note.getNotations(), note.getOrnaments());

		writer.writeEndElement();
	}

	private void writeGraceNoteChord(GraceNoteChord chord, Integer voice, Integer staff) throws XMLStreamException {
		for (int i = 0; i < chord.getNoteCount(); ++i) {
			writeGraceNote(chord.getNote(i), voice, staff, i > 0);
		}
	}

	private void writeChord(Chord chord, Integer voice, Integer staff) throws XMLStreamException {
		for (int i = 0; i < chord.getNoteCount(); ++i) {
			writeNote(chord.getNote(i), voice, staff, i > 0);
		}
	}

	private void writeRest(Rest rest, Integer voice, Integer staff) throws XMLStreamException {
		writer.writeStartElement(Tags.NOTE);

		writer.writeStartElement(Tags.REST);
		writer.writeEndElement();

		writeDuration(rest.getDuration());
		writeVoice(voice);
		DurationAppearanceWriter.INSTANCE.writeAppearanceElements(rest.getDuration(), writer);
		writeStaff(staff);

		writer.writeEndElement();
	}

	private void writeDuration(Duration duration) throws XMLStreamException {
		writeValue(Tags.DURATION, Integer.toString(toDivisionCount(duration)));
	}

	private void writePitch(Pitch pitch) throws XMLStreamException {
		writer.writeStartElement(Tags.PITCH);

		writeValue(Tags.STEP, pitch.getBase().toString());

		writeValue(Tags.ALTER, Integer.toString(pitch.getAccidental().getAlterationInt()));

		writeValue(Tags.OCTAVE, Integer.toString(pitch.getOctave()));

		writer.writeEndElement();
	}

	private void writeVoice(Integer voice) throws XMLStreamException {
		if (voice != null) {
			writeValue(Tags.VOICE, voice.toString());
		}
	}

	private void writeStaff(Integer staff) throws XMLStreamException {
		if (staff != null) {
			writeValue(Tags.STAFF, staff.toString());
		}
	}

	private void writeNotations(Notation.Connectable connectable, Set<Articulation> articulations,
			Set<Notation> notations, Collection<Ornament> ornaments) throws XMLStreamException {
		writer.writeStartElement(Tags.NOTATIONS);
		writeArticulations(articulations);
		writeConnectedNotations(connectable, notations);
		writeOrnaments(ornaments);

		writer.writeEndElement();
	}

	private void writeArticulations(Collection<Articulation> articulations) throws XMLStreamException {
		if (articulations.isEmpty()) {
			return;
		}

		if (articulations.contains(Articulation.FERMATA)) {
			writeValue(Tags.FERMATA, "");
		}

		// The articulations element is only created for articulations other than fermata.
		if (!(articulations.size() == 1 && articulations.contains(Articulation.FERMATA))) {
			writer.writeStartElement(Tags.ARTICULATIONS);
			for (Articulation articulation : articulations) {
				String tag = Transforms.articulationToTag(articulation);
				if (tag != null) {
					writeValue(tag, "");
				}
			}
			writer.writeEndElement();
		}
	}

	private void writeOrnaments(Collection<Ornament> ornaments) throws XMLStreamException {
		final Predicate<Ornament> isGraceNote = (o) -> o.getType()
				.equals(Ornament.Type.SUCCEEDING_GRACE_NOTES)
				|| o.getType().equals(Ornament.Type.GRACE_NOTES);

		// If ornaments is empty or the only ornament is of type grace notes then do not add ornaments element.
		if (ornaments.size() <= 1 && ornaments.stream().anyMatch(isGraceNote)) {
			return;
		}

		writer.writeStartElement(Tags.ORNAMENTS);
		for (Ornament ornament : ornaments) {
			if (isGraceNote.test(ornament)) {
				continue;
			}

			final var type = ornament.getType();
			final String tag = Transforms.ornamentToTag(type);
			if (tag != null) {
				String content;
				switch (type) {
					case SINGLE_TREMOLO:
						content = "1";
						break;
					case DOUBLE_TREMOLO:
						content = "2";
						break;
					case TRIPLE_TREMOLO:
						content = "3";
						break;
					default:
						content = "";
				}

				writeValue(tag, content);
			}
		}
		writer.writeEndElement();
	}

	private void writeConnectedNotations(Notation.Connectable connectable, Collection<Notation> notations)
			throws XMLStreamException {
		if (notations.isEmpty()) {
			return;
		}

		for (Notation notation : notations) {
			final var type = notation.getType();

			if (notationResolver.canStartOrStop(type)) {
				final var connection = connectable.getConnection(notation);
				if (connection.isEmpty()) {
					LOG.warn("Could not get expected notation connection of type {} for {}", type, connectable);
					continue;
				}

				if (connection.get().isBeginning()) {
					notationResolver.writeNotationStartElement(notation);
				}

				if (connection.get().isEnd()) {
					notationResolver.writeNotationStopElement(notation);
				}
			}

			if (notationResolver.isArpeggiation(type)) {
				notationResolver.writeArpeggiationElement(notation);
			}
		}
	}
}
