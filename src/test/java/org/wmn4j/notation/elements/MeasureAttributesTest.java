/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.MeasureAttributes;
import org.wmn4j.notation.elements.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
public class MeasureAttributesTest {

	public MeasureAttributesTest() {
	}

	@BeforeClass
	public static void setUpClass() {
	}

	@AfterClass
	public static void tearDownClass() {
	}

	@Test
	public void testGetMeasureInfo() {
		final MeasureAttributes attr = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G);

		assertFalse(attr == null);
		assertEquals(TimeSignatures.FOUR_FOUR, attr.getTimeSignature());
		assertEquals(KeySignatures.CMAJ_AMIN, attr.getKeySignature());
		assertEquals(Barline.SINGLE, attr.getRightBarline());
		assertEquals(Barline.SINGLE, attr.getLeftBarline());
		assertEquals(Clefs.G, attr.getClef());
	}

	@Test
	public void testGetMeasureInfoWithInvalidParameters() {

		try {
			final MeasureAttributes attr = MeasureAttributes.getMeasureAttr(null, KeySignatures.CMAJ_AMIN, Barline.SINGLE,
					Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null TimeSignature");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, null, Barline.SINGLE,
					Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null KeySignature");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
					null, Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null right barline");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
					Barline.SINGLE, Barline.SINGLE, null);

			fail("Did not throw exception with null Clef");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	@Test
	public void testEquals() {
		final MeasureAttributes attr = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G);

		final MeasureAttributes other = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G);

		final MeasureAttributes different = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR,
				KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.DOUBLE, Clefs.G);

		assertTrue(attr.equals(attr));
		assertTrue(attr.equals(other));
		assertFalse(attr.equals(different));
		assertFalse(different.equals(attr));
	}
}
