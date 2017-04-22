/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

import java.util.List;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Otso Björklund
 */
public class ScoreBuilderTest {
    
    public ScoreBuilderTest() {
    }

    @Test
    public void testBuildingScore() {
        ScoreBuilder builder = new ScoreBuilder();
        Map<Score.Attribute, String> attributes = ScoreTest.getTestAttributes();
        List<Part> parts = ScoreTest.getTestParts(5, 5);
        
        for(Score.Attribute attr : attributes.keySet())
            builder.setAttribute(attr, attributes.get(attr));
        
        for(Part part : parts)
            builder.addPart(part);
        
        Score score = builder.build();
        assertEquals(ScoreTest.scoreName, score.getName());
        assertEquals(ScoreTest.composerName, score.getAttribute(Score.Attribute.COMPOSER));
        
        assertEquals(5, score.getPartCount());
    }
}
