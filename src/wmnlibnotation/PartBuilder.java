/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
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
    private static final int SINGLE_STAFF_NUMBER = 1;
    
    /**
     * @param name Name of the <code>Part</code> to be built.
     */
    public PartBuilder(String name) {
        this.partAttributes.put(Part.Attribute.NAME, name);
    }
    
    /**
     * @return the number of staves in the <code>PartBuilder</code>.
     */
    public int getStaffCount() {
        return this.staveContents.size();
    }
    
    /**
     * Adds a measure. This is used for building parts with a single staff.
     * If no staff exists yet, then it is created.
     * @param measure The measure that is added to the end of the staff.
     */
    public void addMeasure(Measure measure) {
        this.addMeasureToStaff(SINGLE_STAFF_NUMBER, measure);
    }
    
    /**
     * Adds a measure to staff. This is used for building parts with multiple
     * staves. If no staff with staffNumber exists, a new staff is created.
     * @param staffNumber The number of the staff to which measure is added.
     * @param measure The measure that is added to the end of the staff.
     */
    public void addMeasureToStaff(int staffNumber, Measure measure) {
        if(measure == null)
            throw new NullPointerException("Cannot add null to staff");
        
        if(!this.staveContents.containsKey(staffNumber))
            this.staveContents.put(staffNumber, new ArrayList());
        
        this.staveContents.get(staffNumber).add(measure);
    }
 
    /**
     * @param attribute The attribute to be set.
     * @param value The value that will be set for the attribute.
     */
    public void setAttribute(Part.Attribute attribute, String value) {
        this.partAttributes.put(attribute, value);
    }
    
    /**
     * @return The name of the <code>Part</code> being built.
     */
    public String getName() {
        return this.partAttributes.get(Part.Attribute.NAME);
    }
    
    /**
     * Creates a part from the measures.
     * @return A <code>Part</code> with the measures and attributes in the builder.
     * The type of the part depends on the number of staves.
     */
    public Part build() {
        if(this.staveContents.size() == 1) 
            return new SingleStaffPart(this.partAttributes, this.staveContents.get(SINGLE_STAFF_NUMBER));
        else {
            Map<Integer, Staff> staves = new HashMap();
            for(int staffNumber : this.staveContents.keySet())
                staves.put(staffNumber, new Staff(this.staveContents.get(staffNumber)));
            
            return new MultiStaffPart(this.partAttributes, staves);
        }
    }
}
