
package wmnlibnotation;

import java.util.Objects;

/**
 *
 * @author Otso Björklund
 */
public class Pitch implements Comparable<Pitch> {

    public enum Base { C, D, E, F, G, A, B }

    private static final int alterLimit = 3;
    private static final int maxOctave = 10;
    
    private final Base pitchBase;
    private final int alter;
    private final int octave;
    
    public static Pitch getPitch(Base pitchName, int alter, int octave) {
        return new Pitch(pitchName, alter, octave);
    }
    
    private Pitch(Base pitchName, int alter, int octave) {
        if(alter > alterLimit || alter < -1 * alterLimit)
            throw new IllegalArgumentException("alter was " + alter + ". alter must be between -" + alterLimit + " and " + alterLimit);
        
        if(octave < 0 || octave > maxOctave)
            throw new IllegalArgumentException("octave was " + octave + ". octave must be between 0 and " + maxOctave);
 
        this.pitchBase = pitchName;
        this.alter = alter;
        this.octave = octave;
    }
   
    public Base getBase() {
        return this.pitchBase;
    }
    
    public int getAlter() {
        return this.alter;
    }
    
    public int getOctave() {
        return this.octave;
    }
    
    public int toInt() {
        int pitchAsInt;
        
        switch(this.pitchBase) {
            case C: pitchAsInt = 0;
                    break;
            case D: pitchAsInt = 2;
                    break;
            case E: pitchAsInt = 4;
                    break;
            case F: pitchAsInt = 5;
                    break;
            case G: pitchAsInt = 7;
                    break;
            case A: pitchAsInt = 9;
                    break;
            case B: pitchAsInt = 11;
                    break;
            default:
                pitchAsInt = 0;
        }
        
        return pitchAsInt + this.alter + this.octave * 12;
    }
    
    public int getPitchClass() {
        return this.toInt() % 12;
    }
    
    public String getPitchClassName() {
        String pitchName = this.pitchBase.toString();
        
        if (alter >= 0 ) {
            for(int i = 0; i < alter; ++i)
                pitchName += "#";
        }
        else {
            for(int i = 0; i > alter; --i)
                pitchName += "b";
        }
            
        return pitchName;
    }
    
    public boolean equalsEnharmonically(Pitch other) {
        return this.toInt() == other.toInt();
    }
    
    @Override
    public String toString() {
        return this.getPitchClassName() + octave;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        
        if(!(o instanceof Pitch))
            return false;
        
        Pitch other = (Pitch) o;
        
        if(other.pitchBase != this.pitchBase)
            return false;
        
        if(other.octave != this.octave)
            return false;
        
        if(other.alter != this.alter)
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.pitchBase);
        hash = 89 * hash + this.alter;
        hash = 89 * hash + this.octave;
        return hash;
    }
    
    @Override
    public int compareTo(Pitch other) {
        return this.toInt() - other.toInt();
    }
    
    public boolean higherThan(Pitch other) {
        return this.toInt() > other.toInt();
    }
    
    public boolean lowerThan(Pitch other) {
        return this.toInt() < other.toInt();
    }
}
