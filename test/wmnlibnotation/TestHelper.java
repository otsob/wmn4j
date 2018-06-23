/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;

import java.io.IOException;
import java.nio.file.Path;
import wmnlibio.musicxml.MusicXmlReader;
import wmnlibio.musicxml.MusicXmlReaderDom;
import wmnlibnotation.noteobjects.Pitch;
import wmnlibnotation.noteobjects.KeySignature;
import wmnlibnotation.noteobjects.Durations;
import wmnlibnotation.noteobjects.Measure;
import wmnlibnotation.noteobjects.KeySignatures;
import wmnlibnotation.builders.ChordBuilder;
import wmnlibnotation.builders.MeasureBuilder;
import wmnlibnotation.builders.NoteBuilder;
import wmnlibnotation.builders.RestBuilder;
import wmnlibnotation.noteobjects.Score;

/**
 *
 * @author Otso Björklund
 */
public class TestHelper {
	private static final KeySignature keySig = KeySignatures.CMAJ_AMIN;

	private static final NoteBuilder C4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.HALF);
	private static final NoteBuilder E4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.E, 0, 4), Durations.HALF);
	private static final NoteBuilder G4 = new NoteBuilder(Pitch.getPitch(Pitch.Base.G, 0, 4), Durations.HALF);
	private static final NoteBuilder C4Quarter = new NoteBuilder(Pitch.getPitch(Pitch.Base.C, 0, 4), Durations.QUARTER);

	public static MeasureBuilder getTestMeasureBuilder(int number) {
		MeasureBuilder builder = new MeasureBuilder(number);

		builder.addToLayer(0, C4Quarter);
		builder.addToLayer(0, new RestBuilder(Durations.QUARTER));
		ChordBuilder chordBuilder = new ChordBuilder(C4);
		chordBuilder.add(E4).add(G4);
		builder.addToLayer(0, chordBuilder);

		builder.addToLayer(1, new RestBuilder(Durations.QUARTER));
		builder.addToLayer(1, C4);
		builder.addToLayer(1, new RestBuilder(Durations.QUARTER));

		return builder;
	}

	public static Measure getTestMeasure(int number) {
		return getTestMeasureBuilder(number).build();
	}

	public static Score readScore(Path path) {
		Score score = null;

		MusicXmlReader reader = new MusicXmlReaderDom();

		try {
			score = reader.readScore(path);
		} catch (IOException e) {
			System.out.println("Failed to read score with exception: " + e);
		}

		return score;
	}
}
