/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Wmn4jTest {

	@Test
	void testGetVersionReturnsNotEmpty() {
		final String versionNumber = Wmn4j.getVersion();
		assertNotNull(versionNumber);
		assertFalse(versionNumber.isEmpty());
	}
}
