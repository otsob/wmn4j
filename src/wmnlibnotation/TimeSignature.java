
package wmnlibnotation;

import java.util.Objects;

/**
 *
 * @author Otso Björklund
 */
public class TimeSignature {
   
    private final int beats;
    private final Duration beatDuration;
    
    public static TimeSignature getTimeSignature(int beats, int beatDuration) {
        return new TimeSignature(beats, Duration.getDuration(1, beatDuration));
    }
    
    private TimeSignature(int beats, Duration beatDuration) {
        this.beats = beats;
        this.beatDuration = beatDuration;
    }
    
    public int getNoOfBeats() {
        return this.beats;
    }
    
    public Duration getBeatDuration() {
        return this.beatDuration;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TimeSignature))
            return false;
        
        TimeSignature other = (TimeSignature) o;
        
        return this.beatDuration.equals(other.beatDuration) && this.beats == other.beats;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this.beats;
        hash = 29 * hash + Objects.hashCode(this.beatDuration);
        return hash;
    }
    
    @Override
    public String toString() {
        return "Time(" + this.beats + "/" + this.beatDuration.getDenominator() + ")";
    }
}
