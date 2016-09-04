package wmnkitnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Otso Björklund
 */
public class Staff implements Iterable<Measure> {
    
    public enum Info { NAME, ABBR_NAME, TRANSPOSITION }
    
    private final Map<Info, String> staffInfo;
    private final List<Measure> measures;
    
    public Staff(String name, List<Measure> measures) {
        
        this.staffInfo = new HashMap();
        this.staffInfo.put(Info.NAME, name);
        this.measures = Collections.unmodifiableList(new ArrayList(measures));
        
        if(this.staffInfo.get(Info.NAME) == null || this.measures == null)
            throw new NullPointerException();
    }
    
    public List<Measure> getMeasures() {
        return this.measures;
    }
    
    public String getName() {
        String name = this.staffInfo.get(Info.NAME);
        return (name != null) ? name : "";
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Staff: ").append(getName()).append("\n");
        
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
