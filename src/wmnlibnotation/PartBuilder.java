/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class PartBuilder {
    
    private final Map<Integer, List<Measure>> staveContents = new HashMap();
    private String name;
    
    public PartBuilder(String name) {
        this.name = name;
    }
    
    public int getStaffCount() {
        return this.staveContents.size();
    }
    
    public void addMeasure(Measure measure) {
        this.addMeasureToStaff(0, measure);
    }
    
    public void addMeasureToStaff(int staffNumber, Measure measure) {
        if(measure == null)
            throw new NullPointerException("Cannot add null to staff");
        
        if(!this.staveContents.containsKey(staffNumber))
            this.staveContents.put(staffNumber, new ArrayList());
        
        this.staveContents.get(staffNumber).add(measure);
    }
 
    public Part build() {
        if(this.staveContents.size() == 1) 
            return new SingleStaffPart(this.name, this.staveContents.get(0));
        else 
            return null;
    }
    
    
}
