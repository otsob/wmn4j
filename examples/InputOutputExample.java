/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.io.musicxml.MusicXmlReader;
import org.wmn4j.io.musicxml.MusicXmlWriter;
import org.wmn4j.notation.Score;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This example shows how to read a {@link Score} from a MusicXML file and how to write a {@link Score}
 * to MusicXML.
 */
class InputOutputExample {
	public static void main(String[] args) {
		final Path inputPath = Paths.get(args[1]);
		final Path outputPath = Paths.get(args[2]);

		Score score = null;

		// Reading a score from a MusicXML file.
		// The reader implements closeable, so it can be used in a try-with.
		try (MusicXmlReader reader = MusicXmlReader.readerFor(inputPath)) {
			score = reader.readScore();
		} catch (IOException | ParsingFailureException e) {
			e.printStackTrace();
		}

		// Writing a score.
		// The writer also implements closeable, so it can be used in a try-with.
		try (MusicXmlWriter writer = MusicXmlWriter.writerFor(score, outputPath, false, false)) {
			writer.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
