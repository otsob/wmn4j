/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Clefs;
import org.wmn4j.notation.Durations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OffsetTest {

	@Test
	void testGivenOffsetWithDurationThenCorrectOffsetIsSet() {
		final Offset<Clef> offsetClef = new Offset<>(Clefs.G, Durations.EIGHTH);
		assertEquals(Clefs.G, offsetClef.get());
		assertTrue(offsetClef.getDuration().isPresent());
		assertEquals(Durations.EIGHTH, offsetClef.getDuration().get());
	}

	@Test
	void testGivenOffsetWithoutDurationThenOffsetIsEmpty() {
		final Offset<Clef> offsetClef = new Offset<>(Clefs.G, null);
		assertEquals(Clefs.G, offsetClef.get());
		assertTrue(offsetClef.getDuration().isEmpty());
	}

	@Test
	void testComparisonWithPresentOffsetDurations() {
		final Offset<Clef> offsetClef = new Offset<>(Clefs.G, Durations.EIGHTH);
		assertEquals(0, offsetClef.compareTo(offsetClef));

		final Offset<Clef> greaterOffset = new Offset<>(Clefs.ALTO, Durations.QUARTER);
		assertTrue(offsetClef.compareTo(greaterOffset) < 0);
		assertTrue(greaterOffset.compareTo(offsetClef) > 0);
	}

	@Test
	void testComparisonWithAbsentOffsetDurations() {
		final Offset<Clef> zeroOffset = new Offset<>(Clefs.G, null);
		assertEquals(0, zeroOffset.compareTo(zeroOffset));

		final Offset<Clef> greaterOffset = new Offset<>(Clefs.ALTO, Durations.QUARTER);
		assertTrue(zeroOffset.compareTo(greaterOffset) < 0);
		assertTrue(greaterOffset.compareTo(zeroOffset) > 0);
	}
}


