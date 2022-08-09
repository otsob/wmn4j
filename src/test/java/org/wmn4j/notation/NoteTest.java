/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class NoteTest {

	@Test
	void testEqualWithoutOrnaments() {
		final Note A1 = Note.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1, Durations.QUARTER);
		final Note A1differentDur = Note.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1, Durations.EIGHTH);
		final Note A1Copy = Note.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1, Durations.QUARTER);
		final Note B1 = Note.of(Pitch.Base.B, Pitch.Accidental.NATURAL, 1, Durations.QUARTER);
		final Note Asharp1 = Note.of(Pitch.Base.A, Pitch.Accidental.SHARP, 1, Durations.QUARTER);
		final Note C4 = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

		assertTrue(A1.equals(A1));
		assertTrue(A1.equals(A1Copy));
		assertTrue(A1Copy.equals(A1));
		assertFalse(A1.equals(A1differentDur));
		assertFalse(A1.equals(B1));
		assertFalse(A1.equals(Asharp1));
		assertTrue(C4.equals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER)));

		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final HashSet<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		final Note note1 = Note.of(pitch, Durations.EIGHTH, articulations);
		articulations.add(Articulation.TENUTO);
		final Note note2 = Note.of(pitch, Durations.EIGHTH, articulations);
		final Note note3 = Note.of(pitch, Durations.EIGHTH, articulations);

		assertFalse(note1.equals(Note.of(pitch, Durations.EIGHTH)));
		assertFalse(note1.equals(note2));
		assertTrue(note2.equals(note2));
		assertTrue(note2.equals(note3));
	}

	@Test
	void testEqualsWithNotationsArticulationsAndOrnaments() {
		NoteBuilder builderA = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderB = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderC = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderD = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);

		builderA.addArticulation(Articulation.STACCATISSIMO);
		builderA.connectWith(Notation.of(Notation.Type.SLUR),
				new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER));
		builderA.addOrnament(Ornament.of(Ornament.Type.MORDENT));

		builderB.addArticulation(Articulation.STACCATISSIMO);
		builderB.connectWith(Notation.of(Notation.Type.SLUR),
				new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER));
		builderB.addOrnament(Ornament.of(Ornament.Type.MORDENT));

		builderC.addArticulation(Articulation.STACCATISSIMO);
		builderC.connectWith(Notation.of(Notation.Type.GLISSANDO),
				new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER));
		builderC.addOrnament(Ornament.of(Ornament.Type.MORDENT));

		builderD.addArticulation(Articulation.STACCATISSIMO);
		builderD.connectWith(Notation.of(Notation.Type.SLUR),
				new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER));
		builderD.addOrnament(Ornament.of(Ornament.Type.TRILL));

		final Note noteA = builderA.build();
		final Note noteB = builderB.build();
		final Note noteC = builderC.build();
		final Note noteD = builderD.build();

		assertEquals(noteA, noteB);
		assertNotEquals(noteA, noteC);
		assertNotEquals(noteA, noteD);
	}

	@Test
	void testEqualsWithGraceNotes() {
		NoteBuilder builderA = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderB = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderC = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderWithoutGraceNotes = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1),
				Durations.QUARTER);

		GraceNoteBuilder graceNoteBuilderA = new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1),
				Durations.QUARTER);
		GraceNoteBuilder graceNoteBuilderB = new GraceNoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1),
				Durations.QUARTER);
		GraceNoteBuilder graceNoteBuilderC = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1),
				Durations.QUARTER);

		builderA.setPrecedingGraceNotes(Arrays.asList(graceNoteBuilderA));
		builderB.setPrecedingGraceNotes(Arrays.asList(graceNoteBuilderB));
		builderC.setPrecedingGraceNotes(Arrays.asList(graceNoteBuilderC));

		final Note noteA = builderA.build();
		final Note noteB = builderB.build();
		final Note noteC = builderC.build();
		final Note noteWithoutGraceNotes = builderWithoutGraceNotes.build();

		assertEquals(noteA, noteB);
		assertNotEquals(noteA, noteC);
		assertNotEquals(noteA, noteWithoutGraceNotes);
	}

	@Test
	void testEqualsWithAllArticulationsAndOrnaments() {
		NoteBuilder builderA = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderB = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);
		NoteBuilder builderC = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER);

		builderA.addArticulation(Articulation.STACCATISSIMO);
		builderA.connectWith(Notation.of(Notation.Type.SLUR),
				new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER));
		builderA.addOrnament(Ornament.of(Ornament.Type.MORDENT));

		builderB.addArticulation(Articulation.STACCATISSIMO);
		builderB.connectWith(Notation.of(Notation.Type.SLUR),
				new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER));
		builderB.addOrnament(Ornament.of(Ornament.Type.MORDENT));

		builderC.addArticulation(Articulation.STACCATISSIMO);
		builderC.connectWith(Notation.of(Notation.Type.GLISSANDO),
				new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 1), Durations.QUARTER));
		builderC.addOrnament(Ornament.of(Ornament.Type.MORDENT));

		final Note noteA = builderA.build();
		final Note noteB = builderB.build();
		final Note noteC = builderC.build();

		assertEquals(noteA, noteB);
		assertNotEquals(noteA, noteC);
	}

	@Test
	void testCreatingInvalidNote() {

		try {
			Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 11, Durations.QUARTER);
			fail("No exception was thrown. Expected: IllegalArgumentException");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			Note.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1, null);
			fail("No exception was thrown. Expected: IllegalArgumentException");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	@Test
	void testHasArticulation() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		assertTrue(Note.of(pitch, Durations.EIGHTH, articulations).hasArticulation(Articulation.STACCATO));
		assertFalse(Note.of(pitch, Durations.EIGHTH).hasArticulation(Articulation.STACCATO));
	}

	@Test
	void testHasArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		final HashSet<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		assertTrue(Note.of(pitch, Durations.EIGHTH, articulations).hasArticulations());
		assertFalse(Note.of(pitch, Durations.EIGHTH).hasArticulations());
	}

	@Test
	void testGetArticulations() {
		final Pitch pitch = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 1);
		assertTrue(Note.of(pitch, Durations.EIGHTH).getArticulations().isEmpty());

		final Set<Articulation> articulations = new HashSet<>();
		articulations.add(Articulation.STACCATO);
		articulations.add(Articulation.TENUTO);
		final Note note = Note.of(pitch, Durations.EIGHTH, articulations);

		final Collection<Articulation> artic = note.getArticulations();
		assertEquals(2, artic.size());
		assertTrue(artic.contains(Articulation.STACCATO));
		assertTrue(artic.contains(Articulation.TENUTO));
		try {
			artic.remove(Articulation.STACCATO);
			fail("Removing articulation succeeded, immutability violated");
		} catch (final Exception e) {
			/* Do nothing */
		}

		assertTrue(note.hasArticulation(Articulation.STACCATO));
	}

	@Test
	void testTies() {
		final NoteBuilder firstBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final NoteBuilder secondBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.EIGHTH);
		firstBuilder.addTieToFollowing(secondBuilder);

		final Note secondNote = secondBuilder.build();
		assertTrue(secondNote.isTied());
		assertTrue(!secondNote.getFollowingTiedNote().isPresent());
		assertTrue(secondNote.isTiedFromPrevious());

		final Note firstNote = firstBuilder.build();
		assertTrue(firstNote.isTied());
		assertTrue(firstNote.getFollowingTiedNote() != null);
		assertFalse(firstNote.isTiedFromPrevious());

	}

	@Test
	void testTiedDuration() {
		final Note untied = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		assertEquals(Durations.QUARTER, untied.getTiedDuration());

		final NoteBuilder firstBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final NoteBuilder secondBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.EIGHTH);
		firstBuilder.addTieToFollowing(secondBuilder);

		Note firstNote = firstBuilder.build();
		Note secondNote = secondBuilder.build();

		assertEquals(Durations.QUARTER.addDot(), firstNote.getTiedDuration());
		assertEquals(Durations.EIGHTH, secondNote.getTiedDuration());

		firstBuilder.clearCache();
		secondBuilder.clearCache();

		final NoteBuilder thirdBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.EIGHTH);
		secondBuilder.addTieToFollowing(thirdBuilder);

		firstNote = firstBuilder.build();
		secondNote = secondBuilder.build();
		final Note thirdNote = thirdBuilder.build();

		assertEquals(Durations.HALF, firstNote.getTiedDuration());
		assertEquals(Durations.QUARTER, secondNote.getTiedDuration());
		assertEquals(Durations.EIGHTH, thirdNote.getTiedDuration());
	}

	@Test
	void testBeginsAndEndsNotation() {
		final Note followingNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);

		Notation.Connection slurBeginning = Notation.Connection
				.beginningOf(Notation.of(Notation.Type.SLUR), followingNote);
		Notation.Connection glissandoEnd = Notation.Connection.endOf(Notation.of(Notation.Type.GLISSANDO));
		List<Notation.Connection> notationConnections = new ArrayList<>();
		notationConnections.add(slurBeginning);
		notationConnections.add(glissandoEnd);

		final Note noteWithNotationConnections = Note
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
						Collections.emptySet(), notationConnections);

		assertTrue(noteWithNotationConnections.beginsNotation(Notation.Type.SLUR));
		assertTrue(noteWithNotationConnections.endsNotation(Notation.Type.GLISSANDO));

		assertFalse(noteWithNotationConnections.beginsNotation(Notation.Type.GLISSANDO));
		assertFalse(noteWithNotationConnections.endsNotation(Notation.Type.SLUR));
	}

	@Test
	void testHasNotationConnection() {
		final Note followingNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
		Notation.Connection slurBeginning = Notation.Connection
				.beginningOf(Notation.of(Notation.Type.SLUR), followingNote);
		List<Notation.Connection> notationConnections = new ArrayList<>();
		notationConnections.add(slurBeginning);

		final Note noteThatBeginsSlur = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), notationConnections);

		assertTrue(noteThatBeginsSlur.hasNotations());
		assertFalse(followingNote.hasNotations());
		assertTrue(noteThatBeginsSlur.hasNotation(Notation.Type.SLUR));
		assertFalse(noteThatBeginsSlur.hasNotation(Notation.Type.GLISSANDO));
	}

	@Test
	void testEqualsAndHashCodeWithNotations() {
		final Note followingNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);

		Notation.Connection slurBeginning = Notation.Connection
				.beginningOf(Notation.of(Notation.Type.SLUR), followingNote);
		Notation.Connection glissandoEnd = Notation.Connection.endOf(Notation.of(Notation.Type.GLISSANDO));
		List<Notation.Connection> notationConnections = new ArrayList<>();

		notationConnections.add(slurBeginning);
		final Note noteThatBeginsSlur = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), notationConnections);

		Notation.Connection slurEnd = Notation.Connection.endOf(Notation.of(Notation.Type.SLUR));
		List<Notation.Connection> slurEndList = new ArrayList<>();
		slurEndList.add(slurEnd);
		final Note noteThatEndsSlur = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), slurEndList);

		notationConnections.add(glissandoEnd);
		final Note noteWithNotationConnections = Note
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
						Collections.emptySet(), notationConnections);

		assertEquals(noteThatBeginsSlur, noteThatEndsSlur);
		assertEquals(noteThatBeginsSlur.hashCode(), noteThatEndsSlur.hashCode());

		assertFalse(noteThatBeginsSlur.equals(followingNote));
		assertFalse(noteWithNotationConnections.equals(followingNote));
		assertFalse(noteWithNotationConnections.equals(noteThatBeginsSlur));
	}

	@Test
	void testGetNotationConnections() {
		final Note followingNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);

		Notation.Connection slurBeginning = Notation.Connection
				.beginningOf(Notation.of(Notation.Type.SLUR), followingNote);
		Notation.Connection glissandoEnd = Notation.Connection.endOf(Notation.of(Notation.Type.GLISSANDO));
		List<Notation.Connection> notationConnections = new ArrayList<>();
		notationConnections.add(slurBeginning);
		notationConnections.add(glissandoEnd);

		final Note noteWithNotationConnections = Note
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
						Collections.emptySet(), notationConnections);

		final Collection<Notation> notationsInNote = noteWithNotationConnections.getNotations();
		assertEquals(2, notationsInNote.size());
		assertTrue(notationsInNote.contains(slurBeginning.getNotation()));
		assertTrue(notationsInNote.contains(glissandoEnd.getNotation()));

		try {
			notationsInNote.add(Notation.of(Notation.Type.GLISSANDO));
			fail("No exception was thrown when trying to add to notations");
		} catch (Exception e) {
			/* Do nothing */
		}

		assertEquals(2, noteWithNotationConnections.getNotations().size());
	}

	@Test
	void testGetNotationConnection() {
		final Note followingNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
		Notation.Connection slurBeginning = Notation.Connection
				.beginningOf(Notation.of(Notation.Type.SLUR), followingNote);
		List<Notation.Connection> notationConnections = new ArrayList<>();
		notationConnections.add(slurBeginning);

		final Note noteThatBeginsSlur = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH,
				Collections.emptySet(), notationConnections);

		Optional<Notation.Connection> slurBeginningOptional = noteThatBeginsSlur
				.getConnection(slurBeginning.getNotation());
		assertTrue(slurBeginningOptional.isPresent());
		assertEquals(slurBeginning, slurBeginningOptional.get());

		assertFalse(noteThatBeginsSlur.getConnection(Notation.of(Notation.Type.GLISSANDO)).isPresent());
	}

	@Test
	void testGetOrnaments() {
		final Note note = Note
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), Durations.EIGHTH);

		assertFalse(note.hasOrnaments());
		assertFalse(note.hasOrnament(Ornament.Type.TRILL));
		assertTrue(note.getOrnaments().isEmpty());

		final Note ornamentedNote = Note
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 5), null, Durations.EIGHTH,
						Collections.emptySet(),
						Collections.emptyList(), Arrays.asList(Ornament.of(Ornament.Type.MORDENT)),
						Collections.emptySet(),
						null);

		assertTrue(ornamentedNote.hasOrnaments());
		assertTrue(ornamentedNote.hasOrnament(Ornament.Type.MORDENT));
		assertFalse(ornamentedNote.hasOrnament(Ornament.Type.TRILL));
		assertEquals(1, ornamentedNote.getOrnaments().size());
	}

	@Test
	void testGivenNoteConnectedToGraceNotesThenConnectionsAreCorrectInNote() {
		final Notation slur = Notation.of(Notation.Type.SLUR);

		Notation.Connection endOfSlur = Notation.Connection.endOf(slur);

		final GraceNote graceNoteAfter = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(endOfSlur), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		final Ornament succeedingGraceNotes = Ornament
				.succeedingGraceNotes(Arrays.asList(graceNoteAfter));

		final GraceNote middleGraceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 3), null, Durations.EIGHTH, Collections.emptySet(),
						Collections.emptyList(), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE,
						Arrays.asList(Notation.Connection.of(slur, Note.of(
								Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH))), null);

		Notation.Connection beginning = Notation.Connection.beginningOf(slur, middleGraceNote);
		final GraceNote firstGraceNote = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 1), Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(beginning), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.GRACE_NOTE);

		final Ornament graceNotes = Ornament
				.graceNotes(Arrays.asList(firstGraceNote, middleGraceNote));

		final Note note = Note
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), null, Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(Notation.Connection.of(slur, graceNoteAfter)),
						Arrays.asList(succeedingGraceNotes, graceNotes),
						Collections.emptySet(),
						null);

		assertEquals(2, note.getOrnaments().size());
		final Ornament graceNotesBefore = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(Ornament.Type.GRACE_NOTES)).findFirst().get();

		assertEquals(2, graceNotesBefore.getOrnamentalNotes().size());
		final GraceNote first = (GraceNote) graceNotesBefore.getOrnamentalNotes().get(0);
		assertEquals(firstGraceNote, first);
		final GraceNote secondInSlur = first.getConnection(slur).get().getFollowingGraceNote().get();
		assertEquals(middleGraceNote.getPitch().get(), secondInSlur.getPitch().get());

		final GraceNote second = (GraceNote) graceNotesBefore.getOrnamentalNotes().get(1);
		assertEquals(middleGraceNote.getPitch().get(), second.getPitch().get());
		assertEquals(note, second.getConnection(slur).get().getFollowingNote().get());

		assertEquals(graceNoteAfter, note.getConnection(slur).get().getFollowingGraceNote().get());

		final Ornament graceNotesAfter = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(Ornament.Type.SUCCEEDING_GRACE_NOTES)).findFirst().get();

		assertEquals(1, graceNotesAfter.getOrnamentalNotes().size());
		assertEquals(graceNoteAfter, graceNotesAfter.getOrnamentalNotes().get(0));
		assertTrue(graceNoteAfter.endsNotation(Notation.Type.SLUR));
	}

	@Test
	void testGivenGraceNoteChordsWhenNoteIsCreatedThenNotationsAreCorrect() {

		final Notation arpeggiate = Notation.of(Notation.Type.ARPEGGIATE);

		final Notation.Connection arpeggiationEnd = Notation.Connection.endOf(arpeggiate);
		final Notation slur = Notation.of(Notation.Type.SLUR);
		final Notation.Connection slurBegin = Notation.Connection.beginningOf(slur, Note.of(
				Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));
		final GraceNote fifth = GraceNote
				.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 2), null, Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(arpeggiationEnd, slurBegin), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.ACCIACCATURA, Arrays.asList(slurBegin), null);

		final Notation.Connection arpeggiationMiddle = Notation.Connection.of(arpeggiate, fifth);
		final GraceNote third = GraceNote
				.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(arpeggiationMiddle), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.ACCIACCATURA);

		final Notation.Connection arpeggiationBegin = Notation.Connection.beginningOf(arpeggiate, third);
		final GraceNote root = GraceNote
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH, Collections.emptySet(),
						Arrays.asList(arpeggiationBegin), Collections.emptyList(), Collections.emptySet(),
						Ornamental.Type.ACCIACCATURA);

		final GraceNoteChord graceNoteChord = GraceNoteChord.of(Arrays.asList(root, third, fifth));
		final Ornament graceNotes = Ornament.graceNotes(Arrays.asList(graceNoteChord));
		final Note note = Note
				.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), null, Durations.QUARTER,
						Collections.emptySet(),
						Arrays.asList(Notation.Connection.endOf(slur)),
						Arrays.asList(graceNotes),
						Collections.emptySet(),
						null);

		assertEquals(1, note.getOrnaments().size());
		assertTrue(note.hasOrnament(Ornament.Type.GRACE_NOTES));

		Optional<Ornament> graceNotesFromNote = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(
						Ornament.Type.GRACE_NOTES)).findAny();

		assertTrue(graceNotesFromNote.isPresent());
		List<Ornamental> ornamentalNotes = graceNotesFromNote.get().getOrnamentalNotes();
		assertEquals(1, ornamentalNotes.size());
		assertTrue(ornamentalNotes.get(0) instanceof GraceNoteChord);
		final GraceNoteChord graceNoteChordFromNote = (GraceNoteChord) ornamentalNotes.get(0);
		assertEquals(3, graceNoteChordFromNote.getNoteCount());

		final GraceNote highest = graceNoteChordFromNote.getHighest();
		assertEquals(fifth, highest);
		Optional<Notation.Connection> slurToPrincipal = highest.getConnection(slur);
		assertTrue(slurToPrincipal.isPresent());
		final Note connectedTo = slurToPrincipal.get().getFollowingNote().get();
		assertEquals(note, connectedTo);

		assertTrue(graceNoteChordFromNote.getLowest().beginsNotation(Notation.Type.ARPEGGIATE));
		assertTrue(graceNoteChordFromNote.getNote(1).hasNotation(Notation.Type.ARPEGGIATE));
		assertTrue(graceNoteChordFromNote.getHighest().endsNotation(Notation.Type.ARPEGGIATE));
	}

	@Test
	void testGivenNullPitchUnpitchedNoteIsCreated() {
		final Pitch displayPitch = Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4);
		final Note note = Note
				.of(null, displayPitch, Durations.QUARTER,
						Collections.emptySet(),
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptySet(),
						null);

		assertFalse(note.hasPitch());
		assertTrue(note.getPitch().isEmpty());
		assertEquals(displayPitch, note.getDisplayPitch());
	}

	@Test
	void testGivenNullPitchesExceptionIsThrown() {
		assertThrows(NullPointerException.class, () -> Note
				.of(null, null, Durations.QUARTER,
						Collections.emptySet(),
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptySet(),
						null));
	}

	@Test
	void testGivenNoDisplayPitchActualPitchIsUsed() {
		final Pitch pitch = Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4);
		final Note note = Note
				.of(pitch, null, Durations.QUARTER,
						Collections.emptySet(),
						Collections.emptyList(),
						Collections.emptyList(),
						Collections.emptySet(),
						null);

		assertTrue(note.hasPitch());
		assertTrue(note.getPitch().isPresent());
		assertEquals(note.getPitch().get(), note.getDisplayPitch());
		assertTrue(note.getTechniques().isEmpty());
	}
}
