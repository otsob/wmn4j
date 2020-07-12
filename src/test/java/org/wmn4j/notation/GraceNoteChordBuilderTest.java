/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GraceNoteChordBuilderTest {

	@Test
	void testGivenGraceNotesThenCorrectGraceNoteChordIsBuilt() {
		final GraceNoteBuilder rootBuilder = new GraceNoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 3),
				Durations.EIGHTH);
		final GraceNoteBuilder thirdBuilder = new GraceNoteBuilder(Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, 3),
				Durations.EIGHTH);
		final GraceNoteBuilder fifthBuilder = new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 3),
				Durations.EIGHTH);

		final GraceNoteChordBuilder builder = new GraceNoteChordBuilder();
		builder.add(fifthBuilder);
		builder.add(thirdBuilder);
		builder.add(rootBuilder);

		final GraceNoteChord graceNoteChord = builder.build();
		final GraceNote root = rootBuilder.build();
		final GraceNote third = thirdBuilder.build();
		final GraceNote fifth = fifthBuilder.build();

		assertEquals(3, graceNoteChord.getNoteCount());
		assertEquals(Durations.EIGHTH, graceNoteChord.getDisplayableDuration());
		assertEquals(root, graceNoteChord.getLowestNote());
		assertEquals(third, graceNoteChord.getNote(1));
		assertEquals(fifth, graceNoteChord.getHighestNote());
	}
}
