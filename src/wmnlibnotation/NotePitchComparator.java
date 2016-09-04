
package wmnlibnotation;

import java.util.Comparator;

/**
 *
 * @author Otso Björklund
 */
public enum NotePitchComparator implements Comparator<Note> {
    INSTANCE;
    
    @Override
    public int compare(Note o1, Note o2) {
        return o1.getPitch().compareTo(o2.getPitch()); 
    }
}
