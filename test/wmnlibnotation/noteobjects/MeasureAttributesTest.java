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
public class MeasureAttributesTest {
    
    public MeasureAttributesTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    @Test
    public void testGetMeasureInfo() {
        MeasureAttributes attr 
                = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.SINGLE, Clefs.G);
     
        assertFalse(attr == null);
        assertEquals(TimeSignatures.FOUR_FOUR, attr.getTimeSignature());
        assertEquals(KeySignatures.CMAJ_AMIN, attr.getKeySignature());
        assertEquals(Barline.SINGLE, attr.getRightBarline());
        assertEquals(Barline.SINGLE, attr.getLeftBarline());
        assertEquals(Clefs.G, attr.getClef());
    }
    
    @Test
    public void testGetMeasureInfoWithInvalidParameters() {
        
        try {
            MeasureAttributes attr 
                = MeasureAttributes.getMeasureAttr(null, KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.SINGLE, Clefs.G);
     
            fail("Did not throw exception with null TimeSignature");
        } 
        catch(Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
                
        try {
            MeasureAttributes attr 
                = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, null, Barline.SINGLE, Barline.SINGLE, Clefs.G);
     
            fail("Did not throw exception with null KeySignature");
        } 
        catch(Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
        
        try {
            MeasureAttributes attr 
                = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, null, Barline.SINGLE, Clefs.G);
     
            fail("Did not throw exception with null right barline");
        } 
        catch(Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    
        try {
            MeasureAttributes attr 
                = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.SINGLE, null);
     
            fail("Did not throw exception with null Clef");
        } 
        catch(Exception e) {
            assertTrue(e instanceof NullPointerException);
        }
    }

    @Test
    public void testEquals() {
        MeasureAttributes attr 
                = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.SINGLE, Clefs.G);
        
        MeasureAttributes other 
                = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.SINGLE, Clefs.G);
     
        
        MeasureAttributes different 
                = MeasureAttributes.getMeasureAttr(TimeSignatures.FOUR_FOUR, KeySignatures.CMAJ_AMIN, Barline.SINGLE, Barline.DOUBLE, Clefs.G);
     
        assertTrue(attr.equals(attr));
        assertTrue(attr.equals(other));
        assertFalse(attr.equals(different));
        assertFalse(different.equals(attr));
    }
}
