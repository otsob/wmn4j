package wmnlibnotation;

import wmnlibnotation.Duration;
import wmnlibnotation.Durations;
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
        assertTrue(duration.getNominator() == 1);
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
        Duration duration = Duration.getDuration(1, 4);
        assertTrue(duration.equals(duration));
        assertTrue(duration.equals(Durations.QUARTER));
        assertTrue(duration.equals(Duration.getDuration(1, 4)));
    }

    @Test
    public void testToString() {
        assertEquals("(1/4)", Duration.getDuration(1, 4).toString());
        assertEquals("(1/8)", Durations.EIGHT.toString());
        assertEquals("(1/16)", Duration.getDuration(1, 16).toString());
    }
    
    @Test
    public void testAdd() {
        assertEquals(Durations.EIGHT, Durations.SIXTEENTH.add(Durations.SIXTEENTH));
        assertEquals(Durations.QUARTER, Durations.EIGHT_TRIPLET.add(Durations.EIGHT_TRIPLET.add(Durations.EIGHT_TRIPLET)));
        assertEquals(Duration.getDuration(1, 8), Duration.getDuration(3, 32).add(Duration.getDuration(1, 32)));
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
}
