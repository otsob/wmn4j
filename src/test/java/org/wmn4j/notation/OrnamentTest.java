package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OrnamentTest {
	@Test
	void testGivenOrnamentGetTypeReturnsCorrectType() {
		assertEquals(Ornament.Type.MORDENT, Ornament.of(Ornament.Type.MORDENT).getType());
		assertEquals(Ornament.Type.TRILL, Ornament.of(Ornament.Type.TRILL).getType());
	}
}
