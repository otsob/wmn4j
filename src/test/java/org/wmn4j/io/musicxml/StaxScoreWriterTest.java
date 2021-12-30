/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.wmn4j.TestHelper;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
}
