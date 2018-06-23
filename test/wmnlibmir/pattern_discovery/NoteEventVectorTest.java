/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir.pattern_discovery;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class NoteEventVectorTest {

	private final NoteEventVector a;
	private final NoteEventVector b;

	public NoteEventVectorTest() {
		double[] componentsA = { 0, 0, 0 };
		this.a = new NoteEventVector(componentsA);

		double[] componentsB = { 1, 1, 1 };
		this.b = new NoteEventVector(componentsB);
	}

	@Test
	public void testAdd() {
		NoteEventVector vec1 = this.a.add(this.b);
		assertEquals(this.b, vec1);

		double[] components = { -1, -2, -3 };
		NoteEventVector vec2 = new NoteEventVector(components);
		double[] resultComponents = { 0, -1, -2 };
		NoteEventVector correctVec = new NoteEventVector(resultComponents);
		assertEquals(correctVec, vec2.add(this.b));
	}

	@Test
	public void testSubtract() {
		NoteEventVector vec1 = this.b.subtract(this.b);
		assertEquals(this.a, vec1);

		double[] components = { 1, 2, 3 };
		NoteEventVector vec2 = new NoteEventVector(components);
		double[] resultComponents = { 0, -1, -2 };
		NoteEventVector correctVec = new NoteEventVector(resultComponents);
		assertEquals(correctVec, this.b.subtract(vec2));
	}

	@Test
	public void testCompareTo() {
		double[] components1 = { 0, 0, 0 };
		NoteEventVector vec1 = new NoteEventVector(components1);

		double[] components2 = { 0, 1, 2 };
		NoteEventVector vec2 = new NoteEventVector(components2);

		assertTrue(vec1.compareTo(vec1) == 0);
		assertTrue(vec1.compareTo(vec2) < 0);
		assertTrue(vec2.compareTo(vec1) > 0);
	}

	@Test
	public void testEquals() {
		assertEquals(this.a, this.a);
		assertFalse(this.a.equals(this.b));
	}

	@Test
	public void testHashCode() {
		double[] components1 = { 0, 0, 0 };
		NoteEventVector vec1 = new NoteEventVector(components1);
		assertEquals(this.a.hashCode(), vec1.hashCode());
	}
}
