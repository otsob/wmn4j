/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.NoSuchElementException;

/**
 * Interface for iterators that iterate through the <code>Durational</code>
 * objects in a <code>Score</code>.
 * @author Otso Björklund
 */
public interface ScoreIterator {
    
    /**
     * Checks if the <code>ScoreIterator</code> is at the end of the score.
     * @return true if not at the end, false otherwise.
     */
    public boolean hasNext();
    
    /**
     * Get the next <code>Durational</code> from the score.
     * Order of iteration depends on the implementation.
     * @return next <code>Durational</code>.
     * @throws NoSuchElementException if next <code>Durational</code> is not available.
     */
    public Durational next() throws NoSuchElementException;
    
    /**
     * Returns the position of the <code>Durational</code> returned by the 
     * last call of {@link #next() next}. If next has not been called returns 
     * null.
     * @return <code>ScorePosition</code> of the previously returned <code>Durational</code>.
     */
    public ScorePosition positionOfPrevious();
}
