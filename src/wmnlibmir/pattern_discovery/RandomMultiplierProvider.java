/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibmir.pattern_discovery;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author Otso Björklund
 */
public enum RandomMultiplierProvider {
    INSTANCE;
    
    private final List<Long> multipliers;
    
    private RandomMultiplierProvider() {
        this.multipliers = new ArrayList();
        this.generateMultipliers(100);
    }
    
    private void generateMultipliers(int count) {
        Random random = new Random();
        for(int i = 0; i < count; ++i) {
            
            this.multipliers.add(random.nextLong());
        }
    }
    
    public long getMultiplier(int i) {
        
        while(this.multipliers.size() < i + 10)
            this.generateMultipliers(10);
        
        return this.multipliers.get(i);
    }
}
