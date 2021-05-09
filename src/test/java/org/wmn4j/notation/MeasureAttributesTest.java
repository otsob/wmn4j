/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.access.Offset;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MeasureAttributesTest {

	@Test
	void testCreateMeasureAttributes() {
		final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G);

		assertFalse(attr == null);
		assertEquals(TimeSignatures.FOUR_FOUR, attr.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, attr.getKeySignature());
		assertEquals(Barline.SINGLE, attr.getRightBarline());
		assertEquals(Barline.SINGLE, attr.getLeftBarline());
		assertEquals(Clefs.G, attr.getClef());
	}

	@Test
	void testCreateMeasureAttributesWithInvalidParameters() {

		try {
			final MeasureAttributes attr = MeasureAttributes.of(null, KeySignatures.CMAJ_AMIN, Barline.SINGLE,
					Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null TimeSignature");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, null, Barline.SINGLE,
					Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null KeySignature");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
					null, Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null right barline");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
					Barline.SINGLE, Barline.SINGLE, null);

			fail("Did not throw exception with null Clef");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	@Test
	void testEquals() {

		final Set<Offset<Clef>> clefChangesA = new HashSet<>();
		clefChangesA.add(new Offset<>(Clefs.F, Durations.HALF));
		final Set<Offset<Clef>> clefChangesB = new HashSet<>();
		clefChangesB.add(new Offset<>(Clefs.F, Durations.HALF.addDot()));

		final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G, clefChangesA);

		final MeasureAttributes other = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G, clefChangesA);

		final MeasureAttributes different = MeasureAttributes.of(TimeSignatures.FOUR_FOUR,
				KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.DOUBLE, Clefs.G, clefChangesB);

		assertTrue(attr.equals(attr));
		assertTrue(attr.equals(other));
		assertFalse(attr.equals(different));
		assertFalse(different.equals(attr));
	}
}
