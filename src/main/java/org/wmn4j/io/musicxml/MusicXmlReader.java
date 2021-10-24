/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.io.ScoreReader;

import java.nio.file.Path;

/**
 * Represents a reader for MusicXML files.
 * <p>
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public interface MusicXmlReader extends ScoreReader {

	/**
	 * Returns a reader for the MusicXML file at the given path.
	 * The MusicXML file is validated against the schema.
	 *
	 * @param path the path of the file for which this reader is created
	 * @return a reader for the MusicXML file at the given path
	 */
	static MusicXmlReader readerFor(Path path) {
		return new StaxReader(path, true);
	}

	/**
	 * Returns a reader for the MusicXML file at the given path that does not
	 * validate the input against MusicXML schema.
	 *
	 * @param path the path of the file for which this reader is created
	 * @return a reader for the MusicXML file at the given path that skips validation
	 */
	static MusicXmlReader nonValidatingReaderFor(Path path) {
		return new StaxReader(path, false);
	}
}
