/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import java.util.ArrayList;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Unit tests for Duration class.
 * @author Otso Björklund
 */
public class DurationTest {
    
    public DurationTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testGetDurationWithValidParameter() {
        Duration duration = Duration.getDuration(1, 4);
        assertTrue(duration != null);
        assertTrue(duration.getNumerator() == 1);
        assertTrue(duration.getDenominator() == 4);
    }
    
    @Test
    public void testGetDurationWithInvalidParameter() {
        try {
            Duration duration = Duration.getDuration(-1, 2);
            fail("No exception was thrown. Expected: IllegalArgumentException");
        }
        catch(Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        try {
            Duration duration = Duration.getDuration(1, 0);
            fail("No exception was thrown. Expected: IllegalArgumentException");
        }
        catch(Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }

    @Test
    public void testEquals() {
        Duration quarter = Duration.getDuration(1, 4);
        assertTrue(quarter.equals(quarter));
        assertTrue(quarter.equals(Durations.QUARTER));
        assertTrue(quarter.equals(Duration.getDuration(1, 4)));
        
        Duration anotherQuarter = Duration.getDuration(2, 8);
        assertTrue(quarter.equals(anotherQuarter));
        assertTrue(quarter.equals(Durations.QUARTER));
        assertTrue(quarter.equals(Duration.getDuration(1, 4)));
        
        Duration notQuarter = Duration.getDuration(1, 8);
        assertFalse(notQuarter.equals(quarter));
        
        assertFalse(Durations.EIGHT_TRIPLET.equals(Durations.THIRTYSECOND));
    }

    @Test
    public void testRationalNumberReduced() {
        Duration quarter = Duration.getDuration(3, 12);
        assertEquals(1, quarter.getNumerator());
        assertEquals(4, quarter.getDenominator());
        
        Duration quintuplet = Duration.getDuration(5, 100);
        assertEquals(1, quintuplet.getNumerator());
        assertEquals(20, quintuplet.getDenominator());
    }
    
    @Test
    public void testToString() {
        assertEquals("(1/4)", Duration.getDuration(1, 4).toString());
        assertEquals("(1/8)", Durations.EIGHT.toString());
        assertEquals("(1/16)", Duration.getDuration(1, 16).toString());
    }
    
    @Test
    public void testToDouble() {
        assertTrue(new Double(0.25).equals(Durations.QUARTER.toDouble()));
        assertTrue(new Double(0.5).equals(Durations.HALF.toDouble()));
        assertFalse(new Double(0.49).equals(Durations.HALF.toDouble()));
    }
    
    @Test
    public void testAdd() {
        assertEquals(Durations.EIGHT, Durations.SIXTEENTH.add(Durations.SIXTEENTH));
        assertEquals(Durations.QUARTER, Durations.EIGHT_TRIPLET.add(Durations.EIGHT_TRIPLET.add(Durations.EIGHT_TRIPLET)));
        assertEquals(Duration.getDuration(1, 8), Duration.getDuration(3, 32).add(Duration.getDuration(1, 32)));
    }
    
    @Test 
    public void testSubtract() {
        assertEquals(Durations.EIGHT, Durations.QUARTER.subtract(Durations.EIGHT));
        assertEquals(Durations.QUARTER.addDot(), Durations.HALF.subtract(Durations.EIGHT));
        assertEquals(Duration.getDuration(2, 12), Durations.QUARTER.subtract(Durations.EIGHT_TRIPLET));
    }
    
    @Test
    public void testMultiplyBy() {
        assertEquals(Durations.QUARTER, Durations.EIGHT.multiplyBy(2));
        assertEquals(Durations.QUARTER, Durations.EIGHT_TRIPLET.multiplyBy(3));
        assertEquals(Durations.EIGHT.addDot(), Durations.SIXTEENTH.multiplyBy(3));
    }
    
    @Test
    public void testDivideBy() {
        assertEquals(Durations.EIGHT, Durations.QUARTER.divideBy(2));
        assertEquals(Durations.QUARTER, Durations.WHOLE.divideBy(4));
        assertEquals(Duration.getDuration(1, 20), Durations.QUARTER.divideBy(5));
    }
    
    @Test
    public void testLongerThan() {
        assertTrue(Durations.QUARTER.longerThan(Durations.EIGHT));
        assertFalse(Durations.EIGHT_TRIPLET.longerThan(Durations.EIGHT));
    }
    
    @Test
    public void testShorterThan() {
        assertTrue(Durations.SIXTEENTH.shorterThan(Durations.EIGHT));
        assertFalse(Durations.EIGHT_TRIPLET.shorterThan(Durations.THIRTYSECOND));
    }
    
    @Test
    public void testCompareTo() {
        assertEquals(0, Durations.EIGHT.compareTo(Durations.EIGHT));
        assertTrue(0 > Durations.QUARTER.compareTo(Durations.HALF));
        assertTrue(0 < Durations.HALF.compareTo(Durations.QUARTER));
        assertTrue(0 > Durations.QUARTER.compareTo(Durations.QUARTER.addDot()));
        assertTrue(0 < Durations.QUARTER.compareTo(Durations.EIGHT_TRIPLET.addDot()));
    }
    
    @Test
    public void testAddDot() {
        assertEquals(Duration.getDuration(3, 8), Durations.QUARTER.addDot());
        assertEquals(Duration.getDuration(3, 4), Durations.HALF.addDot());
    }
    
    @Test
    public void testSumOf() {
        List<Duration> durations = new ArrayList<>();
        int numOfQuarters = 4;
        for(int i = 0; i < numOfQuarters; ++i) 
            durations.add(Durations.QUARTER);
        
        assertEquals("Four quarters did not add to whole note.", Durations.QUARTER.multiplyBy(numOfQuarters), Duration.sumOf(durations));
        
        durations = new ArrayList<>();
        durations.add(Durations.EIGHT);
        durations.add(Durations.SIXTEENTH);
        durations.add(Durations.SIXTEENTH);
        durations.add(Durations.QUARTER.addDot());
        durations.add(Durations.SIXTEENTH_TRIPLET);
        durations.add(Durations.SIXTEENTH_TRIPLET);
        durations.add(Durations.SIXTEENTH_TRIPLET);
        durations.add(Durations.EIGHT_TRIPLET);
        durations.add(Durations.EIGHT_TRIPLET);
        durations.add(Durations.EIGHT_TRIPLET);
        assertEquals("Mixed durations did not add to whole note.", Durations.WHOLE, Duration.sumOf(durations));
    }
}
