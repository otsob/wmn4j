/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.builders.ScoreBuilder;
import org.wmn4j.notation.elements.Score;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Represents a reader for MusicXML files.
 */
public interface MusicXmlReader {

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

	/**
	 * Returns a score with the contents of the MusicXML file at the given path.
	 *
	 * @param filePath the path of the MusicXML file from which to read the contents of the score
	 * @return a score with the contents of the MusicXML file at the given path
	 * @throws IOException if the file is not found or the file is not valid
	 */
	Score readScore(Path filePath) throws IOException;

	/**
	 * Returns a score builder with the contents of the MusicXML file at the given path.
	 *
	 * @param filePath the path of the MusicXML file from which to read the contents of the score builder
	 * @return a score builder with the contents of the MusicXML file at the given path
	 * @throws IOException if the file is not found or the file is not valid
	 */
	ScoreBuilder scoreBuilderFromFile(Path filePath) throws IOException;
}
