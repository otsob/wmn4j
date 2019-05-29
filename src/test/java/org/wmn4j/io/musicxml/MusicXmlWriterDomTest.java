/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.builders.MeasureBuilder;
import org.wmn4j.notation.builders.NoteBuilder;
import org.wmn4j.notation.builders.PartBuilder;
import org.wmn4j.notation.builders.ScoreBuilder;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Score;
import org.wmn4j.notation.iterators.PartWiseScoreIterator;
import org.wmn4j.notation.iterators.ScoreIterator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class MusicXmlWriterDomTest {

	@TempDir
	Path temporaryDirectory;

	private final String MUSICXML_FILE_PATH = "musicxml/";

	private Score readMusicXmlTestFile(String testFileName, boolean validate) {
		final MusicXmlReader reader = MusicXmlReader.getReader(validate);
		Score score = null;
		final Path path = Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + testFileName);

		try {
			score = reader.readScore(path);
		} catch (final IOException | ParsingFailureException e) {
			fail("Parsing failed with exception " + e);
		}

		assertNotNull(score);
		return score;
	}

	private Score writeAndReadScore(Score score) {
		MusicXmlWriterDom writer = new MusicXmlWriterDom(score);
		Path file = temporaryDirectory.resolve("file.xml");
		writer.writeToFile(file);

		final MusicXmlReader reader = MusicXmlReader.getReader(true);
		Score writtenScore = null;

		try {
			writtenScore = reader.readScore(file);
		} catch (final IOException | ParsingFailureException e) {
			fail("Reading score written by MusicXmlWriterDom failed with exception " + e);
		}

		assertNotNull(writtenScore);
		return writtenScore;
	}

	@Test
	void testWritingNotes() {
		ScoreBuilder scoreBuilder = new ScoreBuilder();
		PartBuilder partBuilder = new PartBuilder("Part1");
		MeasureBuilder measureBuilder = new MeasureBuilder(1);

		measureBuilder.addToVoice(1, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 0), Durations.QUARTER));
		measureBuilder.addToVoice(1, new NoteBuilder(Pitch.of(Pitch.Base.D, 1, 1), Durations.HALF));
		measureBuilder.addToVoice(1, new NoteBuilder(Pitch.of(Pitch.Base.E, -2, 2), Durations.QUARTER_TRIPLET));

		partBuilder.add(measureBuilder);
		scoreBuilder.addPart(partBuilder);
		Score score = scoreBuilder.build();

		Score writtenScore = writeAndReadScore(score);

		ScoreIterator iterator = new PartWiseScoreIterator(writtenScore);

		Note note = (Note) iterator.next();
		assertEquals(Pitch.Base.C, note.getPitch().getBase());
		assertEquals(0, note.getPitch().getAlter());
		assertEquals(0, note.getPitch().getOctave());
		assertEquals(Durations.QUARTER, note.getDuration());

		note = (Note) iterator.next();
		assertEquals(Pitch.Base.D, note.getPitch().getBase());
		assertEquals(1, note.getPitch().getAlter());
		assertEquals(1, note.getPitch().getOctave());
		assertEquals(Durations.HALF, note.getDuration());

		note = (Note) iterator.next();
		assertEquals(Pitch.Base.E, note.getPitch().getBase());
		assertEquals(-2, note.getPitch().getAlter());
		assertEquals(2, note.getPitch().getOctave());
		assertEquals(Durations.QUARTER_TRIPLET, note.getDuration());

		assertFalse(iterator.hasNext());
	}

	@Test
	void testWritingKeySignatures() {
		Score score = readMusicXmlTestFile("keysigs.xml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertKeySignaturesReadToScoreCorrectly(writtenScore);
	}

	@Test
	void testWritingTimeSignatures() {
		Score score = readMusicXmlTestFile("timesigs.xml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertTimeSignaturesReadCorrectly(writtenScore);
	}

	@Test
	void testWritingBarlines() {
		Score score = readMusicXmlTestFile("barlines.xml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertBarlinesReadCorrectly(writtenScore);
	}

	@Test
	void testWritingMultipleVoicesAndChords() {
		Score score = readMusicXmlTestFile("twoMeasures.xml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertChordsAndMultipleVoicesReadCorrectly(writtenScore);
	}

	@Test
	void testWritingPickupMeasure() {
		Score score = readMusicXmlTestFile("pickup_measure_test.xml", false);
		Score writtenScore = writeAndReadScore(score);

		MusicXmlFileChecks.assertPickupMeasureReadCorrectly(writtenScore);
	}

}
