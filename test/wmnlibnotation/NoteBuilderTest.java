/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import wmnlibnotation.builders.NoteBuilder;
import wmnlibnotation.noteobjects.MultiNoteArticulation;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.iterators.ScorePosition;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.Articulation;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class NoteBuilderTest {
    
    public NoteBuilderTest() {
    }
    
    @Test
    public void testBuildingBasicNote() {
        NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        Note note = builder.build();
        assertFalse(note.hasArticulations());
        assertFalse(note.hasMultiNoteArticulations());
        assertFalse(note.isTied());
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), note);
    }
    
    @Test
    public void testBuildingNoteWithAllAttributes() {
        NoteBuilder builder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        builder.addArticulation(Articulation.STACCATO);
        builder.addMultiNoteArticulation(new MultiNoteArticulation(MultiNoteArticulation.Type.SLUR));
        
        Note tiedNote = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        ScorePosition position = new ScorePosition(1,1,1,1);
        
        builder.setTiedTo(tiedNote);
        builder.setIsTiedFromPrevious(false);
        
        Note expected = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), 
                                        Durations.QUARTER, 
                                        EnumSet.of(Articulation.STACCATO));
        
        Note note = builder.build();
        assertEquals(expected, note);
        assertTrue(note.isTied());
        assertTrue(note.isTiedToFollowing());
        assertFalse(note.isTiedFromPrevious());
        assertTrue(note.hasMultiNoteArticulations());
    }
    
    @Test
    public void testBuildingTiedNotes() {
        NoteBuilder first = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        NoteBuilder second = new NoteBuilder(Pitch.getPitch(Pitch.Base.D, 0, 4), Durations.QUARTER);
        NoteBuilder third = new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.QUARTER);

        
        List<NoteBuilder> builders = new ArrayList<>();
        builders.add(first);
        builders.add(second);
        builders.add(third);
        
        List<Note> tiedNotes = NoteBuilder.buildTiedNotes(builders);
        
        Note firstNote = tiedNotes.get(0);
        assertEquals(Pitch.getPitch(Pitch.Base.C, 0, 4), firstNote.getPitch());
        assertTrue(firstNote.isTiedToFollowing());
        assertFalse(firstNote.isTiedFromPrevious());
        assertEquals(Pitch.getPitch(Pitch.Base.D, 0, 4), firstNote.getFollowingTiedNote().getPitch());
        
        Note secondNote = tiedNotes.get(1);
        assertEquals(Pitch.getPitch(Pitch.Base.D, 0, 4), secondNote.getPitch());
        assertTrue(secondNote.isTiedToFollowing());
        assertTrue(secondNote.isTiedFromPrevious());
        assertEquals(Pitch.getPitch(Pitch.Base.E, 0, 4), secondNote.getFollowingTiedNote().getPitch());
        
        Note thirdNote = tiedNotes.get(2);
        assertEquals(Pitch.getPitch(Pitch.Base.E, 0, 4), thirdNote.getPitch());
        assertFalse(thirdNote.isTiedToFollowing());
        assertTrue(thirdNote.isTiedFromPrevious());
    }
    
}
