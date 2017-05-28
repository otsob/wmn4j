/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibanalysis;

import wmnlibnotation.Note;

/**
 * Interface for implementing the weighing function for <code>PCProfile</code>.
 * @author Otso Björklund
 */
public interface PCProfileWeighter {
    
    /**
     * Calculates the coefficient for the pitch class of the note.
     * Used for adding notes to <code>PCProfile</code> objects.
     * Must return a non-negative value.
     * @param note note for which weight is calculated.
     * @return weight of the pitch class of the note.
     */
    double weight(Note note);
}
