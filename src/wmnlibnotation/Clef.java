/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import java.util.Objects;

/**
 * Class for clefs.
 * Clefs have a type which tells the shape of the clef and position which is the 
 * line on which the center of the clef is situated. For example, the center 
 * of a G-type clef is the part of the clef that denotes G4.
 * @author Otso Björklund
 */
public class Clef {
    public enum Type { G, F, C, PERCUSSION }
    private final Type type;
    // The the center of the clef counted from bottom.
    private final int line;
    
    /**
     * Get an instance of <code>Clef</code>.
     * @throws IllegalArgumentException if line is smaller than 1.
     * @throws NullPointerException if type is null.
     * @param type type of the clef.
     * @param line counting from the bottom line, the line on which the clef is centered.
     * @return a Clef with the specified properties.
     */
    public static Clef getClef(Type type, int line) {
        if(type == null)
            throw new NullPointerException("clef is null");
        
        if(line < 1) 
            throw new IllegalArgumentException("line is smaller than 1");
        
        return new Clef(type, line);
    }
    
    private Clef(Type type, int line) {
        this.type = type;
        this.line = line;
    }
    
    @Override
    public String toString() {
        return this.type + "-clef(" + this.line + ")";
    }
    
    /**
     * Compare this <code>Clef</code> with <code>Object o</code> for equality.
     * @param o the Object against which this is compared for equality.
     * @return true if o is an instance of Clef and has the same type and position as this.
     */
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        
        if(!(o instanceof Clef))
            return false;
        
        Clef other = (Clef) o;
        return this.type == other.type && this.line == other.line;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + Objects.hashCode(this.type);
        hash = 71 * hash + this.line;
        return hash;
    }
}
