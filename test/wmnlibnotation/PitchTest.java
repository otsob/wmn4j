/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class PitchTest {
    
    public PitchTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testToInt() {
        assertEquals(12, Pitch.getPitch(Pitch.Base.C, 0, 0).toInt());
        assertEquals(60, Pitch.getPitch(Pitch.Base.C, 0, 4).toInt());
        assertEquals(66, Pitch.getPitch(Pitch.Base.F, 1, 4).toInt());
        assertEquals(51, Pitch.getPitch(Pitch.Base.E, -1, 3).toInt());
    }

    @Test
    public void testGetPitchClass() {
        assertEquals(PitchClass.C, Pitch.getPitch(Pitch.Base.C, 0, 5).getPitchClass());
        assertEquals(PitchClass.GSHARP_AFLAT, Pitch.getPitch(Pitch.Base.A, -1, 3).getPitchClass());
        assertEquals(PitchClass.B, Pitch.getPitch(Pitch.Base.B, 0, 2).getPitchClass());
    }
    
    @Test
    public void testGetPCNumber() {
        assertEquals(0, Pitch.getPitch(Pitch.Base.C, 0, 5).getPCNumber());
        assertEquals(8, Pitch.getPitch(Pitch.Base.A, -1, 3).getPCNumber());
        assertEquals(11, Pitch.getPitch(Pitch.Base.B, 0, 2).getPCNumber());
    }
    
    @Test
    public void testToString() {
        assertEquals("F0", Pitch.getPitch(Pitch.Base.F, 0, 0).toString());
        assertEquals("Eb2", Pitch.getPitch(Pitch.Base.E, -1, 2).toString());
        assertEquals("G#3", Pitch.getPitch(Pitch.Base.G, 1, 3).toString());
        assertEquals("Abb2", Pitch.getPitch(Pitch.Base.A, -2, 2).toString());
        assertEquals("D##6", Pitch.getPitch(Pitch.Base.D, 2, 6).toString());
    }

    @Test
    public void testEquals() {
        assertTrue(Pitch.getPitch(Pitch.Base.C, 0, 2).equals(Pitch.getPitch(Pitch.Base.C, 0, 2)));
        assertTrue(Pitch.getPitch(Pitch.Base.C, 1, 3).equals(Pitch.getPitch(Pitch.Base.C, 1, 3)));
        assertTrue(Pitch.getPitch(Pitch.Base.C, -1, 2).equals(Pitch.getPitch(Pitch.Base.C, -1, 2)));
        
        assertFalse(Pitch.getPitch(Pitch.Base.C, 0, 2).equals(Pitch.getPitch(Pitch.Base.D, 0, 2)));
        assertFalse(Pitch.getPitch(Pitch.Base.C, 0, 2).equals(Pitch.getPitch(Pitch.Base.C, 1, 2)));
        assertFalse(Pitch.getPitch(Pitch.Base.C, -1, 3).equals(Pitch.getPitch(Pitch.Base.D, -1, 2)));
    }
    
    @Test
    public void testEqualsEnharmonically() {
        assertTrue(Pitch.getPitch(Pitch.Base.C, 1, 2).equalsEnharmonically(Pitch.getPitch(Pitch.Base.C, 1, 2)));
        assertTrue(Pitch.getPitch(Pitch.Base.C, 1, 2).equalsEnharmonically(Pitch.getPitch(Pitch.Base.D, -1, 2)));
        assertFalse(Pitch.getPitch(Pitch.Base.C, 1, 2).equalsEnharmonically(Pitch.getPitch(Pitch.Base.D, -1, 3)));
    }
    
    @Test
    public void testHigherThan() {
        assertTrue(Pitch.getPitch(Pitch.Base.C, 0, 3).higherThan(Pitch.getPitch(Pitch.Base.C, 0, 2)));
        assertFalse(Pitch.getPitch(Pitch.Base.C, 0, 1).higherThan(Pitch.getPitch(Pitch.Base.C, 0, 1)));
        assertFalse(Pitch.getPitch(Pitch.Base.C, 0, 2).higherThan(Pitch.getPitch(Pitch.Base.C, 0, 3)));
    }
    
    @Test
    public void testLowerThan() {
        assertTrue(Pitch.getPitch(Pitch.Base.C, 0, 2).lowerThan(Pitch.getPitch(Pitch.Base.C, 1, 2)));
        assertFalse(Pitch.getPitch(Pitch.Base.E, 1, 4).lowerThan(Pitch.getPitch(Pitch.Base.D, 0, 4)));
        assertFalse(Pitch.getPitch(Pitch.Base.C, 0, 4).lowerThan(Pitch.getPitch(Pitch.Base.C, 0, 3)));
    }
    
    @Test
    public void testCompareTo() {
        assertTrue(0 == Pitch.getPitch(Pitch.Base.C, 0, 2).compareTo(Pitch.getPitch(Pitch.Base.C, 0, 2)));
        assertTrue(0 > Pitch.getPitch(Pitch.Base.C, -1, 2).compareTo(Pitch.getPitch(Pitch.Base.C, 0, 2)));
        assertTrue(0 < Pitch.getPitch(Pitch.Base.E, 0, 3).compareTo(Pitch.getPitch(Pitch.Base.D, 1, 3)));
    }
}
