/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.io.ScoreWriter;
import org.wmn4j.notation.Score;

import java.nio.file.Path;

/**
 * Represents a writer for MusicXML files.
 * <p>
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public interface MusicXmlWriter extends ScoreWriter {

	/**
	 * Returns an instance of a writer for the given {@link Score} and path.
	 *
	 * @param score    the score for which the writer is created
	 * @param path     the path to which the MusicXML file is written
	 * @param compress set true to write compressed MusicXML, otherwise writes uncompressed
	 * @return an instance of a writer for the given {@link Score}
	 */
	static MusicXmlWriter writerFor(Score score, Path path, boolean compress) {
		return new StaxWriter(score, path, compress);
	}
}
