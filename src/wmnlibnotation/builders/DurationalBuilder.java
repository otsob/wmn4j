/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.Duration;
import wmnlibnotation.noteobjects.Durational;

/**
 *
 * @author Otso Björklund
 */
public interface DurationalBuilder {
    public Durational build();
    public Duration getDuration();
}
