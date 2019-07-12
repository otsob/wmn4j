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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PolyphonicPatternTest {

	private Map<Integer, List<? extends Durational>> createReferencePatternVoices() {
		final Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		List<Durational> voice1 = new ArrayList<>();
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		voice1.add(Rest.of(Durations.QUARTER));
		voice1.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));

		List<Durational> voice2 = new ArrayList<>();

		voice2.add(Note.of(Pitch.of(Pitch.Base.E, 0, 3), Durations.QUARTER));
		voice2.add(Rest.of(Durations.EIGHTH));
		voice2.add(Note.of(Pitch.of(Pitch.Base.F, 1, 3), Durations.EIGHTH));

		voices.put(1, voice1);
		voices.put(2, voice2);

		return voices;
	}

	@Test
	void testCreatingPatternWithInvalidContentsPossible() {
		List<Durational> nullContents = null;
		Map<Integer, List<? extends Durational>> nullVoices = null;

		assertThrows(NullPointerException.class, () -> new PolyphonicPattern(nullContents),
				"Did not throw NullPointerException when trying to create polyphonic pattern with null contents");

		assertThrows(NullPointerException.class, () -> new PolyphonicPattern(nullVoices),
				"Did not throw NullPointerException when trying to create polyphonic pattern with null voices");

		assertThrows(IllegalArgumentException.class, () -> new PolyphonicPattern(Collections.emptyMap()),
				"Did not throw IllegalArgumentException when trying to create polyphonic pattern with empty voices");

		List<Note> notes = new ArrayList<>();
		notes.add(Note.of(Pitch.of(Pitch.Base.B, 0, 3), Durations.QUARTER));
		notes.add(Note.of(Pitch.of(Pitch.Base.C, 0, 3), Durations.QUARTER));

		Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		voices.put(1, notes);

		assertThrows(IllegalArgumentException.class, () -> new PolyphonicPattern(voices),
				"Did not throw IllegalArgumentException when trying to create polyphonic pattern with monophonic contents");
	}

	@Test
	void testIsMonophonicReturnsFalseForMultiVoicePattern() {
		final Pattern polyphonicPattern = new PolyphonicPattern(createReferencePatternVoices());
		assertFalse(polyphonicPattern.isMonophonic());
	}

	@Test
	void testIsMonophonicReturnsFalseForPatternWithChords() {
		final Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		List<Durational> voice = new ArrayList<>();
		List<Note> chordContents = new ArrayList<>();
		chordContents.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH));
		chordContents.add(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHTH));
		voice.add(Chord.of(chordContents));
		voice.add(Rest.of(Durations.QUARTER));
		voice.add(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.EIGHTH));
		voices.put(1, voice);

		final Pattern patternWithChord = new PolyphonicPattern(voices);
		assertFalse(patternWithChord.isMonophonic());
	}

}
