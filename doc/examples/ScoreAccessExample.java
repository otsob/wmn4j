/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.io.musicxml.MusicXmlReader;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.access.Position;
import org.wmn4j.notation.access.Positional;
import org.wmn4j.notation.access.Selection;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This example shows how to access notation elements in a score.
 */
final class ScoreAccessExample {

	public static void main(String[] args) {
		final Path inputPath = Paths.get(args[0]);

		Score score = null;
		try (MusicXmlReader reader = MusicXmlReader.readerFor(inputPath)) {
			score = reader.readScore();
		} catch (IOException | ParsingFailureException e) {
			e.printStackTrace();
		}

		Position position = null;

		// Scores can be iterated multiple ways. This example shows how to
		// enumerate the durational elements in a score along their positions.
		for (Positional positional : score.enumeratePartwise()) {
			Durational durational = positional.durational();

			// Let's find the position of the first rest in the topmost part of
			// the score.
			if (durational.isRest()) {
				position = positional.position();
				break;
			}
		}

		// Position instance can be used for retrieving Durational notation elements from scores
		Durational firstRest = score.getAt(position);
		System.out.println(firstRest);

		// Selections can be used for limiting to just a part of the score,
		// for example only iterating the third and fourth measures of the score.
		Selection selection = score.toSelection().subSelection(3, 4);
		for (Durational durational : selection) {
			System.out.println(durational);
		}
	}

	private ScoreAccessExample() {
		// Not meant to be instantiated.
	}
}
