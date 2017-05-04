/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibio.musicxml;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;
import wmnlibnotation.Barline;
import wmnlibnotation.Chord;
import wmnlibnotation.Clef;
import wmnlibnotation.Clefs;
import wmnlibnotation.Duration;
import wmnlibnotation.Durations;
import wmnlibnotation.KeySignatures;
import wmnlibnotation.Measure;
import wmnlibnotation.MeasureAttributes;
import wmnlibnotation.Durational;
import wmnlibnotation.MultiStaffPart;
import wmnlibnotation.Note;
import wmnlibnotation.Part;
import wmnlibnotation.SingleStaffPart;
import wmnlibnotation.Pitch;
import wmnlibnotation.Rest;
import wmnlibnotation.Score;
import wmnlibnotation.Staff;
import wmnlibnotation.TimeSignature;
import wmnlibnotation.TimeSignatures;

/**
 *
 * @author Otso Björklund
 */
public class MusicXmlDomReaderTest {
    
    static final String testFilePath = "test/testfiles/musicxml/";
    
    public MusicXmlDomReaderTest() {
    }
    
    public MusicXmlReader getMusicXmlReader() {
        return new MusicXmlDomReader();
    }
    
    public Score readScore(String testFileName) {
        MusicXmlReader reader = getMusicXmlReader();
        Score score = null;
        
        try {
            score = reader.readScore(testFilePath + testFileName);
        } catch(Exception e) {
            fail("Parsing failed with exception " + e);
        }
        
        assertTrue("score is null", score != null);
        return score;
    } 

