
package wmnkitnotation;

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
}
