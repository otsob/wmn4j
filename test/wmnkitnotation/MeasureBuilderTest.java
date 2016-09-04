/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

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
    public void testBuildMeasure() {
        MeasureBuilder builder = new MeasureBuilder(1, TimeSignatures.SIX_EIGHT, KeySignature.DFlatMaj_BFlatMin, Measure.Barline.DOUBLE, Clef.F_CLEF);
        builder.addLayer();
        assertEquals(1, builder.getNumberOfLayers());
        builder.addToLayer(0, Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT))
               .addToLayer(0, Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT))
               .addToLayer(0, Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT));
          
        Measure measure = builder.build();
        assertTrue(measure != null);
        assertEquals(Measure.Barline.DOUBLE, measure.getBarline());
        assertEquals(Clef.F_CLEF, measure.getClef());
        assertEquals(KeySignature.DFlatMaj_BFlatMin, measure.getKeySignature());
        assertEquals(TimeSignatures.SIX_EIGHT, measure.getTimeSignature());
        assertEquals(1, measure.getNumber());
        
        assertEquals(1, measure.getNumberOfLayers());
        List<NotationElement> layer = measure.getLayer(0);
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT), layer.get(0));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT), layer.get(1));
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT), layer.get(2));
    }
    
    @Test
    public void testAdditionOfLayers() { 
        MeasureBuilder builder = new MeasureBuilder(1, TimeSignatures.SIX_EIGHT, KeySignature.DFlatMaj_BFlatMin, Measure.Barline.DOUBLE, Clef.F_CLEF);
        builder.addToLayer(1, Rest.getRest(Durations.EIGHT));
        assertEquals(2, builder.getNumberOfLayers());
        builder.addToLayer(3, Rest.getRest(Durations.EIGHT));
        assertEquals(4, builder.getNumberOfLayers());
        
        Measure measure = builder.build();
        assertEquals(4, measure.getNumberOfLayers());
        assertTrue(measure.getLayer(0).isEmpty());
        assertTrue(measure.getLayer(1).size() == 1);
        assertTrue(measure.getLayer(1).contains(Rest.getRest(Durations.EIGHT)));
        assertTrue(measure.getLayer(2).isEmpty());
        assertTrue(measure.getLayer(3).size() == 1);
        assertTrue(measure.getLayer(3).contains(Rest.getRest(Durations.EIGHT)));
    }
    
}
