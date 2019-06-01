/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MonophonicPatternTest {

	private final List<Durational> referenceNotes;

	MonophonicPatternTest() {
		final List<Durational> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT));
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT));
		notes.add(Rest.of(Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		this.referenceNotes = Collections.unmodifiableList(notes);
	}

	private Pattern createMonophonicPattern(List<Durational> contents) {
		Pattern monophonicPattern = Pattern.monophonicOf(contents);
		assertTrue(monophonicPattern instanceof MonophonicPattern, "Created pattern was of incorrect class");
		return monophonicPattern;
	}

	@Test
	void testCreatingMonophonicPatternFromListOfDurationals() {
		try {
			createMonophonicPattern(null);
			fail("Was able to create pattern with null contents");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		final List<Durational> notes = new ArrayList<>();

		try {
			createMonophonicPattern(notes);
			fail("Was able to create pattern with empty contents");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		final List<Durational> chordList = new ArrayList<>();
		chordList.add(Chord.of(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT),
				Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT)));

		try {
			createMonophonicPattern(chordList);
			fail("Was able to create pattern with Chord in contents");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	void testImmutability() {
		List<Durational> contents = new ArrayList<>(referenceNotes);

		final Pattern pattern = createMonophonicPattern(contents);
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
	void testEquals() {
		final Pattern pattern1 = createMonophonicPattern(this.referenceNotes);
		final Pattern pattern2 = createMonophonicPattern(this.referenceNotes);

		assertEquals(pattern1, pattern1);

		assertEquals(pattern1, pattern2);
		assertEquals(pattern2, pattern1);

		final List<Durational> modifiedNotes = new ArrayList<>(this.referenceNotes);
		modifiedNotes.add(Rest.of(Durations.QUARTER));

		assertFalse(pattern1.equals(createMonophonicPattern(modifiedNotes)));
	}

	@Test
	void testIsMonophonic() {
		final Pattern pattern = createMonophonicPattern(this.referenceNotes);
		assertTrue(pattern.isMonophonic());
	}

	@Test
	void testEqualsInPitch() {
		final Pattern pattern = createMonophonicPattern(this.referenceNotes);

		final List<Durational> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		notes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.WHOLE));

		assertTrue(pattern.equalsInPitch(createMonophonicPattern(notes)));

		notes.add(Rest.of(Durations.QUARTER));

		assertTrue(pattern.equalsInPitch(createMonophonicPattern(notes)),
				"Adding rest to end of pattern should not make pattern inequal in pitches");

		notes.set(1, Note.of(Pitch.of(Pitch.Base.E, -1, 3), Durations.WHOLE));
		assertFalse(pattern.equalsInPitch(createMonophonicPattern(notes)));

		List<Durational> referenceNotesWithAddition = new ArrayList<>(referenceNotes);
		referenceNotesWithAddition.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));
		assertFalse(pattern.equalsInPitch(createMonophonicPattern(referenceNotesWithAddition)));
	}

	@Test
	void testEqualsEnharmonically() {
		final Pattern pattern = createMonophonicPattern(this.referenceNotes);

		assertTrue(pattern.equalsEnharmonically(createMonophonicPattern(referenceNotes)));

		final List<Durational> samePitchesWithDifferentDurations = new ArrayList<>();
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		samePitchesWithDifferentDurations.add(Rest.of(Durations.QUARTER));
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		samePitchesWithDifferentDurations.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.WHOLE));

		assertTrue(pattern.equalsEnharmonically(createMonophonicPattern(samePitchesWithDifferentDurations)),
				"Difference in durations affected enharmonic equality when it shouldn't have affected it.");

		final List<Durational> samePitchesWithDifferentSpellings = new ArrayList<>();
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT));
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.D, -2, 5), Durations.EIGHT));
		samePitchesWithDifferentSpellings.add(Rest.of(Durations.QUARTER));
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		samePitchesWithDifferentSpellings.add(Note.of(Pitch.of(Pitch.Base.A, 1, 3), Durations.QUARTER));

		assertTrue(pattern.equalsEnharmonically(createMonophonicPattern(samePitchesWithDifferentSpellings)),
				"Difference in note spellings affected enharmonic equality when it shouldn't have affected it.");

		final List<Durational> notesWithAdditionalRestAtEnd = new ArrayList<>(referenceNotes);
		notesWithAdditionalRestAtEnd.add(Rest.of(Durations.QUARTER));

		assertTrue(pattern.equalsEnharmonically(createMonophonicPattern(notesWithAdditionalRestAtEnd)),
				"Adding rest to end of pattern should not make patterns unequal enharmonically.");

		final List<Durational> enharmonicallyUnequalPitches = new ArrayList<>();
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT));
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.D, 0, 5), Durations.EIGHT));
		enharmonicallyUnequalPitches.add(Rest.of(Durations.QUARTER));
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		enharmonicallyUnequalPitches.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsEnharmonically(createMonophonicPattern(enharmonicallyUnequalPitches)));
	}

	@Disabled("These tests are unfinished and should be unignored once the logic is implemented")
	@Test
	void testEqualsInTransposedPitch() {
		// TODO
	}

	@Test
	void testEqualsInDurations() {
		final Pattern pattern = createMonophonicPattern(referenceNotes);
		assertTrue(pattern.equalsInDurations(createMonophonicPattern(referenceNotes)));

		List<Durational> withSameDurationsButDifferentPitches = new ArrayList<>();
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHT));
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT));
		withSameDurationsButDifferentPitches.add(Rest.of(Durations.QUARTER));
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.D, -1, 4), Durations.QUARTER));
		withSameDurationsButDifferentPitches.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertTrue(pattern.equalsInDurations(createMonophonicPattern(withSameDurationsButDifferentPitches)));

		List<Durational> withDifferentNoteDurations = new ArrayList<>();
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT));
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		withDifferentNoteDurations.add(Rest.of(Durations.QUARTER));
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		withDifferentNoteDurations.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsInDurations(createMonophonicPattern(withDifferentNoteDurations)));

		List<Durational> withDifferentRestDurations = new ArrayList<>();
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT));
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT));
		withDifferentRestDurations.add(Rest.of(Durations.EIGHT));
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		withDifferentRestDurations.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsInDurations(createMonophonicPattern(withDifferentRestDurations)));

		final List<Durational> withRestInAPlaceWhereReferenceHasANote = new ArrayList<>();
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT));
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHT));
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		withRestInAPlaceWhereReferenceHasANote.add(Rest.of(Durations.QUARTER));
		withRestInAPlaceWhereReferenceHasANote.add(Note.of(Pitch.of(Pitch.Base.B, -1, 3), Durations.QUARTER));

		assertFalse(pattern.equalsInDurations(createMonophonicPattern(withRestInAPlaceWhereReferenceHasANote)));

		final List<Durational> withAddedDurationAtEnd = new ArrayList<>(referenceNotes);
		withAddedDurationAtEnd.add(Rest.of(Durations.SIXTEENTH));

		assertFalse(pattern.equalsInDurations(createMonophonicPattern(withAddedDurationAtEnd)));
	}
}
