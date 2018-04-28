/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class TimeSignatureTest {
    
    public TimeSignatureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testGetTimeSignature() {
        TimeSignature timeSig = TimeSignature.getTimeSignature(4, 4);
        assertEquals(4, timeSig.getNumBeats());
        assertEquals(Durations.QUARTER, timeSig.getBeatDuration());
    }
    
    @Test
    public void testGetTotalDuration() {
        assertEquals(Durations.WHOLE, TimeSignatures.FOUR_FOUR.getTotalDuration());
        assertEquals(Durations.EIGHT.multiplyBy(6), TimeSignatures.SIX_EIGHT.getTotalDuration());
        assertEquals(Durations.EIGHT.multiplyBy(13), TimeSignature.getTimeSignature(13, Durations.EIGHT).getTotalDuration());
    }
    
    @Test
    public void testEquals() {
        TimeSignature timeSigA = TimeSignature.getTimeSignature(4, 4);
        TimeSignature timeSigB = TimeSignature.getTimeSignature(4, 4);
        TimeSignature timeSigC = TimeSignature.getTimeSignature(3, 4);
        TimeSignature timeSigD = TimeSignature.getTimeSignature(4, 8);
        
        assertTrue(timeSigA.equals(timeSigB));
        assertFalse(timeSigA.equals(timeSigC));
        assertFalse(timeSigA.equals(timeSigD));
    }
    
    @Test
    public void testToString() {
        TimeSignature timeSigA = TimeSignature.getTimeSignature(4, 4);
        assertEquals("Time(4/4)", timeSigA.toString());
    }
}
