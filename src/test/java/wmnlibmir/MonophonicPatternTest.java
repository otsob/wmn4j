/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Rest;

@Ignore("These tests are unfinished and should be unignored once the logic is implemented")
public class MonophonicPatternTest {

	final List<Durational> referenceNotes;

	public MonophonicPatternTest() {
		List<Durational> notes = new ArrayList<>();
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.EIGHT));
		notes.add(Rest.getRest(Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.B, -1, 3), Durations.QUARTER));

		this.referenceNotes = Collections.unmodifiableList(notes);
	}

	@Test
	public void testMonophonicPatternListOfDurational() {
		try {
			new MonophonicPattern(null);
			fail("Was able to create pattern with null contents");
		} catch (Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		List<Durational> notes = new ArrayList<>();

		try {
			new MonophonicPattern(notes);
			fail("Was able to create pattern with empty contents");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		List<Durational> chordList = new ArrayList<>();
		chordList.add(Chord.getChord(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT),
				Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT)));

		try {
			new MonophonicPattern(chordList);
			fail("Was able to create pattern with Chord in contents");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}
	}

	@Test
	public void testEqualsPattern() {
		MonophonicPattern pattern1 = new MonophonicPattern(this.referenceNotes);
		MonophonicPattern pattern2 = new MonophonicPattern(this.referenceNotes);

		assertEquals(pattern1, pattern2);

		List<Durational> modifiedNotes = new ArrayList<>(this.referenceNotes);
		modifiedNotes.add(Rest.getRest(Durations.QUARTER));

		assertFalse(pattern1.equals(new MonophonicPattern(modifiedNotes)));
	}

	@Test
	public void testEqualsInPitch() {
		MonophonicPattern pattern1 = new MonophonicPattern(this.referenceNotes);

		List<Durational> notes = new ArrayList<>();
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		notes.add(Rest.getRest(Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.B, -1, 3), Durations.WHOLE));

		assertTrue(pattern1.equalsInPitch(new MonophonicPattern(notes)));

		notes.add(Rest.getRest(Durations.QUARTER));

		assertTrue("Adding rest to end of pattern should not make pattern inequal in pitches",
				pattern1.equalsInPitch(new MonophonicPattern(notes)));

		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.E, -1, 3), Durations.WHOLE));

		assertFalse(pattern1.equalsInPitch(new MonophonicPattern(notes)));
	}

	@Test
	public void testEqualsEnharmonicallyInPitch() {
		MonophonicPattern pattern1 = new MonophonicPattern(this.referenceNotes);

		List<Durational> notes = new ArrayList<>();
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		notes.add(Rest.getRest(Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER));
		notes.add(Note.getNote(Pitch.getPitch(Pitch.Base.A, 1, 3), Durations.WHOLE));

		assertTrue(pattern1.equalsEnharmonicallyInPitch(new MonophonicPattern(notes)));
	}

	@Test
	public void testEqualsInTransposedPitch() {
		// TODO
	}

	@Test
	public void testEqualsInRhythm() {
		// TODO
	}

	@Test
	public void testEqualsInOnsets() {
		// TODO
	}
}
