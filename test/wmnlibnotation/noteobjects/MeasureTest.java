/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

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
public class MeasureTest {
    
    Map<Integer, List<Durational>> singleNoteLayer = new HashMap<>();
    Map<Integer, List<Durational>> multipleNoteLayers = new HashMap<>();
    
    KeySignature keySig = KeySignatures.CMAJ_AMIN;
    
    Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
    Note E4 = Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
    Note G4 = Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
    Note C4Quarter = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
    
    public MeasureTest() {
        List<Durational> layerContents = new ArrayList<>();
        layerContents.add(C4Quarter);
        layerContents.add(Rest.getRest(Durations.QUARTER));
        layerContents.add(Chord.getChord(C4, E4, G4));
        this.singleNoteLayer.put(0, layerContents);
        
        this.multipleNoteLayers = new HashMap<>();
        this.multipleNoteLayers.put(0, layerContents);
        this.multipleNoteLayers.put(1, new ArrayList<>());
        this.multipleNoteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
        this.multipleNoteLayers.get(1).add(C4);
        this.multipleNoteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
    }
    
    @Test
    public void testCreatingIllegalMeasureThrowsException() {
        // Test exceptions thrown correctly for illegal arguments
        try {
            Measure m = new Measure(-1, this.multipleNoteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
            fail("Exception not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        
        try {
            Measure m = new Measure(1, null, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
            fail("Exception not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    
    @Test
    public void testLayersImmutable() {
        Map<Integer, List<Durational>> layers = new HashMap<>();
        List<Durational> layer = new ArrayList<>();
        layer.add(C4);
        layers.put(0, layer);
        
        Measure measure = new Measure(1, layers,TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
        
        // Test that modifying the original layers list does no affect measure created using it.
        layers.get(0).add(C4);
        assertEquals("Modifying list from which measure is created changes measure", 1, measure.getLayer(0).size());
    }
    
    @Test
    public void testGetLayer() {
        Measure m = new Measure(1, multipleNoteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
        assertTrue(m.getLayer(1).contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF)));
        assertTrue(m.getLayer(0).contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER)));
        
        List<Durational> layer = m.getLayer(0);
        try {
            layer.add(Rest.getRest(Durations.QUARTER));
            fail("Failed to throw exception for disabled adding");
        }
        catch(Exception e) { /* Do nothing */ }
    }

    @Test
    public void testGetNumber() {
        assertEquals(1, new Measure(1, singleNoteLayer, TimeSignatures.FOUR_FOUR, keySig, Clefs.G).getNumber());
        assertEquals(512, new Measure(512, singleNoteLayer, TimeSignatures.FOUR_FOUR, keySig, Clefs.G).getNumber());
    }
    
    @Test
    public void testIteratorWithSingleLayerMeasure() {
        Measure singleLayerMeasure = new Measure(1, singleNoteLayer, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
        int noteCount = 0;
        
        List<Durational> expected = singleNoteLayer.get(0);
        List<Durational> found = new ArrayList<>();
        
        for(Durational e : singleLayerMeasure) {
            found.add(e);
        }
        
        assertEquals(expected.size(), found.size());
        
        for(Durational e : expected) 
            assertTrue(found.contains(e));
    }
    
    @Test
    public void testIteratorWithMultiLayerMeasure() {
        Measure multiLayerMeasure = new Measure(1, multipleNoteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
        int noteCount = 0;

        List<Durational> expected = new ArrayList<>();
        expected.addAll(multipleNoteLayers.get(0));
        expected.addAll(multipleNoteLayers.get(1));
        
        List<Durational> found = new ArrayList<>();
        
        for(Durational e : multiLayerMeasure) {
            found.add(e);
        }
        
        assertEquals(expected.size(), found.size());
        
        for(Durational e : expected)
            assertTrue(found.contains(e));
    }
    
    @Test
    public void testIteratorRemoveDisabled() {
        Measure m = new Measure(1, multipleNoteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
      
        try {
            Iterator<Durational> iter = m.iterator();
            iter.next();
            iter.remove();
            fail("Expected exception was not thrown");
        }
        catch(Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        } 
    }
    
    @Test
    public void testIteratorWithEmptyMeasure() {
        Map<Integer, List<Durational>> layers = new HashMap<>();
        layers.put(0, new ArrayList<>());
        Measure emptyMeasure = new Measure(1, layers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
        
        int noteElemCount = 0;
        
        for(Durational n : emptyMeasure) {
            ++noteElemCount;
            break;
        }
        
        assertEquals(0, noteElemCount);
    }
    
    @Test
    public void testIteratorWithNonContiguousLayerNumbers() {
        List<Durational> noteList = this.singleNoteLayer.get(0);
        Map<Integer, List<Durational>> noteLayers = new HashMap<>();
        noteLayers.put(1, noteList);
        noteLayers.put(3, noteList);
        
        Measure measure = new Measure(1, noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
        
        List<Durational> expected = new ArrayList<>(noteList);
        for(Durational d : noteList)
            expected.add(d);
        
        int count = 0;
        int indexInExpected = 0;
        for(Durational d : measure) {
            assertEquals("Iterator did not return the expected object", expected.get(indexInExpected++), d);
            ++count;
        }
        
        assertEquals("Iterator iterated through a number of objects different from size of expected", expected.size(), count);
    }
}
