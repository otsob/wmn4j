/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import java.util.Comparator;

/**
 * <code>Comparator</code> for comparing the pitch height of notes.
 * @author Otso Björklund
 */
public enum NotePitchComparator implements Comparator<Note> {
    INSTANCE;
    
    /**
     * Compare the pitch height of notes.
     * @param o1 note whose pitch height is compared with o2.
     * @param o2 note whose pitch height is compared with o1.
     * @return negative integer if this is lower than other, positive integer 
     * if this is higher than other, 0 if pitches are (enharmonically) of same height.
     */
    @Override
    public int compare(Note o1, Note o2) {
        return o1.getPitch().compareTo(o2.getPitch()); 
    }
}
