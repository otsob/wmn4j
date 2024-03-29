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
		final Score score = readScore("singleC.musicxml", false);
		MusicXmlFileChecks.assertSingleNoteScoreReadCorrectly(score);
	}

	@Test
	void testReadScoreWithNonNumericMeasureNumber() {
		// The score is otherwise equal to singleC.musicxml, apart from having measure-
		// number with a non-numeric character in it.
		final Score score = readScore("nonnumeric_measure_number.musicxml", true);
		MusicXmlFileChecks.assertSingleNoteScoreReadCorrectly(score);
	}

	@Test
	void testReadScoreBuilderWithSingleNote() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("singleC.musicxml", false);
		MusicXmlFileChecks.assertSingleNoteScoreReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testChordsAndMultipleVoicesReadToScore() {
		final Score score = readScore("twoMeasures.musicxml", false);
		MusicXmlFileChecks.assertChordsAndMultipleVoicesReadCorrectly(score);
	}

	@Test
	void testChordsAndMultipleVoicesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("twoMeasures.musicxml", false);
		MusicXmlFileChecks.assertChordsAndMultipleVoicesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testReadScoreWithMultipleParts() {
		final Score score = readScore("twoPartsAndMeasures.musicxml", false);
		MusicXmlFileChecks.assertScoreWithMultiplePartsReadCorrectly(score);
	}

	@Test
	void testReadScoreBuilderWithMultipleParts() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("twoPartsAndMeasures.musicxml", false);
		MusicXmlFileChecks.assertScoreWithMultiplePartsReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testBarlinesReadToScore() {
		final Score score = readScore("barlines.musicxml", false);
		MusicXmlFileChecks.assertBarlinesReadCorrectly(score);
	}

	@Test
	void testBarlinesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("barlines.musicxml", false);
		MusicXmlFileChecks.assertBarlinesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testClefsReadToScore() {
		final Score score = readScore("clefs.musicxml", false);
		MusicXmlFileChecks.assertClefsReadCorrectly(score);
	}

	@Test
	void testClefsReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("clefs.musicxml", false);
		MusicXmlFileChecks.assertClefsReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testMultiStaffClefsReadToScore() {
		final Score score = readScore("multiStaffClefs.musicxml", false);
		MusicXmlFileChecks.assertMultiStaffClefsReadCorrectlyToScore(score);
	}

	@Test
	void testMultiStaffClefsReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("multiStaffClefs.musicxml", false);
		MusicXmlFileChecks.assertMultiStaffClefsReadCorrectlyToScore(scoreBuilder.build());
	}

	@Test
	void testKeySignaturesReadToScore() {
		final Score score = readScore("keysigs.musicxml", false);
		MusicXmlFileChecks.assertKeySignaturesReadToScoreCorrectly(score);
	}

	@Test
	void testKeySignaturesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("keysigs.musicxml", false);
		MusicXmlFileChecks.assertKeySignaturesReadToScoreCorrectly(scoreBuilder.build());
	}

	@Test
	void testMultiStaffPartReadToScore() {
		final Score score = readScore("multistaff.musicxml", false);
		MusicXmlFileChecks.assertMultiStaffPartReadCorrectly(score);
	}

	@Test
	void testMultiStaffPartReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("multistaff.musicxml", false);
		MusicXmlFileChecks.assertMultiStaffPartReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testTimeSignaturesReadToScore() {
		final Score score = readScore("timesigs.musicxml", false);
		MusicXmlFileChecks.assertTimeSignaturesReadCorrectly(score);
	}

	@Test
	void testTimeSignaturesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("timesigs.musicxml", false);
		MusicXmlFileChecks.assertTimeSignaturesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testTimeSignatureChangeReadToScore() {
		final Score score = readScore("scoreIteratorTesting.musicxml", false);
		MusicXmlFileChecks.assertTimeSignatureChangeReadCorrectly(score);
	}

	@Test
	void testTimeSignatureChangeReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("scoreIteratorTesting.musicxml", false);
		MusicXmlFileChecks.assertTimeSignatureChangeReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testTiedNotesReadToScore() {
		final Score score = readScore("tieTesting.musicxml", false);
		MusicXmlFileChecks.assertTiedNotesReadCorrectly(score);
	}

	@Test
	void testTiedNotesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("tieTesting.musicxml", false);
		MusicXmlFileChecks.assertTiedNotesReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testReadingScoreWithArticulations() {
		final Score score = readScore("articulations.musicxml", false);
		MusicXmlFileChecks.assertScoreWithArticulationsReadCorrectly(score);
	}

	@Test
	void testReadingScoreBuilderWithArticulations() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("articulations.musicxml", false);
		MusicXmlFileChecks.assertScoreWithArticulationsReadCorrectly(scoreBuilder.build());
	}

	@Test
	void testReadingIncorrectXmlFileToScore() {
		final MusicXmlReader reader = getMusicXmlReader(
				Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCinvalidXml.musicxml"), true);
		try {
			reader.readScore();
			fail("No exception was thrown when trying to read incorrectly formatted XML file");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingIncorrectXmlFileToScoreBuilder() {
		final MusicXmlReader reader = getMusicXmlReader(
				Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCinvalidXml.musicxml"), true);
		try {
			reader.readScoreBuilder();
			fail("No exception was thrown when trying to read incorrectly formatted XML file");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingIncorrectMusicXmlFileToScore() {
		final MusicXmlReader reader = getMusicXmlReader(
				Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCInvalidMusicXml.musicxml"), true);
		try {
			reader.readScore();
			fail("No exception was thrown when trying to read XML file that does not comply to MusicXml schema");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingIncorrectMusicXmlFileToScoreBuilder() {
		final MusicXmlReader reader = getMusicXmlReader(
				Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCInvalidMusicXml.musicxml"), true);
		try {
			reader.readScoreBuilder();
			fail("No exception was thrown when trying to read XML file that does not comply to MusicXml schema");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testValidatingCorrectXmlFileWhenReadingToScore() {
		final MusicXmlReader reader = getMusicXmlReader(
				Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleC.musicxml"), true);
		try {
			reader.readScore();
		} catch (Exception e) {
			fail("Exception " + e + " was thrown while validating valid MusicXml file");
		}
	}

	@Test
	void testValidatingCorrectXmlFileWhenReadingToScoreBuilder() {
		final MusicXmlReader reader = getMusicXmlReader(
				Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleC.musicxml"), true);
		try {
			reader.readScoreBuilder();
		} catch (Exception e) {
			fail("Exception " + e + " was thrown while validating valid MusicXml file");
		}
	}

	@Test
	void testReadingFileThatDoesNotExistToScore() {
		final MusicXmlReader reader = getMusicXmlReader(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH
				+ "aFileThatDoesNotAndShouldNotExistInTestFiles.musicxml"), true);
		try {
			reader.readScore();
		} catch (ParsingFailureException parsingFailure) {
			fail("Reading the score failed due to parsing failure when an io expection was expected");
		} catch (IOException ioException) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingFileThatDoesNotExistToScoreBuilder() {
		final MusicXmlReader reader = getMusicXmlReader(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH
				+ "aFileThatDoesNotAndShouldNotExistInTestFiles.musicxml"), true);
		try {
			reader.readScoreBuilder();
		} catch (ParsingFailureException parsingFailure) {
			fail("Reading the score failed due to parsing failure when an io expection was expected");
		} catch (IOException ioException) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingScoreWithPickupMeasure() {
		Score scoreWithPickup = readScore("pickup_measure_test.musicxml", false);
		MusicXmlFileChecks.assertPickupMeasureReadCorrectly(scoreWithPickup);
	}

	@Test
	void testReadingScoreBuilderWithPickupMeasure() {
		ScoreBuilder scoreWithPickup = readScoreBuilder("pickup_measure_test.musicxml", false);
		MusicXmlFileChecks.assertPickupMeasureReadCorrectly(scoreWithPickup.build());
	}

	@Test
	void testReadingAttributesIntoScore() {
		Score scoreWithAttributes = readScore("attribute_reading_test.musicxml", false);
		MusicXmlFileChecks.assertScoreHasExpectedAttributes(scoreWithAttributes);
	}

	@Test
	void testReadingAttributesIntoScoreBuilder() {
		ScoreBuilder scoreWithAttributesBuilder = readScoreBuilder("attribute_reading_test.musicxml", false);
		MusicXmlFileChecks.assertScoreHasExpectedAttributes(scoreWithAttributesBuilder.build());
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
		Score scoreWitNotations = readScore("multi_staff_multi_voice_notation_test.musicxml", false);
		MusicXmlFileChecks.assertNotationsReadCorrectlyFromMultipleStavesWithMultipleVoices(scoreWitNotations);
	}

	@Test
	void testReadingNotationsIntoScoreFromCompressedMultipleStavesAndVoices() {
		Score scoreWitNotations = readScore("multi_staff_multi_voice_notation_test.mxl", true);
		MusicXmlFileChecks.assertNotationsReadCorrectlyFromMultipleStavesWithMultipleVoices(scoreWitNotations);
	}

	@Test
	void testReadingNotationsIntoScoreBuilderFromMultipleStavesAndVoices() {
		ScoreBuilder scoreBuilderWitNotations = readScoreBuilder("multi_staff_multi_voice_notation_test.musicxml",
				false);
		MusicXmlFileChecks
				.assertNotationsReadCorrectlyFromMultipleStavesWithMultipleVoices(scoreBuilderWitNotations.build());
	}

	@Test
	void testGivenFileWhereNoteContinuesOverClefChangeWhenScoreIsReadThenClefChangeIsCorrect() {
		Score scoreWithClefChange = readScore("clef_change_where_note_in_another_voice_carries_over.musicxml", false);
		MusicXmlFileChecks.assertClefChangeInCorrectPlaceWhenNoteCarriesOverClefChange(scoreWithClefChange);
	}

	@Test
	void testGivenFileWhereNoteContinuesOverClefChangeWhenScoreBuilderIsReadThenClefChangeIsCorrect() {
		ScoreBuilder scoreBuilderWithClefChange = readScoreBuilder(
				"clef_change_where_note_in_another_voice_carries_over.musicxml", false);
		MusicXmlFileChecks
				.assertClefChangeInCorrectPlaceWhenNoteCarriesOverClefChange(scoreBuilderWithClefChange.build());
	}

	@Test
	void testDottedDurationsAreReadWithCorrectDotCounts() {
		Score scoreWithDottedDurations = readScore("dotted_note_test.musicxml", true);
		MusicXmlFileChecks.assertDottedNotesReadCorrectly(scoreWithDottedDurations);
	}

	@Test
	void testTupletDurationsAreReadWithCorrectTupletDivisors() {
		Score scoreWithTupletDurations = readScore("tuplet_test.musicxml", true);
		MusicXmlFileChecks.assertTupletNotesReadCorrectly(scoreWithTupletDurations);
	}

	@Test
	void testOrnamentsAreReadCorrectly() {
		Score scoreWithOrnaments = readScore("ornament_test.musicxml", true);
		MusicXmlFileChecks.assertOrnamentsAreCorrect(scoreWithOrnaments);
	}

	@Test
	void testGivenFileWithGraceNotesThenAllGraceNotesAreReadCorrectly() {
		Score scoreWithGraceNotes = readScore("grace_note_test.musicxml", true);
		MusicXmlFileChecks.assertGraceNotesAreCorrect(scoreWithGraceNotes);
	}

	@Test
	void testGivenFileWithGraceNoteChordsThenAllGraceNotesAreReadCorrectly() {
		Score scoreWithGraceNotes = readScore("grace_note_chord_test.musicxml", true);
		MusicXmlFileChecks.assertGraceNoteChordsAreCorrect(scoreWithGraceNotes);
	}

	@Test
	void testGivenFileWithDirectionsThenDirectionsCorrectlyRead() {
		Score scoreWithDirections = readScore("directions_test.musicxml", true);
		MusicXmlFileChecks.assertDirectionsCorrect(scoreWithDirections);
	}

	@Test
	void testGivenPercussionNotationThenUnpitchedNotesAreCorrectlyRead() {
		Score percussionScore = readScore("unpitched_notes_test.musicxml", true);
		MusicXmlFileChecks.assertUnpitchedPercussionNotesCorrect(percussionScore);
	}

	@Test
	void testGivenPlayingTechniqueMarkingsThenAllTechniquesAreCorrectlyRead() {
		Score score = readScore("techniques_test.musicxml", true);
		MusicXmlFileChecks.assertPlayingTechniquesAreCorrect(score);
	}

	@Test
	void testGivenScoreWithLyricsThenLyricsAreCorrectlyRead() {
		Score score = readScore("lyrics_test.musicxml", true);
		MusicXmlFileChecks.assertLyricsAreCorrect(score);
	}

	@Test
	void testGivenScoreWithChordSymbolsThenSymbolsAreCorrectlyRead() {
		Score score = readScore("chord_symbol_test.musicxml", true);
		MusicXmlFileChecks.assertChordSymbolsCorrect(score);
	}
}
