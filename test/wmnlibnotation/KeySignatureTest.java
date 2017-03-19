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
            KeySignature illegalCustomKeySig = KeySignature.getKeySig(sharps, sharps);
            fail("A KeySignature with the same note as sharp and flat was created without exception.");
        } catch (Exception e) {
            assertTrue("Exception was of incorrect type.", e instanceof IllegalArgumentException);
        }
    }
 
    @Test
    public void testGetNumSharps() {
        assertEquals(0, KeySignatures.CMaj_Amin.getNumSharps());
        assertEquals(1, KeySignatures.GMaj_Emin.getNumSharps());
        assertEquals(2, KeySignatures.DMaj_Bmin.getNumSharps());
    }
    
    @Test
    public void testGetNumFlats() {
        assertEquals(0, KeySignatures.CMaj_Amin.getNumFlats());
        assertEquals(4, KeySignatures.AFlatMaj_Fmin.getNumFlats());
    }

    @Test
    public void testEquals() {
        assertTrue(KeySignatures.CMaj_Amin.equals(KeySignatures.CMaj_Amin));
        assertTrue(KeySignatures.EMaj_CSharpMin.equals(KeySignatures.EMaj_CSharpMin));
        assertTrue(KeySignatures.EFlatMaj_Cmin.equals(KeySignatures.EFlatMaj_Cmin));
        
        KeySignature customSig = KeySignature.getKeySig(Arrays.asList(Pitch.Base.C),  Arrays.asList(Pitch.Base.B));
        assertTrue(customSig.equals(KeySignature.getKeySig(Arrays.asList(Pitch.Base.C),  Arrays.asList(Pitch.Base.B))));
        
        assertFalse(KeySignatures.CMaj_Amin.equals(KeySignatures.FMaj_Dmin));
    }
    
}
