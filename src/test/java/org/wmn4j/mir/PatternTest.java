/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PatternTest {

	@Test
	public void testCreatingPatternFromList() {
		final Note noteC = Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4, Durations.HALF);
		final Note noteD = Note.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4, Durations.HALF);
		final Note noteE = Note.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4, Durations.QUARTER);
		final Note noteF = Note.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4, Durations.QUARTER);

		List<Note> notes = new ArrayList<>();
		notes.add(noteC);
		notes.add(noteD);
		notes.add(noteE);
		notes.add(noteF);

		final Pattern monophonicPattern = Pattern.of(notes);
		assertTrue(monophonicPattern.isMonophonic());

		final Iterable<Durational> contentsOfMonophonicPattern = monophonicPattern
				.getVoice(monophonicPattern.getVoiceNumbers().get(0));

		List<Durational> monophonicPatternVoiceContentsAsList = new ArrayList<>();
		for (Durational durational : contentsOfMonophonicPattern) {
			monophonicPatternVoiceContentsAsList.add(durational);
		}

		assertEquals(notes, monophonicPatternVoiceContentsAsList);

		final List<Durational> contentsOfPolyphonicPattern = new ArrayList<>(notes);
		final Chord chord = Chord.of(notes.subList(0, 2));
		contentsOfPolyphonicPattern.add(chord);

		final Pattern polyphonicPatternWithOneVoice = Pattern.of(contentsOfPolyphonicPattern);
		assertFalse(polyphonicPatternWithOneVoice.isMonophonic());
		assertEquals(1, polyphonicPatternWithOneVoice.getVoiceCount());
		assertEquals(contentsOfPolyphonicPattern,
				polyphonicPatternWithOneVoice.getVoice(polyphonicPatternWithOneVoice.getVoiceNumbers().get(0)));
	}

	@Test
	public void testCreatingPatternFromMap() {
		final Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		List<Durational> voice1 = new ArrayList<>();
		voice1.add(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));
		voice1.add(Rest.of(Durations.QUARTER));
		voice1.add(Note.of(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));

		List<Durational> voice2 = new ArrayList<>();

		voice2.add(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 3), Durations.QUARTER));
		voice2.add(Rest.of(Durations.EIGHTH));
		voice2.add(Note.of(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3), Durations.EIGHTH));

		voices.put(1, voice1);
		voices.put(2, voice2);

		final Pattern patternWithMultipleVoices = Pattern.of(voices);
		assertFalse(patternWithMultipleVoices.isMonophonic());

		assertEquals(2, patternWithMultipleVoices.getVoiceCount());
		final List<Integer> voiceNumbers = patternWithMultipleVoices.getVoiceNumbers();
		assertEquals(voice1, patternWithMultipleVoices.getVoice(voiceNumbers.get(0)));
		assertEquals(voice2, patternWithMultipleVoices.getVoice(voiceNumbers.get(1)));
	}
}
