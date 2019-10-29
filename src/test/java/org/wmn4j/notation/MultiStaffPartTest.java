/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.TestHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MultiStaffPartTest {

	private final Map<Integer, Staff> testStaves;

	MultiStaffPartTest() {
		this.testStaves = new HashMap<>();
		final List<Measure> measures = new ArrayList<>();
		for (int i = 1; i <= 5; ++i) {
			measures.add(TestHelper.getTestMeasure(i));
		}

		this.testStaves.put(1, Staff.of(measures));
		this.testStaves.put(2, Staff.of(measures));
	}

	@Test
	void testPartHasName() {
		final Map<Integer, Staff> testStavesCopy = new HashMap<>(this.testStaves);

		final String partName = "Test staff";
		final MultiStaffPart part = MultiStaffPart.of(partName, testStavesCopy);
		assertTrue(part.getName().isPresent());
		assertEquals(partName, part.getName().get());
	}

	@Test
	void testImmutability() {
		final Map<Integer, Staff> testStavesCopy = new HashMap<>(this.testStaves);
		final MultiStaffPart part = MultiStaffPart.of("Test staff", testStavesCopy);

		final List<Measure> testMeasures = new ArrayList<>();
		testMeasures.add(TestHelper.getTestMeasure(1));
		testStavesCopy.put(1, Staff.of(testMeasures));

		assertTrue(part.getStaff(1).getMeasure(1) == this.testStaves.get(1).getMeasure(1),
				"Modifying map that was used to create MultiStaffPart changed the MultiStaffPart.");
	}

	@Test
	void testIterator() {
		final MultiStaffPart part = MultiStaffPart.of("Test staff", this.testStaves);

		final int expectedCount = 10;
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
			} else {
				staffIndex = 2;
			}
		}

		assertEquals(expectedCount, count, "Iterator went through an unexpected number of measures.");
	}

	@Test
	void testIteratorWithPickupMeasure() {
		final Map<Integer, Staff> staves = new HashMap<>();
		final List<Measure> measures = new ArrayList<>();
		for (int i = 0; i <= 4; ++i) {
			measures.add(TestHelper.getTestMeasure(i));
		}

		staves.put(1, Staff.of(measures));
		staves.put(2, Staff.of(measures));

		final MultiStaffPart part = MultiStaffPart.of("Test Staff", staves);

		final int expectedCount = 10;
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
			} else {
				staffIndex = 2;
			}
		}

		assertEquals(expectedCount, count, "Iterator went through an unexpected number of measures.");
	}

	@Test
	void testIteratorImmutability() {
		final MultiStaffPart part = MultiStaffPart.of("Test staff", this.testStaves);
		final Iterator<Measure> iterator = part.iterator();
		iterator.next();
		try {
			iterator.remove();
			fail("Did not throw exception when calling remove on iterator");
		} catch (final Exception e) {
			/* Do nothing */
		}
	}
}
