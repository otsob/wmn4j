/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.MultiNoteArticulation;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Articulation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * Class for building <code>Note</code> objects.
 * @author Otso Björklund
 */
public class NoteBuilder {
    
    private Pitch pitch;
    private Duration duration;
    private Set<Articulation> articulations;
    private List<MultiNoteArticulation> multiNoteArticulations;
    private Note tiedTo;
    private boolean isTiedFromPrevious;
    
    /**
     * @param pitch The pitch set in this builder.
     * @param duration The duration set in this builder.
     */
    public NoteBuilder(Pitch pitch, Duration duration) {
        this.pitch = pitch;
        this.duration = duration;
        this.articulations = EnumSet.noneOf(Articulation.class);
        this.multiNoteArticulations = new ArrayList<>();
        this.isTiedFromPrevious = false;
    }

    /**
     * @return The pitch currently set in this builder.
     */
    public Pitch getPitch() {
        return pitch;
    }

    /**
     * @param pitch The pitch to be set in this builder.
     */
    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    /**
     * @return The duration currently set in this builder.
     */
    public Duration getDuration() {
        return duration;
    }

    /**
     * @param duration The duration to be set in this builder.
     */
    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Set<Articulation> getArticulations() {
        return articulations;
    }

    public void setArticulations(Set<Articulation> articulations) {
        this.articulations = articulations;
    }
    
    public void addArticulation(Articulation articulation) {
        this.articulations.add(articulation);
    }

    public List<MultiNoteArticulation> getMultiNoteArticulations() {
        return multiNoteArticulations;
    }
    
    public void addMultiNoteArticulation(MultiNoteArticulation articulation) {
        this.multiNoteArticulations.add(articulation);
    }

    public void setMultiNoteArticulations(List<MultiNoteArticulation> multiNoteArticulations) {
        this.multiNoteArticulations = multiNoteArticulations;
    }

    public Note getTiedTo() {
        return tiedTo;
    }

    public void setTiedTo(Note tiedTo) {
        this.tiedTo = tiedTo;
    }

    public boolean isIsTiedFromPrevious() {
        return isTiedFromPrevious;
    }

    public void setIsTiedFromPrevious(boolean isTiedFromPrevious) {
        this.isTiedFromPrevious = isTiedFromPrevious;
    }

    /**
     * @return <code>Note</code> instance with the values set in this builder.
     */
    public Note build() {
        return Note.getNote(this.pitch, this.duration, this.articulations, this.multiNoteArticulations, this.tiedTo, this.isTiedFromPrevious);
    }
    
    /**
     * Get a sequence of <code>Note</code> objects that are tied together 
     * from builders.
     * @param noteBuilders
     * @return List of notes, such that the first is tied to the second, second
     * to third and so on.
     */
    public static List<Note> buildTiedNotes(List<NoteBuilder> noteBuilders) {
        List<Note> notes = new ArrayList<>();
        
        Note tieTargetNote = null;
        for(int i = noteBuilders.size() - 1; i >= 0; --i) {
            NoteBuilder builder = noteBuilders.get(i);
            
            if(i != 0)
                builder.setIsTiedFromPrevious(true);
            
            builder.setTiedTo(tieTargetNote);
            Note note = builder.build();
            tieTargetNote = note;
            notes.add(0, note);
        }
        
        return notes;
    }
}
