/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MonophonicPatternTest {

	private final List<Durational> referenceNotes;

	MonophonicPatternTest() {
		final List<Durational> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH));
		notes.add(Rest.of(Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		this.referenceNotes = Collections.unmodifiableList(notes);
	}

	@Test
	void testCreatingMonophonicPatternFromListOfDurationals() {
		try {
			new MonophonicPattern(null);
			fail("Was able to create pattern with null contents");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		final List<Durational> notes = new ArrayList<>();

		try {
			new MonophonicPattern(notes);
			fail("Was able to create pattern with empty contents");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		final List<Durational> chordList = new ArrayList<>();
		chordList.add(Chord.of(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH),
				Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH)));

		try {
			new MonophonicPattern(chordList);
			fail("Was able to create pattern with Chord in contents");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	void testImmutability() {
		List<Durational> contents = new ArrayList<>(referenceNotes);

		final Pattern pattern = new MonophonicPattern(contents);
		assertEquals(contents, pattern.getContents());

		contents.add(Rest.of(Durations.QUARTER));
		assertNotEquals(pattern.getContents().size(), contents.size());

		try {
			pattern.getContents().add(Rest.of(Durations.QUARTER));
			fail("Was able to add to contents of pattern");
		} catch (Exception e) {
			// Pass, exception is expected.
		}
	}

	@Test
	void testGetNumberOfVoicesReturnsOne() {
		final Pattern pattern = new MonophonicPattern(this.referenceNotes);
		assertEquals(1, pattern.getNumberOfVoices());
	}

	@Test
	void testGetVoiceNumbersReturnsSingleValue() {
		final Pattern pattern = new MonophonicPattern(this.referenceNotes);
		assertEquals(1, pattern.getVoiceNumbers().size());
	}

	@Test
	void testGetVoiceReturnsContents() {
		final Pattern pattern = new MonophonicPattern(this.referenceNotes);
		List<Durational> voiceContents = pattern.getVoice(pattern.getVoiceNumbers().get(0));
		assertEquals(this.referenceNotes, voiceContents);
	}

	@Test
	void testEquals() {
		final Pattern pattern1 = new MonophonicPattern(this.referenceNotes);
		final Pattern pattern2 = new MonophonicPattern(this.referenceNotes);

		assertEquals(pattern1, pattern1);

		assertEquals(pattern1, pattern2);
		assertEquals(pattern2, pattern1);

		final List<Durational> modifiedNotes = new ArrayList<>(this.referenceNotes);
		modifiedNotes.add(Rest.of(Durations.QUARTER));

		assertFalse(pattern1.equals(new MonophonicPattern(modifiedNotes)));
	}

	@Test
	void testIsMonophonic() {
		final Pattern pattern = new MonophonicPattern(this.referenceNotes);
		assertTrue(pattern.isMonophonic());
	}

	@Test
	void testEqualsInPitch() {
		final Pattern pattern = new MonophonicPattern(this.referenceNotes);

		final List<Durational> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		notes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.WHOLE));

		assertTrue(pattern.equalsInPitch(new MonophonicPattern(notes)));

		notes.add(Rest.of(Durations.QUARTER));

		assertTrue(pattern.equalsInPitch(new MonophonicPattern(notes)),
				"Adding rest to end of pattern should not make pattern inequal in pitches");

		notes.set(1, Note.of(Pitch.of(Pitch.Base.E, -1, 3), Durations.WHOLE));
		assertFalse(pattern.equalsInPitch(new MonophonicPattern(notes)));

		List<Durational> referenceNotesWithAddition = new ArrayList<>(referenceNotes);
		referenceNotesWithAddition.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));
		assertFalse(pattern.equalsInPitch(new MonophonicPattern(referenceNotesWithAddition)));
	}

	@Test
	void testEqualsEnharmonically() {
		final Pattern pattern = new MonophonicPattern(this.referenceNotes);

		assertTrue(pattern.equalsEnharmonically(new MonophonicPattern(referenceNotes)));

		final List<Durational> samePitchesWithDifferentDurations = new ArrayList<>();
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		samePitchesWithDifferentDurations.add(Rest.of(Durations.QUARTER));
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.WHOLE));

		assertTrue(pattern.equalsEnharmonically(new MonophonicPattern(samePitchesWithDifferentDurations)),
				"Difference in durations affected enharmonic equality when it shouldn't have affected it.");

		final List<Durational> samePitchesWithDifferentSpellings = new ArrayList<>();
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.D, -2, 5), Durations.EIGHTH));
		samePitchesWithDifferentSpellings.add(Rest.of(Durations.QUARTER));
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.A, 1, 3), Durations.QUARTER));

		assertTrue(pattern.equalsEnharmonically(new MonophonicPattern(samePitchesWithDifferentSpellings)),
				"Difference in note spellings affected enharmonic equality when it shouldn't have affected it.");

		final List<Durational> notesWithAdditionalRestAtEnd = new ArrayList<>(referenceNotes);
		notesWithAdditionalRestAtEnd.add(Rest.of(Durations.QUARTER));

		assertTrue(pattern.equalsEnharmonically(new MonophonicPattern(notesWithAdditionalRestAtEnd)),
				"Adding rest to end of pattern should not make patterns unequal enharmonically.");

		final List<Durational> enharmonicallyUnequalPitches = new ArrayList<>();
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.D, 0, 5), Durations.EIGHTH));
		enharmonicallyUnequalPitches.add(Rest.of(Durations.QUARTER));
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsEnharmonically(new MonophonicPattern(enharmonicallyUnequalPitches)));
	}

	@Test
	void testEqualsTranspositionally() {
		final Pattern pattern = new MonophonicPattern(referenceNotes);
		assertTrue(pattern.equalsTranspositionally(new MonophonicPattern(referenceNotes)));

		final List<Durational> transposedUpByMajorSecond = new ArrayList<>();
		transposedUpByMajorSecond.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));
		transposedUpByMajorSecond.add(Note.of(Pitch.of(Pitch.Base.D, 0, 5), Durations.EIGHTH));
		transposedUpByMajorSecond.add(Rest.of(Durations.QUARTER));
		transposedUpByMajorSecond.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.QUARTER));
		transposedUpByMajorSecond.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));

		assertTrue(pattern.equalsTranspositionally(new MonophonicPattern(transposedUpByMajorSecond)));

		final List<Durational> transposedDownByOctave = new ArrayList<>();
		transposedDownByOctave.add(Note.of(Pitch.of(Pitch.Base.C, 0, 3), Durations.EIGHTH));
		transposedDownByOctave.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		transposedDownByOctave.add(Rest.of(Durations.QUARTER));
		transposedDownByOctave.add(Note.of(Pitch.of(Pitch.Base.D, 0, 3), Durations.QUARTER));
		transposedDownByOctave.add(Note.of(Pitch.of(Pitch.Base.B, -1, 2), Durations.QUARTER));

		assertTrue(pattern.equalsTranspositionally(new MonophonicPattern(transposedDownByOctave)));

		final List<Durational> withOneNoteInDifferentOctaveThanInReferencePattern = new ArrayList<>();
		withOneNoteInDifferentOctaveThanInReferencePattern.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		withOneNoteInDifferentOctaveThanInReferencePattern.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		withOneNoteInDifferentOctaveThanInReferencePattern.add(Rest.of(Durations.QUARTER));
		withOneNoteInDifferentOctaveThanInReferencePattern
				.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		withOneNoteInDifferentOctaveThanInReferencePattern
				.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsTranspositionally(
				new MonophonicPattern(withOneNoteInDifferentOctaveThanInReferencePattern)));

		final List<Durational> transposedUpByMajorSecondWithOneNoteTransposedByMinorSecond = new ArrayList<>();
		transposedUpByMajorSecondWithOneNoteTransposedByMinorSecond
				.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));
		transposedUpByMajorSecondWithOneNoteTransposedByMinorSecond
				.add(Note.of(Pitch.of(Pitch.Base.D, 0, 5), Durations.EIGHTH));
		transposedUpByMajorSecondWithOneNoteTransposedByMinorSecond.add(Rest.of(Durations.QUARTER));
		transposedUpByMajorSecondWithOneNoteTransposedByMinorSecond
				.add(Note.of(Pitch.of(Pitch.Base.D, 1, 4), Durations.QUARTER));
		transposedUpByMajorSecondWithOneNoteTransposedByMinorSecond
				.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));

		assertFalse(pattern.equalsTranspositionally(
				new MonophonicPattern(transposedUpByMajorSecondWithOneNoteTransposedByMinorSecond)));

		final List<Durational> notesWithAdditionalRestAtEnd = new ArrayList<>(referenceNotes);
		notesWithAdditionalRestAtEnd.add(Rest.of(Durations.QUARTER));
		assertTrue(pattern.equalsTranspositionally(new MonophonicPattern(notesWithAdditionalRestAtEnd)),
				"Adding rest to end of pattern should not make patterns unequal enharmonically.");
	}

	@Test
	void testEqualsInDurations() {
		final Pattern pattern = new MonophonicPattern(referenceNotes);
		assertTrue(pattern.equalsInDurations(new MonophonicPattern(referenceNotes)));

		List<Durational> withSameDurationsButDifferentPitches = new ArrayList<>();
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH));
		withSameDurationsButDifferentPitches.add(Rest.of(Durations.QUARTER));
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.D, -1, 4), Durations.QUARTER));
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertTrue(pattern.equalsInDurations(new MonophonicPattern(withSameDurationsButDifferentPitches)));

		List<Durational> withDifferentNoteDurations = new ArrayList<>();
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		withDifferentNoteDurations.add(Rest.of(Durations.QUARTER));
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsInDurations(new MonophonicPattern(withDifferentNoteDurations)));

		List<Durational> withDifferentRestDurations = new ArrayList<>();
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH));
		withDifferentRestDurations.add(Rest.of(Durations.EIGHTH));
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsInDurations(new MonophonicPattern(withDifferentRestDurations)));

		final List<Durational> withRestInAPlaceWhereReferenceHasANote = new ArrayList<>();
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH));
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		withRestInAPlaceWhereReferenceHasANote.add(Rest.of(Durations.QUARTER));
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsInDurations(new MonophonicPattern(withRestInAPlaceWhereReferenceHasANote)));

		final List<Durational> withAddedDurationAtEnd = new ArrayList<>(referenceNotes);
		withAddedDurationAtEnd.add(Rest.of(Durations.SIXTEENTH));

		assertFalse(pattern.equalsInDurations(new MonophonicPattern(withAddedDurationAtEnd)));
	}

	@Test
	void testGetName() {
		final Pattern patternWithoutName = new MonophonicPattern(referenceNotes);
		assertTrue(patternWithoutName.getName().isEmpty());

		final String patternName = "A";
		final Pattern patternWithName = new MonophonicPattern(referenceNotes, patternName);
		assertEquals(patternName, patternWithName.getName().get());
	}

	@Test
	void testGetLabels() {
		final Pattern patternWithoutLabels = new MonophonicPattern(referenceNotes);
		assertTrue(patternWithoutLabels.getLabels().isEmpty());

		final String patternName = "A";

		Set<String> labels = new HashSet<>();
		final String testLabelA = "Long";
		final String testLabelB = "Large ambitus";
		labels.add(testLabelA);
		labels.add(testLabelB);

		final Pattern patternWithLabels = new MonophonicPattern(referenceNotes, patternName, labels);

		assertEquals(patternName, patternWithLabels.getName().get());
		assertEquals(labels, patternWithLabels.getLabels());

		labels.add("Label not in pattern");
		assertNotEquals(labels, patternWithLabels.getLabels());
	}

	@Test
	void testHasLabel() {
		final String patternName = "A";

		Set<String> labels = new HashSet<>();
		final String testLabelA = "LabelA";
		final String testLabelB = "LabelB";
		labels.add(testLabelA);
		labels.add(testLabelB);

		final Pattern patternWithLabels = new PolyphonicPattern(referenceNotes, patternName,
				labels);

		assertEquals(patternName, patternWithLabels.getName().get());
		assertTrue(patternWithLabels.hasLabel(testLabelA));
		assertTrue(patternWithLabels.hasLabel(testLabelB));
		assertFalse(patternWithLabels.hasLabel("Label not in pattern"));
	}
}
