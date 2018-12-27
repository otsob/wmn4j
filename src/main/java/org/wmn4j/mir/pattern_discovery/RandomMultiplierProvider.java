/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.pattern_discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Otso Björklund
 */
enum RandomMultiplierProvider {
	INSTANCE;

	private final List<Long> multipliers;

	RandomMultiplierProvider() {
		this.multipliers = new ArrayList<>();
		this.generateMultipliers(100);
	}

	private void generateMultipliers(int count) {
		final Random random = new Random();
		for (int i = 0; i < count; ++i) {

			this.multipliers.add(random.nextLong());
		}
	}

	public long getMultiplier(int i) {

		while (this.multipliers.size() < i + 10) {
			this.generateMultipliers(10);
		}

		return this.multipliers.get(i);
	}
}
