/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class MeasureTest {
    
    List<List<NotationElement>> noteLayer;
    List<List<NotationElement>> noteLayers;
    
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
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testGetMeasure() {
        assertTrue(Measure.getMeasure(1, this.noteLayers) != null);
        
        // Test exceptions thrown correctly for illegal arguments
        try {
            Measure m = Measure.getMeasure(0, this.noteLayers);
            assertTrue("Exception not thrown", false);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        
        try {
            Measure m = Measure.getMeasure(1, null);
            assertTrue("Exception not thrown", false);
        } catch (Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    
    @Test
    public void testGetLayer() {
        Measure m = Measure.getMeasure(1, noteLayers);
        assertTrue(m.getLayer(1).contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF)));
        assertTrue(m.getLayer(0).contains(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER)));
        
        List<NotationElement> layer = m.getLayer(0);
        try {
            layer.add(Rest.getRest(Durations.QUARTER));
            assertTrue("Failed to throw exception for disabled adding", false);
        }
        catch(Exception e) { /* Do nothing */ }
    }

    @Test
    public void testGetNumber() {
        assertEquals(1, Measure.getMeasure(1, noteLayer).getNumber());
        assertEquals(512, Measure.getMeasure(512, noteLayer).getNumber());
    }

    @Test
    public void testToString() {
        Measure m = Measure.getMeasure(5, noteLayer);
        assertEquals("Measure 5:\nLayer 0: C4(1/4), R(1/4), [C4(1/2),E4(1/2),G4(1/2)]\n", m.toString());
        
        Measure multiLayer = Measure.getMeasure(2, noteLayers);
        assertEquals("Measure 2:\n"
                        + "Layer 0: C4(1/4), R(1/4), [C4(1/2),E4(1/2),G4(1/2)]\n"
                        + "Layer 1: R(1/4), C4(1/2), R(1/4)\n", 
                        multiLayer.toString());
    }
    
    @Test
    public void testIteratorWithSingleLayerMeasure() {
        Measure singleLayerMeasure = Measure.getMeasure(1, noteLayer);
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
        Measure multiLayerMeasure = Measure.getMeasure(1, noteLayers);
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
        Measure m = Measure.getMeasure(1, noteLayers);
      
        try {
            Iterator<NotationElement> iter = m.iterator();
            iter.next();
            iter.remove();
            assertTrue("Expexted exception was not thrown", false);
        }
        catch(Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        } 
    }
    
    @Test
    public void testTimeWiseIteratorSingleLayer() {
        Measure singleLayerMeasure = Measure.getMeasure(1, noteLayer);
        
        Iterator<NotationElement> timeWiseIterator = singleLayerMeasure.timeWiseIterator();
        List<NotationElement> expectedNotes = this.noteLayer.get(0);
        int index = 0;
        
        while(timeWiseIterator.hasNext()) 
            assertEquals(expectedNotes.get(index++), timeWiseIterator.next());
    }
    
    @Test
    public void testTimeWiseIteratorMultiLayer() {
        Measure multiLayerMeasure = Measure.getMeasure(1, noteLayers);
        
        Iterator<NotationElement> timeWiseIterator = multiLayerMeasure.timeWiseIterator();
        
        List<NotationElement> expectedNotes = new ArrayList();
        expectedNotes.add(C4Quarter);
        expectedNotes.add(Rest.getRest(Durations.QUARTER));
        expectedNotes.add(Rest.getRest(Durations.QUARTER));
        expectedNotes.add(C4);
        expectedNotes.add(Chord.getChord(C4, E4, G4));
        expectedNotes.add(Rest.getRest(Durations.QUARTER));
     
        int index = 0;
        
        while(timeWiseIterator.hasNext()) 
            assertEquals(expectedNotes.get(index++), timeWiseIterator.next());
    }
}
