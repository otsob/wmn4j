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
import org.wmn4j.notation.Lyric;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.OptionallyPitched;
import org.wmn4j.notation.Ornament;
import org.wmn4j.notation.Ornamental;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignature;
import org.wmn4j.notation.access.Offset;
import org.wmn4j.notation.directions.Direction;
import org.wmn4j.notation.techniques.Technique;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

final class StaxWriter implements MusicXmlWriter {

	private static final Logger LOG = LoggerFactory.getLogger(StaxWriter.class);
	public static final String ENCODING = "UTF-8";
	public static final String XML_VERSION = "1.0";

	private final Score score;
	private final Path path;
	private final List<String> partIds = new ArrayList<>();
	private final boolean compress;
	private final boolean minify;

	private XMLStreamWriter writer;
	private OutputStream outputStream;
	private final int divisions;
	private NotationWriteResolver notationResolver;
	private boolean isClosed;
	private boolean staffInfoWritten;

	private final Map<Technique.AdditionalValue, Pitch> artificialHarmonicPitches = new EnumMap<>(
			Technique.AdditionalValue.class);

	static void writeValue(XMLStreamWriter writer, String tag, String value) throws XMLStreamException {
		if (value.isEmpty()) {
			writer.writeEmptyElement(tag);
		} else {
			writer.writeStartElement(tag);
			writer.writeCharacters(value);
			writer.writeEndElement();
		}
	}

	StaxWriter(Score score, Path path, boolean compress, boolean minify) {
		this.score = score;
		this.path = path;
		this.divisions = computeDivisions(score.partwiseIterator());
		this.compress = compress;
		this.minify = minify;
		this.isClosed = false;
		this.staffInfoWritten = false;
	}

	private String getDTD(String version) {
		StringBuilder builder = new StringBuilder();

		builder.append("<!DOCTYPE score-partwise PUBLIC \"-//Recordare//DTD MusicXML ");
		builder.append(version);
		builder.append(" Partwise//EN\" \"http://www.musicxml.org/dtds/partwise.dtd\">");

		return builder.toString();
	}

	private void writeMetaInf(XMLStreamWriter metaInfWriter, String filename) throws XMLStreamException {
		metaInfWriter.writeStartDocument(ENCODING, XML_VERSION);

		metaInfWriter.writeStartElement(CompressedMxl.CONTAINER_TAG);
		metaInfWriter.writeStartElement(CompressedMxl.ROOTFILES_TAG);

		metaInfWriter.writeStartElement(CompressedMxl.ROOTFILE_TAG);
		metaInfWriter.writeAttribute(CompressedMxl.FULL_PATH_ATTR, filename);
		metaInfWriter.writeAttribute(CompressedMxl.MEDIA_TYPE_ATTR, CompressedMxl.UNCOMPRESSED_CONTENT_TYPE);

		metaInfWriter.writeEndElement();
		metaInfWriter.writeEndElement();

		metaInfWriter.writeEndElement();
		metaInfWriter.writeEndDocument();
		metaInfWriter.flush();
	}

	private OutputStream createOutputStream() throws IOException, XMLStreamException {
		OutputStream outputStream;
		FileOutputStream foutput = new FileOutputStream(path.toString());

		if (compress) {
			ZipOutputStream zipOut = new ZipOutputStream(foutput);
			outputStream = new BufferedOutputStream(zipOut);

			XMLStreamWriter metaInfWriter = createWriter(outputStream);
			ZipEntry metaInf = new ZipEntry(CompressedMxl.META_INF_PATH);
			String filename = getFilename();
			zipOut.putNextEntry(metaInf);
			writeMetaInf(metaInfWriter, filename);
			metaInfWriter.close();
			zipOut.closeEntry();

			zipOut.putNextEntry(new ZipEntry(filename));
		} else {
			outputStream = new BufferedOutputStream(foutput);
		}

		return outputStream;
	}

	private String getFilename() {
		List<String> splitFilename = new ArrayList<>();
		Collections.addAll(splitFilename, path.getFileName().toString().split("\\."));
		final int lastIndex = splitFilename.size() - 1;

		final String extension = "musicxml";

		if (splitFilename.get(lastIndex).equals("mxl")) {
			splitFilename.set(lastIndex, extension);
		} else {
			splitFilename.add(extension);
		}

		return String.join(".", splitFilename);
	}

