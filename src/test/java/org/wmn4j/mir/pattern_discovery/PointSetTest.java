/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.pattern_discovery;

import static org.junit.Assert.fail;

import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.wmn4j.io.musicxml.MusicXmlReader;
import org.wmn4j.mir.pattern_discovery.PointSet;
import org.wmn4j.notation.elements.Score;

/**
 *
 * @author Otso Björklund
 */
@Ignore("These tests are unfinished and should be unignored once the logic is implemented")
public class PointSetTest {

	public PointSetTest() {
	}

	@Before
	public void setUp() {
	}

	@Test
	public void testCreatingFromSingleStaffScore() {
		MusicXmlReader reader = MusicXmlReader.getReader(false);
		try {
			Score score = reader.readScore(Paths.get("src/test/resources/musicxml/twoMeasures.xml"));
			PointSet pointset = new PointSet(score);
			System.out.println(score);
			System.out.println(pointset);
			fail("This test is not implemented yet");

		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
