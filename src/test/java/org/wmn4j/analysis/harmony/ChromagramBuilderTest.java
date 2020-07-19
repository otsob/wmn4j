/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.PitchClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChromagramBuilderTest {
	// By how much values are allowed fo differ
	private static final double TOLERANCE = 0.0000000001;

	private static final Note C_EIGHTH = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
	private static final Note E_EIGHTH = Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
	private static final Note G_EIGHTH = Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH);
	private static final Note C_SHARP_SIXTEENTH = Note
			.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, 4), Durations.SIXTEENTH);
	private static final Note A_FLAT_TRIPLET = Note
			.of(Pitch.of(Pitch.Base.A, Pitch.Accidental.FLAT, 2), Durations.EIGHTH_TRIPLET);
	private static final Chord C_TRIAD_EIGHTH = Chord.of(C_EIGHTH, E_EIGHTH, G_EIGHTH);

	@Test
	void testGivenNegativeValueSetValueThrowsException() {
		final ChromagramBuilder builder = new ChromagramBuilder();
		assertThrows(IllegalArgumentException.class, () -> builder.setValue(PitchClass.C, -0.1));
	}

	@Test
	void testSetValue() {
		final ChromagramBuilder builder = new ChromagramBuilder();
		builder.setValue(PitchClass.C, 1.0);
		builder.setValue(PitchClass.DSHARP_EFLAT, 2.0);
		builder.setValue(PitchClass.A, 3.0);
		builder.setValue(PitchClass.B, 4.0);

		final Chromagram profile = builder.build();
		assertEquals(1.0, profile.getValue(PitchClass.C), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.CSHARP_DFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.D), TOLERANCE);
		assertEquals(2.0, profile.getValue(PitchClass.DSHARP_EFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.E), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.F), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.FSHARP_GFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.G), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.GSHARP_AFLAT), TOLERANCE);
		assertEquals(3.0, profile.getValue(PitchClass.A), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.ASHARP_BFLAT), TOLERANCE);
		assertEquals(4.0, profile.getValue(PitchClass.B), TOLERANCE);
	}

	@Test
	void testAddWithDefaultWeightFunction() {
		final ChromagramBuilder builder = new ChromagramBuilder();
		builder.add(C_EIGHTH);
		builder.add(E_EIGHTH);
		builder.add(G_EIGHTH);
		builder.add(C_SHARP_SIXTEENTH);
		builder.add(C_TRIAD_EIGHTH);
		builder.add(A_FLAT_TRIPLET);

		final Chromagram profile = builder.build();
		assertEquals(2.0, profile.getValue(PitchClass.C), TOLERANCE);
		assertEquals(1.0, profile.getValue(PitchClass.CSHARP_DFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.D), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.DSHARP_EFLAT), TOLERANCE);
		assertEquals(2.0, profile.getValue(PitchClass.E), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.F), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.FSHARP_GFLAT), TOLERANCE);
		assertEquals(2.0, profile.getValue(PitchClass.G), TOLERANCE);
		assertEquals(1.0, profile.getValue(PitchClass.GSHARP_AFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.A), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.ASHARP_BFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.B), TOLERANCE);
	}

	@Test
	void testAddWithDurationWeightFunction() {
		final ChromagramBuilder builder = new ChromagramBuilder(ChromagramBuilder::durationWeight);
		builder.add(C_EIGHTH);
		builder.add(E_EIGHTH);
		builder.add(G_EIGHTH);
		builder.add(C_SHARP_SIXTEENTH);
		builder.add(C_TRIAD_EIGHTH);
		builder.add(A_FLAT_TRIPLET);

		final Chromagram profile = builder.build();
		assertEquals(0.25, profile.getValue(PitchClass.C), TOLERANCE);
		assertEquals(1.0 / 16.0, profile.getValue(PitchClass.CSHARP_DFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.D), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.DSHARP_EFLAT), TOLERANCE);
		assertEquals(0.25, profile.getValue(PitchClass.E), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.F), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.FSHARP_GFLAT), TOLERANCE);
		assertEquals(0.25, profile.getValue(PitchClass.G), TOLERANCE);
		assertEquals(1.0 / 12.0, profile.getValue(PitchClass.GSHARP_AFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.A), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.ASHARP_BFLAT), TOLERANCE);
		assertEquals(0.0, profile.getValue(PitchClass.B), TOLERANCE);
	}
}
