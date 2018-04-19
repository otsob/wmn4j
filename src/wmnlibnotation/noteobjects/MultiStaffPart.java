/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import wmnlibnotation.noteobjects.Staff;
import wmnlibnotation.noteobjects.Measure;
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
        this.partAttributes = new HashMap<>();
        this.partAttributes.put(Part.Attribute.NAME, name);
        this.staves = Collections.unmodifiableSortedMap(new TreeMap<>(staves));
    }
    
    public MultiStaffPart(Map<Part.Attribute, String> attributes, Map<Integer, Staff> staves) {
        this.partAttributes = Collections.unmodifiableMap(new HashMap<>(attributes));
        this.staves = Collections.unmodifiableSortedMap(new TreeMap<>(staves));
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
    
    @Override
    public int getMeasureCount() {
        return this.staves.get(this.getStaffNumbers().get(0)).getMeasureCount();
    }
    
    @Override
    public int getFullMeasureCount() {
        return this.staves.get(this.getStaffNumbers().get(0)).getFullMeasureCount();
    }
    
    /**
     * Returns the <code>Staff</code> with the number.
     * @param number number of staff.
     * @return <code>Staff</code> associated with the number.
     */
    public Staff getStaff(int number) {
        return this.staves.get(number);
    }
    
    /**
     * @return the numbers of the staves in the part.
     */
    public List<Integer> getStaffNumbers() {
        return new ArrayList<>(this.staves.keySet());
    }
    
    @Override
    public String getPartAttribute(Attribute attribute) {
        if(this.partAttributes.containsKey(attribute))
            return this.partAttributes.get(attribute);
        else
            return "";
    }
    
    @Override
    public Measure getMeasure(int staffNumber, int measureNumber) {
        return this.staves.get(staffNumber).getMeasure(measureNumber);
    }

    @Override
    public Part.Iter getPartIterator() {
        return new MultiStaffPart.Iter(this);
    }
    
    @Override
    public Iterator<Measure> iterator() {
        return this.getPartIterator();
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
    
    /**
     * Iterator for <code>MultiStaffPart</code>
     * Iterates through the measures by going through all staves for a certain 
     * measure number before going on to the next measure. 
     * Staves are iterated through from smallest staff number to greatest.
     * Does not support removing.
     */
    public static class Iter implements Part.Iter {
        private final MultiStaffPart part;
        private int keyIndex;
        private final List<Integer> keys;
        private int measureNumber;
        private int prevStaffNumber = 0;
        private int prevMeasureNumber = 0;

        public Iter(MultiStaffPart part) {
            this.part = part;
            this.keyIndex = 0;
            this.keys = this.part.getStaffNumbers();
            this.measureNumber = 1;

            // If there is a pickup measure start from measure 0.
            if(this.part.staves.get(this.keys.get(0)).hasPickupMeasure())
                this.measureNumber = 0;
        }
        
        @Override
        public int getStaffNumberOfPrevious() {
            return this.prevStaffNumber;
        }
        
        @Override
        public int getMeasureNumberOfPrevious() {
            return this.prevMeasureNumber;
        }

        @Override
        public boolean hasNext() {
            return this.measureNumber <= this.part.getFullMeasureCount();
        }

        @Override
        public Measure next() {
            Measure measure = null;

            if(this.hasNext()) {
                this.prevStaffNumber = this.keys.get(keyIndex);
                this.prevMeasureNumber = this.measureNumber;
                measure = this.part.staves.get(this.prevStaffNumber).getMeasure(this.prevMeasureNumber);

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
}

