package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OrnamentTest {
	@Test
	void testGivenOrnamentGetTypeReturnsCorrectType() {
		assertEquals(Ornament.Type.MORDENT, Ornament.of(Ornament.Type.MORDENT).getType());
		assertEquals(Ornament.Type.TRILL, Ornament.of(Ornament.Type.TRILL).getType());
	}

	@Test
	void testGraceNotes() {
		final GraceNote graceNoteC = GraceNote
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		Ornament graceNote = Ornament.graceNotes(Arrays.asList(graceNoteC));
		assertEquals(Ornament.Type.GRACE_NOTES, graceNote.getType());
		assertEquals(1, graceNote.getOrnamentalNotes().size());
		assertTrue(graceNote.getOrnamentalNotes().contains(graceNoteC));
	}
}
