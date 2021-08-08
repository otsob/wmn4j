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
}
