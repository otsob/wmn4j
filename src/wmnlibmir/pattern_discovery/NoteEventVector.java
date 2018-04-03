/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir.pattern_discovery;

import wmnlibnotation.ScorePosition;


/**
 * 
 * @author Otso Björklund
 */
public class NoteEventVector implements Comparable<NoteEventVector> {
    
    private final double[] components;
    private final int hash;
    private final ScorePosition scorePosition;
    
    public NoteEventVector(double[] components) {
        this.components = new double[components.length];
        System.arraycopy(components, 0, this.components, 0, components.length);
        this.scorePosition = null;
        this.hash = computeHash();
    }
    
    public NoteEventVector(double[] components, ScorePosition scorePosition) {
        this.components = new double[components.length];
        System.arraycopy(components, 0, this.components, 0, components.length);
        this.scorePosition = scorePosition;
        this.hash = computeHash();
    }
    
    public int getDimensionality() {
        return this.components.length;
    }
    
    public double getComponent(int index) {
        return this.components[index];
    }
    
    public boolean hasPosition() {
        return this.scorePosition != null;
    }
    
    public ScorePosition getPosition() {
        return this.scorePosition;
    }
    
    public NoteEventVector add(NoteEventVector other) {
        double[] sumComponents = new double[this.getDimensionality()];
        
        for(int i = 0; i < sumComponents.length; ++i) {
            sumComponents[i] = this.components[i] + other.getComponent(i);
        }
        
        return new NoteEventVector(sumComponents);
    }
    
    public NoteEventVector subtract(NoteEventVector other) {
        double[] diffComponents = new double[this.getDimensionality()];
        
        for(int i = 0; i < diffComponents.length; ++i) {
            diffComponents[i] = this.components[i] - other.getComponent(i);
        }
        
        return new NoteEventVector(diffComponents);
    }
    
    @Override
    public int compareTo(NoteEventVector other) {
        for(int i = 0; i < this.getDimensionality(); ++i) {
            // TODO: Consider checking if doubles just really close to each other
            if(this.getComponent(i) < other.getComponent(i))
                return -1;
            if(this.getComponent(i) > other.getComponent(i))
                return 1;
        }
        
        return 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        
        if(!(o instanceof NoteEventVector))
            return false;
        
        NoteEventVector other = (NoteEventVector) o;
        if(other.getDimensionality() != this.getDimensionality())
            return false;
        
        return this.compareTo(other) == 0;
    }
    
    private int computeHash() {
        // TODO: Improbe the hash function
        int multiplierIndex = 0;
        long hash = RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);
        
        for(int i = 0; i < this.components.length; ++i) {
            long bits = Double.doubleToRawLongBits(this.components[i]);
            int first = (int) (bits >> 32);
            hash += first * RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);
            int second = (int) bits;
            hash += second * RandomMultiplierProvider.INSTANCE.getMultiplier(multiplierIndex++);
        }
       
        return (int) hash;
    }
    
    @Override
    public int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("(");
        for(int i = 0; i < this.components.length - 1; ++i) {
            strBuilder.append(Double.toString(this.components[i])).append(", ");
        }
        strBuilder.append(Double.toString(this.components[this.components.length-1])).append(")");
        
        if(this.scorePosition != null)
            strBuilder.append(" at ").append(scorePosition);
        
        return strBuilder.toString();
    }
}
