/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * @author Otso Björklund
 */
class NoteEventVectorTest {

	private final NoteEventVector a;
	private final NoteEventVector b;

	NoteEventVectorTest() {
		final double[] componentsA = { 0, 0, 0 };
		this.a = new NoteEventVector(componentsA);

		final double[] componentsB = { 1, 1, 1 };
		this.b = new NoteEventVector(componentsB);
	}

	@Test
	void testAdd() {
		final NoteEventVector vec1 = this.a.add(this.b);
		assertEquals(this.b, vec1);

		final double[] components = { -1, -2, -3 };
		final NoteEventVector vec2 = new NoteEventVector(components);
		final double[] resultComponents = { 0, -1, -2 };
		final NoteEventVector correctVec = new NoteEventVector(resultComponents);
		assertEquals(correctVec, vec2.add(this.b));
	}

	@Test
	void testSubtract() {
		final NoteEventVector vec1 = this.b.subtract(this.b);
		assertEquals(this.a, vec1);

		final double[] components = { 1, 2, 3 };
		final NoteEventVector vec2 = new NoteEventVector(components);
		final double[] resultComponents = { 0, -1, -2 };
		final NoteEventVector correctVec = new NoteEventVector(resultComponents);
		assertEquals(correctVec, this.b.subtract(vec2));
	}

	@Test
	void testCompareTo() {
		final double[] components1 = { 0, 0, 0 };
		final NoteEventVector vec1 = new NoteEventVector(components1);

		final double[] components2 = { 0, 1, 2 };
		final NoteEventVector vec2 = new NoteEventVector(components2);

		assertTrue(vec1.compareTo(vec1) == 0);
		assertTrue(vec1.compareTo(vec2) < 0);
		assertTrue(vec2.compareTo(vec1) > 0);
	}

	@Test
	void testEquals() {
		assertEquals(this.a, this.a);
		assertFalse(this.a.equals(this.b));
	}

	@Test
	void testHashCode() {
		final double[] components1 = { 0, 0, 0 };
		final NoteEventVector vec1 = new NoteEventVector(components1);
		assertEquals(this.a.hashCode(), vec1.hashCode());
	}
}
