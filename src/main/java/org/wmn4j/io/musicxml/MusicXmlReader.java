/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.io.ScoreReader;

import java.nio.file.Path;

/**
 * Represents a reader for MusicXML files.
 */
public interface MusicXmlReader extends ScoreReader {

	/**
	 * Returns a reader object with the given configuration.
	 *
	 * @param path     the path of the file for which this reader is created
	 * @param validate specifies whether the returned reader should validate input
	 *                 files for complying to MusicXML schema
	 * @return a reader with the given configuration
	 */
	static MusicXmlReader readerFor(Path path, boolean validate) {
		return new MusicXmlReaderDom(path, validate);
	}

}
