/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.io.musicxml.MusicXmlReader;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.access.Position;
import org.wmn4j.notation.access.PositionalIterator;
import org.wmn4j.notation.access.Selection;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This example shows how to access notation elements in a score.
 */
public class ScoreAccessExample {

	public static void main(String[] args) {
		final Path inputPath = Paths.get(args[1]);

		Score score = null;
		try (MusicXmlReader reader = MusicXmlReader.readerFor(inputPath)) {
			score = reader.readScore();
		} catch (IOException | ParsingFailureException e) {
			e.printStackTrace();
		}

		Position position = null;

		// Scores can be iterated using iterators. Positional iterator
		// also offers a method for getting the position of the durational
		// notation element that was returned on the previous call of next.
		PositionalIterator iter = score.partwiseIterator();
		while (iter.hasNext()) {
			Durational durational = iter.next();

			// Let's find the position of the first rest in the topmost part of
			// the score.
			if (durational.isRest()) {
				position = iter.getPositionOfPrevious();
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
}
