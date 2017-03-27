/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

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
public class Score implements Iterable<SingleStaffPart> {
    
    enum Info { NAME, COMPOSER, ARRANGER, YEAR }
    
    private final Map<Info, String> scoreInfo;
    private final List<SingleStaffPart> parts;
    
    public Score(String name, String composerName, List<SingleStaffPart> staves) {
        this.parts = Collections.unmodifiableList(new ArrayList(staves));
        this.scoreInfo = new HashMap();
        this.scoreInfo.put(Info.NAME, name);
        this.scoreInfo.put(Info.COMPOSER, composerName);
        
        if(this.parts == null)
            throw new NullPointerException("Cannot create score: staves was null");
    }
    
    public String getName() {
        String name = this.scoreInfo.get(Info.NAME);
        return (name != null) ? name : "";
    }
    
    public List<SingleStaffPart> getParts() {
        return Collections.unmodifiableList(this.parts);
    }
    
    public String getComposerName() {
        String name = this.scoreInfo.get(Info.COMPOSER);
        return (name != null) ? name : "";
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Score ").append(getName()).append("\n");
        
        for(int i = 0; i < parts.size(); ++i) {
            strBuilder.append(parts.get(i).toString());
            strBuilder.append("\n\n");
        }
     
        return strBuilder.toString();
    }

    @Override
    public Iterator<SingleStaffPart> iterator() {
        return this.parts.iterator();
    }    
}
