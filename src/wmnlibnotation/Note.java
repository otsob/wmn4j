
package wmnlibnotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class that defines a note
 * @author otsobjorklund
 */
public class Note implements NotationElement {
    
    private final Pitch pitch;
    private final Duration duration;
    private final Set<Articulation> articulations; 
    
    public static Note getNote(Pitch.Base pitchName, int alter, int octave, Duration duration) {
        return getNote(Pitch.getPitch(pitchName, alter, octave), duration, null);
    }
    
    public static Note getNote(Pitch pitch, Duration duration) {
        return getNote(pitch, duration, null);
    }
    
    public static Note getNote(Pitch pitch, Duration duration, Set<Articulation> articulations) {
        return new Note(pitch, duration, articulations);
    }
    
    private Note(Pitch pitch, Duration duration, Set<Articulation> articulations ) {
        
        this.pitch = pitch;
        this.duration = duration;
        if(articulations != null && !articulations.isEmpty())
            this.articulations = new HashSet(articulations);
        else
            this.articulations = Collections.EMPTY_SET;
        
        if(this.pitch == null)
            throw new NullPointerException("Pitch was null. Note must have a pitch.");
        
        if(this.duration == null)
            throw new NullPointerException("Duration was null. Note must have a duration.");
    }
    
    public Pitch getPitch() {
        return this.pitch;
    }
 
    @Override
    public Duration getDuration() {
        return this.duration;
    }
    
    @Override
    public boolean isRest() {
        return false;
    }
    
    public List<Articulation> getArticulations() {
        return new ArrayList(this.articulations);
    }
    
    public boolean hasArticulations() {
        return !this.articulations.isEmpty();
    }
    
    public boolean hasArticulation(Articulation articulation) {
        return this.articulations.contains(articulation);
    }
    
    public boolean equalsInPitch(Note other) {
        return this.pitch.equals(other.pitch);
    }
    
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
