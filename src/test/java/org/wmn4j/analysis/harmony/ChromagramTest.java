/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.PitchClass;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ChromagramTest {

	// By how much values are allowed fo differ
	private static final double TOLERANCE = 1e-10;

	private Chromagram getTestProfile(double... values) {
		int i = 0;

		Map<PitchClass, Double> profile = new EnumMap<>(PitchClass.class);
		for (PitchClass pc : PitchClass.values()) {
			profile.put(pc, values[i++]);
		}

		return Chromagram.of(profile);
	}

	@Test
	void testGivenValueForEachPitchClassChromagramReturnsCorrectValues() {
		Map<PitchClass, Double> profile = new EnumMap<>(PitchClass.class);
		final double valueOfC = 1.0;
		profile.put(PitchClass.C, valueOfC);

		final double valueOfCSharp = 1.1;
		profile.put(PitchClass.CSHARP_DFLAT, valueOfCSharp);

		final double valueOfD = 1.2;
		profile.put(PitchClass.D, valueOfD);

		final double valueOfDSharp = 1.3;
		profile.put(PitchClass.DSHARP_EFLAT, valueOfDSharp);

		final double valueOfE = 1.4;
		profile.put(PitchClass.E, valueOfE);

		final double valueOfF = 1.5;
		profile.put(PitchClass.F, valueOfF);

		final double valueOfFSharp = 1.6;
		profile.put(PitchClass.FSHARP_GFLAT, valueOfFSharp);

		final double valueOfG = 1.7;
		profile.put(PitchClass.G, valueOfG);

		final double valueOfGSharp = 1.8;
		profile.put(PitchClass.GSHARP_AFLAT, valueOfGSharp);

		final double valueOfA = 1.9;
		profile.put(PitchClass.A, valueOfA);

		final double valueOfASharp = 2.0;
		profile.put(PitchClass.ASHARP_BFLAT, valueOfASharp);

		final double valueOfB = 2.1;
		profile.put(PitchClass.B, valueOfB);

		final Chromagram chromagram = Chromagram.of(profile);

		assertEquals(valueOfC, chromagram.getValue(PitchClass.C));
		assertEquals(valueOfCSharp, chromagram.getValue(PitchClass.CSHARP_DFLAT));
		assertEquals(valueOfD, chromagram.getValue(PitchClass.D));
		assertEquals(valueOfDSharp, chromagram.getValue(PitchClass.DSHARP_EFLAT));
		assertEquals(valueOfE, chromagram.getValue(PitchClass.E));
		assertEquals(valueOfF, chromagram.getValue(PitchClass.F));
		assertEquals(valueOfFSharp, chromagram.getValue(PitchClass.FSHARP_GFLAT));
		assertEquals(valueOfG, chromagram.getValue(PitchClass.G));
		assertEquals(valueOfGSharp, chromagram.getValue(PitchClass.GSHARP_AFLAT));
		assertEquals(valueOfA, chromagram.getValue(PitchClass.A));
		assertEquals(valueOfASharp, chromagram.getValue(PitchClass.ASHARP_BFLAT));
		assertEquals(valueOfB, chromagram.getValue(PitchClass.B));
	}

	@Test
	void testGivenMissingValuesForSomePitchClasseChromagramReturnsCorrectValues() {
		Map<PitchClass, Double> profile = new EnumMap<>(PitchClass.class);
		final double valueOfC = 1.0;
		profile.put(PitchClass.C, valueOfC);

		final double valueOfCSharp = 1.1;
		profile.put(PitchClass.CSHARP_DFLAT, valueOfCSharp);

		final double valueOfDSharp = 1.3;
		profile.put(PitchClass.DSHARP_EFLAT, valueOfDSharp);

		final double valueOfE = 1.4;
		profile.put(PitchClass.E, valueOfE);

		final double valueOfFSharp = 1.6;
		profile.put(PitchClass.FSHARP_GFLAT, valueOfFSharp);

		final double valueOfG = 1.7;
		profile.put(PitchClass.G, valueOfG);

		final double valueOfA = 1.9;
		profile.put(PitchClass.A, valueOfA);

		final Chromagram chromagram = Chromagram.of(profile);

		assertEquals(valueOfC, chromagram.getValue(PitchClass.C));
		assertEquals(valueOfCSharp, chromagram.getValue(PitchClass.CSHARP_DFLAT));
		assertEquals(0.0, chromagram.getValue(PitchClass.D));
		assertEquals(valueOfDSharp, chromagram.getValue(PitchClass.DSHARP_EFLAT));
		assertEquals(valueOfE, chromagram.getValue(PitchClass.E));
		assertEquals(0.0, chromagram.getValue(PitchClass.F));
		assertEquals(valueOfFSharp, chromagram.getValue(PitchClass.FSHARP_GFLAT));
		assertEquals(valueOfG, chromagram.getValue(PitchClass.G));
		assertEquals(0.0, chromagram.getValue(PitchClass.GSHARP_AFLAT));
		assertEquals(valueOfA, chromagram.getValue(PitchClass.A));
		assertEquals(0.0, chromagram.getValue(PitchClass.ASHARP_BFLAT));
		assertEquals(0.0, chromagram.getValue(PitchClass.B));
	}

	@Test
	void testGivenEqualChromagramsEqualsReturnsTrue() {
		Map<PitchClass, Double> profile = new EnumMap<>(PitchClass.class);
		final double valueOfC = 1.0;
		profile.put(PitchClass.C, valueOfC);

		final double valueOfCSharp = 1.1;
		profile.put(PitchClass.CSHARP_DFLAT, valueOfCSharp);

		final double valueOfDSharp = 1.3;
		profile.put(PitchClass.DSHARP_EFLAT, valueOfDSharp);

		final double valueOfE = 1.4;
		profile.put(PitchClass.E, valueOfE);

		final double valueOfFSharp = 1.6;
		profile.put(PitchClass.FSHARP_GFLAT, valueOfFSharp);

		final double valueOfG = 1.7;
		profile.put(PitchClass.G, valueOfG);

		final double valueOfA = 1.9;
		profile.put(PitchClass.A, valueOfA);

		final Chromagram chromagramA = Chromagram.of(profile);
		assertEquals(chromagramA, chromagramA);

		final Chromagram chromagramB = Chromagram.of(profile);
		assertEquals(chromagramA, chromagramB);
	}

	@Test
	void testGivenUnequalChromagramsEqualsReturnsFalse() {
		Map<PitchClass, Double> profile = new EnumMap<>(PitchClass.class);
		final double valueOfC = 1.0;
		profile.put(PitchClass.C, valueOfC);

		final double valueOfCSharp = 1.1;
		profile.put(PitchClass.CSHARP_DFLAT, valueOfCSharp);

		final double valueOfDSharp = 1.3;
		profile.put(PitchClass.DSHARP_EFLAT, valueOfDSharp);

		final double valueOfE = 1.4;
		profile.put(PitchClass.E, valueOfE);

		final double valueOfFSharp = 1.6;
		profile.put(PitchClass.FSHARP_GFLAT, valueOfFSharp);

		final double valueOfG = 1.7;
		profile.put(PitchClass.G, valueOfG);

		final double valueOfA = 1.9;
		profile.put(PitchClass.A, valueOfA);

		final Chromagram chromagramA = Chromagram.of(profile);

		profile.put(PitchClass.B, 7.0);
		final Chromagram chromagramB = Chromagram.of(profile);
		assertNotEquals(chromagramA, chromagramB);
	}

	@Test
	void testGivenNegativeValuesExceptionIsThrown() {
		Map<PitchClass, Double> invalidProfile = new EnumMap<>(PitchClass.class);

		Arrays.stream(PitchClass.values()).forEach(pc -> invalidProfile.put(pc, 1.0));
		invalidProfile.put(PitchClass.A, -0.1);

		assertThrows(IllegalArgumentException.class, () -> Chromagram.of(invalidProfile));
	}

	@Test
	void testCorrelation() {

		// Profile for C-major from Krumhansl and Kessler.
		final Chromagram cMajorProfile = getTestProfile(6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66,
				2.29,
				2.88);
		// Profile for A minor from Krumhansl and Kessler.
		final Chromagram aMinorProfile = getTestProfile(5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17, 6.33,
				2.68,
				3.52);

		assertEquals(1.0, cMajorProfile.correlation(cMajorProfile), TOLERANCE,
				"Incorrect correlation for c major profile with itself");

		assertEquals(0.6496, cMajorProfile.correlation(aMinorProfile), 0.0001,
				"Incorrect correlation between c major and a minor profiles");

		assertEquals(cMajorProfile.correlation(aMinorProfile), aMinorProfile.correlation(cMajorProfile), TOLERANCE,
				"Correlation should be symmetric but is not");
	}
}
