/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Clefs;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.MeasureAttributes;
import org.wmn4j.notation.MultiStaffPart;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignatures;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class PartBuilderTest {

	private final Map<Integer, List<DurationalBuilder>> measureContents;
	private final MeasureAttributes measureAttr;

	PartBuilderTest() {
		NoteBuilder C4 = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.HALF);
		NoteBuilder E4 = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.HALF);
		NoteBuilder G4 = new NoteBuilder(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF);
		NoteBuilder C4Quarter = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);

		final Map<Integer, List<DurationalBuilder>> noteVoice = new HashMap<>();
		noteVoice.put(0, new ArrayList<>());
		noteVoice.get(0).add(C4Quarter);
		noteVoice.get(0).add(new RestBuilder(Durations.QUARTER));
		final ChordBuilder chordBuilder = new ChordBuilder(C4);
		chordBuilder.add(E4).add(G4);

		noteVoice.get(0).add(chordBuilder);

		final Map<Integer, List<DurationalBuilder>> noteVoices = new HashMap<>();
		noteVoices.put(0, noteVoice.get(0));
		noteVoices.put(1, new ArrayList<>());
		noteVoices.get(1).add(new RestBuilder(Durations.QUARTER));
		noteVoices.get(1).add(C4);
		noteVoices.get(1).add(new RestBuilder(Durations.QUARTER));

		this.measureContents = Collections.unmodifiableMap(noteVoices);
		this.measureAttr = MeasureAttributes.of(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN,
				Barline.SINGLE, Clefs.G);
	}

	private MeasureBuilder getMeasureBuilder(int number) {
		final MeasureBuilder builder = new MeasureBuilder(number, this.measureAttr);
		for (Integer voiceNum : this.measureContents.keySet()) {
			builder.addVoice(this.measureContents.get(voiceNum));
		}

		return builder;
	}

	@Test
	void testGetStaffCount() {
		final int measureCount = 5;
		final PartBuilder builder = new PartBuilder("");
		for (int i = 1; i <= measureCount; ++i) {
			final MeasureBuilder m = getMeasureBuilder(i);
			builder.addToStaff(0, m);
		}
		assertEquals(1, builder.getStaffCount());

		for (int i = 1; i <= measureCount; ++i) {
			final MeasureBuilder m = getMeasureBuilder(i);
			builder.addToStaff(1, m);
		}
		assertEquals(2, builder.getStaffCount());
	}

	@Test
	void testGetMeasureCount() {
		PartBuilder builder = new PartBuilder("Test");
		assertEquals(0, builder.getMeasureCount());

		builder.addToStaff(1, new MeasureBuilder(1));
		assertEquals(1, builder.getMeasureCount());

		builder.addToStaff(2, new MeasureBuilder(1));
		assertEquals(1, builder.getMeasureCount());

		builder.addToStaff(1, new MeasureBuilder(2));
		assertEquals(2, builder.getMeasureCount());
	}

	@Test
	void testGetStaffNumbers() {
		PartBuilder builder = new PartBuilder("Test");
		assertTrue(builder.getStaffNumbers().isEmpty());

		builder.add(new MeasureBuilder(1));
		assertEquals(1, builder.getStaffNumbers().size());
		assertTrue(builder.getStaffNumbers().contains(1));

		builder.addToStaff(2, new MeasureBuilder(1));
		assertEquals(2, builder.getStaffNumbers().size());
		assertTrue(builder.getStaffNumbers().contains(1));
		assertTrue(builder.getStaffNumbers().contains(2));
	}

	@Test
	void testGetMeasureBuildersForStaff() {
		PartBuilder builder = new PartBuilder("Test");
		assertTrue(builder.getStaffNumbers().isEmpty());

		builder.addToStaff(1, new MeasureBuilder(1));
		builder.addToStaff(1, new MeasureBuilder(2));
		builder.addToStaff(2, new MeasureBuilder(1));

		assertEquals(2, builder.getStaffContents(1).size());
		assertEquals(1, builder.getStaffContents(1).get(0).getNumber());
		assertEquals(2, builder.getStaffContents(1).get(1).getNumber());

		assertEquals(1, builder.getStaffContents(2).size());
		assertEquals(1, builder.getStaffContents(2).get(0).getNumber());

		try {
			builder.getStaffContents(3);
			fail("Trying to get builder from a voice that is not set in the builder did not throw exception");
		} catch (NoSuchElementException e) {
			/* Do nothing */
		}
	}

	@Test
	void testBuildSingleStaffPart() {
		final int measureCount = 5;
		final PartBuilder builder = new PartBuilder("");
		for (int i = 1; i <= measureCount; ++i) {
			final MeasureBuilder m = getMeasureBuilder(i);
			builder.add(m);
		}

		final Part part = builder.build();
		assertTrue(part instanceof SingleStaffPart);
		assertFalse(part.isMultiStaff());
	}

	@Test
	void testBuildMultiStaffPart() {
		final int measureCount = 5;
		final PartBuilder builder = new PartBuilder("");
		for (int i = 1; i <= measureCount; ++i) {
			builder.addToStaff(1, getMeasureBuilder(i));
			builder.addToStaff(2, getMeasureBuilder(i));
		}

		final Part part = builder.build();
		assertTrue(part.isMultiStaff());
		assertTrue(part instanceof MultiStaffPart);
		final MultiStaffPart mpart = (MultiStaffPart) part;
		final List<Integer> staffNumbers = mpart.getStaffNumbers();
		assertEquals(2, staffNumbers.size());
		assertTrue(staffNumbers.contains(1));
		assertTrue(staffNumbers.contains(2));

		final Staff staff1 = mpart.getStaff(1);
		assertEquals(5, staff1.getMeasureCount());

		final Staff staff2 = mpart.getStaff(2);
		assertEquals(5, staff2.getMeasureCount());
	}

	@Test
	void testBuildPartWithTieBetweenMeasures() {

		final MeasureBuilder firstMeasureBuilder = new MeasureBuilder(1);
		final NoteBuilder firstNoteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.WHOLE);
		firstMeasureBuilder.addToVoice(1, firstNoteBuilder);

		final MeasureBuilder secondMeasureBuilder = new MeasureBuilder(2);
		final NoteBuilder secondNoteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.WHOLE);
		firstNoteBuilder.addTieToFollowing(secondNoteBuilder);
		secondMeasureBuilder.addToVoice(1, secondNoteBuilder);

		final PartBuilder partBuilder = new PartBuilder("TiedMeasures");
		partBuilder.add(firstMeasureBuilder).add(secondMeasureBuilder);
		final Part part = partBuilder.build();

		final Note firstNote = (Note) part.getMeasure(SingleStaffPart.STAFF_NUMBER, 1).get(1, 0);
		final Note secondNote = (Note) part.getMeasure(SingleStaffPart.STAFF_NUMBER, 2).get(1, 0);

		assertTrue(firstNote.getFollowingTiedNote().isPresent());
		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());
	}

	@Test
	void testGivenBuilderWithMultipleStavesWhenBuiltPartsHavePadding() {
		final MeasureBuilder firstMeasureBuilder = new MeasureBuilder(1);
		final NoteBuilder firstNoteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.WHOLE);
		firstMeasureBuilder.addToVoice(1, firstNoteBuilder);

		final MeasureBuilder secondMeasureBuilder = new MeasureBuilder(2);
		final NoteBuilder secondNoteBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.WHOLE);
		secondMeasureBuilder.addToVoice(1, secondNoteBuilder);

		final PartBuilder partBuilder = new PartBuilder("Part");
		partBuilder.addToStaff(1, firstMeasureBuilder).addToStaff(1, secondMeasureBuilder);
		partBuilder.addToStaff(2, firstMeasureBuilder);
		final Part part = partBuilder.build();

		final Note firstNote = (Note) part.getMeasure(SingleStaffPart.STAFF_NUMBER, 1).get(1, 0);
		final Note secondNote = (Note) part.getMeasure(SingleStaffPart.STAFF_NUMBER, 2).get(1, 0);

		assertEquals(firstNoteBuilder.build(), firstNote);
		assertEquals(secondNoteBuilder.build(), secondNote);

		assertFalse(part.getMeasure(1, 1).isFullMeasureRest());
		assertFalse(part.getMeasure(1, 2).isFullMeasureRest());

		assertFalse(part.getMeasure(2, 1).isFullMeasureRest());
		assertTrue(part.getMeasure(2, 2).isFullMeasureRest());
	}

	@Test
	void testGivenBuilderWithMultipleStavesAndTimeSignatureChangesWhenBuiltMeasuresHaveCorrectTimeSignatures() {
		final MeasureBuilder firstMeasureBuilder = new MeasureBuilder(1);
		firstMeasureBuilder.setTimeSignature(TimeSignatures.SIX_EIGHT);
		final MeasureBuilder secondMeasureBuilder = new MeasureBuilder(2);
		secondMeasureBuilder.setTimeSignature(TimeSignatures.FOUR_FOUR);
		final MeasureBuilder thirdMeasureBuilder = new MeasureBuilder(3);
		thirdMeasureBuilder.setTimeSignature(TimeSignatures.THREE_FOUR);

		PartBuilder builder = new PartBuilder("Test part");
		builder.addToStaff(1, firstMeasureBuilder).addToStaff(1, secondMeasureBuilder)
				.addToStaff(1, thirdMeasureBuilder);
		builder.addToStaff(2, firstMeasureBuilder).addToStaff(2, secondMeasureBuilder);
		builder.addToStaff(3, firstMeasureBuilder);

		Part part = builder.build();
		final Measure staffOneMeasureOne = part.getMeasure(1, 1);
		final Measure staffOneMeasureTwo = part.getMeasure(1, 2);
		final Measure staffOneMeasureThree = part.getMeasure(1, 3);

		assertEquals(TimeSignatures.SIX_EIGHT, staffOneMeasureOne.getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, staffOneMeasureTwo.getTimeSignature());
		assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureThree.getTimeSignature());

		Measure staffTwoMeasureOne = part.getMeasure(2, 1);
		Measure staffTwoMeasureTwo = part.getMeasure(2, 2);
		Measure staffTwoMeasureThree = part.getMeasure(2, 3);

		assertEquals(TimeSignatures.SIX_EIGHT, staffTwoMeasureOne.getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, staffTwoMeasureTwo.getTimeSignature());
		assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureThree.getTimeSignature());

		Measure staffThreeMeasureOne = part.getMeasure(3, 1);
		Measure staffThreeMeasureTwo = part.getMeasure(3, 2);
		Measure staffThreeMeasureThree = part.getMeasure(3, 3);

		assertEquals(TimeSignatures.SIX_EIGHT, staffThreeMeasureOne.getTimeSignature());
		assertEquals(TimeSignatures.FOUR_FOUR, staffThreeMeasureTwo.getTimeSignature());
		assertEquals(TimeSignatures.THREE_FOUR, staffThreeMeasureThree.getTimeSignature());
	}
}
