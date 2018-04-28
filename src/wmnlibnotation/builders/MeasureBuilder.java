/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.TimeSignatures;
import wmnlibnotation.noteobjects.TimeSignature;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.Clef;
import wmnlibnotation.noteobjects.Durational;
import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Clefs;
import wmnlibnotation.noteobjects.KeySignature;
import wmnlibnotation.noteobjects.KeySignatures;
import wmnlibnotation.noteobjects.MeasureAttributes;
import wmnlibnotation.noteobjects.Barline;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for building <code>Measure</code> objects.
 * The builder does not ensure that the <code>DurationalBuilder</code> objects in the 
 * builder fill up exactly a measure that has the set time signature. 
 * The methods {@link #isFull() isFull} and {@link #isLayerFull(int) isLayerFull} should be used for checking if the durations add up to the correct 
 * 
 * Default values:
 * TimeSignature : 4/4
 * KeySignature : C-major/a-minor
 * Clef: G
 * Barlines (right and left): Single.
 * No clef changes.
 * @author Otso Björklund
 */
public class MeasureBuilder {
   
    private int number;
    // TODO: Keep track of layer durations in some way to make checking if measure is full faster.
    private final Map<Integer, List<DurationalBuilder>> layers;
    
    private TimeSignature timeSig = TimeSignatures.FOUR_FOUR;
    private KeySignature keySig = KeySignatures.CMAJ_AMIN;
    private Clef clef = Clefs.G;
    private Barline leftBarline = Barline.NONE;
    private Barline rightBarline = Barline.SINGLE;
    private Map<Duration, Clef> clefChanges = new HashMap<>();
    
    /**
     * Create a <code>MeasureBuilder</code> with the given <code>MeasureAttributes</code>.
     * @param number Measure number for measure being built.
     * @param measureAttr MeasureAttributes for measure.
     */
    public MeasureBuilder(int number, MeasureAttributes measureAttr) {
        this.layers = new HashMap<>();
        this.number = number;
        
        this.timeSig = measureAttr.getTimeSignature();
        this.keySig = measureAttr.getKeySignature();
        this.clef = measureAttr.getClef();
        this.leftBarline = measureAttr.getLeftBarline();
        this.rightBarline = measureAttr.getRightBarline();
        this.clefChanges = new HashMap<>(measureAttr.getClefChanges());
    }
    
    /**
     * @param number Measure number for measure being built.
     */
    public MeasureBuilder(int number) {
        this.layers = new HashMap<>();
        this.number = number;
    }

    /**
     * @return Measure number.
     */
    public int getNumber() {
        return this.number;
    }

    /**
     * @param number measure number for the measure that can be built.
     * @return reference to this builder.
     */
    public MeasureBuilder setNumber(int number) {
        this.number = number;
        return this;
    }

    /**
     * @return time signature currently set for this builder.
     */
    public TimeSignature getTimeSig() {
        return this.timeSig;
    }
    
    /**
     * @param timeSig time signature for the measure that can be built.
     * @return reference to this builder.
     */
    public MeasureBuilder setTimeSig(TimeSignature timeSig) {
        this.timeSig = timeSig;
        return this;
    }

    /**
     * @return key signature that is currently set for this builder.
     */
    public KeySignature getKeySig() {
        return this.keySig;
    }

    /**
     * @param keySig key signature for the measure that can be built.
     * @return reference to this builder.
     */
    public MeasureBuilder setKeySig(KeySignature keySig) {
        this.keySig = keySig;
        return this;
    }

    /**
     * @return clef currently set for this builder.
     */
    public Clef getClef() {
        return this.clef;
    }

    /**
     * @param clef clef for the measure that can be built.
     * @return reference to this builder.
     */
    public MeasureBuilder setClef(Clef clef) {
        this.clef = clef;
        return this;
    }

    /**
     * @return left barline currently set for this builder.
     */
    public Barline getLeftBarline() {
        return this.leftBarline;
    }

    /**
     * @param leftBarline left barline for the measure that can be built.
     * @return reference to this builder.
     */
    public MeasureBuilder setLeftBarline(Barline leftBarline) {
        this.leftBarline = leftBarline;
        return this;
    }

    /**
     * @return right barline currently set for this builder.
     */
    public Barline getRightBarline() {
        return rightBarline;
    }

    /**
     * @param rightBarline right barline for the measure that can be built.
     * @return reference to this builder.
     */
    public MeasureBuilder setRightBarline(Barline rightBarline) {
        this.rightBarline = rightBarline;
        return this;
    }

    /**
     * @return clef changes currently set for this builder. 
     * Durations are offsets from the beginning of the measure.
     */
    public Map<Duration, Clef> getClefChanges() {
        return this.clefChanges;
    }

    /**
     * Add clef change at offset.
     * @param offset Offset of clef change from beginning of measure.
     * @param clef clef starting from offset.
     * @return reference to this builder.
     */
    public MeasureBuilder addClefChange(Duration offset, Clef clef) {
        this.clefChanges.put(offset, clef);
        return this;
    }
    
    /**
     * Add new empty layer to this <code>MeasureBuilder</code>.
     * @return reference to this builder.
     */
    public MeasureBuilder addLayer() {
        this.layers.put(this.layers.keySet().size(), new ArrayList<>());
        return this;
    }
    
    /**
     * Add possibly non-empty layer to this <code>MeasureBuilder</code>.
     * @param layer new layer to be added to this.
     * @return reference to this builder.
     */
    public MeasureBuilder addLayer(List<DurationalBuilder> layer) {
        this.layers.put(this.layers.keySet().size(), layer);
        return this;
    }
     
    /**
     * Append <code>DurationalBuilder</code> object to layer with index <code>layer</code>.
     * If layer does not exist it is created.
     * @param layer index of layer to which builder is appended.
     * @param builder DurationalBuilder object to be appended to layer.
     * @return reference to this builder.
     */
    public MeasureBuilder addToLayer(int layer, DurationalBuilder builder) {
        
        if(!this.layers.keySet().contains(layer))
            this.layers.put(layer, new ArrayList<>());
        
        this.layers.get(layer).add(builder);
        return this;
    }
    
    /**
     * @return number or layers in this builder.
     */
    public int getNumberOfLayers() {
        return this.layers.size();
    }
    
    /**
     * Set the element at specified location to given value.
     * @param layer the number of the layer to be modified.
     * @param index the index in the layer.
     * @param builder element to be placed in index on layer.
     */
    public void setElement(int layer, int index, DurationalBuilder builder) {
        this.layers.get(layer).set(index, builder);
    }
    
    /**
     * Get the sum of durations on a layer.
     * @param layer the index of the layer.
     * @return Sum of the durations of the <code>Durational</code> objects on the layer.
     */
    public Duration totalDurationOfLayer(int layer) {
        List<Duration> durations = new ArrayList<>();
        for(DurationalBuilder d : this.layers.get(layer))
            durations.add(d.getDuration());
        
        return Duration.sumOf(durations);
    }
    
    /**
     * Check if layer is full.
     * A layer is considered full when it contains <code>DurationalBuilder</code> objects 
     * whose combined duration is enough to fill a measure that has the time 
     * signature that is set for this builder.
     * @param layer index of layer that is checked.
     * @return true if the durations in the layer add up to fill a measure. False otherwise.
     */
    public boolean isLayerFull(int layer) {
        Duration layerDuration = this.totalDurationOfLayer(layer);
        return !layerDuration.shorterThan(this.timeSig.getTotalDuration());
    }
    
    /**
     * Check if any layer in this builder is full.
     * @return true if even a single layer is full. False otherwise.
     */
    public boolean isFull() {
        for(int layer = 0; layer < this.layers.size(); ++layer) {
            if(this.isLayerFull(layer)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Create a <code>Measure</code> with the contents of this builder.
     * @return Measure that has the set attributes and contains 
     * <code>Durational</code> objects built using the contained
     * <code>DurationalBuilder</code> objects.
     */
    public Measure build() {
        MeasureAttributes measureAttr
                = MeasureAttributes.getMeasureAttr(this.timeSig, this.keySig, this.rightBarline, this.leftBarline, this.clef, this.clefChanges);
        
        // TODO: Check that layers are full. If not, pad them with rests.
        Map<Integer, List<Durational>> builtLayers = new HashMap<>();
        for(Integer layerNumber : this.layers.keySet()) {
            List<Durational> durationalsOnLayer = new ArrayList<>();
            this.layers.get(layerNumber).forEach((builder) -> durationalsOnLayer.add(builder.build()));
            builtLayers.put(layerNumber, durationalsOnLayer);
        }
        
        return new Measure(this.number, builtLayers, measureAttr);
    }
}
