/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class StaxReaderTest {

	private static final String MUSICXML_FILE_PATH = "musicxml/";

	MusicXmlReader getMusicXmlReader(Path path, boolean validate) {
		return new StaxReader(path, validate);
	}

	Score readScore(String testFileName, boolean validate) {
		final Path path = Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + testFileName);
		final MusicXmlReader reader = getMusicXmlReader(path, validate);
		Score score = null;

		try {
			score = reader.readScore();
		} catch (final IOException | ParsingFailureException e) {
			fail("Parsing failed with exception " + e);
		}

		assertNotNull(score);
		return score;
	}

	ScoreBuilder readScoreBuilder(String testFileName, boolean validate) {
		final Path path = Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + testFileName);
		final MusicXmlReader reader = getMusicXmlReader(path, validate);
		ScoreBuilder scoreBuilder = null;

		try {
			scoreBuilder = reader.readScoreBuilder();
		} catch (final IOException | ParsingFailureException e) {
			fail("Parsing failed with exception " + e);
		}

		assertNotNull(scoreBuilder);
		return scoreBuilder;
	}

	@Test
	void testReadScoreWithSingleNote() {
		final Score score = readScore("singleC.xml", false);
		MusicXmlFileChecks.assertSingleNoteScoreReadCorrectly(score);
	}

	@Test
	void testReadScoreBuilderWithSingleNote() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("singleC.xml", false);
		MusicXmlFileChecks.assertSingleNoteScoreReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testChordsAndMultipleVoicesReadToScore() {
		final Score score = readScore("twoMeasures.xml", false);
		MusicXmlFileChecks.assertChordsAndMultipleVoicesReadCorrectly(score);
	}

	@Test
	void testChordsAndMultipleVoicesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("twoMeasures.xml", false);
		MusicXmlFileChecks.assertChordsAndMultipleVoicesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testReadScoreWithMultipleParts() {
		final Score score = readScore("twoPartsAndMeasures.xml", false);
		MusicXmlFileChecks.assertScoreWithMultiplePartsReadCorrectly(score);
	}

	@Test
	void testReadScoreBuilderWithMultipleParts() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("twoPartsAndMeasures.xml", false);
		MusicXmlFileChecks.assertScoreWithMultiplePartsReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testBarlinesReadToScore() {
		final Score score = readScore("barlines.xml", false);
		MusicXmlFileChecks.assertBarlinesReadCorrectly(score);
	}

	@Test
	void testBarlinesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("barlines.xml", false);
		MusicXmlFileChecks.assertBarlinesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testClefsReadToScore() {
		final Score score = readScore("clefs.xml", false);
		MusicXmlFileChecks.assertClefsReadCorrectly(score);
	}

	@Test
	void testClefsReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("clefs.xml", false);
		MusicXmlFileChecks.assertClefsReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testMultiStaffClefsReadToScore() {
		final Score score = readScore("multiStaffClefs.xml", false);
		MusicXmlFileChecks.assertMultiStaffClefsReadCorrectlyToScore(score);
	}

	@Test
	void testMultiStaffClefsReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("multiStaffClefs.xml", false);
		MusicXmlFileChecks.assertMultiStaffClefsReadCorrectlyToScore(scoreBuilder.build());
	}

	@Test
	void testKeySignaturesReadToScore() {
		final Score score = readScore("keysigs.xml", false);
		MusicXmlFileChecks.assertKeySignaturesReadToScoreCorrectly(score);
	}

	@Test
	void testKeySignaturesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("keysigs.xml", false);
		MusicXmlFileChecks.assertKeySignaturesReadToScoreCorrectly(scoreBuilder.build());
	}

	@Test
	void testMultiStaffPartReadToScore() {
		final Score score = readScore("multistaff.xml", false);
		MusicXmlFileChecks.assertMultiStaffPartReadCorrectly(score);
	}

	@Test
	void testMultiStaffPartReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("multistaff.xml", false);
		MusicXmlFileChecks.assertMultiStaffPartReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testTimeSignaturesReadToScore() {
		final Score score = readScore("timesigs.xml", false);
		MusicXmlFileChecks.assertTimeSignaturesReadCorrectly(score);
	}

	@Test
	void testTimeSignaturesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("timesigs.xml", false);
		MusicXmlFileChecks.assertTimeSignaturesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testTimeSignatureChangeReadToScore() {
		final Score score = readScore("scoreIteratorTesting.xml", false);
		MusicXmlFileChecks.assertTimeSignatureChangeReadCorrectly(score);
	}

	@Test
	void testTimeSignatureChangeReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("scoreIteratorTesting.xml", false);
		MusicXmlFileChecks.assertTimeSignatureChangeReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testTiedNotesReadToScore() {
		final Score score = readScore("tieTesting.xml", false);
		MusicXmlFileChecks.assertTiedNotesReadCorrectly(score);
	}

	@Test
	void testTiedNotesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("tieTesting.xml", false);
		MusicXmlFileChecks.assertTiedNotesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testReadingScoreWithArticulations() {
		final Score score = readScore("articulations.xml", false);
		MusicXmlFileChecks.assertScoreWithArticulationsReadCorrectly(score);
	}

	@Test
	void testReadingScoreBuilderWithArticulations() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("articulations.xml", false);
		MusicXmlFileChecks.assertScoreWithArticulationsReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testReadingNotationsIntoScoreFromSingleVoice() {
		Score scoreWitNotations = readScore("single_staff_single_voice_notation_test.musicxml", false);
		MusicXmlFileChecks.assertNotationsReadCorrectlyFromSingleVoiceToScore(scoreWitNotations);
	}

	@Test
	void testReadingNotationsIntoScoreBuilderFromSingleVoice() {
		ScoreBuilder scoreWitNotations = readScoreBuilder("single_staff_single_voice_notation_test.musicxml", false);
		MusicXmlFileChecks.assertNotationsReadCorrectlyFromSingleVoiceToScore(scoreWitNotations.build());
	}

	@Test
	void testReadingNotationsIntoScoreFromMultipleStavesAndVoices() {
		Score scoreWitNotations = readScore("multi_staff_multi_voice_notation_test.xml", false);
		MusicXmlFileChecks.assertNotationsReadCorrectlyFromMultipleStavesWithMultipleVoices(scoreWitNotations);
	}

	@Test
	void testReadingNotationsIntoScoreBuilderFromMultipleStavesAndVoices() {
		ScoreBuilder scoreBuilderWitNotations = readScoreBuilder("multi_staff_multi_voice_notation_test.xml", false);
		MusicXmlFileChecks
				.assertNotationsReadCorrectlyFromMultipleStavesWithMultipleVoices(scoreBuilderWitNotations.build());
	}
}