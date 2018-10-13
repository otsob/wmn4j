/*
* Copyright 2018 Otso Björklund.
* Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
*/
package wmnlibio.musicxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import wmnlibnotation.TestHelper;
import wmnlibnotation.noteobjects.Articulation;
import wmnlibnotation.noteobjects.Barline;
import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.Clef;
import wmnlibnotation.noteobjects.Clefs;
import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.KeySignatures;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.MultiStaffPart;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Part;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Rest;
import wmnlibnotation.noteobjects.Score;
import wmnlibnotation.noteobjects.SingleStaffPart;
import wmnlibnotation.noteobjects.Staff;
import wmnlibnotation.noteobjects.TimeSignature;
import wmnlibnotation.noteobjects.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
public class MusicXmlReaderDomTest {

	static final String MUSICXML_FILE_PATH = "musicxml/";

	public MusicXmlReaderDomTest() {
	}

	public MusicXmlReader getMusicXmlReader() {
		return MusicXmlReader.getReader(false);
	}

	public Score readScore(String testFileName) {
		MusicXmlReader reader = getMusicXmlReader();
		Score score = null;
		Path path = Paths.get(TestHelper.TESTFILE_PATH + MUSICXML_FILE_PATH + testFileName);

		try {
			score = reader.readScore(path);
		} catch (IOException e) {
			fail("Parsing failed with exception " + e);
		}

		assertTrue("score is null", score != null);
		return score;
	}

