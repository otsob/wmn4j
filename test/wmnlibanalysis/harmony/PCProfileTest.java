/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibanalysis.harmony;

import org.junit.Test;
import static org.junit.Assert.*;
import wmnlibnotation.Durations;
import wmnlibnotation.Note;
import wmnlibnotation.Pitch;
import wmnlibnotation.PitchClass;

/**
 *
 * @author Otso Björklund
 */
public class PCProfileTest {
    
    // By how much values are allowed fo differ
    static final double EPS = 0.0000000001;
    
    static final Note C4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);
    static final Note E4 = Note.getNote(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.EIGHT);
    static final Note G4 = Note.getNote(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.EIGHT);
    static final Note Csharp4 = Note.getNote(Pitch.getPitch(Pitch.Base.C, 1, 4), Durations.SIXTEENTH);
    
    public PCProfileTest() {
    }

    @Test
    public void testSetIncorrectValue() {
        PCProfile profile = new PCProfile();
        
        try {
            profile.setValue(PitchClass.C, -0.1);
            fail("No exception thrown");
        }
        catch(Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    
    }
    
    @Test
    public void testAddWithDefaultWeightFunction() {
        PCProfile profile = new PCProfile();
        profile.add(C4);
        assertEquals("Incorrect value for C", 1.0, profile.getValue(PitchClass.C), EPS);
        assertEquals("Incorrect value for Csharp", 0.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS);
        profile.add(Csharp4);
        assertEquals("Incorrect value for Csharp", 1.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS);
    }
    
    @Test
    public void testAddWithDurationWeightFunction() {
        PCProfile profile = new PCProfile(DurationWeighter.getInstance());
        profile.add(C4);
        assertEquals("Incorrect value for C", 0.25, profile.getValue(PitchClass.C), EPS);
        assertEquals("Incorrect value for Csharp", 0.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS);
        profile.add(Csharp4);
        assertEquals("Incorrect value for Csharp", 1.0/16.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS);
        profile.add(C4);
        assertEquals("Incorrect value for C", 0.5, profile.getValue(PitchClass.C), EPS);
    }

    @Test
    public void testNormalizeWithDefaultWeightFunction() {
        PCProfile profile = new PCProfile();
        final int C4Count = 5;
        final int G4Count = 3;
        final int CsharpCount = 2;
        
        for(int i = 0; i < C4Count; ++i)
            profile.add(C4);
        
        for(int i = 0; i < G4Count; ++i)
            profile.add(G4);
        
        for(int i = 0; i < CsharpCount; ++i)
            profile.add(Csharp4);
        
        
        assertEquals("Incorrect value for C before normalization", C4Count, profile.getValue(PitchClass.C), EPS);
        assertEquals("Incorrect value for G before normalization", G4Count, profile.getValue(PitchClass.G), EPS);
        assertEquals("Incorrect value for C sharp before normalization", CsharpCount, profile.getValue(PitchClass.CSHARP_DFLAT), EPS);
        
        profile = profile.normalize();
        
        assertEquals("Incorrect value for C before normalization", 1.0, profile.getValue(PitchClass.C), EPS);
        assertEquals("Incorrect value for G before normalization", 3.0/5.0, profile.getValue(PitchClass.G), EPS);
        assertEquals("Incorrect value for C sharp before normalization", 2.0/5.0, profile.getValue(PitchClass.CSHARP_DFLAT), EPS);
    }
    
    public static PCProfile getTestProfile(double ... values) {
        PCProfile profile = new PCProfile();
        int i = 0;
        
        for(PitchClass pc : PitchClass.values()) {
            profile.setValue(pc, values[i++]);
        }
    
        return profile;
    }
    
    @Test
    public void testCorrelation() {
        
        // Profile for C-major from Krumhansl and Kessler.
        PCProfile cMajorProfile 
                = getTestProfile(6.35, 2.23, 3.48, 2.33, 4.38, 4.09, 2.52, 5.19, 2.39, 3.66, 2.29, 2.88);
        // Profile for A minor from Krumhansl and Kessler.
        PCProfile aMinorProfile 
                = getTestProfile(5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17, 6.33, 2.68, 3.52);
        
        assertEquals("Incorrect correlation for c major profile with itself", 
                        1.0, PCProfile.correlation(cMajorProfile, cMajorProfile), EPS);
    
        assertEquals("Incorrect correlation between c major and a minor profiles", 
                0.6496, PCProfile.correlation(cMajorProfile, aMinorProfile), 0.0001);
        
        assertEquals("Correlation should be symmetric but is not", 
                PCProfile.correlation(cMajorProfile, aMinorProfile), PCProfile.correlation(aMinorProfile, cMajorProfile), EPS);
    }
    
    @Test
    public void testEuclidean() {
        // Profile for C-major from Krumhansl and Kessler.
        PCProfile a 
                = getTestProfile(1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00, 1.00);
        // Profile for A minor from Krumhansl and Kessler.
        PCProfile b 
                = getTestProfile(5.38, 2.60, 3.53, 2.54, 4.75, 3.98, 2.69, 3.34, 3.17, 6.33, 2.68, 3.52);
 
        assertEquals("Incorrect distance when computing distance with itself", 0.0, 
                       PCProfile.euclidean(b, b), EPS);
        
        PCProfile c 
                = getTestProfile(0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00);
        
        assertEquals("Incorrect value", Math.sqrt(12.0), PCProfile.euclidean(a, c), EPS);
    }
}
