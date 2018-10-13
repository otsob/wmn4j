/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibio.musicxml;

import wmnlibnotation.noteobjects.Score;

/**
 *
 * @author Otso Björklund
 */
public interface MusicXmlWriter {

	static MusicXmlWriter getWriter(Score score) {
		return new MusicXmlWriterDom(score);
	}

	void writeToFile(String path);

}
