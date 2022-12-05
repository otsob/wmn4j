/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.Barline;
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
import org.wmn4j.notation.Score;
import org.wmn4j.notation.SingleStaffPart;
import org.wmn4j.notation.Staff;
import org.wmn4j.notation.TimeSignature;
import org.wmn4j.notation.access.Offset;
import org.wmn4j.notation.directions.Direction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

class PatternsToScoreConverter {

	private static final KeySignature DEFAULT_KEY_SIGNATURE = KeySignatures.CMAJ_AMIN;
	private static final Barline PATTERN_ENDING_BARLINE = Barline.DOUBLE;
	private static final Barline PATTERN_SEPARATING_BARLINE = Barline.INVISIBLE;
	private static final String DEFAULT_SCORE_TITLE = "Patterns";
	private static final String VOICE_NAME = "Voice";
	private static final int MIDDLE_C_AS_INT = Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4).toInt();

	private static final TimeSignature DEFAULT_TIME_SIGNATURE = TimeSignature.of(2, 1);
	private static final Duration MEASURE_CUTOFF_DURATION = DEFAULT_TIME_SIGNATURE.getTotalDuration();

	private int nextMeasureNumber = 1;
	private final Collection<Pattern> patterns;

	PatternsToScoreConverter(Collection<Pattern> patterns) {
		this.patterns = patterns;
	}

	Score convert() {
		List<Part> parts = fromPatterns(patterns);
		Map<Score.Attribute, String> attributes = new HashMap<>();
		attributes.put(Score.Attribute.TITLE, DEFAULT_SCORE_TITLE);
		return Score.of(attributes, parts);
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
			List<Measure> measures = voiceToMeasureList(pattern.iterator(), createPatternAnnotation(pattern), true);
			allMeasures.addAll(measures);
		}

		return SingleStaffPart.of(VOICE_NAME + "-1", Staff.of(allMeasures));
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

				String annotation = "";
				if (voiceIndex == 0) {
					annotation = createPatternAnnotation(pattern);
				}

				staveContents.get(voiceIndex)
						.addAll(voiceToMeasureList(voice.iterator(), annotation,
								voiceDuration.equals(longestVoiceDuration)));

				nextMeasureNumber = measureNumberBeforeStaffCreation;
			}

			evenOutMeasureListLengthsAndUpdateNextMeasureNumber(staveContents);
		}

		List<Part> parts = new ArrayList<>();
		int voice = 1;

		for (List<Measure> staffContent : staveContents) {
			parts.add(SingleStaffPart.of(VOICE_NAME + " " + voice++, Staff.of(staffContent)));
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

		return Measure.restMeasureOf(measureNumber, attributes, null);
	}

	private List<Measure> voiceToMeasureList(Iterator<Durational> voiceIterator, String annotation,
			boolean useEndingBarline) {
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
						createMeasure(voice, annotation, !voiceIterator.hasNext() && useEndingBarline,
								nextMeasureNumber));
				nextMeasureNumber++;
				cumulatedDuration = null;
				voice = new ArrayList<>();
			}
		}

		if (!voice.isEmpty()) {
			measures.add(createMeasure(voice, annotation, useEndingBarline, nextMeasureNumber));
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

	private Measure createMeasure(List<Durational> measureContent, String annotation, boolean isLastMeasureOfPattern,
			int measureNumber) {

		final List<Offset<Direction>> directions = new ArrayList<>();
		if (annotation != null && !annotation.isBlank()) {
			directions.add(new Offset<>(Direction.of(Direction.Type.TEXT, annotation), null));
		}

		MeasureAttributes attributes = MeasureAttributes
				.of(DEFAULT_TIME_SIGNATURE, DEFAULT_KEY_SIGNATURE, getBarline(isLastMeasureOfPattern), Barline.NONE,
						findSuitableClef(measureContent), null, directions);

		return Measure.of(measureNumber, Collections.singletonMap(1, measureContent), attributes, null);
	}

	private Clef findSuitableClef(List<Durational> voice) {
		int sumOfPitch = 0;
		int pitchCount = 0;
		for (Durational durational : voice) {
			if (durational.isNote()) {
				final var pitch = durational.toNote().getPitch();
				if (pitch.isPresent()) {
					sumOfPitch += pitch.get().toInt();
					pitchCount++;
				}
			}

			if (durational.isChord()) {
				for (Note note : durational.toChord()) {
					final var pitch = note.getPitch();
					if (pitch.isPresent()) {
						sumOfPitch += pitch.get().toInt();
						pitchCount++;
					}
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
}
