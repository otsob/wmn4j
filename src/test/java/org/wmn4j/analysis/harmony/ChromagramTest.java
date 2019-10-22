/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.PitchClass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ChromagramTest {

	// By how much values are allowed fo differ
	private static final double EPS = 0.0000000001;

	private static final Note C4 = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);
	static final Note E4 = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHTH);
	private static final Note G4 = Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHTH);
	private static final Note Csharp4 = Note.of(Pitch.of(Pitch.Base.C, 1, 4), Durations.SIXTEENTH);

	@Test
	void testSetIncorrectValue() {
		final Chromagram profile = new Chromagram();

		try {
			profile.setValue(PitchClass.C, -0.1);
			fail("No exception thrown");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

	}

	@Test
	void testAddWithDefaultWeightFunction() {
		final Chromagram profile = new Chromagram();
		profile.add(C4);
		assertEquals(1.0, profile.getValue(PitchClass.C), EPS, "Incorrect value for C");
		assertEquals(0.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS, "Incorrect value for Csharp");
		profile.add(Csharp4);
		assertEquals(1.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS, "Incorrect value for Csharp");
	}

	@Test
	void testAddWithDurationWeightFunction() {
		final Chromagram profile = Chromagram.getDurationWeightedProfile();
		profile.add(C4);
		assertEquals(0.25, profile.getValue(PitchClass.C), EPS, "Incorrect value for C");
		assertEquals(0.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS, "Incorrect value for Csharp");
		profile.add(Csharp4);
		assertEquals(1.0 / 16.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS, "Incorrect value for Csharp");
		profile.add(C4);
		assertEquals(0.5, profile.getValue(PitchClass.C), EPS, "Incorrect value for C");
	}

	@Test
	void testNormalizeWithDefaultWeightFunction() {
		Chromagram profile = new Chromagram();
		final int C4Count = 5;
		final int G4Count = 3;
		final int CsharpCount = 2;

		for (int i = 0; i < C4Count; ++i) {
			profile.add(C4);
		}

		for (int i = 0; i < G4Count; ++i) {
			profile.add(G4);
		}

		for (int i = 0; i < CsharpCount; ++i) {
			profile.add(Csharp4);
		}

		assertEquals(C4Count, profile.getValue(PitchClass.C), EPS, "Incorrect value for C before normalization");
		assertEquals(G4Count, profile.getValue(PitchClass.G), EPS, "Incorrect value for G before normalization");
		assertEquals(CsharpCount,
				profile.getValue(PitchClass.CSHARP_DFLAT), EPS, "Incorrect value for C sharp before normalization");

		profile = profile.normalize();

		assertEquals(1.0, profile.getValue(PitchClass.C), EPS, "Incorrect value for C before normalization");
		assertEquals(3.0 / 5.0, profile.getValue(PitchClass.G), EPS, "Incorrect value for G before normalization");
		assertEquals(2.0 / 5.0,
				profile.getValue(PitchClass.CSHARP_DFLAT), EPS, "Incorrect value for C sharp before normalization");
	}

	static Chromagram getTestProfile(double... values) {
		final Chromagram profile = new Chromagram();
		int i = 0;

		for (PitchClass pc : PitchClass.values()) {
			profile.setValue(pc, values[i++]);
		}

		return profile;
	}

	@Test
	void testCorrelation() {

		// Profile for C-major from Krumhansl and Kessler.
		final Chromagram cMajorProfile = getTestProfile(6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66, 2.29,
				2.88);
		// Profile for A minor from Krumhansl and Kessler.
		final Chromagram aMinorProfile = getTestProfile(5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17, 6.33, 2.68,
				3.52);

		assertEquals(1.0,
				Chromagram.correlation(cMajorProfile, cMajorProfile), EPS,
				"Incorrect correlation for c major profile with itself");

		assertEquals(0.6496,
				Chromagram.correlation(cMajorProfile, aMinorProfile), 0.0001,
				"Incorrect correlation between c major and a minor profiles");

		assertEquals(Chromagram.correlation(cMajorProfile, aMinorProfile),
				Chromagram.correlation(aMinorProfile, cMajorProfile), EPS, "Correlation should be symmetric but is not");
	}

	@Test
	void testEuclidean() {
		// Profile for C-major from Krumhansl and Kessler.
		final Chromagram a = getTestProfile(1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00);
		// Profile for A minor from Krumhansl and Kessler.
		final Chromagram b = getTestProfile(5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17, 6.33, 2.68, 3.52);

		assertEquals(0.0, Chromagram.euclidean(b, b), EPS, "Incorrect distance when computing distance with itself");

		final Chromagram c = getTestProfile(0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00);

		assertEquals(Math.sqrt(12.0), Chromagram.euclidean(a, c), EPS, "Incorrect value");
	}
}
