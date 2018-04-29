/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Durational;

/**
 * Interface for builders that build <code>Durational</code> objects.
 * @author Otso Björklund
 */
public interface DurationalBuilder {
    
    /**
     * Create a <code>Durational</code> object with the values set in the builder.
     * @return a Durational object.
     */
    public Durational build();
    
    /**
     * Get the <code>Duration</code> set in the builder.
     * Each <code>DurationalBuilder</code> should always have at least a valid duration.
     * @return The <code>Duration</code> set in the builder.
     */
    public Duration getDuration();
}
