/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.wmn4j.notation.TestHelper;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MultiStaffPart;
import org.wmn4j.notation.elements.Staff;

/**
 *
 * @author Otso Björklund
 */
public class MultiStaffPartTest {

	private final Map<Integer, Staff> testStaves;

	public MultiStaffPartTest() {
		this.testStaves = new HashMap<>();
		List<Measure> measures = new ArrayList<>();
		for (int i = 1; i <= 5; ++i)
			measures.add(TestHelper.getTestMeasure(i));

		this.testStaves.put(1, new Staff(measures));
		this.testStaves.put(2, new Staff(measures));
	}

	@Test
	public void testImmutability() {
		Map<Integer, Staff> testStavesCopy = new HashMap<>(this.testStaves);
		MultiStaffPart part = new MultiStaffPart("Test staff", testStavesCopy);

		List<Measure> testMeasures = new ArrayList<>();
		testMeasures.add(TestHelper.getTestMeasure(1));
		testStavesCopy.put(1, new Staff(testMeasures));

		assertTrue("Modifying map that was used to create MultiStaffPart changed the MultiStaffPart.",
				part.getStaff(1).getMeasure(1) == this.testStaves.get(1).getMeasure(1));
	}

	@Test
	public void testIterator() {
		MultiStaffPart part = new MultiStaffPart("Test staff", this.testStaves);

		int expectedCount = 10;
		int count = 0;
		int prevMeasureNumber = 0;

		int staffIndex = 1;
		int measureNumber = 1;

		for (Measure m : part) {
			++count;
			assertTrue(m.getNumber() >= prevMeasureNumber);
			prevMeasureNumber = m.getNumber();

			assertTrue(m == this.testStaves.get(staffIndex).getMeasure(measureNumber));

			if (staffIndex == 2) {
				staffIndex = 1;
				++measureNumber;
			} else
				staffIndex = 2;
		}

		assertEquals("Iterator went through an unexpected number of measures.", expectedCount, count);
	}

	@Test
	public void testIteratorWithPickupMeasure() {
		Map<Integer, Staff> staves = new HashMap<>();
		List<Measure> measures = new ArrayList<>();
		for (int i = 0; i <= 4; ++i)
			measures.add(TestHelper.getTestMeasure(i));

		staves.put(1, new Staff(measures));
		staves.put(2, new Staff(measures));

		MultiStaffPart part = new MultiStaffPart("Test Staff", staves);

		int expectedCount = 10;
		int count = 0;
		int prevMeasureNumber = 0;

		int staffIndex = 1;
		int measureNumber = 0;

		for (Measure m : part) {
			++count;
			assertTrue(m.getNumber() >= prevMeasureNumber);
			prevMeasureNumber = m.getNumber();

			assertTrue(m == staves.get(staffIndex).getMeasure(measureNumber));

			if (staffIndex == 2) {
				staffIndex = 1;
				++measureNumber;
			} else
				staffIndex = 2;
		}

		assertEquals("Iterator went through an unexpected number of measures.", expectedCount, count);
	}

	@Test
	public void testIteratorImmutability() {
		MultiStaffPart part = new MultiStaffPart("Test staff", this.testStaves);
		Iterator<Measure> iterator = part.iterator();
		iterator.next();
		try {
			iterator.remove();
			fail("Did not throw exception when calling remove on iterator");
		} catch (Exception e) {
			/* Do nothing */ }
	}
}
