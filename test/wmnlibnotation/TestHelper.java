/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Rest;
import wmnlibnotation.noteobjects.TimeSignatures;
import wmnlibnotation.noteobjects.KeySignature;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.Clefs;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.KeySignatures;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import wmnlibnotation.builders.ChordBuilder;
import wmnlibnotation.builders.MeasureBuilder;
import wmnlibnotation.builders.NoteBuilder;
import wmnlibnotation.builders.RestBuilder;

/**
 *
 * @author Otso Björklund
 */
public class TestHelper {
    
    private static Map<Integer, List<Durational>> singleNoteLayer = new HashMap();
    private static Map<Integer, List<Durational>> multipleNoteLayers = new HashMap();
    
    private static KeySignature keySig = KeySignatures.CMAJ_AMIN;
    
    private static NoteBuilder C4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
    private static NoteBuilder E4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
    private static NoteBuilder G4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
    private static NoteBuilder C4Quarter = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
    
    public static MeasureBuilder getTestMeasureBuilder(int number) {
        MeasureBuilder builder = new MeasureBuilder(number);
        
        builder.addToLayer(0, C4Quarter);
        builder.addToLayer(0, new RestBuilder(Durations.QUARTER));
        ChordBuilder chordBuilder = new ChordBuilder(C4.getDuration());
        chordBuilder.add(C4).add(E4).add(G4);
        builder.addToLayer(0, chordBuilder);
        
        builder.addToLayer(1, new RestBuilder(Durations.QUARTER));
        builder.addToLayer(1, C4);
        builder.addToLayer(1, new RestBuilder(Durations.QUARTER));
        
        return builder;
    }
    
    public static Measure getTestMeasure(int number) {
//        List<Durational> layerContents = new ArrayList();
//        layerContents.add(C4Quarter);
//        layerContents.add(Rest.getRest(Durations.QUARTER));
//        layerContents.add(Chord.getChord(C4, E4, G4));
//        singleNoteLayer.put(0, layerContents);
//        
//        multipleNoteLayers = new HashMap();
//        multipleNoteLayers.put(0, layerContents);
//        multipleNoteLayers.put(1, new ArrayList());
//        multipleNoteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
//        multipleNoteLayers.get(1).add(C4);
//        multipleNoteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
        
        return getTestMeasureBuilder(number).build();
        // return new Measure(number, multipleNoteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
    }
}
