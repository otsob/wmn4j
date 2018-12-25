/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.wmn4j.io.musicxml.MusicXmlReader;
import org.wmn4j.notation.builders.ChordBuilder;
import org.wmn4j.notation.builders.MeasureBuilder;
import org.wmn4j.notation.builders.NoteBuilder;
import org.wmn4j.notation.builders.RestBuilder;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Score;

/**
 *
 * @author Otso Björklund
 */
public class TestHelper {

	public static final String TESTFILE_PATH = "src/test/resources/";

	private static final NoteBuilder C4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
	private static final NoteBuilder E4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
	private static final NoteBuilder G4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
	private static final NoteBuilder C4Quarter = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);

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

		final MusicXmlReader reader = MusicXmlReader.getReader(false);

		final Path path = Paths.get(TESTFILE_PATH, pathString);

		try {
			score = reader.readScore(path);
		} catch (final IOException e) {
			System.out.println("Failed to read score from " + path.toString() + " with exception: " + e);
		}

		return score;
	}
}
