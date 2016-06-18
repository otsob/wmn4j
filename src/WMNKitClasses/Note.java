
package WMNKitClasses;
import java.util.Objects;

/**
 * Class that defines a note
 * @author otsobjorklund
 */
public class Note implements NotationElement {
    
    public enum Pitch { C, D, E, F, G, A, B }
    
    private final Pitch pitch;
    private final int alter;
    private final int octave;
    private final Duration duration;
    
    public static Note getNote(Pitch pitch, int alter, int octave, Duration duration) {
        return new Note(pitch, alter, octave, duration);
    }
    
    private Note(Pitch pitch, int alter, int octave, Duration duration) {
        // Todo: Limit altering, duration, and octave to reasonable limits.
        this.pitch = pitch;
        this.alter = alter;
        this.octave = octave;
        this.duration = duration;
    }
    
    public Pitch getPitch() {
        return this.pitch;
    }
    
    public int getAlter() {
        return this.alter;
    }
    
    public int getOctave() {
        return this.octave;
    }
    
    public Duration getDuration() {
        return this.duration;
    }
    
    public int toInt() {
        int pitchAsInt;
        
        switch(this.pitch) {
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
        
        pitchAsInt += this.alter;
        pitchAsInt += (this.octave * 12);
        return pitchAsInt;
    }
    
    public int getPitchClass() {
        return this.toInt() % 12;
    }
    
    public boolean hasSamePitchAs(Note other) {
    
        if(other.pitch != this.pitch)
            return false;
        
        if(other.octave != this.octave)
            return false;
        
        if(other.alter != this.alter)
            return false;
        
        return true;
    }
    
    public String getPitchString() {
        String pitchAsString = this.pitch.toString();
        
        if(alter >= 0 ) {
            for(int i = 0; i < alter; ++i)
                pitchAsString += "#";
        }
        else {
            for(int i = 0; i > alter; --i)
                pitchAsString += "b";
        }
        pitchAsString += octave;
        
        return pitchAsString;
    }
    
    @Override
    public String toString() {
        return getPitchString() + this.duration.toString();
    }
   
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Note))
            return false;
        
        if(o == this)
            return true;
        
        Note other = (Note) o;
        
        if(!this.hasSamePitchAs(other))
            return false;
        
        if(!this.duration.equals(other.duration))
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + Objects.hashCode(this.pitch);
        hash = 43 * hash + this.alter;
        hash = 43 * hash + this.octave;
        hash = 43 * hash + Objects.hashCode(this.duration);
        return hash;
    }
}
