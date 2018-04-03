/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

/**
 * Common time signatures.
 * @author Otso Björklund
 */
public class TimeSignatures {
    
    public static final TimeSignature FOUR_FOUR = TimeSignature.getTimeSignature(4, 4);
    public static final TimeSignature THREE_FOUR = TimeSignature.getTimeSignature(3, 4);
    public static final TimeSignature TWO_FOUR = TimeSignature.getTimeSignature(2, 4);
    
    public static final TimeSignature THREE_EIGHT = TimeSignature.getTimeSignature(3, 8);
    public static final TimeSignature SIX_EIGHT = TimeSignature.getTimeSignature(6, 8);
    
    private TimeSignatures() {
        // Not meant to be instantiated.
        throw new AssertionError();
    }
}
