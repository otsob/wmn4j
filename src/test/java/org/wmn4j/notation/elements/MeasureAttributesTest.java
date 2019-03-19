
package org.wmn4j.notation.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
		final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
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
			final MeasureAttributes attr = MeasureAttributes.of(null, KeySignatures.CMAJ_AMIN, Barline.SINGLE,
					Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null TimeSignature");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, null, Barline.SINGLE,
					Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null KeySignature");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
					null, Barline.SINGLE, Clefs.G);

			fail("Did not throw exception with null right barline");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}

		try {
			final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
					Barline.SINGLE, Barline.SINGLE, null);

			fail("Did not throw exception with null Clef");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	@Test
	public void testEquals() {

		final Map<Duration, Clef> clefChangesA = new HashMap<>();
		clefChangesA.put(Durations.HALF, Clefs.F);
		final Map<Duration, Clef> clefChangesB = new HashMap<>();
		clefChangesB.put(Durations.HALF.addDot(), Clefs.F);

		final MeasureAttributes attr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G, clefChangesA);

		final MeasureAttributes other = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Barline.SINGLE, Clefs.G, clefChangesA);

		final MeasureAttributes different = MeasureAttributes.of(TimeSignatures.FOUR_FOUR,
				KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.DOUBLE, Clefs.G, clefChangesB);

		assertTrue(attr.equals(attr));
		assertTrue(attr.equals(other));
		assertFalse(attr.equals(different));
		assertFalse(different.equals(attr));
	}
}