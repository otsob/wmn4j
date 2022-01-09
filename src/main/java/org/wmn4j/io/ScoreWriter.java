/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io;

import java.io.Closeable;
import java.io.IOException;

/**
 * Represents a writer for music notation files. The only supported file type is currently MusicXML.
 * <p>
 * ScoreWriter implementations are expected to be single use: for writing separate scores,
 * separate instances need to be used.
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public interface ScoreWriter extends Closeable {

	/**
	 * Writes the contents in this writer into a file specified by the given path.
	 *
	 * @throws IOException in case the score cannot be written to the output
	 */
	void write() throws IOException;
}
