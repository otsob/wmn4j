/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

/**
 * Class that represents markings that span across multiple notes, such
 * as ties and slurs. Is immutable.
 * @author Otso Björklund
 */
public class MultiNoteArticulation {
    public enum Type { TIE, SLUR, GLISSANDO }
    
    private final Type type;

    private final ScorePosition target;
    
    public MultiNoteArticulation(Type type, ScorePosition target) {
        this.type = type;
        this.target = target;
    }
    
    public Type getType() {
        return type;
    }

    public ScorePosition getTarget() {
        return target;
    }
}
