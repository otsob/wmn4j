/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class KeySignatureTest {
    
    public KeySignatureTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Test
    public void testGetKeySigExceptions() {
        try {
            List<Pitch.Base> sharps = Arrays.asList(Pitch.Base.C);
            KeySignature illegalCustomKeySig = new KeySignature(sharps, sharps);
            fail("A KeySignature with the same note as sharp and flat was created without exception.");
        } catch (Exception e) {
            assertTrue("Exception was of incorrect type.", e instanceof IllegalArgumentException);
        }
    }
 
    @Test
    public void testGetNumSharps() {
        assertEquals(0, KeySignatures.CMAJ_AMIN.getNumSharps());
        assertEquals(1, KeySignatures.GMAJ_EMIN.getNumSharps());
        assertEquals(2, KeySignatures.DMAJ_BMIN.getNumSharps());
    }
    
    @Test
    public void testGetNumFlats() {
        assertEquals(0, KeySignatures.CMAJ_AMIN.getNumFlats());
        assertEquals(4, KeySignatures.AFLATMAJ_FMIN.getNumFlats());
    }

    @Test
    public void testEquals() {
        assertTrue(KeySignatures.CMAJ_AMIN.equals(KeySignatures.CMAJ_AMIN));
        assertTrue(KeySignatures.EMAJ_CSHARPMIN.equals(KeySignatures.EMAJ_CSHARPMIN));
        assertTrue(KeySignatures.EFLATMAJ_CMIN.equals(KeySignatures.EFLATMAJ_CMIN));
        
        KeySignature customSig = new KeySignature(Arrays.asList(Pitch.Base.C),  Arrays.asList(Pitch.Base.B));
        assertTrue(customSig.equals(new KeySignature(Arrays.asList(Pitch.Base.C),  Arrays.asList(Pitch.Base.B))));
        
        assertFalse(KeySignatures.CMAJ_AMIN.equals(KeySignatures.FMAJ_DMIN));
    }
    
}
