/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

import wmnkitnotation.Chord;
import wmnkitnotation.Durations;
import wmnkitnotation.Note;
import wmnkitnotation.Measure;
import wmnkitnotation.NotationElement;
import wmnkitnotation.Rest;
import wmnkitnotation.Pitch;
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
    
    public MeasureTest() {
        Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
        Note E4 = Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
        Note G4 = Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
        
        this.noteLayer = new ArrayList();
        this.noteLayer.add(new ArrayList());
        this.noteLayer.get(0).add(C4);
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
        
        try {
            Measure m = Measure.getMeasure(0, this.noteLayers);
            assertTrue("Exception not thrown", false);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
    
    @Test
    public void testGetLayer() {
        
    }

    @Test
    public void testGetNumber() {
    }

    @Test
    public void testToString() {

    }
    
    @Test
    public void testIterator() {
        
    }
    
}
