/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WMNKitClasses;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
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

    /**
     * Test of getPitchString method with unaltered Note.
     */
    @Test
    public void testGetPitchStringUnalteredNote() {
        System.out.println("testToStringUnalteredNote");
        assertEquals("C4", Note.getNote( Note.Pitch.C, 0, 4, Durations.QUARTER ).getPitchString() );
    }
    
    /**
     * Test of getPitchString method with sharp and flat Note.
     */
    @Test
    public void testGetPitchStringSharpAndFlatNote() {
        System.out.println("testToStringSharpAndFlatNote");
        assertEquals("D#1", Note.getNote(Note.Pitch.D, 1, 1, Durations.QUARTER).getPitchString());
        assertEquals("Eb2", Note.getNote(Note.Pitch.E, -1, 2, Durations.QUARTER).getPitchString());
    }
    
    /**
     * Test of getPitchString method with double sharp and double flat Note.
     */
    @Test
    public void testGetPitchStringDoubleSharpDoubleFlatNote() {
        System.out.println("testToStringDoubleSharpDoubleFlatNote");
        assertEquals("F##5", Note.getNote(Note.Pitch.F, 2, 5, Durations.QUARTER).getPitchString());
        assertEquals("Gbb3", Note.getNote(Note.Pitch.G, -2, 3, Durations.QUARTER).getPitchString());
    }
    
    /**
     * Test of toString
     */
    @Test
    public void testToString() {
        System.out.println("testToString");
        assertEquals( "C#4(1/4)", Note.getNote(Note.Pitch.C, 1, 4, Durations.QUARTER).toString() );
        assertEquals( "Fb5(1/8)", Note.getNote(Note.Pitch.F, -1, 5, Durations.EIGHT).toString() );
    }
    
    /**
     * Test of equals.
     */
    @Test
    public void testEquals() {
        System.out.println("testEquals");
        Note A1 = Note.getNote(Note.Pitch.A, 0, 1, Durations.QUARTER);
        Note A1differentDur = Note.getNote(Note.Pitch.A, 0, 1, Durations.EIGHT);
        Note A1Copy = Note.getNote(Note.Pitch.A, 0, 1, Durations.QUARTER);
        Note B1 = Note.getNote(Note.Pitch.B, 0, 1, Durations.QUARTER);
        Note Asharp1 = Note.getNote(Note.Pitch.A, 1, 1, Durations.QUARTER);
        
        assertTrue(A1.equals(A1));
        assertTrue(A1.equals(A1Copy));
        assertTrue(A1Copy.equals(A1));
        assertFalse(A1.equals(A1differentDur));
        assertFalse(A1.equals(B1));
        assertFalse(A1.equals(Asharp1));
    }
    
    /**
     * Test of toInt.
     */
    @Test
    public void testPitchToInt() {
        System.out.println("testPitchToInt");
        Note C0 = Note.getNote(Note.Pitch.C, 0, 0, Durations.QUARTER);
        Note Csharp0 = Note.getNote(Note.Pitch.C, 1, 0, Durations.QUARTER);
        Note F4 = Note.getNote(Note.Pitch.F, 0, 4, Durations.QUARTER);
        Note Gb6 = Note.getNote(Note.Pitch.G, -1, 6, Durations.QUARTER);
        assertEquals(0, C0.toInt());
        assertEquals(1, Csharp0.toInt());
        assertEquals(53, F4.toInt());
        assertEquals(78, Gb6.toInt());
    }
    /**
     * Test getPitchClass.
     */
    @Test
    public void testGetPitchClass() {
        System.out.println("testGetPitchClass");
        Note C0 = Note.getNote(Note.Pitch.C, 0, 0, Durations.QUARTER);
        Note Csharp3 = Note.getNote(Note.Pitch.C, 1, 3, Durations.QUARTER);
        Note F2 = Note.getNote(Note.Pitch.F, 0, 2, Durations.QUARTER);
        Note Gb6 = Note.getNote(Note.Pitch.G, -1, 6, Durations.QUARTER);
        assertEquals(0, C0.getPitchClass());
        assertEquals(1, Csharp3.getPitchClass());
        assertEquals(5, F2.getPitchClass());
        assertEquals(6, Gb6.getPitchClass());
    }
}
