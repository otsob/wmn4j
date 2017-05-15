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
 * Builder for building <code>Part</code> objects.
 * @author Otso Björklund
 */
public class PartBuilder {
    
    private final Map<Integer, List<Measure>> staveContents = new HashMap();
    private final Map<Part.Attribute, String> partAttributes = new HashMap();
    private static final int singleStaffNumber = 1;
    
    public PartBuilder(String name) {
        this.partAttributes.put(Part.Attribute.NAME, name);
    }
    
    public int getStaffCount() {
        return this.staveContents.size();
    }
    
    public void addMeasure(Measure measure) {
        this.addMeasureToStaff(singleStaffNumber, measure);
    }
    
    public void addMeasureToStaff(int staffNumber, Measure measure) {
        if(measure == null)
            throw new NullPointerException("Cannot add null to staff");
        
        if(!this.staveContents.containsKey(staffNumber))
            this.staveContents.put(staffNumber, new ArrayList());
        
        this.staveContents.get(staffNumber).add(measure);
    }
 
    public void setAttribute(Part.Attribute attribute, String value) {
        this.partAttributes.put(attribute, value);
    }
    
    public String getName() {
        return this.partAttributes.get(Part.Attribute.NAME);
    }
    
    public Part build() {
        if(this.staveContents.size() == 1) 
            return new SingleStaffPart(this.partAttributes, this.staveContents.get(singleStaffNumber));
        else {
            Map<Integer, Staff> staves = new HashMap();
            for(int staffNumber : this.staveContents.keySet())
                staves.put(staffNumber, new Staff(this.staveContents.get(staffNumber)));
            
            return new MultiStaffPart(this.partAttributes, staves);
        }
    }
}
