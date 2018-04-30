/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Rest;

/**
 * Class for building <code>Rest</code> objects.
 * @author Otso Björklund
 */
public class RestBuilder implements DurationalBuilder {

    private Duration duration;
    
    /**
     * Create a new instance.
     * @param duration The 
     */
    public RestBuilder(Duration duration) {
        if(duration == null)
            throw new NullPointerException("duration was null.");
        
        this.duration = duration;
    }
    
    /**
     * Set the duration of the <code>Rest</code>.
     * @param duration 
     */
    public void setDuration(Duration duration) {
        if(duration == null)
            throw new NullPointerException("duration was null.");
        
        this.duration = duration;
    }
    
    @Override
    public Duration getDuration() {
        return this.duration;
    }
    
    @Override
    public Rest build() {
        return Rest.getRest(this.duration);
    }
}