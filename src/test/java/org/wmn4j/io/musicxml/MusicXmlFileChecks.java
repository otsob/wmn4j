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
import org.wmn4j.notation.GraceNote;
import org.wmn4j.notation.GraceNoteChord;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.MultiStaffPart;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Ornament;
import org.wmn4j.notation.Ornamental;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignature;
import org.wmn4j.notation.TimeSignatures;
import org.wmn4j.notation.access.Offset;
import org.wmn4j.notation.directions.Direction;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	 * Expects the contents of the file "singleC.musicxml"
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
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.WHOLE), measure.get(1, 0));
	}

	/*
	 * Expects the contents of the file "twoMeasures.musicxml"
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
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				measureOne.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
				measureOne.get(1, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
				measureOne.get(1, 2));
		assertEquals(Rest.of(Durations.EIGHTH), measureOne.get(1, 3));
		final Chord cMajor = Chord.of(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
				Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH),
				Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));
		assertEquals(cMajor, measureOne.get(1, 4));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH_TRIPLET),
				measureOne.get(1, 5));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH_TRIPLET),
				measureOne.get(1, 6));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH_TRIPLET),
				measureOne.get(1, 7));

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
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.HALF),
				measureTwo.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.HALF),
				measureTwo.get(1, 1));

		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				measureTwo.get(2, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				measureTwo.get(2, 1));
		assertEquals(Rest.of(Durations.QUARTER), measureTwo.get(2, 2));
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				measureTwo.get(2, 3));
	}

	/*
	 * Expects the contents of the file "twoPartsAndMeasures.musicxml"
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
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.HALF),
				staffOneMeasureOne.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				staffOneMeasureOne.get(1, 1));

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
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.HALF),
				staffOneMeasureTwo.get(1, 1));

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
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 3), Durations.HALF.addDot()),
				staffTwoMeasureOne.get(1, 0));

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
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 3), Durations.HALF.addDot()),
				staffTwoMeasureTwo.get(1, 0));
	}

	/*
	 * Expects the contents of the file "barlines.musicxml"
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
	 * Expects the contents of the file "clefs.musicxml"
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
		final List<Offset<Clef>> clefChanges = part.getMeasure(4).getClefChanges();
		assertEquals(2, clefChanges.size());
		assertEquals(new Offset<>(Clefs.F, Durations.QUARTER), clefChanges.get(0));
		assertEquals(new Offset<>(Clefs.PERCUSSION, Durations.WHOLE), clefChanges.get(1));

		assertEquals(Clefs.PERCUSSION, part.getMeasure(5).getClef());
		assertTrue(part.getMeasure(5).containsClefChanges());
		assertEquals(2, part.getMeasure(5).getClefChanges().size());
		assertEquals(new Offset<>(Clefs.G, Durations.QUARTER), part.getMeasure(5).getClefChanges().get(0));
		assertEquals(new Offset<>(Clefs.F, Durations.HALF.addDot()), part.getMeasure(5).getClefChanges().get(1));
	}

	/*
	 * Expects the contents of the file "multiStaffClefs.musicxml"
	 */
	static void assertMultiStaffClefsReadCorrectlyToScore(Score score) {
		final MultiStaffPart part = (MultiStaffPart) score.getPart(0);
		final Staff upper = part.getStaff(1);
		final Staff lower = part.getStaff(2);

		// Check upper staff
		assertEquals(Clefs.G, upper.getMeasure(1).getClef(), "Incorrect clef measure 1 upper staff beginning");
		assertTrue(upper.getMeasure(1).containsClefChanges(), "Upper staff measure 1 does not contain a clef change");
		assertEquals(1, upper.getMeasure(1).getClefChanges().size(), "Incorrect number of clef changes");

		assertEquals(new Offset<>(Clefs.ALTO, Durations.HALF.addDot()),
				upper.getMeasure(1).getClefChanges().get(0), "Incorrect clef change");

		assertEquals(Clefs.ALTO, upper.getMeasure(2).getClef(), "Incorrect clef measure 2 upper staff.");
		assertFalse(upper.getMeasure(2).containsClefChanges(), "Upper staff measure 2 contains a clef change");

		// Check lower staff
		assertEquals(Clefs.F, lower.getMeasure(1).getClef(), "Incorrect clef in measure 1 lower staff");
		assertTrue(lower.getMeasure(1).containsClefChanges(), "Lower staff measure 1 does not contain a clef change");
		final List<Offset<Clef>> clefChanges = lower.getMeasure(1).getClefChanges();
		assertEquals(1, clefChanges.size(), "Incorrect number of clef changes");
		assertEquals(new Offset<>(Clefs.G, Durations.HALF.add(Durations.SIXTEENTH.multiply(3))), clefChanges.get(0),
				"Incorrect clef change");

		assertEquals(Clefs.G, lower.getMeasure(2).getClef(), "Incorrect clef measure 2 of lower staff");
		assertFalse(lower.getMeasure(2).containsClefChanges(), "Lower staff measure 2 contians clef changes");
	}

	/*
	 * Expects the contents of the file "keysigs.musicxml"
	 */
	static void assertKeySignaturesReadToScoreCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);

		assertEquals(KeySignatures.CMAJ_AMIN, part.getMeasure(1).getKeySignature());
		assertEquals(KeySignatures.GMAJ_EMIN, part.getMeasure(2).getKeySignature());
		assertEquals(KeySignatures.AFLATMAJ_FMIN, part.getMeasure(3).getKeySignature());
	}

	/*
	 * Expects the contents of the file "multistaff.musicxml"
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

		assertFalse(singleStaff.hasPickupMeasure());
		assertEquals(3, singleStaff.getMeasureCount());

		assertFalse(multiStaff.hasPickupMeasure());
		assertEquals(3, multiStaff.getMeasureCount());

		assertEquals(2, multiStaff.getStaffCount());

		int measureCount = 0;
		final Note expectedNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.WHOLE);

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
	 * Expects the contents of the file "timesigs.musicxml"
	 */
	static void assertTimeSignaturesReadCorrectly(Score score) {
		assertEquals(1, score.getPartCount());
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);
		assertEquals(TimeSignature.of(2, 2), part.getMeasure(1).getTimeSignature());
		assertEquals(TimeSignature.of(3, 4), part.getMeasure(2).getTimeSignature());
		assertEquals(TimeSignature.of(6, 8), part.getMeasure(3).getTimeSignature());
		assertEquals(TimeSignature.of(15, 16), part.getMeasure(4).getTimeSignature());
		assertEquals(TimeSignatures.COMMON, part.getMeasure(5).getTimeSignature());
		assertEquals(TimeSignatures.CUT_TIME, part.getMeasure(6).getTimeSignature());
		assertEquals(TimeSignature.of(4, Durations.QUARTER, TimeSignature.Symbol.BEAT_DURATION_AS_DOTTED_NOTE),
				part.getMeasure(7).getTimeSignature());
		assertEquals(TimeSignature.of(4, Durations.QUARTER, TimeSignature.Symbol.BEAT_NUMBER_ONLY),
				part.getMeasure(8).getTimeSignature());
		assertEquals(TimeSignature.of(4, Durations.QUARTER, TimeSignature.Symbol.BEAT_DURATION_AS_NOTE),
				part.getMeasure(9).getTimeSignature());
	}

	/*
	 * Expects the contents of the file "scoreIteratorTesting.musicxml"
	 */
	static void assertTimeSignatureChangeReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);
		final Durational n = part.getMeasure(2).get(1, 0);
		assertEquals(Durations.EIGHTH, n.getDuration());
	}

	/*
	 * Expects the contents of the file "tieTesting.musicxml"
	 */
	static void assertTiedNotesReadCorrectly(Score score) {
		final SingleStaffPart part = (SingleStaffPart) score.getPart(0);

		final Measure firstMeasure = part.getMeasure(1);
		final Note first = (Note) firstMeasure.get(1, 0);
		assertTrue(first.isTiedToFollowing());
		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				first.getFollowingTiedNote().get().getPitch().get());

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
		assertEquals(Durations.WHOLE.multiply(2).add(Durations.QUARTER), eight.getTiedDuration());

		final Measure fourthMeasure = part.getMeasure(4);
		final Note ninth = (Note) fourthMeasure.get(1, 0);
		assertTrue(ninth.isTiedFromPrevious());
		assertTrue(ninth.isTiedToFollowing());
	}

	/*
	 * Expects the contents of the file "articulations.musicxml"
	 */
	static void assertScoreWithArticulationsReadCorrectly(Score score) {
		final Measure measureOne = score.getPart(0).getMeasure(Part.DEFAULT_STAFF_NUMBER, 1);
		assertTrue(((Note) measureOne.get(1, 0)).hasArticulation(Articulation.STACCATO));
		assertTrue(((Note) measureOne.get(1, 1)).hasArticulation(Articulation.ACCENT));
		assertTrue(((Note) measureOne.get(1, 2)).hasArticulation(Articulation.TENUTO));
		assertTrue(((Note) measureOne.get(1, 3)).hasArticulation(Articulation.FERMATA));

		final Measure measureTwo = score.getPart(0).getMeasure(Part.DEFAULT_STAFF_NUMBER, 2);
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

		final Measure measureThree = score.getPart(0).getMeasure(Part.DEFAULT_STAFF_NUMBER, 3);
		assertTrue(((Note) measureThree.get(1, 0)).hasArticulation(Articulation.BREATH_MARK));
		assertTrue(((Note) measureThree.get(1, 1)).hasArticulation(Articulation.CAESURA));
		assertTrue(((Note) measureThree.get(1, 2)).hasArticulation(Articulation.TENUTO_STACCATO));
		assertTrue(((Note) measureThree.get(1, 3)).hasArticulation(Articulation.SLIDE_OUT_UP));

		final Measure measureFour = score.getPart(0).getMeasure(Part.DEFAULT_STAFF_NUMBER, 4);
		assertTrue(((Note) measureFour.get(1, 0)).hasArticulation(Articulation.SLIDE_OUT_DOWN));
		assertTrue(((Note) measureFour.get(1, 2)).hasArticulation(Articulation.SLIDE_IN_DOWN));
		assertTrue(((Note) measureFour.get(1, 3)).hasArticulation(Articulation.SLIDE_IN_UP));

		final Measure measureFive = score.getPart(0).getMeasure(Part.DEFAULT_STAFF_NUMBER, 5);
		assertTrue(((Note) measureFive.get(1, 0)).hasArticulation(Articulation.SPICCATO));
		assertTrue(((Note) measureFive.get(1, 1)).hasArticulation(Articulation.STACCATISSIMO));
		assertTrue(((Note) measureFive.get(1, 2)).hasArticulation(Articulation.STRESS));
		assertTrue(((Note) measureFive.get(1, 3)).hasArticulation(Articulation.STRONG_ACCENT));

		final Measure measureSix = score.getPart(0).getMeasure(Part.DEFAULT_STAFF_NUMBER, 6);
		assertTrue(((Note) measureSix.get(1, 0)).hasArticulation(Articulation.UNSTRESS));
	}

	/*
	 * Expects the content of "articulationsOnMultipleStaves.musicxml".
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
	 * Expects the contents of "pickup_measure_test.musicxml".
	 */
	static void assertPickupMeasureReadCorrectly(Score score) {
		assertEquals(1, score.getPartCount());

		assertEquals(4, score.getMeasureCount());
		assertEquals(3, score.getFullMeasureCount());
		assertTrue(score.hasPickupMeasure());

		Part part = score.getPart(0);
		assertFalse(part.isMultiStaff());
		assertTrue(part.hasPickupMeasure());
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
	 * Expectes the contents of "attribute_reading_test.musicxml".
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
	 * Expects the contents of "single_staff_single_voice_notation_test.musicxml".
	 */
	static void assertNotationsReadCorrectlyFromSingleVoiceToScore(Score score) {
		final Part part = score.getPart(0);
		final Measure firstMeasure = part.getMeasure(1, 1);
		final int voiceNumber = firstMeasure.getVoiceNumbers().iterator().next();

		final Note firstNote = (Note) firstMeasure.get(voiceNumber, 0);
		final Note secondNote = (Note) firstMeasure.get(voiceNumber, 1);

		assertTrue(firstNote.hasNotation(Notation.Type.SLUR));
		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertEquals(1, firstNote.getNotations().size());
		Notation slurBetweenSecondAndFirst = firstNote.getNotations().stream().findFirst().orElseThrow();
		assertEquals(secondNote,
				firstNote.getConnection(slurBetweenSecondAndFirst).get().getFollowingNote().get());

		assertTrue(secondNote.hasNotations());
		assertTrue(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(1, secondNote.getNotations().size());

		final Note thirdNote = (Note) firstMeasure.get(voiceNumber, 2);
		final Note fourthNote = (Note) firstMeasure.get(voiceNumber, 3);

		assertTrue(thirdNote.hasNotation(Notation.Type.GLISSANDO));
		assertTrue(thirdNote.getNotations().stream()
				.anyMatch(notation -> notation.getType().equals(Notation.Type.GLISSANDO)
						&& notation.getStyle().equals(Notation.Style.WAVY)));
		assertTrue(thirdNote.beginsNotation(Notation.Type.GLISSANDO));
		assertEquals(1, thirdNote.getNotations().size());
		Notation glissandoBetweenThirdAndFourth = thirdNote.getNotations().stream().findFirst().orElseThrow();
		assertEquals(fourthNote,
				thirdNote.getConnection(glissandoBetweenThirdAndFourth).get().getFollowingNote().get());

		assertTrue(fourthNote.hasNotations());
		assertTrue(fourthNote.endsNotation(Notation.Type.GLISSANDO));
		assertTrue(fourthNote.getNotations().stream()
				.anyMatch(notation -> notation.getType().equals(Notation.Type.GLISSANDO)
						&& notation.getStyle().equals(Notation.Style.WAVY)));
		assertEquals(1, fourthNote.getNotations().size());

		final Note fifthNote = (Note) firstMeasure.get(voiceNumber, 4);
		assertEquals(1, fifthNote.getNotations().size());
		assertTrue(fifthNote.beginsNotation(Notation.Type.SLUR));

		final Note sixthNote = (Note) firstMeasure.get(voiceNumber, 5);
		assertEquals(1, sixthNote.getNotations().size());
		assertTrue(sixthNote.hasNotation(Notation.Type.SLUR));
		assertFalse(sixthNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(sixthNote.endsNotation(Notation.Type.SLUR));

		final Note seventhNote = (Note) firstMeasure.get(voiceNumber, 6);
		assertEquals(1, seventhNote.getNotations().size());
		assertTrue(seventhNote.endsNotation(Notation.Type.SLUR));

		final Notation slurFromFifthToSeventhNote = fifthNote.getNotations().stream().findAny().orElseThrow();
		final List<Note> notesInSlurFromFifthToSeventhNote = slurFromFifthToSeventhNote
				.getNotesStartingFrom(fifthNote);
		assertEquals(3, notesInSlurFromFifthToSeventhNote.size());
		assertEquals(fifthNote, notesInSlurFromFifthToSeventhNote.get(0));
		assertEquals(sixthNote, notesInSlurFromFifthToSeventhNote.get(1));
		assertEquals(seventhNote, notesInSlurFromFifthToSeventhNote.get(2));

		final Measure secondMeasure = part.getMeasure(1, 2);

		final Note eightNote = (Note) secondMeasure.get(voiceNumber, 0);
		assertEquals(1, eightNote.getNotations().size());
		assertTrue(eightNote.beginsNotation(Notation.Type.SLUR));

		final Note ninthNote = (Note) secondMeasure.get(voiceNumber, 1);
		assertEquals(1, ninthNote.getNotations().size());
		assertTrue(ninthNote.hasNotation(Notation.Type.SLUR));
		assertFalse(ninthNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(ninthNote.endsNotation(Notation.Type.SLUR));

		assertTrue(secondMeasure.get(voiceNumber, 2).isRest());

		final Note tenthNote = (Note) secondMeasure.get(voiceNumber, 3);
		assertEquals(1, tenthNote.getNotations().size());
		assertTrue(tenthNote.endsNotation(Notation.Type.SLUR));

		final Notation slurFromEightToTenthNote = eightNote.getNotations().stream().findAny().orElseThrow();
		final List<Note> notesInSlurFromEightToTenthNote = slurFromEightToTenthNote.getNotesStartingFrom(eightNote);
		assertEquals(3, notesInSlurFromEightToTenthNote.size());
		assertEquals(eightNote, notesInSlurFromEightToTenthNote.get(0));
		assertEquals(ninthNote, notesInSlurFromEightToTenthNote.get(1));
		assertEquals(tenthNote, notesInSlurFromEightToTenthNote.get(2));

		final Note eleventhNote = (Note) secondMeasure.get(voiceNumber, 4);
		assertEquals(2, eleventhNote.getNotations().size());
		assertTrue(eleventhNote.beginsNotation(Notation.Type.GLISSANDO));
		assertTrue(eleventhNote.beginsNotation(Notation.Type.SLUR));

		final Note twelthNote = (Note) secondMeasure.get(voiceNumber, 5);
		assertEquals(2, twelthNote.getNotations().size());
		assertTrue(twelthNote.endsNotation(Notation.Type.GLISSANDO));
		assertTrue(twelthNote.endsNotation(Notation.Type.SLUR));

		final Measure thirdMeasure = part.getMeasure(1, 3);
		final Chord firstChord = (Chord) thirdMeasure.get(voiceNumber, 0);
		assertTrue(firstChord.getNote(0).beginsNotation(Notation.Type.ARPEGGIATE));
		assertTrue(firstChord.getNote(1).hasNotation(Notation.Type.ARPEGGIATE));
		assertTrue(firstChord.getNote(2).endsNotation(Notation.Type.ARPEGGIATE));

		final Chord secondChord = (Chord) thirdMeasure.get(voiceNumber, 1);
		assertTrue(secondChord.getNote(0).beginsNotation(Notation.Type.ARPEGGIATE_UP));
		assertTrue(secondChord.getNote(1).hasNotation(Notation.Type.ARPEGGIATE_UP));
		assertTrue(secondChord.getNote(2).endsNotation(Notation.Type.ARPEGGIATE_UP));

		final Chord thirdChord = (Chord) thirdMeasure.get(voiceNumber, 2);
		assertTrue(thirdChord.getNote(0).beginsNotation(Notation.Type.ARPEGGIATE_DOWN));
		assertTrue(thirdChord.getNote(1).hasNotation(Notation.Type.ARPEGGIATE_DOWN));
		assertTrue(thirdChord.getNote(2).endsNotation(Notation.Type.ARPEGGIATE_DOWN));

		final Chord fourthChord = (Chord) thirdMeasure.get(voiceNumber, 3);
		assertTrue(fourthChord.getNote(0).beginsNotation(Notation.Type.NON_ARPEGGIATE));
		assertTrue(fourthChord.getNote(1).hasNotation(Notation.Type.NON_ARPEGGIATE));
		assertTrue(fourthChord.getNote(2).endsNotation(Notation.Type.NON_ARPEGGIATE));

		final Measure fourthMeasure = part.getMeasure(1, 4);
		final Note fourthMeasureFirst = (Note) fourthMeasure.get(voiceNumber, 0);
		assertEquals(1, fourthMeasureFirst.getNotations().size());
		assertTrue(fourthMeasureFirst.getNotations().stream().anyMatch(notation -> notation.getType().equals(
				Notation.Type.SLUR) && notation.getStyle().equals(Notation.Style.DOTTED)));

		final Note fourthMeasureSecond = (Note) fourthMeasure.get(voiceNumber, 1);
		assertEquals(1, fourthMeasureSecond.getNotations().size());
		assertTrue(fourthMeasureSecond.getNotations().stream().anyMatch(notation -> notation.getType().equals(
				Notation.Type.SLUR) && notation.getStyle().equals(Notation.Style.DOTTED)));

		final Note fourthMeasureThird = (Note) fourthMeasure.get(voiceNumber, 2);
		assertEquals(1, fourthMeasureThird.getNotations().size());
		assertTrue(fourthMeasureThird.getNotations().stream().anyMatch(notation -> notation.getType().equals(
				Notation.Type.TIE) && notation.getStyle().equals(Notation.Style.DASHED)));

		final Note fourthMeasureFourth = (Note) fourthMeasure.get(voiceNumber, 3);
		assertEquals(1, fourthMeasureFourth.getNotations().size());
		assertTrue(fourthMeasureFourth.getNotations().stream().anyMatch(notation -> notation.getType().equals(
				Notation.Type.TIE) && notation.getStyle().equals(Notation.Style.DASHED)));

		final Note fourthMeasureFifth = (Note) fourthMeasure.get(voiceNumber, 4);
		assertEquals(1, fourthMeasureFifth.getNotations().size());
		assertTrue(fourthMeasureFifth.getNotations().stream().anyMatch(notation -> notation.getType().equals(
				Notation.Type.GLISSANDO) && notation.getStyle().equals(Notation.Style.SOLID)));

		final Note fourthMeasureSixth = (Note) fourthMeasure.get(voiceNumber, 5);
		assertEquals(1, fourthMeasureSixth.getNotations().size());
		assertTrue(fourthMeasureSixth.getNotations().stream().anyMatch(notation -> notation.getType().equals(
				Notation.Type.GLISSANDO) && notation.getStyle().equals(Notation.Style.SOLID)));
	}

	/*
	 * Expects the content of "multi_staff_multi_voice_notation_test.musicxml".
	 */
	static void assertNotationsReadCorrectlyFromMultipleStavesWithMultipleVoices(Score score) {
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
		assertEquals(1, firstNoteInTopStaff.getNotations().size());
		assertTrue(firstNoteInTopStaff.beginsNotation(Notation.Type.SLUR));

		final Note secondNoteInTopStaff = (Note) topStaffFirstMeasure.get(topStaffSingleVoiceNumber, 1);
		assertEquals(2, secondNoteInTopStaff.getNotations().size());
		assertTrue(secondNoteInTopStaff.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNoteInTopStaff.endsNotation(Notation.Type.SLUR));

		final Note thirdNoteInTopStaff = (Note) topStaffFirstMeasure.get(topStaffSingleVoiceNumber, 2);
		assertEquals(2, thirdNoteInTopStaff.getNotations().size());
		assertTrue(thirdNoteInTopStaff.endsNotation(Notation.Type.SLUR));
		assertFalse(thirdNoteInTopStaff.beginsNotation(Notation.Type.SLUR));

		final Note fourthNoteInTopStaff = (Note) topStaffFirstMeasure.get(topStaffSingleVoiceNumber, 3);
		assertEquals(1, fourthNoteInTopStaff.getNotations().size());
		assertTrue(fourthNoteInTopStaff.endsNotation(Notation.Type.SLUR));

		final Notation slurFromFirstToThirdNote = firstNoteInTopStaff.getNotations().stream().findFirst().orElseThrow();
		List<Note> notesInSlurFromFirstToThirdNote = slurFromFirstToThirdNote
				.getNotesStartingFrom(firstNoteInTopStaff);
		assertEquals(3, notesInSlurFromFirstToThirdNote.size());
		assertEquals(firstNoteInTopStaff, notesInSlurFromFirstToThirdNote.get(0));
		assertEquals(secondNoteInTopStaff, notesInSlurFromFirstToThirdNote.get(1));
		assertEquals(thirdNoteInTopStaff, notesInSlurFromFirstToThirdNote.get(2));

		final Notation slurFromSecondToFourthNote = secondNoteInTopStaff.getNotations().stream()
				.filter(notation -> secondNoteInTopStaff.getConnection(notation).get().isBeginning())
				.findAny()
				.orElseThrow();

		List<Note> notesInSlurFromSecondToFourthNote = slurFromSecondToFourthNote
				.getNotesStartingFrom(secondNoteInTopStaff);
		assertEquals(3, notesInSlurFromSecondToFourthNote.size());
		assertEquals(secondNoteInTopStaff, notesInSlurFromSecondToFourthNote.get(0));
		assertEquals(thirdNoteInTopStaff, notesInSlurFromSecondToFourthNote.get(1));
		assertEquals(fourthNoteInTopStaff, notesInSlurFromSecondToFourthNote.get(2));

		// Check bottom staff of first measure
		final Measure bottomStaffFirstMeasure = bottomStaff.getMeasure(1);
		final int bottomStaffSingleVoiceNumber = bottomStaffFirstMeasure.getVoiceNumbers().get(0);

		assertTrue(bottomStaffFirstMeasure.get(bottomStaffSingleVoiceNumber, 0).isRest());

		final Note firstNoteInBottomStaff = (Note) bottomStaffFirstMeasure.get(bottomStaffSingleVoiceNumber, 1);
		assertEquals(1, firstNoteInBottomStaff.getNotations().size());
		assertTrue(firstNoteInBottomStaff.beginsNotation(Notation.Type.SLUR));

		final Note secondNoteInBottomStaff = (Note) bottomStaffFirstMeasure.get(bottomStaffSingleVoiceNumber, 2);
		assertEquals(1, secondNoteInBottomStaff.getNotations().size());
		assertTrue(secondNoteInBottomStaff.endsNotation(Notation.Type.SLUR));

		final Notation bottomStaffFirstMeasureSlur = firstNoteInBottomStaff.getNotations().stream().findFirst()
				.orElseThrow();
		final List<Note> bottomStaffFirstMeasureSlurNotes = bottomStaffFirstMeasureSlur
				.getNotesStartingFrom(firstNoteInBottomStaff);
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
		assertEquals(1, fifthNoteInTopStaffTopVoice.getNotations().size());
		assertTrue(fifthNoteInTopStaffTopVoice.beginsNotation(Notation.Type.SLUR));

		Note sixthNoteInTopStaffTopVoice = (Note) topStaffSecondMeasure.get(topStaffTopVoice, 1);
		assertEquals(2, sixthNoteInTopStaffTopVoice.getNotations().size());
		assertTrue(sixthNoteInTopStaffTopVoice.hasNotation(Notation.Type.SLUR));
		assertFalse(sixthNoteInTopStaffTopVoice.beginsNotation(Notation.Type.SLUR));
		assertFalse(sixthNoteInTopStaffTopVoice.endsNotation(Notation.Type.SLUR));
		assertTrue(sixthNoteInTopStaffTopVoice.beginsNotation(Notation.Type.GLISSANDO));

		Note seventhNoteInTopStaffTopVoice = (Note) topStaffSecondMeasure.get(topStaffTopVoice, 2);
		assertEquals(2, seventhNoteInTopStaffTopVoice.getNotations().size());
		assertTrue(seventhNoteInTopStaffTopVoice.endsNotation(Notation.Type.SLUR));
		assertTrue(seventhNoteInTopStaffTopVoice.endsNotation(Notation.Type.GLISSANDO));

		final Notation slurBetweenTopVoiceNotes = fifthNoteInTopStaffTopVoice.getNotations().stream().findFirst()
				.orElseThrow();
		final List<Note> notesInSlurBetweenTopVoiceNotes = slurBetweenTopVoiceNotes
				.getNotesStartingFrom(fifthNoteInTopStaffTopVoice);
		assertEquals(3, notesInSlurBetweenTopVoiceNotes.size());
		assertEquals(fifthNoteInTopStaffTopVoice, notesInSlurBetweenTopVoiceNotes.get(0));
		assertEquals(sixthNoteInTopStaffTopVoice, notesInSlurBetweenTopVoiceNotes.get(1));
		assertEquals(seventhNoteInTopStaffTopVoice, notesInSlurBetweenTopVoiceNotes.get(2));

		final Notation glissandoInTopVoice = sixthNoteInTopStaffTopVoice.getNotations().stream()
				.filter(notation -> sixthNoteInTopStaffTopVoice.getConnection(notation).get().isBeginning())
				.findAny()
				.orElseThrow();

		final List<Note> notesInTopVoiceGlissando = glissandoInTopVoice
				.getNotesStartingFrom(sixthNoteInTopStaffTopVoice);
		assertEquals(2, notesInTopVoiceGlissando.size());
		assertEquals(sixthNoteInTopStaffTopVoice, notesInTopVoiceGlissando.get(0));
		assertEquals(seventhNoteInTopStaffTopVoice, notesInTopVoiceGlissando.get(1));

		// Check notes in lower voice of top staff measure 2
		Note fifthNoteInTopStaffBottomVoice = (Note) topStaffSecondMeasure.get(topStaffBottomVoice, 0);
		assertEquals(2, fifthNoteInTopStaffBottomVoice.getNotations().size());
		assertTrue(fifthNoteInTopStaffBottomVoice.beginsNotation(Notation.Type.SLUR));
		assertTrue(fifthNoteInTopStaffBottomVoice.beginsNotation(Notation.Type.GLISSANDO));

		Note sixthNoteInTopStaffBottomVoice = (Note) topStaffSecondMeasure.get(topStaffBottomVoice, 1);
		assertEquals(2, sixthNoteInTopStaffBottomVoice.getNotations().size());
		assertTrue(sixthNoteInTopStaffBottomVoice.endsNotation(Notation.Type.GLISSANDO));
		assertTrue(sixthNoteInTopStaffBottomVoice.hasNotation(Notation.Type.SLUR));
		assertFalse(sixthNoteInTopStaffBottomVoice.beginsNotation(Notation.Type.SLUR));
		assertFalse(sixthNoteInTopStaffBottomVoice.endsNotation(Notation.Type.SLUR));

		Note seventhNoteInTopStaffBottomVoice = (Note) topStaffSecondMeasure.get(topStaffBottomVoice, 2);
		assertEquals(1, seventhNoteInTopStaffBottomVoice.getNotations().size());
		assertTrue(seventhNoteInTopStaffBottomVoice.endsNotation(Notation.Type.SLUR));

		final Notation slurBetweenBottomVoiceNotes = fifthNoteInTopStaffBottomVoice.getNotations().stream()
				.filter(notation -> notation.getType().equals(Notation.Type.SLUR))
				.findAny()
				.orElseThrow();

		final List<Note> notesInSlurBetweenBottomVoiceNotes = slurBetweenBottomVoiceNotes
				.getNotesStartingFrom(fifthNoteInTopStaffBottomVoice);
		assertEquals(3, notesInSlurBetweenBottomVoiceNotes.size());
		assertEquals(fifthNoteInTopStaffBottomVoice, notesInSlurBetweenBottomVoiceNotes.get(0));
		assertEquals(sixthNoteInTopStaffBottomVoice, notesInSlurBetweenBottomVoiceNotes.get(1));
		assertEquals(seventhNoteInTopStaffBottomVoice, notesInSlurBetweenBottomVoiceNotes.get(2));

		final Notation glissandoInBottomVoice = fifthNoteInTopStaffBottomVoice.getNotations().stream()
				.filter(notation -> notation.getType().equals(Notation.Type.GLISSANDO))
				.findAny()
				.orElseThrow();

		final List<Note> notesInBottomVoiceGlissando = glissandoInBottomVoice
				.getNotesStartingFrom(fifthNoteInTopStaffBottomVoice);
		assertEquals(2, notesInTopVoiceGlissando.size());
		assertEquals(fifthNoteInTopStaffBottomVoice, notesInBottomVoiceGlissando.get(0));
		assertEquals(sixthNoteInTopStaffBottomVoice, notesInBottomVoiceGlissando.get(1));
	}

	/*
	 * Expects the contents of "clef_change_where_note_in_another_voice_carries_over.musicxml".
	 */
	static void assertClefChangeInCorrectPlaceWhenNoteCarriesOverClefChange(Score score) {
		final Measure measure = score.getPart(0).getMeasure(1, 1);
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), Durations.QUARTER),
				measure.get(1, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 5), Durations.HALF), measure.get(1, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 5), Durations.QUARTER),
				measure.get(1, 2));

		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				measure.get(2, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.QUARTER),
				measure.get(2, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 3), Durations.QUARTER),
				measure.get(2, 2));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 3), Durations.QUARTER),
				measure.get(2, 3));

		assertEquals(Clefs.G, measure.getClef());
		final List<Offset<Clef>> clefChanges = measure.getClefChanges();

		assertEquals(1, clefChanges.size());
		assertEquals(new Offset<>(Clefs.ALTO, Durations.HALF), clefChanges.get(0));
	}

	/*
	 * Expects the content of "dotted_note_test.musicxml".
	 * Checks only that the durations are correct.
	 */
	static void assertDottedNotesReadCorrectly(Score score) {
		final Measure firstMeasure = score.getPart(0).getMeasure(1, 1);

		final Duration firstNoteDuration = firstMeasure.get(1, 0).getDuration();
		assertEquals(Durations.QUARTER.addDot(), firstNoteDuration);
		assertEquals(1, firstNoteDuration.getDotCount());
		assertEquals(1, firstNoteDuration.getTupletDivisor());

		final Duration secondNoteDuration = firstMeasure.get(1, 2).getDuration();
		assertEquals(Durations.EIGHTH, secondNoteDuration);
		assertEquals(0, secondNoteDuration.getDotCount());
		assertEquals(1, secondNoteDuration.getTupletDivisor());

		final Duration secondRestDuration = firstMeasure.get(1, 3).getDuration();
		assertEquals(Durations.QUARTER.addDot(), secondRestDuration);
		assertEquals(1, secondRestDuration.getDotCount());
		assertEquals(1, secondRestDuration.getTupletDivisor());

		final Measure secondMeasure = score.getPart(0).getMeasure(1, 2);
		final Duration thirdNoteDuration = secondMeasure.get(1, 0).getDuration();
		assertEquals(Durations.HALF.addDot().addDot(), thirdNoteDuration);
		assertEquals(2, thirdNoteDuration.getDotCount());
		assertEquals(1, thirdNoteDuration.getTupletDivisor());

		final Measure thirdMeasure = score.getPart(0).getMeasure(1, 3);
		final Duration fourthNoteDuration = thirdMeasure.get(1, 0).getDuration();
		assertEquals(Durations.EIGHTH_TRIPLET.addDot(), fourthNoteDuration);
		assertEquals(1, fourthNoteDuration.getDotCount());
		assertEquals(3, fourthNoteDuration.getTupletDivisor());

		final Duration fifthNoteDuration = thirdMeasure.get(1, 1).getDuration();
		assertEquals(Durations.SIXTEENTH_TRIPLET, fifthNoteDuration);
		assertEquals(0, fifthNoteDuration.getDotCount());
		assertEquals(3, fifthNoteDuration.getTupletDivisor());

		final Duration sixthNoteDuration = thirdMeasure.get(1, 2).getDuration();
		assertEquals(Durations.EIGHTH_TRIPLET, sixthNoteDuration);
		assertEquals(0, sixthNoteDuration.getDotCount());
		assertEquals(3, sixthNoteDuration.getTupletDivisor());
	}

	/*
	 * Expects the content of "tuplet_test.musicxml".
	 * Checks only that durations are correct.
	 */
	static void assertTupletNotesReadCorrectly(Score score) {
		final Measure firstMeasure = score.getPart(0).getMeasure(1, 1);

		for (int i = 0; i <= 2; ++i) {
			final Duration expectedTriplet = firstMeasure.get(1, i).getDuration();
			assertEquals(Durations.EIGHTH_TRIPLET, expectedTriplet);
			assertEquals(3, expectedTriplet.getTupletDivisor());
		}

		for (int i = 3; i <= 7; ++i) {
			final Duration expectedQuintuplet = firstMeasure.get(1, i).getDuration();
			assertEquals(Durations.QUARTER.divide(5), expectedQuintuplet);
			assertEquals(5, expectedQuintuplet.getTupletDivisor());
		}

		for (int i = 8; i <= 14; ++i) {
			final Duration expectedSeptuplet = firstMeasure.get(1, i).getDuration();
			assertEquals(Durations.EIGHTH.divide(7), expectedSeptuplet);
			assertEquals(7, expectedSeptuplet.getTupletDivisor());
		}

		// Skip one rest.

		for (int i = 16; i <= 21; ++i) {
			final Duration expectedSextuplet = firstMeasure.get(1, i).getDuration();
			assertEquals(Durations.QUARTER.divide(6), expectedSextuplet);
			assertEquals(6, expectedSextuplet.getTupletDivisor());
		}

		final Measure secondMeasure = score.getPart(0).getMeasure(1, 2);

		for (int i = 0; i <= 1; ++i) {
			final Duration expectedDottedTriplet = secondMeasure.get(1, i).getDuration();
			assertEquals(Durations.EIGHTH_TRIPLET.addDot(), expectedDottedTriplet);
			assertEquals(3, expectedDottedTriplet.getTupletDivisor());
			assertEquals(1, expectedDottedTriplet.getDotCount());
		}
	}

	/*
	 * Expects the contents of "ornament_test.musicxml".
	 */
	static void assertOrnamentsAreCorrect(Score score) {
		final Measure firstMeasure = score.getPart(0).getMeasure(1, 1);
		final Note firstNote = (Note) firstMeasure.get(1, 0);
		assertEquals(1, firstNote.getOrnaments().size());
		assertTrue(firstNote.hasOrnament(Ornament.Type.INVERTED_TURN));

		final Note secondNote = (Note) firstMeasure.get(1, 1);
		assertEquals(1, secondNote.getOrnaments().size());
		assertTrue(secondNote.hasOrnament(Ornament.Type.TURN));

		final Note thirdNote = (Note) firstMeasure.get(1, 2);
		assertEquals(1, thirdNote.getOrnaments().size());
		assertTrue(thirdNote.hasOrnament(Ornament.Type.TRILL));

		final Note fourthNote = (Note) firstMeasure.get(1, 3);
		assertEquals(1, fourthNote.getOrnaments().size());
		assertTrue(fourthNote.hasOrnament(Ornament.Type.MORDENT));

		final Note fifthNote = (Note) firstMeasure.get(1, 4);
		assertEquals(1, fifthNote.getOrnaments().size());
		assertTrue(fifthNote.hasOrnament(Ornament.Type.INVERTED_MORDENT));

		final Note sixthNote = (Note) firstMeasure.get(1, 5);
		assertEquals(1, sixthNote.getOrnaments().size());
		assertTrue(sixthNote.hasOrnament(Ornament.Type.DOUBLE_TREMOLO));

		final Note seventhNote = (Note) firstMeasure.get(1, 6);
		assertEquals(1, seventhNote.getOrnaments().size());
		assertTrue(seventhNote.hasOrnament(Ornament.Type.TRIPLE_TREMOLO));
	}

	/*
	 * Expects the contents of "grace_note_test.musicxml".
	 */
	static void assertGraceNotesAreCorrect(Score score) {
		final Measure firstMeasure = score.getPart(0).getMeasure(1, 1);

		final Note firstNote = (Note) firstMeasure.get(1, 0);
		assertEquals(1, firstNote.getOrnaments().size());
		List<GraceNote> firstNoteGraceNotes = getGraceNotes(firstNote, Ornament.Type.GRACE_NOTES, GraceNote.class);
		assertEquals(1, firstNoteGraceNotes.size());
		final GraceNote firstGraceNote = firstNoteGraceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), firstGraceNote.getPitch().get());
		assertEquals(Durations.EIGHTH, firstGraceNote.getDisplayableDuration());
		assertEquals(GraceNote.Type.ACCIACCATURA, firstGraceNote.getType());
		assertTrue(firstGraceNote.hasArticulation(Articulation.STACCATO));

		final Note secondNote = (Note) firstMeasure.get(1, 1);
		assertEquals(1, secondNote.getOrnaments().size());
		List<GraceNote> secondNoteGraceNotes = getGraceNotes(secondNote, Ornament.Type.GRACE_NOTES, GraceNote.class);
		assertEquals(1, secondNoteGraceNotes.size());
		final GraceNote secondGraceNote = secondNoteGraceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), secondGraceNote.getPitch().get());
		assertEquals(Durations.EIGHTH, secondGraceNote.getDisplayableDuration());
		assertEquals(GraceNote.Type.APPOGGIATURA, secondGraceNote.getType());
		assertTrue(secondGraceNote.hasArticulation(Articulation.TENUTO));

		final Note thirdNote = (Note) firstMeasure.get(1, 2);
		assertEquals(1, thirdNote.getOrnaments().size());
		List<GraceNote> thirdNoteGraceNotes = getGraceNotes(thirdNote, Ornament.Type.SUCCEEDING_GRACE_NOTES,
				GraceNote.class);
		assertEquals(1, thirdNoteGraceNotes.size());
		final GraceNote thirdGraceNote = thirdNoteGraceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), thirdGraceNote.getPitch().get());
		assertEquals(GraceNote.Type.GRACE_NOTE, thirdGraceNote.getType());
		assertEquals(Durations.EIGHTH, thirdGraceNote.getDisplayableDuration());
		assertTrue(thirdGraceNote.hasOrnament(Ornament.Type.TRILL));

		final Measure secondMeasure = score.getPart(0).getMeasure(1, 2);

		final Note fourthNote = (Note) secondMeasure.get(1, 0);
		assertTrue(fourthNote.endsNotation(Notation.Type.GLISSANDO));
		assertTrue(fourthNote.endsNotation(Notation.Type.SLUR));
		assertTrue(fourthNote.beginsNotation(Notation.Type.SLUR));

		List<GraceNote> fourthNotePrecedingGraceNotes = getGraceNotes(fourthNote, Ornament.Type.GRACE_NOTES,
				GraceNote.class);
		assertEquals(2, fourthNotePrecedingGraceNotes.size());
		final GraceNote fourthGraceNote = fourthNotePrecedingGraceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), fourthGraceNote.getPitch().get());
		assertEquals(GraceNote.Type.GRACE_NOTE, fourthGraceNote.getType());
		assertEquals(Durations.SIXTEENTH, fourthGraceNote.getDisplayableDuration());
		assertTrue(fourthGraceNote.beginsNotation(Notation.Type.SLUR));

		final GraceNote fifthGraceNote = fourthNotePrecedingGraceNotes.get(1);
		assertEquals(Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 4), fifthGraceNote.getPitch().get());
		assertEquals(GraceNote.Type.GRACE_NOTE, fifthGraceNote.getType());
		assertEquals(Durations.SIXTEENTH, fifthGraceNote.getDisplayableDuration());
		assertTrue(fifthGraceNote.hasNotation(Notation.Type.SLUR));
		assertTrue(fifthGraceNote.beginsNotation(Notation.Type.GLISSANDO));

		List<GraceNote> fourthNoteSucceedingGraceNotes = getGraceNotes(fourthNote,
				Ornament.Type.SUCCEEDING_GRACE_NOTES, GraceNote.class);

		assertEquals(2, fourthNoteSucceedingGraceNotes.size());
		final GraceNote sixthGraceNote = fourthNoteSucceedingGraceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5), sixthGraceNote.getPitch().get());
		assertEquals(GraceNote.Type.GRACE_NOTE, sixthGraceNote.getType());
		assertEquals(Durations.SIXTEENTH, sixthGraceNote.getDisplayableDuration());
		assertTrue(sixthGraceNote.hasNotation(Notation.Type.SLUR));

		final GraceNote seventhGraceNote = fourthNoteSucceedingGraceNotes.get(1);
		assertEquals(Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 4), seventhGraceNote.getPitch().get());
		assertEquals(GraceNote.Type.GRACE_NOTE, seventhGraceNote.getType());
		assertEquals(Durations.SIXTEENTH, seventhGraceNote.getDisplayableDuration());
		assertTrue(seventhGraceNote.endsNotation(Notation.Type.SLUR));
		assertTrue(seventhGraceNote.beginsNotation(Notation.Type.GLISSANDO));

		final Note fifthNote = (Note) secondMeasure.get(1, 1);
		assertTrue(fifthNote.endsNotation(Notation.Type.SLUR));

		List<GraceNote> fifthNoteGraceNotes = getGraceNotes(fifthNote, Ornament.Type.GRACE_NOTES, GraceNote.class);
		assertEquals(2, fifthNoteGraceNotes.size());

		final GraceNote eightGraceNote = fifthNoteGraceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), eightGraceNote.getPitch().get());
		assertEquals(Durations.QUARTER, eightGraceNote.getDisplayableDuration());
		assertTrue(eightGraceNote.beginsNotation(Notation.Type.SLUR));
		assertTrue(eightGraceNote.endsNotation(Notation.Type.GLISSANDO));

		final GraceNote ninthGraceNote = fifthNoteGraceNotes.get(1);
		assertEquals(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), ninthGraceNote.getPitch().get());
		assertEquals(Durations.QUARTER, ninthGraceNote.getDisplayableDuration());
		assertTrue(ninthGraceNote.hasNotation(Notation.Type.SLUR));
	}

	private static <T extends Ornamental> List<T> getGraceNotes(Note note, Ornament.Type graceNotesType,
			Class<T> type) {
		Optional<Ornament> precedingGraceNotes = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(
						graceNotesType)).findFirst();
		assertTrue(precedingGraceNotes.isPresent());

		return precedingGraceNotes.get().getOrnamentalNotes().stream()
				.filter(ornamental -> type.isAssignableFrom(ornamental.getClass()))
				.map(ornamental -> type.cast(ornamental))
				.collect(Collectors.toList());
	}

	/*
	 * Expects the contents of "grace_note_chord_test.musicxml".
	 */
	static void assertGraceNoteChordsAreCorrect(Score score) {
		final Measure firstMeasure = score.getPart(0).getMeasure(1, 1);
		final Note firstNote = (Note) firstMeasure.get(1, 0);
		assertEquals(1, firstNote.getOrnaments().size());
		List<GraceNoteChord> firstNoteOrnamentals = getGraceNotes(firstNote, Ornament.Type.GRACE_NOTES,
				GraceNoteChord.class);
		assertEquals(1, firstNoteOrnamentals.size());
		final GraceNoteChord firstGraceChord = firstNoteOrnamentals.get(0);
		assertGraceChord(firstGraceChord, Arrays.asList(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4),
				Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4),
				Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 5)), Ornamental.Type.ACCIACCATURA, Durations.EIGHTH);

		final Note secondNote = (Note) firstMeasure.get(1, 1);
		assertEquals(2, secondNote.getOrnaments().size());
		List<GraceNoteChord> secondNotePrecedingOrnamentals = getGraceNotes(secondNote, Ornament.Type.GRACE_NOTES,
				GraceNoteChord.class);
		assertEquals(1, secondNotePrecedingOrnamentals.size());
		final GraceNoteChord secondGraceChord = secondNotePrecedingOrnamentals.get(0);
		assertGraceChord(secondGraceChord, Arrays.asList(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4),
				Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4),
				Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4)), Ornamental.Type.GRACE_NOTE, Durations.EIGHTH);

		List<GraceNoteChord> secondNoteSucceedingOrnamentals = getGraceNotes(secondNote,
				Ornament.Type.SUCCEEDING_GRACE_NOTES, GraceNoteChord.class);
		assertEquals(1, secondNoteSucceedingOrnamentals.size());
		final GraceNoteChord thirdGraceChord = secondNoteSucceedingOrnamentals.get(0);
		assertGraceChord(thirdGraceChord, Arrays.asList(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4),
				Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 4),
				Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 5)), Ornamental.Type.GRACE_NOTE, Durations.SIXTEENTH);

		assertTrue(thirdGraceChord.getNote(0).hasNotation(Notation.Type.ARPEGGIATE));
		assertTrue(thirdGraceChord.getNote(1).hasNotation(Notation.Type.ARPEGGIATE));
		assertTrue(thirdGraceChord.getNote(2).hasNotation(Notation.Type.ARPEGGIATE));

		final Measure secondMeasure = score.getPart(0).getMeasure(1, 2);
		final Note thirdNote = (Note) secondMeasure.get(1, 0);
		assertEquals(1, thirdNote.getOrnaments().size());
		List<GraceNoteChord> thirdNoteSucceedingOrnamentals = getGraceNotes(thirdNote,
				Ornament.Type.SUCCEEDING_GRACE_NOTES, GraceNoteChord.class);
		assertEquals(1, thirdNoteSucceedingOrnamentals.size());
		final GraceNoteChord fourthGraceChord = thirdNoteSucceedingOrnamentals.get(0);
		assertGraceChord(fourthGraceChord, Arrays.asList(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4),
				Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 4)), Ornamental.Type.GRACE_NOTE, Durations.SIXTEENTH);
	}

	private static void assertGraceChord(GraceNoteChord chord, List<Pitch> expectedPitches, GraceNote.Type expectedType,
			Duration expectedDuration) {
		assertEquals(expectedPitches.size(), chord.getNoteCount());
		for (int i = 0; i < expectedPitches.size(); ++i) {
			final GraceNote note = chord.getNote(i);
			assertEquals(expectedPitches.get(i), note.getPitch().get());
			assertEquals(expectedType, note.getType());
			assertEquals(expectedDuration, note.getDisplayableDuration());
		}
	}

	/*
	 * Expects the contents of "direction_test.musicxml".
	 */
	static void assertDirectionsCorrect(Score score) {
		final Part part = score.getPart(0);
		assertTrue(part.isMultiStaff());
		final Measure topStaffFirst = part.getMeasure(1, 1);
		assertEquals(1, topStaffFirst.getVoiceCount());
		assertTrue(topStaffFirst.containsDirections());

		List<Offset<Direction>> topStaffFirstDirections = topStaffFirst.getDirections();
		assertEquals(1, topStaffFirstDirections.size());

		Offset<Direction> secondDirection = topStaffFirstDirections.get(0);
		assertEquals(Durations.QUARTER.addDot(), secondDirection.getDuration().get());
		assertEquals(Direction.of(Direction.Type.TEXT, "A text"), secondDirection.get());

		final Measure topStaffSecond = part.getMeasure(1, 2);
		assertEquals(2, topStaffSecond.getVoiceCount());
		assertTrue(topStaffSecond.containsDirections());

		List<Offset<Direction>> topStaffSecondDirections = topStaffSecond.getDirections();
		assertEquals(1, topStaffSecondDirections.size());

		Offset<Direction> thirdDirection = topStaffSecondDirections.get(0);
		assertEquals(Durations.QUARTER.addDot(), thirdDirection.getDuration().get());
		assertEquals(Direction.of(Direction.Type.TEXT, "Another text"), thirdDirection.get());
	}
}
