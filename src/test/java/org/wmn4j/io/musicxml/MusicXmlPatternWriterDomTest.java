package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.Rest;
import org.wmn4j.notation.elements.Score;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class MusicXmlPatternWriterDomTest {

	@TempDir
	Path temporaryDirectory;

	private Score writeAndReadUsingWriter(MusicXmlWriter writer) {
		Path file = temporaryDirectory.resolve("file.xml");
		writer.writeToFile(file);

		final MusicXmlReader reader = MusicXmlReader.getReader(true);
		Score writtenScore = null;

		try {
			writtenScore = reader.readScore(file);
		} catch (final IOException | ParsingFailureException e) {
			fail("Reading score written by MusicXmlWriterDom failed with exception " + e);
		}

		assertNotNull(writtenScore);
		return writtenScore;
	}

	private final List<Durational> getPatternVoiceOnGClef() {
		List<Durational> voice = new ArrayList<>();
		voice.add(Note.of(Pitch.of(Pitch.Base.C, 0, 4), Durations.QUARTER));
		voice.add(Rest.of(Durations.QUARTER));
		voice.add(Note.of(Pitch.of(Pitch.Base.A, -1, 4), Durations.EIGHTH));
		voice.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.EIGHTH));
		voice.add(Chord.of(Note.of(Pitch.of(Pitch.Base.D, 0, 4), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.F, 1, 4), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.A, 0, 4), Durations.QUARTER)));
		voice.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.SIXTEENTH));
		voice.add(Note.of(Pitch.of(Pitch.Base.C, 0, 5), Durations.HALF));
		return voice;
	}

	private final List<Durational> getPatternVoiceOnFClef() {
		List<Durational> voice = new ArrayList<>();
		voice.add(Note.of(Pitch.of(Pitch.Base.C, 0, 2), Durations.QUARTER));
		voice.add(Rest.of(Durations.QUARTER));
		voice.add(Note.of(Pitch.of(Pitch.Base.A, -1, 2), Durations.EIGHTH));
		voice.add(Note.of(Pitch.of(Pitch.Base.C, 0, 3), Durations.EIGHTH));
		voice.add(Chord.of(Note.of(Pitch.of(Pitch.Base.D, 0, 2), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.F, 1, 2), Durations.QUARTER),
				Note.of(Pitch.of(Pitch.Base.A, 0, 2), Durations.QUARTER)));
		return voice;
	}

	@Test
	void givenSimplePatternOnGClefWhenPatternIsWrittenThenOutputFileContainsCorrectContent() {
		final List<Durational> expectedContents = getPatternVoiceOnGClef();

		MusicXmlWriter writer = new MusicXmlPatternWriterDom(Pattern.of(expectedContents));
		final Score patternsAsScore = writeAndReadUsingWriter(writer);

		assertPatternAsScoreHasCorrectContents(expectedContents, patternsAsScore, Clefs.G);
	}

	@Test
	void givenSimplePatternOnFClefWhenPatternIsWrittenThenOutputFileContainsCorrectContent() {
		final List<Durational> expectedContents = getPatternVoiceOnFClef();

		MusicXmlWriter writer = new MusicXmlPatternWriterDom(Pattern.of(expectedContents));
		final Score patternsAsScore = writeAndReadUsingWriter(writer);

		assertPatternAsScoreHasCorrectContents(expectedContents, patternsAsScore, Clefs.F);
	}

	private void assertPatternAsScoreHasCorrectContents(List<Durational> expectedContents, Score patternsAsScore,
			Clef expectedClef) {
		assertEquals(1, patternsAsScore.getPartCount());

		final Part part = patternsAsScore.getParts().get(0);
		assertFalse(part.isMultiStaff());

		List<Durational> partContents = new ArrayList<>();
		for (Measure measure : part) {
			assertEquals(1, measure.getVoiceCount());
			partContents.addAll(measure.getVoice(measure.getVoiceNumbers().get(0)));
			assertEquals(expectedClef, measure.getClef());
		}

		assertEquals(expectedContents, partContents);
	}

	@Test
	void givenTwoSimplePatternsWhenPatternsWrittenThenOutputFileContainsCorrectContent() {
		List<Pattern> patterns = new ArrayList<>();
		patterns.add(Pattern.of(getPatternVoiceOnGClef()));
		patterns.add(Pattern.of(getPatternVoiceOnFClef()));

		final List<Durational> expectedContents = new ArrayList<>();
		expectedContents.addAll(getPatternVoiceOnGClef());
		expectedContents.addAll(getPatternVoiceOnFClef());

		MusicXmlWriter writer = new MusicXmlPatternWriterDom(patterns);
		final Score patternsAsScore = writeAndReadUsingWriter(writer);

		assertEquals(1, patternsAsScore.getPartCount());

		final Part part = patternsAsScore.getParts().get(0);
		assertFalse(part.isMultiStaff());

		List<Durational> partContents = new ArrayList<>();
		for (Measure measure : part) {
			assertEquals(1, measure.getVoiceCount());
			partContents.addAll(measure.getVoice(measure.getVoiceNumbers().get(0)));
		}

		assertEquals(expectedContents, partContents);
	}
}
