/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibanalysis.harmony;

import wmnlibnotation.Note;

/**
 * Weighs the pitch class of a note by its duration.
 * For example, the weight of the pitch class of a note with duration of 
 * a quarter is 0.25.
 * @author Otso Björklund
 */
public class DurationWeighter implements PCProfileWeighter {

    private static final DurationWeighter INSTANCE = new DurationWeighter();
    
    /**
     * @param note note for which weight is calculated.
     * @return weight of the pitch class of the note 
     * weighed by the duration of the note.
     */
    @Override
    public double weight(Note note) {
        return note.getDuration().toDouble();
    }
    
    private DurationWeighter() {}
    
    /**
     * This class is a singleton.
     * @return the only instance of this class.
     */
    public static DurationWeighter getInstance() {
        return INSTANCE;
    }
}
