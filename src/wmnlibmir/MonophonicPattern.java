/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import wmnlibnotation.iterators.ScorePosition;
import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.Durational;

/**
 * A class for representing monophonic musical patterns.
 * In a monophonic pattern no notes occur simultaneously.
 * The pattern cannot contain chords and does not consist of multiple
 * layers. This class is immutable.
 * @author Otso Björklund
 */
public class MonophonicPattern implements Pattern {
    
    private final List<Durational> contents;
    private final List<PatternOccurrence> occurrences;
    
    public MonophonicPattern(List<Durational> contents) {
        this(contents, null);
    }
    
    public MonophonicPattern(List<Durational> contents, List<List<ScorePosition>> occurrences) {
        this.contents = Collections.unmodifiableList(new ArrayList<>(contents));
        if(this.contents == null)
            throw new NullPointerException("Cannot create pattern with null contents");
        if(this.contents.isEmpty())
            throw new IllegalArgumentException("Cannote create pattern with empty contents");
        if(this.contents.stream().anyMatch((dur) -> (dur instanceof Chord)))
            throw new IllegalArgumentException("Contents contain a Chord. Contents must be monophonic");
        
        if(occurrences == null || occurrences.isEmpty()) {
            this.occurrences = Collections.emptyList();
        }
        else {
            List<PatternOccurrence> occurrenceCopy = 
                    occurrences.stream().map((list) -> new PatternOccurrence(list)).collect(Collectors.toList());
            
            // TODO: Sort in ascending order of occurrence.
            
            this.occurrences = Collections.unmodifiableList(occurrenceCopy);
        }
    }
    
    public List<Durational> getContents() {
        return this.contents;
    }
    
    @Override
    public List<PatternOccurrence> getOccurrences() {
        return this.occurrences;
    }
    
    @Override
    public String toString() {
        StringBuilder strBuilder = new StringBuilder();
        for(Durational dur : this.contents)
            strBuilder.append(dur.toString());
        
        return strBuilder.toString();
    }
}
