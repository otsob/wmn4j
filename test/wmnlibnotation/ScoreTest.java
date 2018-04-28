/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import wmnlibnotation.iterators.ScoreIterator;
import wmnlibnotation.iterators.PartWiseScoreIterator;
import wmnlibnotation.builders.PartBuilder;
import wmnlibnotation.noteobjects.Part;
import wmnlibnotation.noteobjects.Note;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.Score;
import wmnlibnotation.iterators.ScorePosition;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.Durational;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.junit.Test;
import static org.junit.Assert.*;
import wmnlibio.musicxml.MusicXmlReaderDom;

/**
 *
 * @author Otso Björklund
 */
public class ScoreTest {
    
    public static final String scoreName = "TestScore";
    public static final String composerName = "TestComposer";
    
    public ScoreTest() {
    }
    
    public static Map<Score.Attribute, String> getTestAttributes() {
        Map<Score.Attribute, String> attributes = new HashMap();
        attributes.put(Score.Attribute.NAME, scoreName);
        attributes.put(Score.Attribute.COMPOSER, composerName);
        return attributes;
    }
    
    public static List<Part> getTestParts(int partCount, int measureCount) {
        List<Part> parts = new ArrayList();
        
        for(int p = 1; p <= partCount; ++p) {
            PartBuilder partBuilder = new PartBuilder("Part" + p);
            for(int m = 1; m <= measureCount; ++m) {
                partBuilder.addMeasureBuilder(TestHelper.getTestMeasureBuilder(m));
            }
            
            parts.add(partBuilder.build());
        }
        
        return parts;
    }

    @Test
    public void testGetAttribute() {
        Score score = new Score(getTestAttributes(), getTestParts(5, 5));
        assertEquals(scoreName, score.getAttribute(Score.Attribute.NAME));
        assertEquals(composerName, score.getAttribute(Score.Attribute.COMPOSER));
        assertEquals("", score.getAttribute(Score.Attribute.ARRANGER));
    }
    
    @Test
    public void testImmutability() {
        Map<Score.Attribute, String> attributes = getTestAttributes();
        List<Part> parts = getTestParts(5, 5);
        
        Score score = new Score(attributes, parts);
        assertEquals("Number of parts was incorrect before trying to modify.", 5, score.getPartCount());
        parts.add(parts.get(0));
        assertEquals("Adding part to the list used for creating score changed score.", 5, score.getPartCount());
        
        assertEquals("Score name was incorrect before trying to modify", scoreName, score.getName());
        attributes.put(Score.Attribute.NAME, "ModifiedName");
        assertEquals("Score name was changed by modifying map used for creating score", scoreName, score.getName());
    
        List<Part> scoreParts = score.getParts();
        try {
            scoreParts.add(parts.get(0));
        } catch (Exception e) { /* Do nothing */ }
        assertEquals("Number of parts changed in score", 5, score.getPartCount());
    }
    
    @Test
    public void testIterator() {
        int partCount = 10;
        int measureCount = 10;
        Score score = new Score(getTestAttributes(), getTestParts(partCount, measureCount));
        
        int parts = 0;
        
        for(Part p : score) {
            assertEquals(measureCount, p.getMeasureCount());
            ++parts;
        }
    
        assertEquals("Iterated through a wrong number of parts", partCount, parts);
        
        Iterator<Part> iter = score.iterator();
        iter.next();
        try {
            iter.remove();
            fail("Iterator supports removing, immutability violated");
        }
        catch(Exception e) {
            assertTrue(e instanceof UnsupportedOperationException);
        }
    }
    
    @Test
    public void testGetAtPositionLimits() {
        MusicXmlReaderDom reader = new MusicXmlReaderDom();
        Score score = null;
        try {
            score = reader.readScore("test/testfiles/musicxml/scoreIteratorTesting.xml");
        } 
        catch(Exception e) {
            System.out.println(e);
            fail("Failed to read score");
        }
        
        try {
            score.getAtPosition(new ScorePosition(0, 1, 1, 5, 0));
            fail("Did not throw exception");
        } 
        catch (Exception e) {
            assertTrue("Exception: " + e, e instanceof NoSuchElementException);
        }
        
        // Test first note.
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER), score.getAtPosition(new ScorePosition(0, 1, 1, 1, 0)));
        
        // Test last note.
        assertEquals(Note.getNote(Pitch.getPitch(Pitch.Base.C, 0, 3), Durations.WHOLE), score.getAtPosition(new ScorePosition(1, 2, 3, 2, 0)));
    }
    
    @Test
    public void testIteratorAndGetAtPosition() {
        MusicXmlReaderDom reader = new MusicXmlReaderDom();
        Score score = null;
        try {
            score = reader.readScore("test/testfiles/musicxml/scoreIteratorTesting.xml");
        } 
        catch(Exception e) {
            System.out.println(e);
            fail("Failed to read score");
        }
        
        ScoreIterator iterator = new PartWiseScoreIterator(score);
        while(iterator.hasNext()) {
            Durational elem = iterator.next();
            ScorePosition position = iterator.positionOfPrevious();
            assertEquals(elem, score.getAtPosition(position));
        }
    }
}
