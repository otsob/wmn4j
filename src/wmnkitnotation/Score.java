/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

import java.util.List;

/**
 *
 * @author Otso Björklund
 */
public class Score {
    
    private final List<Staff> staves;
    
    public Score(List<Staff> staves) {
        this.staves = staves;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        
        
        for(int i = 0; i < staves.size(); ++i) {
            strBuilder.append(staves.get(i).toString());
            strBuilder.append("\n\n");
        }
     
        return strBuilder.toString();
    }
    
}
