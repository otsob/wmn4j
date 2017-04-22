package wmnlibnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class for representing a staff in a score. This class is immutable.
 * @author Otso Björklund
 */
public class Staff implements Iterable<Measure> {
    
    /**
     * Type of staff.
     */
    public enum Type { NORMAL, SINGLE_LINE };
    
    private final List<Measure> measures;
    private final Type type;
    
    /**
     * Constructor
     * @param measures the measures in the staff.
     */
    public Staff(List<Measure> measures) {
        this.type = Type.NORMAL;
        this.measures = Collections.unmodifiableList(new ArrayList(measures));
    }
    
    /**
     * Get <code>Measure</code> by measure number.
     * @param number the number of the measure to get from this staff.
     * @return the measure with number
     */
    public Measure getMeasure(int number) {
        return this.measures.get(number - 1);
    }
    
    /**
     * @return number of measures in this <code>Staff</code>.
     */
    public int getMeasureCount() {
        return this.measures.size();
    }
    
    /**
     * @return List of measures in this staff in order from first measure to last.
     */
    public List<Measure> getMeasures() {
        return this.measures;
    }
    
    /**
     * @return type of this <code>Staff</code>.
     */
    public Type getType() {
        return this.type;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Staff: ").append("\n");
        
        for(int i = 0; i < measures.size(); ++i) {
            strBuilder.append(measures.get(i).toString()).append("\n");
        }
        
        return strBuilder.toString();
    }

    /**
     * @return iterator that does not support modifying this <code>Staff</code>.
     */
    @Override
    public Iterator<Measure> iterator() {
        return this.measures.iterator();
    }
}
