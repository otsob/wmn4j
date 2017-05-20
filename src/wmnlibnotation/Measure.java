
package wmnlibnotation;

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
        SortedMap<Integer, List<Durational>> layersCopy = new TreeMap();
        
        for(Integer layerNum : noteLayers.keySet())
            layersCopy.put(layerNum, Collections.unmodifiableList(new ArrayList(noteLayers.get(layerNum))));
        
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
        return new ArrayList(this.layers.keySet());
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
     * Iterator for <code>Durational</code> objects in this <code>Measure</code>.
     * The iterator iterates through the notes in the Measure layer by layer
     * going from the earliest Durational in the layer to the last on each layer.
     * The order of layers is unspecified.
     * The iterator does not support removing.
     * @return iterator that goes through the Measure layerwise.
     */
    @Override
    public Iterator<Durational> iterator() {
        class LayerWiseIterator implements Iterator<Durational> {
            private List<Integer> layerNumbers = Measure.this.getLayerNumbers();
            private int layerNumberIndex = 0;
            private int positionInLayer = 0;
            
            @Override
            public boolean hasNext() {
                if(layerNumberIndex >= this.layerNumbers.size())
                    return false;
                
                int layerNumber = this.layerNumbers.get(this.layerNumberIndex);
                if(Measure.this.layers.get(layerNumber).isEmpty())
                    return false;
                
                return true;
            }

            @Override
            public Durational next() {
                Durational next = null;
                
                if(this.hasNext()) {
                    List<Durational> currentLayer = Measure.this.layers.get(this.layerNumbers.get(this.layerNumberIndex));
                    next = currentLayer.get(this.positionInLayer);
                    ++this.positionInLayer;
                    if(this.positionInLayer == currentLayer.size()) {
                        ++this.layerNumberIndex;
                        this.positionInLayer = 0;
                    }
                }
                else
                    throw new NoSuchElementException();
                
                return next;
            }
            
            @Override
            public void remove() {
                throw new UnsupportedOperationException("Removing not supported.");
            }
        }
        
        return new LayerWiseIterator();
    }
}
