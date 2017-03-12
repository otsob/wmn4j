/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Otso Björklund
 */
public class MeasureBuilder {
   
    private final List<List<NotationElement>> layers;
    private final MeasureInfo measureInfo;
    private final int number;
    
    public MeasureBuilder(int number, MeasureInfo measureInfo) {
        this.layers = new ArrayList();
        this.number = number;
        this.measureInfo = measureInfo;
    }
    
    public MeasureBuilder(int number, TimeSignature timeSig, KeySignature keySig, MeasureInfo.Barline barline, Clef clef) {
        this.layers = new ArrayList();
        this.number = number;
        this.measureInfo = MeasureInfo.getMeasureInfo(timeSig, keySig, barline, clef);
    }
    
    public MeasureBuilder addLayer() {
        this.layers.add(new ArrayList());
        return this;
    }
    
    public MeasureBuilder addLayer(List<NotationElement> layer) {
        this.layers.add(layer);
        return this;
    }
     
    public MeasureBuilder addToLayer(int layer, NotationElement elem) {
        
        while(this.layers.size() <= layer)
            this.layers.add(new ArrayList());
        
        this.layers.get(layer).add(elem);
        return this;
    }
    
    public int getNumberOfLayers() {
        return this.layers.size();
    }
    
    public Measure build() {
        return Measure.getMeasure(this.number, this.layers, this.measureInfo);
    }
}
