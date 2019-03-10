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
import org.wmn4j.notation.builders.MeasureBuilder;
import org.wmn4j.notation.builders.NoteBuilder;
import org.wmn4j.notation.builders.RestBuilder;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MeasureAttributes;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;
import org.wmn4j.notation.elements.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
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
		builder.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
		.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
		.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		final Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		final List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testBuildMeasureWithGivenAttributes() {
		final MeasureAttributes measureAttr = MeasureAttributes.of(TimeSignatures.SIX_EIGHT,
				KeySignatures.DFLATMAJ_BFLATMIN, Barline.DOUBLE, Clefs.F);
		final MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.addVoice();
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
		.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
		.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		final Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		final List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
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
		builder.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
		.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
		.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		final Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		final List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testAdditionOfVoices() {
		final MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addToVoice(1, new RestBuilder(Durations.EIGHT));
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(3, new RestBuilder(Durations.EIGHT));
		assertEquals(2, builder.getNumberOfVoices());

		final Measure measure = builder.build();
		assertEquals(2, measure.getVoiceCount());
		assertTrue(measure.getVoice(1).size() == 1);
		assertTrue(measure.getVoice(1).contains(Rest.getRest(Durations.EIGHT)));
		assertTrue(measure.getVoice(3).size() == 1);
		assertTrue(measure.getVoice(3).contains(Rest.getRest(Durations.EIGHT)));
	}

	@Test
	public void testIsVoiceFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToVoice(0, new RestBuilder(Durations.QUARTER));
		assertFalse("Voice 0 is full for 4/4 measure after adding one quarter rest", builder.isVoiceFull(0));
		final NoteBuilder c = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.QUARTER);
		builder.addToVoice(0, c);
		assertFalse("Voice 0 is full for 4/4 measure after adding two quarters", builder.isVoiceFull(0));
		builder.addToVoice(0, c);
		builder.addToVoice(0, c);
		assertTrue("Voice 0 is not full when 4 quarter durations added to 4/4", builder.isVoiceFull(0));

		builder = new MeasureBuilder(1);
		builder.setTimeSignature(TimeSignatures.SIX_EIGHT).setKeySignature(KeySignatures.CMAJ_AMIN);

		builder.addToVoice(0, new RestBuilder(Durations.QUARTER.addDot()))
		.addToVoice(0, new RestBuilder(Durations.QUARTER))
		.addToVoice(1, new RestBuilder(Durations.QUARTER.addDot()));
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET)).addToVoice(0,
				new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertFalse("Voice 0 is full when 6/8 measure is lacking one sixteenth triplet", builder.isVoiceFull(0));
		builder.addToVoice(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertTrue("Voice 0 is not full when 6/8 measure should be full.", builder.isVoiceFull(0));
		assertFalse("Voice 1 is full for 6/8 measure when it should not be", builder.isVoiceFull(1));
	}

	@Test
	public void testIsFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToVoice(0, new RestBuilder(Durations.QUARTER));
		assertFalse("builder for 4/4 is full after adding one quarter rest", builder.isFull());
		final NoteBuilder c = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.QUARTER);
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
		final NoteBuilder first = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.HALF);
		final NoteBuilder second = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.HALF);

		first.addTieToFollowing(second);
		builder.addToVoice(1, first).addToVoice(1, second);
		final Measure measure = builder.build();

		final Note firstNote = (Note) measure.get(1, 0);
		final Note secondNote = (Note) measure.get(1, 1);
		assertEquals(secondNote, firstNote.getFollowingTiedNote().get());
		assertTrue(secondNote.isTiedFromPrevious());
	}
}
