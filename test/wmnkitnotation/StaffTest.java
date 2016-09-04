/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class StaffTest {
    
    public StaffTest() {
    }
    
    @Test
    public void testGetMeasures() {
        List<Measure> origMeasures = new ArrayList();
        TimeSignature timeSig = TimeSignature.getTimeSignature(4, 4);
        List<List<NotationElement>> notes = new ArrayList();
        notes.add(new ArrayList());
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        origMeasures.add(Measure.getMeasure(1, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        origMeasures.add(Measure.getMeasure(2, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        Staff staff = new Staff("TestStaff", origMeasures);
        
        List<Measure> measures = staff.getMeasures();
        
        assertEquals(origMeasures.size(), measures.size());
        
        try {
            measures.add(Measure.getMeasure(2, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
            assertTrue("Did not throw UnsupportedOperationException", false);
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
        
        int sizeBeforeAddition = origMeasures.size();
        origMeasures.add(Measure.getMeasure(3, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        assertEquals(sizeBeforeAddition, staff.getMeasures().size());
    }
    
    @Test
    public void testGetName() {
        List<Measure> origMeasures = new ArrayList();
        TimeSignature timeSig = TimeSignature.getTimeSignature(4, 4);
        List<List<NotationElement>> notes = new ArrayList();
        notes.add(new ArrayList());
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        origMeasures.add(Measure.getMeasure(1, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        origMeasures.add(Measure.getMeasure(2, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        Staff staff = new Staff("TestStaff", origMeasures);
        
        assertEquals("TestStaff", staff.getName());
    }
    
    @Test
    public void testIterator() {
        List<Measure> origMeasures = new ArrayList();
        TimeSignature timeSig = TimeSignature.getTimeSignature(4, 4);
        List<List<NotationElement>> notes = new ArrayList();
        notes.add(new ArrayList());
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        origMeasures.add(Measure.getMeasure(1, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        origMeasures.add(Measure.getMeasure(2, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        Staff staff = new Staff("TestStaff", origMeasures);
    
        int measureCount = 0;
        int prevMeasureNum = 0;
        
        for(Measure m : staff) {
            ++measureCount;
            assertTrue(prevMeasureNum < m.getNumber());
            prevMeasureNum = m.getNumber();
        }
        
        assertEquals(origMeasures.size(), measureCount);
    }
    
    @Test
    public void testIteratorRemoveDisabled() {
        List<Measure> origMeasures = new ArrayList();
        TimeSignature timeSig = TimeSignature.getTimeSignature(4, 4);
        List<List<NotationElement>> notes = new ArrayList();
        notes.add(new ArrayList());
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        origMeasures.add(Measure.getMeasure(1, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        origMeasures.add(Measure.getMeasure(2, notes, timeSig, KeySignature.CMaj_Amin, Clef.G_CLEF));
        Staff staff = new Staff("TestStaff", origMeasures);
        
        try {
            Iterator<Measure> iter = staff.iterator();
            iter.next();
            iter.remove();
            fail("Expected exception was not thrown");
        }
        catch(Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        } 
    }
}
