/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibmir.pattern_discovery;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import wmnlibnotation.Score;

/**
 *
 * @author Otso Björklund
 */
public class PointSet {
    
    private final List<NoteEventVector> points;
    
    public PointSet(List<NoteEventVector> points) {
        this.points = new ArrayList(points);
    }
    
    public PointSet(String csvFilePath) {
        this.points = this.pointsFromCsv(csvFilePath);
    }
    
    public PointSet(Score score) {
        this.points = this.pointsFromScore(score);
    }
    
    public void sortLexicographically() {
        this.points.sort(Comparator.naturalOrder());
    }
    
    public int size() {
        return this.points.size();
    } 
    
    public NoteEventVector get(int index) {
        return this.points.get(index);
    }
    
    private List<NoteEventVector> pointsFromCsv(String csvPath) {
        // TODO
        return new ArrayList();
    }
    
    private List<NoteEventVector> pointsFromScore(Score score) {
        // TODO
        return new ArrayList();
    }
}
