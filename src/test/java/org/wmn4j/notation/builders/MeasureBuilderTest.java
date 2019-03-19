/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MeasureAttributes;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;
import org.wmn4j.notation.elements.TimeSignatures;

public class MeasureBuilderTest {

	public MeasureBuilderTest() {
	}

	@Test
	public void testBuildMeasureBySettingParameters() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addVoice();
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHT));

		final Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		final List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testBuildMeasureWithGivenAttributes() {
		final MeasureAttributes measureAttr = MeasureAttributes.of(TimeSignatures.SIX_EIGHT,
				KeySignatures.DFLATMAJ_BFLATMIN, Barline.DOUBLE, Clefs.F);
		final MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.addVoice();
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHT));

		final Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		final List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testSetParametersUsedOverMeasureAttributes() {
		final MeasureAttributes measureAttr = MeasureAttributes.of(TimeSignatures.THREE_EIGHT,
				KeySignatures.AMAJ_FSHARPMIN, Barline.REPEAT_RIGHT, Clefs.ALTO);
		final MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addVoice();
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHT));

		final Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		final List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.of(Pitch.of(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.of(Pitch.of(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testAdditionOfVoices() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		final Duration measureFillingDuration = TimeSignatures.SIX_EIGHT.getTotalDuration();

		builder.addToVoice(1, new RestBuilder(measureFillingDuration));
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(3, new RestBuilder(measureFillingDuration));
		assertEquals(2, builder.getNumberOfVoices());

		final Measure measure = builder.build();
		assertEquals(2, measure.getVoiceCount());
		assertEquals(1, measure.getVoice(1).size());
		assertTrue(measure.getVoice(1).contains(Rest.of(measureFillingDuration)));
		assertEquals(1, measure.getVoice(3).size());
		assertTrue(measure.getVoice(3).contains(Rest.of(measureFillingDuration)));
	}

	@Test
	public void testIsVoiceFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToVoice(0, new RestBuilder(Durations.QUARTER));
		assertFalse("Voice 0 is full for 4/4 measure after adding one quarter rest", builder.isFull(0));
		final NoteBuilder c = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.QUARTER);
		builder.addToVoice(0, c);
		assertFalse("Voice 0 is full for 4/4 measure after adding two quarters", builder.isFull(0));
		builder.addToVoice(0, c);
		builder.addToVoice(0, c);
		assertTrue("Voice 0 is not full when 4 quarter durations added to 4/4", builder.isFull(0));

		builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.CMAJ_AMIN);

		builder.addToVoice(0, new RestBuilder(Durations.QUARTER.addDot()))
				.addToVoice(0, new RestBuilder(Durations.QUARTER))
				.addToVoice(1, new RestBuilder(Durations.QUARTER.addDot()));
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET)).addToVoice(0,
				new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertFalse("Voice 0 is full when 6/8 measure is lacking one sixteenth triplet", builder.isFull(0));
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertTrue("Voice 0 is not full when 6/8 measure should be full.", builder.isFull(0));
		assertFalse("Voice 1 is full for 6/8 measure when it should not be", builder.isFull(1));
	}

	@Test
	public void testIsFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToVoice(0, new RestBuilder(Durations.QUARTER));
		assertFalse("builder for 4/4 is full after adding one quarter rest", builder.isFull());
		final NoteBuilder c = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.QUARTER);
		builder.addToVoice(0, c);
		assertFalse("builder for 4/4 is full only after adding two quarters", builder.isFull());
		builder.addToVoice(0, c);
		builder.addToVoice(0, c);
		assertTrue("builder is not full when 4 quarter durations added to 4/4", builder.isFull());

		builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT);

		builder.addToVoice(0, new RestBuilder(Durations.QUARTER.addDot()))
				.addToVoice(0, new RestBuilder(Durations.QUARTER))
				.addToVoice(1, new RestBuilder(Durations.QUARTER.addDot()));
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET)).addToVoice(0,
				new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertFalse("builder is full when 6/8 measure is lacking one sixteenth triplet", builder.isFull());
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertTrue("builder is not full when 6/8 measure should be full.", builder.isFull());
	}

	@Test
	public void testBuildingMeasureWithTiedNotes() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		final NoteBuilder first = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF);
		final NoteBuilder second = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF);

		first.addTieToFollowing(second);
		builder.addToVoice(1, first).addToVoice(1, second);
		final Measure measure = builder.build();

		final Note firstNote = (Note) measure.get(1, 0);
		final Note secondNote = (Note) measure.get(1, 1);
		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());
	}

	@Test
	public void testAllVoicesArePaddedWithRestsWhenBuilding() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.THREE_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF);
		NoteBuilder withEightDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.EIGHT);
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
		assertEquals(Rest.of(Durations.EIGHT), measure.get(1, 2));
	}

	@Test
	public void testIsVoiceOverflowing() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF);
		NoteBuilder withDottedHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF.addDot());
		builder.addToVoice(0, withHalfDuration);
		builder.addToVoice(1, withDottedHalfDuration);

		assertFalse(builder.isOverflowing(0));
		assertTrue(builder.isOverflowing(1));
	}

	@Test
	public void testIsOverflowing() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF);
		NoteBuilder withDottedHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF.addDot());
		builder.addToVoice(0, withHalfDuration);

		assertFalse(builder.isOverflowing());

		builder.addToVoice(1, withDottedHalfDuration);

		assertTrue(builder.isOverflowing());
	}

	@Test
	public void testTrimWithSingleElementPerVoice() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);
		NoteBuilder withHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF);
		NoteBuilder withDottedHalfDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.HALF.addDot());
		NoteBuilder withEightDuration = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.EIGHT);

		builder.addToVoice(0, withHalfDuration);
		builder.addToVoice(1, withDottedHalfDuration);
		builder.addToVoice(2, withEightDuration);

		builder.trim();
		assertEquals(
				"Voice that had one durational with exactly the duration of the time signature was affected by trim.",
				Durations.HALF, builder.get(0, 0).getDuration());
		assertEquals(
				"Voice that had one durational exceeding the duration specified by the time signature was not trimmed correctly.",
				Durations.HALF, builder.get(1, 0).getDuration());
		assertEquals(
				"Voice that had one durational shorter than the duration specified by the time signature was affected by trim.",
				Durations.EIGHT, builder.get(2, 0).getDuration());
		builder.build();
	}

	@Test
	public void testTrimWithMultipleElementsInVoice() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);

		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.QUARTER));
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.EIGHT));
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.QUARTER));
		builder.addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.QUARTER));

		builder.trim();

		assertEquals(
				"First element was affected by trim when it shouldn't have been.",
				Durations.QUARTER, builder.get(0, 0).getDuration());
		assertEquals(
				"Second element was affected by trim when it shouldn't have been.",
				Durations.EIGHT, builder.get(0, 1).getDuration());
		assertEquals(
				"Third element was not affected by trim when it should have been.",
				Durations.EIGHT, builder.get(0, 2).getDuration());

		assertFalse(builder.isOverflowing());
		builder.build();
	}

	@Test
	public void testBuildingWithoutPaddingAndTrimming() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.TWO_FOUR);

		NoteBuilder tooLongBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.WHOLE);
		NoteBuilder tooShortBuilder = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 2), Durations.QUARTER);

		builder.addToVoice(0, tooLongBuilder);
		builder.addToVoice(1, tooShortBuilder);

		final Note expectedVoice0Note = tooLongBuilder.build();
		final Note expectedVoice1Note = tooShortBuilder.build();

		assertTrue(builder.isOverflowing(0));
		assertTrue(!builder.isFull(1));

		final Measure incompleteMeasure = builder.build(false, false);

		assertEquals(1, incompleteMeasure.getVoice(0).size());
		assertEquals(expectedVoice0Note, incompleteMeasure.get(0, 0));

		assertEquals(1, incompleteMeasure.getVoice(1).size());
		assertEquals(expectedVoice1Note, incompleteMeasure.get(1, 0));
	}
}
