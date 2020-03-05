/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TimeSignatureTest {

	@Test
	void testGetTimeSignature() {
		final TimeSignature timeSig = TimeSignature.of(4, 4);
		assertEquals(4, timeSig.getBeatCount());
		assertEquals(Durations.QUARTER, timeSig.getBeatDuration());
	}

	@Test
	void testGetTotalDuration() {
		assertEquals(Durations.WHOLE, TimeSignatures.FOUR_FOUR.getTotalDuration());
		assertEquals(Durations.EIGHTH.multiplyBy(6), TimeSignatures.SIX_EIGHT.getTotalDuration());
		assertEquals(Durations.EIGHTH.multiplyBy(13),
				TimeSignature.of(13, Durations.EIGHTH).getTotalDuration());
	}

	@Test
	void testEquals() {
		final TimeSignature timeSigA = TimeSignature.of(4, 4);
		final TimeSignature timeSigB = TimeSignature.of(4, 4);
		final TimeSignature timeSigC = TimeSignature.of(3, 4);
		final TimeSignature timeSigD = TimeSignature.of(4, 8);

		assertTrue(timeSigA.equals(timeSigB));
		assertFalse(timeSigA.equals(timeSigC));
		assertFalse(timeSigA.equals(timeSigD));
	}

	@Test
	void testToString() {
		final TimeSignature timeSigA = TimeSignature.of(4, 4);
		assertEquals("Time(4/4)", timeSigA.toString());
	}

	@Test
	void testGetSymbol() {
		assertEquals(TimeSignature.Symbol.NUMERIC, TimeSignatures.FOUR_FOUR.getSymbol());
		assertEquals(TimeSignature.Symbol.COMMON, TimeSignatures.COMMON.getSymbol());
		assertEquals(TimeSignature.Symbol.CUT_TIME, TimeSignatures.CUT_TIME.getSymbol());
	}
}
