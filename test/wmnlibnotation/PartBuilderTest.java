/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import wmnlibnotation.builders.PartBuilder;
import wmnlibnotation.noteobjects.Part;
import wmnlibnotation.noteobjects.MultiStaffPart;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Rest;
import wmnlibnotation.noteobjects.TimeSignatures;
import wmnlibnotation.noteobjects.SingleStaffPart;
import wmnlibnotation.noteobjects.Staff;
import wmnlibnotation.noteobjects.KeySignature;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.MeasureAttributes;
import wmnlibnotation.noteobjects.Clefs;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.Barline;
import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.KeySignatures;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class PartBuilderTest {
    
    private final Map<Integer, List<Durational>> measureContents;
    private final MeasureAttributes measureAttr;
    
    KeySignature keySig = KeySignatures.CMAJ_AMIN;
    
    Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
    Note E4 = Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
    Note G4 = Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
    Note C4Quarter = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
    
    public PartBuilderTest() {
        Map<Integer, List<Durational>> noteLayer = new HashMap();
        noteLayer.put(0, new ArrayList());
        noteLayer.get(0).add(C4Quarter);
        noteLayer.get(0).add(Rest.getRest(Durations.QUARTER));
        noteLayer.get(0).add(Chord.getChord(C4, E4, G4));
        
        Map<Integer, List<Durational>> noteLayers = new HashMap();
        noteLayers.put(0,noteLayer.get(0));
        noteLayers.put(1, new ArrayList());
        noteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
        noteLayers.get(1).add(C4);
        noteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
    
        this.measureContents = Collections.unmodifiableMap(noteLayers);
        this.measureAttr = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Barline.SINGLE, Clefs.G);
    }

    @Test
    public void testGetStaffCount() {
        int measureCount = 5;
        PartBuilder builder = new PartBuilder("");
        for(int i = 1; i <= measureCount; ++i) {
            Measure m = new Measure(i, this.measureContents, this.measureAttr);
            builder.addMeasureToStaff(0, m);
        }
        assertEquals(1, builder.getStaffCount());
    
        for(int i = 1; i <= measureCount; ++i) {
            Measure m = new Measure(i, this.measureContents, this.measureAttr);
            builder.addMeasureToStaff(1, m);
        }
        assertEquals(2, builder.getStaffCount());
    }

    @Test
    public void testBuildSingleStaffPart() {
        int measureCount = 5;
        PartBuilder builder = new PartBuilder("");
        for(int i = 1; i <= measureCount; ++i) {
            Measure m = new Measure(i, this.measureContents, this.measureAttr);
            builder.addMeasure(m);
        }
        
        Part part = builder.build();
        assertTrue(part instanceof SingleStaffPart);
        assertFalse(part.isMultiStaff());
    }
    
    @Test
    public void testBuildMultiStaffPart() {
        int measureCount = 5;
        PartBuilder builder = new PartBuilder("");
        for(int i = 1; i <= measureCount; ++i) {
            builder.addMeasureToStaff(1, new Measure(i, this.measureContents, this.measureAttr));
            builder.addMeasureToStaff(2, new Measure(i, this.measureContents, this.measureAttr));
        }
        
        Part part = builder.build();
        assertTrue(part.isMultiStaff());
        assertTrue(part instanceof MultiStaffPart);
        MultiStaffPart mpart = (MultiStaffPart) part;
        List<Integer> staffNumbers = mpart.getStaffNumbers();
        assertTrue(staffNumbers.size() == 2);
        assertTrue(staffNumbers.contains(1));
        assertTrue(staffNumbers.contains(2));
        
        Staff staff1 = mpart.getStaff(1);
        assertTrue(staff1.getMeasureCount() == 5);
        
        Staff staff2 = mpart.getStaff(2);
        assertTrue(staff2.getMeasureCount() == 5);
    }
}
