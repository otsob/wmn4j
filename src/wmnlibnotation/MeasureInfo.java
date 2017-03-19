/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Otso Björklund
 */
public class MeasureInfo {
    
    private final TimeSignature timeSig;
    private final KeySignature keySig;
    private final Barline rightBarline;
    private final Barline leftBarline;
    private final Clef clef;
    private final Map<Duration, Clef> clefChanges;
    
    public static MeasureInfo getMeasureInfo(TimeSignature timeSig, KeySignature keySig, Barline rightBarline, Clef clef) {
        return getMeasureInfo(timeSig, keySig, rightBarline, Barline.SINGLE, clef);
    }
    
    public static MeasureInfo getMeasureInfo(TimeSignature timeSig, KeySignature keySig, Barline rightBarline, Barline leftBarline, Clef clef) {
        // Todo: This should definitely use caching.
        return new MeasureInfo(timeSig, keySig, rightBarline, leftBarline, clef);
    }
    
    private MeasureInfo(TimeSignature timeSig, KeySignature keySig, Barline rightBarline, Barline leftBarline, Clef clef) {
        this.timeSig = timeSig;
        this.keySig = keySig;
        this.rightBarline = rightBarline;
        this.leftBarline = leftBarline;
        this.clef = clef;
        // Add this 
        this.clefChanges = new HashMap();
        
        if(this.rightBarline == null)
            throw new NullPointerException("right barline is null");
        
        if(this.leftBarline == null)
            throw new NullPointerException("right barline is null");
        
        if(this.timeSig == null)
            throw new NullPointerException("timeSig is null");
            
        if(this.keySig == null)
            throw new NullPointerException("keySig is null");

        if(this.clef == null)
            throw new NullPointerException("clef is null");
    }
    
    public TimeSignature getTimeSignature() {
        return this.timeSig;
    }
    
    public KeySignature getKeySignature() {
        return this.keySig;
    }
    
    public Barline getRightBarline() {
        return this.rightBarline;
    }
    
    public Barline getLeftBarline() {
        return this.leftBarline;
    }
    
    public Clef getClef() {
        return this.clef;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof MeasureInfo))
            return false;
        
        MeasureInfo other = (MeasureInfo) o;
        
        return this.clef.equals(other.clef)
                && this.rightBarline.equals(other.rightBarline)
                && this.timeSig.equals(other.timeSig)
                && this.keySig.equals(other.keySig);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.timeSig);
        hash = 89 * hash + Objects.hashCode(this.keySig);
        hash = 89 * hash + Objects.hashCode(this.rightBarline);
        hash = 89 * hash + Objects.hashCode(this.clef);
        return hash;
    }
    
    @Override
    public String toString() {
        return this.timeSig + ", " + this.keySig + ", " + this.clef;
    }
}
