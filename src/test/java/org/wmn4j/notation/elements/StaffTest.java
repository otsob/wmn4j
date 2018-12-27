/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;
import org.wmn4j.notation.elements.Staff;
import org.wmn4j.notation.elements.TimeSignature;
import org.wmn4j.notation.elements.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
public class StaffTest {

	public StaffTest() {
	}

	static List<Measure> getTestMeasures() {
		final List<Measure> measures = new ArrayList<>();
		final TimeSignature timeSig = TimeSignature.getTimeSignature(4, 4);
		final Map<Integer, List<Durational>> notes = new HashMap<>();
		notes.put(0, new ArrayList<>());
		notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
		notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
		measures.add(new Measure(1, notes, timeSig, KeySignatures.CMAJ_AMIN, Clefs.G));
		measures.add(new Measure(2, notes, timeSig, KeySignatures.CMAJ_AMIN, Clefs.G));
		return measures;
	}

	@Test
	public void testGetMeasures() {
		final List<Measure> origMeasures = getTestMeasures();
		final Staff staff = new Staff(origMeasures);

		final List<Measure> measures = staff.getMeasures();

		assertEquals(origMeasures.size(), measures.size());

		try {
			measures.add(origMeasures.get(0));
			assertTrue("Did not throw UnsupportedOperationException", false);
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}

		final int sizeBeforeAddition = origMeasures.size();
		final List<Durational> voice = new ArrayList<>();
		voice.add(Rest.getRest(Durations.WHOLE));
		final Map<Integer, List<Durational>> notes = new HashMap<>();
		notes.put(1, voice);
		origMeasures.add(new Measure(3, notes, TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Clefs.G));
		assertEquals(sizeBeforeAddition, staff.getMeasures().size());
	}

	@Test
	public void testIterator() {
		final List<Measure> origMeasures = getTestMeasures();
		final Staff staff = new Staff(origMeasures);

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
		final Staff staff = new Staff(origMeasures);

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
		final Staff staff = new Staff(measures);

		assertFalse(staff.hasPickupMeasure());

		for (int measureNumber = 1; measureNumber < measures.size(); ++measureNumber) {
			assertEquals(measureNumber, staff.getMeasure(measureNumber).getNumber());
		}
	}

	@Test
	public void testGetMeasureWithPickup() {
		final List<Measure> measures = new ArrayList<>();
		final List<Durational> voice = new ArrayList<>();
		voice.add(Rest.getRest(Durations.WHOLE));
		final Map<Integer, List<Durational>> notes = new HashMap<>();
		notes.put(1, voice);
		measures.add(new Measure(0, notes, TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Clefs.G));
		measures.addAll(getTestMeasures());

		final Staff staff = new Staff(measures);
		assertTrue(staff.hasPickupMeasure());

		for (int measureNumber = 0; measureNumber < measures.size(); ++measureNumber) {
			assertEquals(measureNumber, staff.getMeasure(measureNumber).getNumber());
		}
	}
}
