/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io;

import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;

import java.io.IOException;

/**
 * Represents a reader for music notation files. The only supported file type is MusicXML.
 * <p>
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public interface ScoreReader {

	/**
	 * Returns a score with the contents of the music notation file defined by the path set in this reader.
	 *
	 * @return aa score with the contents of the music notation file defined by the path set in this reader
	 * @throws IOException             if the file is not found or reading the file fails
	 * @throws ParsingFailureException if the file cannot be parsed
	 */
	Score readScore() throws IOException, ParsingFailureException;

	/**
	 * Returns a score builder with the contents of the music notation file defined by the path set in this reader.
	 *
	 * @return a score builder with the contents of the music notation file defined by the path set in this reader
	 * @throws IOException             if the file is not found or reading the file fails
	 * @throws ParsingFailureException if the file cannot be parsed
	 */
	ScoreBuilder readScoreBuilder() throws IOException, ParsingFailureException;
}
