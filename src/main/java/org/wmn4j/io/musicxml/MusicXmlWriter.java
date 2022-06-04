/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.io.ScoreWriter;
import org.wmn4j.notation.Score;

import java.nio.file.Path;

/**
 * Represents a writer for MusicXML files.
 * The default MusicXML version for output is currently 4.0.
 * Only part-wise scores are currently supported.
 * <p>
 * Implementations of this interface are not guaranteed to be thread-safe.
 */
public interface MusicXmlWriter extends ScoreWriter {

	/**
	 * Returns an instance of a writer for the given {@link Score} and path.
	 * <p>
	 * The MusicXML output can be minified and compressed for very compact output.
	 * However, some incorrectly implemented MusicXML parsers may depend on even
	 * insignificant whitespace so minified MusicXML files might not work with all software.
	 *
	 * @param score    the score for which the writer is created
	 * @param path     the path to which the MusicXML file is written
	 * @param compress set true to write compressed MusicXML, otherwise writes uncompressed
	 * @param minify   set true to produce minified MusicXML with no unnecessary whitespace
	 * @return an instance of a writer for the given {@link Score}
	 */
	static MusicXmlWriter writerFor(Score score, Path path, boolean compress, boolean minify) {
		return new StaxWriter(score, path, compress, minify);
	}
}
