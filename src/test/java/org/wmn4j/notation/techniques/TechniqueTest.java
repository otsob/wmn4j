/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.techniques;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TechniqueTest {

	@Test
	void testPlainTechniqueCreatedCorrectly() {
		final var technique = Technique.of(Technique.Type.UP_BOW);
		assertEquals(Technique.Type.UP_BOW, technique.getType());
		assertTrue(technique.getNumber().isEmpty());
		assertTrue(technique.getText().isEmpty());
	}

	@Test
	void testTextualTechniqueCreatedCorrectly() {
		final var technique = Technique.of(Technique.Type.FINGERING, "0");
		assertEquals(Technique.Type.FINGERING, technique.getType());
		assertTrue(technique.getText().isPresent());
		assertEquals("0", technique.getText().get());
		assertTrue(technique.getNumber().isEmpty());
	}

	@Test
	void testNumericTechniqueCreatedCorrectly() {
		final var technique = Technique.of(Technique.Type.STRING, 1);
		assertEquals(Technique.Type.STRING, technique.getType());
		assertTrue(technique.getText().isEmpty());
		assertTrue(technique.getNumber().isPresent());
		assertEquals(1, technique.getNumber().getAsInt());
	}

	@Test
	void testGivenEqualTechniquesEqualsReturnsTrueAndHashesMatch() {
		final var plainTech1 = Technique.of(Technique.Type.UP_BOW);
		final var plainTech2 = Technique.of(Technique.Type.UP_BOW);
		assertEquals(plainTech1, plainTech2);
		assertEquals(plainTech1.hashCode(), plainTech2.hashCode());

		final var textTech1 = Technique.of(Technique.Type.FINGERING, "0");
		final var textTech2 = Technique.of(Technique.Type.FINGERING, "0");
		assertEquals(textTech1, textTech2);
		assertEquals(textTech1.hashCode(), textTech2.hashCode());

		final var numericTech1 = Technique.of(Technique.Type.STRING, 1);
		final var numericTech2 = Technique.of(Technique.Type.STRING, 1);
		assertEquals(numericTech1, numericTech2);
		assertEquals(numericTech1.hashCode(), numericTech2.hashCode());
	}

	@Test
	void testGivenDifferentTechniquesEqualsReturnsFalse() {
		final var plainTech1 = Technique.of(Technique.Type.UP_BOW);
		final var plainTech2 = Technique.of(Technique.Type.DOWN_BOW);
		assertNotEquals(plainTech1, plainTech2);

		final var textTech1 = Technique.of(Technique.Type.FINGERING, "0");
		final var textTech2 = Technique.of(Technique.Type.FINGERING, "1");
		assertNotEquals(textTech1, textTech2);

		final var numericTech1 = Technique.of(Technique.Type.STRING, 1);
		final var numericTech2 = Technique.of(Technique.Type.STRING, 2);
		assertNotEquals(numericTech1, numericTech2);
	}
}
