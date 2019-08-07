package org.wmn4j.io.musicxml;

import org.w3c.dom.Element;
import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.KeySignature;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MeasureAttributes;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.SingleStaffPart;
import org.wmn4j.notation.elements.Staff;
import org.wmn4j.notation.elements.TimeSignature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

final class MusicXmlPatternWriterDom extends MusicXmlWriterDom {

	private static final KeySignature DEFAULT_KEY_SIGNATURE = KeySignatures.CMAJ_AMIN;
	private static final Barline PATTERN_ENDING_BARLINE = Barline.DOUBLE;
	private static final Barline PATTERN_SEPARATING_BARLINE = Barline.INVISIBLE;
	private static final String DEFAULT_SCORE_TITLE = "Patterns";
	private static final String VOICE_NAME = "Voice";
	private static final String ABBREVIATED_VOICE_NAME = "V";
	private static final int MIDDLE_C_AS_INT = Pitch.of(Pitch.Base.C, 0, 4).toInt();

	private static final TimeSignature DEFAULT_TIME_SIGNATURE = TimeSignature.of(2, 1);
	private static final Duration MEASURE_CUTOFF_DURATION = DEFAULT_TIME_SIGNATURE.getTotalDuration();

	private final int divisions;
	private final List<Part> partsFromPatterns;
	private int nextMeasureNumber = 1;
	private final Set<Integer> newSystemBeginningMeasureNumbers;

	MusicXmlPatternWriterDom(Collection<Pattern> patterns) {
		Collection<Durational> allDurationals = new ArrayList<>();
		patterns.forEach(pattern -> {
			allDurationals.addAll(pattern.getContents());
		});

		this.divisions = computeDivisions(allDurationals.iterator());
		this.newSystemBeginningMeasureNumbers = new HashSet<>();
		this.partsFromPatterns = fromPatterns(patterns);
	}

	MusicXmlPatternWriterDom(Pattern pattern) {
		this(Collections.singletonList(pattern));
	}

	private List<Part> fromPatterns(Collection<Pattern> patterns) {

		int maxNumberOfVoices = patterns.stream().map(Pattern::getNumberOfVoices).max(Integer::compareTo).orElseThrow();

		if (maxNumberOfVoices == 1) {
			return Collections.singletonList(singleVoicePatternsToPart(patterns));
		}

		return multiVoicePatternsToParts(patterns, maxNumberOfVoices);
	}

	private Part singleVoicePatternsToPart(Collection<Pattern> patterns) {
		final List<Measure> allMeasures = new ArrayList<>();
		for (Pattern pattern : patterns) {
			allMeasures.addAll(voiceToMeasureList(pattern.getContents().iterator(), true));
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
			final int indexOfLongestVoice = getIndexOfLongestVoiceInPattern(pattern);

			final List<Integer> patternVoiceNumbers = pattern.getVoiceNumbers();
			for (int voiceIndex = 0; voiceIndex < patternVoiceNumbers.size(); ++voiceIndex) {
				List<Durational> voice = pattern.getVoice(patternVoiceNumbers.get(voiceIndex));

				final int measureNumberBeforeStaffCreation = nextMeasureNumber;

				final boolean isLongestVoice = voiceIndex == indexOfLongestVoice;
				staveContents.get(voiceIndex)
						.addAll(voiceToMeasureList(voice.iterator(), isLongestVoice));

				nextMeasureNumber = measureNumberBeforeStaffCreation;
			}

			evenOutMeasureListLengthsAndUpdateNextMeasureNumber(staveContents);
		}

		List<Part> parts = new ArrayList<>();

		for (List<Measure> staffContent : staveContents) {
			parts.add(SingleStaffPart.of("", Staff.of(staffContent)));
		}

		return parts;
	}

	private int getIndexOfLongestVoiceInPattern(Pattern pattern) {
		List<Integer> patternVoiceNumbers = pattern.getVoiceNumbers();
		int indexOfLongestVoice = 0;
		double longestVoiceDuration = 0.0;

		for (int voiceIndex = 0; voiceIndex < patternVoiceNumbers.size(); ++voiceIndex) {
			List<Durational> voice = pattern.getVoice(patternVoiceNumbers.get(voiceIndex));
			double totalDuration = voice.stream().map(durational -> durational.getDuration().toDouble())
					.reduce(0.0, Double::sum);

			if (totalDuration > longestVoiceDuration) {
				longestVoiceDuration = totalDuration;
				indexOfLongestVoice = voiceIndex;
			}
		}

		return indexOfLongestVoice;
	}

	private void evenOutMeasureListLengthsAndUpdateNextMeasureNumber(List<List<Measure>> staveContents) {
		int maxStaffContentLength = staveContents.stream().map(staffContent -> staffContent.size())
				.max(Integer::compareTo).orElseThrow();

		for (List<Measure> staffContent : staveContents) {
			if (!staffContent.isEmpty()) {
				final Clef clefForPaddingMeasures = staffContent.get(staffContent.size() - 1).getClef();

				while (staffContent.size() < maxStaffContentLength) {
					int measureNumber = staffContent.size() + 1;
					Barline rightBarline = getBarline(measureNumber == maxStaffContentLength);
					staffContent.add(createPaddingMeasure(measureNumber, clefForPaddingMeasures, rightBarline));
				}
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

		newSystemBeginningMeasureNumbers.add(measures.get(0).getNumber());

		return measures;
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
}
