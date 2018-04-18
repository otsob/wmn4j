/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibanalysis.harmony;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;
import wmnlibnotation.Chord;
import wmnlibnotation.Durational;
import wmnlibnotation.Note;
import wmnlibnotation.PitchClass;

/**
 * Pitch class profile in twelve-tone equal-temperament (TET).
 * Contains a bin for each pitch class, and each bin contains a 
 * non-negative value associated with the pitch class.
 * 
 * By default the value of a note is 1.0 meaning that the pitch class profile 
 * is based on the counts of pitch classes. When creating the profile a 
 * <code>PCProfile.Weighter</code> can be given to calculate the value of a note differently.
 * @author Otso Björklund
 */
public class PCProfile {
    
    /**
     * Interface for weighing the pitch class of a <code>Note</code>.
     */
    public interface Weighter {
        /**
         * Calculates the coefficient for the pitch class of the note.
         * Used for adding notes to <code>PCProfile</code> objects.
         * @param note note for which weight is calculated.
         * @return weight of the pitch class of the note.
         */
        double weight(Note note);
    }
    
    private final PCProfile.Weighter pw;
    private final Map<PitchClass, Double> profile;
    
    /**
     * Using this constructor the profile is based on the counts of pitch classes.
     */
    public PCProfile() {
        this.pw = note -> 1.0;
        this.profile = new TreeMap<>();
        this.initialize();
    }
    
    /**
     * Creates a <code>PCProfile</code> where the weight of a note's pitch class
     * is weighted using the duration of the note. 
     * @return a PCProfile where pitch classes are weighted using note durations.
     */
    public static PCProfile getDurationWeightedProfile() {
        return new PCProfile(note -> note.getDuration().toDouble());
    }
    
    /**
     * Constructor that allows specifying how the value of a note is computed 
     * when it is added to the profile.
     * @param weighter used for computing the value of a note when 
     * the note is added to this profile.
     */
    public PCProfile(PCProfile.Weighter weighter) {
        this.pw = weighter;
        this.profile = new TreeMap<>();
        this.initialize();
    }
    
    private void initialize() {
        for(PitchClass pc : PitchClass.values()) {
            this.setValue(pc, 0.0);
        }
    }
  
    /**
     * Set a value for the <code>PitchClass</code>.
     * @param pc the pitch class for the bin whose value is set.
     * @param value a non-negative value.
     */
    public void setValue(PitchClass pc, double value) {
        // TODO: Reconsider if this has to be only non-negative
        if(value < 0.0)
            throw new IllegalArgumentException("value must be at least 0.0");
        
        this.profile.put(pc, value);
    }
        
    /**
     * Add the pitch class/classes of durational to this <code>PCProfile</code>.
     * If durational is a <code>Note</code> add its pitch class bin.
     * If durational is a <code>Chord</code> add pitch class bins of its notes.
     * If durational is a <code>Rest</code> nothing will be added to the profile.
     * @param durational durational object to be added to this profile.
     */
    public void add(Durational durational) {
        if(durational instanceof Note)
            this.add((Note) durational);
        if(durational instanceof Chord)
            this.add((Chord) durational);
    }
    
    /**
     * Add the notes in in the chord to this <code>PCProfile</code>.
     * @param chord chord whose pitch classes are added to profile.
     */
    public void add(Chord chord) {
        for(Note note : chord) 
            this.add(note);
    }
    
    /**
     * Add to the pitch class bin of the pitch class of note.
     * Uses the <code>PCProfileWeighter</code> of this <code>PCProfile</code> 
     * to calculate the value that is added to the pitch class bin.
     * @param note the value is added to the bin corresponding to 
     * the pitch class of this note.
     */
    public void add(Note note) {
        PitchClass pc = note.getPitch().getPitchClass();
        double value = this.getValue(note.getPitch().getPitchClass());
        value += this.pw.weight(note);
        this.setValue(pc, value);
    }
  
    /**
     * Returns a normalized copy of this pitch profile.
     * Normalizes profile so that largest value is 1.0
     * @return normalized pitch class profile.
     */
    public PCProfile normalize() {
        PCProfile normalized = new PCProfile();
        double largest = 0.0;
        
        for(PitchClass pc : this.profile.keySet()) {
            double value = this.profile.get(pc);
            if(value > largest) {
                largest = value;
            }
        }
        
        if(!new Double(0.0).equals(largest)) {
            for(PitchClass pc : this.profile.keySet()) {
                double value = this.profile.get(pc);
                normalized.setValue(pc, value / largest);
            }
        }
        
        return normalized;
    }
    
    /**
     * @param pc pitch class for which to get the value.
     * @return the value associated with <code>PitchClass</code> pc.
     */
    public double getValue(PitchClass pc) {
        return this.profile.get(pc);
    }
    
    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.000"); 
        StringBuilder strBuilder = new StringBuilder();
        
        for(PitchClass pc : this.profile.keySet())
            strBuilder.append(pc).append(": ").append(df.format(this.profile.get(pc))).append(", ");
        
        strBuilder.replace(strBuilder.length() - 2, strBuilder.length(), "");
        return strBuilder.toString();
    }
    
    /**
     * Computes the correlation between the two profiles.
     * Uses <a href="http://en.wikipedia.org/wiki/Pearson_correlation_coefficient">
     * Pearson correlation coefficient</a> formula for a sample.
     * Normalizing the profiles does not affect correlation.
     * @param a Profile for computing correlation.
     * @param b Profile for computing correlation.
     * @return correlation between profiles a and b.
     */
    public static double correlation(PCProfile a, PCProfile b) {
        
        double averageOfA = 0.0;
        double averageOfB = 0.0;
        
        for(PitchClass pc : PitchClass.values()) {
            averageOfA += a.getValue(pc);
            averageOfB += b.getValue(pc);
        }
        
        averageOfA /= PitchClass.values().length;
        averageOfB /= PitchClass.values().length;
        
        double numerator = 0.0;
        double denomA = 0.0;
        double denomB = 0.0;
        
        for(PitchClass pc : PitchClass.values()) {
            double diffA = a.getValue(pc) - averageOfA;
            double diffB = b.getValue(pc) - averageOfB;
        
            numerator += diffA * diffB;
            denomA += Math.pow(diffA, 2.0);
            denomB += Math.pow(diffB, 2.0);
        }
        
        double denominator = Math.sqrt(denomA * denomB);
        return numerator / denominator;
    }
    
    /**
     * Computes the Euclidean distance between profiles.
     * Computes the <a href="https://en.wikipedia.org/wiki/Euclidean_distance">
     * Euclidean distance</a> between the profiles 
     * which can be considered 12-dimensional vectors.
     * The method {@link #normalize() normalize()} can be used for getting the 
     * profiles to a similar range.
     * @param a Profile for computing Euclidean distance.
     * @param b Profile for computing Euclidean distance.
     * @return Euclidean distance between a and b.
     */
    public static double euclidean(PCProfile a, PCProfile b) {
        double sumOfSquaredDiffs = 0.0;
        
        for(PitchClass pc : PitchClass.values()) {
            sumOfSquaredDiffs += Math.pow(a.getValue(pc) - b.getValue(pc), 2.0);
        }
        
        return Math.sqrt(sumOfSquaredDiffs);
    }
}
