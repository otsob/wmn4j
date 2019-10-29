/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j;

import org.w3c.dom.Document;
import org.wmn4j.io.musicxml.MusicXmlReader;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.fail;

public class TestHelper {

	public static final String TESTFILE_PATH = "src/test/resources/";

	private static final NoteBuilder C4 = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.HALF);
	private static final NoteBuilder E4 = new NoteBuilder(Pitch.of(Pitch.Base.E, 0, 4), Durations.HALF);
	private static final NoteBuilder G4 = new NoteBuilder(Pitch.of(Pitch.Base.G, 0, 4), Durations.HALF);
	private static final NoteBuilder C4Quarter = new NoteBuilder(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER);

	public static MeasureBuilder getTestMeasureBuilder(int number) {
		final MeasureBuilder builder = new MeasureBuilder(number);

		builder.addToVoice(0, C4Quarter);
		builder.addToVoice(0, new RestBuilder(Durations.QUARTER));
		final ChordBuilder chordBuilder = new ChordBuilder(C4);
		chordBuilder.add(E4).add(G4);
		builder.addToVoice(0, chordBuilder);

		builder.addToVoice(1, new RestBuilder(Durations.QUARTER));
		builder.addToVoice(1, C4);
		builder.addToVoice(1, new RestBuilder(Durations.QUARTER));

		return builder;
	}

	public static Measure getTestMeasure(int number) {
		return getTestMeasureBuilder(number).build();
	}

	public static Score readScore(String pathString) {
		Score score = null;

		final Path path = Paths.get(TESTFILE_PATH, pathString);
		final MusicXmlReader reader = MusicXmlReader.nonValidatingReaderFor(path);

		try {
			score = reader.readScore();
		} catch (final Exception e) {
			System.out.println("Failed to read score from " + path.toString() + " with exception: " + e);
		}

		return score;
	}

	public static Document readDocument(Path path) {
		try {
			final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			dbf.setNamespaceAware(true);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			final DocumentBuilder docBuilder = dbf.newDocumentBuilder();
			return docBuilder.parse(path.toFile());
		} catch (Exception e) {
			fail("Failed to open and parse document: " + e);
		}

		return null;
	}

	public static List<PartBuilder> getTestPartBuilders(int partCount, int measureCount) {
		final List<PartBuilder> partBuilders = new ArrayList<>();

		for (int p = 1; p <= partCount; ++p) {
			final PartBuilder partBuilder = new PartBuilder("Part" + p);
			for (int m = 1; m <= measureCount; ++m) {
				partBuilder.add(TestHelper.getTestMeasureBuilder(m));
			}

			partBuilders.add(partBuilder);
		}

		return partBuilders;
	}
}
