package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RandomMultipliersTest {

	@Test
	void testSameIndexProducesSameMultiplier() {
		final int count = 200;
		for (int i = 0; i < count; ++i) {
			final long multiplierA = RandomMultipliers.INSTANCE.getMultiplier(i);
			final long multiplierB = RandomMultipliers.INSTANCE.getMultiplier(i);
			assertEquals(multiplierA, multiplierB, "Multipliers at index " + i + " differ");
		}
	}
}
