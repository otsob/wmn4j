/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Otso Björklund
 */
public class SingleStaffPart implements Part {
    
    private final Map<Part.Attr, String> partAttributes;
    private final Staff staff;
    
    public SingleStaffPart(String name, List<Measure> measures) {
        this.staff = new Staff(measures);
        Map<Part.Attr, String> attributes = new HashMap();
        attributes.put(Attr.NAME, name);
        this.partAttributes = Collections.unmodifiableMap(attributes);
    }
    
    public SingleStaffPart(Map<Part.Attr, String> partAttributes, List<Measure> measures) {
        this.staff = new Staff(measures);
        this.partAttributes = Collections.unmodifiableMap(new HashMap(partAttributes));
    }
    
    public String getName() {
        return this.getPartAttribute(Attr.NAME);
    }
    
    public boolean isMultiStaff() {
        return false;
    }
    
    public Staff getStaff() {
        return this.staff;
    }

    @Override
    public Iterator<Measure> iterator() {
        return this.staff.iterator();
    }

    @Override
    public String getPartAttribute(Attr attribute) {
        if(this.partAttributes.containsKey(attribute))
            return this.partAttributes.get(attribute);
        else
            return "Not Set";
    }
}
