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
		chordList.add(Chord.of(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT),
				Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT)));

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

		final MonophonicPattern pattern = new MonophonicPattern(contents);
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
		final MonophonicPattern pattern1 = new MonophonicPattern(this.referenceNotes);
		final MonophonicPattern pattern2 = new MonophonicPattern(this.referenceNotes);

		assertEquals(pattern1, pattern1);

		assertEquals(pattern1, pattern2);
		assertEquals(pattern2, pattern1);

		final List<Durational> modifiedNotes = new ArrayList<>(this.referenceNotes);
		modifiedNotes.add(Rest.of(Durations.QUARTER));

		assertFalse(pattern1.equals(new MonophonicPattern(modifiedNotes)));
	}

	@Test
	void testIsMonophonic() {
		final MonophonicPattern pattern = new MonophonicPattern(this.referenceNotes);
		assertTrue(pattern.isMonophonic());
	}

	@Test
	void testEqualsInPitch() {
		final MonophonicPattern pattern = new MonophonicPattern(this.referenceNotes);

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

	@Disabled("These tests are unfinished and should be unignored once the logic is implemented")
	@Test
	void testEqualsEnharmonically() {
		final MonophonicPattern pattern1 = new MonophonicPattern(this.referenceNotes);

		final List<Durational> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		notes.add(Rest.of(Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.A, 1, 3), Durations.WHOLE));

		assertTrue(pattern1.equalsEnharmonically(new MonophonicPattern(notes)));
	}

	@Disabled("These tests are unfinished and should be unignored once the logic is implemented")
	@Test
	void testEqualsInTransposedPitch() {
		// TODO
	}

	@Disabled("These tests are unfinished and should be unignored once the logic is implemented")
	@Test
	void testEqualsInRhythm() {
		// TODO
	}

	@Disabled("These tests are unfinished and should be unignored once the logic is implemented")
	@Test
	void testEqualsInOnsets() {
		// TODO
	}
}
