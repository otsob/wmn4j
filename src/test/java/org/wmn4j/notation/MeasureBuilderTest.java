/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MeasureBuilderTest {

	@Test
	void testBuildMeasureBySettingParameters() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));

		assertEquals(1, builder.getVoiceCount());

		final Measure measure = builder.build();
		assertNotNull(measure);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 2));
	}

	@Test
	void testBuildMeasureWithGivenAttributes() {
		final MeasureAttributes measureAttr = MeasureAttributes.of(TimeSignatures.SIX_EIGHT,
				KeySignatures.DFLATMAJ_BFLATMIN, Barline.DOUBLE, Clefs.F);
		final MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));

		assertEquals(1, builder.getVoiceCount());

		final Measure measure = builder.build();
		assertNotNull(measure);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());

		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 2));
	}

	@Test
	void testSetParametersUsedOverMeasureAttributes() {
		final MeasureAttributes measureAttr = MeasureAttributes.of(TimeSignatures.THREE_EIGHT,
				KeySignatures.AMAJ_FSHARPMIN, Barline.REPEAT_RIGHT, Clefs.ALTO);
		final MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH));

		assertEquals(1, builder.getVoiceCount());

		final Measure measure = builder.build();
		assertNotNull(measure);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Durations.EIGHTH), measure.get(0, 2));
	}

	@Test
	void testAdditionOfVoices() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		final Duration measureFillingDuration = TimeSignatures.SIX_EIGHT.getTotalDuration();

		builder.addToVoice(1, new RestBuilder(measureFillingDuration));
		assertEquals(1, builder.getVoiceCount());
		builder.addToVoice(3, new RestBuilder(measureFillingDuration));
		assertEquals(2, builder.getVoiceCount());

		final Measure measure = builder.build();
		assertEquals(2, measure.getVoiceCount());
		assertEquals(1, measure.getVoiceSize(1));
		assertEquals(Rest.of(measureFillingDuration), measure.get(1, 0));
		assertEquals(1, measure.getVoiceSize(3));
		assertEquals(Rest.of(measureFillingDuration), measure.get(3, 0));
	}

	@Test
	void testIsVoiceFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToVoice(0, new RestBuilder(Durations.QUARTER));
		assertFalse(builder.isFull(0), "Voice 0 is full for 4/4 measure after adding one quarter rest");
		final NoteBuilder c = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.QUARTER);
		builder.addToVoice(0, c);
		assertFalse(builder.isFull(0), "Voice 0 is full for 4/4 measure after adding two quarters");
		builder.addToVoice(0, c);
		builder.addToVoice(0, c);
		assertTrue(builder.isFull(0), "Voice 0 is not full when 4 quarter durations added to 4/4");

		builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.CMAJ_AMIN);

		builder.addToVoice(0, new RestBuilder(Durations.QUARTER.addDot()))
				.addToVoice(0, new RestBuilder(Durations.QUARTER))
				.addToVoice(1, new RestBuilder(Durations.QUARTER.addDot()));
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET)).addToVoice(0,
				new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertFalse(builder.isFull(0), "Voice 0 is full when 6/8 measure is lacking one sixteenth triplet");
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertTrue(builder.isFull(0), "Voice 0 is not full when 6/8 measure should be full.");
		assertFalse(builder.isFull(1), "Voice 1 is full for 6/8 measure when it should not be");
	}

	@Test
	void testIsFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToVoice(0, new RestBuilder(Durations.QUARTER));
		assertFalse(builder.isFull(), "builder for 4/4 is full after adding one quarter rest");
		final NoteBuilder c = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.QUARTER);
		builder.addToVoice(0, c);
		assertFalse(builder.isFull(), "builder for 4/4 is full only after adding two quarters");
		builder.addToVoice(0, c);
		builder.addToVoice(0, c);
		assertTrue(builder.isFull(), "builder is not full when 4 quarter durations added to 4/4");

		builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT);

		builder.addToVoice(0, new RestBuilder(Durations.QUARTER.addDot()))
				.addToVoice(0, new RestBuilder(Durations.QUARTER))
				.addToVoice(1, new RestBuilder(Durations.QUARTER.addDot()));
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET)).addToVoice(0,
				new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertFalse(builder.isFull(), "builder is full when 6/8 measure is lacking one sixteenth triplet");
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertTrue(builder.isFull(), "builder is not full when 6/8 measure should be full.");
	}

	@Test
	void testBuildingMeasureWithTiedNotes() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		final NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.HALF);
		final NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.HALF);

		first.addTieToFollowing(second);
		builder.addToVoice(1, first).addToVoice(1, second);
		final Measure measure = builder.build();

		final Note firstNote = (Note) measure.get(1, 0);
		final Note secondNote = (Note) measure.get(1, 1);
		assertTrue(firstNote.getFollowingTiedNote().isPresent());
		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());
	}

	@Test
	void testAllVoicesArePaddedWithRestsWhenBuilding() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.THREE_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.HALF);
		NoteBuilder withEightDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.EIGHTH);
		builder.addToVoice(0, withHalfDuration);
		builder.addToVoice(1, withHalfDuration);
		builder.addToVoice(1, withEightDuration);

		final Measure measure = builder.build();
		final Note halfDurationNote = withHalfDuration.build();
		final Note eightDurationNote = withEightDuration.build();

		assertEquals(halfDurationNote, measure.get(0, 0));
		assertEquals(Rest.of(Durations.QUARTER), measure.get(0, 1));

		assertEquals(halfDurationNote, measure.get(1, 0));
		assertEquals(eightDurationNote, measure.get(1, 1));
		assertEquals(Rest.of(Durations.EIGHTH), measure.get(1, 2));
	}

	@Test
	void testIsVoiceOverflowing() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.HALF);
		NoteBuilder withDottedHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.HALF.addDot());
		builder.addToVoice(0, withHalfDuration);
		builder.addToVoice(1, withDottedHalfDuration);

		assertFalse(builder.isOverflowing(0));
		assertTrue(builder.isOverflowing(1));
	}

	@Test
	void testIsOverflowing() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.HALF);
		NoteBuilder withDottedHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.HALF.addDot());
		builder.addToVoice(0, withHalfDuration);

		assertFalse(builder.isOverflowing());

		builder.addToVoice(1, withDottedHalfDuration);

		assertTrue(builder.isOverflowing());
	}

	@Test
	void testTrimWithSingleElementPerVoice() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.HALF);
		NoteBuilder withDottedHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.HALF.addDot());
		NoteBuilder withEightDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.EIGHTH);

		builder.addToVoice(0, withHalfDuration);
		builder.addToVoice(1, withDottedHalfDuration);
		builder.addToVoice(2, withEightDuration);

		builder.trim();
		assertEquals(Durations.HALF, builder.get(0, 0).getDuration(),
				"Voice that had one durational with exactly the duration of the time signature was affected by trim.");
		assertEquals(Durations.HALF, builder.get(1, 0).getDuration(),
				"Voice that had one durational exceeding the duration specified by the time signature was not trimmed correctly.");
		assertEquals(Durations.EIGHTH, builder.get(2, 0).getDuration(),
				"Voice that had one durational shorter than the duration specified by the time signature was affected by trim.");
		builder.build();
	}

	@Test
	void testTrimWithMultipleElementsInVoice() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);

		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.QUARTER));
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.EIGHTH));
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.QUARTER));
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2), Durations.QUARTER));

		builder.trim();

		assertEquals(Durations.QUARTER, builder.get(0, 0).getDuration(),
				"First element was affected by trim when it shouldn't have been.");
		assertEquals(Durations.EIGHTH, builder.get(0, 1).getDuration(),
				"Second element was affected by trim when it shouldn't have been.");
		assertEquals(Durations.EIGHTH, builder.get(0, 2).getDuration(),
				"Third element was not affected by trim when it should have been.");

		assertFalse(builder.isOverflowing());
		builder.build();
	}

	@Test
	void testBuildingWithoutPaddingAndTrimming() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);

		NoteBuilder tooLongBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.WHOLE);
		NoteBuilder tooShortBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 2),
				Durations.QUARTER);

		builder.addToVoice(0, tooLongBuilder);
		builder.addToVoice(1, tooShortBuilder);

		final Note expectedVoice0Note = tooLongBuilder.build();
		final Note expectedVoice1Note = tooShortBuilder.build();

		assertTrue(builder.isOverflowing(0));
		assertTrue(!builder.isFull(1));

		final Measure incompleteMeasure = builder.build(false, false);

		assertEquals(1, incompleteMeasure.getVoiceSize(0));
		assertEquals(expectedVoice0Note, incompleteMeasure.get(0, 0));

		assertEquals(1, incompleteMeasure.getVoiceSize(1));
		assertEquals(expectedVoice1Note, incompleteMeasure.get(1, 0));
	}

	@Test
	void testBuildingFullMeasureRest() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		final Measure fullMeasureRest = builder.build();
		assertTrue(fullMeasureRest.isFullMeasureRest());
	}

	@Test
	void testBuildingWithAttributesOfAnotherBuilder() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		final MeasureBuilder withSameAttributes = MeasureBuilder.withAttributesOf(builder);

		assertEquals(TimeSignatures.SIX_EIGHT, withSameAttributes.getTimeSignature());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, withSameAttributes.getKeySignature());
		assertEquals(Barline.DOUBLE, withSameAttributes.getRightBarline());
		assertEquals(Clefs.F, withSameAttributes.getClef());
	}
}
