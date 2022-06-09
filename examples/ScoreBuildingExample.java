/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Clefs;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;
import org.wmn4j.notation.TimeSignature;

/**
 * This example shows how to use the builder classes when building a {@link Score}.
 */
public class ScoreBuildingExample {
	public static void main(String[] args) {
		// ScoreBuilder is the class for building a score programmatically.
		ScoreBuilder scoreBuilder = new ScoreBuilder();
		scoreBuilder.setAttribute(Score.Attribute.TITLE, "Score title");
		scoreBuilder.setAttribute(Score.Attribute.COMPOSER, "Example Composer");

		// Scores consist of parts that also have settable attributes
		PartBuilder partBuilder = new PartBuilder();
		partBuilder.setAttribute(Part.Attribute.NAME, "Part");

		// Create a builder for the first measure contents and
		// set some of its attributes.
		MeasureBuilder firstMeasure = new MeasureBuilder(1);
		firstMeasure.setClef(Clefs.F);
		firstMeasure.setTimeSignature(TimeSignature.of(3, 4));

		// Creating a note and a rest builder and adding them to voice 1 of the measure.
		NoteBuilder firstNote = new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Durations.HALF);
		firstMeasure.addToVoice(1, firstNote);
		firstMeasure.addToVoice(1, new RestBuilder(Durations.QUARTER));

		partBuilder.addToStaff(Part.DEFAULT_STAFF_NUMBER, firstMeasure);

		// Create second measure builder by copying attributes, such as clef, from first measure.
		MeasureBuilder secondMeasure = MeasureBuilder.withAttributesOf(firstMeasure).setNumber(2);
		NoteBuilder secondNote = new NoteBuilder(Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, 4), Durations.HALF);
		secondNote.addArticulation(Articulation.TENUTO);

		NoteBuilder thirdNote = new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Durations.QUARTER);
		thirdNote.addArticulation(Articulation.TENUTO);

		// Connect the notes with a slur
		secondNote.connectWith(Notation.of(Notation.Type.SLUR), thirdNote);

		secondMeasure.addToVoice(1, secondNote);
		secondMeasure.addToVoice(1, thirdNote);

		partBuilder.addToStaff(Part.DEFAULT_STAFF_NUMBER, secondMeasure);
		scoreBuilder.addPart(partBuilder);

		// Turn the builder into an immutable Score.
		Score score = scoreBuilder.build();
		System.out.println(score);
	}
}
