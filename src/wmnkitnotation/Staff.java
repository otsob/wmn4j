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
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Staff: ").append(this.name).append("\n");
        
        for(int i = 0; i < measures.size(); ++i) {
            strBuilder.append(measures.get(i).toString()).append("\n");
        }
        
        return strBuilder.toString();
    }
}
