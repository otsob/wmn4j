package org.wmn4j.io;

import java.nio.file.Path;

/**
 * Represents a writer for music notation files. The only supported file type is currently MusicXML.
 */
public interface ScoreWriter {

	/**
	 * Writes the contents in this writer into a file specified by the given path.
	 *
	 * @param path the path to which the contents in this writer are written
	 */
	void write(Path path);
}
