/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Class for chords.
 * This class should be used for chords where the notes are all of same length.
 * For polyphonic textures add layers to the Measure.
 * This class is immutable.
 * @author Otso Björklund
 */
public class Chord implements Durational, Iterable<Note> {
    private final List<Note> notes;
 
    /**
     * Get an instance of <code>Chord</code> with the given <code>Note</code>s.
     * @param n Notes.
     * @return a chord with the given notes.
     */
    public static Chord getChord(Note... n) {   
        return new Chord(Arrays.asList(n));
    }
    
    /**
     * Get an instance of <code>Chord</code> with the given <code>Note</code>s.
     * @param notes Notes.
     * @return a chord with the given notes.
     */
    public static Chord getChord(List<Note> notes) {
        return new Chord(notes);
    }
    
    /**
     * Private constructor.
     * Use static getters for getting an instance.
     * @throws NullPointerException if notes is null.
     * @throws IllegalArgumentException if notes is empty or all Note objects in 
     * notes are not of same duration.
     * @param notes 
     */
    private Chord(List<Note> notes) {
        
        if(notes == null)
            throw new NullPointerException();
        
        List<Note> notesCopy = new ArrayList<>(notes);
        
        if(notesCopy.isEmpty())
            throw new IllegalArgumentException("Chord cannot be constructed with an empty List of notes");
        
        notesCopy.sort(Note::compareByPitch);
        this.notes = Collections.unmodifiableList(notesCopy);
        
        final Duration d = this.notes.get(0).getDuration();
                
        for(Note n : this.notes) {
            if(!d.equals(n.getDuration()))
                throw new IllegalArgumentException("All notes in chord must be of same duration");
        }
    }
    
    @Override
    public Duration getDuration() {
        return this.notes.get(0).getDuration();
    }

    /**
     * Get the <code>Note</code> with the fromLowest lowest pitch from this <code>Chord</code>.
     * @throws IllegalArgumentException if fromLowest is smaller than 0 or 
     * at least the number of notes in this Chord.
     * @param fromLowest index of note, 0 being the lowest note in the chord.
     * @return the note from index fromLowest.
     */
    public Note getNote(int fromLowest) {
        if(fromLowest < 0 || fromLowest >= this.notes.size())
            throw new IllegalArgumentException("Tried to get note with invalid index: " 
                    + fromLowest + "from chord: " + this);
        
        return this.notes.get(fromLowest);
    }
    
    /**
     * @return the note with the lowest pitch in this Chord.
     */
    public Note getLowestNote() {
        return this.getNote(0);
    }
    
    /**
     * @return the note with the highest pitch in this Chord.
     */
    public Note getHighestNote() {
        return this.getNote(this.notes.size() - 1);
    }
    
    /**
     * @return number of notes in this Chord.
     */
    public int getNoteCount() {
        return this.notes.size();
    }
    
    /**
     * Get a Chord with the added note.
     * @param note note to be Added.
     * @return Chord with the notes of this and the added note.
     */
    public Chord addNote(Note note) {
        ArrayList<Note> noteList = new ArrayList<>(this.notes);
        noteList.add(note);
        return Chord.getChord(noteList);
    }
    
    /**
     * Get a Chord with the given note removed.
     * @param note note to be removed.
     * @return Chord without the given note.
     */
    public Chord removeNote(Note note) {
        ArrayList<Note> noteList = new ArrayList<>(this.notes);
        noteList.remove(note);
        return Chord.getChord(noteList);
    }
    
    /**
     * Check if this contains the given pitch.
     * @param pitch pitch whose presence in this Chord is checked.
     * @return true if this contains the given pitch, false otherwise.
     */
    public boolean contains(Pitch pitch) {
        for(Note note : this.notes) {
            if(note.getPitch().equals(pitch))
                return true;
        }
        
        return false;
    }
    
    /**
     * Check if this contains the given note.
     * @param note Note whose presence in this Chord is checked.
     * @return true if this contains the given note, false otherwise.
     */
    public boolean contains(Note note) {
        return this.notes.contains(note);
    }
    
    /**
     * Get a Chord with the given pitch removed.
     * @param pitch pitch of the note to be removed.
     * @return a chord without a note with the given pitch.
     */
    public Chord removePitch(Pitch pitch) {
        if(this.contains(pitch)) {
            List<Note> newNotes = new ArrayList<>(this.notes);
            newNotes.remove(Note.getNote(pitch, this.getDuration()));
            return Chord.getChord(newNotes);
        }
        
        return this;
    }
    
    /**
     * Check equality against <code>Object o</code>.
     * @param o Object against which this is compared for equality.
     * @return true if o is an instance of Chord and contains all and 
     * no other notes than the ones in this Chord.
     */
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
    
    /**
     * Get a String representation of this <code>Chord</code>.
     * The string representation of chord is of form: <code>[Note0, Note1, ...]</code>
     * where the notes are in order increasing order of pitch.
     * @return string representation of this chord.
     */
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
    
    /**
     * @return false.
     */
    @Override
    public boolean isRest() {
        return false;
    }

    /**
     * @return Iterator that can be used to iterate through the notes in this Chord. 
     * The Iterator does not support removing.
     */
    @Override
    public Iterator<Note> iterator() {
        return this.notes.iterator();
    }
}