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
public class MeasureTest {
    
    List<List<NotationElement>> noteLayer;
    List<List<NotationElement>> noteLayers;
    
    KeySignature keySig = KeySignature.CMaj_Amin;
    
    Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
    Note E4 = Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
    Note G4 = Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
    Note C4Quarter = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
    
    public MeasureTest() {
        this.noteLayer = new ArrayList();
        this.noteLayer.add(new ArrayList());
        this.noteLayer.get(0).add(C4Quarter);
        this.noteLayer.get(0).add(Rest.getRest(Durations.QUARTER));
        this.noteLayer.get(0).add(Chord.getChord(C4, E4, G4));
        
        this.noteLayers = new ArrayList();
        this.noteLayers.add(this.noteLayer.get(0));
        this.noteLayers.add(new ArrayList());
        this.noteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
        this.noteLayers.get(1).add(C4);
        this.noteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
    }
    
    @Test
    public void testGetMeasure() {
        assertTrue(Measure.getMeasure(1, this.noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF) != null);
        
        // Test exceptions thrown correctly for illegal arguments
        try {
            Measure m = Measure.getMeasure(0, this.noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
            fail("Exception not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        
        try {
            Measure m = Measure.getMeasure(1, null, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
            fail("Exception not thrown");
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    
    @Test
    public void testGetLayer() {
        Measure m = Measure.getMeasure(1, noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
        assertTrue(m.getLayer(1).contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF)));
        assertTrue(m.getLayer(0).contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER)));
        
        List<NotationElement> layer = m.getLayer(0);
        try {
            layer.add(Rest.getRest(Durations.QUARTER));
            fail("Failed to throw exception for disabled adding");
        }
        catch(Exception e) { /* Do nothing */ }
    }

    @Test
    public void testGetNumber() {
        assertEquals(1, Measure.getMeasure(1, noteLayer, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF).getNumber());
        assertEquals(512, Measure.getMeasure(512, noteLayer, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF).getNumber());
    }

    @Test
    public void testToString() {
        Measure m = Measure.getMeasure(5, noteLayer, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
        assertEquals("Measure 5, Time(4/4), CMaj_Amin, G_CLEF:\nLayer 0: C4(1/4), R(1/4), [C4(1/2),E4(1/2),G4(1/2)]\nBarline:SINGLE\n", m.toString());
        
        Measure multiLayer = Measure.getMeasure(2, noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
        assertEquals("Measure 2, Time(4/4), CMaj_Amin, G_CLEF:\n"
                        + "Layer 0: C4(1/4), R(1/4), [C4(1/2),E4(1/2),G4(1/2)]\n"
                        + "Layer 1: R(1/4), C4(1/2), R(1/4)\nBarline:SINGLE\n", 
                        multiLayer.toString());
    }
    
    @Test
    public void testIteratorWithSingleLayerMeasure() {
        Measure singleLayerMeasure = Measure.getMeasure(1, noteLayer, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
        int noteCount = 0;
        
        List<NotationElement> expected = noteLayer.get(0);
        List<NotationElement> found = new ArrayList();
        
        for(NotationElement e : singleLayerMeasure) {
            found.add(e);
        }
        
        assertEquals(expected.size(), found.size());
        
        for(NotationElement e : expected) 
            assertTrue(found.contains(e));
    }
    
    @Test
    public void testIteratorWithMultiLayerMeasure() {
        Measure multiLayerMeasure = Measure.getMeasure(1, noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
        int noteCount = 0;

        List<NotationElement> expected = new ArrayList();
        expected.addAll(noteLayers.get(0));
        expected.addAll(noteLayers.get(1));
        
        List<NotationElement> found = new ArrayList();
        
        for(NotationElement e : multiLayerMeasure) {
            found.add(e);
        }
        
        assertEquals(expected.size(), found.size());
        
        for(NotationElement e : expected)
            assertTrue(found.contains(e));
    }
    
    @Test
    public void testIteratorRemoveDisabled() {
        Measure m = Measure.getMeasure(1, noteLayers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
      
        try {
            Iterator<NotationElement> iter = m.iterator();
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
        List<List<NotationElement>> layers = new ArrayList();
        layers.add(new ArrayList());
        Measure emptyMeasure = Measure.getMeasure(1, layers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
        
        int noteElemCount = 0;
        
        for(NotationElement n : emptyMeasure) {
            ++noteElemCount;
            break;
        }
        
        assertEquals(0, noteElemCount);
    }
    
    @Test
    public void testIteratorMultipleLayersOneEmptyLayer() {
        List<List<NotationElement>> layers = new ArrayList();
        layers.add(new ArrayList());
        layers.add(new ArrayList());
        
        layers.get(1).add(C4Quarter);
        layers.get(1).add(Rest.getRest(Durations.QUARTER));
        layers.get(1).add(Chord.getChord(C4, E4, G4));
        
        Measure multiLayerWithEmptyLayer = Measure.getMeasure(1, layers, TimeSignatures.FOUR_FOUR, keySig, Clef.G_CLEF);
        
        int noteElemCount = 0;
        int expectedNoteElemCount = layers.get(0).size() + layers.get(1).size();
        
        for(NotationElement n : multiLayerWithEmptyLayer) {
            ++noteElemCount;
            assertTrue(noteElemCount <= expectedNoteElemCount);
        }
        
        assertEquals(expectedNoteElemCount, noteElemCount);
    }
}
