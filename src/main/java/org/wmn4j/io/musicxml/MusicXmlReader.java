/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.io.ScoreReader;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a reader for MusicXML files.
 */
public interface MusicXmlReader extends ScoreReader {

	/**
	 * The path to the local MusicXML schema.
	 */
	Path SCHEMA = Paths.get("src/wmnlibio/musicxml/musicxml.xsd");

	/**
	 * Returns a reader object with the given configuration.
	 *
	 * @param validate specifies whether the returned reader should validate input
	 *                 files for complying to MusicXML schema
	 * @return a reader with the given configuration
	 */
	static MusicXmlReader getReader(boolean validate) {
		return new MusicXmlReaderDom(validate);
	}

}
