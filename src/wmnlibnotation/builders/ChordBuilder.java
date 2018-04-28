/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import java.util.ArrayList;
import java.util.List;
import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Note;

/**
 *
 * @author Otso Björklund
 */
public class ChordBuilder implements DurationalBuilder {
    
    private final List<NoteBuilder> noteBuilders;
    private Duration duration;
    
    public ChordBuilder(Duration duration) {
        this.duration = duration;
        this.noteBuilders = new ArrayList<>();
    }
    
    public ChordBuilder(List<NoteBuilder> noteBuilders) {
        this.duration = noteBuilders.get(0).getDuration();
        this.noteBuilders = new ArrayList<>(noteBuilders);
    }
    
    public ChordBuilder add(NoteBuilder noteBuilder) {
        this.noteBuilders.add(noteBuilder);
        return this;
    }
    
    @Override
    public Duration getDuration() {
        return this.duration;
    }

    @Override
    public Chord build() {
        List<Note> notes = new ArrayList<>();
        this.noteBuilders.forEach((builder) -> notes.add(builder.build()));
        return Chord.getChord(notes);
    }
    
}
