/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibio.musicxml;

import org.junit.Test;

import wmnlibnotation.TestHelper;
import wmnlibnotation.noteobjects.Score;

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
