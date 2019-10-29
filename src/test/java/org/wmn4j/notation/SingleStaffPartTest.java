/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Clefs;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class SingleStaffPartTest {

	private final List<Measure> measures;
	private final int measureCount = 5;

	private KeySignature keySig = KeySignatures.CMAJ_AMIN;

	private Note C4 = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.HALF);
	private Note E4 = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.HALF);
	private Note G4 = Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF);
	private Note C4Quarter = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);

	SingleStaffPartTest() {
		final Map<Integer, List<Durational>> noteVoice = new HashMap<>();
		noteVoice.put(0, new ArrayList<>());
		noteVoice.get(0).add(C4Quarter);
		noteVoice.get(0).add(Rest.of(Durations.QUARTER));
		noteVoice.get(0).add(Chord.of(C4, E4, G4));

		final Map<Integer, List<Durational>> noteVoices = new HashMap<>();
		noteVoices.put(0, noteVoice.get(0));
		noteVoices.put(1, new ArrayList<>());
		noteVoices.get(1).add(Rest.of(Durations.QUARTER));
		noteVoices.get(1).add(C4);
		noteVoices.get(1).add(Rest.of(Durations.QUARTER));

		final List<Measure> measureList = new ArrayList<>();
		for (int i = 1; i <= this.measureCount; ++i) {
			measureList.add(Measure.of(i, noteVoices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G));
		}

		this.measures = Collections.unmodifiableList(measureList);
	}

	@Test
	void testImmutability() {
		final List<Measure> measuresCopy = new ArrayList<>(this.measures);
		final SingleStaffPart part = SingleStaffPart.of("Test part", Staff.of(measuresCopy));
		measuresCopy.set(0, null);
		assertTrue(part.getStaff().getMeasures().get(0) != null,
				"Modifying list used to create part modified part also.");
	}

	@Test
	void testGetName() {
		final SingleStaffPart part = SingleStaffPart.of("Test part", Staff.of(this.measures));
		assertTrue(part.getName().isPresent());
		assertEquals("Test part", part.getName().get());
	}

	@Test
	void testIsMultiStaff() {
		final SingleStaffPart part = SingleStaffPart.of("Test part", Staff.of(this.measures));
		assertFalse(part.isMultiStaff());
	}

	@Test
	void testGetStaff() {
		final SingleStaffPart part = SingleStaffPart.of("Test part", Staff.of(this.measures));
		final Staff staff = part.getStaff();
		assertEquals(5, staff.getMeasures().size());
	}

	@Test
	void getMeasure() {
		final SingleStaffPart part = SingleStaffPart.of("Test part", Staff.of(this.measures));
		final Measure m = part.getMeasure(1);
		assertTrue(m == this.measures.get(0));
	}

	@Test
	void testIterator() {
		final SingleStaffPart part = SingleStaffPart.of("Test part", Staff.of(this.measures));
		int measCount = 0;
		int measureNumber = 1;
		for (Measure m : part) {
			assertEquals(measureNumber++, m.getNumber(), "Iterator went through measures in incorrect order");
			++measCount;
		}

		assertEquals(this.measureCount, measCount,
				"Iterator did not go through all measures/went through measures multiple times.");
	}

	@Test
	void testIteratorImmutability() {
		final SingleStaffPart part = SingleStaffPart.of("Test part", Staff.of(this.measures));
		final Iterator<Measure> iter = part.iterator();
		iter.next();

		try {
			iter.remove();
			fail("Removing through iterator did not cause exception");
		} catch (final Exception e) {
			/* Ignore */
		}

		assertEquals(this.measureCount, part.getStaff().getMeasures().size());
	}

}
