/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import wmnlibnotation.builders.NoteBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Note class.
 * @author Otso Björklund
 */
public class NoteTest {
    
    public NoteTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testToString() {
        assertEquals( "C#4(1/4)", Note.getNote(Pitch.getPitch( Pitch.Base.C, 1, 4), Durations.QUARTER).toString());
        assertEquals( "Fb5(1/8)", Note.getNote(Pitch.getPitch( Pitch.Base.F, -1, 5), Durations.EIGHT).toString() );
        
        Pitch pitch = Pitch.getPitch(Pitch.Base.C, 0, 1);
        HashSet<Articulation> articulations = new HashSet<>();
        articulations.add(Articulation.STACCATO);
        assertEquals( "C1(1/8)(STACCATO)", Note.getNote(pitch, Durations.EIGHT, articulations).toString());
        articulations.add(Articulation.TENUTO);
        assertTrue(Note.getNote(pitch, Durations.EIGHT, articulations).toString().equals("C1(1/8)(STACCATO TENUTO)")
                    ||Note.getNote(pitch, Durations.EIGHT, articulations).toString().equals("C1(1/8)(TENUTO STACCATO)"));
    }
    
    @Test
    public void testEquals() {
        Note A1 = Note.getNote(Pitch.Base.A, 0, 1, Durations.QUARTER);
        Note A1differentDur = Note.getNote(Pitch.Base.A, 0, 1, Durations.EIGHT);
        Note A1Copy = Note.getNote(Pitch.Base.A, 0, 1, Durations.QUARTER);
        Note B1 = Note.getNote(Pitch.Base.B, 0, 1, Durations.QUARTER);
        Note Asharp1 = Note.getNote(Pitch.Base.A, 1, 1, Durations.QUARTER);
        Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        
        assertTrue(A1.equals(A1));
        assertTrue(A1.equals(A1Copy));
        assertTrue(A1Copy.equals(A1));
        assertFalse(A1.equals(A1differentDur));
        assertFalse(A1.equals(B1));
        assertFalse(A1.equals(Asharp1));
        assertTrue(C4.equals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER)));
        
        Pitch pitch = Pitch.getPitch(Pitch.Base.C, 0, 1);
        HashSet<Articulation> articulations = new HashSet<>();
        articulations.add(Articulation.STACCATO);
        Note note1 = Note.getNote(pitch, Durations.EIGHT, articulations);
        articulations.add(Articulation.TENUTO);
        Note note2 = Note.getNote(pitch, Durations.EIGHT, articulations);
        Note note3 = Note.getNote(pitch, Durations.EIGHT, articulations);
        
        assertFalse(note1.equals(Note.getNote(pitch, Durations.EIGHT)));
        assertFalse(note1.equals(note2));
        assertTrue(note2.equals(note2));
        assertTrue(note2.equals(note3));
    }
    
    @Test
    public void testCreatingInvalidNote() {
        
        try {
            Note note = Note.getNote(Pitch.Base.C, 5, 1, Durations.QUARTER);
            fail("No exception was thrown. Expected: IllegalArgumentException");
        }
        catch(Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        
        try {
            Note note = Note.getNote(Pitch.Base.C, 0, 11, Durations.QUARTER);
            fail("No exception was thrown. Expected: IllegalArgumentException");
        }
        catch(Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
                
        try {
            Note note = Note.getNote(Pitch.Base.C, 0, 1, null);
            fail("No exception was thrown. Expected: IllegalArgumentException");
        }
        catch(Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }
    
    @Test
    public void testHasArticulation() {
        Pitch pitch = Pitch.getPitch(Pitch.Base.C, 0, 1);
        Set<Articulation> articulations = new HashSet<>();
        articulations.add(Articulation.STACCATO);
        assertTrue(Note.getNote(pitch, Durations.EIGHT, articulations).hasArticulation(Articulation.STACCATO));
        assertFalse(Note.getNote(pitch, Durations.EIGHT).hasArticulation(Articulation.STACCATO));
    }
    
    @Test
    public void testHasArticulations() {
        Pitch pitch = Pitch.getPitch(Pitch.Base.C, 0, 1);
        HashSet<Articulation> articulations = new HashSet<>();
        articulations.add(Articulation.STACCATO);
        assertTrue(Note.getNote(pitch, Durations.EIGHT, articulations).hasArticulations());
        assertFalse(Note.getNote(pitch, Durations.EIGHT).hasArticulations());
    }
    
    @Test
    public void testGetArticulations() {
        Pitch pitch = Pitch.getPitch(Pitch.Base.C, 0, 1);
        assertTrue(Note.getNote(pitch, Durations.EIGHT).getArticulations().isEmpty());
        
        Set<Articulation> articulations = new HashSet<>();
        articulations.add(Articulation.STACCATO);
        articulations.add(Articulation.TENUTO);
        Note note = Note.getNote(pitch, Durations.EIGHT, articulations);
       
        Set<Articulation> artic = note.getArticulations();
        assertEquals(2, artic.size());
        assertTrue(artic.contains(Articulation.STACCATO));
        assertTrue(artic.contains(Articulation.TENUTO));
        try {
            artic.remove(Articulation.STACCATO);
            fail("Removing articulation succeeded, immutability violated");
        }
        catch(Exception e) { /* Do nothing */ }
        
        assertTrue(note.hasArticulation(Articulation.STACCATO));
    }
    
    @Test
    public void testTies() {
        NoteBuilder firstBuilder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        NoteBuilder secondBuilder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT);
        firstBuilder.addTieToFollowing(secondBuilder);
    
        Note secondNote = secondBuilder.build();
        assertTrue(secondNote.isTied());
        assertTrue(secondNote.getFollowingTiedNote() == null);
        assertTrue(secondNote.isTiedFromPrevious());
    
        Note firstNote = firstBuilder.build();
        assertTrue(firstNote.isTied());
        assertTrue(firstNote.getFollowingTiedNote() != null);
        assertFalse(firstNote.isTiedFromPrevious());
        
    }
    
    @Test
    public void testTiedDuration() {
        Note untied = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        assertEquals(Durations.QUARTER, untied.getTiedDuration());
        
        NoteBuilder firstBuilder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
        NoteBuilder secondBuilder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT);
        firstBuilder.addTieToFollowing(secondBuilder);
        
        Note firstNote = firstBuilder.build();
        Note secondNote = secondBuilder.build();
        
        assertEquals(Durations.QUARTER.addDot(), firstNote.getTiedDuration());
        assertEquals(Durations.EIGHT, secondNote.getTiedDuration());
        
        firstBuilder.clearCache();
        secondBuilder.clearCache();
        
        NoteBuilder thirdBuilder = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.EIGHT);
        secondBuilder.addTieToFollowing(thirdBuilder);
        
        firstNote = firstBuilder.build();
        secondNote = secondBuilder.build();
        Note thirdNote = thirdBuilder.build();
        
        assertEquals(Durations.HALF, firstNote.getTiedDuration());
        assertEquals(Durations.QUARTER, secondNote.getTiedDuration());
        assertEquals(Durations.EIGHT, thirdNote.getTiedDuration());
    }
}
