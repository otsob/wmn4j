/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibmir.pattern_discovery;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Otso Björklund
 */
public class TEC {
    
    private final Pattern pattern;
    private final List<NoteEventVector> translators;
    
    public TEC(Pattern pattern, List<NoteEventVector> translators) {
        this.pattern = pattern;
        this.translators = translators;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
    
        strBuilder.append("pattern: {");
        for(NoteEventVector p : this.pattern.getPoints()) {
            strBuilder.append(p.toString()).append(", ");
        }
        strBuilder.replace(strBuilder.length()-2, strBuilder.length(), "");
        strBuilder.append("}, translators: {");
        
        for(NoteEventVector t : this.translators) {
            strBuilder.append(t.toString()).append(", ");
        }
        strBuilder.replace(strBuilder.length()-2, strBuilder.length(), "");
        strBuilder.append("}");
    
        return strBuilder.toString();
    }
    
}
