/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibanalysis;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Otso Björklund
 */
public class PitchClassCounter {
    private final Map<Integer, Long> pitchClasses;
    
    public PitchClassCounter() {
        this.pitchClasses = new HashMap();
    }
    
}
