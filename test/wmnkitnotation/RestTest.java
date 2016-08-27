/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class RestTest {
    
    public RestTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testGetDuration() {
        assertTrue(Rest.getRest(Durations.EIGHT).getDuration().equals(Durations.EIGHT));
        assertFalse(Rest.getRest(Durations.QUARTER).getDuration().equals(Durations.EIGHT));
    }

    @Test
    public void testToString() {
        assertEquals( "R(1/4)", Rest.getRest(Durations.QUARTER).toString() );
        assertEquals( "R(1/12)", Rest.getRest(Durations.EIGHT_TRIPLET).toString() );
    }
    
    @Test
    public void testEquals() {
        Rest quarter = Rest.getRest(Durations.QUARTER);
        Rest half = Rest.getRest(Durations.HALF);
        
        assertTrue(quarter.equals(Rest.getRest(Durations.QUARTER)));
        assertTrue(Rest.getRest(Durations.QUARTER).equals(quarter));
        assertFalse(quarter.equals(half));
    }
}
