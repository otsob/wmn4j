/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibio.musicxml;

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import wmnlibnotation.Barline;
import wmnlibnotation.Chord;
import wmnlibnotation.Clef;
import wmnlibnotation.Clefs;
import wmnlibnotation.Durations;
import wmnlibnotation.KeySignatures;
import wmnlibnotation.Measure;
import wmnlibnotation.MeasureInfo;
import wmnlibnotation.Durational;
import wmnlibnotation.Note;
import wmnlibnotation.Pitch;
import wmnlibnotation.Rest;
import wmnlibnotation.Score;
import wmnlibnotation.Staff;
import wmnlibnotation.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
public class MusicXmlDomReaderTest {
    
    static final String testFilePath = "test/testfiles/";
    
    public MusicXmlDomReaderTest() {
    }
    
    public MusicXmlReader getMusicXmlReader() {
        return new MusicXmlDomReader();
    }

    @Test
    public void testReadScoreWithSingleNote() {
        MusicXmlReader reader = getMusicXmlReader();
        Score score = null;
        try {
            score = reader.readScore(testFilePath + "singleC.xml");
        } catch(Exception e) {
            fail("Parsing failed with exception " + e);
        }
        assertTrue(score != null);
        assertEquals("Single C", score.getName());
        assertEquals("TestFile Composer", score.getComposerName());
        assertEquals(1, score.getStaves().size());
        
        Staff staff = score.getStaves().get(0);
        assertEquals("Staff1", staff.getName());
        assertEquals(1, staff.getMeasures().size());
        
        Measure measure = staff.getMeasures().get(0);
        assertEquals(1, measure.getNumber());
        assertEquals(1, measure.getNumberOfLayers());
        assertEquals(TimeSignatures.FOUR_FOUR, measure.getTimeSignature());
        assertEquals(KeySignatures.CMaj_Amin, measure.getKeySignature());
        assertEquals(Barline.SINGLE, measure.getRightBarline());
        assertEquals(Clefs.G, measure.getClef());
        
        List<Durational> layer = measure.getLayer(0);
        assertEquals(1, layer.size());
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.WHOLE), layer.get(0));
    }
    
    @Test
    public void testReadScoreWithOneStaff() {
        MusicXmlReader reader = getMusicXmlReader();
        Score score = null;
        try {
            score = reader.readScore(testFilePath + "twoMeasures.xml");
        } catch(Exception e) {
            fail("Parsing failed with exception " + e);
        }
        assertTrue(score != null);
        
        assertEquals("Two bar sample", score.getName());
        assertEquals("TestFile Composer", score.getComposerName());
        assertEquals(1, score.getStaves().size());
        
        Staff staff = score.getStaves().get(0);
        assertEquals("Staff1", staff.getName());
        assertEquals(2, staff.getMeasures().size());
        
        // Verify data of measure one
        Measure measureOne = staff.getMeasures().get(0);
        assertEquals(1, measureOne.getNumber());
        assertEquals(1, measureOne.getNumberOfLayers());
        assertEquals(TimeSignatures.FOUR_FOUR, measureOne.getTimeSignature());
        assertEquals(KeySignatures.CMaj_Amin, measureOne.getKeySignature());
        assertEquals(Barline.SINGLE, measureOne.getRightBarline());
        assertEquals(Clefs.G, measureOne.getClef());
    
        // Verify notes of measure one
        List<Durational> layerOne = measureOne.getLayer(0);
        assertEquals(8, layerOne.size());
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), layerOne.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), layerOne.get(1));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), layerOne.get(2));
        assertEquals(Rest.getRest(Durations.EIGHT), layerOne.get(3));
        Chord cMajor = Chord.getChord(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT),
                                      Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT),
                                      Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));
        assertEquals(cMajor, layerOne.get(4));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), layerOne.get(5));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), layerOne.get(6));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.EIGHT_TRIPLET), layerOne.get(7));
        
        // Verify data of measure two
        Measure measureTwo = staff.getMeasures().get(1);
        assertEquals(2, measureTwo.getNumber());
        assertEquals(2, measureTwo.getNumberOfLayers());
        assertEquals(TimeSignatures.FOUR_FOUR, measureTwo.getTimeSignature());
        assertEquals(KeySignatures.CMaj_Amin, measureTwo.getKeySignature());
        assertEquals(Barline.FINAL, measureTwo.getRightBarline());
        assertEquals(Clefs.G, measureTwo.getClef());
        
        // Verify notes of measure two
        layerOne = measureTwo.getLayer(0);
        assertEquals(2, layerOne.size());
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.HALF), layerOne.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 5), Durations.HALF), layerOne.get(1));
        
        List<Durational> layerTwo = measureTwo.getLayer(1);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), layerTwo.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), layerTwo.get(1));
        assertEquals(Rest.getRest(Durations.QUARTER), layerTwo.get(2));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), layerTwo.get(3));
    }
    
    @Test
    public void testReadScoreWithMultipleStaves() {
        MusicXmlReader reader = getMusicXmlReader();
        
        Score score = null;
        try {
            score = reader.readScore(testFilePath + "twoStavesAndMeasures.xml");
        } catch(Exception e) {
            fail("Parsing failed with exception " + e);
        }
        
        assertTrue(score != null);
        
        assertEquals("Multistaff test file", score.getName());
        assertEquals("TestFile Composer", score.getComposerName());
        assertEquals(2, score.getStaves().size());
        
        Staff staffOne = score.getStaves().get(0);
        assertEquals("Staff 1", staffOne.getName());
        assertEquals(2, staffOne.getMeasures().size());
 
        // Verify data of measure one of staff one
        Measure staffOneMeasureOne = staffOne.getMeasures().get(0);        
        assertEquals(1, staffOneMeasureOne.getNumber());
        assertEquals(1, staffOneMeasureOne.getNumberOfLayers());
        assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureOne.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffOneMeasureOne.getKeySignature());
        assertEquals(Barline.SINGLE, staffOneMeasureOne.getRightBarline());
        assertEquals(Clefs.G, staffOneMeasureOne.getClef());
    
        // Verify contents of measure one of staff one
        assertEquals(1, staffOneMeasureOne.getNumberOfLayers());
        List<Durational> layerMOne = staffOneMeasureOne.getLayer(0);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF), layerMOne.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.QUARTER), layerMOne.get(1));
        
        // Verify data of measure two of staff one
        Measure staffOneMeasureTwo = staffOne.getMeasures().get(1);        
        assertEquals(2, staffOneMeasureTwo.getNumber());
        assertEquals(1, staffOneMeasureTwo.getNumberOfLayers());
        assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureTwo.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffOneMeasureTwo.getKeySignature());
        assertEquals(Barline.FINAL, staffOneMeasureTwo.getRightBarline());
        assertEquals(Clefs.G, staffOneMeasureTwo.getClef());
    
        // Verify contents of measure one of staff one
        assertEquals(1, staffOneMeasureTwo.getNumberOfLayers());
        List<Durational> layerM2 = staffOneMeasureTwo.getLayer(0);
        assertEquals(Rest.getRest(Durations.QUARTER), layerM2.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF), layerM2.get(1));
        
        
        Staff staffTwo = score.getStaves().get(1);
        assertEquals("Staff 2", staffTwo.getName());
        assertEquals(2, staffTwo.getMeasures().size());
 
        // Verify data of measure one of staff two
        Measure staffTwoMeasureOne = staffTwo.getMeasures().get(0);        
        assertEquals(1, staffTwoMeasureOne.getNumber());
        assertEquals(1, staffTwoMeasureOne.getNumberOfLayers());
        assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureOne.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffTwoMeasureOne.getKeySignature());
        assertEquals(Barline.SINGLE, staffTwoMeasureOne.getRightBarline());
        assertEquals(Clefs.F, staffTwoMeasureOne.getClef());
    
        // Verify contents of measure one of staff two
        assertEquals(1, staffTwoMeasureOne.getNumberOfLayers());
        List<Durational> layerMOneS2 = staffTwoMeasureOne.getLayer(0);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), layerMOneS2.get(0));
        
        // Verify data of measure two of staff two
        Measure staffTwoMeasureTwo = staffTwo.getMeasures().get(1);        
        assertEquals(2, staffTwoMeasureTwo.getNumber());
        assertEquals(1, staffTwoMeasureTwo.getNumberOfLayers());
        assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureTwo.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffTwoMeasureTwo.getKeySignature());
        assertEquals(Barline.FINAL, staffTwoMeasureTwo.getRightBarline());
        assertEquals(Clefs.F, staffTwoMeasureTwo.getClef());
    
        // Verify contents of measure two of staff two
        assertEquals(1, staffTwoMeasureTwo.getNumberOfLayers());
        List<Durational> layerM2S2 = staffTwoMeasureTwo.getLayer(0);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), layerM2S2.get(0));
    }
    
    @Test
    public void testBarlines() {
        MusicXmlReader reader = getMusicXmlReader();
        
        Score score = null;
        try {
            score = reader.readScore(testFilePath + "barlines.xml");
        } catch(Exception e) {
            fail("Parsing failed with exception " + e);
        }
        
        assertTrue(score != null);
        assertEquals(1, score.getStaves().size());
        Staff staff = score.getStaves().get(0);
        List<Measure> measures = staff.getMeasures();
        assertEquals(9, measures.size());
        assertEquals(Barline.SINGLE, measures.get(0).getRightBarline());
        assertEquals(Barline.DOUBLE, measures.get(1).getRightBarline());
        assertEquals(Barline.THICK, measures.get(2).getRightBarline());
        assertEquals(Barline.DASHED, measures.get(3).getRightBarline());
        assertEquals(Barline.INVISIBLE, measures.get(4).getRightBarline());
        assertEquals(Barline.REPEAT_LEFT, measures.get(5).getRightBarline());
        assertEquals(Barline.REPEAT_RIGHT, measures.get(6).getRightBarline());
        assertEquals(Barline.REPEAT_MEASURE, measures.get(7).getRightBarline());
        assertEquals(Barline.FINAL, measures.get(8).getRightBarline());
    }
}
