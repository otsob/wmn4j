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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MeasureTest {

	private Map<Integer, List<Durational>> singleNoteVoice = new HashMap<>();
	private Map<Integer, List<Durational>> multipleNoteVoices = new HashMap<>();

	private KeySignature keySig = KeySignatures.CMAJ_AMIN;

	private Note C4 = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.HALF);
	private Note E4 = Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.HALF);
	private Note G4 = Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF);
	private Note C4Quarter = Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);

	MeasureTest() {
		final List<Durational> voiceContents = new ArrayList<>();
		voiceContents.add(C4Quarter);
		voiceContents.add(Rest.of(Durations.QUARTER));
		voiceContents.add(Chord.of(C4, E4, G4));
		this.singleNoteVoice.put(0, voiceContents);

		this.multipleNoteVoices = new HashMap<>();
		this.multipleNoteVoices.put(0, voiceContents);
		this.multipleNoteVoices.put(1, new ArrayList<>());
		this.multipleNoteVoices.get(1).add(Rest.of(Durations.QUARTER));
		this.multipleNoteVoices.get(1).add(C4);
		this.multipleNoteVoices.get(1).add(Rest.of(Durations.QUARTER));
	}

	@Test
	void testCreatingIllegalMeasureThrowsException() {
		// Test exceptions thrown correctly for illegal arguments
		try {
			final Measure m = Measure.of(-1, this.multipleNoteVoices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
			fail("Exception not thrown");
		} catch (final Exception e) {
			assertTrue(e instanceof IllegalArgumentException);
		}

		try {
			final Measure m = Measure.of(1, null, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
			fail("Exception not thrown");
		} catch (final Exception e) {
			assertTrue(e instanceof NullPointerException);
		}
	}

	@Test
	void testVoicesImmutable() {
		final Map<Integer, List<Durational>> voices = new HashMap<>();
		final List<Durational> voice = new ArrayList<>();
		voice.add(C4);
		voices.put(0, voice);

		final Measure measure = Measure.of(1, voices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);

		// Test that modifying the original voices list does no affect measure created
		// using it.
		voices.get(0).add(C4);
		assertEquals(1, measure.getVoice(0).size(), "Modifying list from which measure is created changes measure");
	}

	@Test
	void testGetVoice() {
		final Measure m = Measure.of(1, multipleNoteVoices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
		assertTrue(m.getVoice(1).contains(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.HALF)));
		assertTrue(m.getVoice(0).contains(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER)));

		final List<Durational> voice = m.getVoice(0);
		try {
			voice.add(Rest.of(Durations.QUARTER));
			fail("Failed to throw exception for disabled adding");
		} catch (final Exception e) {
			/* Do nothing */
		}
	}

	@Test
	void testGetNumber() {
		assertEquals(1, Measure.of(1, singleNoteVoice, TimeSignatures.FOUR_FOUR, keySig, Clefs.G).getNumber());
		assertEquals(512, Measure.of(512, singleNoteVoice, TimeSignatures.FOUR_FOUR, keySig, Clefs.G).getNumber());
	}

	@Test
	void testIteratorWithSingleVoiceMeasure() {
		final Measure singleVoiceMeasure = Measure.of(1, singleNoteVoice, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
		final int noteCount = 0;

		final List<Durational> expected = singleNoteVoice.get(0);
		final List<Durational> found = new ArrayList<>();

		for (Durational e : singleVoiceMeasure) {
			found.add(e);
		}

		assertEquals(expected.size(), found.size());

		for (Durational e : expected) {
			assertTrue(found.contains(e));
		}
	}

	@Test
	void testIteratorWithMultiVoiceMeasure() {
		final Measure multiVoiceMeasure = Measure.of(1, multipleNoteVoices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
		final int noteCount = 0;

		final List<Durational> expected = new ArrayList<>();
		expected.addAll(multipleNoteVoices.get(0));
		expected.addAll(multipleNoteVoices.get(1));

		final List<Durational> found = new ArrayList<>();

		for (Durational e : multiVoiceMeasure) {
			found.add(e);
		}

		assertEquals(expected.size(), found.size());

		for (Durational e : expected) {
			assertTrue(found.contains(e));
		}
	}

	@Test
	void testIteratorRemoveDisabled() {
		final Measure m = Measure.of(1, multipleNoteVoices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);

		try {
			final Iterator<Durational> iter = m.iterator();
			iter.next();
			iter.remove();
			fail("Expected exception was not thrown");
		} catch (final Exception e) {
			assertTrue(e instanceof UnsupportedOperationException);
		}
	}

	@Test
	void testIteratorWithEmptyMeasure() {
		final Map<Integer, List<Durational>> voices = new HashMap<>();
		voices.put(0, new ArrayList<>());
		final Measure emptyMeasure = Measure.of(1, voices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);

		int noteElemCount = 0;

		for (Durational n : emptyMeasure) {
			++noteElemCount;
			break;
		}

		assertEquals(0, noteElemCount);
	}

	@Test
	void testIteratorWithNonContiguousVoiceNumbers() {
		final List<Durational> noteList = this.singleNoteVoice.get(0);
		final Map<Integer, List<Durational>> noteVoices = new HashMap<>();
		noteVoices.put(1, noteList);
		noteVoices.put(3, noteList);

		final Measure measure = Measure.of(1, noteVoices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);

		final List<Durational> expected = new ArrayList<>(noteList);
		for (Durational d : noteList) {
			expected.add(d);
		}

		int count = 0;
		int indexInExpected = 0;
		for (Durational d : measure) {
			assertEquals(expected.get(indexInExpected++), d, "Iterator did not return the expected object");
			++count;
		}

		assertEquals(expected.size(),
				count, "Iterator iterated through a number of objects different from size of expected");
	}

	@Test
	void testFullMeasureRest() {
		final List<Durational> noteList = this.singleNoteVoice.get(0);
		final Map<Integer, List<Durational>> noteVoices = new HashMap<>();
		noteVoices.put(1, noteList);
		noteVoices.put(3, noteList);

		final Measure measure = Measure.of(1, noteVoices, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
		assertFalse(measure.isFullMeasureRest());

		final MeasureAttributes attributes = MeasureAttributes
				.of(TimeSignatures.FOUR_FOUR, keySig, Barline.SINGLE, Clefs.G);
		final Measure fullMeasureRest = Measure.restMeasureOf(2, attributes);
		assertTrue(fullMeasureRest.isFullMeasureRest());

		try {
			fullMeasureRest.get(0, 0);
			fail("Trying to get a durational from full meausure rest did not throw exception");
		} catch (NoSuchElementException exception) {
			/* Do nothing */
		}
	}

	@Test
	void testPickUpMeasure() {
		final MeasureAttributes attributes = MeasureAttributes
				.of(TimeSignatures.FOUR_FOUR, keySig, Barline.SINGLE, Clefs.G);
		final Measure pickupMeasure = Measure.pickupOf(this.multipleNoteVoices, attributes);

		assertTrue(pickupMeasure.isPickUp());

		final Measure nonPickup = Measure.of(1, this.multipleNoteVoices, attributes);

		assertFalse(nonPickup.isPickUp());
	}
}
