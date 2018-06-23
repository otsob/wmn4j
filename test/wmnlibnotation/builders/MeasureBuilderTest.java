/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.MeasureAttributes;
import wmnlibnotation.noteobjects.Clefs;
import wmnlibnotation.noteobjects.Barline;
import wmnlibnotation.noteobjects.KeySignatures;
import wmnlibnotation.noteobjects.TimeSignatures;
import wmnlibnotation.noteobjects.Rest;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Note;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

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

		builder.addLayer();
		assertEquals(1, builder.getNumberOfLayers());
		builder.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getLayerCount());
		List<Durational> layer = measure.getLayer(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), layer.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), layer.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), layer.get(2));
	}

	@Test
	public void testBuildMeasureWithGivenAttributes() {
		MeasureAttributes measureAttr = MeasureAttributes.getMeasureAttr(TimeSignatures.SIX_EIGHT,
				KeySignatures.DFLATMAJ_BFLATMIN, Barline.DOUBLE, Clefs.F);
		MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.addLayer();
		assertEquals(1, builder.getNumberOfLayers());
		builder.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getLayerCount());
		List<Durational> layer = measure.getLayer(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), layer.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), layer.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), layer.get(2));
	}

	@Test
	public void testSetParametersUsedOverMeasureAttributes() {
		MeasureAttributes measureAttr = MeasureAttributes.getMeasureAttr(TimeSignatures.THREE_EIGHT,
				KeySignatures.AMAJ_FSHARPMIN, Barline.REPEAT_RIGHT, Clefs.ALTO);
		MeasureBuilder builder = new MeasureBuilder(1, measureAttr);

		builder.setTimeSig(TimeSignatures.SIX_EIGHT).setKeySig(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addLayer();
		assertEquals(1, builder.getNumberOfLayers());
		builder.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
				.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
				.addToLayer(0, new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));

		Measure measure = builder.build();
		assertTrue(measure != null);
		assertEquals(Barline.DOUBLE, measure.getRightBarline());
		assertEquals(Clefs.F, measure.getClef());
		assertEquals(KeySignatures.DFLATMAJ_BFLATMIN, measure.getKeySignature());
		assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
		assertEquals(1, measure.getNumber());

		assertEquals(1, measure.getLayerCount());
		List<Durational> layer = measure.getLayer(0);
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), layer.get(0));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), layer.get(1));
		assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), layer.get(2));
	}

	@Test
	public void testAdditionOfLayers() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.setTimeSig(TimeSignatures.SIX_EIGHT).setKeySig(KeySignatures.DFLATMAJ_BFLATMIN);
		builder.setRightBarline(Barline.DOUBLE).setClef(Clefs.F);

		builder.addToLayer(1, new RestBuilder(Durations.EIGHT));
		assertEquals(1, builder.getNumberOfLayers());
		builder.addToLayer(3, new RestBuilder(Durations.EIGHT));
		assertEquals(2, builder.getNumberOfLayers());

		Measure measure = builder.build();
		assertEquals(2, measure.getLayerCount());
		assertTrue(measure.getLayer(1).size() == 1);
		assertTrue(measure.getLayer(1).contains(Rest.getRest(Durations.EIGHT)));
		assertTrue(measure.getLayer(3).size() == 1);
		assertTrue(measure.getLayer(3).contains(Rest.getRest(Durations.EIGHT)));
	}

	@Test
	public void testIsLayerFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToLayer(0, new RestBuilder(Durations.QUARTER));
		assertFalse("Layer 0 is full for 4/4 measure after adding one quarter rest", builder.isLayerFull(0));
		NoteBuilder c = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.QUARTER);
		builder.addToLayer(0, c);
		assertFalse("Layer 0 is full for 4/4 measure after adding two quarters", builder.isLayerFull(0));
		builder.addToLayer(0, c);
		builder.addToLayer(0, c);
		assertTrue("Layer 0 is not full when 4 quarter durations added to 4/4", builder.isLayerFull(0));

		builder = new MeasureBuilder(1);
		builder.setTimeSig(TimeSignatures.SIX_EIGHT).setKeySig(KeySignatures.CMAJ_AMIN);

		builder.addToLayer(0, new RestBuilder(Durations.QUARTER.addDot()))
				.addToLayer(0, new RestBuilder(Durations.QUARTER))
				.addToLayer(1, new RestBuilder(Durations.QUARTER.addDot()));
		builder.addToLayer(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET)).addToLayer(0,
				new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertFalse("Layer 0 is full when 6/8 measure is lacking one sixteenth triplet", builder.isLayerFull(0));
		builder.addToLayer(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertTrue("Layer 0 is not full when 6/8 measure should be full.", builder.isLayerFull(0));
		assertFalse("Layer 1 is full for 6/8 measure when it should not be", builder.isLayerFull(1));
	}

	@Test
	public void testIsFull() {
		MeasureBuilder builder = new MeasureBuilder(1);
		builder.addToLayer(0, new RestBuilder(Durations.QUARTER));
		assertFalse("builder for 4/4 is full after adding one quarter rest", builder.isFull());
		NoteBuilder c = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.QUARTER);
		builder.addToLayer(0, c);
		assertFalse("builder for 4/4 is full only after adding two quarters", builder.isFull());
		builder.addToLayer(0, c);
		builder.addToLayer(0, c);
		assertTrue("builder is not full when 4 quarter durations added to 4/4", builder.isFull());

		builder = new MeasureBuilder(1);
		builder.setTimeSig(TimeSignatures.SIX_EIGHT);

		builder.addToLayer(0, new RestBuilder(Durations.QUARTER.addDot()))
				.addToLayer(0, new RestBuilder(Durations.QUARTER))
				.addToLayer(1, new RestBuilder(Durations.QUARTER.addDot()));
		builder.addToLayer(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET)).addToLayer(0,
				new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertFalse("builder is full when 6/8 measure is lacking one sixteenth triplet", builder.isFull());
		builder.addToLayer(0, new RestBuilder(Durations.SIXTEENTH_TRIPLET));
		assertTrue("builder is not full when 6/8 measure should be full.", builder.isFull());
	}

	@Test
	public void testBuildingMeasureWithTiedNotes() {
		MeasureBuilder builder = new MeasureBuilder(1);
		NoteBuilder first = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.HALF);
		NoteBuilder second = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 2), Durations.HALF);

		first.addTieToFollowing(second);
		builder.addToLayer(1, first).addToLayer(1, second);
		Measure measure = builder.build();

		Note firstNote = (Note) measure.get(1, 0);
		Note secondNote = (Note) measure.get(1, 1);
		assertEquals(secondNote, firstNote.getFollowingTiedNote());
		assertTrue(secondNote.isTiedFromPrevious());
	}
}
