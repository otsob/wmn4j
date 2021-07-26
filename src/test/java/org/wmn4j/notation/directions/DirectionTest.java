/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.directions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectionTest {

	@Test
	void testTempoHasTypeTempo() {
		Direction direction = Direction.of(Direction.Type.TEXT, null);
		assertEquals(Direction.Type.TEXT, direction.getType());
	}

	@Test
	void testGivenNonTextualDirectionNoTextIsPresent() {
		Direction direction = Direction.of(Direction.Type.TEXT, null);
		assertFalse(direction.isTextual());
		assertTrue(direction.getText().isEmpty());
	}

	@Test
	void testTextualDirectionTextIsPresent() {
		Direction direction = Direction.of(Direction.Type.TEXT, "A text");
		assertTrue(direction.isTextual());
		assertTrue(direction.getText().isPresent());
		assertEquals("A text", direction.getText().get());
	}
}
