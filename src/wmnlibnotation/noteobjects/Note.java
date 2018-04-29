/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class that defines a note.
 * This class is immutable. The <code>NoteBuilder</code> class can be used
 * for creating <code>Note</code> objects.
 * Notes have pitch, duration, articulations, and can be tied.
 * A sequence of tied notes functions like a singly linked list where 
 * a previous note keeps track of the following tied note.
 * @author otsobjorklund
 */
public class Note implements Durational {
    
    private final Pitch pitch;
    private final Duration duration;
    private final Set<Articulation> articulations;
    private final List<MultiNoteArticulation> multiNoteArticulations;
    
    private final Note tiedTo;
    private final boolean isTiedFrom;
    
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
        return new Note(pitch, duration, articulations, null, null, false);
    }
    
    /**
     * Get a <code>Note</code> object with specified parameters.
     * @param pitch the pitch of the note. Must not be null.
     * @param duration the duration of the note. Must not be null.
     * @param articulations a set of Articulations associated with the note.
     * @param multiNoteArticulations list of the MultiNoteArticulations for the note.
     * @return a Note object with the given parameters.
     */
    public static Note getNote(Pitch pitch, 
                                Duration duration, 
                                Set<Articulation> articulations, 
                                List<MultiNoteArticulation> multiNoteArticulations) {
        return new Note(pitch, duration, articulations, multiNoteArticulations, null, false);
    }
    
    /**
     * Get a <code>Note</code> object with specified parameters.
     * @param pitch the pitch of the note. Must not be null.
     * @param duration the duration of the note. Must not be null.
     * @param articulations a set of Articulations associated with the note.
     * @param multiNoteArticulations list of the MultiNoteArticulations for the note.
     * @param tiedTo A tie from a previous note that ends in this.
     * @param isTiedFromPrevious A tie that originates from this note.
     * @return a Note object with the given parameters.
     */
    public static Note getNote(Pitch pitch, 
                                Duration duration, 
                                Set<Articulation> articulations, 
                                List<MultiNoteArticulation> multiNoteArticulations, 
                                Note tiedTo,
                                boolean isTiedFromPrevious) {
        return new Note(pitch, duration, articulations, multiNoteArticulations, tiedTo, isTiedFromPrevious);
    }
    
    /**
     * Private constructor. Use the static <code>getNote</code> methods
     * or <code>NoteBuilder</code> to get an instance.
     * @throws NullPointerException if duration or pitch is null.
     * @param pitch the pitch of the note.
     * @param duration the duration of the note.
     * @param articulations a set of Articulations associated with the note.
     */
    private Note(Pitch pitch, 
            Duration duration, 
            Set<Articulation> articulations, 
            List<MultiNoteArticulation> multiNoteArticulations,
            Note tiedTo,
            boolean isTiedFromPrevious) {
        
        this.pitch = pitch;
        this.duration = duration;
        if(articulations != null && !articulations.isEmpty())
            this.articulations = Collections.unmodifiableSet(EnumSet.copyOf(articulations));
        else
            this.articulations = Collections.emptySet();
        
        if(multiNoteArticulations != null && !multiNoteArticulations.isEmpty())
            this.multiNoteArticulations = Collections.unmodifiableList(new ArrayList<>(multiNoteArticulations));
        else
            this.multiNoteArticulations = Collections.emptyList();
        
        if(this.pitch == null)
            throw new NullPointerException("Pitch was null. Note must have a pitch.");
        
        if(this.duration == null)
            throw new NullPointerException("Duration was null. Note must have a duration.");
        
        this.tiedTo = tiedTo;
        this.isTiedFrom = isTiedFromPrevious;
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
        return this.articulations;
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
     * @return The <code>MultiNoteArticulation</code> objects of this <code>Note</code>.
     */
    public List<MultiNoteArticulation> getMultiNoteArticulations() {
        return this.multiNoteArticulations;
    }
    
    /**
     * @return true if this <code>Note</code> has any <code>MultiNoteArticulations</code>, 
     * false otherwise.
     */
    public boolean hasMultiNoteArticulations() {
        return !this.multiNoteArticulations.isEmpty();
    }
   
    /**
     * @return True if this note is tied to a following note. False otherwise.
     */
    public boolean isTiedToFollowing() {
        return this.tiedTo != null;
    }
    
    /**
     * @return True if this note is the end of a tie originating from a 
     * previous note. False otherwise.
     */
    public boolean isTiedFromPrevious() {
        return this.isTiedFrom;
    }
    
    /**
     * @return True if there are any ties attached to this note. False otherwise.
     */
    public boolean isTied() {
        return this.isTiedFromPrevious() || this.isTiedToFollowing();
    }
    
    /**
     * Get the total duration of tied notes starting from 
     * the onset of this note.
     * For example, if three quarter notes are tied together, 
     * then the tied duration of the first note is a dotted half note.
     * For a note that is not tied to a following note this is equal
     * to the note's duration.
     * @return The total duration of consecutive tied notes starting 
     * from the onset of this <code>Note</code>.
     */
    public Duration getTiedDuration() {
        List<Duration> tiedDurations = new ArrayList<>(); 
        
        Note currentNote = this;
        while(currentNote != null) {
            tiedDurations.add(currentNote.getDuration());
            currentNote = currentNote.getFollowingTiedNote();
        }
        
        return Duration.sumOf(tiedDurations);
    }

    /**
     * @return The following <code>Note</code> that this is tied to.
     * returns null if this note is not tied to a following note.
     */
    public Note getFollowingTiedNote() {
        return this.tiedTo;
    }
    
    /**
     * Compare notes by pitch.
     * @param other
     * @return negative integer if this note is lower than other, 
     * positive integer if this is higher than other, 0 if notes are (enharmonically) of same height.
     */
    public int compareByPitch(Note other) {
        return this.pitch.compareTo(other.getPitch());
    }
    
    /**
     * Returns a string representation of this <code>Note</code>.
     * @return string representation of this Note.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
                
        if(this.isTiedFromPrevious()) {
            strBuilder.append("->");
        }
        
        strBuilder.append(this.pitch.toString()).append(this.duration.toString());
        
        if(!this.articulations.isEmpty()) {
            
            strBuilder.append("(");
            for(Articulation articulation : this.articulations) {
                strBuilder.append(articulation.toString()).append(" ");
            }
            
            strBuilder.replace(strBuilder.length() - 1, strBuilder.length(), "");
            strBuilder.append(")");
        }

        if(this.isTiedToFollowing()) {
            strBuilder.append("->");
        }
        
        return strBuilder.toString();
    }
   
    /**
     * Compare this to other <code>Note</code> for equality of pitch and 
     * duration.
     * @param other Note with which this is compared.
     * @return True if pitch and duration are equal, false otherwise.
     */
    public boolean equalsInPitchAndDuration(Note other) {
        return this.pitch.equals(other.pitch) && this.duration.equals(other.duration);
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
        
        if(!this.equalsInPitchAndDuration(other))
            return false;
        
        if(!this.articulations.equals(other.articulations))
            return false;
        
        // TODO: Effect of ties and MultiNoteArticulations.
        
        return true;
    }

    @Override
    public int hashCode() {
        // TODO: if equals is changed update this.
        int hash = 3;
        hash = 79 * hash + Objects.hashCode(this.pitch);
        hash = 79 * hash + Objects.hashCode(this.duration);
        hash = 79 * hash + Objects.hashCode(this.articulations);
        return hash;
    }

}
