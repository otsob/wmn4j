/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generates and caches random multipliers for use in hash functions.
 * NOTE: This class is not thread-safe.
 */
enum RandomMultipliers {
	INSTANCE;

	private static final int INCREMENT = 50;
	private final List<Long> multipliers;

	RandomMultipliers() {
		this.multipliers = new ArrayList<>();
		this.generateMultipliers(100);
	}

	private void generateMultipliers(int count) {
		final Random random = new Random();
		for (int i = 0; i < count; ++i) {

			this.multipliers.add(random.nextLong());
		}
	}

	/**
	 * Returns a random multiplier at the given index.
	 * The multiplier is guaranteed to be the same on subsequent calls with the same index.
	 * NOTE: This method is not thread-safe.
	 *
	 * @param index the index of the multiplier
	 * @return a random multiplier at the given index
	 */
	long getMultiplier(int index) {

		while (this.multipliers.size() < index + INCREMENT) {
			this.generateMultipliers(INCREMENT);
		}

		return this.multipliers.get(index);
	}
}
