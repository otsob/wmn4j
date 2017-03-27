package wmnlibnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Class for representing a staff in a score.
 * @author Otso Björklund
 */
public class Staff implements Iterable<Measure> {
    
    /**
     * Type of staff.
     */
    enum Type { NORMAL, SINGLE_LINE };
    
    private final List<Measure> measures;
    private final Type type;
    
    public Staff(List<Measure> measures) {
        this.type = Type.NORMAL;
        this.measures = Collections.unmodifiableList(new ArrayList(measures));
    }
    
    /**
     * @return List of measures in this staff in order from first measure to last.
     */
    public List<Measure> getMeasures() {
        return this.measures;
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

    @Override
    public Iterator<Measure> iterator() {
        return this.measures.iterator();
    }
}
