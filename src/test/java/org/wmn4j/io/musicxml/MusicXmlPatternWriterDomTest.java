package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wmn4j.io.ParsingFailureException;
import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.TestHelper;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class MusicXmlPatternWriterDomTest {

	@TempDir
	Path temporaryDirectory;

	private Score writeAndReadUsingWriter(MusicXmlWriter writer) {
		Path file = temporaryDirectory.resolve("file.xml");
		writer.write(file);

		final MusicXmlReader reader = MusicXmlReader.readerFor(file);
		Score writtenScore = null;

		try {
			writtenScore = reader.readScore();
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
		assertPartHasCorrectContents(expectedContents, part, expectedClef);
	}

	private void assertPartHasCorrectContents(List<Durational> expectedContents, Part part,
			Clef expectedClef) {

		assertFalse(part.isMultiStaff(),
				"Pattern voices should be written out as separate parts with single staff each.");

		List<Durational> partContents = new ArrayList<>();
		for (Measure measure : part) {
			assertEquals(1, measure.getVoiceCount());
			partContents.addAll(measure.getVoice(measure.getVoiceNumbers().get(0)));

			// Check the clef only if it has been specified
			if (expectedClef != null) {
				assertEquals(expectedClef, measure.getClef());
			}
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
			// Full measure rests are used for padding, so they should be ignored.
			if (!measure.isFullMeasureRest()) {
				partContents.addAll(measure.getVoice(measure.getVoiceNumbers().get(0)));
			}
		}

		assertEquals(expectedContents, partContents);
	}

	@Test
	void givenOneMultivoicePatternWhenPatternIsWrittenThenOutputHasCorrectContent() {
		final List<Durational> topVoiceContent = getPatternVoiceOnGClef();
		final List<Durational> bottomVoiceContent = getPatternVoiceOnFClef();

		Map<Integer, List<? extends Durational>> voices = new HashMap<>();
		voices.put(1, topVoiceContent);
		voices.put(2, bottomVoiceContent);

		final Pattern pattern = Pattern.of(voices);

		MusicXmlWriter writer = new MusicXmlPatternWriterDom(pattern);
		final Score patternsAsScore = writeAndReadUsingWriter(writer);

		assertEquals(2, patternsAsScore.getPartCount());

		Part topPart = patternsAsScore.getPart(0);
		assertPartHasCorrectContents(topVoiceContent, topPart, Clefs.G);

		Part bottomPart = patternsAsScore.getPart(1);
		assertPartHasCorrectContents(bottomVoiceContent, bottomPart, Clefs.F);
	}

	@Test
	void givenTwoMultivoicePatternsWhenPatternsAreWrittenThenOutputHasCorrectContent() {
		final List<Durational> pattern1TopVoiceContent = getPatternVoiceOnGClef();
		final List<Durational> pattern1BottomVoiceContent = getPatternVoiceOnFClef();

		Map<Integer, List<? extends Durational>> pattern1voices = new HashMap<>();
		pattern1voices.put(1, pattern1TopVoiceContent);
		pattern1voices.put(2, pattern1BottomVoiceContent);
		final Pattern pattern1 = Pattern.of(pattern1voices);

		final List<Durational> pattern2TopVoiceContent = getPatternVoiceOnFClef();
		final List<Durational> pattern2BottomVoiceContent = getPatternVoiceOnGClef();

		Map<Integer, List<? extends Durational>> pattern2voices = new HashMap<>();
		pattern2voices.put(1, pattern2TopVoiceContent);
		pattern2voices.put(2, pattern2BottomVoiceContent);
		final Pattern pattern2 = Pattern.of(pattern2voices);

		Collection<Pattern> patterns = Arrays.asList(pattern1, pattern2);

		MusicXmlWriter writer = new MusicXmlPatternWriterDom(patterns);
		final Score patternsAsScore = writeAndReadUsingWriter(writer);

		assertEquals(2, patternsAsScore.getPartCount());

		final List<Durational> expectedTopPartContent = new ArrayList<>(pattern1TopVoiceContent);
		expectedTopPartContent.addAll(pattern2TopVoiceContent);

		final List<Durational> expectedBottomPartContent = new ArrayList<>(pattern1BottomVoiceContent);
		expectedBottomPartContent.addAll(pattern2BottomVoiceContent);

		Part topPart = patternsAsScore.getPart(0);
		assertPartHasCorrectContents(expectedTopPartContent, topPart, null);

		Part bottomPart = patternsAsScore.getPart(1);
		assertPartHasCorrectContents(expectedBottomPartContent, bottomPart, null);
	}

	@Test
	void givenTwoMultivoicePatternsWhenPatternsAreWrittenThenOutputHasCorrectLayout() {
		List<Durational> singleNoteVoice = Arrays.asList(Note.of(Pitch.Base.C, 0, 4, Durations.QUARTER));

		Map<Integer, List<? extends Durational>> pattern1voices = new HashMap<>();
		pattern1voices.put(1, singleNoteVoice);
		pattern1voices.put(2, singleNoteVoice);
		final Pattern pattern1 = Pattern.of(pattern1voices);

		Map<Integer, List<? extends Durational>> pattern2voices = new HashMap<>();
		pattern2voices.put(1, singleNoteVoice);
		pattern2voices.put(2, singleNoteVoice);
		final Pattern pattern2 = Pattern.of(pattern2voices);

		Collection<Pattern> patterns = Arrays.asList(pattern1, pattern2);
		MusicXmlWriter writer = new MusicXmlPatternWriterDom(patterns);

		Path path = temporaryDirectory.resolve("temporary_file.xml");
		writer.write(path);

		Document musicXmlDocument = TestHelper.readDocument(path);

		final NodeList partNodes = musicXmlDocument.getElementsByTagName(MusicXmlTags.PART);
		for (int i = 0; i < partNodes.getLength(); ++i) {
			List<Node> measureNodes = DocHelper.findChildren(partNodes.item(i), MusicXmlTags.MEASURE);
			assertFalse(measureNodes.isEmpty());

			// Check that all time signatures are set invisible
			measureNodes.forEach(node -> {
				Optional<Node> attributes = DocHelper.findChild(node, MusicXmlTags.MEASURE_ATTRIBUTES);
				if (attributes.isPresent()) {
					Optional<Node> timeElement = DocHelper.findChild(attributes.get(), MusicXmlTags.MEAS_ATTR_TIME);
					if (timeElement.isPresent()) {
						Node printObjectNode = timeElement.get().getAttributes()
								.getNamedItem(MusicXmlTags.PRINT_OBJECT);
						assertNotNull(printObjectNode, "Missing print-object attribute in time signature");
						assertEquals(MusicXmlTags.NO, printObjectNode.getTextContent(),
								"print-object attribute of time signature has incorrect value");
					}
				}
			});

			// Check that new systems are started on pattern change
			Node secondMeasureNode = measureNodes.stream()
					.filter(node -> node.getAttributes().getNamedItem(MusicXmlTags.MEASURE_NUM).getTextContent()
							.equals("2")).findAny().orElseThrow();

			Optional<Node> printNode = DocHelper.findChild(secondMeasureNode, MusicXmlTags.PRINT);
			assertTrue(printNode.isPresent(),
					"Missing print element from measure that is expected to start new system");

			Node newSystemNode = printNode.get().getAttributes().getNamedItem(MusicXmlTags.NEW_SYSTEM);
			assertNotNull(newSystemNode);

			assertEquals(MusicXmlTags.YES, newSystemNode.getTextContent());
		}
	}
}
