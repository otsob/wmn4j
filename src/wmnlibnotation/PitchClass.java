/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

/**
 * Represents the 12 pitch classes in equal-temperament tuning.
 * @author Otso Björklund
 */
public enum PitchClass {
    C(0), 
    CSHARP_DFLAT(1), 
    D(2), 
    DSHARP_EFLAT(3), 
    E(4), 
    F(5), 
    FSHARP_GFLAT(6), 
    G(7), 
    GSHARP_AFLAT(8), 
    A(9), 
    ASHARP_BFLAT(10), 
    B(11);
    
    static private final PitchClass pcs[]
            = {C, CSHARP_DFLAT, D, DSHARP_EFLAT, E, F, FSHARP_GFLAT, G, GSHARP_AFLAT, A, ASHARP_BFLAT, B};
    
    private final int number;
    
    private PitchClass(int number) {
        this.number = number;
    }
    
    /**
     * @return the <a href="http://en.wikipedia.org/wiki/Pitch_class">pitch class number</a> of this pitch class.
     */
    public int toInt() {
        return this.number;
    }
    
    /**
     * Computes the pitch class from the pitch number.
     * @param pitchNumber The MIDI number of the pitch.
     * @return pitch class corresponding to the pitchNumber.
     */
    public static PitchClass fromInt(int pitchNumber) {
        if(pitchNumber < 0)
            throw new IllegalArgumentException("pitchNumber must be non-negative.");
        
        return pcs[pitchNumber % 12];
    }
}
