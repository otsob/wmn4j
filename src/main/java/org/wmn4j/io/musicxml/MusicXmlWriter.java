/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.elements.Score;

import java.nio.file.Path;

/**
 * Represents a writer for MusicXML files.
 */
public interface MusicXmlWriter {

	/**
	 * Returns an instance of a writer for the given {@link Score}.
	 *
	 * @param score the score for which the writer is created
	 * @return an instance of a writer for the given {@link Score}
	 */
	static MusicXmlWriter getWriter(Score score) {
		return new MusicXmlScoreWriterDom(score);
	}

	/**
	 * Writes the {@link Score} with which this writer was initialized into a
	 * MusicXML file to the the given output path.
	 *
	 * @param path the output path for the MusicXML file
	 */
	void writeToFile(Path path);

}
