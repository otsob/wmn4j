/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for building <code>Score</code> objects.
 * @author Otso Björklund
 */
public class ScoreBuilder {
    
    private Map<Score.Attribute, String> scoreAttr;
    private List<Part> parts;
    
    public ScoreBuilder() {
        this.scoreAttr = new HashMap();
        this.parts = new ArrayList();
    }
    
    /**
     * Set attribute to given value.
     * @param attribute the attribute to be set.
     * @param attrValue value for the attribute.
     */
    public void setAttribute(Score.Attribute attribute, String attrValue) {
        this.scoreAttr.put(attribute, attrValue);
    }
    
    /**
     * Add <code>Part</code> to builder.
     * @param part part to added to this builder.
     */
    public void addPart(Part part) {
        this.parts.add(part);
    }
    
    /**
     * Build a <code>Score</code> with the values specified in this builder.
     * @return a <code>Score</code> with the values specified in this builder.
     */
    public Score build() {
        return new Score(this.scoreAttr, this.parts);
    }
}
