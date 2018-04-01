/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

/**
 *
 * @author Otso Björklund
 */
public class ScorePosition {
    
    private final int partNumber;
    private final int staffNumber;
    private final int measureNumber;
    private final int layerNumber;
    private final int indexInLayer;
    
    /**
     * 
     * @param partNumber
     * @param staff
     * @param measure
     * @param layer
     * @param indexInLayer 
     */
    public ScorePosition(int partNumber, int staff, int measure, int layer, int indexInLayer) {
        
        this.partNumber = partNumber;
        this.staffNumber = staff;
        this.measureNumber = measure;
        this.layerNumber = layer;
        this.indexInLayer = indexInLayer;
    }
    
    public int getPartNumber() {
        return this.partNumber;
    }

    public int getStaffNumber() {
        return this.staffNumber;
    }

    public int getMeasureNumber() {
        return this.measureNumber;
    }

    public int getLayerNumber() {
        return this.layerNumber;
    }

    public int getIndexInLayer() {
        return this.indexInLayer;
    }
    
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        
        if(!(o instanceof ScorePosition))
            return false;
        
        ScorePosition other = (ScorePosition) o;
        
        // Part must be same instance.
        if(this.partNumber != other.getPartNumber())
            return false;
        
        // Staff number matters only for MultiStaffParts
        if(this.staffNumber != other.getStaffNumber())
            return false;
        
        if(other.getMeasureNumber() != this.measureNumber)
            return false;
        
        if(other.getLayerNumber() != this.layerNumber)
            return false;
        
        if(other.getIndexInLayer() != this.indexInLayer)
            return false;
        
        return true;
    }

    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append("Part: ").append(this.partNumber)
                    .append(", Staff: ").append(this.staffNumber)
                    .append(", Measure: ").append(this.measureNumber)
                    .append(", Layer: ").append(this.layerNumber)
                    .append(", Index: ").append(this.indexInLayer);
        
        return strBuilder.toString();
    }
}