    @Test
    public void testReadScoreWithSingleNote() {
        Score score = readScore("singleC.xml");
        
        assertEquals("Single C", score.getName());
        assertEquals(1, score.getParts().size());
        
        Part part = score.getParts().get(0);
        assertTrue(part instanceof SingleStaffPart);
        SingleStaffPart spart = (SingleStaffPart) part;
        Staff staff = spart.getStaff();
        assertEquals("Part1", part.getName());
        assertEquals(1, staff.getMeasures().size());
        
        Measure measure = staff.getMeasures().get(0);
        assertEquals(1, measure.getNumber());
        assertEquals(1, measure.getLayerCount());
        assertEquals(TimeSignatures.FOUR_FOUR, measure.getTimeSignature());
        assertEquals(KeySignatures.CMaj_Amin, measure.getKeySignature());
        assertEquals(Barline.SINGLE, measure.getRightBarline());
        assertEquals(Clefs.G, measure.getClef());
        
        List<Durational> layer = measure.getLayer(0);
        assertEquals(1, layer.size());
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.WHOLE), layer.get(0));
    }
    
    @Test
    public void testChordsAndMultipleLayers() {
        Score score = readScore("twoMeasures.xml");
        
        assertEquals("Two bar sample", score.getName());
        assertEquals("TestFile Composer", score.getAttribute(Score.Attribute.COMPOSER));
        assertEquals(1, score.getParts().size());
        
        Part part = score.getParts().get(0);
        assertTrue(part instanceof SingleStaffPart);
        SingleStaffPart spart = (SingleStaffPart) part;
        Staff staff = spart.getStaff();
        assertEquals("Part1", part.getName());
        assertEquals(2, staff.getMeasures().size());
        
        // Verify data of measure one
        Measure measureOne = staff.getMeasures().get(0);
        assertEquals(1, measureOne.getNumber());
        assertEquals(1, measureOne.getLayerCount());
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
        assertEquals(2, measureTwo.getLayerCount());
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
        Score score = readScore("twoStavesAndMeasures.xml");

        assertEquals("Multistaff test file", score.getName());
        assertEquals("TestFile Composer", score.getAttribute(Score.Attribute.COMPOSER));
        assertEquals(2, score.getParts().size());
        
        SingleStaffPart partOne = (SingleStaffPart) score.getParts().get(0);
        Staff staffOne = partOne.getStaff();
        assertEquals("Part1", partOne.getName());
        assertEquals(2, staffOne.getMeasures().size());
 
        // Verify data of measure one of staff one
        Measure staffOneMeasureOne = staffOne.getMeasures().get(0);        
        assertEquals(1, staffOneMeasureOne.getNumber());
        assertEquals(1, staffOneMeasureOne.getLayerCount());
        assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureOne.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffOneMeasureOne.getKeySignature());
        assertEquals(Barline.SINGLE, staffOneMeasureOne.getRightBarline());
        assertEquals(Clefs.G, staffOneMeasureOne.getClef());
    
        // Verify contents of measure one of staff one
        assertEquals(1, staffOneMeasureOne.getLayerCount());
        List<Durational> layerMOne = staffOneMeasureOne.getLayer(0);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF), layerMOne.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.QUARTER), layerMOne.get(1));
        
        // Verify data of measure two of staff one
        Measure staffOneMeasureTwo = staffOne.getMeasures().get(1);        
        assertEquals(2, staffOneMeasureTwo.getNumber());
        assertEquals(1, staffOneMeasureTwo.getLayerCount());
        assertEquals(TimeSignatures.THREE_FOUR, staffOneMeasureTwo.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffOneMeasureTwo.getKeySignature());
        assertEquals(Barline.FINAL, staffOneMeasureTwo.getRightBarline());
        assertEquals(Clefs.G, staffOneMeasureTwo.getClef());
    
        // Verify contents of measure one of staff one
        assertEquals(1, staffOneMeasureTwo.getLayerCount());
        List<Durational> layerM2 = staffOneMeasureTwo.getLayer(0);
        assertEquals(Rest.getRest(Durations.QUARTER), layerM2.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF), layerM2.get(1));
        
        SingleStaffPart partTwo = (SingleStaffPart) score.getParts().get(1);
        Staff staffTwo = partTwo.getStaff();
        assertEquals("Part2", partTwo.getName());
        assertEquals(2, staffTwo.getMeasures().size());
 
        // Verify data of measure one of staff two
        Measure staffTwoMeasureOne = staffTwo.getMeasures().get(0);        
        assertEquals(1, staffTwoMeasureOne.getNumber());
        assertEquals(1, staffTwoMeasureOne.getLayerCount());
        assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureOne.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffTwoMeasureOne.getKeySignature());
        assertEquals(Barline.SINGLE, staffTwoMeasureOne.getRightBarline());
        assertEquals(Clefs.F, staffTwoMeasureOne.getClef());
    
        // Verify contents of measure one of staff two
        assertEquals(1, staffTwoMeasureOne.getLayerCount());
        List<Durational> layerMOneS2 = staffTwoMeasureOne.getLayer(0);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), layerMOneS2.get(0));
        
        // Verify data of measure two of staff two
        Measure staffTwoMeasureTwo = staffTwo.getMeasures().get(1);        
        assertEquals(2, staffTwoMeasureTwo.getNumber());
        assertEquals(1, staffTwoMeasureTwo.getLayerCount());
        assertEquals(TimeSignatures.THREE_FOUR, staffTwoMeasureTwo.getTimeSignature());
        assertEquals(KeySignatures.GMaj_Emin, staffTwoMeasureTwo.getKeySignature());
        assertEquals(Barline.FINAL, staffTwoMeasureTwo.getRightBarline());
        assertEquals(Clefs.F, staffTwoMeasureTwo.getClef());
    
        // Verify contents of measure two of staff two
        assertEquals(1, staffTwoMeasureTwo.getLayerCount());
        List<Durational> layerM2S2 = staffTwoMeasureTwo.getLayer(0);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 3), Durations.HALF.addDot()), layerM2S2.get(0));
    }
    
    @Test
    public void testBarlines() {
        Score score = readScore("barlines.xml");
        
        assertEquals(1, score.getParts().size());
        SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
        Staff staff = part.getStaff();
        
        assertEquals(Barline.SINGLE, part.getMeasure(1).getRightBarline());
        assertEquals(Barline.NONE, part.getMeasure(1).getLeftBarline());
        
        assertEquals(Barline.DOUBLE, part.getMeasure(2).getRightBarline());
        assertEquals(Barline.NONE, part.getMeasure(2).getLeftBarline());
        
        assertEquals(Barline.THICK, part.getMeasure(3).getRightBarline());
        assertEquals(Barline.NONE, part.getMeasure(3).getLeftBarline());
        
        assertEquals(Barline.DASHED, part.getMeasure(4).getRightBarline());
        assertEquals(Barline.NONE, part.getMeasure(4).getLeftBarline());
        
        assertEquals(Barline.INVISIBLE, part.getMeasure(5).getRightBarline());
        assertEquals(Barline.NONE, part.getMeasure(5).getLeftBarline());
        
        assertEquals(Barline.SINGLE, part.getMeasure(6).getRightBarline());
        assertEquals(Barline.REPEAT_LEFT, part.getMeasure(6).getLeftBarline());
        
        assertEquals(Barline.REPEAT_RIGHT, part.getMeasure(7).getRightBarline());
        assertEquals(Barline.NONE, part.getMeasure(7).getLeftBarline());
        
        assertEquals(Barline.REPEAT_RIGHT, part.getMeasure(8).getRightBarline());
        assertEquals(Barline.REPEAT_LEFT, part.getMeasure(8).getLeftBarline());
        
        assertEquals(Barline.FINAL, part.getMeasure(9).getRightBarline());
        assertEquals(Barline.NONE, part.getMeasure(9).getLeftBarline());
    }
    
    @Test
    public void testClefs() {
        Score score = readScore("clefs.xml");
        SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
        
        assertEquals(Clefs.G, part.getMeasure(1).getClef());
        assertFalse(part.getMeasure(1).containsClefChanges());
        
        assertEquals(Clefs.ALTO, part.getMeasure(2).getClef());
        assertFalse(part.getMeasure(2).containsClefChanges());
        
        assertEquals(Clef.getClef(Clef.Type.C, 4), part.getMeasure(3).getClef());
        assertFalse(part.getMeasure(3).containsClefChanges());
        
        assertEquals(Clef.getClef(Clef.Type.C, 4), part.getMeasure(4).getClef());
        assertTrue(part.getMeasure(4).containsClefChanges());
        Map<Duration, Clef> clefChanges = part.getMeasure(4).getClefChanges();
        assertEquals(1, clefChanges.size());
        assertEquals(Clefs.F, clefChanges.get(Durations.QUARTER));
        
        assertEquals(Clefs.PERCUSSION, part.getMeasure(5).getClef());
        assertTrue(part.getMeasure(5).containsClefChanges());
        assertEquals(2, part.getMeasure(5).getClefChanges().size());
        assertEquals(Clefs.G, part.getMeasure(5).getClefChanges().get(Durations.QUARTER));
        assertEquals(Clefs.F, part.getMeasure(5).getClefChanges().get(Durations.HALF.addDot()));
    }
    
    @Test
    public void testKeySignatures() {
        Score score = readScore("keysigs.xml");
        
        SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
        
        assertEquals(KeySignatures.CMaj_Amin, part.getMeasure(1).getKeySignature());
        assertEquals(KeySignatures.GMaj_Emin, part.getMeasure(2).getKeySignature());
        assertEquals(KeySignatures.AFlatMaj_Fmin, part.getMeasure(3).getKeySignature());
    }
    
    @Test
    public void testMultiStaffPart() {
        Score score = readScore("multistaff.xml");
        assertEquals(2, score.getPartCount());
        MultiStaffPart multiStaff = null;
        SingleStaffPart singleStaff = null;
        for(Part part : score) {
            if(part.getName().equals("MultiStaff"))
                multiStaff = (MultiStaffPart) part;
            if(part.getName().equals("SingleStaff"))
                singleStaff = (SingleStaffPart) part;
        }
        
        assertTrue(multiStaff != null);
        assertTrue(singleStaff != null);
        
        assertEquals(3, singleStaff.getMeasureCount());
        assertEquals(3, multiStaff.getMeasureCount());
        
        assertEquals(2, multiStaff.getStaffCount());
        
        int measureCount = 0;
        Note expectedNote = Note.getNote(Pitch.getPitch(Pitch.Base.A, 0, 4), Durations.WHOLE);
        
        for(Measure measure : multiStaff) {
            assertTrue(measure.isSingleLayer());
            List<Durational> layer = measure.getLayer(measure.getLayerNumbers().get(0));
            assertEquals(1, layer.size());
            assertTrue(layer.get(0).equals(expectedNote));
            ++measureCount;
        }
        
        assertEquals("Incorrect number of measures in multistaff part", 6, measureCount);
    }
    
    @Test
    public void testTimeSignatures() {
        Score score = readScore("timesigs.xml");
        assertEquals(1, score.getPartCount());
        SingleStaffPart part = (SingleStaffPart) score.getParts().get(0);
        assertEquals(TimeSignature.getTimeSignature(2, 2), part.getMeasure(1).getTimeSignature());
        assertEquals(TimeSignature.getTimeSignature(3, 4), part.getMeasure(2).getTimeSignature());
        assertEquals(TimeSignature.getTimeSignature(6, 8), part.getMeasure(3).getTimeSignature());
        assertEquals(TimeSignature.getTimeSignature(15, 16), part.getMeasure(4).getTimeSignature());
    }
}
