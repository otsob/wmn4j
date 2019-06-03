package org.wmn4j.mir;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatternBuilderTest {

	@Test
	void testGivenMonophonicContentsWhenBuiltMonophonicPatternIsCreated() {
		final PatternBuilder builder = new PatternBuilder();

		final Note firstElement = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT);
		builder.add(firstElement);

		final Note secondElement = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT);
		builder.add(secondElement);

		final Rest thirdElement = Rest.of(Durations.QUARTER);
		builder.add(thirdElement);

		final Note fourthElement = Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.SIXTEENTH);
		builder.add(fourthElement);

		final Rest fifthElement = Rest.of(Durations.EIGHT);
		builder.add(fifthElement);

		final Note sixthElement = Note.of(Pitch.of(Pitch.Base.B, -1, 4), Durations.SIXTEENTH);
		builder.add(sixthElement);

		final Pattern pattern = builder.build();

		final List<Durational> patternContents = pattern.getContents();

		assertEquals(firstElement, patternContents.get(0));
		assertEquals(secondElement, patternContents.get(1));
		assertEquals(thirdElement, patternContents.get(2));
		assertEquals(fourthElement, patternContents.get(3));
		assertEquals(fifthElement, patternContents.get(4));
		assertEquals(sixthElement, patternContents.get(5));
	}

}
