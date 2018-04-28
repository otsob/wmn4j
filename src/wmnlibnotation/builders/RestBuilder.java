/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Rest;

/**
 *
 * @author Otso Björklund
 */
public class RestBuilder implements DurationalBuilder {

    private Duration duration;
    
    public RestBuilder(Duration duration) {
        this.duration = duration;
    }
    
    public void setDuration(Duration duration) {
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