	@Test
	public void testReadScoreWithSingleNote() {
		Score score = readScore("singleC.xml");

		assertEquals("Single C", score.getName());
		assertEquals(1, score.getParts().size());

		Part part = score.getParts().get(0);
		assertTrue(part instanceof SingleStaffPart);
		SingleStaffPart spart = (SingleStaffPart) part;
		Staff staff = spart.getStaff();
		assertEquals("Part1", part.getName());
		assertEquals(1, staff.getMeasures().size());

		Measure measure = staff.getMeasures().get(0);
		assertEquals(1, measure.getNumber());
		assertEquals(1, measure.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measure.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measure.getKeySignature());
		assertEquals(Barline.SINGLE, measure.getRightBarline());
		assertEquals(Clefs.G, measure.getClef());

		List<Durational> voice = measure.getVoice(1);
		assertEquals(1, voice.size());
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.WHOLE), voice.get(0));
	}

	@Test
	public void testChordsAndMultipleVoices() {
		Score score = readScore("twoMeasures.xml");

		assertEquals("Two bar sample", score.getName());
		assertEquals("TestFile Composer", score.getAttribute(Score.Attribute.COMPOSER));
		assertEquals(1, score.getParts().size());

		Part part = score.getParts().get(0);
		assertTrue(part instanceof SingleStaffPart);
		SingleStaffPart spart = (SingleStaffPart) part;
		Staff staff = spart.getStaff();
		assertEquals("Part1", part.getName());
		assertEquals(2, staff.getMeasures().size());

		// Verify data of measure one
		Measure measureOne = staff.getMeasures().get(0);
		assertEquals(1, measureOne.getNumber());
		assertEquals(1, measureOne.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measureOne.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measureOne.getKeySignature());
		assertEquals(Barline.SINGLE, measureOne.getRightBarline());
		assertEquals(Clefs.G, measureOne.getClef());

		// Verify notes of measure one
		List<Durational> voiceOne = measureOne.getVoice(1);
		assertEquals(8, voiceOne.size());
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceOne.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voiceOne.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voiceOne.get(2));
		assertEquals(Rest.getRest(Durations.EIGHT), voiceOne.get(3));
		Chord cMajor = Chord.getChord(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT),
				Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT),
				Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));
		assertEquals(cMajor, voiceOne.get(4));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), voiceOne.get(5));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), voiceOne.get(6));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), voiceOne.get(7));

		// Verify data of measure two
		Measure measureTwo = staff.getMeasures().get(1);
		assertEquals(2, measureTwo.getNumber());
		assertEquals(2, measureTwo.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measureTwo.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measureTwo.getKeySignature());
		assertEquals(Barline.FINAL, measureTwo.getRightBarline());
		assertEquals(Clefs.G, measureTwo.getClef());

		// Verify notes of measure two
		voiceOne = measureTwo.getVoice(1);
		assertEquals(2, voiceOne.size());
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.HALF), voiceOne.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.HALF), voiceOne.get(1));

		List<Durational> voiceTwo = measureTwo.getVoice(2);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceTwo.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceTwo.get(1));
		assertEquals(Rest.getRest(Durations.QUARTER), voiceTwo.get(2));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), voiceTwo.get(3));
	}

	@Test
	public void testReadScoreWithMultipleStaves() {
		Score score = readScore("twoStavesAndMeasures.xml");

		assertEquals("Multistaff test file", score.getName());
		assertEquals("TestFile Composer", score.getAttribute(Score.Attribute.COMPOSER));
		assertEquals(2, score.getParts().size());

		SingleStaffPart partOne = (SingleStaffPart) score.getParts().get(0);
		Staff staffOne = partOne.getStaff();
		assertEquals("Part1", partOne.getName());
		assertEquals(2, staffOne.getMeasures().size());

		// Verify data of measure one of staff one
		Measure staffOneMeasureOne = staffOne.getMeasures().get(0);
		assertEquals(1, staffOneMeasureOne.getNumber());
		assertEquals(1, staffOneMeasureOne.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureOne.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffOneMeasureOne.getKeySignature());
		assertEquals(Barline.SINGLE, staffOneMeasureOne.getRightBarline());
		assertEquals(Clefs.G, staffOneMeasureOne.getClef());

		// Verify contents of measure one of staff one
		assertEquals(1, staffOneMeasureOne.getVoiceCount());
		List<Durational> voiceMOne = staffOneMeasureOne.getVoice(1);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF), voiceMOne.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.QUARTER), voiceMOne.get(1));

		// Verify data of measure two of staff one
		Measure staffOneMeasureTwo = staffOne.getMeasures().get(1);
		assertEquals(2, staffOneMeasureTwo.getNumber());
		assertEquals(1, staffOneMeasureTwo.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureTwo.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffOneMeasureTwo.getKeySignature());
		assertEquals(Barline.FINAL, staffOneMeasureTwo.getRightBarline());
		assertEquals(Clefs.G, staffOneMeasureTwo.getClef());

		// Verify contents of measure one of staff one
		assertEquals(1, staffOneMeasureTwo.getVoiceCount());
		List<Durational> voiceM2 = staffOneMeasureTwo.getVoice(1);
		assertEquals(Rest.getRest(Durations.QUARTER), voiceM2.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF), voiceM2.get(1));

		SingleStaffPart partTwo = (SingleStaffPart) score.getParts().get(1);
		Staff staffTwo = partTwo.getStaff();
		assertEquals("Part2", partTwo.getName());
		assertEquals(2, staffTwo.getMeasures().size());

		// Verify data of measure one of staff two
		Measure staffTwoMeasureOne = staffTwo.getMeasures().get(0);
		assertEquals(1, staffTwoMeasureOne.getNumber());
		assertEquals(1, staffTwoMeasureOne.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureOne.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffTwoMeasureOne.getKeySignature());
		assertEquals(Barline.SINGLE, staffTwoMeasureOne.getRightBarline());
		assertEquals(Clefs.F, staffTwoMeasureOne.getClef());

		// Verify contents of measure one of staff two
		assertEquals(1, staffTwoMeasureOne.getVoiceCount());
		List<Durational> voiceMOneS2 = staffTwoMeasureOne.getVoice(1);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), voiceMOneS2.get(0));

		// Verify data of measure two of staff two
		Measure staffTwoMeasureTwo = staffTwo.getMeasures().get(1);
		assertEquals(2, staffTwoMeasureTwo.getNumber());
		assertEquals(1, staffTwoMeasureTwo.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureTwo.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffTwoMeasureTwo.getKeySignature());
		assertEquals(Barline.FINAL, staffTwoMeasureTwo.getRightBarline());
		assertEquals(Clefs.F, staffTwoMeasureTwo.getClef());

		// Verify contents of measure two of staff two
		assertEquals(1, staffTwoMeasureTwo.getVoiceCount());
		List<Durational> voiceM2S2 = staffTwoMeasureTwo.getVoice(1);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), voiceM2S2.get(0));
	}

	@Test
	public void testBarlines() {
		Score score = readScore("barlines.xml");

		assertEquals(1, score.getParts().size());
		SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
		Staff staff = part.getStaff();

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
	public void testClefs() {
		Score score = readScore("clefs.xml");
		SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);

		assertEquals(Clefs.G, part.getMeasure(1).getClef());
		assertFalse(part.getMeasure(1).containsClefChanges());

		assertEquals(Clefs.ALTO, part.getMeasure(2).getClef());
		assertFalse(part.getMeasure(2).containsClefChanges());

		assertEquals(Clef.getClef(Clef.Type.C, 4), part.getMeasure(3).getClef());
		assertFalse(part.getMeasure(3).containsClefChanges());

		assertEquals(Clef.getClef(Clef.Type.C, 4), part.getMeasure(4).getClef());
		assertTrue(part.getMeasure(4).containsClefChanges());
		Map<Duration, Clef> clefChanges = part.getMeasure(4).getClefChanges();
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
	public void testMultiStaffClefs() {
		Score score = readScore("multiStaffClefs.xml");
		MultiStaffPart part = (MultiStaffPart) score.getParts().get(0);
		Staff upper = part.getStaff(1);
		Staff lower = part.getStaff(2);

		// Check upper staff
		assertEquals("Incorrect clef measure 1 upper staff beginning", Clefs.G, upper.getMeasure(1).getClef());
		assertTrue("Upper staff measure 1 does not contain a clef change", upper.getMeasure(1).containsClefChanges());
		assertEquals("Incorrect number of clef changes", 1, upper.getMeasure(1).getClefChanges().size());
		assertEquals("Incorrect clef change", Clefs.ALTO,
				upper.getMeasure(1).getClefChanges().get(Durations.HALF.addDot()));

		assertEquals("Incorrect clef measure 2 upper staff.", Clefs.ALTO, upper.getMeasure(2).getClef());
		assertFalse("Upper staff measure 2 contains a clef change", upper.getMeasure(2).containsClefChanges());

		// Check lower staff
		assertEquals("Incorrect clef in measure 1 lower staff", Clefs.F, lower.getMeasure(1).getClef());
		assertTrue("Lower staff measure 1 does not contain a clef change", lower.getMeasure(1).containsClefChanges());
		Map<Duration, Clef> clefChanges = lower.getMeasure(1).getClefChanges();
		assertEquals("Incorrect number of clef changes", 1, clefChanges.size());
		Duration offset = Durations.HALF.add(Durations.SIXTEENTH.multiplyBy(3));
		assertEquals("Incorrect clef change", Clefs.G, clefChanges.get(offset));

		assertEquals("Incorrect clef measure 2 of lower staff", Clefs.G, lower.getMeasure(2).getClef());
		assertFalse("Lower staff measure 2 contians clef changes", lower.getMeasure(2).containsClefChanges());
	}

	@Test
	public void testKeySignatures() {
		Score score = readScore("keysigs.xml");

		SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);

		assertEquals(KeySignatures.CMAJ_AMIN, part.getMeasure(1).getKeySignature());
		assertEquals(KeySignatures.GMAJ_EMIN, part.getMeasure(2).getKeySignature());
		assertEquals(KeySignatures.AFLATMAJ_FMIN, part.getMeasure(3).getKeySignature());
	}

	@Test
	public void testMultiStaffPart() {
		Score score = readScore("multistaff.xml");
		assertEquals(2, score.getPartCount());
		MultiStaffPart multiStaff = null;
		SingleStaffPart singleStaff = null;
		for (Part part : score) {
			if (part.getName().equals("MultiStaff"))
				multiStaff = (MultiStaffPart) part;
			if (part.getName().equals("SingleStaff"))
				singleStaff = (SingleStaffPart) part;
		}

		assertTrue(multiStaff != null);
		assertTrue(singleStaff != null);

		assertEquals(3, singleStaff.getMeasureCount());
		assertEquals(3, multiStaff.getMeasureCount());

		assertEquals(2, multiStaff.getStaffCount());

		int measureCount = 0;
		Note expectedNote = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.WHOLE);

		for (Measure measure : multiStaff) {
			assertTrue(measure.isSingleVoice());
			List<Durational> voice = measure.getVoice(measure.getVoiceNumbers().get(0));
			assertEquals(1, voice.size());
			assertTrue(voice.get(0).equals(expectedNote));
			++measureCount;
		}

		assertEquals("Incorrect number of measures in multistaff part", 6, measureCount);
	}

	@Test
	public void testTimeSignatures() {
		Score score = readScore("timesigs.xml");
		assertEquals(1, score.getPartCount());
		SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
		assertEquals(TimeSignature.getTimeSignature(2, 2), part.getMeasure(1).getTimeSignature());
		assertEquals(TimeSignature.getTimeSignature(3, 4), part.getMeasure(2).getTimeSignature());
		assertEquals(TimeSignature.getTimeSignature(6, 8), part.getMeasure(3).getTimeSignature());
		assertEquals(TimeSignature.getTimeSignature(15, 16), part.getMeasure(4).getTimeSignature());
	}

	@Test
	public void testTimeSignatureChange() {
		Score score = readScore("scoreIteratorTesting.xml");
		SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
		Durational n = part.getMeasure(2).getVoice(1).get(0);
		assertEquals(Durations.EIGHT, n.getDuration());
	}

	@Test
	public void testTiedNotes() {
		Score score = readScore("tieTesting.xml");
		SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);

		Measure firstMeasure = part.getMeasure(1);
		Note first = (Note) firstMeasure.get(1, 0);
		assertTrue(first.isTiedToFollowing());
		assertEquals(Pitch.getPitch(Pitch.Base.C, 0, 4), first.getFollowingTiedNote().get().getPitch());

		Note second = (Note) firstMeasure.get(1, 1);
		assertTrue(second.isTiedFromPrevious());
		assertFalse(second.isTiedToFollowing());

		Note third = (Note) firstMeasure.get(1, 2);
		assertTrue(third.isTiedToFollowing());
		assertFalse(third.isTiedFromPrevious());

		Measure secondMeasure = part.getMeasure(2);
		Note fourth = (Note) secondMeasure.get(1, 0);
		assertEquals(fourth, third.getFollowingTiedNote().get());
		assertTrue(fourth.isTiedFromPrevious());
		assertFalse(fourth.isTiedToFollowing());

		Measure thirdMeasure = part.getMeasure(3);
		Note fifth = (Note) thirdMeasure.get(1, 0);
		assertFalse(fifth.isTied());
		Note sixth = (Note) thirdMeasure.get(1, 1);
		assertFalse(sixth.isTied());
		Note seventh = (Note) thirdMeasure.get(1, 2);
		assertFalse(seventh.isTied());

		Note eight = (Note) thirdMeasure.get(1, 3);
		assertTrue(eight.isTiedToFollowing());
		assertEquals(Durations.WHOLE.multiplyBy(2).add(Durations.QUARTER), eight.getTiedDuration());

		Measure fourthMeasure = part.getMeasure(4);
		Note ninth = (Note) fourthMeasure.get(1, 0);
		assertTrue(ninth.isTiedFromPrevious());
		assertTrue(ninth.isTiedToFollowing());
	}

	@Test
	public void testReadingScoreWithArticulations() {
		Score score = readScore("articulations.xml");

		System.out.println(score);

		Measure measure = score.getPart(0).getMeasure(0, 1);
		assertTrue(((Note) measure.get(1, 0)).hasArticulation(Articulation.STACCATO));
		assertTrue(((Note) measure.get(1, 1)).hasArticulation(Articulation.ACCENT));
		assertTrue(((Note) measure.get(1, 2)).hasArticulation(Articulation.TENUTO));
		assertTrue(((Note) measure.get(1, 3)).hasArticulation(Articulation.FERMATA));
	}

	@Test
	public void testReadingIncorrectXmlFile() {
		MusicXmlReader reader = new MusicXmlReaderDom(true);
		try {
			Score score = reader.readScore(Paths.get(MUSICXML_FILE_PATH + "singleCinvalid.xml"));
			fail("No exception was thrown when trying to read incorrectly formatted XML file");
		} catch (IOException e) {

		}

	}
}
