
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
    
    private final int number;
    private final List<List<NotationElement>> layers;
    
    public static Measure getMeasure(int number, List<List<NotationElement>> noteLayers) {
        return new Measure(number, noteLayers);
    }
    
    public Measure(int number, List<List<NotationElement>> noteLayers) {
        if(noteLayers == null)
            throw new NullPointerException();
        
        if(number <= 0)
            throw new IllegalArgumentException("Measure number must be positive");
        
        this.number = number;
        this.layers = noteLayers;
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
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Measure ").append(this.number).append(":\n");
        
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

    @Override
    public Iterator<NotationElement> iterator() {
        class LayerWiseIterator implements Iterator<NotationElement> {
            private int currentLayer = 0;
            private int positionInLayer = 0;
            
            @Override
            public boolean hasNext() {
                // If not on the last layer, then has next note
                if(currentLayer < Measure.this.layers.size() - 1)
                    return true;
            
                // If on last layer, then check that there are still notes left in the layer
                if(currentLayer == Measure.this.layers.size() - 1) {
                    return positionInLayer < Measure.this.layers.get(currentLayer).size();
                }
                
                return false;
            }

            @Override
            public NotationElement next() {
                NotationElement next = null;
                
                if(currentLayer < Measure.this.layers.size()) {
                    if(positionInLayer < Measure.this.layers.get(currentLayer).size()) {
                        next = Measure.this.layers.get(currentLayer).get(positionInLayer);
                        ++positionInLayer;
                        
                        if(positionInLayer == Measure.this.layers.get(currentLayer).size()) {
                            positionInLayer = 0;
                            ++currentLayer;
                        }
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
    
    /**
     * Iterates through the notes in a timewise manner, from the left of the measure to the right.
     * If NotationElements on different layers occur at the same time, note from layer with smallest number comes first.
     * 
     * @return Iterator
     */
    public Iterator<NotationElement> timeWiseIterator() {
        if(this.layers.size() == 1)
            return iterator();
        
        class TimeWiseIterator implements Iterator<NotationElement> {

            @Override
            public boolean hasNext() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public NotationElement next() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        }
        
        return new TimeWiseIterator();
    }
}
