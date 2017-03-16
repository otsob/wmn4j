/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class PitchClassTest {
    
    public PitchClassTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testToInt() {
        assertEquals(0, PitchClass.C.toInt());
        assertEquals(3, PitchClass.DSharpEFlat.toInt());
        assertEquals(11, PitchClass.B.toInt());
    }

    /**
     * Test of fromInt method, of class PitchClass.
     */
    @Test
    public void testFromInt() {
        assertEquals(PitchClass.C, PitchClass.fromInt(12));
        assertEquals(PitchClass.CSharpDFlat, PitchClass.fromInt(25));
        assertEquals(PitchClass.B, PitchClass.fromInt(11 + 12*2));
        assertEquals(PitchClass.G, PitchClass.fromInt(7 + 12*5));
    }
    
}
