
package wmnkitnotation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Class that defines a measure.
 * @author Otso Björklund
 */
public class Measure implements Iterable<NotationElement> {
    
    public enum Barline { SINGLE, DOUBLE, REPEAT, FINAL }
    
    private final int number;
    private final List<List<NotationElement>> layers;
    private final MeasureInfo measureInfo;
    
    public static Measure getMeasure(int number, List<List<NotationElement>> noteLayers, TimeSignature timeSig, KeySignature keySig, Barline barline, Clef clef) {
        return getMeasure(number, noteLayers, MeasureInfo.getMeasureInfo(timeSig, keySig, barline, clef));
    }

    public static Measure getMeasure(int number, List<List<NotationElement>> noteLayers, TimeSignature timeSig, KeySignature keySig, Clef clef) {
        return getMeasure(number, noteLayers, MeasureInfo.getMeasureInfo(timeSig, keySig, Barline.SINGLE, clef));
    }
    
    public static Measure getMeasure(int number, List<List<NotationElement>> noteLayers, MeasureInfo measureInfo) {
        return new Measure(number, noteLayers, measureInfo);
    }
    
    public Measure(int number, List<List<NotationElement>> noteLayers, MeasureInfo measureInfo) {
        this.number = number;
        this.layers = noteLayers;
        
        this.measureInfo = measureInfo;
        
        if(this.layers == null || this.measureInfo == null)
            throw new NullPointerException();
        
        if(this.number <= 0)
            throw new IllegalArgumentException("Measure number must be positive");
    }
    
    public List<NotationElement> getLayer(int layer) {
        return Collections.unmodifiableList(this.layers.get(layer));
    }
    
    public int getNumberOfLayers() {
        return this.layers.size();
    }
    
    public int getNumber() {
        return this.number;
    }
    
    public TimeSignature getTimeSignature() {
        return this.measureInfo.getTimeSignature();
    }
    
    public KeySignature getKeySignature() {
        return this.measureInfo.getKeySignature();
    }
    
    public Barline getBarline() {
        return this.measureInfo.getBarline();
    }
    
    public Clef getClef() {
        return this.measureInfo.getClef();
    }
    
    @Override
    public String toString() {
        // Todo: add clef
        
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Measure ").append(this.number).append(", ")
                    .append(this.measureInfo).append(":\n");
        
        for(int i = 0; i < layers.size(); ++i) {
            strBuilder.append("Layer ").append(i).append(": ");
            for(int j = 0; j < layers.get(i).size(); ++j) {
                strBuilder.append(layers.get(i).get(j).toString());
                if(j != layers.get(i).size() - 1)
                    strBuilder.append(", ");
            }
            strBuilder.append("\n");
        }
        
        strBuilder.append("Barline:").append(getBarline()).append("\n");
        return strBuilder.toString();
    }

    @Override
    public Iterator<NotationElement> iterator() {
        class LayerWiseIterator implements Iterator<NotationElement> {
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
            public NotationElement next() {
                NotationElement next = null;
                
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
