/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class GraceNoteBuilderTest {

	@Test
	void testBuildingBasicNote() {
		final GraceNoteBuilder builder = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final GraceNote note = builder.build();
		assertFalse(note.hasArticulations());
		assertFalse(note.hasNotations());
		assertEquals(GraceNote.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
						Durations.QUARTER,
						Collections.emptySet(),
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE),
				note);
	}

	@Test
	void testBuildingNoteWithAllAttributes() {
		final GraceNoteBuilder builder = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		builder.setGraceNoteType(Ornamental.Type.ACCIACCATURA);
		builder.addArticulation(Articulation.STACCATO);
		Notation slur = Notation.of(Notation.Type.SLUR);
		builder.connectWith(slur,
				new GraceNoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		builder.addOrnament(Ornament.of(Ornament.Type.MORDENT));

		final GraceNote graceNote = builder.build();
		assertEquals(1, graceNote.getOrnaments().size());
		assertTrue(graceNote.hasOrnament(Ornament.Type.MORDENT));
		assertEquals(1, graceNote.getArticulations().size());
		assertTrue(graceNote.hasArticulation(Articulation.STACCATO));
		assertTrue(graceNote.getConnection(slur).isPresent());
		assertTrue(graceNote.hasNotation(Notation.Type.SLUR));
	}

	@Test
	void testBuildingWitMultipleNotesInSlur() {
		GraceNoteBuilder first = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		GraceNoteBuilder second = new GraceNoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		GraceNoteBuilder third = new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		final GraceNote firstNote = first.build();
		final GraceNote secondNote = second.build();
		final GraceNote thirdNote = third.build();

		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(firstNote.endsNotation(Notation.Type.SLUR));
		assertEquals(secondNote, firstNote.getConnection(slur).get().getFollowingGraceNote().get());

		assertTrue(secondNote.hasNotation(Notation.Type.SLUR));
		assertFalse(secondNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(thirdNote, secondNote.getConnection(slur).get().getFollowingGraceNote().get());

		assertTrue(thirdNote.endsNotation(Notation.Type.SLUR));
		assertTrue(thirdNote.getConnection(slur).get().getFollowingGraceNote().isEmpty());
	}

	@Test
	void testBuildingWithLoopedSlur() {
		GraceNoteBuilder first = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		GraceNoteBuilder second = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		GraceNoteBuilder third = new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		// Create a circular dependency
		third.connectWith(slur, first);

		try {
			first.build();
			fail("Trying to build notes with a circular slur dependency did not result in exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}
}
