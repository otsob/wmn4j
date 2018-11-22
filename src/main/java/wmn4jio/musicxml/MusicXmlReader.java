/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmn4jio.musicxml;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import wmn4jnotation.builders.ScoreBuilder;
import wmn4jnotation.noteobjects.Score;

/**
 * Interface for MusicXML readers.
 * 
 * @author Otso Björklund
 */
public interface MusicXmlReader {

	static final Path SCHEMA = Paths.get("src/wmnlibio/musicxml/musicxml.xsd");

	static MusicXmlReader getReader(boolean validate) {
		return new MusicXmlReaderDom(validate);
	}

	/**
	 * Reads the MusicXML file at the path specified by filePath and creates a
	 * <code>Score</code>.
	 * 
	 * @param filePath
	 *            Path of the MusicXML file.
	 * @return <code>Score</code> based on the contents of the MusicXML file.
	 * @throws IOException
	 *             If parsing the file does not succeed.
	 */
	public Score readScore(Path filePath) throws IOException;

	/**
	 * Reads the MusicXML file at the path specified by filePath and creates a
	 * <code>ScoreBuilder</code>. This is intended for the purposes of allowing
	 * modifications in memory.
	 * 
	 * @param filePath
	 *            Path of the MusicXML file.
	 * @return ScoreBuilder based on the contents of the MusicXML file.
	 * @throws IOException
	 *             If parsing the file does not succeed.
	 */
	public ScoreBuilder scoreBuilderFromFile(Path filePath) throws IOException;
}
