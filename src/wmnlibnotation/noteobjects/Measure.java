/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.noteobjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class that defines a measure.
 * A measure may contain multiple layers that are referred to using layer numbers.
 * This class is immutable. 
 * Use the MeasureBuilder class for easier creation of Measures.
 * @author Otso Björklund
 */
public class Measure implements Iterable<Durational> {
    
    private final int number;
    private final SortedMap<Integer, List<Durational>> layers;
    private final MeasureAttributes measureAttr;
    
    /**
     * @param number number of the measure.
     * @param noteLayers the notes on the different layers of the measure.
     * @param timeSig TimeSignature of the measure.
     * @param keySig KeySignature in effect in the measure.
     * @param rightBarLine barline on the right side (left side is NONE by default).
     * @param clef Clef in effect in the measure.
     */
    public Measure(int number, Map<Integer, List<Durational>> noteLayers, TimeSignature timeSig, KeySignature keySig, Barline rightBarLine, Clef clef) {
        this(number, noteLayers, MeasureAttributes.getMeasureAttr(timeSig, keySig, rightBarLine, clef));
    }
    
    /**
     * @param number number of the measure.
     * @param noteLayers the notes on the different layers of the measure.
     * @param timeSig TimeSignature of the measure.
     * @param keySig KeySignature in effect in the measure.
     * @param clef Clef in effect in the measure.
     */
    public Measure(int number, Map<Integer, List<Durational>> noteLayers, TimeSignature timeSig, KeySignature keySig, Clef clef) {
        this(number, noteLayers, MeasureAttributes.getMeasureAttr(timeSig, keySig, Barline.SINGLE, clef));
    }
    
    /**
     * @param number number of the measure.
     * @param noteLayers the notes on the different layers of the measure.
     * @param measureAttr the attributes of the measure.
     */
    public Measure(int number, Map<Integer, List<Durational>> noteLayers, MeasureAttributes measureAttr) {
        this.number = number;
        SortedMap<Integer, List<Durational>> layersCopy = new TreeMap<>();
        
        for(Integer layerNum : noteLayers.keySet())
            layersCopy.put(layerNum, Collections.unmodifiableList(new ArrayList<>(noteLayers.get(layerNum))));
        
        this.layers = Collections.unmodifiableSortedMap(layersCopy);
        
        this.measureAttr = measureAttr;
        
        if(this.layers == null || this.measureAttr == null)
            throw new NullPointerException();
        
        if(this.number < 0)
            throw new IllegalArgumentException("Measure number must be at least 0");
    }
    
    /**
     * Get the layer numbers in this measure.
     * Layer numbers are not necessarily consecutive and do not begin from 0.
     * @return list of the layer numbers used in this measure.
     */
    public List<Integer> getLayerNumbers() {
        return new ArrayList<>(this.layers.keySet());
    }
    
    /**
     * Get a layerNumber of the measure.
     * @param layerNumber the number of the layer.
     * @return the layerNumber at the given index layerNumber.
     */
    public List<Durational> getLayer(int layerNumber) {
        return this.layers.get(layerNumber);
    }
    
    /**
     * @param layerNumber 
     * @return the number of elements on the layer with layerNumber.
     */
    public int getLayerSize(int layerNumber) {
        return this.layers.get(layerNumber).size();
    }
    
    /**
     * @return number of layers in this measure.
     */
    public int getLayerCount() {
        return this.layers.keySet().size();
    }
    
    /**
     * @return true if this measure only has one layer, false otherwise.
     */
    public boolean isSingleLayer() {
        return this.getLayerCount() == 1;
    }
    
    /**
     * @return the number of this measure.
     */
    public int getNumber() {
        return this.number;
    }
    
    /**
     * @return TimeSignature in effect in this measure.
     */
    public TimeSignature getTimeSignature() {
        return this.measureAttr.getTimeSignature();
    }
    
    /**
     * @return KeySignature in effect in this measure.
     */
    public KeySignature getKeySignature() {
        return this.measureAttr.getKeySignature();
    }
    
    /**
     * @return right barline of this measure.
     */
    public Barline getRightBarline() {
        return this.measureAttr.getRightBarline();
    }
    
    /**
     * @return left barline of this measure.
     */
    public Barline getLeftBarline() {
        return this.measureAttr.getLeftBarline();
    }
    
    /**
     * @return The clef in effect in the beginning of this measure.
     */
    public Clef getClef() {
        return this.measureAttr.getClef();
    }
    
