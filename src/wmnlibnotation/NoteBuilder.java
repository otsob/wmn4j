/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

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
    
    public NoteBuilder() {
        this.articulations = EnumSet.noneOf(Articulation.class);
        this.multiNoteArticulations = new ArrayList<>();
    }

    public Pitch getPitch() {
        return pitch;
    }

    public void setPitch(Pitch pitch) {
        this.pitch = pitch;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public Set<Articulation> getArticulations() {
        return articulations;
    }

    public void setArticulations(Set<Articulation> articulations) {
        this.articulations = articulations;
    }

    public List<MultiNoteArticulation> getMultiNoteArticulations() {
        return multiNoteArticulations;
    }

    public void setMultiNoteArticulations(List<MultiNoteArticulation> multiNoteArticulations) {
        this.multiNoteArticulations = multiNoteArticulations;
    }
    
    public Note build() {
        return Note.getNote(this.pitch, this.duration, this.articulations, this.multiNoteArticulations);
    }
    
    
}
