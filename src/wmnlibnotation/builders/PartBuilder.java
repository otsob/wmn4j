/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.Part;
import wmnlibnotation.noteobjects.MultiStaffPart;
import wmnlibnotation.noteobjects.Staff;
import wmnlibnotation.noteobjects.SingleStaffPart;
import wmnlibnotation.noteobjects.Measure;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for building <code>Part</code> objects.
 * @author Otso Björklund
 */
public class PartBuilder {
    
    private final Map<Integer, List<MeasureBuilder>> staveContents = new HashMap<>();
    private final Map<Part.Attribute, String> partAttributes = new HashMap<>();
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
     * Adds a <code>MeasureBuilder</code>. This is used for building parts with a single staff.
     * @param measureBuilder The measureBuilder that is added to the end of the staff.
     * @return reference to this.
     */
    public PartBuilder add(MeasureBuilder measureBuilder) {
        this.addToStaff(SINGLE_STAFF_NUMBER, measureBuilder);
        return this;
    }
    
    /**
     * Adds a <code>MeasureBuilder</code> to staff. This is used for building parts with multiple
     * staves.
     * @param staffNumber The number of the staff to which measureBuilder is added.
     * @param measureBuilder The measureBuilder that is added to the end of the staff.
     * @return reference to this.
     */
    public PartBuilder addToStaff(int staffNumber, MeasureBuilder measureBuilder) {
        if(measureBuilder == null)
            throw new NullPointerException("Cannot add null to staff");
        
        if(!this.staveContents.containsKey(staffNumber))
            this.staveContents.put(staffNumber, new ArrayList<>());
        
        this.staveContents.get(staffNumber).add(measureBuilder);
        return this;
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
     * Creates a part using the contained <code>MeasureBuilder</code> objects.
     * @return A <code>Part</code> with the measures and attributes in the builder.
     * The type of the part depends on the number of staves.
     */
    public Part build() {
        if(this.staveContents.size() == 1) {
            List<Measure> measures = new ArrayList<>();
            this.staveContents.get(SINGLE_STAFF_NUMBER).forEach((builder) -> measures.add(builder.build()));
            return new SingleStaffPart(this.partAttributes, measures);
        
        }
        else {
            Map<Integer, Staff> staves = new HashMap<>();
            for(int staffNumber : this.staveContents.keySet()) {
                List<Measure> measures = new ArrayList<>();
                this.staveContents.get(staffNumber).forEach((builder) -> measures.add(builder.build()));
                staves.put(staffNumber, new Staff(measures));
            }
            
            return new MultiStaffPart(this.partAttributes, staves);
        }
    }
}
