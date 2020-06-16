/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class GraceNoteTest {

	@Test
	void testEquals() {
		final GraceNote graceNoteC = GraceNote
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		final GraceNote graceNoteCCopy = GraceNote
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		final GraceNote graceNoteA = GraceNote
				.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		final GraceNote acciaccaturaA = GraceNote
				.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Ornamental.Type.ACCIACCATURA);

		assertEquals(graceNoteC, graceNoteC);
		assertEquals(graceNoteC, graceNoteCCopy);
		assertNotEquals(graceNoteC, graceNoteA);
		assertNotEquals(graceNoteA, acciaccaturaA);
	}

	@Test
	void testHasArticulation() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		final GraceNote withArticulation = GraceNote.of(pitch, Durations.QUARTER, articulations,
				Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		final GraceNote withoutArticulation = GraceNote.of(pitch, Durations.QUARTER, Collections.emptySet(),
				Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		assertTrue(withArticulation.hasArticulation(Articulation.STACCATO));
		assertFalse(withoutArticulation.hasArticulation(Articulation.STACCATO));
	}

	@Test
	void testHasArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		final GraceNote withArticulation = GraceNote.of(pitch, Durations.QUARTER, articulations,
				Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		final GraceNote withoutArticulation = GraceNote.of(pitch, Durations.QUARTER, Collections.emptySet(),
				Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		assertTrue(withArticulation.hasArticulations());
		assertFalse(withoutArticulation.hasArticulations());
	}

	@Test
	void testGetArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final GraceNote withoutArticulation = GraceNote.of(pitch, Durations.QUARTER, Collections.emptySet(),
				Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);
		assertTrue(withoutArticulation.getArticulations().isEmpty());

		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		articulations.add(Articulation.TENUTO);
		final GraceNote withArticulation = GraceNote.of(pitch, Durations.QUARTER, articulations,
				Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		final Collection<Articulation> artic = withArticulation.getArticulations();
		assertEquals(2, artic.size());
		assertTrue(artic.contains(Articulation.STACCATO));
		assertTrue(artic.contains(Articulation.TENUTO));
		try {
			artic.remove(Articulation.STACCATO);
			fail("Removing articulation succeeded, immutability violated");
		} catch (final Exception e) {
			/* Do nothing */
		}

		assertTrue(withArticulation.hasArticulation(Articulation.STACCATO));
	}

	@Test
	void testGetOrnaments() {
		final GraceNote withoutOrnament = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		assertFalse(withoutOrnament.hasOrnaments());
		assertFalse(withoutOrnament.hasOrnament(Ornament.Type.TRILL));
		assertTrue(withoutOrnament.getOrnaments().isEmpty());

		final GraceNote ornamentedGraceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH, Collections.emptySet(),
						Collections.emptyList(), Arrays.asList(Ornament.of(Ornament.Type.MORDENT)),
						Ornamental.Type.GRACE_NOTE);

		assertTrue(ornamentedGraceNote.hasOrnaments());
		assertTrue(ornamentedGraceNote.hasOrnament(Ornament.Type.MORDENT));
		assertFalse(ornamentedGraceNote.hasOrnament(Ornament.Type.TRILL));
		assertEquals(1, ornamentedGraceNote.getOrnaments().size());
	}

	@Test
	void testGetDisplayableDuration() {
		final GraceNote graceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Ornamental.Type.GRACE_NOTE);

		assertEquals(Durations.EIGHTH, graceNote.getDisplayableDuration());
	}
}
