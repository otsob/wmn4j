/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Otso Björklund
 */
public class TestHelper {
    
    private static Map<Integer, List<Durational>> singleNoteLayer = new HashMap();
    private static Map<Integer, List<Durational>> multipleNoteLayers = new HashMap();
    
    private static KeySignature keySig = KeySignatures.CMAJ_AMIN;
    
    private static Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
    private static Note E4 = Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
    private static Note G4 = Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
    private static Note C4Quarter = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
    
    public static Measure getTestMeasure(int number) {
        List<Durational> layerContents = new ArrayList();
        layerContents.add(C4Quarter);
        layerContents.add(Rest.getRest(Durations.QUARTER));
        layerContents.add(Chord.getChord(C4, E4, G4));
        singleNoteLayer.put(0, layerContents);
        
        multipleNoteLayers = new HashMap();
        multipleNoteLayers.put(0, layerContents);
        multipleNoteLayers.put(1, new ArrayList());
        multipleNoteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
        multipleNoteLayers.get(1).add(C4);
        multipleNoteLayers.get(1).add(Rest.getRest(Durations.QUARTER));
        
        return new Measure(number, multipleNoteLayers, TimeSignatures.FOUR_FOUR, keySig, Clefs.G);
    }
}
