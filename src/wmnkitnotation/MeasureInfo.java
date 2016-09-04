/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

import java.util.Objects;

/**
 *
 * @author Otso Björklund
 */
public class MeasureInfo {
    
    private final TimeSignature timeSig;
    private final KeySignature keySig;
    private final Measure.Barline barline;
    private final Clef clef;
    
    public static MeasureInfo getMeasureInfo(TimeSignature timeSig, KeySignature keySig, Measure.Barline barline, Clef clef) {
        return new MeasureInfo(timeSig, keySig, barline, clef);
    }
    
    private MeasureInfo(TimeSignature timeSig, KeySignature keySig, Measure.Barline barline, Clef clef) {
        this.timeSig = timeSig;
        this.keySig = keySig;
        this.barline = barline;
        this.clef = clef;
        
        if(this.barline == null)
            throw new NullPointerException("barline is null");
            
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
    
    public Measure.Barline getBarline() {
        return this.barline;
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
                && this.barline.equals(other.barline)
                && this.timeSig.equals(other.timeSig)
                && this.keySig.equals(other.keySig);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.timeSig);
        hash = 89 * hash + Objects.hashCode(this.keySig);
        hash = 89 * hash + Objects.hashCode(this.barline);
        hash = 89 * hash + Objects.hashCode(this.clef);
        return hash;
    }
    
    @Override
    public String toString() {
        return this.timeSig + ", " + this.keySig + ", " + this.clef;
    }
}
