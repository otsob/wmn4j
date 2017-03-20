/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Class for key signatures. 
 * This class is immutable.
 * @author Otso Björklund
 */
public class KeySignature {
    private final List<Pitch.Base> sharps;
    private final List<Pitch.Base> flats;
    
    /**
     * Get an instance of a KeySignature.
     * For common key signatures use the ones defined in <code>KeySignatures</code>.
     * This is mostly intended for creating custom key signatures.
     * @param sharps the Pitch.Base names that should be raised. For example, 
     * for G-major this list consists only of Pitch.Base.F.
     * @param flats the Pitch.Base names that should be flattened. For example, 
     * for F-major this list consists only of Pitch.Base.B.
     * @return a <code>KeySignature</code> with the specified sharps and flats.
     */
    public static KeySignature getKeySig(List<Pitch.Base> sharps, List<Pitch.Base> flats) {
        return new KeySignature(sharps, flats);
    }
    
    /**
     * Constructor for KeySignature.
     * Use the method {@link #getKeySig(java.util.List, java.util.List) getKeySig} 
     * to get an instance of <code>KeySignature</code>.
     * @throws IllegalArgumentException if the same Pitch.Base is in both sharps and flats.
     * @param sharps the Pitch.Base names that should be raised. For example, 
     * for G-major this list consists only of Pitch.Base.F.
     * @param flats the Pitch.Base names that should be flattened. For example, 
     * for F-major this list consists only of Pitch.Base.B.
     */
    private KeySignature(List<Pitch.Base> sharps, List<Pitch.Base> flats) {
        if(sharps != null && !sharps.isEmpty()) {
            this.sharps = new ArrayList(sharps);
        }
        else {
            this.sharps = Collections.EMPTY_LIST;
        }
        if(flats != null && !flats.isEmpty()) {
            this.flats = new ArrayList(flats);
        }
        else {
            this.flats = Collections.EMPTY_LIST;
        }
        
        // Check that there are no conflicts and throw exception if there are.
        for(Pitch.Base sharp : this.sharps) {
            if(this.flats.contains(sharp))
                throw new IllegalArgumentException(sharp + " is both in sharps and in flats.");
        }
        
        for(Pitch.Base flat : this.flats) {
            if(this.sharps.contains(flat))
                throw new IllegalArgumentException(flat + " is both in flats and in sharps.");
        }
    }
    
    /**
     * @return number of sharps in this KeySignature.
     */
    public int getNumSharps() {
        return this.sharps.size();
    }
    
    /**
     * @return number of flats in this KeySignature. 
     */
    public int getNumFlats() {
        return this.flats.size();
    }
    
    /**
     * @return a copy of the sharps in this KeySignature.
     */
    public List<Pitch.Base> getSharps() {
        return new ArrayList(this.sharps);
    }
    
    /**
     * @return a copy of the flats in this KeySignature.
     */
    public List<Pitch.Base> getFlats() {
        return new ArrayList(this.flats);
    }
    
    /**
     * String representation of <code>KeySignature</code>.
     * String is of form: <code>KeySig(sharps: ...)</code>,
     * <code>KeySig(flats: ...)</code>, or
     * <code>KeySig(sharps: ... flats: ...)</code> where <code>...</code> 
     * refers to the raised or flatted pitches.
     * @return string representation of KeySignature.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("KeySig(");
        if(!this.sharps.isEmpty()) {
            strBuilder.append("sharps: ");
            for(Pitch.Base sharp : this.sharps) {
                strBuilder.append(sharp).append("#").append(" ");
            }
        }
        
        if(!this.flats.isEmpty()) {
            strBuilder.append("flats: ");
            for(Pitch.Base flat : this.flats) {
                strBuilder.append(flat).append("b").append(" ");
            }
        }
        if(strBuilder.charAt(strBuilder.length() - 1) == ' ')
            strBuilder.deleteCharAt(strBuilder.length() - 1);
        
        strBuilder.append(")");
        return strBuilder.toString();
    }
    
    /**
     * Compare this to <code>Object o</code> for equality.
     * @param o Object against which this is compared for equality.
     * @return true if Object o is of class KeySignature and has the same 
     * sharps and flats as this. false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if(this == o) 
            return true;
        
        if(!(o instanceof KeySignature))
            return false;
        
        KeySignature other = (KeySignature) o;
        return this.sharps.equals(other.sharps) && this.flats.equals(other.flats);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.sharps);
        hash = 41 * hash + Objects.hashCode(this.flats);
        return hash;
    }
}


