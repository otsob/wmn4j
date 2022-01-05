/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.wmn4j.TestHelper;
import org.wmn4j.Wmn4j;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;
import org.wmn4j.notation.access.PositionalIterator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class StaxScoreWriterTest {

	@TempDir
	Path temporaryDirectory;

	private final String MUSICXML_FILE_PATH = "musicxml/";

	private Score readMusicXmlTestFile(String testFileName, boolean validate) {
		final Path path = Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + testFileName);
		Score score = null;

		try (final MusicXmlReader reader = validate ?
				MusicXmlReader.readerFor(path) :
				MusicXmlReader.nonValidatingReaderFor(path)) {
			score = reader.readScore();
		} catch (final IOException | ParsingFailureException e) {
			fail("Parsing failed with exception " + e);
		}

		assertNotNull(score);
		return score;
	}

	private Score writeAndReadScore(Score score) {
		MusicXmlWriter writer = new StaxScoreWriter(score);
		Path file = temporaryDirectory.resolve("file.musicxml");
		writer.write(file);

		Score writtenScore = null;

		try (final MusicXmlReader reader = MusicXmlReader.readerFor(file)) {
			writtenScore = reader.readScore();
		} catch (final IOException | ParsingFailureException e) {
			fail("Reading score written by MusicXmlScoreWriterDom failed with exception " + e);
		}

		assertNotNull(writtenScore);
		return writtenScore;
	}

	@Test
	void testWritingNotes() {
		ScoreBuilder scoreBuilder = new ScoreBuilder();
		PartBuilder partBuilder = new PartBuilder("Part1");
		MeasureBuilder measureBuilder = new MeasureBuilder(1);

		measureBuilder
				.addToVoice(1, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 0), Durations.QUARTER));
		measureBuilder
				.addToVoice(1, new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.SHARP, 1), Durations.HALF));
		measureBuilder.addToVoice(1,
				new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.DOUBLE_FLAT, 2), Durations.QUARTER_TRIPLET));

		partBuilder.add(measureBuilder);
		scoreBuilder.addPart(partBuilder);
		Score score = scoreBuilder.build();

		Score writtenScore = writeAndReadScore(score);

		PositionalIterator iterator = writtenScore.partwiseIterator();

		Note note = (Note) iterator.next();
		assertEquals(Pitch.Base.C, note.getPitch().getBase());
		assertEquals(Pitch.Accidental.NATURAL, note.getPitch().getAccidental());
		assertEquals(0, note.getPitch().getOctave());
		assertEquals(Durations.QUARTER, note.getDuration());

		note = (Note) iterator.next();
		assertEquals(Pitch.Base.D, note.getPitch().getBase());
		assertEquals(Pitch.Accidental.SHARP, note.getPitch().getAccidental());
		assertEquals(1, note.getPitch().getOctave());
		assertEquals(Durations.HALF, note.getDuration());

		note = (Note) iterator.next();
		assertEquals(Pitch.Base.E, note.getPitch().getBase());
		assertEquals(Pitch.Accidental.DOUBLE_FLAT, note.getPitch().getAccidental());
		assertEquals(2, note.getPitch().getOctave());
		assertEquals(Durations.QUARTER_TRIPLET, note.getDuration());

		assertFalse(iterator.hasNext());
	}

	@Test
	void testWritingKeySignatures() {
		Score score = readMusicXmlTestFile("keysigs.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertKeySignaturesReadToScoreCorrectly(writtenScore);
	}

	@Test
	void testWritingTimeSignatures() {
		Score score = readMusicXmlTestFile("timesigs.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertTimeSignaturesReadCorrectly(writtenScore);
	}

	@Test
	void testWritingClefs() {
		Score score = readMusicXmlTestFile("clefs.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertClefsReadCorrectly(writtenScore);
	}

	@Test
	void testWritingBarlines() {
		Score score = readMusicXmlTestFile("barlines.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertBarlinesReadCorrectly(writtenScore);
	}

	@Test
	void testWritingMultipleVoicesAndChords() {
		Score score = readMusicXmlTestFile("twoMeasures.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertChordsAndMultipleVoicesReadCorrectly(writtenScore);
	}

	@Test
	void testWritingPickupMeasure() {
		Score score = readMusicXmlTestFile("pickup_measure_test.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertPickupMeasureReadCorrectly(writtenScore);
	}

	@Test
	void testWritingScoreAttributes() {
		Score score = readMusicXmlTestFile("attribute_reading_test.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertScoreHasExpectedAttributes(writtenScore);
	}

	@Test
	void testWritingScoreWithMultiStaffPart() {
		Score score = readMusicXmlTestFile("multistaff.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertMultiStaffPartReadCorrectly(writtenScore);
	}

	@Test
	void testWritingArticulations() {
		Score score = readMusicXmlTestFile("articulations.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertScoreWithArticulationsReadCorrectly(writtenScore);
	}

	@Test
	void testArticulationsOnMultipleStaves() {
		Score score = readMusicXmlTestFile("articulationsOnMultipleStaves.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertArticulationsReadCorrectlyFromMultipleStaves(writtenScore);
	}

	@Test
	void testWritingOrnaments() {
		Score score = readMusicXmlTestFile("ornament_test.musicxml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertOrnamentsAreCorrect(writtenScore);
	}

	@Test
	void testWritingBasicNoteAppearances() {
		final Score score = readMusicXmlTestFile("basic_duration_appearances.musicxml", false);

		MusicXmlWriter writer = new StaxScoreWriter(score);
		Path filePath = temporaryDirectory.resolve("temporary_file.musicxml");
		writer.write(filePath);

		final Document document = TestHelper.readDocument(filePath);
		final Node partNode = document.getElementsByTagName(Tags.PART).item(0);
		assertNotNull(partNode);

		final Optional<Node> measureNode = DocHelper.findChild(partNode, Tags.MEASURE);
		assertTrue(measureNode.isPresent());

		final List<Node> noteNodes = DocHelper.findChildren(measureNode.get(), Tags.NOTE).stream()
				.filter(node -> DocHelper.findChild(node, Tags.REST).isEmpty())
				.collect(Collectors.toList());

		assertEquals(8, noteNodes.size(), "Wrong number of note notes found in written test document");

		assertNoteNodeDurationTypeElement("32nd", noteNodes.get(0));
		assertNoteNodeDurationTypeElement("16th", noteNodes.get(1));
		assertNoteNodeDurationTypeElement("eighth", noteNodes.get(2));
		assertNoteNodeDurationTypeElement("quarter", noteNodes.get(3));
		assertNoteNodeDurationTypeElement("half", noteNodes.get(4));
		assertNoteNodeDurationTypeElement("whole", noteNodes.get(5));
		assertNoteNodeDurationTypeElement("breve", noteNodes.get(6));
		assertNoteNodeDurationTypeElement("long", noteNodes.get(7));
	}

	private void assertNoteNodeDurationTypeElement(String expectedDurationTypeText, Node noteNode) {
		final List<Node> durationTypeNodes = DocHelper.findChildren(noteNode, Tags.TYPE);
		assertEquals(1, durationTypeNodes.size());

		final Node durationTypeNode = durationTypeNodes.get(0);
		assertEquals(expectedDurationTypeText, durationTypeNode.getTextContent());
	}

	@Test
	void testWritingBasicDottedNoteAppearances() {
		final Score score = readMusicXmlTestFile("basic_dotted_duration_appearances.musicxml", false);

		MusicXmlWriter writer = new StaxScoreWriter(score);
		Path filePath = temporaryDirectory.resolve("temporary_file.musicxml");
		writer.write(filePath);

		final Document document = TestHelper.readDocument(filePath);
		final Node partNode = document.getElementsByTagName(Tags.PART).item(0);
		assertNotNull(partNode);

		final Optional<Node> measureNode = DocHelper.findChild(partNode, Tags.MEASURE);
		assertTrue(measureNode.isPresent());

		final List<Node> noteNodes = DocHelper.findChildren(measureNode.get(), Tags.NOTE).stream()
				.filter(node -> DocHelper.findChild(node, Tags.REST).isEmpty())
				.collect(Collectors.toList());

		assertEquals(8, noteNodes.size(), "Wrong number of note notes found in written test document");

		assertBasicDottedNoteAppearance("32nd", noteNodes.get(0));
		assertBasicDottedNoteAppearance("16th", noteNodes.get(1));
		assertBasicDottedNoteAppearance("eighth", noteNodes.get(2));
		assertBasicDottedNoteAppearance("quarter", noteNodes.get(3));
		assertBasicDottedNoteAppearance("half", noteNodes.get(4));
		assertBasicDottedNoteAppearance("whole", noteNodes.get(5));
		assertBasicDottedNoteAppearance("breve", noteNodes.get(6));
		assertBasicDottedNoteAppearance("long", noteNodes.get(7));
	}

	private void assertBasicDottedNoteAppearance(String expectedDurationTypeText, Node noteNode) {
		assertNoteNodeDurationTypeElement(expectedDurationTypeText, noteNode);
		List<Node> dotNodes = DocHelper.findChildren(noteNode, Tags.DOT);
		assertEquals(1, dotNodes.size(), "Incorrect number of dot nodes");
	}

	@Test
	void testWritingTupletAppearances() {
		final Score score = readMusicXmlTestFile("tuplet_writing_test.musicxml", false);

		MusicXmlWriter writer = new StaxScoreWriter(score);
		Path filePath = temporaryDirectory.resolve("temporary_file.musicxml");
		writer.write(filePath);

		final Document document = TestHelper.readDocument(filePath);
		final Node partNode = document.getElementsByTagName(Tags.PART).item(0);
		assertNotNull(partNode);

		final Optional<Node> measureNode = DocHelper.findChild(partNode, Tags.MEASURE);
		assertTrue(measureNode.isPresent());

		final List<Node> noteNodes = DocHelper.findChildren(measureNode.get(), Tags.NOTE).stream()
				.filter(node -> DocHelper.findChild(node, Tags.REST).isEmpty())
				.collect(Collectors.toList());

		assertEquals(15, noteNodes.size());

		assertTupletAppearanceNodes("eighth", noteNodes.get(0), 3, 2);
		assertTupletAppearanceNodes("eighth", noteNodes.get(1), 3, 2);
		assertTupletAppearanceNodes("eighth", noteNodes.get(2), 3, 2);

		assertTupletAppearanceNodes("16th", noteNodes.get(3), 5, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(4), 5, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(5), 5, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(6), 5, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(7), 5, 4);

		assertTupletAppearanceNodes("16th", noteNodes.get(8), 7, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(9), 7, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(10), 7, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(11), 7, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(12), 7, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(13), 7, 4);
		assertTupletAppearanceNodes("16th", noteNodes.get(14), 7, 4);
	}

	private void assertTupletAppearanceNodes(String expectedDurationTypeText, Node noteNode,
			int expectedActualNotesNumber, int expectedNormalNotesNumber) {

		assertNoteNodeDurationTypeElement(expectedDurationTypeText, noteNode);
		final List<Node> timeModificationNodes = DocHelper.findChildren(noteNode, Tags.TIME_MODIFICATION);
		assertEquals(1, timeModificationNodes.size(), "Found incorrect number of time-modification nodes");

		final Node timeModification = timeModificationNodes.get(0);

		final List<Node> actualNotesNodes = DocHelper
				.findChildren(timeModification, Tags.ACTUAL_NOTES);
		assertEquals(1, actualNotesNodes.size(), "Found incorrect number of actual-notes nodes");
		assertEquals(expectedActualNotesNumber, Integer.parseInt(actualNotesNodes.get(0).getTextContent()),
				"Incorrect value for actual-notes");

		final List<Node> normalNotesNodes = DocHelper
				.findChildren(timeModification, Tags.NORMAL_NOTES);
		assertEquals(1, normalNotesNodes.size(), "Found incorrect number of normal-notes nodes");
		assertEquals(expectedNormalNotesNumber, Integer.parseInt(normalNotesNodes.get(0).getTextContent()),
				"Incorrect value for normal-notes");
	}

	@Test
	void testWhenMusicXmlIsWrittenThenEncodingInformationIsWrittenToFile() {
		final List<PartBuilder> partBuilders = TestHelper.getTestPartBuilders(3, 3);
		ScoreBuilder scoreBuilder = new ScoreBuilder();
		scoreBuilder.setAttribute(Score.Attribute.TITLE, "test score");

		scoreBuilder.addPart(partBuilders.get(0));
		scoreBuilder.addPart(partBuilders.get(1));
		scoreBuilder.addPart(partBuilders.get(2));

		final Score score = scoreBuilder.build();

		// Check that score is valid MusicXML
		writeAndReadScore(score);

		MusicXmlWriter writer = new StaxScoreWriter(score);
		Path filePath = temporaryDirectory.resolve("temporary_file.musicxml");
		writer.write(filePath);

		final Document document = TestHelper.readDocument(filePath);

		final Node identificationElement = document.getElementsByTagName(Tags.IDENTIFICATION).item(0);
		assertNotNull(identificationElement, "Missing identification element entirely");

		final Optional<Node> encodingElement = DocHelper.findChild(identificationElement, Tags.ENCODING);
		assertTrue(encodingElement.isPresent(), "Missing encoding element");

		final Optional<Node> softwareElement = DocHelper.findChild(encodingElement.get(), Tags.SOFTWARE);
		assertTrue(softwareElement.isPresent(), "Missing software element");
		assertEquals(Wmn4j.getNameWithVersion(), softwareElement.get().getTextContent());

		final Optional<Node> dateElement = DocHelper.findChild(encodingElement.get(), Tags.ENCODING_DATE);
		assertTrue(dateElement.isPresent(), "Missing encoding date");

		final DateFormat musicXmlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		final String dateStringInFile = dateElement.get().getTextContent();
		try {
			Date date = musicXmlDateFormat.parse(dateStringInFile);
			assertEquals(musicXmlDateFormat.format(new Date()), musicXmlDateFormat.format(date),
					"Date in file is incorrect");
		} catch (ParseException exception) {
			fail("Failed to parse date " + dateStringInFile + " with exception " + exception);
		}
	}
}
