/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
						Collections.emptyList(), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		final GraceNote graceNoteCCopy = GraceNote
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		final GraceNote graceNoteA = GraceNote
				.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		final GraceNote acciaccaturaA = GraceNote
				.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.QUARTER, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.ACCIACCATURA);

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
				Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), Ornamental.Type.GRACE_NOTE);

		final GraceNote withoutArticulation = GraceNote.of(pitch, Durations.QUARTER, Collections.emptySet(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), Ornamental.Type.GRACE_NOTE);

		assertTrue(withArticulation.hasArticulation(Articulation.STACCATO));
		assertFalse(withoutArticulation.hasArticulation(Articulation.STACCATO));
	}

	@Test
	void testHasArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		final GraceNote withArticulation = GraceNote.of(pitch, Durations.QUARTER, articulations,
				Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), Ornamental.Type.GRACE_NOTE);

		final GraceNote withoutArticulation = GraceNote.of(pitch, Durations.QUARTER, Collections.emptySet(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), Ornamental.Type.GRACE_NOTE);

		assertTrue(withArticulation.hasArticulations());
		assertFalse(withoutArticulation.hasArticulations());
	}

	@Test
	void testGetArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final GraceNote withoutArticulation = GraceNote.of(pitch, Durations.QUARTER, Collections.emptySet(),
				Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), Ornamental.Type.GRACE_NOTE);
		assertTrue(withoutArticulation.getArticulations().isEmpty());

		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		articulations.add(Articulation.TENUTO);
		final GraceNote withArticulation = GraceNote.of(pitch, Durations.QUARTER, articulations,
				Collections.emptyList(), Collections.emptyList(), Collections.emptySet(), Ornamental.Type.GRACE_NOTE);

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
						Collections.emptyList(), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		assertFalse(withoutOrnament.hasOrnaments());
		assertFalse(withoutOrnament.hasOrnament(Ornament.Type.TRILL));
		assertTrue(withoutOrnament.getOrnaments().isEmpty());

		final GraceNote ornamentedGraceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH, Collections.emptySet(),
						Collections.emptyList(), Arrays.asList(Ornament.of(Ornament.Type.MORDENT)),
						Collections.emptySet(),
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
						Collections.emptyList(), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		assertEquals(Durations.EIGHTH, graceNote.getDisplayableDuration());
	}

	@Test
	void testConnectedGraceNotes() {
		final Notation slur = Notation.of(Notation.Type.SLUR);

		Notation.Connection end = Notation.Connection.endOf(slur);
		final Note lastNote = Note
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
						Collections.emptySet(), Arrays.asList(end));

		Notation.Connection secondLast = Notation.Connection.of(slur, lastNote);
		final GraceNote lastGraceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(secondLast), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		Notation.Connection middleConnection = Notation.Connection.of(slur, lastGraceNote);
		final GraceNote middleGraceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 3), Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(middleConnection), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		Notation.Connection beginning = Notation.Connection.beginningOf(slur, middleGraceNote);
		final GraceNote firstGraceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 1), Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(beginning), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		assertTrue(firstGraceNote.beginsNotation(Notation.Type.SLUR));
		assertTrue(firstGraceNote.getConnection(slur).isPresent());
		Notation.Connection slurBeginning = firstGraceNote.getConnection(slur).get();
		assertTrue(slurBeginning.isBeginning());
		assertTrue(slurBeginning.getFollowingNote().isEmpty());
		assertEquals(middleGraceNote, slurBeginning.getFollowingGraceNote().get());

		assertTrue(middleGraceNote.getConnection(slur).isPresent());
		Notation.Connection slurMiddle = middleGraceNote.getConnection(slur).get();
		assertFalse(slurMiddle.isBeginning());
		assertTrue(slurMiddle.getFollowingNote().isEmpty());
		assertEquals(lastGraceNote, slurMiddle.getFollowingGraceNote().get());

		assertTrue(lastGraceNote.getConnection(slur).isPresent());
		Notation.Connection secondLastConnection = lastGraceNote.getConnection(slur).get();
		assertFalse(secondLastConnection.isBeginning());
		assertFalse(secondLastConnection.isEnd());
		assertTrue(secondLastConnection.getFollowingGraceNote().isEmpty());
		assertEquals(lastNote, secondLastConnection.getFollowingNote().get());

		assertTrue(lastNote.getConnection(slur).isPresent());
		assertTrue(lastNote.getConnection(slur).get().isEnd());

		List<GraceNote> connectedGraceNotes = slur.getGraceNotesStartingFrom(firstGraceNote);
		assertEquals(3, connectedGraceNotes.size());
		assertEquals(firstGraceNote, connectedGraceNotes.get(0));
		assertEquals(middleGraceNote, connectedGraceNotes.get(1));
		assertEquals(lastGraceNote, connectedGraceNotes.get(2));
	}
}
