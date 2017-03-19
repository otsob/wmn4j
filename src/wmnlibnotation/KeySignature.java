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
 * @author Otso Björklund
 */
public class KeySignature {
    private final List<Pitch.Base> sharps;
    private final List<Pitch.Base> flats;
    
    public static KeySignature getKeySig(List<Pitch.Base> sharps, List<Pitch.Base> flats) {
        return new KeySignature(sharps, flats);
    }
    
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
    
    public int getNumSharps() {
        return this.sharps.size();
    }
    
    public int getNumFlats() {
        return this.flats.size();
    }
    
    public List<Pitch.Base> getSharps() {
        return new ArrayList(this.sharps);
    }
    
    public List<Pitch.Base> getFlats() {
        return new ArrayList(this.flats);
    }
    
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


