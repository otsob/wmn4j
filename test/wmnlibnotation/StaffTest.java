/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class StaffTest {
    
    public StaffTest() {
    }
    
    static List<Measure> getTestMeasures() {
        List<Measure> measures = new ArrayList();
        TimeSignature timeSig = TimeSignature.getTimeSignature(4, 4);
        Map<Integer, List<Durational>> notes = new HashMap();
        notes.put(0, new ArrayList());
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        notes.get(0).add(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER));
        measures.add(new Measure(1, notes, timeSig, KeySignatures.CMAJ_AMIN, Clefs.G));
        measures.add(new Measure(2, notes, timeSig, KeySignatures.CMAJ_AMIN, Clefs.G));
        return measures;
    }
    
    @Test
    public void testGetMeasures() {
        List<Measure> origMeasures = getTestMeasures();
        Staff staff = new Staff(origMeasures);
        
        List<Measure> measures = staff.getMeasures();
        
        assertEquals(origMeasures.size(), measures.size());
        
        try {
            measures.add(origMeasures.get(0));
            assertTrue("Did not throw UnsupportedOperationException", false);
        } catch (Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
        
        int sizeBeforeAddition = origMeasures.size();
        List<Durational> layer = new ArrayList();
        layer.add(Rest.getRest(Durations.WHOLE));
        Map<Integer, List<Durational>> notes = new HashMap();
        notes.put(1, layer);
        origMeasures.add(new Measure(3, notes, TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Clefs.G));
        assertEquals(sizeBeforeAddition, staff.getMeasures().size());
    }
    
    @Test
    public void testIterator() {
        List<Measure> origMeasures = getTestMeasures();
        Staff staff = new Staff(origMeasures);
        
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
        List<Measure> origMeasures = getTestMeasures();
        Staff staff = new Staff(origMeasures);
        
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
    
    @Test
    public void testGetMeasure() {
        List<Measure> measures = getTestMeasures();
        Staff staff = new Staff(measures);
        
        assertFalse(staff.hasPickupMeasure());
        
        for(int measureNumber = 1; measureNumber < measures.size(); ++measureNumber) {
            assertEquals(measureNumber, staff.getMeasure(measureNumber).getNumber());
        }
    }
    
    @Test
    public void testGetMeasureWithPickup() {
        List<Measure> measures = new ArrayList();
        List<Durational> layer = new ArrayList();
        layer.add(Rest.getRest(Durations.WHOLE));
        Map<Integer, List<Durational>> notes = new HashMap();
        notes.put(1, layer);
        measures.add(new Measure(0, notes, TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Clefs.G));
        measures.addAll(getTestMeasures());
        
        Staff staff = new Staff(measures);
        assertTrue(staff.hasPickupMeasure());
        
        for(int measureNumber = 0; measureNumber < measures.size(); ++measureNumber) {
            assertEquals(measureNumber, staff.getMeasure(measureNumber).getNumber());
        }
    }
}