	private XMLStreamWriter createWriter(OutputStream outputStream) throws XMLStreamException {
		if (minify) {
			return XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
		}

		return new IndentingXmlStreamWriter(outputStream);
	}

	@Override
	public void write() throws IOException {
		try {
			outputStream = createOutputStream();
			writer = createWriter(outputStream);

			notationResolver = new NotationWriteResolver(writer);

			writer.writeStartDocument(ENCODING, XML_VERSION);
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
		} catch (XMLStreamException | IOException e) {
			LOG.error("Writing MusicXML failed with {}", e.getMessage());
		}

		close();
	}

	@Override
	public void close() throws IOException {
		if (!isClosed) {
			isClosed = true;

			try {
				writer.close();
			} catch (XMLStreamException e) {
				throw new IOException("Failed to close with exception: " + e.getMessage());
			}

			outputStream.close();
		}
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

			// Name is always required, default to empty tag if not defined.
			final var name = part.getName();
			if (name.isPresent()) {
				writeValue(Tags.PART_NAME, name.get());
			} else {
				writer.writeEmptyElement(Tags.PART_NAME);
			}

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
			staffInfoWritten = false;
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
			lcd = (lcd * denominator) / BigInteger.valueOf(lcd).gcd(BigInteger.valueOf(denominator)).intValue();
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

			final Staff.Type staffType = part.getStaff(s).getType();
			writeMeasureAttributes(current, prev, isMultiStaff, s, part.getStaffCount(), staffType);

			final int backup = writeMeasureContents(current, s, isMultiStaff);
			writeBarline(current, false);

			if (!s.equals(lastStaff)) {
				writeOffset(backup, false);
			}

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
				writer.writeEmptyElement(Tags.REPEAT);
				writer.writeAttribute(Tags.DIRECTION, repeatDirection);
			}

