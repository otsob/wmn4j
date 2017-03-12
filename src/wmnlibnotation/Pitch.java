
package wmnlibnotation;

import java.util.Objects;

/**
 * The <code>Pitch</code> class represents a pitch. Pitches consist of 
 * the basic pitch letter <code>Pitch.Base</code>, alter number which tells by how 
 * many half-steps the pitch is altered, and octave number which tells the octave of the note. 
 * Octave number is based on <a href="http://en.wikipedia.org/wiki/Scientific_pitch_notation">scientific pitch notation</a>.
 * @author Otso Björklund
 */
public class Pitch implements Comparable<Pitch> {

    public enum Base { C, D, E, F, G, A, B }

    private static final int alterLimit = 3;
    private static final int maxOctave = 10;
    
    private final Base pitchBase;
    private final int alter;
    private final int octave;
    
    /**
     * Returns a <code>Pitch</code> object.
     * @param pitchName the letter on which the name of the pitch is based.
     * @param alter by how many half-steps the pitch is altered up (positive values)
     * or down (negative values).
     * @param octave the octave of the pitch.
     * @return Pitch object with the specified attributes.
     */
    public static Pitch getPitch(Base pitchName, int alter, int octave) {
        return new Pitch(pitchName, alter, octave);
    }
    
    /**
     * Private constructor. To get a <code>Pitch</code> object use the method 
     * {@link #getPitch(wmnlibnotation.Pitch.Base, int, int)  getPitch}.
     * @throws IllegalArgumentException if alter is greater than 
     * {@link #alterLimit alterLimit} of smaller than {@link #alterLimit -1*alterLimit}, 
     * or if octave is negative or larger than {@link #maxOctave maxOctave}.
     * @param pitchName the letter on which the name of the pitch is based.
     * @param alter by how many half-steps the pitch is altered up or down.
     * @param octave the octave of the pitch.
     */
    private Pitch(Base pitchName, int alter, int octave) {
        if(alter > alterLimit || alter < -1 * alterLimit)
            throw new IllegalArgumentException("alter was " + alter + ". alter must be between -" + alterLimit + " and " + alterLimit);
        
        if(octave < 0 || octave > maxOctave)
            throw new IllegalArgumentException("octave was " + octave + ". octave must be between 0 and " + maxOctave);
 
        this.pitchBase = pitchName;
        this.alter = alter;
        this.octave = octave;
    }
   
    /**
     * @return the letter on which the name of the pitch is based.
     */
    public Base getBase() {
        return this.pitchBase;
    }
    
    /**
     * @return by how many half-steps the pitch is altered up or down. 
     */
    public int getAlter() {
        return this.alter;
    }
    
    /**
     * @return octave number of this pitch.
     */
    public int getOctave() {
        return this.octave;
    }
    
    /**
     * @return this Pitch as an integer. C4 (middle C) is 48.
     */
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
    
    // TODO: Use class PitchClass for handling these two.
    /**
     * @return the <a href="http://en.wikipedia.org/wiki/Pitch_class">pitch class integer</a> of this Pitch.
     */
    public int getPitchClass() {
        return this.toInt() % 12;
    }
    
    /**
     * @return the name of the pitch class as string.
     */
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
    
    /**
     * Test enharmonic equality.
     * Compare this to other for 
     * <a href="http://en.wikipedia.org/wiki/Enharmonic">enharmonic</a> equality.
     * @param other Pitch against which this is compared.
     * @return true if this is enharmonically equal to other, otherwise false.
     */
    public boolean equalsEnharmonically(Pitch other) {
        return this.toInt() == other.toInt();
    }
    
    /**
     * String representation of <code>Pitch</code>.
     * <code>Pitch</code> objects are represented as strings of form <code>bao</code>, 
     * where <code>b</code> is the base letter in the pitch name, <code>a</code> is the 
     * alteration (sharps # or flats b), and <code>o</code> is the 
     * octave number. For example middle C-sharp is represented as the string <code>C#4</code>.
     * @return the string representation of this Pitch.
     */
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
