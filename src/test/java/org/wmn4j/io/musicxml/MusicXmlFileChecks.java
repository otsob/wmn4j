/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Clefs;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.Marking;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.MultiStaffPart;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignature;
import org.wmn4j.notation.TimeSignatures;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * A class containing full checks for MusicXML files for testing purposes.
 */
class MusicXmlFileChecks {

	private MusicXmlFileChecks() {
	}

	/*
	 * Expects the contents of the file "singleC.xml"
	 */
	static void assertSingleNoteScoreReadCorrectly(Score score) {
		assertEquals("Single C", score.getAttribute(Score.Attribute.MOVEMENT_TITLE).get());
		assertEquals(1, score.getPartCount());

		final Part part = score.getPart(0);
		assertTrue(part instanceof SingleStaffPart);
		final SingleStaffPart spart = (SingleStaffPart) part;
		final Staff staff = spart.getStaff();
		assertEquals("Part1", part.getName().get());
		assertEquals(1, staff.getMeasureCount());

		final Measure measure = staff.getMeasure(1);
		assertEquals(1, measure.getNumber());
		assertEquals(1, measure.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measure.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measure.getKeySignature());
		assertEquals(Barline.SINGLE, measure.getRightBarline());
		assertEquals(Clefs.G, measure.getClef());

		assertEquals(1, measure.getVoiceSize(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.WHOLE), measure.get(1, 0));
	}

