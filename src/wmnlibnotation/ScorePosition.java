/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

/**
 * Defines the position of a <code>Durational</code> in a <code>Score</code>.
 * Is immutable.
 * @author Otso Björklund
 */
public class ScorePosition {
    
    private final int partNumber;
    private final int staffNumber;
    private final int measureNumber;
    private final int layerNumber;
    private final int indexInLayer;
    
    /**
     * @param partNumber The number (index) of the part in the <code>Score</code>.
     * @param staffNumber The number of the staff in the <code>Part</code>. 
     * For <code>SingleStaffPart</code> objects use the constructor without the staffNumber parameter.
     * @param measureNumber The measure number.
     * @param layerNumber The layer number in the measure.
     * @param indexInLayer The index in the layer specified by layerNumber.
     */
    public ScorePosition(int partNumber, int staffNumber, int measureNumber, int layerNumber, int indexInLayer) {
        this.partNumber = partNumber;
        this.staffNumber = staffNumber;
        this.measureNumber = measureNumber;
        this.layerNumber = layerNumber;
        this.indexInLayer = indexInLayer;
    }

    /**
     * @param partNumber The number (index) of the part in the <code>Score</code>.
     * @param measureNumber The measure number.
     * @param layerNumber The layer number in the measure.
     * @param indexInLayer The index in the layer specified by layerNumber.
     */
    public ScorePosition(int partNumber, int measureNumber, int layerNumber, int indexInLayer) {
        this.partNumber = partNumber;
        this.staffNumber = SingleStaffPart.STAFF_NUMBER;
        this.measureNumber = measureNumber;
        this.layerNumber = layerNumber;
        this.indexInLayer = indexInLayer;
    }
    
    /**
     * @return The number (index) of the part in the score.
     */
    public int getPartNumber() {
        return this.partNumber;
    }

    /**
     * @return The number of the staff in the part.
     */
    public int getStaffNumber() {
        return this.staffNumber;
    }

    /**
     * @return The measure number specified by this position.
     */
    public int getMeasureNumber() {
        return this.measureNumber;
    }

    /**
     * @return The number of the layer in the measure.
     */
    public int getLayerNumber() {
        return this.layerNumber;
    }

    /**
     * @return The index of the <code>Durational</code> in the layer.
     */
    public int getIndexInLayer() {
        return this.indexInLayer;
    }
    
    /**
     * Compares this <code>ScorePosition</code> with equality against 
     * <code>Object o</code>. 
     * Two positions are equal if and only if all of their properties are 
     * equal.
     * @param o
     * @return 
     */
    @Override
    public boolean equals(Object o) {
        if(o == this)
            return true;
        
        if(!(o instanceof ScorePosition))
            return false;
        
        ScorePosition other = (ScorePosition) o;
        
        if(this.partNumber != other.getPartNumber())
            return false;
        
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
