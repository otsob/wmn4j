package wmnkitnotation;

import java.util.List;

/**
 *
 * @author Otso Björklund
 */
public class Staff {
    
    private final String name;
    private final List<Measure> measures;
    
    public Staff(String name, List<Measure> measures) {
        this.name = name;
        this.measures = measures;
    }
    
    @Override
    public String toString() {
        String contents = "Staff: " + this.name + "\n";
        
        for(int i = 0; i < measures.size(); ++i) {
            contents += measures.get(i).toString() + "\n";
        }
        
        return contents;
    }
}
