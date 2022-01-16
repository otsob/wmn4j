/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.utils;

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
