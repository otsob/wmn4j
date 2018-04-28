/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import wmnlibnotation.noteobjects.Durational;

/**
 *
 * @author Otso Björklund
 */
public class Pattern {
    
    private final List<Durational> contents;
    
    public Pattern(List<Durational> contents) {
        this.contents = Collections.unmodifiableList(new ArrayList<>(contents));
    }

    public List<Durational> getContents() {
        return contents;
    }
    
    
}
