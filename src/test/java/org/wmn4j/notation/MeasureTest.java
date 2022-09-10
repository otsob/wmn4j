/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.access.Offset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
	private Map<Integer, List<Durational>> multipleNoteVoices;

	private KeySignature keySig = KeySignatures.CMAJ_AMIN;

	private Note C4 = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.HALF);
	private Note E4 = Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.HALF);
	private Note G4 = Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.HALF);
	private Note C4Quarter = Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);

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
		assertEquals(1, measure.getVoiceSize(0), "Modifying list from which measure is created changes measure");
	}

	@Test
	void testGetNumber() {
		assertEquals(1, Measure.of(1, singleNoteVoice, TimeSignatures.FOUR_FOUR, keySig, Clefs.G).getNumber());
		assertEquals(512, Measure.of(512, singleNoteVoice, TimeSignatures.FOUR_FOUR, keySig, Clefs.G).getNumber());
	}

	@Test
	void testIteratorWithSingleVoiceMeasure() {
		final Measure singleVoiceMeasure = Measure.of(1, singleNoteVoice, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);

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
		final Measure fullMeasureRest = Measure.restMeasureOf(2, attributes, null);
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
		final Measure pickupMeasure = Measure.pickupOf(this.multipleNoteVoices, attributes, null);

		assertTrue(pickupMeasure.isPickup());

		final Measure nonPickup = Measure.of(1, this.multipleNoteVoices, attributes, null);

		assertFalse(nonPickup.isPickup());
	}

	@Test
	void testMeasureWithChordSymbols() {
		final MeasureAttributes attributes = MeasureAttributes
				.of(TimeSignatures.FOUR_FOUR, keySig, Barline.SINGLE, Clefs.G);

		final Collection<Offset<ChordSymbol>> chords = new ArrayList<>();
		chords.add(new Offset<>(
				ChordSymbol.of(ChordSymbol.Base.MAJOR, PitchName.of(Pitch.Base.C, Pitch.Accidental.NATURAL), null,
						null), null));

		chords.add(new Offset<>(
				ChordSymbol.of(ChordSymbol.Base.MAJOR, PitchName.of(Pitch.Base.G, Pitch.Accidental.NATURAL),
						null, Arrays.asList(
								ChordSymbol.extension(ChordSymbol.Extension.Type.PLAIN, Pitch.Accidental.NATURAL, 7))),
				Durations.HALF.addDot()));

		chords.add(new Offset<>(
				ChordSymbol.of(ChordSymbol.Base.MAJOR, PitchName.of(Pitch.Base.D, Pitch.Accidental.NATURAL),
						PitchName.of(Pitch.Base.A, Pitch.Accidental.NATURAL),
						null), Durations.HALF));

		final Measure measure = Measure.pickupOf(this.multipleNoteVoices, attributes, chords);
		assertTrue(measure.containsChordSymbols());
		final var chordSymbols = measure.getChordSymbols();
		assertEquals(3, chordSymbols.size());
		assertTrue(chordSymbols.get(0).getDuration().isEmpty());
		assertEquals(ChordSymbol.of(ChordSymbol.Base.MAJOR, PitchName.of(Pitch.Base.C, Pitch.Accidental.NATURAL), null,
				null), chordSymbols.get(0).get());

		assertEquals(Durations.HALF, chordSymbols.get(1).getDuration().get());
		assertEquals(ChordSymbol.of(ChordSymbol.Base.MAJOR, PitchName.of(Pitch.Base.D, Pitch.Accidental.NATURAL),
				PitchName.of(Pitch.Base.A, Pitch.Accidental.NATURAL),
				null), chordSymbols.get(1).get());

		assertEquals(Durations.HALF.addDot(), chordSymbols.get(2).getDuration().get());
		assertEquals(ChordSymbol.of(ChordSymbol.Base.MAJOR, PitchName.of(Pitch.Base.G, Pitch.Accidental.NATURAL),
						null,
						Arrays.asList(ChordSymbol.extension(ChordSymbol.Extension.Type.PLAIN, Pitch.Accidental.NATURAL, 7))),
				chordSymbols.get(2).get());
	}
}
