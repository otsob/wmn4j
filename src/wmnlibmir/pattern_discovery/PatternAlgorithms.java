/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir.pattern_discovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;

/**
 *
 * @author Otso Björklund
 */
public class PatternAlgorithms {
    
    // Implements SIATECH
    public static List<TEC> computeTecs(PointSet dataset) {
        
        dataset.sortLexicographically();
        Map<NoteEventVector, List<Pair<Integer, Integer>>> mtpMap = new HashMap<>();
        
        for(int i = 0; i < dataset.size() - 1; ++i) {
            
            NoteEventVector origin = dataset.get(i);
            
            for(int j = i + 1; j < dataset.size(); ++j) {
                NoteEventVector diff = dataset.get(j).subtract(origin);
                
                if(!mtpMap.containsKey(diff))
                    mtpMap.put(diff, new ArrayList<>());
                
                mtpMap.get(diff).add(new Pair<>(i, j));
            }
        }
        
        List<TEC> tecs = new ArrayList<>();
        Set<Pattern> patternVecs = new HashSet<>();
        
        for(NoteEventVector diff : mtpMap.keySet()) {
            List<Pair<Integer, Integer>> indexPairs = mtpMap.get(diff);
            
            List<NoteEventVector> patternPoints = new ArrayList<>();
            for(Pair<Integer, Integer> indexPair : indexPairs) {
                patternPoints.add(dataset.get(indexPair.getKey()));
            }
            
            Pattern pattern = new Pattern(patternPoints);
            Pattern vec = pattern.getVectorizedRepresentation();
            
            if(!patternVecs.contains(vec)) {
                List<NoteEventVector> translators = new ArrayList<>();
                if(pattern.getPoints().size() == 1) {
                    for(int i = 0; i < dataset.size(); ++i) {
                        translators.add(pattern.getPoints().get(0).subtract(dataset.get(i)));
                    }
                }
                else {
                    translators = findTranslators(pattern, mtpMap, dataset);
                }
                
                tecs.add(new TEC(pattern, translators));
                patternVecs.add(vec);
            }
        }
        
        return tecs;
    }
    
    public static List<NoteEventVector> findTranslators(Pattern pattern, Map<NoteEventVector, List<Pair<Integer, Integer>>> mtpMap, PointSet dataset) {
        List<Integer> pointIndices = new ArrayList<>();
        List<NoteEventVector> vecPatternPoints = pattern.getVectorizedRepresentation().getPoints();
        NoteEventVector vec = vecPatternPoints.get(0);
        
        for(Pair<Integer, Integer> indexPair : mtpMap.get(vec)) {
            pointIndices.add(indexPair.getValue());
        }
        for(int i = 1; i < vecPatternPoints.size(); ++i) {
            vec = vecPatternPoints.get(i);
            List<Pair<Integer, Integer>> indexPairs = mtpMap.get(vec);
            List<Integer> tmpPointIndices = new ArrayList<>();
            int j = 0;
            int k = 0;
            while(j < pointIndices.size() && k < indexPairs.size()) {
                if(pointIndices.get(j).equals(indexPairs.get(k).getKey())) {
                    tmpPointIndices.add(indexPairs.get(k).getValue());
                    ++j;
                    ++k;
                }
                else if(pointIndices.get(j) < indexPairs.get(k).getKey()) {
                    ++j;
                }
                else if(pointIndices.get(j) > indexPairs.get(k).getKey()) {
                    ++k;
                }
            }
            pointIndices = tmpPointIndices;
        }
        
        List<NoteEventVector> translators = new ArrayList<>();
        NoteEventVector lastPoint = pattern.getPoints().get(pattern.getPoints().size() - 1);
        
        for(Integer i : pointIndices) {
            translators.add(dataset.get(i).subtract(lastPoint));
        }
        
        return translators;
    }
    
}
