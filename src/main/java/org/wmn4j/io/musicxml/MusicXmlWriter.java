/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.elements.Score;

/**
 * Interface for MusicXML writers.
 */
public interface MusicXmlWriter {

	/**
	 * Returns an instance of a writer for the given {@link Score}.
	 *
	 * @param score the score for which the writer is created
	 * @return an instance of a writer for the given {@link Score}
	 */
	static MusicXmlWriter getWriter(Score score) {
		return new MusicXmlWriterDom(score);
	}

	/**
	 * Writes the {@link Score} with which this writer was initialized into a
	 * MusicXML file into the given path.
	 *
	 * @param path the output path for the MusicXML file
	 */
	void writeToFile(String path);

}
