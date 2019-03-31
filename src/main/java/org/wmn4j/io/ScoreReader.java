/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io;

import org.wmn4j.notation.builders.ScoreBuilder;
import org.wmn4j.notation.elements.Score;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Represents a reader for music notation files. The only supported file type is MusicXML.
 */
public interface ScoreReader {

	/**
	 * Returns a score with the contents of the music notation file at the given path.
	 *
	 * @param filePath the path of the music notation file from which to read the contents of the score
	 * @return a score with the contents of the music notation file at the given path
	 * @throws IOException if the file is not found or the file is not valid
	 */
	Score readScore(Path filePath) throws IOException;

	/**
	 * Returns a score builder with the contents of the music notation file at the given path.
	 *
	 * @param filePath the path of the music notation file from which to read the contents of the score builder
	 * @return a score builder with the contents of the music notation file at the given path
	 * @throws IOException if the file is not found or the file is not valid
	 */
	ScoreBuilder scoreBuilderFromFile(Path filePath) throws IOException;
}
