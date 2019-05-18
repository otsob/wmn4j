/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class StaffTest {

	static List<Measure> getTestMeasures() {
		final List<Measure> measures = new ArrayList<>();
		final TimeSignature timeSig = TimeSignature.of(4, 4);
		final Map<Integer, List<Durational>> notes = new HashMap<>();
		notes.put(0, new ArrayList<>());
		notes.get(0).add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.get(0).add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.get(0).add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.get(0).add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		measures.add(Measure.of(1, notes, timeSig, KeySignatures.CMAJ_AMIN, Clefs.G));
		measures.add(Measure.of(2, notes, timeSig, KeySignatures.CMAJ_AMIN, Clefs.G));
		return measures;
	}

	@Test
	public void testGetMeasures() {
		final List<Measure> origMeasures = getTestMeasures();
		final Staff staff = Staff.of(origMeasures);

		final List<Measure> measures = staff.getMeasures();

		assertEquals(origMeasures.size(), measures.size());

		try {
			measures.add(origMeasures.get(0));
			fail("Did not throw UnsupportedOperationException");
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException, "Exception was of incorrect type: " + e);
		}

		final int sizeBeforeAddition = origMeasures.size();
		final List<Durational> voice = new ArrayList<>();
		voice.add(Rest.of(Durations.WHOLE));
		final Map<Integer, List<Durational>> notes = new HashMap<>();
		notes.put(1, voice);
		origMeasures.add(Measure.of(3, notes, TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Clefs.G));
		assertEquals(sizeBeforeAddition, staff.getMeasures().size());
	}

	@Test
	public void testIterator() {
		final List<Measure> origMeasures = getTestMeasures();
		final Staff staff = Staff.of(origMeasures);

		int measureCount = 0;
		int prevMeasureNum = 0;

		for (Measure m : staff) {
			++measureCount;
			assertTrue(prevMeasureNum < m.getNumber());
			prevMeasureNum = m.getNumber();
		}

		assertEquals(origMeasures.size(), measureCount);
	}

	@Test
	public void testIteratorRemoveDisabled() {
		final List<Measure> origMeasures = getTestMeasures();
		final Staff staff = Staff.of(origMeasures);

		try {
			final Iterator<Measure> iter = staff.iterator();
			iter.next();
			iter.remove();
			fail("Expected exception was not thrown");
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}
	}

	@Test
	public void testGetMeasure() {
		final List<Measure> measures = getTestMeasures();
		final Staff staff = Staff.of(measures);

		assertFalse(staff.hasPickupMeasure());

		for (int measureNumber = 1; measureNumber < measures.size(); ++measureNumber) {
			assertEquals(measureNumber, staff.getMeasure(measureNumber).getNumber());
		}
	}

	@Test
	public void testGetMeasureWithPickup() {
		final List<Measure> measures = new ArrayList<>();
		final List<Durational> voice = new ArrayList<>();
		voice.add(Rest.of(Durations.WHOLE));
		final Map<Integer, List<Durational>> notes = new HashMap<>();
		notes.put(1, voice);
		measures.add(Measure.of(0, notes, TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Clefs.G));
		measures.addAll(getTestMeasures());

		final Staff staff = Staff.of(measures);
		assertTrue(staff.hasPickupMeasure());

		for (int measureNumber = 0; measureNumber < measures.size(); ++measureNumber) {
			assertEquals(measureNumber, staff.getMeasure(measureNumber).getNumber());
		}
	}
}