	/*
	 * Expects the contents of the file "twoMeasures.xml"
	 */
	static void assertChordsAndMultipleVoicesReadCorrectly(Score score) {
		assertEquals(1, score.getPartCount());

		final Part part = score.getPart(0);
		assertTrue(part instanceof SingleStaffPart);
		final SingleStaffPart spart = (SingleStaffPart) part;
		final Staff staff = spart.getStaff();
		assertEquals("Part1", part.getName().get());
		assertEquals(2, staff.getMeasureCount());

		// Verify data of measure one
		final Measure measureOne = staff.getMeasure(1);
		assertEquals(1, measureOne.getNumber());
		assertEquals(1, measureOne.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measureOne.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measureOne.getKeySignature());
		assertEquals(Barline.SINGLE, measureOne.getRightBarline());
		assertEquals(Clefs.G, measureOne.getClef());

		// Verify notes of measure one
		assertEquals(8, measureOne.getVoiceSize(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), measureOne.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH), measureOne.get(1, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH), measureOne.get(1, 2));
		assertEquals(Rest.of(Durations.EIGHTH), measureOne.get(1, 3));
		final Chord cMajor = Chord.of(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH),
				Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH),
				Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHTH));
		assertEquals(cMajor, measureOne.get(1, 4));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH_TRIPLET), measureOne.get(1, 5));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH_TRIPLET), measureOne.get(1, 6));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH_TRIPLET), measureOne.get(1, 7));

		// Verify data of measure two
		final Measure measureTwo = staff.getMeasure(2);
		assertEquals(2, measureTwo.getNumber());
		assertEquals(2, measureTwo.getVoiceCount());
		assertEquals(TimeSignatures.FOUR_FOUR, measureTwo.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, measureTwo.getKeySignature());
		assertEquals(Barline.FINAL, measureTwo.getRightBarline());
		assertEquals(Clefs.G, measureTwo.getClef());

		// Verify notes of measure two
		assertEquals(2, measureTwo.getVoiceSize(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.HALF), measureTwo.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.HALF), measureTwo.get(1, 1));

		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), measureTwo.get(2, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), measureTwo.get(2, 1));
		assertEquals(Rest.of(Durations.QUARTER), measureTwo.get(2, 2));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER), measureTwo.get(2, 3));
	}

	/*
	 * Expects the contents of the file "twoPartsAndMeasures.xml"
	 */
	static void assertScoreWithMultiplePartsReadCorrectly(Score score) {
		assertEquals("Multistaff test file", score.getAttribute(Score.Attribute.MOVEMENT_TITLE).get());
		assertEquals("TestFile Composer", score.getAttribute(Score.Attribute.COMPOSER).get());
		assertEquals(2, score.getPartCount());

		final SingleStaffPart partOne = (SingleStaffPart) score.getPart(0);
		final Staff staffOne = partOne.getStaff();
		assertEquals("Part1", partOne.getName().get());
		assertEquals(2, staffOne.getMeasureCount());

		// Verify data of measure one of staff one
		final Measure staffOneMeasureOne = staffOne.getMeasure(1);
		assertEquals(1, staffOneMeasureOne.getNumber());
		assertEquals(1, staffOneMeasureOne.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureOne.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffOneMeasureOne.getKeySignature());
		assertEquals(Barline.SINGLE, staffOneMeasureOne.getRightBarline());
		assertEquals(Clefs.G, staffOneMeasureOne.getClef());

		// Verify contents of measure one of staff one
		assertEquals(1, staffOneMeasureOne.getVoiceCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF), staffOneMeasureOne.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER), staffOneMeasureOne.get(1, 1));

		// Verify data of measure two of staff one
		final Measure staffOneMeasureTwo = staffOne.getMeasure(2);
		assertEquals(2, staffOneMeasureTwo.getNumber());
		assertEquals(1, staffOneMeasureTwo.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureTwo.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffOneMeasureTwo.getKeySignature());
		assertEquals(Barline.FINAL, staffOneMeasureTwo.getRightBarline());
		assertEquals(Clefs.G, staffOneMeasureTwo.getClef());

		// Verify contents of measure one of staff one
		assertEquals(1, staffOneMeasureTwo.getVoiceCount());
		assertEquals(Rest.of(Durations.QUARTER), staffOneMeasureTwo.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF), staffOneMeasureTwo.get(1, 1));

		final SingleStaffPart partTwo = (SingleStaffPart) score.getPart(1);
		final Staff staffTwo = partTwo.getStaff();
		assertEquals("Part2", partTwo.getName().get());
		assertEquals(2, staffTwo.getMeasureCount());

		// Verify data of measure one of staff two
		final Measure staffTwoMeasureOne = staffTwo.getMeasure(1);
		assertEquals(1, staffTwoMeasureOne.getNumber());
		assertEquals(1, staffTwoMeasureOne.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureOne.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffTwoMeasureOne.getKeySignature());
		assertEquals(Barline.SINGLE, staffTwoMeasureOne.getRightBarline());
		assertEquals(Clefs.F, staffTwoMeasureOne.getClef());

		// Verify contents of measure one of staff two
		assertEquals(1, staffTwoMeasureOne.getVoiceCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), staffTwoMeasureOne.get(1, 0));

		// Verify data of measure two of staff two
		final Measure staffTwoMeasureTwo = staffTwo.getMeasure(2);
		assertEquals(2, staffTwoMeasureTwo.getNumber());
		assertEquals(1, staffTwoMeasureTwo.getVoiceCount());
		assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureTwo.getTimeSignature());
		assertEquals(KeySignatures.GMAJ_EMIN, staffTwoMeasureTwo.getKeySignature());
		assertEquals(Barline.FINAL, staffTwoMeasureTwo.getRightBarline());
		assertEquals(Clefs.F, staffTwoMeasureTwo.getClef());

		// Verify contents of measure two of staff two
		assertEquals(1, staffTwoMeasureTwo.getVoiceCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), staffTwoMeasureTwo.get(1, 0));
	}

	/*
	 * Expects the contents of the file "barlines.xml"
	 */
	static void assertBarlinesReadCorrectly(Score score) {

		assertEquals(1, score.getPartCount());
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);

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

	/*
	 * Expects the contents of the file "clefs.xml"
	 */
	static void assertClefsReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);

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

	/*
	 * Expects the contents of the file "multiStaffClefs.xml"
	 */
	static void assertMultiStaffClefsReadCorrectlyToScore(Score score) {
		final MultiStaffPart part = (MultiStaffPart) score.getPart(0);
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

	/*
	 * Expects the contents of the file "keysigs.xml"
	 */
	static void assertKeySignaturesReadToScoreCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);

		assertEquals(KeySignatures.CMAJ_AMIN, part.getMeasure(1).getKeySignature());
		assertEquals(KeySignatures.GMAJ_EMIN, part.getMeasure(2).getKeySignature());
		assertEquals(KeySignatures.AFLATMAJ_FMIN, part.getMeasure(3).getKeySignature());
	}

	/*
	 * Expects the contents of the file "multistaff.xml"
	 */
	static void assertMultiStaffPartReadCorrectly(Score score) {
		assertEquals(2, score.getPartCount());
		MultiStaffPart multiStaff = null;
		SingleStaffPart singleStaff = null;
		for (Part part : score) {
			if (part.getName().get().equals("MultiStaff")) {
				multiStaff = (MultiStaffPart) part;
			}
			if (part.getName().get().equals("SingleStaff")) {
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
			final int voiceNumber = measure.getVoiceNumbers().get(0);
			assertEquals(1, measure.getVoiceSize(voiceNumber));
			assertTrue(measure.get(voiceNumber, 0).equals(expectedNote));
			++measureCount;
		}

		assertEquals(6, measureCount, "Incorrect number of measures in multistaff part");
	}

	/*
	 * Expects the contents of the file "timesigs.xml"
	 */
	static void assertTimeSignaturesReadCorrectly(Score score) {
		assertEquals(1, score.getPartCount());
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);
		assertEquals(TimeSignature.of(2, 2), part.getMeasure(1).getTimeSignature());
		assertEquals(TimeSignature.of(3, 4), part.getMeasure(2).getTimeSignature());
		assertEquals(TimeSignature.of(6, 8), part.getMeasure(3).getTimeSignature());
		assertEquals(TimeSignature.of(15, 16), part.getMeasure(4).getTimeSignature());
	}

	/*
	 * Expects the contents of the file "scoreIteratorTesting.xml"
	 */
	static void assertTimeSignatureChangeReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);
		final Durational n = part.getMeasure(2).get(1, 0);
		assertEquals(Durations.EIGHTH, n.getDuration());
	}

	/*
	 * Expects the contents of the file "tieTesting.xml"
	 */
	static void assertTiedNotesReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);

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

	/*
	 * Expects the contents of the file "articulations.xml"
	 */
	static void assertScoreWithArticulationsReadCorrectly(Score score) {
		final Measure measureOne = score.getPart(0).getMeasure(SingleStaffPart.STAFF_NUMBER, 1);
		assertTrue(((Note) measureOne.get(1, 0)).hasArticulation(Articulation.STACCATO));
		assertTrue(((Note) measureOne.get(1, 1)).hasArticulation(Articulation.ACCENT));
		assertTrue(((Note) measureOne.get(1, 2)).hasArticulation(Articulation.TENUTO));
		assertTrue(((Note) measureOne.get(1, 3)).hasArticulation(Articulation.FERMATA));

		final Measure measureTwo = score.getPart(0).getMeasure(SingleStaffPart.STAFF_NUMBER, 2);
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

	/*
	 * Expects the content of "articulationsOnMultipleStaves.xml".
	 */
	static void assertArticulationsReadCorrectlyFromMultipleStaves(Score score) {
		MultiStaffPart part = (MultiStaffPart) score.getPart(0);
		final Staff topStaff = part.getStaff(1);
		final Staff bottomStaff = part.getStaff(2);

		final Measure topStaffMeasure = topStaff.getMeasure(1);

		assertTrue(((Note) topStaffMeasure.get(1, 0)).hasArticulation(Articulation.FERMATA));
		assertFalse(((Note) topStaffMeasure.get(1, 1)).hasArticulations());
		assertTrue(((Note) topStaffMeasure.get(1, 2)).hasArticulation(Articulation.ACCENT));
		assertTrue(((Note) topStaffMeasure.get(1, 2)).hasArticulation(Articulation.TENUTO));
		assertFalse(((Note) topStaffMeasure.get(1, 3)).hasArticulations());

		final Measure bottomStaffMeasure = bottomStaff.getMeasure(1);
		final int bottomStaffSingleVoiceNumber = bottomStaffMeasure.getVoiceNumbers().get(0);

		assertTrue(((Note) bottomStaffMeasure.get(bottomStaffSingleVoiceNumber, 0))
				.hasArticulation(Articulation.STACCATO));
		assertTrue(
				((Note) bottomStaffMeasure.get(bottomStaffSingleVoiceNumber, 1)).hasArticulation(Articulation.TENUTO));
		assertTrue(
				((Note) bottomStaffMeasure.get(bottomStaffSingleVoiceNumber, 2)).hasArticulation(Articulation.ACCENT));
		assertTrue(((Note) bottomStaffMeasure.get(bottomStaffSingleVoiceNumber, 3))
				.hasArticulation(Articulation.STACCATO));
		assertTrue(
				((Note) bottomStaffMeasure.get(bottomStaffSingleVoiceNumber, 3)).hasArticulation(Articulation.TENUTO));
		assertTrue(
				((Note) bottomStaffMeasure.get(bottomStaffSingleVoiceNumber, 3)).hasArticulation(Articulation.ACCENT));
		assertTrue(
				((Chord) bottomStaffMeasure.get(bottomStaffSingleVoiceNumber, 4)).hasArticulation(Articulation.TENUTO));
	}

	/*
	 * Expects the contents of "pickup_measure_test.xml".
	 */
	static void assertPickupMeasureReadCorrectly(Score score) {
		assertEquals(1, score.getPartCount());
		Part part = score.getPart(0);
		assertFalse(part.isMultiStaff());
		assertEquals(4, part.getMeasureCount());
		assertEquals(3, part.getFullMeasureCount());

		assertTrue(part instanceof SingleStaffPart);
		Staff staff = ((SingleStaffPart) part).getStaff();
		assertTrue(staff.hasPickupMeasure());
		assertEquals(4, staff.getMeasureCount());
		assertEquals(3, staff.getFullMeasureCount());

		Measure pickupMeasure = staff.getMeasure(0);
		assertTrue(pickupMeasure.isPickup());
		assertEquals(0, pickupMeasure.getNumber());
	}

	/*
	 * Expectes the contents of "attribute_reading_test.xml".
	 */
	static void assertScoreHasExpectedAttributes(Score score) {
		assertEquals("Composition title", score.getTitle().get());
		assertEquals("Composer name", score.getAttribute(Score.Attribute.COMPOSER).get());
		assertEquals("Movement title", score.getAttribute(Score.Attribute.MOVEMENT_TITLE).get());
		assertEquals("Arranger name", score.getAttribute(Score.Attribute.ARRANGER).get());

		assertEquals(2, score.getPartCount());
		Part part1 = score.getPart(0);
		assertEquals("Part name 1", part1.getName().get());
		assertEquals("Short part name 1", part1.getAttribute(Part.Attribute.ABBREVIATED_NAME).get());

		Part part2 = score.getPart(1);
		assertEquals("Part name 2", part2.getName().get());
		assertEquals("Short part name 2", part2.getAttribute(Part.Attribute.ABBREVIATED_NAME).get());
	}

	/*
	 * Expects the contents of "single_staff_single_voice_marking_test.xml".
	 */
	static void assertMarkingsReadCorrectlyFromSingleVoiceToScore(Score score) {
		final Part part = score.getPart(0);
		final Measure firstMeasure = part.getMeasure(1, 1);
		final int voiceNumber = firstMeasure.getVoiceNumbers().iterator().next();

		final Note firstNote = (Note) firstMeasure.get(voiceNumber, 0);
		final Note secondNote = (Note) firstMeasure.get(voiceNumber, 1);

		assertTrue(firstNote.hasMarking(Marking.Type.SLUR));
		assertTrue(firstNote.begins(Marking.Type.SLUR));
		assertEquals(1, firstNote.getMarkings().size());
		Marking slurBetweenSecondAndFirst = firstNote.getMarkings().stream().findFirst().orElseThrow();
		assertEquals(secondNote,
				firstNote.getMarkingConnection(slurBetweenSecondAndFirst).get().getFollowingNote().get());

		assertTrue(secondNote.hasMarkings());
		assertTrue(secondNote.ends(Marking.Type.SLUR));
		assertEquals(1, secondNote.getMarkings().size());

		final Note thirdNote = (Note) firstMeasure.get(voiceNumber, 2);
		final Note fourthNote = (Note) firstMeasure.get(voiceNumber, 3);

		assertTrue(thirdNote.hasMarking(Marking.Type.GLISSANDO));
		assertTrue(thirdNote.begins(Marking.Type.GLISSANDO));
		assertEquals(1, thirdNote.getMarkings().size());
		Marking glissandoBetweenThirdAndFourth = thirdNote.getMarkings().stream().findFirst().orElseThrow();
		assertEquals(fourthNote,
				thirdNote.getMarkingConnection(glissandoBetweenThirdAndFourth).get().getFollowingNote().get());

		assertTrue(fourthNote.hasMarkings());
		assertTrue(fourthNote.ends(Marking.Type.GLISSANDO));
		assertEquals(1, fourthNote.getMarkings().size());

		final Note fifthNote = (Note) firstMeasure.get(voiceNumber, 4);
		assertEquals(1, fifthNote.getMarkings().size());
		assertTrue(fifthNote.begins(Marking.Type.SLUR));

		final Note sixthNote = (Note) firstMeasure.get(voiceNumber, 5);
		assertEquals(1, sixthNote.getMarkings().size());
		assertTrue(sixthNote.hasMarking(Marking.Type.SLUR));
		assertFalse(sixthNote.begins(Marking.Type.SLUR));
		assertFalse(sixthNote.ends(Marking.Type.SLUR));

		final Note seventhNote = (Note) firstMeasure.get(voiceNumber, 6);
		assertEquals(1, seventhNote.getMarkings().size());
		assertTrue(seventhNote.ends(Marking.Type.SLUR));

		final Marking slurFromFifthToSeventhNote = fifthNote.getMarkings().stream().findAny().orElseThrow();
		final List<Note> notesInSlurFromFifthToSeventhNote = slurFromFifthToSeventhNote
				.getAffectedStartingFrom(fifthNote);
		assertEquals(3, notesInSlurFromFifthToSeventhNote.size());
		assertEquals(fifthNote, notesInSlurFromFifthToSeventhNote.get(0));
		assertEquals(sixthNote, notesInSlurFromFifthToSeventhNote.get(1));
		assertEquals(seventhNote, notesInSlurFromFifthToSeventhNote.get(2));

		final Measure secondMeasure = part.getMeasure(1, 2);

		final Note eightNote = (Note) secondMeasure.get(voiceNumber, 0);
		assertEquals(1, eightNote.getMarkings().size());
		assertTrue(eightNote.begins(Marking.Type.SLUR));

		final Note ninthNote = (Note) secondMeasure.get(voiceNumber, 1);
		assertEquals(1, ninthNote.getMarkings().size());
		assertTrue(ninthNote.hasMarking(Marking.Type.SLUR));
		assertFalse(ninthNote.begins(Marking.Type.SLUR));
		assertFalse(ninthNote.ends(Marking.Type.SLUR));

		assertTrue(secondMeasure.get(voiceNumber, 2) instanceof Rest);

		final Note tenthNote = (Note) secondMeasure.get(voiceNumber, 3);
		assertEquals(1, tenthNote.getMarkings().size());
		assertTrue(tenthNote.ends(Marking.Type.SLUR));

		final Marking slurFromEightToTenthNote = eightNote.getMarkings().stream().findAny().orElseThrow();
		final List<Note> notesInSlurFromEightToTenthNote = slurFromEightToTenthNote.getAffectedStartingFrom(eightNote);
		assertEquals(3, notesInSlurFromEightToTenthNote.size());
		assertEquals(eightNote, notesInSlurFromEightToTenthNote.get(0));
		assertEquals(ninthNote, notesInSlurFromEightToTenthNote.get(1));
		assertEquals(tenthNote, notesInSlurFromEightToTenthNote.get(2));

		final Note eleventhNote = (Note) secondMeasure.get(voiceNumber, 4);
		assertEquals(2, eleventhNote.getMarkings().size());
		assertTrue(eleventhNote.begins(Marking.Type.GLISSANDO));
		assertTrue(eleventhNote.begins(Marking.Type.SLUR));

		final Note twelthNote = (Note) secondMeasure.get(voiceNumber, 5);
		assertEquals(2, twelthNote.getMarkings().size());
		assertTrue(twelthNote.ends(Marking.Type.GLISSANDO));
		assertTrue(twelthNote.ends(Marking.Type.SLUR));
	}

	/*
	 * Expects the content of "multi_staff_multi_voice_marking_test.xml".
	 */
	static void assertMarkingsReadCorrectlyFromMultipleStavesWithMultipleVoices(Score score) {
		assertEquals(1, score.getPartCount(), "Score is expected to have single part");
		assertTrue(score.getPart(0) instanceof MultiStaffPart,
				"The only part in the score is expected to have multiple staves");

		MultiStaffPart part = (MultiStaffPart) score.getPart(0);
		final Staff topStaff = part.getStaff(1);
		final Staff bottomStaff = part.getStaff(2);

		final Measure topStaffFirstMeasure = topStaff.getMeasure(1);
		final int topStaffSingleVoiceNumber = topStaffFirstMeasure.getVoiceNumbers().get(0);

		// Check top staff of first measure
		final Note firstNoteInTopStaff = (Note) topStaffFirstMeasure.get(topStaffSingleVoiceNumber, 0);
		assertEquals(1, firstNoteInTopStaff.getMarkings().size());
		assertTrue(firstNoteInTopStaff.begins(Marking.Type.SLUR));

		final Note secondNoteInTopStaff = (Note) topStaffFirstMeasure.get(topStaffSingleVoiceNumber, 1);
		assertEquals(2, secondNoteInTopStaff.getMarkings().size());
		assertTrue(secondNoteInTopStaff.begins(Marking.Type.SLUR));
		assertFalse(secondNoteInTopStaff.ends(Marking.Type.SLUR));

		final Note thirdNoteInTopStaff = (Note) topStaffFirstMeasure.get(topStaffSingleVoiceNumber, 2);
		assertEquals(2, thirdNoteInTopStaff.getMarkings().size());
		assertTrue(thirdNoteInTopStaff.ends(Marking.Type.SLUR));
		assertFalse(thirdNoteInTopStaff.begins(Marking.Type.SLUR));

		final Note fourthNoteInTopStaff = (Note) topStaffFirstMeasure.get(topStaffSingleVoiceNumber, 3);
		assertEquals(1, fourthNoteInTopStaff.getMarkings().size());
		assertTrue(fourthNoteInTopStaff.ends(Marking.Type.SLUR));

		final Marking slurFromFirstToThirdNote = firstNoteInTopStaff.getMarkings().stream().findFirst().orElseThrow();
		List<Note> notesInSlurFromFirstToThirdNote = slurFromFirstToThirdNote
				.getAffectedStartingFrom(firstNoteInTopStaff);
		assertEquals(3, notesInSlurFromFirstToThirdNote.size());
		assertEquals(firstNoteInTopStaff, notesInSlurFromFirstToThirdNote.get(0));
		assertEquals(secondNoteInTopStaff, notesInSlurFromFirstToThirdNote.get(1));
		assertEquals(thirdNoteInTopStaff, notesInSlurFromFirstToThirdNote.get(2));

		final Marking slurFromSecondToFourthNote = secondNoteInTopStaff.getMarkings().stream()
				.filter(marking -> secondNoteInTopStaff.getMarkingConnection(marking).get().isBeginning())
				.findAny()
				.orElseThrow();

		List<Note> notesInSlurFromSecondToFourthNote = slurFromSecondToFourthNote
				.getAffectedStartingFrom(secondNoteInTopStaff);
		assertEquals(3, notesInSlurFromSecondToFourthNote.size());
		assertEquals(secondNoteInTopStaff, notesInSlurFromSecondToFourthNote.get(0));
		assertEquals(thirdNoteInTopStaff, notesInSlurFromSecondToFourthNote.get(1));
		assertEquals(fourthNoteInTopStaff, notesInSlurFromSecondToFourthNote.get(2));

		// Check bottom staff of first measure
		final Measure bottomStaffFirstMeasure = bottomStaff.getMeasure(1);
		final int bottomStaffSingleVoiceNumber = bottomStaffFirstMeasure.getVoiceNumbers().get(0);

		assertTrue(bottomStaffFirstMeasure.get(bottomStaffSingleVoiceNumber, 0).isRest());

		final Note firstNoteInBottomStaff = (Note) bottomStaffFirstMeasure.get(bottomStaffSingleVoiceNumber, 1);
		assertEquals(1, firstNoteInBottomStaff.getMarkings().size());
		assertTrue(firstNoteInBottomStaff.begins(Marking.Type.SLUR));

		final Note secondNoteInBottomStaff = (Note) bottomStaffFirstMeasure.get(bottomStaffSingleVoiceNumber, 2);
		assertEquals(1, secondNoteInBottomStaff.getMarkings().size());
		assertTrue(secondNoteInBottomStaff.ends(Marking.Type.SLUR));

		final Marking bottomStaffFirstMeasureSlur = firstNoteInBottomStaff.getMarkings().stream().findFirst()
				.orElseThrow();
		final List<Note> bottomStaffFirstMeasureSlurNotes = bottomStaffFirstMeasureSlur
				.getAffectedStartingFrom(firstNoteInBottomStaff);
		assertEquals(2, bottomStaffFirstMeasureSlurNotes.size());
		assertEquals(firstNoteInBottomStaff, bottomStaffFirstMeasureSlurNotes.get(0));
		assertEquals(secondNoteInBottomStaff, bottomStaffFirstMeasureSlurNotes.get(1));

		assertTrue(bottomStaffFirstMeasure.get(bottomStaffSingleVoiceNumber, 3).isRest());

		final Measure topStaffSecondMeasure = topStaff.getMeasure(2);

		final List<Integer> topStaffVoiceNumbers = topStaffSecondMeasure.getVoiceNumbers();
		assertEquals(2, topStaffVoiceNumbers.size());
		final int topStaffTopVoice = topStaffVoiceNumbers.get(0);
		final int topStaffBottomVoice = topStaffVoiceNumbers.get(1);

		// Check top voice in second measure top staff
		Note fifthNoteInTopStaffTopVoice = (Note) topStaffSecondMeasure.get(topStaffTopVoice, 0);
		assertEquals(1, fifthNoteInTopStaffTopVoice.getMarkings().size());
		assertTrue(fifthNoteInTopStaffTopVoice.begins(Marking.Type.SLUR));

		Note sixthNoteInTopStaffTopVoice = (Note) topStaffSecondMeasure.get(topStaffTopVoice, 1);
		assertEquals(2, sixthNoteInTopStaffTopVoice.getMarkings().size());
		assertTrue(sixthNoteInTopStaffTopVoice.hasMarking(Marking.Type.SLUR));
		assertFalse(sixthNoteInTopStaffTopVoice.begins(Marking.Type.SLUR));
		assertFalse(sixthNoteInTopStaffTopVoice.ends(Marking.Type.SLUR));
		assertTrue(sixthNoteInTopStaffTopVoice.begins(Marking.Type.GLISSANDO));

		Note seventhNoteInTopStaffTopVoice = (Note) topStaffSecondMeasure.get(topStaffTopVoice, 2);
		assertEquals(2, seventhNoteInTopStaffTopVoice.getMarkings().size());
		assertTrue(seventhNoteInTopStaffTopVoice.ends(Marking.Type.SLUR));
		assertTrue(seventhNoteInTopStaffTopVoice.ends(Marking.Type.GLISSANDO));

		final Marking slurBetweenTopVoiceNotes = fifthNoteInTopStaffTopVoice.getMarkings().stream().findFirst()
				.orElseThrow();
		final List<Note> notesInSlurBetweenTopVoiceNotes = slurBetweenTopVoiceNotes
				.getAffectedStartingFrom(fifthNoteInTopStaffTopVoice);
		assertEquals(3, notesInSlurBetweenTopVoiceNotes.size());
		assertEquals(fifthNoteInTopStaffTopVoice, notesInSlurBetweenTopVoiceNotes.get(0));
		assertEquals(sixthNoteInTopStaffTopVoice, notesInSlurBetweenTopVoiceNotes.get(1));
		assertEquals(seventhNoteInTopStaffTopVoice, notesInSlurBetweenTopVoiceNotes.get(2));

		final Marking glissandoInTopVoice = sixthNoteInTopStaffTopVoice.getMarkings().stream()
				.filter(marking -> sixthNoteInTopStaffTopVoice.getMarkingConnection(marking).get().isBeginning())
				.findAny()
				.orElseThrow();

		final List<Note> notesInTopVoiceGlissando = glissandoInTopVoice
				.getAffectedStartingFrom(sixthNoteInTopStaffTopVoice);
		assertEquals(2, notesInTopVoiceGlissando.size());
		assertEquals(sixthNoteInTopStaffTopVoice, notesInTopVoiceGlissando.get(0));
		assertEquals(seventhNoteInTopStaffTopVoice, notesInTopVoiceGlissando.get(1));

		// Check notes in lower voice of top staff measure 2
		Note fifthNoteInTopStaffBottomVoice = (Note) topStaffSecondMeasure.get(topStaffBottomVoice, 0);
		assertEquals(2, fifthNoteInTopStaffBottomVoice.getMarkings().size());
		assertTrue(fifthNoteInTopStaffBottomVoice.begins(Marking.Type.SLUR));
		assertTrue(fifthNoteInTopStaffBottomVoice.begins(Marking.Type.GLISSANDO));

		Note sixthNoteInTopStaffBottomVoice = (Note) topStaffSecondMeasure.get(topStaffBottomVoice, 1);
		assertEquals(2, sixthNoteInTopStaffBottomVoice.getMarkings().size());
		assertTrue(sixthNoteInTopStaffBottomVoice.ends(Marking.Type.GLISSANDO));
		assertTrue(sixthNoteInTopStaffBottomVoice.hasMarking(Marking.Type.SLUR));
		assertFalse(sixthNoteInTopStaffBottomVoice.begins(Marking.Type.SLUR));
		assertFalse(sixthNoteInTopStaffBottomVoice.ends(Marking.Type.SLUR));

		Note seventhNoteInTopStaffBottomVoice = (Note) topStaffSecondMeasure.get(topStaffBottomVoice, 2);
		assertEquals(1, seventhNoteInTopStaffBottomVoice.getMarkings().size());
		assertTrue(seventhNoteInTopStaffBottomVoice.ends(Marking.Type.SLUR));

		final Marking slurBetweenBottomVoiceNotes = fifthNoteInTopStaffBottomVoice.getMarkings().stream()
				.filter(marking -> marking.getType().equals(Marking.Type.SLUR))
				.findAny()
				.orElseThrow();

		final List<Note> notesInSlurBetweenBottomVoiceNotes = slurBetweenBottomVoiceNotes
				.getAffectedStartingFrom(fifthNoteInTopStaffBottomVoice);
		assertEquals(3, notesInSlurBetweenBottomVoiceNotes.size());
		assertEquals(fifthNoteInTopStaffBottomVoice, notesInSlurBetweenBottomVoiceNotes.get(0));
		assertEquals(sixthNoteInTopStaffBottomVoice, notesInSlurBetweenBottomVoiceNotes.get(1));
		assertEquals(seventhNoteInTopStaffBottomVoice, notesInSlurBetweenBottomVoiceNotes.get(2));

		final Marking glissandoInBottomVoice = fifthNoteInTopStaffBottomVoice.getMarkings().stream()
				.filter(marking -> marking.getType().equals(Marking.Type.GLISSANDO))
				.findAny()
				.orElseThrow();

		final List<Note> notesInBottomVoiceGlissando = glissandoInBottomVoice
				.getAffectedStartingFrom(fifthNoteInTopStaffBottomVoice);
		assertEquals(2, notesInTopVoiceGlissando.size());
		assertEquals(fifthNoteInTopStaffBottomVoice, notesInBottomVoiceGlissando.get(0));
		assertEquals(sixthNoteInTopStaffBottomVoice, notesInBottomVoiceGlissando.get(1));
	}

	/*
	 * Expects the contents of "clef_change_where_note_in_another_voice_carries_over.xml".
	 */
	static void assertClefChangeInCorrectPlaceWhenNoteCarriesOverClefChange(Score score) {
		final Measure measure = score.getPart(0).getMeasure(1, 1);
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.QUARTER), measure.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.D, 0, 5), Durations.HALF), measure.get(1, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, 0, 5), Durations.QUARTER), measure.get(1, 2));

		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER), measure.get(2, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.QUARTER), measure.get(2, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 3), Durations.QUARTER), measure.get(2, 2));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 3), Durations.QUARTER), measure.get(2, 3));

		assertEquals(Clefs.G, measure.getClef());
		final Map<Duration, Clef> clefChanges = measure.getClefChanges();

		assertEquals(1, clefChanges.size());
		assertTrue(clefChanges.containsKey(Durations.HALF));
		assertEquals(Clefs.ALTO, clefChanges.get(Durations.HALF));
	}
}
