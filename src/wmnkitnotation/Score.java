/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnkitnotation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Otso Björklund
 */
public class Score implements Iterable<Staff> {
    
    enum Info { NAME, COMPOSER, ARRANGER, YEAR }
    
    private final Map<Info, String> scoreInfo;
    private final List<Staff> staves;
    
    public Score(String name, String composerName, List<Staff> staves) {
        this.staves = Collections.unmodifiableList(new ArrayList(staves));
        this.scoreInfo = new HashMap();
        this.scoreInfo.put(Info.NAME, name);
        this.scoreInfo.put(Info.COMPOSER, composerName);
        
        if(this.staves == null)
            throw new NullPointerException("Cannot create score: staves was null");
    }
    
    public String getName() {
        String name = this.scoreInfo.get(Info.NAME);
        return (name != null) ? name : "";
    }
    
    public List<Staff> getStaves() {
        return this.staves;
    }
    
    public String getComposerName() {
        String name = this.scoreInfo.get(Info.COMPOSER);
        return (name != null) ? name : "";
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Score ").append(getName()).append("\n");
        
        for(int i = 0; i < staves.size(); ++i) {
            strBuilder.append(staves.get(i).toString());
            strBuilder.append("\n\n");
        }
     
        return strBuilder.toString();
    }

    @Override
    public Iterator<Staff> iterator() {
        return this.staves.iterator();
    }    
}
