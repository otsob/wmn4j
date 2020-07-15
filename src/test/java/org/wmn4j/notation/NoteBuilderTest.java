/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class NoteBuilderTest {

	@Test
	void testWhenCreatingFromNoteThenCorrectValuesAreSet() {
		final Note noteWithoutArticulations = Note.of(Pitch.Base.A, Pitch.Accidental.FLAT, 2, Duration.of(1, 12));
		NoteBuilder builderWithoutArticulations = new NoteBuilder(noteWithoutArticulations);

		assertEquals(noteWithoutArticulations.getPitch(), builderWithoutArticulations.getPitch());
		assertEquals(noteWithoutArticulations.getDuration(), builderWithoutArticulations.getDuration());
		assertEquals(noteWithoutArticulations.getArticulations(), builderWithoutArticulations.getArticulations());
		assertEquals(noteWithoutArticulations, builderWithoutArticulations.build());

		final Note noteWithArticulations = Note
				.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.SHARP, 3), Duration.of(1, 16),
						EnumSet.of(Articulation.ACCENT, Articulation.STACCATO));
		NoteBuilder builderWithArticulations = new NoteBuilder(noteWithArticulations);

		assertEquals(noteWithArticulations.getPitch(), builderWithArticulations.getPitch());
		assertEquals(noteWithArticulations.getDuration(), builderWithArticulations.getDuration());
		assertEquals(noteWithArticulations.getArticulations(), builderWithArticulations.getArticulations());
		assertEquals(noteWithArticulations, builderWithArticulations.build());
	}

	@Test
	void testBuildingBasicNote() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final Note note = builder.build();
		assertFalse(note.hasArticulations());
		assertFalse(note.hasNotations());
		assertFalse(note.isTied());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER), note);
	}

	@Test
	void testBuildingNoteWithAllAttributes() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);

		builder.connectWith(Notation.of(Notation.Type.SLUR),
				new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.QUARTER));

		final Note tiedNote = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

		builder.addTieToFollowing(new NoteBuilder(tiedNote));

		final Note expected = Note
				.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER,
						EnumSet.of(Articulation.STACCATO));

		final Note note = builder.build();
		assertTrue(expected.equalsInPitchAndDuration(note));
		assertFalse(expected.equals(note));
		assertTrue(note.isTied());
		assertTrue(note.isTiedToFollowing());
		assertFalse(note.isTiedFromPrevious());
		assertTrue(note.beginsNotation(Notation.Type.SLUR));
	}

	@Test
	void testBuildingTiedNotes() {
		final NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		first.addTieToFollowing(second);
		second.addTieToFollowing(third);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), firstNote.getPitch());
		assertTrue(firstNote.isTiedToFollowing());
		assertFalse(firstNote.isTiedFromPrevious());
		assertEquals(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4),
				firstNote.getFollowingTiedNote().get().getPitch());

		assertEquals(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), secondNote.getPitch());
		assertTrue(secondNote.isTiedToFollowing());
		assertTrue(secondNote.isTiedFromPrevious());
		assertEquals(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4),
				secondNote.getFollowingTiedNote().get().getPitch());

		assertEquals(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), thirdNote.getPitch());
		assertFalse(thirdNote.isTiedToFollowing());
		assertTrue(thirdNote.isTiedFromPrevious());
	}

	@Test
	void testCopyConstructor() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		final NoteBuilder copy = new NoteBuilder(builder);
		assertNotSame(builder, copy);

		assertEquals(Durations.QUARTER, copy.getDuration());
		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), copy.getPitch());

		builder.setDuration(Durations.HALF);
		assertEquals(Durations.QUARTER, copy.getDuration());

		builder.setPitch(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4));
		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), copy.getPitch());
	}

	@Test
	void testCopyConstructorArticulations() {
		final NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		builder.addArticulation(Articulation.STACCATO);

		final NoteBuilder copy = new NoteBuilder(builder);

		builder.addArticulation(Articulation.FERMATA);
		assertEquals(1, copy.getArticulations().size());
		assertTrue(copy.getArticulations().contains(Articulation.STACCATO));
	}

	@Test
	void testBuildingWitMultipleNotesInSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(firstNote.endsNotation(Notation.Type.SLUR));
		assertEquals(secondNote, firstNote.getConnection(slur).get().getFollowingNote().get());

		assertTrue(secondNote.hasNotation(Notation.Type.SLUR));
		assertFalse(secondNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(thirdNote, secondNote.getConnection(slur).get().getFollowingNote().get());

		assertTrue(thirdNote.endsNotation(Notation.Type.SLUR));
		assertTrue(thirdNote.getConnection(slur).get().getFollowingNote().isEmpty());
	}

	@Test
	void testBuildingWithMultipleNotesWithTiesInSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		first.addTieToFollowing(second);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(firstNote.endsNotation(Notation.Type.SLUR));
		assertEquals(2, firstNote.getNotations().size());
		assertEquals(secondNote, firstNote.getConnection(slur).get().getFollowingNote().get());

		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());

		assertTrue(secondNote.hasNotation(Notation.Type.SLUR));
		assertEquals(2, secondNote.getNotations().size());
		assertFalse(secondNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(thirdNote, secondNote.getConnection(slur).get().getFollowingNote().get());

		assertFalse(thirdNote.isTied());
		assertEquals(1, thirdNote.getNotations().size());
		assertTrue(thirdNote.endsNotation(Notation.Type.SLUR));
		assertTrue(thirdNote.getConnection(slur).get().getFollowingNote().isEmpty());
	}

	@Test
	void testBuildingWithSlurAndGlissandoAndTie() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		final Notation glissando = Notation.of(Notation.Type.GLISSANDO);
		first.connectWith(slur, second);
		second.connectWith(slur, third);

		first.connectWith(glissando, second);
		second.connectWith(glissando, third);

		first.addTieToFollowing(second);

		final Note firstNote = first.build();
		final Note secondNote = second.build();
		final Note thirdNote = third.build();

		assertTrue(firstNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(firstNote.endsNotation(Notation.Type.SLUR));

		assertTrue(firstNote.beginsNotation(Notation.Type.GLISSANDO));
		assertFalse(firstNote.endsNotation(Notation.Type.GLISSANDO));

		assertEquals(3, firstNote.getNotations().size());
		assertEquals(secondNote, firstNote.getConnection(slur).get().getFollowingNote().get());
		assertEquals(secondNote, firstNote.getConnection(glissando).get().getFollowingNote().get());

		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());

		assertEquals(3, secondNote.getNotations().size());
		assertTrue(secondNote.hasNotation(Notation.Type.SLUR));
		assertFalse(secondNote.beginsNotation(Notation.Type.SLUR));
		assertFalse(secondNote.endsNotation(Notation.Type.SLUR));
		assertEquals(thirdNote, secondNote.getConnection(slur).get().getFollowingNote().get());

		assertTrue(secondNote.hasNotation(Notation.Type.GLISSANDO));
		assertFalse(secondNote.beginsNotation(Notation.Type.GLISSANDO));
		assertFalse(secondNote.endsNotation(Notation.Type.GLISSANDO));
		assertEquals(thirdNote, secondNote.getConnection(glissando).get().getFollowingNote().get());

		assertFalse(thirdNote.isTied());
		assertEquals(2, thirdNote.getNotations().size());

		assertTrue(thirdNote.endsNotation(Notation.Type.SLUR));
		assertTrue(thirdNote.getConnection(slur).get().getFollowingNote().isEmpty());

		assertTrue(thirdNote.endsNotation(Notation.Type.GLISSANDO));
		assertTrue(thirdNote.getConnection(glissando).get().getFollowingNote().isEmpty());
	}

	@Test
	void testBuildingWithLoopedSlur() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

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

	@Test
	void testBuildingWithLoopedTie() {
		NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		NoteBuilder third = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

		first.addTieToFollowing(second);
		second.addTieToFollowing(third);

		// Create a circular dependency
		third.addTieToFollowing(first);

		try {
			first.build();
			fail("Trying to build notes with a circular tie dependency did not result in exception");
		} catch (Exception e) {
			assertTrue(e instanceof IllegalStateException);
		}
	}

	@Test
	void testBuildingWithOrnaments() {
		NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		final Ornament ornament = Ornament.of(Ornament.Type.MORDENT);
		builder.addOrnament(ornament);
		final Note note = builder.build();
		assertTrue(note.hasOrnaments());
		assertEquals(1, note.getOrnaments().size());
		assertTrue(note.hasOrnament(ornament.getType()));
	}

	@Test
	void testBuildingWithSlurToGraceNote() {
		NoteBuilder builder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		GraceNoteBuilder graceNoteBuilder = new GraceNoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		builder.connectWith(slur, graceNoteBuilder);

		final Note note = builder.build();
		final GraceNote graceNote = graceNoteBuilder.build();
		assertTrue(note.beginsNotation(Notation.Type.SLUR));
		assertTrue(graceNote.endsNotation(Notation.Type.SLUR));
		assertTrue(note.getConnection(slur).isPresent());
		assertTrue(graceNote.getConnection(slur).isPresent());
	}

	@Test
	void testGivenPrecedingAndSucceedingGraceNotesWhenBuiltThenAllNotationsAreCorrect() {
		GraceNoteBuilder firstGraceNoteBuilder = new GraceNoteBuilder(
				Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.SIXTEENTH);

		GraceNoteBuilder secondGraceNoteBuilder = new GraceNoteBuilder(
				Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4),
				Durations.SIXTEENTH);

		NoteBuilder noteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		GraceNoteBuilder thirdGraceNoteBuilder = new GraceNoteBuilder(
				Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4),
				Durations.SIXTEENTH);

		GraceNoteBuilder fourthGraceNoteBuilder = new GraceNoteBuilder(
				Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4),
				Durations.SIXTEENTH);

		Notation firstSlur = Notation.of(Notation.Type.SLUR);
		Notation lastSlur = Notation.of(Notation.Type.SLUR);
		Notation glissando = Notation.of(Notation.Type.GLISSANDO);

		firstGraceNoteBuilder.connectWith(firstSlur, secondGraceNoteBuilder);
		secondGraceNoteBuilder.connectWith(firstSlur, noteBuilder);
		secondGraceNoteBuilder.connectWith(glissando, noteBuilder);
		noteBuilder.connectWith(lastSlur, thirdGraceNoteBuilder);
		thirdGraceNoteBuilder.connectWith(lastSlur, fourthGraceNoteBuilder);

		noteBuilder.setPrecedingGraceNotes(Arrays.asList(firstGraceNoteBuilder, secondGraceNoteBuilder));
		noteBuilder.setSucceedingGraceNotes(Arrays.asList(thirdGraceNoteBuilder, fourthGraceNoteBuilder));

		final Note note = noteBuilder.build();

		assertEquals(2, note.getOrnaments().size());
		Optional<Ornament> precedingGraceNotes = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(Ornament.Type.GRACE_NOTES)).findFirst();

		assertTrue(precedingGraceNotes.isPresent());
		List<Ornamental> graceNotes = precedingGraceNotes.get().getOrnamentalNotes();
		assertEquals(2, graceNotes.size());
		GraceNote firstGraceNote = (GraceNote) graceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), firstGraceNote.getPitch());
		GraceNote secondGraceNote = (GraceNote) graceNotes.get(1);
		assertEquals(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), secondGraceNote.getPitch());

		assertTrue(firstGraceNote.beginsNotation(Notation.Type.SLUR));
		Optional<Notation.Connection> firstConnection = firstGraceNote.getConnection(firstSlur);
		assertTrue(firstConnection.isPresent());
		assertTrue(firstConnection.get().getFollowingNote().isEmpty());
		assertEquals(secondGraceNote, firstConnection.get().getFollowingGraceNote().get());

		Optional<Notation.Connection> secondConnection = secondGraceNote.getConnection(firstSlur);
		assertTrue(secondConnection.isPresent());
		assertTrue(secondConnection.get().getFollowingGraceNote().isEmpty());
		assertEquals(note, secondConnection.get().getFollowingNote().get());

		Optional<Notation.Connection> glissandoConnection = secondGraceNote.getConnection(glissando);
		assertTrue(glissandoConnection.isPresent());
		assertTrue(glissandoConnection.get().getFollowingGraceNote().isEmpty());
		assertEquals(note, glissandoConnection.get().getFollowingNote().get());

		assertTrue(note.endsNotation(Notation.Type.SLUR));
		Optional<Notation.Connection> firstSlurEnding = note.getConnection(firstSlur);
		assertTrue(firstSlurEnding.isPresent());
		assertTrue(firstSlurEnding.get().isEnd());
		assertTrue(note.endsNotation(Notation.Type.GLISSANDO));
		Optional<Notation.Connection> glissandoEnding = note.getConnection(glissando);
		assertTrue(glissandoEnding.isPresent());
		assertTrue(glissandoEnding.get().isEnd());

		Optional<Ornament> succeedingGraceNotesOpt = note.getOrnaments().stream()
				.filter(ornament -> ornament.getType().equals(Ornament.Type.SUCCEEDING_GRACE_NOTES)).findFirst();

		assertTrue(succeedingGraceNotesOpt.isPresent());
		List<Ornamental> succeedingGraceNotes = succeedingGraceNotesOpt.get().getOrnamentalNotes();
		assertEquals(2, succeedingGraceNotes.size());
		GraceNote thirdGracenote = (GraceNote) succeedingGraceNotes.get(0);
		assertEquals(Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), thirdGracenote.getPitch());
		GraceNote fourthGraceNote = (GraceNote) succeedingGraceNotes.get(1);
		assertEquals(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), fourthGraceNote.getPitch());

		assertTrue(note.beginsNotation(Notation.Type.SLUR));
		Optional<Notation.Connection> thirdConnection = note.getConnection(lastSlur);
		assertTrue(thirdConnection.isPresent());
		assertTrue(thirdConnection.get().isBeginning());
		assertEquals(thirdGracenote, thirdConnection.get().getFollowingGraceNote().get());

		Optional<Notation.Connection> fourthConnection = thirdGracenote.getConnection(lastSlur);
		assertTrue(fourthConnection.isPresent());
		assertEquals(fourthGraceNote, fourthConnection.get().getFollowingGraceNote().get());

		assertTrue(fourthGraceNote.endsNotation(Notation.Type.SLUR));
	}

	@Test
	void testGivenGraceNoteConnectedToPrecedingAndSucceedingNoteWhenBuildThenNotationsAreCorrect() {
		NoteBuilder firstNoteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		GraceNoteBuilder graceNoteBuilder = new GraceNoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);
		NoteBuilder lastNoteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4),
				Durations.QUARTER);

		final Notation slur = Notation.of(Notation.Type.SLUR);
		final Notation glissando = Notation.of(Notation.Type.GLISSANDO);

		firstNoteBuilder.connectWith(glissando, graceNoteBuilder);
		graceNoteBuilder.connectWith(slur, lastNoteBuilder);
		lastNoteBuilder.setPrecedingGraceNotes(Arrays.asList(graceNoteBuilder));

		final Note firstNote = firstNoteBuilder.build();
		final GraceNote graceNote = graceNoteBuilder.build();
		final Note lastNote = lastNoteBuilder.build();

		assertEquals(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), firstNote.getPitch());
		assertTrue(firstNote.beginsNotation(Notation.Type.GLISSANDO));
		Optional<Notation.Connection> glissandoToGraceNote = firstNote.getConnection(glissando);
		assertTrue(glissandoToGraceNote.isPresent());

		final GraceNote endOfGlissando = glissandoToGraceNote.get().getFollowingGraceNote().get();

		assertEquals(1, firstNote.getNotations().size());
		assertTrue(firstNote.getOrnaments().isEmpty());

		assertEquals(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), lastNote.getPitch());
		assertTrue(lastNote.hasOrnament(Ornament.Type.GRACE_NOTES));
		assertTrue(lastNote.endsNotation(Notation.Type.SLUR));

		final Ornament graceNotes = lastNote.getOrnaments().stream().filter(ornament -> ornament.getType().equals(
				Ornament.Type.GRACE_NOTES)).findAny().get();

		assertEquals(1, graceNotes.getOrnamentalNotes().size());
		final GraceNote beginningOfSlur = (GraceNote) graceNotes.getOrnamentalNotes().get(0);

		assertEquals(beginningOfSlur, endOfGlissando);
		assertEquals(graceNote, endOfGlissando);

		assertEquals(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), graceNote.getPitch());

		assertTrue(graceNote.endsNotation(Notation.Type.GLISSANDO));
		assertTrue(graceNote.beginsNotation(Notation.Type.SLUR));

		Optional<Notation.Connection> slurToLastNote = graceNote.getConnection(slur);
		assertTrue(slurToLastNote.isPresent());
		assertEquals(lastNote, slurToLastNote.get().getFollowingNote().get());
	}

	@Test
	void testGivenGraceNoteChordsWhenBuiltThenNotationsAreCorrectlyConnected() {
		NoteBuilder noteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, 4), Durations.HALF);
		GraceNoteChordBuilder graceNoteChordBuilder = new GraceNoteChordBuilder();
		GraceNoteBuilder rootGraceNoteBuilder = new GraceNoteBuilder(
				Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
		GraceNoteBuilder thirdGraceNoteBuilder = new GraceNoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.FLAT, 4),
				Durations.EIGHTH);
		graceNoteChordBuilder.add(rootGraceNoteBuilder);
		graceNoteChordBuilder.add(thirdGraceNoteBuilder);

		GraceNoteBuilder graceNoteBuilder = new GraceNoteBuilder(
				Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);

		final Notation glissando = Notation.of(Notation.Type.GLISSANDO);
		final Notation slur = Notation.of(Notation.Type.SLUR);

		graceNoteBuilder.connectWith(glissando, rootGraceNoteBuilder);
		thirdGraceNoteBuilder.connectWith(slur, noteBuilder);

		List<OrnamentalBuilder> ornamentalBuilders = new ArrayList<>();
		ornamentalBuilders.add(graceNoteBuilder);
		ornamentalBuilders.add(graceNoteChordBuilder);
		noteBuilder.setPrecedingGraceNotes(ornamentalBuilders);

		final Note note = noteBuilder.build();
		assertTrue(note.endsNotation(Notation.Type.SLUR));
		assertTrue(note.getConnection(slur).isPresent());

		final GraceNote graceNote = graceNoteBuilder.build();
		final GraceNoteChord graceNoteChord = graceNoteChordBuilder.build();

		Optional<Notation.Connection> glissandoToRoot = graceNote.getConnection(glissando);
		assertTrue(glissandoToRoot.isPresent());
		assertEquals(graceNoteChord.getLowestNote(), glissandoToRoot.get().getFollowingGraceNote().get());

		final GraceNote graceNoteChordThird = graceNoteChord.getHighestNote();
		Optional<Notation.Connection> slurToNote = graceNoteChordThird.getConnection(slur);
		assertTrue(slurToNote.isPresent());

		assertEquals(note, slurToNote.get().getFollowingNote().get());
	}
}
