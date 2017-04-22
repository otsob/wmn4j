/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class for parts that have multiple staves such as keyboard instruments. 
 * This class is immutable.
 * @author Otso Björklund
 */
public class MultiStaffPart implements Part {

    private final Map<Part.Attribute, String> partAttributes;
    private final SortedMap<Integer, Staff> staves;
    
    public MultiStaffPart(String name, Map<Integer, Staff> staves) {
        this.partAttributes = new HashMap();
        this.partAttributes.put(Part.Attribute.NAME, name);
        this.staves = Collections.unmodifiableSortedMap(new TreeMap(staves));
    }
    
    public MultiStaffPart(Map<Part.Attribute, String> attributes, Map<Integer, Staff> staves) {
        this.partAttributes = Collections.unmodifiableMap(new HashMap(attributes));
        this.staves = Collections.unmodifiableSortedMap(new TreeMap(staves));
    }
    
    @Override
    public String getName() {
        return this.getPartAttribute(Part.Attribute.NAME);
    }

    @Override
    public boolean isMultiStaff() {
        return true;
    }

    @Override
    public int getStaffCount() {
        return this.staves.keySet().size();
    }
    
    public int getMeasureCount() {
        return this.staves.get(this.getStaffNumbers().get(0)).getMeasureCount();
    }
    
    public Staff getStaff(int number) {
        return this.staves.get(number);
    }
    
    public List<Integer> getStaffNumbers() {
        return new ArrayList(this.staves.keySet());
    }
    
    @Override
    public String getPartAttribute(Attribute attribute) {
        if(this.partAttributes.containsKey(attribute))
            return this.partAttributes.get(attribute);
        else
            return "";
    }

    /**
     * Iterates through the measures by going through all staves for a certain 
     * measure number before going on to the next measure. 
     * Staves are iterated through from smallest staff number to greatest.
     * Does not support removing.
     * @return iterator.
     */
    @Override
    public Iterator<Measure> iterator() {
        class MultiStaffIterator implements Iterator<Measure> {

            private int keyIndex = 0;
            private List<Integer> keys = new ArrayList(MultiStaffPart.this.getStaffNumbers());
            private int measureNumber = 1;
            
            @Override
            public boolean hasNext() {
                if(this.measureNumber > MultiStaffPart.this.getMeasureCount())
                    return false;
                
                return true;
            }

            @Override
            public Measure next() {
                Measure measure = null;
                
                if(this.hasNext()) {
                    int key = this.keys.get(keyIndex);
                    measure = MultiStaffPart.this.staves.get(key).getMeasure(measureNumber);
                    
                    ++keyIndex;
                    if(keyIndex == this.keys.size()) {
                        keyIndex = 0;
                        ++this.measureNumber;
                    }
                }
                else
                    throw new NoSuchElementException();
                
                return measure;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing not supported.");
            }
        }
        
        return new MultiStaffIterator();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Part: ");
        
        for(Attribute attr : this.partAttributes.keySet())
            builder.append(attr).append(": ").append(this.partAttributes.get(attr));
        
        for(Measure m : this)
            builder.append("\n").append(m.toString());
        
        return builder.toString();
    }
}