    /**
     * @return a Map of clef changes in this measure, where the Duration key 
     * is the offset counted from the beginning of the measure.
     */
    public Map<Duration, Clef> getClefChanges() {
        return this.measureAttr.getClefChanges();
    }
    
    /**
     * @return true if there are clef changes in this measure, false otherwise.
     */
    public boolean containsClefChanges() {
        return this.measureAttr.containsClefChanges();
    }
    
    /**
     * @return true if this <code>Measure</code> is a pickup measure.
     */
    public boolean isPickUp() {
        return this.getNumber() == 0;
    }
    
    /**
     * Returns the <code>Durational</code> at the given index on the given layer
     * number.
     * @param layerNumber Number of the layer from which to get the element.
     * @param index index of element on the layer.
     * @return <code>Durational</code> at the given index on the given layer.
     * @throws NoSuchElementException 
     */
    public Durational get(int layerNumber, int index) throws NoSuchElementException {
        if(!this.layers.keySet().contains(layerNumber))
            throw new NoSuchElementException();
        
        List<Durational> layer = this.layers.get(layerNumber);
        if(index < 0 || index >= layer.size())
            throw new NoSuchElementException();
        
        return layer.get(index);
    }
    
    /**
     * String representation of <code>Measure</code>.
     * This is subject to change.
     * @return string representation of measure.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Measure ").append(this.number).append(", ")
                  .append(this.measureAttr).append(":\n");
        
        for(Integer i : this.layers.keySet()) {
            strBuilder.append("Layer ").append(i).append(": ");
            for(int j = 0; j < layers.get(i).size(); ++j) {
                strBuilder.append(layers.get(i).get(j).toString());
                if(j != layers.get(i).size() - 1)
                    strBuilder.append(", ");
            }
            strBuilder.append("\n");
        }
        
        return strBuilder.toString();
    }

    /**
     * @return iterator that goes through the Measure layer wise.
     */
    @Override
    public Iterator<Durational> iterator() {
        return this.getMeasureIterator();
    }
    
    /**
     * Get an iterator as <code>Measure.Iter</code>.
     * @return an iterator of type <code>Measure.Iter</code>.
     */
    public Measure.Iter getMeasureIterator() {
        return new Iter(this);
    }
    
    /**
     * Iterator for <code>Durational</code> objects in a <code>Measure</code>.
     * The iterator iterates through the notes in the Measure layer by layer
     * going from the earliest Durational in the layer to the last on each layer.
     * The order of layers is unspecified.
     * The iterator does not support removing.
     */
    public static class Iter implements Iterator<Durational> {
        private final List<Integer> layerNumbers;
        private final Measure measure;
        private int layerNumberIndex = 0;
        private int positionInLayer = 0;
        private int prevLayerNumber = 0;
        private int prevPositionInLayer = 0;

        public Iter(Measure measure) {
            this.measure = measure;
            this.layerNumbers = measure.getLayerNumbers();
        }

        /**
         * @return The layer of the <code>Durational</code> that was returned
         * by the last call of {@link #next() next}. If next has not been called,
         * return value is useless.
         */
        public int getLayerOfPrevious() {
            return this.prevLayerNumber;
        }

        /**
         * @return The index of the <code>Durational</code> that was returned
         * by the last call of {@link #next() next}. If next has not been called,
         * return value is useless.
         */
        public int getIndexOfPrevious() {
            return this.prevPositionInLayer;
        }

        @Override
        public boolean hasNext() {
            if(layerNumberIndex >= this.layerNumbers.size())
                return false;

            int layerNumber = this.layerNumbers.get(this.layerNumberIndex);
            return !this.measure.getLayer(layerNumber).isEmpty();
        }

        @Override
        public Durational next() {
            if(!this.hasNext())
                throw new NoSuchElementException();
            
            this.prevLayerNumber = this.layerNumbers.get(this.layerNumberIndex);
            List<Durational> currentLayer = this.measure.getLayer(this.prevLayerNumber);
            this.prevPositionInLayer = this.positionInLayer;
            Durational next = currentLayer.get(this.prevPositionInLayer);
            
            ++this.positionInLayer;
            if(this.positionInLayer == currentLayer.size()) {
                ++this.layerNumberIndex;
                this.positionInLayer = 0;
            }
            
            return next;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Removing not supported.");
        }
    }
}
