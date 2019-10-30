package org.wmn4j.mir;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PatternBuilderTest {

	@Test
	void testGivenMonophonicContentsWhenBuiltMonophonicPatternIsCreated() {
		final PatternBuilder builder = new PatternBuilder();

		final Note firstElement = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH);
		builder.add(new NoteBuilder(firstElement));

		final Note secondElement = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH);
		builder.add(new NoteBuilder(secondElement));

		final Rest thirdElement = Rest.of(Durations.QUARTER);
		builder.add(new RestBuilder(thirdElement));

		final Note fourthElement = Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.SIXTEENTH);
		builder.add(new NoteBuilder(fourthElement));

		final Rest fifthElement = Rest.of(Durations.EIGHTH);
		builder.add(new RestBuilder(fifthElement));

		final Note sixthElement = Note.of(Pitch.of(Pitch.Base.B, -1, 4), Durations.SIXTEENTH);
		builder.add(new NoteBuilder(sixthElement));

		assertTrue(builder.isMonophonic());
		final Pattern pattern = builder.build();

		final List<Durational> patternContents = pattern.getContents();

		assertEquals(firstElement, patternContents.get(0));
		assertEquals(secondElement, patternContents.get(1));
		assertEquals(thirdElement, patternContents.get(2));
		assertEquals(fourthElement, patternContents.get(3));
		assertEquals(fifthElement, patternContents.get(4));
		assertEquals(sixthElement, patternContents.get(5));

		assertTrue(pattern.isMonophonic());
	}

	@Test
	void testGivenPolyphonicContentsInSingleVoiceWhenBuiltPolyphonicPatternIsCreated() {
		final PatternBuilder builder = new PatternBuilder();

		final Note firstElement = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH);
		builder.add(new NoteBuilder(firstElement));

		final Note secondElement = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH);
		builder.add(new NoteBuilder(secondElement));

		final Rest thirdElement = Rest.of(Durations.QUARTER);
		builder.add(new RestBuilder(thirdElement));

		final Note fourthElement = Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.SIXTEENTH);
		builder.add(new NoteBuilder(fourthElement));

		final Rest fifthElement = Rest.of(Durations.EIGHTH);
		builder.add(new RestBuilder(fifthElement));

		final Chord sixthElement = Chord.of(Note.of(Pitch.of(Pitch.Base.B, -1, 4), Durations.SIXTEENTH),
				Note.of(Pitch.of(Pitch.Base.E, -1, 4), Durations.SIXTEENTH));
		builder.add(new ChordBuilder(sixthElement));

		assertFalse(builder.isMonophonic());
		final Pattern pattern = builder.build();

		final List<Durational> patternContents = pattern.getContents();

		assertEquals(firstElement, patternContents.get(0));
		assertEquals(secondElement, patternContents.get(1));
		assertEquals(thirdElement, patternContents.get(2));
		assertEquals(fourthElement, patternContents.get(3));
		assertEquals(fifthElement, patternContents.get(4));
		assertEquals(sixthElement, patternContents.get(5));

		assertFalse(pattern.isMonophonic());
	}

	@Test
	void testGivenMultipleVoicesWhenBuiltPolyphonicPatternIsCreated() {
		final PatternBuilder builder = new PatternBuilder();

		final int voice1number = 1;
		final Note firstElement = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH);
		builder.addToVoice(new NoteBuilder(firstElement), voice1number);

		final Note secondElement = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH);
		builder.addToVoice(new NoteBuilder(secondElement), voice1number);

		final Rest thirdElement = Rest.of(Durations.QUARTER);
		builder.addToVoice(new RestBuilder(thirdElement), voice1number);

		final int voice2number = 2;
		final Note fourthElement = Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.SIXTEENTH);
		builder.addToVoice(new NoteBuilder(fourthElement), voice2number);

		final Rest fifthElement = Rest.of(Durations.EIGHTH);
		builder.addToVoice(new RestBuilder(fifthElement), voice2number);

		final Note sixthElement = Note.of(Pitch.of(Pitch.Base.B, -1, 4), Durations.SIXTEENTH);
		builder.addToVoice(new NoteBuilder(sixthElement), voice2number);

		final Pattern patternWithTwoVoices = builder.build();
		assertFalse(patternWithTwoVoices.isMonophonic());

		assertEquals(2, patternWithTwoVoices.getVoiceCount());

		List<Durational> voice1 = patternWithTwoVoices.getVoice(voice1number);
		assertEquals(3, voice1.size());
		assertEquals(firstElement, voice1.get(0));
		assertEquals(secondElement, voice1.get(1));
		assertEquals(thirdElement, voice1.get(2));

		List<Durational> voice2 = patternWithTwoVoices.getVoice(voice2number);
		assertEquals(3, voice2.size());
		assertEquals(fourthElement, voice2.get(0));
		assertEquals(fifthElement, voice2.get(1));
		assertEquals(sixthElement, voice2.get(2));
	}

	@Test
	void testSettingName() {
		final PatternBuilder builder = new PatternBuilder();

		final Note firstElement = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH);
		builder.add(new NoteBuilder(firstElement));

		final String patternName = "Pattern A";
		builder.setName(patternName);

		final Pattern pattern = builder.build();
		assertEquals(patternName, pattern.getName().get());
	}

	@Test
	void testAddingLabels() {
		final PatternBuilder builder = new PatternBuilder();

		final Note firstElement = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHTH);
		builder.add(new NoteBuilder(firstElement));

		String labelA = "LabelA";
		String labelB = "LabelB";

		builder.addLabel(labelA);
		builder.addLabel(labelB);

		final Pattern patternWithLabels = builder.build();
		assertEquals(2, patternWithLabels.getLabels().size());
		assertTrue(patternWithLabels.getLabels().contains(labelA));
		assertTrue(patternWithLabels.getLabels().contains(labelB));
	}
}
