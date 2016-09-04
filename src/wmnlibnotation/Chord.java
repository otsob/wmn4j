
package wmnlibnotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Otso Björklund
 */
public class Chord implements NotationElement, Iterable<Note> {
    private final List<Note> notes;
 
    public static Chord getChord(Note... n) {   
        return new Chord(Arrays.asList(n));
    }
    
    public static Chord getChord(List<Note> notes) {
        return new Chord(notes);
    }
    
    private Chord(List<Note> notes) {
        
        if(notes == null)
            throw new NullPointerException();
        
        this.notes = new ArrayList(notes);
        final Duration d = this.notes.get(0).getDuration();
                
        for (Note n : notes) {
            if(!d.equals(n.getDuration()))
                throw new IllegalArgumentException("All notes in chord must be of same duration");
        }
        
        if (this.notes.isEmpty())
            throw new IllegalArgumentException("Chord cannot be constructed with an empty List of notes");
        
        Collections.sort(this.notes, NotePitchComparator.INSTANCE);
    }
    
    @Override
    public Duration getDuration() {
        return this.notes.get(0).getDuration();
    }

    public Note getNote(int fromLowest) {
        return this.notes.get(fromLowest);
    }
    
    public Note getLowestNote() {
        return this.notes.get(0);
    }
    
    public Note getHighestNote() {
        return this.notes.get(this.notes.size() - 1);
    }
    
    public int getNoteCount() {
        return this.notes.size();
    }
    
    public Chord addNote(Note note) {
        ArrayList<Note> noteList = new ArrayList(this.notes);
        noteList.add(note);
        return Chord.getChord(noteList);
    }
    
    public Chord removeNote(Note note) {
        ArrayList<Note> noteList = new ArrayList(this.notes);
        noteList.remove(note);
        return Chord.getChord(noteList);
    }
    
    public boolean contains(Pitch pitch) {
        for(Note note : this.notes) {
            if(note.getPitch().equals(pitch))
                return true;
        }
        
        return false;
    }
    
    public boolean contains(Note note) {
        return this.notes.contains(note);
    }
    
    public Chord removePitch(Pitch pitch) {
        if(this.contains(pitch)) {
            List<Note> newNotes = new ArrayList(this.notes);
            newNotes.remove(Note.getNote(pitch, this.getDuration()));
            return Chord.getChord(newNotes);
        }
        
        return this;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Chord))
            return false;
    
        Chord other = (Chord) o;
        
        if(!this.getDuration().equals(other.getDuration()))
            return false;
        
        if(this.getNoteCount() != other.getNoteCount())
            return false;
        
        if(!this.notes.equals(other.notes))
            return false;
            
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.notes);
        return hash;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("[");
        
        for(int i = 0; i < this.notes.size(); ++i) {
            strBuilder.append(this.notes.get(i).toString());
            
            if(i != this.notes.size() - 1)
                strBuilder.append(",");
        }
        strBuilder.append("]");
        return strBuilder.toString();
    }
    
    @Override
    public boolean isRest() {
        return false;
    }

    @Override
    public Iterator<Note> iterator() {
        return this.notes.iterator();
    }
}