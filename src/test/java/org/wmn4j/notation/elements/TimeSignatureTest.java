/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeSignatureTest {

	@Test
	public void testGetTimeSignature() {
		final TimeSignature timeSig = TimeSignature.of(4, 4);
		assertEquals(4, timeSig.getBeatCount());
		assertEquals(Durations.QUARTER, timeSig.getBeatDuration());
	}

	@Test
	public void testGetTotalDuration() {
		assertEquals(Durations.WHOLE, TimeSignatures.FOUR_FOUR.getTotalDuration());
		assertEquals(Durations.EIGHT.multiplyBy(6), TimeSignatures.SIX_EIGHT.getTotalDuration());
		assertEquals(Durations.EIGHT.multiplyBy(13),
				TimeSignature.of(13, Durations.EIGHT).getTotalDuration());
	}

	@Test
	public void testEquals() {
		final TimeSignature timeSigA = TimeSignature.of(4, 4);
		final TimeSignature timeSigB = TimeSignature.of(4, 4);
		final TimeSignature timeSigC = TimeSignature.of(3, 4);
		final TimeSignature timeSigD = TimeSignature.of(4, 8);

		assertTrue(timeSigA.equals(timeSigB));
		assertFalse(timeSigA.equals(timeSigC));
		assertFalse(timeSigA.equals(timeSigD));
	}

	@Test
	public void testToString() {
		final TimeSignature timeSigA = TimeSignature.of(4, 4);
		assertEquals("Time(4/4)", timeSigA.toString());
	}
}
