/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.Objects;

/**
 *
 * @author Otso Björklund
 */
public class Clef {
    public enum Type { G, F, C, PERCUSSION }
    private final Type type;
    // The the center of the clef counted from bottom.
    private final int line;
    
    public static Clef getClef(Type type, int line) {
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
