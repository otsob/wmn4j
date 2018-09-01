/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import wmnlibnotation.noteobjects.Barline;
import wmnlibnotation.noteobjects.Clefs;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.KeySignatures;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.MeasureAttributes;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Rest;
import wmnlibnotation.noteobjects.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
public class MeasureBuilderTest {

	public MeasureBuilderTest() {
	}

	@Test
	public void testBuildMeasureBySettingParameters() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSig(TimeSignatures.SIX_EIGHT).setKeySig(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addVoice();
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testBuildMeasureWithGivenAttributes() {
		MeasureAttributes measureAttr = MeasureAttributes.getMeasureAttr(TimeSignatures.SIX_EIGHT,
				KeySignatures.DFLATMAJ_BFLATMIN, Barline.DOUBLE, Clefs.F);
		MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.addVoice();
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testSetParametersUsedOverMeasureAttributes() {
		MeasureAttributes measureAttr = MeasureAttributes.getMeasureAttr(TimeSignatures.THREE_EIGHT,
				KeySignatures.AMAJ_FSHARPMIN, Barline.REPEAT_RIGHT, Clefs.ALTO);
		MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.setTimeSig(TimeSignatures.SIX_EIGHT).setKeySig(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addVoice();
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToVoice(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getVoiceCount());
		List<Durational> voice = measure.getVoice(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), voice.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), voice.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), voice.get(2));
	}

	@Test
	public void testAdditionOfVoices() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSig(TimeSignatures.SIX_EIGHT).setKeySig(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addToVoice(1, new RestBuilder(Durations.EIGHT));
		assertEquals(1, builder.getNumberOfVoices());
		builder.addToVoice(3, new RestBuilder(Durations.EIGHT));
		assertEquals(2, builder.getNumberOfVoices());

		Measure measure = builder.build();
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
		NoteBuilder c = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.QUARTER);
		builder.addToVoice(0, c);
		assertFalse("Voice 0 is full for 4/4 measure after adding two quarters", builder.isVoiceFull(0));
		builder.addToVoice(0, c);
		builder.addToVoice(0, c);
		assertTrue("Voice 0 is not full when 4 quarter durations added to 4/4", builder.isVoiceFull(0));

		builder = new MeasureBuilder(1);
		builder.setTimeSig(TimeSignatures.SIX_EIGHT).setKeySig(KeySignatures.CMAJ_AMIN);

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
		NoteBuilder c = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.QUARTER);
		builder.addToVoice(0, c);
		assertFalse("builder for 4/4 is full only after adding two quarters", builder.isFull());
		builder.addToVoice(0, c);
		builder.addToVoice(0, c);
		assertTrue("builder is not full when 4 quarter durations added to 4/4", builder.isFull());

		builder = new MeasureBuilder(1);
		builder.setTimeSig(TimeSignatures.SIX_EIGHT);

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
		MeasureBuilder builder = new MeasureBuilder(1);
		NoteBuilder first = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.HALF);
		NoteBuilder second = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.HALF);

		first.addTieToFollowing(second);
		builder.addToVoice(1, first).addToVoice(1, second);
		Measure measure = builder.build();

		Note firstNote = (Note) measure.get(1, 0);
		Note secondNote = (Note) measure.get(1, 1);
		assertEquals(secondNote, firstNote.getFollowingTiedNote());
		assertTrue(secondNote.isTiedFromPrevious());
	}
}
