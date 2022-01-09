/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io;

/**
 * Represents a writer for music notation files. The only supported file type is currently MusicXML.
 * <p>
 * ScoreWriter implementations are expected to be single use: for writing separate scores,
 * separate instances need to be used.
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public interface ScoreWriter {

	/**
	 * Writes the contents in this writer into a file specified by the given path.
	 */
	void write();
}
