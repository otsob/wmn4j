/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterates through a <code>Score</code> in part wise order.
 * Starts by iterating through the part with the smallest number.
 * Iterates through parts starting from smallest measure number.
 * Iterates through measure layer by layer.
 * @author Otso Björklund
 */
public class PartWiseScoreIterator implements ScoreIterator {

    private final Iterator<Part> scoreIterator;
    
    private Part.Iter currentPartIterator;
    private Measure.Iter currentMeasureIterator;
    private Part prevPart = null;
    private int prevPartIndex;
    private int prevStaffNumber;
    private int prevMeasureNumber;
    private int prevLayer;
    private int prevIndex;
    
    /**
     * @param score The score that this iterates through.
     */
    public PartWiseScoreIterator(Score score) {
        this.scoreIterator = score.iterator();
        // TODO: Consider a better way to handle iterating Parts and part index handling.
        this.prevPart = this.scoreIterator.next();
        this.prevPartIndex = 0;
        this.currentPartIterator = this.prevPart.getPartIterator();
        this.currentMeasureIterator = this.currentPartIterator.next().getMeasureIterator();
    }
    
    @Override
    public boolean hasNext() {
        return this.currentMeasureIterator.hasNext() || this.currentPartIterator.hasNext() || this.scoreIterator.hasNext();
    }

    @Override
    public Durational next() {
        if(!this.hasNext())
            throw new NoSuchElementException();
        
        if(!this.currentMeasureIterator.hasNext()) {
            if(!this.currentPartIterator.hasNext()) {
                this.prevPart = this.scoreIterator.next();
                ++this.prevPartIndex;
                this.currentPartIterator = this.prevPart.getPartIterator();
            }
            
            this.currentMeasureIterator = this.currentPartIterator.next().getMeasureIterator();
        }
        
        Durational next = this.currentMeasureIterator.next();
        this.prevStaffNumber = this.currentPartIterator.getStaffNumberOfPrevious();
        this.prevMeasureNumber = this.currentPartIterator.getMeasureNumberOfPrevious();
        this.prevLayer = this.currentMeasureIterator.getLayerOfPrevious();
        this.prevIndex = this.currentMeasureIterator.getIndexOfPrevious();
        
        return next;
    }
    
    @Override
    public ScorePosition positionOfPrevious() {
        if(this.prevPart == null)
            return null;
        
        return new ScorePosition(this.prevPartIndex, this.prevStaffNumber, this.prevMeasureNumber, 
                                    this.prevLayer, this.prevIndex);
    }
}
