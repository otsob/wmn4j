package org.wmn4j.io.musicxml;

import org.w3c.dom.Element;
import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Clefs;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.MeasureAttributes;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

final class MusicXmlPatternWriterDom extends MusicXmlWriterDom {

	private static final KeySignature DEFAULT_KEY_SIGNATURE = KeySignatures.CMAJ_AMIN;
	private static final Barline PATTERN_ENDING_BARLINE = Barline.DOUBLE;
	private static final Barline PATTERN_SEPARATING_BARLINE = Barline.INVISIBLE;
	private static final String DEFAULT_SCORE_TITLE = "Patterns";
	private static final String VOICE_NAME = "Voice";
	private static final String ABBREVIATED_VOICE_NAME = "V";
	private static final int MIDDLE_C_AS_INT = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4).toInt();

	private static final TimeSignature DEFAULT_TIME_SIGNATURE = TimeSignature.of(2, 1);
	private static final Duration MEASURE_CUTOFF_DURATION = DEFAULT_TIME_SIGNATURE.getTotalDuration();

	private final int divisions;
	private final List<Part> partsFromPatterns;
	private int nextMeasureNumber = 1;
	private final Set<Integer> newSystemBeginningMeasureNumbers;
	private final Map<Integer, String> annotations;

	MusicXmlPatternWriterDom(Collection<Pattern> patterns) {
		Collection<Durational> allDurationals = new ArrayList<>();
		patterns.forEach(pattern -> {
			for (Durational dur : pattern) {
				allDurationals.add(dur);
			}
		});

		this.divisions = computeDivisions(allDurationals.iterator());
		this.newSystemBeginningMeasureNumbers = new HashSet<>();
		this.annotations = new HashMap<>();
		this.partsFromPatterns = fromPatterns(patterns);
	}

	MusicXmlPatternWriterDom(Pattern pattern) {
		this(Collections.singletonList(pattern));
	}

	private List<Part> fromPatterns(Collection<Pattern> patterns) {

		int maxNumberOfVoices = patterns.stream().map(Pattern::getVoiceCount).max(Integer::compareTo).orElseThrow();

		if (maxNumberOfVoices == 1) {
			return Collections.singletonList(singleVoicePatternsToPart(patterns));
		}

		return multiVoicePatternsToParts(patterns, maxNumberOfVoices);
	}

	private Part singleVoicePatternsToPart(Collection<Pattern> patterns) {
		final List<Measure> allMeasures = new ArrayList<>();
		for (Pattern pattern : patterns) {
			List<Measure> measures = voiceToMeasureList(pattern.iterator(), true);
			allMeasures.addAll(measures);
			final Integer firstMeasureNumber = measures.get(0).getNumber();
			newSystemBeginningMeasureNumbers.add(firstMeasureNumber);
			annotations.put(firstMeasureNumber, createPatternAnnotation(pattern));
		}

		return SingleStaffPart.of("", Staff.of(allMeasures));
	}

	private List<Part> multiVoicePatternsToParts(Collection<Pattern> patterns, int maxNumberOfVoices) {

		// Create and initialize lists for the contents of staves
		List<List<Measure>> staveContents = new ArrayList<>(maxNumberOfVoices);
		for (int i = 0; i < maxNumberOfVoices; ++i) {
			staveContents.add(new ArrayList<>());
		}

		for (Pattern pattern : patterns) {
			final Duration longestVoiceDuration = getLongestVoiceDuration(pattern);

			final List<Integer> patternVoiceNumbers = pattern.getVoiceNumbers();
			final int measureNumberBeforeStaffCreation = nextMeasureNumber;

			for (int voiceIndex = 0; voiceIndex < patternVoiceNumbers.size(); ++voiceIndex) {
				final int voiceNumber = patternVoiceNumbers.get(voiceIndex);
				Iterable<Durational> voice = pattern.getVoice(voiceNumber);

				Duration voiceDuration = getVoiceDuration(voice, pattern.getVoiceSize(voiceNumber));

				staveContents.get(voiceIndex)
						.addAll(voiceToMeasureList(voice.iterator(), voiceDuration.equals(longestVoiceDuration)));

				nextMeasureNumber = measureNumberBeforeStaffCreation;
			}

			evenOutMeasureListLengthsAndUpdateNextMeasureNumber(staveContents);

			final Integer firstMeasureNumber = measureNumberBeforeStaffCreation;
			newSystemBeginningMeasureNumbers.add(firstMeasureNumber);
			annotations.put(firstMeasureNumber, createPatternAnnotation(pattern));
		}

		List<Part> parts = new ArrayList<>();

		for (List<Measure> staffContent : staveContents) {
			parts.add(SingleStaffPart.of("", Staff.of(staffContent)));
		}

		return parts;
	}

	private Duration getVoiceDuration(Iterable<Durational> voice, int voiceSize) {
		List<Duration> durations = new ArrayList<>(voiceSize);

		for (Durational durational : voice) {
			durations.add(durational.getDuration());
		}

		return Duration.sum(durations);
	}

	private Duration getLongestVoiceDuration(Pattern pattern) {
		List<Integer> patternVoiceNumbers = pattern.getVoiceNumbers();
		Duration longestVoiceDuration = null;

		for (int voiceIndex = 0; voiceIndex < patternVoiceNumbers.size(); ++voiceIndex) {
			final int voiceNumber = patternVoiceNumbers.get(voiceIndex);
			Duration totalDuration = getVoiceDuration(pattern.getVoice(voiceNumber), pattern.getVoiceSize(voiceNumber));

			if (longestVoiceDuration == null) {
				longestVoiceDuration = totalDuration;
			} else if (totalDuration.isLongerThan(longestVoiceDuration)) {
				longestVoiceDuration = totalDuration;
			}
		}

		return longestVoiceDuration;
	}

	private void evenOutMeasureListLengthsAndUpdateNextMeasureNumber(List<List<Measure>> staveContents) {
		int maxStaffContentLength = staveContents.stream().map(staffContent -> staffContent.size())
				.max(Integer::compareTo).orElseThrow();

		for (List<Measure> staffContent : staveContents) {
			final Clef clefForPaddingMeasures = !staffContent.isEmpty()
					? staffContent.get(staffContent.size() - 1).getClef()
					: Clefs.G;

			while (staffContent.size() < maxStaffContentLength) {
				int measureNumber = staffContent.size() + 1;
				Barline rightBarline = getBarline(measureNumber == maxStaffContentLength);
				staffContent.add(createPaddingMeasure(measureNumber, clefForPaddingMeasures, rightBarline));
			}
		}

		nextMeasureNumber = maxStaffContentLength + 1;
	}

	private Measure createPaddingMeasure(int measureNumber, Clef clef, Barline rightBarline) {
		MeasureAttributes attributes = MeasureAttributes
				.of(DEFAULT_TIME_SIGNATURE, DEFAULT_KEY_SIGNATURE, rightBarline, clef);

		return Measure.restMeasureOf(measureNumber, attributes);
	}

	private List<Measure> voiceToMeasureList(Iterator<Durational> voiceIterator, boolean useEndingBarline) {
		List<Measure> measures = new ArrayList<>();

		List<Durational> voice = new ArrayList<>();

		Duration cumulatedDuration = null;

		while (voiceIterator.hasNext()) {
			Durational durational = voiceIterator.next();
			voice.add(durational);

			cumulatedDuration = cumulatedDuration != null
					? durational.getDuration().add(cumulatedDuration)
					: durational.getDuration();

			if (cumulatedDuration.isLongerThan(MEASURE_CUTOFF_DURATION) || cumulatedDuration
					.equals(MEASURE_CUTOFF_DURATION)) {
				measures.add(
						createMeasure(voice, !voiceIterator.hasNext() && useEndingBarline, nextMeasureNumber));
				nextMeasureNumber++;
				cumulatedDuration = null;
				voice = new ArrayList<>();
			}
		}

		if (!voice.isEmpty()) {
			measures.add(createMeasure(voice, useEndingBarline, nextMeasureNumber));
			nextMeasureNumber++;
		}

		return measures;
	}

	private String createPatternAnnotation(Pattern pattern) {
		StringBuilder patternAnnotation = new StringBuilder();

		final Optional<String> patternName = pattern.getName();
		final Set<String> patternLabels = pattern.getLabels();

		if (patternName.isPresent()) {
			patternAnnotation.append(patternName.get());
		}

		if (!patternLabels.isEmpty()) {
			String separator = !patternName.isEmpty() ? " " : "";
			patternAnnotation.append(separator).append(pattern.getLabels());
		}

		return patternAnnotation.toString();
	}

	private Barline getBarline(boolean isEndingMeasureOfPattern) {
		return isEndingMeasureOfPattern ? PATTERN_ENDING_BARLINE : PATTERN_SEPARATING_BARLINE;
	}

	private Measure createMeasure(List<Durational> measureContent, boolean isLastMeasureOfPattern,
			int measureNumber) {

		MeasureAttributes attributes = MeasureAttributes
				.of(DEFAULT_TIME_SIGNATURE, DEFAULT_KEY_SIGNATURE, getBarline(isLastMeasureOfPattern),
						findSuitableClef(measureContent));

		return Measure.of(measureNumber, Collections.singletonMap(1, measureContent), attributes);
	}

	@Override
	protected int getDivisions() {
		return divisions;
	}

	@Override
	protected void writeScoreAttributes(Element rootElement) {
		final Element workTitleElement = getDocument().createElement(MusicXmlTags.SCORE_WORK_TITLE);
		workTitleElement.setTextContent(DEFAULT_SCORE_TITLE);

		final Element workElement = getDocument().createElement(MusicXmlTags.SCORE_WORK);
		workElement.appendChild(workTitleElement);
		rootElement.appendChild(workElement);

		rootElement.appendChild(createIdentificationElement());
	}

	@Override
	protected Element createIdentificationElement() {
		final Element identificationElement = getDocument().createElement(MusicXmlTags.SCORE_IDENTIFICATION);
		identificationElement.appendChild(createEncodingElement());
		return identificationElement;
	}

	@Override
	protected void writePartList(Element scoreRoot) {
		final Element partList = getDocument().createElement(MusicXmlTags.PART_LIST);

		for (int i = 0; i < partsFromPatterns.size(); ++i) {
			final String partNumberString = Integer.toString(i + 1);
			final String partId = "P" + partNumberString;
			addPartWithId(partId, partsFromPatterns.get(i));

			final Element partElement = getDocument().createElement(MusicXmlTags.PLIST_SCORE_PART);
			partElement.setAttribute(MusicXmlTags.PART_ID, partId);

			final Element partName = getDocument().createElement(MusicXmlTags.PART_NAME);
			partName.setTextContent(VOICE_NAME + " " + partNumberString);
			partElement.appendChild(partName);

			final Element abbreviatedPartName = getDocument().createElement(MusicXmlTags.PART_NAME_ABBREVIATION);
			abbreviatedPartName.setTextContent(ABBREVIATED_VOICE_NAME + " " + partNumberString);
			partElement.appendChild(abbreviatedPartName);

			partList.appendChild(partElement);
		}

		scoreRoot.appendChild(partList);
	}

	private Clef findSuitableClef(List<Durational> voice) {
		int sumOfPitch = 0;
		int pitchCount = 0;
		for (Durational durational : voice) {
			if (durational instanceof Note) {
				sumOfPitch += ((Note) durational).getPitch().toInt();
				pitchCount++;
			}

			if (durational instanceof Chord) {
				for (Note note : (Chord) durational) {
					sumOfPitch += note.getPitch().toInt();
					pitchCount++;
				}
			}
		}

		if (pitchCount == 0) {
			return Clefs.G;
		}

		final int averagePitch = sumOfPitch / pitchCount;
		if (averagePitch > MIDDLE_C_AS_INT) {
			return Clefs.G;
		}

		return Clefs.F;
	}

	@Override
	protected boolean showTimeSignature() {
		return false;
	}

	@Override
	protected boolean startNewSystem(Measure measure) {
		return newSystemBeginningMeasureNumbers.contains(measure.getNumber());
	}

	@Override
	protected String getAnnotation(Measure measure) {
		Integer measureNumber = measure.getNumber();
		if (annotations.containsKey(measureNumber)) {
			String annotation = annotations.get(measureNumber);
			annotations.remove(measureNumber);
			return annotation;
		}

		return "";
	}
}
