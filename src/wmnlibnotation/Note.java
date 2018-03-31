
package wmnlibnotation;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Set;

/**
 * Class that defines a note.
 * This class is immutable.
 * @author otsobjorklund
 */
public class Note implements Durational {
    
    private final Pitch pitch;
    private final Duration duration;
    private final Set<Articulation> articulations; 
    
    /**
     * Get a <code>Note</code> object with specified parameters.
     * @param pitchName the letter in the pitch name.
     * @param alter how many half-steps the pitch is altered up (positive) or down (negative).
     * @param octave octave number of the pitch.
     * @param duration the duration of the note. Must not be null.
     * @return a Note object with the given parameters.
     */
    public static Note getNote(Pitch.Base pitchName, int alter, int octave, Duration duration) {
        return getNote(Pitch.getPitch(pitchName, alter, octave), duration, null);
    }
    
    /**
     * Get a <code>Note</code> object with specified parameters.
     * @param pitch the pitch of the note. Must not be null.
     * @param duration the duration of the note. Must not be null.
     * @return a Note object with the given parameters.
     */
    public static Note getNote(Pitch pitch, Duration duration) {
        return getNote(pitch, duration, null);
    }
    
    /**
     * Get a <code>Note</code> object with specified parameters.
     * @param pitch the pitch of the note. Must not be null.
     * @param duration the duration of the note. Must not be null.
     * @param articulations a set of Articulations associated with the note.
     * @return a Note object with the given parameters.
     */
    public static Note getNote(Pitch pitch, Duration duration, Set<Articulation> articulations) {
        return new Note(pitch, duration, articulations);
    }
    
    /**
     * Private constructor. Use the static <code>getNote</code> methods to get an instance.
     * @throws NullPointerException if duration or pitch is null.
     * @param pitch the pitch of the note.
     * @param duration the duration of the note.
     * @param articulations a set of Articulations associated with the note.
     */
    private Note(Pitch pitch, Duration duration, Set<Articulation> articulations) {
        this.pitch = pitch;
        this.duration = duration;
        if(articulations != null && !articulations.isEmpty())
            this.articulations = EnumSet.copyOf(articulations);
        else
            this.articulations = Collections.EMPTY_SET;
        
        if(this.pitch == null)
            throw new NullPointerException("Pitch was null. Note must have a pitch.");
        
        if(this.duration == null)
            throw new NullPointerException("Duration was null. Note must have a duration.");
    }
    
    /**
     * Get the <code>Pitch</code> of this <code>Note</code>.
     * @return the Pitch of this Note.
     */
    public Pitch getPitch() {
        return this.pitch;
    }
 
    /**
     * Get the <code>Duration</code> of this <code>Note</code>.
     * @return the Duration of this Note.
     */
    @Override
    public Duration getDuration() {
        return this.duration;
    }
    
    /**
     * @return false.
     */
    @Override
    public boolean isRest() {
        return false;
    }
    
    /**
     * Get the articulations of this <code>Note</code> as a <code>Set</code>.
     * @return the articulations of this <code>Note</code>.
     */
    public Set<Articulation> getArticulations() {
        return Collections.unmodifiableSet(this.articulations);
    }
    
    /**
     * Check if this <code>Note</code> has any articulations.
     * @return true if this Note has any articulations, otherwise false.
     */
    public boolean hasArticulations() {
        return !this.articulations.isEmpty();
    }
    
    /**
     * Check if this <code>Note</code> has a particular articulation.
     * @param articulation the articulation whose presence is checked.
     * @return true if this Note has the given articulation, otherwise false.
     */
    public boolean hasArticulation(Articulation articulation) {
        return this.articulations.contains(articulation);
    }
    
    /**
     * Returns a string representation of this <code>Note</code>.
     * The string representation of a note is of form <code>PDA</code>, 
     * where <code>P</code> is the string representation of this note's pitch,
     * <code>D</code> is the string representation of this note's duration, 
     * and <code>A</code> is the set of articulations in parentheses and separated by spaces.
     * For example, for a Note with articulations STACCATO and ACCENT <code>A</code> 
     * will be <code>(ACCENT STACCATO)</code>. 
     * The order of articulations in the string representation is not specified.
     * @return string representation of this Note.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(this.pitch.toString()).append(this.duration.toString());
        
        if(!this.articulations.isEmpty()) {
            
            strBuilder.append("(");
            for(Articulation articulation : this.articulations) {
                strBuilder.append(articulation.toString()).append(" ");
            }
            
            strBuilder.replace(strBuilder.length() - 1, strBuilder.length(), "");
            strBuilder.append(")");
        }
        return strBuilder.toString();
    }
   
    /**
     * Check this <code>Note</code> for equality against <code>Object o</code>.
     * Notes are equal if they have the same Pitch, Duration, and set of Articulations.
     * @param o the Object against which this Note is compared for equality.
     * @return true if Object o is of class Note and has the same Pitch, Duration, 
     * and Articulations as this Node. false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        
        if(!(o instanceof Note))
            return false;
       
        Note other = (Note) o;
        
        if(!this.pitch.equals(other.pitch))
            return false;
        
        if(!this.duration.equals(other.duration))
            return false;

        if(!this.articulations.equals(other.articulations))
            return false;
        
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.pitch);
        hash = 79 * hash + Objects.hashCode(this.duration);
        hash = 79 * hash + Objects.hashCode(this.articulations);
        return hash;
    }

}
