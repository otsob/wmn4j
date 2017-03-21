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
   
    // TODO: Keep track of cumulated durations of layers for faster checking of whether layer is full?
    // TODO: Add method to check if layer is full?
    private final List<List<Durational>> layers;
    private final MeasureAttributes measureInfo;
    private final int number;
    
    public MeasureBuilder(int number, MeasureAttributes measureInfo) {
        this.layers = new ArrayList();
        this.number = number;
        this.measureInfo = measureInfo;
    }
    
    public MeasureBuilder(int number, TimeSignature timeSig, KeySignature keySig, Barline barline, Clef clef) {
        this.layers = new ArrayList();
        this.number = number;
        this.measureInfo = MeasureAttributes.getMeasureAttr(timeSig, keySig, barline, clef);
    }
    
    public MeasureBuilder addLayer() {
        this.layers.add(new ArrayList());
        return this;
    }
    
    public MeasureBuilder addLayer(List<Durational> layer) {
        this.layers.add(layer);
        return this;
    }
     
    public MeasureBuilder addToLayer(int layer, Durational elem) {
        
        while(this.layers.size() <= layer)
            this.layers.add(new ArrayList());
        
        this.layers.get(layer).add(elem);
        return this;
    }
    
    public int getNumberOfLayers() {
        return this.layers.size();
    }
    
    public boolean isLayerFull(int layer) {
        return true;
    }
    
    public boolean isFull() {
        return true;
    }
    
    public Measure build() {
        return new Measure(this.number, this.layers, this.measureInfo);
    }
}
