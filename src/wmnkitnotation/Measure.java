
package wmnkitnotation;

import java.util.Iterator;
import java.util.List;

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
        
        if(number <= 0)
            throw new IllegalArgumentException("Measure number must be positive");
        
        this.number = number;
        this.layers = noteLayers;
    }
    
    public List<NotationElement> getLayer(int layer) {
        return layers.get(layer);
    }
    
    public int getNumber() {
        return this.number;
    }
    
    @Override
    public String toString() {
        String contents = "Measure " + this.number + "\n";
        
        for(int i = 0; i < layers.size(); ++i) {
            contents += "Layer " + i + ": ";
            for(int j = 0; j < layers.get(i).size(); ++j) {
                contents += layers.get(i).get(j).toString() + ", ";
            }
        }
        
        return contents;
    }

    @Override
    public Iterator<NotationElement> iterator() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
