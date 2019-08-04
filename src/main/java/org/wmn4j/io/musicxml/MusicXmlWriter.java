/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.io.ScoreWriter;
import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.elements.Score;

import java.util.Collection;

/**
 * Represents a writer for MusicXML files.
 */
public interface MusicXmlWriter extends ScoreWriter {

	/**
	 * Returns an instance of a writer for the given {@link Score}.
	 *
	 * @param score the score for which the writer is created
	 * @return an instance of a writer for the given {@link Score}
	 */
	static MusicXmlWriter writerFor(Score score) {
		return new MusicXmlScoreWriterDom(score);
	}

	/**
	 * Returns an instance of a writer for the given {@link Pattern}.
	 *
	 * @param pattern the pattern for which the writer is created
	 * @return an instance of a writer for the given {@link Pattern}
	 */
	static MusicXmlWriter writerFor(Pattern pattern) {
		return new MusicXmlPatternWriterDom(pattern);
	}

	/**
	 * Returns an instance of a writer for the given {@link Pattern} collection.
	 * <p>
	 * The patterns are written into MusicXML so that they are separated by
	 * double bar lines and system breaks from each other.
	 *
	 * @param patterns the patterns for which the writer is created
	 * @return an instance of a writer for the given {@link Pattern} collection
	 */
	static MusicXmlWriter writerFor(Collection<Pattern> patterns) {
		return new MusicXmlPatternWriterDom(patterns);
	}
}
