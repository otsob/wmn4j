/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jio.musicxml;

import org.junit.Test;

import wmn4jnotation.TestHelper;
import wmn4jnotation.noteobjects.Score;

/**
 *
 * @author Otso Björklund
 */
public class MusicXmlWriterDomTest {

	public MusicXmlWriterDomTest() {
	}

	private Score readScore(String path) {
		return TestHelper.readScore(path);
	}

	@Test
	public void testWritingSingleC() {

	}

}
