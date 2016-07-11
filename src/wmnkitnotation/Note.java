
package wmnkitnotation;
import java.util.ArrayList;
import java.util.Arrays;
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
        return new Note(Pitch.getPitch(pitchName, alter, octave), duration, null);
    }
    
    public static Note getNote(Pitch pitch, Duration duration, Set<Articulation> articulations) {
        return new Note(pitch, duration, articulations);
    }
    
    public static Note getNote(Pitch pitch, Duration duration) {
        return new Note(pitch, duration, null);
    }
    
    private Note(Pitch pitch, Duration duration, Set<Articulation> articulations ) {
        if(pitch == null)
            throw new IllegalArgumentException("Pitch was null. Note must have a duration");
        
        if(duration == null)
            throw new IllegalArgumentException("Duration was null. Note must have a duration");
        
        this.pitch = pitch;
        this.duration = duration;
        if(articulations != null && !articulations.isEmpty())
            this.articulations = new HashSet(articulations);
        else
            this.articulations = Collections.EMPTY_SET;
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
        ArrayList<Articulation> list = new ArrayList();
        
        if(this.articulations != null)
            list.addAll(this.articulations);
        
        return list;
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
        String noteAsString = this.pitch.toString() + this.duration.toString();
        
        if(!this.articulations.isEmpty()) {
            noteAsString += "(";
            for(Articulation articulation : this.articulations) 
                noteAsString += articulation.toString() + " ";
            
            noteAsString = noteAsString.trim();
            noteAsString += ")";
        }
        return noteAsString;
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