			writer.writeEndElement();
		}
	}

	private boolean isAttributesElementRequired(Measure current, Measure previous) {
		if (previous == null) {
			return true;
		}

		return !(current.getKeySignature().equals(previous.getKeySignature()) && current.getTimeSignature()
				.equals(previous.getTimeSignature()) && current.getClef().equals(previous.getClef()));
	}

	private void writeMeasureAttributes(Measure current, Measure previous, boolean isMultiStaff, Integer staffNumber,
			int staffCount, Staff.Type staffType) throws XMLStreamException {

		if (!isAttributesElementRequired(current, previous)) {
			return;
		}

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

		// Write staves
		boolean staffCountWritten = false;
		if (!staffInfoWritten) {
			writeValue(Tags.STAVES, Integer.toString(staffCount));
			staffCountWritten = true;
		}

		// Write clef
		final var clef = current.getClef();
		if (previous == null || !clef.equals(getLastClefInEffect(previous))) {
			writeClef(isMultiStaff, staffNumber, clef);
		}

		// Write staff line count if needed (only when staffCount has been written)
		// staff-details needs to be written after clef.
		boolean staffDetailsWritten = false;
		if (!staffInfoWritten && Staff.Type.SINGLE_LINE.equals(staffType)) {
			writer.writeStartElement(Tags.STAFF_DETAILS);
			writeValue(Tags.STAFF_LINES, Integer.toString(1));
			writer.writeEndElement();
			staffDetailsWritten = true;
		}

		staffInfoWritten = staffCountWritten || staffDetailsWritten;

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

	private int writeMeasureContents(Measure measure, Integer staff, boolean isMultiStaff) throws XMLStreamException {

		writeDirections(measure.getDirections(), staff);

		final List<Offset<Clef>> undealtClefChanges = new ArrayList<>(measure.getClefChanges());

		if (measure.isEmpty()) {
			return toDivisionCount(measure.getTimeSignature().getTotalDuration());
		}

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

	private void writeDirections(Iterable<Offset<Direction>> offsetDirections, Integer staffNumber)
			throws XMLStreamException {

		for (var offsetDirection : offsetDirections) {
			writer.writeStartElement(Tags.DIRECTION);
			writer.writeAttribute(Tags.PLACEMENT, Tags.ABOVE);

			writer.writeStartElement(Tags.DIRECTION_TYPE);
			final var direction = offsetDirection.get();

			if (direction.getType().equals(Direction.Type.TEXT)) {
				writeValue(Tags.WORDS, direction.getText().orElse(""));
			} else {
				LOG.info("Only text type directions are currently supported: ignoring direction with type {}",
						direction.getType());
			}

			// End direction-type element
			writer.writeEndElement();

			final var offset = offsetDirection.getDuration();
			if (offset.isPresent()) {
				writeValue(Tags.OFFSET, Integer.toString(toDivisionCount(offset.get())));
			}

			writeStaff(staffNumber);

			// End direction element
			writer.writeEndElement();
		}
	}

	private void writeDurational(Integer staffNumber, Integer voice, Durational durational) throws XMLStreamException {
		if (durational.isNote()) {
			writeNote(durational.toNote(), voice, staffNumber, false);
		} else if (durational.isChord()) {
			writeChord(durational.toChord(), voice, staffNumber);
		} else if (durational.isRest()) {
			writeRest(durational.toRest(), voice, staffNumber);
		}
	}

	private void writeDecomposedDurationals(Integer staffNumber, Integer voice, Durational durational,
			TimeSignature timeSig) throws XMLStreamException {

		final var decomposedDurations = durational.getDuration().decompose(timeSig.getTotalDuration());

		if (durational.isNote()) {
			for (Duration duration : decomposedDurations) {
				NoteBuilder builder = new NoteBuilder(durational.toNote());
				builder.setDuration(duration);
				writeNote(builder.build(), voice, staffNumber, false);
			}
		} else if (durational.isChord()) {
			for (Duration duration : decomposedDurations) {
				ChordBuilder builder = new ChordBuilder(durational.toChord());
				builder.setDuration(duration);
				writeChord(builder.build(), voice, staffNumber);
			}
		} else if (durational.isRest()) {
			for (Duration duration : decomposedDurations) {
				RestBuilder builder = new RestBuilder(durational.toRest());
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

	private void writeNote(Note note, Integer voice, Integer staff, boolean addChordTag) throws XMLStreamException {

		writeGraceNotes(note, voice, staff, Ornament.Type.GRACE_NOTES);

		writer.writeStartElement(Tags.NOTE);

		if (addChordTag) {
			writer.writeEmptyElement(Tags.CHORD);
		}

		writePitch(note);
		writeDuration(note.getDuration());
		writeVoice(voice);
		DurationAppearanceWriter.INSTANCE.writeAppearanceElements(note.getDuration(), writer);
		writeStaff(staff);
		writeNotations(note, note.getArticulations(), note.getNotations(), note.getOrnaments(), note.getTechniques());
		writeLyrics(note.getLyrics());

		writer.writeEndElement();

		if (!artificialHarmonicPitches.isEmpty()) {
			writeArtificialHarmonicPitches(note.getDuration(), voice, staff);
			artificialHarmonicPitches.clear();
		}

		writeGraceNotes(note, voice, staff, Ornament.Type.SUCCEEDING_GRACE_NOTES);
	}

	private void writeLyrics(List<Lyric> lyrics) throws XMLStreamException {
		int lyricNumber = 1;
		for (var lyric : lyrics) {
			writer.writeStartElement(Tags.LYRIC);
			writer.writeAttribute(Tags.NUMBER, Integer.toString(lyricNumber));

			final var type = lyric.getType();
			final var text = lyric.getText();

			final var syllabic = Transforms.lyricTypeToSyllabic(type);
			if (syllabic != null) {
				writeValue(Tags.SYLLABIC, syllabic);
				writeValue(Tags.TEXT, text);
			} else if (type.equals(Lyric.Type.EXTENDED)) {
				writeValue(Tags.TEXT, text);
				writer.writeEmptyElement(Tags.EXTEND);
				writer.writeAttribute(Tags.TYPE, Tags.START);
			} else if (type.equals(Lyric.Type.EXTENSION)) {
				writer.writeEmptyElement(Tags.EXTEND);
				writer.writeAttribute(Tags.TYPE, Tags.CONTINUE);
			} else if (type.equals(Lyric.Type.ELIDED)) {
				final String[] parts = text.split(Lyric.ELISION_SEPARATOR);
				writeValue(Tags.TEXT, parts[0]);

				for (int i = 1; i < parts.length; ++i) {
					writeValue(Tags.ELISION, Lyric.ELISION_SEPARATOR);
					writeValue(Tags.TEXT, parts[i]);
				}
			}

			writer.writeEndElement();
			++lyricNumber;
		}

	}

	private void writeArtificialHarmonicPitches(Duration baseNoteDuration, Integer voice, Integer staff)
			throws XMLStreamException {

		final var touchingPitch = artificialHarmonicPitches.getOrDefault(
				Technique.AdditionalValue.HARMONIC_TOUCHING_PITCH, null);

		if (touchingPitch != null) {
			writeArtificialHarmonicNote(touchingPitch, false, baseNoteDuration, voice, staff, Tags.TOUCHING_PITCH);
		}

		final var soundingPitch = artificialHarmonicPitches.getOrDefault(
				Technique.AdditionalValue.HARMONIC_SOUNDING_PITCH, null);

		if (soundingPitch != null) {
			writeArtificialHarmonicNote(soundingPitch, true, baseNoteDuration, voice, staff, Tags.SOUNDING_PITCH);
		}
	}

	private void writeArtificialHarmonicNote(Pitch soundingPitch, boolean hide, Duration baseNoteDuration,
			Integer voice,
			Integer staff, String harmonicTypeTag) throws XMLStreamException {
		writer.writeStartElement(Tags.NOTE);
		if (hide) {
			writer.writeAttribute(Tags.PRINT_OBJECT, Tags.NO);
		}
		writer.writeEmptyElement(Tags.CHORD);
		writePitchElement(soundingPitch);
		writeDuration(baseNoteDuration);
		writeVoice(voice);
		DurationAppearanceWriter.INSTANCE.writeAppearanceElements(baseNoteDuration, writer);
		writeStaff(staff);

		writer.writeStartElement(Tags.NOTATIONS);
		writer.writeStartElement(Tags.TECHNICAL);
		writer.writeStartElement(Tags.HARMONIC);

		writer.writeEmptyElement(Tags.ARTIFICIAL);
		writer.writeEmptyElement(harmonicTypeTag);

		writer.writeEndElement(); // End harmonic
		writer.writeEndElement(); // End technical
		writer.writeEndElement(); // End notations

		// End note element
		writer.writeEndElement();
	}

	private void writeGraceNotes(Note note, Integer voice, Integer staff, Ornament.Type ornamentType)
			throws XMLStreamException {
		final var graceNotes = note.getOrnaments().stream().filter(ornament -> ornament.getType().equals(ornamentType))
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

		writer.writeEmptyElement(Tags.GRACE);
		if (note.getType().equals(Ornamental.Type.ACCIACCATURA)) {
			writer.writeAttribute(Tags.SLASH, Tags.YES);
		}

		if (addChordTag) {
			writer.writeEmptyElement(Tags.CHORD);
		}

		writePitch(note);
		writeVoice(voice);
		DurationAppearanceWriter.INSTANCE.writeAppearanceElements(note.getDisplayableDuration(), writer);
		writeStaff(staff);
		writeNotations(note, note.getArticulations(), note.getNotations(), note.getOrnaments(), note.getTechniques());
		writeLyrics(note.getLyrics());

		writer.writeEndElement();

		if (!artificialHarmonicPitches.isEmpty()) {
			writeArtificialHarmonicPitches(note.getDisplayableDuration(), voice, staff);
			artificialHarmonicPitches.clear();
		}
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

	private void writePitch(OptionallyPitched note) throws XMLStreamException {
		if (note.hasPitch()) {
			final Pitch pitch = note.getPitch().get();
			writePitchElement(pitch);
		} else {
			final Pitch displayPitch = note.getDisplayPitch();
			writer.writeStartElement(Tags.UNPITCHED);
			writeValue(Tags.DISPLAY_STEP, displayPitch.getBase().toString());
			writeValue(Tags.DISPLAY_OCTAVE, Integer.toString(displayPitch.getOctave()));
			writer.writeEndElement();
		}
	}

	private void writePitchElement(Pitch pitch) throws XMLStreamException {
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

	private boolean hasOnlyGraceNoteOrnaments(Collection<Ornament> ornaments) {
		return ornaments.stream().allMatch(StaxWriter::isGraceNote);
	}

	private boolean hasWritableNotations(Notation.Connectable connectable, Collection<Notation> notations) {
		for (Notation notation : notations) {
			boolean isBeginningOrEnd = connectable.getConnection(notation)
					.map(connection -> connection.isBeginning() || connection.isEnd()).orElse(false);

			if (isBeginningOrEnd || notation.getType().isArpeggiation()) {
				return true;
			}
		}

		return false;
	}

	private void writeNotations(Notation.Connectable connectable, Set<Articulation> articulations,
			Set<Notation> notations, Collection<Ornament> ornaments, Collection<Technique> techniques)
			throws XMLStreamException {

		if (articulations.isEmpty() && techniques.isEmpty() && !hasWritableNotations(connectable, notations)
				&& hasOnlyGraceNoteOrnaments(ornaments)) {
			return;
		}

		writer.writeStartElement(Tags.NOTATIONS);
		writeArticulations(articulations);
		writeConnectedNotations(connectable, notations);
		writeOrnaments(ornaments);
		writeTechnicals(techniques);

		writer.writeEndElement();
	}

	private void writeTechnicals(Collection<Technique> techniques) throws XMLStreamException {
		if (techniques.isEmpty()) {
			return;
		}

		writer.writeStartElement(Tags.TECHNICAL);

		for (final var technique : techniques) {
			switch (technique.getType()) {
				case HARMON_MUTE:
					writeHarmonMuteTechnical(technique);
					break;
				case HARMONIC:
					writeHarmonic(technique);
					break;
				case BEND:
					writeBendElement(technique);
					break;
				case HOLE:
					writeHoleElement(technique);
					break;
				case ARROW:
					writeArrowElement(technique);
					break;
				default:
					writeBasicTechnicalElement(technique);
			}
		}

		writer.writeEndElement();
	}

	private void writeArrowElement(Technique arrow) throws XMLStreamException {
		writer.writeStartElement(Tags.ARROW);

		final var direction = arrow.getValue(Technique.AdditionalValue.ARROW_DIRECTION, String.class);
		if (direction.isPresent()) {
			writeValue(Tags.ARROW_DIRECTION, direction.get());
		}

		final var style = arrow.getValue(Technique.AdditionalValue.ARROW_STYLE, String.class);
		if (style.isPresent()) {
			writeValue(Tags.ARROW_STYLE, style.get());
		}

		final var arrowhead = arrow.getValue(Technique.AdditionalValue.ARROWHEAD, Boolean.class);
		if (arrowhead.isPresent() && arrowhead.get().equals(Boolean.TRUE)) {
			writer.writeEmptyElement(Tags.ARROWHEAD);
		}

		final var circular = arrow.getValue(Technique.AdditionalValue.CIRCULAR_ARROW, String.class);
		if (circular.isPresent()) {
			writeValue(Tags.CIRCULAR_ARROW, circular.get());
		}

		writer.writeEndElement();
	}

	private void writeHoleElement(Technique hole) throws XMLStreamException {
		writer.writeStartElement(Tags.HOLE);

		final var holeType = hole.getValue(Technique.AdditionalValue.WIND_HOLE_TYPE, String.class);
		if (holeType.isPresent()) {
			writeValue(Tags.HOLE_TYPE, holeType.get());
		}

		final var holeClosed = hole.getValue(Technique.AdditionalValue.WIND_HOLE_POSITION, Technique.Opening.class);
		if (holeClosed.isPresent()) {
			writeValue(Tags.HOLE_CLOSED, Transforms.openingTypeToText(holeClosed.get()));
		}

		final var holeShape = hole.getValue(Technique.AdditionalValue.WIND_HOLE_SHAPE, String.class);
		if (holeShape.isPresent()) {
			writeValue(Tags.HOLE_SHAPE, holeShape.get());
		}

		writer.writeEndElement();
	}

	private void writeBendElement(Technique bend) throws XMLStreamException {
		writer.writeStartElement(Tags.BEND);
		final var alter = bend.getValue(Technique.AdditionalValue.BEND_SEMITONES, Double.class);
		if (alter.isPresent()) {
			writeValue(Tags.BEND_ALTER, alter.get().toString());
		}

		final var preBend = bend.getValue(Technique.AdditionalValue.PRE_BEND, Boolean.class);
		if (preBend.isPresent() && preBend.get().equals(Boolean.TRUE)) {
			writer.writeEmptyElement(Tags.PRE_BEND);
		}

		final var release = bend.getValue(Technique.AdditionalValue.BEND_RELEASE, Duration.class);
		if (release.isPresent()) {
			writer.writeEmptyElement(Tags.RELEASE);
			writer.writeAttribute(Tags.OFFSET, Integer.toString(toDivisionCount(release.get())));
		}

		final var withBar = bend.getValue(Technique.AdditionalValue.BEND_WITH_BAR, String.class);
		if (withBar.isPresent()) {
			writeValue(Tags.WITH_BAR, withBar.get());
		}

		writer.writeEndElement();
	}

	private void writeHarmonic(Technique harmonic) throws XMLStreamException {
		final var isNatural = harmonic.getValue(Technique.AdditionalValue.IS_NATURAL_HARMONIC,
				Technique.AdditionalValue.IS_NATURAL_HARMONIC.getValueClass());

		writer.writeStartElement(Tags.HARMONIC);

		if (isNatural.isPresent() && isNatural.get().equals(Boolean.TRUE)) {
			writer.writeEmptyElement(Tags.NATURAL);
		} else {
			writer.writeEmptyElement(Tags.ARTIFICIAL);
			writer.writeEmptyElement(Tags.BASE_PITCH);

			// Add the touching and sounding pitches, so they can be written
			// as separate note elements after the base pitch note element.
			harmonic.getValue(Technique.AdditionalValue.HARMONIC_SOUNDING_PITCH, Pitch.class).ifPresent(
					pitch -> artificialHarmonicPitches.put(Technique.AdditionalValue.HARMONIC_SOUNDING_PITCH, pitch));

			harmonic.getValue(Technique.AdditionalValue.HARMONIC_TOUCHING_PITCH, Pitch.class).ifPresent(
					pitch -> artificialHarmonicPitches.put(Technique.AdditionalValue.HARMONIC_TOUCHING_PITCH, pitch));
		}

		writer.writeEndElement();
	}

	private void writeHarmonMuteTechnical(Technique technique) throws XMLStreamException {
		final var harmonMutePosition = technique.getValue(Technique.AdditionalValue.HARMON_MUTE_POSITION,
				Technique.Opening.class);

		if (harmonMutePosition.isEmpty()) {
			LOG.warn("Harmon mute technique marking missing position.");
			return;
		}

		writer.writeStartElement(Tags.HARMON_MUTE);
		final var harmonClosedValue = Transforms.openingTypeToText(harmonMutePosition.get());
		writeValue(Tags.HARMON_CLOSED, harmonClosedValue);
		writer.writeEndElement();
	}

	private void writeBasicTechnicalElement(Technique technique) throws XMLStreamException {
		final String tag = Transforms.techniqueTypeToTag(technique.getType());
		if (technique.getText().isPresent()) {
			writeValue(tag, technique.getText().get());
		} else if (technique.getNumber().isPresent()) {
			writeValue(tag, Integer.toString(technique.getNumber().getAsInt()));
		} else {
			writer.writeEmptyElement(tag);
		}
	}

	private void writeArticulations(Collection<Articulation> articulations) throws XMLStreamException {
		if (articulations.isEmpty()) {
			return;
		}

		if (articulations.contains(Articulation.FERMATA)) {
			writer.writeEmptyElement(Tags.FERMATA);
		}

		// The articulations element is only created for articulations other than fermata.
		if (!(articulations.size() == 1 && articulations.contains(Articulation.FERMATA))) {
			writer.writeStartElement(Tags.ARTICULATIONS);
			for (Articulation articulation : articulations) {
				String tag = Transforms.articulationToTag(articulation);
				if (tag != null) {
					writer.writeEmptyElement(tag);
				}
			}
			writer.writeEndElement();
		}
	}

	private static boolean isGraceNote(Ornament ornament) {
		return ornament.getType().equals(Ornament.Type.SUCCEEDING_GRACE_NOTES) || ornament.getType()
				.equals(Ornament.Type.GRACE_NOTES);
	}

	private void writeOrnaments(Collection<Ornament> ornaments) throws XMLStreamException {
		// If ornaments is empty or the only ornament is of type grace notes then do not add ornaments element.
		if (hasOnlyGraceNoteOrnaments(ornaments)) {
			return;
		}

		writer.writeStartElement(Tags.ORNAMENTS);
		for (Ornament ornament : ornaments) {
			if (isGraceNote(ornament)) {
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

				if (content.isEmpty()) {
					writer.writeEmptyElement(tag);
				} else {
					writeValue(tag, content);
				}
			}
		}
		writer.writeEndElement();
	}

	private void writeConnectedNotations(Notation.Connectable connectable, Collection<Notation> notations)
			throws XMLStreamException {
		if (!hasWritableNotations(connectable, notations)) {
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
