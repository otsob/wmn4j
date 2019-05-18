/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.builders.ScoreBuilder;
import org.wmn4j.notation.elements.Articulation;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MultiStaffPart;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;
import org.wmn4j.notation.elements.Score;
import org.wmn4j.notation.elements.SingleStaffPart;
import org.wmn4j.notation.elements.Staff;
import org.wmn4j.notation.elements.TimeSignature;
import org.wmn4j.notation.elements.TimeSignatures;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MusicXmlReaderDomTest {

	private static final String MUSICXML_FILE_PATH = "musicxml/";

	MusicXmlReader getMusicXmlReader() {
		return MusicXmlReader.getReader(false);
	}

	Score readScore(String testFileName) {
		final MusicXmlReader reader = getMusicXmlReader();
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

	ScoreBuilder readScoreBuilder(String testFileName) {
		final MusicXmlReader reader = getMusicXmlReader();
		ScoreBuilder scoreBuilder = null;
		final Path path = Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + testFileName);

		try {
			scoreBuilder = reader.readScoreBuilder(path);
		} catch (final IOException | ParsingFailureException e) {
			fail("Parsing failed with exception " + e);
		}

		assertNotNull(scoreBuilder);
		return scoreBuilder;
	}

	@Test
	void testReadScoreWithSingleNote() {
		final Score score = readScore("singleC.xml");
		assertSingleNoteScoreReadCorrectly(score);
	}

	@Test
	void testReadScoreBuilderWithSingleNote() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("singleC.xml");
		assertSingleNoteScoreReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "singleC.xml"
	 */
	private void assertSingleNoteScoreReadCorrectly(Score score) {
		assertEquals("Single C", score.getAttribute(Score.Attribute.MOVEMENT_TITLE));
		assertEquals(1, score.getParts().size());

		final Part part = score.getParts().get(0);
		assertTrue(part instanceof SingleStaffPart);
		final SingleStaffPart spart = (SingleStaffPart) part;
		final Staff staff = spart.getStaff();
		assertEquals("Part1", part.getName());
		assertEquals(1, staff.getMeasures().size());

		final Measure measure = staff.getMeasures().get(0);
		assertEquals(1, measure.getNumber());
		assertEquals(1, measure.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measure.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measure.getKeySignature());
		assertEquals(Barline.SINGLE, measure.getRightBarline());
		assertEquals(Clefs.G, measure.getClef());

		final List<Durational> voice = measure.getVoice(1);
		assertEquals(1, voice.size());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.WHOLE), voice.get(0));
	}

	@Test
	void testChordsAndMultipleVoicesReadToScore() {
		final Score score = readScore("twoMeasures.xml");
		assertChordsAndMultipleVoicesReadCorrectly(score);
	}

	@Test
	void testChordsAndMultipleVoicesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("twoMeasures.xml");
		assertChordsAndMultipleVoicesReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "twoMeasures.xml"
	 */
	private void assertChordsAndMultipleVoicesReadCorrectly(Score score) {
		assertEquals("Two bar sample", score.getAttribute(Score.Attribute.MOVEMENT_TITLE));
		assertEquals("TestFile Composer", score.getAttribute(Score.Attribute.COMPOSER));
		assertEquals(1, score.getParts().size());

		final Part part = score.getParts().get(0);
		assertTrue(part instanceof SingleStaffPart);
		final SingleStaffPart spart = (SingleStaffPart) part;
		final Staff staff = spart.getStaff();
		assertEquals("Part1", part.getName());
		assertEquals(2, staff.getMeasures().size());

		// Verify data of measure one
		final Measure measureOne = staff.getMeasures().get(0);
		assertEquals(1, measureOne.getNumber());
		assertEquals(1, measureOne.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measureOne.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measureOne.getKeySignature());
		assertEquals(Barline.SINGLE, measureOne.getRightBarline());
		assertEquals(Clefs.G, measureOne.getClef());

		// Verify notes of measure one
		List<Durational> voiceOne = measureOne.getVoice(1);
		assertEquals(8, voiceOne.size());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceOne.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT), voiceOne.get(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT), voiceOne.get(2));
		assertEquals(Rest.of(Durations.EIGHT), voiceOne.get(3));
		final Chord cMajor = Chord.of(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT),
				Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT),
				Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHT));
		assertEquals(cMajor, voiceOne.get(4));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), voiceOne.get(5));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), voiceOne.get(6));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), voiceOne.get(7));

		// Verify data of measure two
		final Measure measureTwo = staff.getMeasures().get(1);
		assertEquals(2, measureTwo.getNumber());
		assertEquals(2, measureTwo.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measureTwo.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measureTwo.getKeySignature());
		assertEquals(Barline.FINAL, measureTwo.getRightBarline());
		assertEquals(Clefs.G, measureTwo.getClef());

		// Verify notes of measure two
		voiceOne = measureTwo.getVoice(1);
		assertEquals(2, voiceOne.size());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.HALF), voiceOne.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.HALF), voiceOne.get(1));

		final List<Durational> voiceTwo = measureTwo.getVoice(2);
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceTwo.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceTwo.get(1));
		assertEquals(Rest.of(Durations.QUARTER), voiceTwo.get(2));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceTwo.get(3));
	}

	@Test
	void testReadScoreWithMultipleStaves() {
		final Score score = readScore("twoStavesAndMeasures.xml");
		assertScoreWithMultipleStavesReadCorrectly(score);
	}

	@Test
	void testReadScoreBuilderWithMultipleStaves() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("twoStavesAndMeasures.xml");
		assertScoreWithMultipleStavesReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "twoStavesAndMeasures.xml"
	 */
	private void assertScoreWithMultipleStavesReadCorrectly(Score score) {
		assertEquals("Multistaff test file", score.getAttribute(Score.Attribute.MOVEMENT_TITLE));
		assertEquals("TestFile Composer", score.getAttribute(Score.Attribute.COMPOSER));
		assertEquals(2, score.getParts().size());

		final SingleStaffPart partOne = (SingleStaffPart) score.getParts().get(0);
		final Staff staffOne = partOne.getStaff();
		assertEquals("Part1", partOne.getName());
		assertEquals(2, staffOne.getMeasures().size());

		// Verify data of measure one of staff one
		final Measure staffOneMeasureOne = staffOne.getMeasures().get(0);
		assertEquals(1, staffOneMeasureOne.getNumber());
		assertEquals(1, staffOneMeasureOne.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureOne.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffOneMeasureOne.getKeySignature());
		assertEquals(Barline.SINGLE, staffOneMeasureOne.getRightBarline());
		assertEquals(Clefs.G, staffOneMeasureOne.getClef());

		// Verify contents of measure one of staff one
		assertEquals(1, staffOneMeasureOne.getVoiceCount());
		final List<Durational> voiceMOne = staffOneMeasureOne.getVoice(1);
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF), voiceMOne.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER), voiceMOne.get(1));

		// Verify data of measure two of staff one
		final Measure staffOneMeasureTwo = staffOne.getMeasures().get(1);
		assertEquals(2, staffOneMeasureTwo.getNumber());
		assertEquals(1, staffOneMeasureTwo.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureTwo.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffOneMeasureTwo.getKeySignature());
		assertEquals(Barline.FINAL, staffOneMeasureTwo.getRightBarline());
		assertEquals(Clefs.G, staffOneMeasureTwo.getClef());

		// Verify contents of measure one of staff one
		assertEquals(1, staffOneMeasureTwo.getVoiceCount());
		final List<Durational> voiceM2 = staffOneMeasureTwo.getVoice(1);
		assertEquals(Rest.of(Durations.QUARTER), voiceM2.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF), voiceM2.get(1));

		final SingleStaffPart partTwo = (SingleStaffPart) score.getParts().get(1);
		final Staff staffTwo = partTwo.getStaff();
		assertEquals("Part2", partTwo.getName());
		assertEquals(2, staffTwo.getMeasures().size());

		// Verify data of measure one of staff two
		final Measure staffTwoMeasureOne = staffTwo.getMeasures().get(0);
		assertEquals(1, staffTwoMeasureOne.getNumber());
		assertEquals(1, staffTwoMeasureOne.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureOne.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffTwoMeasureOne.getKeySignature());
		assertEquals(Barline.SINGLE, staffTwoMeasureOne.getRightBarline());
		assertEquals(Clefs.F, staffTwoMeasureOne.getClef());

		// Verify contents of measure one of staff two
		assertEquals(1, staffTwoMeasureOne.getVoiceCount());
		final List<Durational> voiceMOneS2 = staffTwoMeasureOne.getVoice(1);
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), voiceMOneS2.get(0));

		// Verify data of measure two of staff two
		final Measure staffTwoMeasureTwo = staffTwo.getMeasures().get(1);
		assertEquals(2, staffTwoMeasureTwo.getNumber());
		assertEquals(1, staffTwoMeasureTwo.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureTwo.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffTwoMeasureTwo.getKeySignature());
		assertEquals(Barline.FINAL, staffTwoMeasureTwo.getRightBarline());
		assertEquals(Clefs.F, staffTwoMeasureTwo.getClef());

		// Verify contents of measure two of staff two
		assertEquals(1, staffTwoMeasureTwo.getVoiceCount());
		final List<Durational> voiceM2S2 = staffTwoMeasureTwo.getVoice(1);
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), voiceM2S2.get(0));
	}

	@Test
	void testBarlinesReadToScore() {
		final Score score = readScore("barlines.xml");
		assertBarlinesReadCorrectly(score);
	}

	@Test
	void testBarlinesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("barlines.xml");
		assertBarlinesReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "barlines.xml"
	 */
	private void assertBarlinesReadCorrectly(Score score) {

		assertEquals(1, score.getParts().size());
		final SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);

		assertEquals(Barline.SINGLE, part.getMeasure(1).getRightBarline());
		assertEquals(Barline.NONE, part.getMeasure(1).getLeftBarline());

		assertEquals(Barline.DOUBLE, part.getMeasure(2).getRightBarline());
		assertEquals(Barline.NONE, part.getMeasure(2).getLeftBarline());

		assertEquals(Barline.THICK, part.getMeasure(3).getRightBarline());
		assertEquals(Barline.NONE, part.getMeasure(3).getLeftBarline());

		assertEquals(Barline.DASHED, part.getMeasure(4).getRightBarline());
		assertEquals(Barline.NONE, part.getMeasure(4).getLeftBarline());

		assertEquals(Barline.INVISIBLE, part.getMeasure(5).getRightBarline());
		assertEquals(Barline.NONE, part.getMeasure(5).getLeftBarline());

		assertEquals(Barline.SINGLE, part.getMeasure(6).getRightBarline());
		assertEquals(Barline.REPEAT_LEFT, part.getMeasure(6).getLeftBarline());

		assertEquals(Barline.REPEAT_RIGHT, part.getMeasure(7).getRightBarline());
		assertEquals(Barline.NONE, part.getMeasure(7).getLeftBarline());

		assertEquals(Barline.REPEAT_RIGHT, part.getMeasure(8).getRightBarline());
		assertEquals(Barline.REPEAT_LEFT, part.getMeasure(8).getLeftBarline());

		assertEquals(Barline.FINAL, part.getMeasure(9).getRightBarline());
		assertEquals(Barline.NONE, part.getMeasure(9).getLeftBarline());
	}

	@Test
	void testClefsReadToScore() {
		final Score score = readScore("clefs.xml");
		assertClefsReadCorrectly(score);
	}

	@Test
	void testClefsReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("clefs.xml");
		assertClefsReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "clefs.xml"
	 */
	private void assertClefsReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);

		assertEquals(Clefs.G, part.getMeasure(1).getClef());
		assertFalse(part.getMeasure(1).containsClefChanges());

		assertEquals(Clefs.ALTO, part.getMeasure(2).getClef());
		assertFalse(part.getMeasure(2).containsClefChanges());

		assertEquals(Clef.of(Clef.Symbol.C, 4), part.getMeasure(3).getClef());
		assertFalse(part.getMeasure(3).containsClefChanges());

		assertEquals(Clef.of(Clef.Symbol.C, 4), part.getMeasure(4).getClef());
		assertTrue(part.getMeasure(4).containsClefChanges());
		final Map<Duration, Clef> clefChanges = part.getMeasure(4).getClefChanges();
		assertEquals(2, clefChanges.size());
		assertEquals(Clefs.F, clefChanges.get(Durations.QUARTER));
		assertEquals(Clefs.PERCUSSION, clefChanges.get(Durations.WHOLE));

		assertEquals(Clefs.PERCUSSION, part.getMeasure(5).getClef());
		assertTrue(part.getMeasure(5).containsClefChanges());
		assertEquals(2, part.getMeasure(5).getClefChanges().size());
		assertEquals(Clefs.G, part.getMeasure(5).getClefChanges().get(Durations.QUARTER));
		assertEquals(Clefs.F, part.getMeasure(5).getClefChanges().get(Durations.HALF.addDot()));
	}

	@Test
	void testMultiStaffClefsReadToScore() {
		final Score score = readScore("multiStaffClefs.xml");
		assertMultiStaffClefsReadCorrectlyToScore(score);
	}

	@Test
	void testMultiStaffClefsReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("multiStaffClefs.xml");
		assertMultiStaffClefsReadCorrectlyToScore(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "multiStaffClefs.xml"
	 */
	private void assertMultiStaffClefsReadCorrectlyToScore(Score score) {
		final MultiStaffPart part = (MultiStaffPart) score.getParts().get(0);
		final Staff upper = part.getStaff(1);
		final Staff lower = part.getStaff(2);

		// Check upper staff
		assertEquals(Clefs.G, upper.getMeasure(1).getClef(), "Incorrect clef measure 1 upper staff beginning");
		assertTrue(upper.getMeasure(1).containsClefChanges(), "Upper staff measure 1 does not contain a clef change");
		assertEquals(1, upper.getMeasure(1).getClefChanges().size(), "Incorrect number of clef changes");
		assertEquals(Clefs.ALTO,
				upper.getMeasure(1).getClefChanges().get(Durations.HALF.addDot()), "Incorrect clef change");

		assertEquals(Clefs.ALTO, upper.getMeasure(2).getClef(), "Incorrect clef measure 2 upper staff.");
		assertFalse(upper.getMeasure(2).containsClefChanges(), "Upper staff measure 2 contains a clef change");

		// Check lower staff
		assertEquals(Clefs.F, lower.getMeasure(1).getClef(), "Incorrect clef in measure 1 lower staff");
		assertTrue(lower.getMeasure(1).containsClefChanges(), "Lower staff measure 1 does not contain a clef change");
		final Map<Duration, Clef> clefChanges = lower.getMeasure(1).getClefChanges();
		assertEquals(1, clefChanges.size(), "Incorrect number of clef changes");
		final Duration offset = Durations.HALF.add(Durations.SIXTEENTH.multiplyBy(3));
		assertEquals(Clefs.G, clefChanges.get(offset), "Incorrect clef change");

		assertEquals(Clefs.G, lower.getMeasure(2).getClef(), "Incorrect clef measure 2 of lower staff");
		assertFalse(lower.getMeasure(2).containsClefChanges(), "Lower staff measure 2 contians clef changes");
	}

	@Test
	void testKeySignaturesReadToScore() {
		final Score score = readScore("keysigs.xml");
		assertKeySignaturesReadToScoreCorrectly(score);
	}

	@Test
	void testKeySignaturesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("keysigs.xml");
		assertKeySignaturesReadToScoreCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "keysigs.xml"
	 */
	private void assertKeySignaturesReadToScoreCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);

		assertEquals(KeySignatures.CMAJ_AMIN, part.getMeasure(1).getKeySignature());
		assertEquals(KeySignatures.GMAJ_EMIN, part.getMeasure(2).getKeySignature());
		assertEquals(KeySignatures.AFLATMAJ_FMIN, part.getMeasure(3).getKeySignature());
	}

	@Test
	void testMultiStaffPartReadToScore() {
		final Score score = readScore("multistaff.xml");
		assertMultiStaffPartReadCorrectly(score);
	}

	@Test
	void testMultiStaffPartReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("multistaff.xml");
		assertMultiStaffPartReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "multistaff.xml"
	 */
	private void assertMultiStaffPartReadCorrectly(Score score) {
		assertEquals(2, score.getPartCount());
		MultiStaffPart multiStaff = null;
		SingleStaffPart singleStaff = null;
		for (Part part : score) {
			if (part.getName().equals("MultiStaff")) {
				multiStaff = (MultiStaffPart) part;
			}
			if (part.getName().equals("SingleStaff")) {
				singleStaff = (SingleStaffPart) part;
			}
		}

		assertTrue(multiStaff != null);
		assertTrue(singleStaff != null);

		assertEquals(3, singleStaff.getMeasureCount());
		assertEquals(3, multiStaff.getMeasureCount());

		assertEquals(2, multiStaff.getStaffCount());

		int measureCount = 0;
		final Note expectedNote = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.WHOLE);

		for (Measure measure : multiStaff) {
			assertTrue(measure.isSingleVoice());
			final List<Durational> voice = measure.getVoice(measure.getVoiceNumbers().get(0));
			assertEquals(1, voice.size());
			assertTrue(voice.get(0).equals(expectedNote));
			++measureCount;
		}

		assertEquals(6, measureCount, "Incorrect number of measures in multistaff part");
	}

	@Test
	void testTimeSignaturesReadToScore() {
		final Score score = readScore("timesigs.xml");
		assertTimeSignaturesReadCorrectly(score);
	}

	@Test
	void testTimeSignaturesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("timesigs.xml");
		assertTimeSignaturesReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "timesigs.xml"
	 */
	private void assertTimeSignaturesReadCorrectly(Score score) {
		assertEquals(1, score.getPartCount());
		final SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
		assertEquals(TimeSignature.of(2, 2), part.getMeasure(1).getTimeSignature());
		assertEquals(TimeSignature.of(3, 4), part.getMeasure(2).getTimeSignature());
		assertEquals(TimeSignature.of(6, 8), part.getMeasure(3).getTimeSignature());
		assertEquals(TimeSignature.of(15, 16), part.getMeasure(4).getTimeSignature());
	}

	@Test
	void testTimeSignatureChangeReadToScore() {
		final Score score = readScore("scoreIteratorTesting.xml");
		assertTimeSignatureChangeReadCorrectly(score);
	}

	@Test
	void testTimeSignatureChangeReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("scoreIteratorTesting.xml");
		assertTimeSignatureChangeReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "scoreIteratorTesting.xml"
	 */
	private void assertTimeSignatureChangeReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
		final Durational n = part.getMeasure(2).getVoice(1).get(0);
		assertEquals(Durations.EIGHT, n.getDuration());
	}

	@Test
	void testTiedNotesReadToScore() {
		final Score score = readScore("tieTesting.xml");
		assertTiedNotesReadCorrectly(score);
	}

	@Test
	void testTiedNotesReadToScoreBuilder() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("tieTesting.xml");
		assertTiedNotesReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "tieTesting.xml"
	 */
	private void assertTiedNotesReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);

		final Measure firstMeasure = part.getMeasure(1);
		final Note first = (Note) firstMeasure.get(1, 0);
		assertTrue(first.isTiedToFollowing());
		assertEquals(Pitch.of(Pitch.Base.C, 0, 4), first.getFollowingTiedNote().get().getPitch());

		final Note second = (Note) firstMeasure.get(1, 1);
		assertTrue(second.isTiedFromPrevious());
		assertFalse(second.isTiedToFollowing());

		final Note third = (Note) firstMeasure.get(1, 2);
		assertTrue(third.isTiedToFollowing());
		assertFalse(third.isTiedFromPrevious());

		final Measure secondMeasure = part.getMeasure(2);
		final Note fourth = (Note) secondMeasure.get(1, 0);
		assertEquals(fourth, third.getFollowingTiedNote().get());
		assertTrue(fourth.isTiedFromPrevious());
		assertFalse(fourth.isTiedToFollowing());

		final Measure thirdMeasure = part.getMeasure(3);
		final Note fifth = (Note) thirdMeasure.get(1, 0);
		assertFalse(fifth.isTied());
		final Note sixth = (Note) thirdMeasure.get(1, 1);
		assertFalse(sixth.isTied());
		final Note seventh = (Note) thirdMeasure.get(1, 2);
		assertFalse(seventh.isTied());

		final Note eight = (Note) thirdMeasure.get(1, 3);
		assertTrue(eight.isTiedToFollowing());
		assertEquals(Durations.WHOLE.multiplyBy(2).add(Durations.QUARTER), eight.getTiedDuration());

		final Measure fourthMeasure = part.getMeasure(4);
		final Note ninth = (Note) fourthMeasure.get(1, 0);
		assertTrue(ninth.isTiedFromPrevious());
		assertTrue(ninth.isTiedToFollowing());
	}

	@Test
	void testReadingScoreWithArticulations() {
		final Score score = readScore("articulations.xml");
		assertScoreWithArticulationsReadCorrectly(score);
	}

	@Test
	void testReadingScoreBuilderWithArticulations() {
		final ScoreBuilder scoreBuilder = readScoreBuilder("articulations.xml");
		assertScoreWithArticulationsReadCorrectly(scoreBuilder.build());
	}

	/*
	 * Expects the contents of the file "articulations.xml"
	 */
	private void assertScoreWithArticulationsReadCorrectly(Score score) {
		final Measure measureOne = score.getPart(0).getMeasure(0, 1);
		assertTrue(((Note) measureOne.get(1, 0)).hasArticulation(Articulation.STACCATO));
		assertTrue(((Note) measureOne.get(1, 1)).hasArticulation(Articulation.ACCENT));
		assertTrue(((Note) measureOne.get(1, 2)).hasArticulation(Articulation.TENUTO));
		assertTrue(((Note) measureOne.get(1, 3)).hasArticulation(Articulation.FERMATA));

		final Measure measureTwo = score.getPart(0).getMeasure(0, 2);
		Note firstNoteInMeasureTwo = (Note) measureTwo.get(1, 0);
		assertTrue(firstNoteInMeasureTwo.hasArticulation(Articulation.STACCATO));
		assertTrue(firstNoteInMeasureTwo.hasArticulation(Articulation.ACCENT));
		assertTrue(firstNoteInMeasureTwo.hasArticulation(Articulation.TENUTO));
		assertTrue(firstNoteInMeasureTwo.hasArticulation(Articulation.FERMATA));
		assertEquals(4, firstNoteInMeasureTwo.getArticulations().size());

		assertTrue(((Chord) measureTwo.get(1, 1)).hasArticulation(Articulation.STACCATO));

		Chord secondChordInMeasureTwo = (Chord) measureTwo.get(1, 2);
		assertEquals(3, secondChordInMeasureTwo.getArticulations().size());
		assertTrue((secondChordInMeasureTwo.hasArticulation(Articulation.STACCATO)));
		assertTrue((secondChordInMeasureTwo.hasArticulation(Articulation.ACCENT)));
		assertTrue((secondChordInMeasureTwo.hasArticulation(Articulation.FERMATA)));
		assertFalse((secondChordInMeasureTwo.hasArticulation(Articulation.TENUTO)));

		assertFalse(((Note) measureTwo.get(1, 3)).hasArticulations());
	}

	@Test
	void testReadingIncorrectXmlFileToScore() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScore(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCinvalidXml.xml"));
			fail("No exception was thrown when trying to read incorrectly formatted XML file");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingIncorrectXmlFileToScoreBuilder() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScoreBuilder(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCinvalidXml.xml"));
			fail("No exception was thrown when trying to read incorrectly formatted XML file");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingIncorrectMusicXmlFileToScore() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScore(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCInvalidMusicXml.xml"));
			fail("No exception was thrown when trying to read XML file that does not comply to MusicXml schema");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingIncorrectMusicXmlFileToScoreBuilder() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScoreBuilder(
					Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleCInvalidMusicXml.xml"));
			fail("No exception was thrown when trying to read XML file that does not comply to MusicXml schema");
		} catch (IOException ioException) {
			fail("Reading the score failed due to IOException when a parsing failure was expected");
		} catch (ParsingFailureException e) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testValidatingCorrectXmlFileWhenReadingToScore() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScore(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleC.xml"));
		} catch (Exception e) {
			fail("Exception " + e + " was thrown while validating valid MusicXml file");
		}
	}

	@Test
	void testValidatingCorrectXmlFileWhenReadingToScoreBuilder() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScoreBuilder(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "singleC.xml"));
		} catch (Exception e) {
			fail("Exception " + e + " was thrown while validating valid MusicXml file");
		}
	}

	@Test
	void testReadingFileThatDoesNotExistToScore() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScore(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH
					+ "aFileThatDoesNotAndShouldNotExistInTestFiles.xml"));
		} catch (ParsingFailureException parsingFailure) {
			fail("Reading the score failed due to parsing failure when an io expection was expected");
		} catch (IOException ioException) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingFileThatDoesNotExistToScoreBuilder() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			reader.readScoreBuilder(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH
					+ "aFileThatDoesNotAndShouldNotExistInTestFiles.xml"));
		} catch (ParsingFailureException parsingFailure) {
			fail("Reading the score failed due to parsing failure when an io expection was expected");
		} catch (IOException ioException) {
			// Pass the test, this exception is expected.
		}
	}

	@Test
	void testReadingScoreWithPickupMeasure() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			Score scoreWithPickup = reader
					.readScore(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "pickup_measure_test.xml"));
			assertPickupMeasureReadCorrectly(scoreWithPickup);
		} catch (Exception exception) {
			fail("Reading score with pickup measure failed with " + exception);
		}
	}

	@Test
	void testReadingScoreBuilderWithPickupMeasure() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			ScoreBuilder scoreWithPickup = reader
					.readScoreBuilder(
							Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "pickup_measure_test.xml"));
			assertPickupMeasureReadCorrectly(scoreWithPickup.build());
		} catch (Exception exception) {
			fail("Reading score with pickup measure failed with " + exception);
		}
	}

	private void assertPickupMeasureReadCorrectly(Score score) {
		assertEquals(1, score.getPartCount());
		Part part = score.getParts().get(0);
		assertFalse(part.isMultiStaff());
		assertEquals(4, part.getMeasureCount());
		assertEquals(3, part.getFullMeasureCount());

		assertTrue(part instanceof SingleStaffPart);
		Staff staff = ((SingleStaffPart) part).getStaff();
		assertTrue(staff.hasPickupMeasure());
		assertEquals(4, staff.getMeasureCount());
		assertEquals(3, staff.getFullMeasureCount());

		Measure pickupMeasure = staff.getMeasure(0);
		assertTrue(pickupMeasure.isPickUp());
		assertEquals(0, pickupMeasure.getNumber());
	}

	@Test
	void testReadingAttributesIntoScore() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			Score scoreWithAttributes = reader
					.readScore(Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "attribute_reading_test.xml"));
			assertScoreHasExpectedAttributes(scoreWithAttributes);
		} catch (Exception exception) {
			fail("Reading attributes test file failed with " + exception);
		}
	}

	@Test
	void testReadingAttributesIntoScoreBuilder() {
		final MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			ScoreBuilder scoreWithAttributesBuilder = reader
					.readScoreBuilder(
							Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + "attribute_reading_test.xml"));
			assertScoreHasExpectedAttributes(scoreWithAttributesBuilder.build());
		} catch (Exception exception) {
			fail("Reading attributes test file failed with " + exception);
		}
	}

	private void assertScoreHasExpectedAttributes(Score score) {
		assertEquals("Composition title", score.getTitle());
		assertEquals("Composer name", score.getAttribute(Score.Attribute.COMPOSER));
		assertEquals("Movement title", score.getAttribute(Score.Attribute.MOVEMENT_TITLE));
		assertEquals("Arranger name", score.getAttribute(Score.Attribute.ARRANGER));

		List<Part> parts = score.getParts();
		assertEquals(2, parts.size());
		Part part1 = parts.get(0);
		assertEquals("Part name 1", part1.getName());
		assertEquals("Short part name 1", part1.getAttribute(Part.Attribute.ABBREVIATED_NAME));

		Part part2 = parts.get(1);
		assertEquals("Part name 2", part2.getName());
		assertEquals("Short part name 2", part2.getAttribute(Part.Attribute.ABBREVIATED_NAME));
	}
}
