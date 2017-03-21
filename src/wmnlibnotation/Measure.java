
package wmnlibnotation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Class that defines a measure.
 * This class is immutable. 
 * Use the MeasureBuilder class for easier creation of Measures.
 * @author Otso Björklund
 */
public class Measure implements Iterable<Durational> {
    
    private final int number;
    private final List<List<Durational>> layers;
    private final MeasureAttributes measureAttr;
    
    /**
     * @param number number of the measure.
     * @param noteLayers the notes on the different layers of the measure.
     * @param timeSig TimeSignature of the measure.
     * @param keySig KeySignature in effect in the measure.
     * @param rightBarLine barline on the right side (left side is normal SINGLE by default).
     * @param clef Clef in effect in the measure.
     */
    public Measure(int number, List<List<Durational>> noteLayers, TimeSignature timeSig, KeySignature keySig, Barline rightBarLine, Clef clef) {
        this(number, noteLayers, MeasureAttributes.getMeasureAttr(timeSig, keySig, rightBarLine, clef));
    }
    
    /**
     * @param number number of the measure.
     * @param noteLayers the notes on the different layers of the measure.
     * @param timeSig TimeSignature of the measure.
     * @param keySig KeySignature in effect in the measure.
     * @param clef Clef in effect in the measure.
     */
    public Measure(int number, List<List<Durational>> noteLayers, TimeSignature timeSig, KeySignature keySig, Clef clef) {
        this(number, noteLayers, MeasureAttributes.getMeasureAttr(timeSig, keySig, Barline.SINGLE, clef));
    }
    
    /**
     * @param number number of the measure.
     * @param noteLayers the notes on the different layers of the measure.
     * @param measureAttr the attributes of the measure.
     */
    public Measure(int number, List<List<Durational>> noteLayers, MeasureAttributes measureAttr) {
        this.number = number;
        this.layers = noteLayers;
        
        this.measureAttr = measureAttr;
        
        if(this.layers == null || this.measureAttr == null)
            throw new NullPointerException();
        
        if(this.number <= 0)
            throw new IllegalArgumentException("Measure number must be positive");
    }
    
    /**
     * Get a layer of the measure.
     * @param layer the index of the layer.
     * @return the layer at the given index layer.
     */
    public List<Durational> getLayer(int layer) {
        return Collections.unmodifiableList(this.layers.get(layer));
    }
    
    /**
     * @return number of layers in this measure.
     */
    public int getNumberOfLayers() {
        return this.layers.size();
    }
    
    /**
     * @return true if this measure only has one layer, false otherwise.
     */
    public boolean isSingleLayer() {
        return this.getNumberOfLayers() == 1;
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
     * String representation of <code>Measure</code>.
     * This is subject to change.
     * @return string representation of measure.
     */
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Measure ").append(this.number).append(", ")
                  .append(this.measureAttr).append(":\n");
        
        for(int i = 0; i < layers.size(); ++i) {
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
     * The iterator iterates through the notes in the Measure starting from lowest 
     * indexed layer and on each layer going from the earliest Durational in the layer to the last.
     * The iterator does not support removing.
     * @return iterator that goes through the Measure layerwise.
     */
    @Override
    public Iterator<Durational> iterator() {
        class LayerWiseIterator implements Iterator<Durational> {
            private int currentLayer = 0;
            private int positionInLayer = 0;
            
            @Override
            public boolean hasNext() {
                
                // Skip empty layers
                while(currentLayer < Measure.this.layers.size() && Measure.this.layers.get(currentLayer).isEmpty())
                    ++currentLayer;
                
                // Check that there are still layers left and there are notes in the layer.
                if(currentLayer < Measure.this.layers.size()) {
                    return positionInLayer < Measure.this.layers.get(currentLayer).size();
                }
                
                return false;
            }

            @Override
            public Durational next() {
                Durational next = null;
                
                if(hasNext()) {
                    next = Measure.this.layers.get(currentLayer).get(positionInLayer);
                    ++positionInLayer;

                    if(positionInLayer == Measure.this.layers.get(currentLayer).size()) {
                        positionInLayer = 0;
                        ++currentLayer;
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
