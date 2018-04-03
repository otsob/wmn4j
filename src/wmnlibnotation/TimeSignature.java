/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import java.util.Objects;

/**
 * Class for time signatures.
 * Time signatures consist of the number of beats and the duration of each beat.
 * For example, for the time signature 4/4 the number of beats is 4 and the duration of beat is 
 * <code>Durations.QUARTER</code>.
 * @author Otso Björklund
 */
public class TimeSignature {
   
    private final int beats;
    private final Duration beatDuration;
    
    /**
     * Get an instance of <code>TimeSignature</code>.
     * @param numerator the top number of the time signature.
     * @param denominator the bottom number of the time signature.
     * @return a TimeSignature with the specified properties.
     */
    public static TimeSignature getTimeSignature(int numerator, int denominator) {
        return getTimeSignature(numerator, Duration.getDuration(1, denominator));
    }
    
    /**
     * Get an instance of <code>TimeSignature</code>.
     * @throws IllegalArgumentException if beats is less than 1.
     * @throws NullPointerException if beatDur is null. 
     * @param beats number of beats in measure.
     * @param beatDur the Duration of the beats.
     * @return a TimeSignature with the specified properties.
     */
    public static TimeSignature getTimeSignature(int beats, Duration beatDur) {
        if(beats < 1)
            throw new IllegalArgumentException("beats must be at least 1.");
        
        if(beatDur == null)
            throw new NullPointerException("beatDur was null.");
        
        return new TimeSignature(beats, beatDur);
    }
    
    private TimeSignature(int beats, Duration beatDuration) {
        this.beats = beats;
        this.beatDuration = beatDuration;
    }
    
    /**
     * @return number of beats.
     */
    public int getNumBeats() {
        return this.beats;
    }
    
    /**
     * @return the Duration of beats.
     */
    public Duration getBeatDuration() {
        return this.beatDuration;
    }
    
    /**
     * Get the total duration of a measure with this time signature.
     * For example for time signature 4/4 duration is one whole note.
     * @return sum of beat durations in a measure.
     */
    public Duration getTotalDuration() {
        return this.beatDuration.multiplyBy(this.beats);
    }
    
    /**
     * Compare for equality.
     * @param o Object against which this is compared for equality.
     * @return true if o is an instance of TimeSignature and o has the same 
     * number of beats and same beat duration as this. false otherwise.
     */
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
    
    /**
     * Get String representation of <code>TimeSignature</code>.
     * String representation is of form <code>Time(numBeats/beatDuration)</code>.
     * @return string representation of TimeSignature.
     */
    @Override
    public String toString() {
        return "Time(" + this.beats + "/" + this.beatDuration.getDenominator() + ")";
    }
}
