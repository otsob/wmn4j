/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibmir.pattern_discovery;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import wmnlibio.musicxml.MusicXmlDomReader;
import wmnlibio.musicxml.MusicXmlReader;
import wmnlibnotation.Score;

/**
 *
 * @author Otso Björklund
 */
public class PointSetTest {
    
    public PointSetTest() {
    }
    
    @Before
    public void setUp() {
    }

    @Test
    public void testCreatingFromSingleStaffScore() {
        MusicXmlReader reader = new MusicXmlDomReader();
        try {
            Score score = reader.readScore("test/testfiles/musicxml/twoMeasures.xml");
            PointSet pointset = new PointSet(score);
            System.out.println(score);
            System.out.println(pointset);
            fail("This test is not implemented yet");
            
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }
    
}
