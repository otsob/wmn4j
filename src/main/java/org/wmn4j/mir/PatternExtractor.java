/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Measure;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Rest;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.access.MeasureIterator;
import org.wmn4j.notation.access.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Class for extracting a pattern from a score based on a given pattern position.
 */
final class PatternExtractor {
	private final Score score;
	private final PatternPosition patternPosition;
	private final int firstMeasureNumber;
	private final int lastMeasureNumber;

	PatternExtractor(Score score, PatternPosition patternPosition) {
		this.score = score;
		this.patternPosition = patternPosition;
		this.firstMeasureNumber = patternPosition.getMeasureNumbers().first();
		this.lastMeasureNumber = patternPosition.getMeasureNumbers().last();
	}

	Pattern extract() {
		final Map<Integer, List<Durational>> allPatternVoices = new HashMap<>();

		int partVoiceNumber = 1;

		for (Integer partIndex : patternPosition.getPartIndices()) {
			if (partIndex < 0 || partIndex >= score.getPartCount()) {
				throw new NoSuchElementException("Pattern position refers to part outside the score");
			}

			Map<StaffVoicePair, List<Durational>> voicesForPart = createPatternVoicesForPart(partIndex);
			for (StaffVoicePair voiceKey : voicesForPart.keySet()) {
				allPatternVoices.put(partVoiceNumber, voicesForPart.get(voiceKey));
				partVoiceNumber++;
			}
		}

		trimVoiceBeginnings(allPatternVoices);

		final String name = createName();

		if (allPatternVoices.keySet().size() == 1) {
			return Pattern.of(allPatternVoices.values().iterator().next(), name);
		}

		return Pattern.of(Collections.unmodifiableMap(allPatternVoices), name);
	}

	private String createName() {
		StringBuilder builder = new StringBuilder();
		builder.append("Measures: ").append(firstMeasureNumber).append("-").append(lastMeasureNumber);

		StringBuilder partsListBuilder = new StringBuilder();
		for (Integer partIndex : patternPosition.getPartIndices()) {
			partsListBuilder.append(score.getPart(partIndex).getName().orElse("")).append(" ");
		}

		String partsList = partsListBuilder.toString().trim();
		if (!partsList.isEmpty()) {
			builder.append("\nParts: ").append(partsList);
		}

		return builder.toString();
	}

	/*
	 * Trims the beginnings of the pattern voices to have as little rests as possible
	 * while keeping relative onset times of voices correct.
	 */
	private void trimVoiceBeginnings(Map<Integer, List<Durational>> allPatternVoices) {
		Duration durationToTrimFromBeginnings = null;

		// Find the voice with the smallest duration of rests in the
		// beginning.
		for (Integer voiceNumber : allPatternVoices.keySet()) {
			List<Duration> restDurationInBeginning = allPatternVoices.get(voiceNumber).stream()
					.takeWhile(Durational::isRest).map(Durational::getDuration).collect(
							Collectors.toList());

			// One of the voices has not rests at beginning, so there is nothing to trim.
			if (restDurationInBeginning.isEmpty()) {
				durationToTrimFromBeginnings = null;
				break;
			}

			Duration totalDuration = Duration.sum(restDurationInBeginning);
			if (durationToTrimFromBeginnings == null || durationToTrimFromBeginnings.isLongerThan(totalDuration)) {
				durationToTrimFromBeginnings = totalDuration;
			}
		}

		// Remove the duration from the beginning of each voice.
		if (durationToTrimFromBeginnings != null) {
			for (Integer voiceNumber : allPatternVoices.keySet()) {
				Duration amountOfRestToRemove = durationToTrimFromBeginnings;
				List<Durational> patternVoice = allPatternVoices.get(voiceNumber);

				int i = 0;
				while (amountOfRestToRemove != null) {
					Duration firstRestDuration = patternVoice.get(i).getDuration();

					if (firstRestDuration.isShorterThan(amountOfRestToRemove)) {
						amountOfRestToRemove = amountOfRestToRemove.subtract(firstRestDuration);
						i++;
					} else if (firstRestDuration.equals(amountOfRestToRemove)) {
						amountOfRestToRemove = null;
						i++;
					} else if (firstRestDuration.isLongerThan(amountOfRestToRemove)) {
						patternVoice.set(i, Rest.of(firstRestDuration.subtract(amountOfRestToRemove)));
						amountOfRestToRemove = null;
					}
				}

				allPatternVoices.put(voiceNumber, patternVoice.subList(i, patternVoice.size()));
			}
		}
	}

	private SortedMap<StaffVoicePair, List<Durational>> createPatternVoicesForPart(int partIndex) {
		final Part part = score.getPart(partIndex);
		final Set<Position> positionsInPart = new HashSet<>(patternPosition.getPositions(partIndex));
		final Set<Integer> staffNumbers = patternPosition.getStaffNumbers(partIndex);

		final Map<Integer, Map<Integer, Set<Integer>>> voicesInMeasuresPerStaff = createVoicesInMeasuresMappings(
				positionsInPart);
		final Map<Integer, Map<Integer, Integer>> voiceNumberMappingsPerStaff = createPatternVoiceMappings(
				voicesInMeasuresPerStaff);

		SortedMap<StaffVoicePair, List<Durational>> patternVoices = new TreeMap<>();

		for (int measureNumber = firstMeasureNumber;
			 measureNumber <= lastMeasureNumber; measureNumber++) {

			for (Integer staffNumber : staffNumbers) {
				final Measure measure = part.getMeasure(staffNumber, measureNumber);

				// The mapping used for mapping the voice numbers from the measures so that
				// non-overlapping notes can be combined in the pattern voices to be in the same
				// voice.
				final Map<Integer, Integer> voiceNumberMapping = voiceNumberMappingsPerStaff.get(staffNumber);
				final Map<Integer, Set<Integer>> voicesInMeasures = voicesInMeasuresPerStaff.get(staffNumber);

				boolean isMeasureReferencedInStaff = voicesInMeasures.containsKey(measureNumber);
				final int measureNumberForLambda = measureNumber;
				boolean isAnyVoiceOfMeasureReferenced = isMeasureReferencedInStaff
						&& measure.getVoiceNumbers().stream()
						.anyMatch(
								voiceNumber -> voicesInMeasures.get(measureNumberForLambda).contains(voiceNumber));

				if (isAnyVoiceOfMeasureReferenced) {
					addReferencedMeasureContentsToVoices(partIndex, positionsInPart, patternVoices, measureNumber,
							staffNumber, measure, voiceNumberMapping, voicesInMeasures);
				} else {
					addFullMeasureRests(voiceNumberMappingsPerStaff, patternVoices, staffNumber, measure);
				}
			}
		}

		if (!positionsInPart.isEmpty()) {
			throw new NoSuchElementException(
					"No elements for positions " + positionsInPart + " in part with index " + partIndex);
		}

		// Trim any trailing rests from the ends.
		for (List<Durational> patternVoice : patternVoices.values()) {
			while (!patternVoice.isEmpty() && patternVoice.get(patternVoice.size() - 1).isRest()) {
				patternVoice.remove(patternVoice.size() - 1);
			}
		}

		return patternVoices;
	}

	private void addReferencedMeasureContentsToVoices(int partIndex, Set<Position> positionsInPart,
			SortedMap<StaffVoicePair, List<Durational>> patternVoices, int measureNumber,
			Integer staffNumber, Measure measure, Map<Integer, Integer> voiceNumberMapping,
			Map<Integer, Set<Integer>> voicesInMeasures) {

		final MeasureIterator iterator = measure.getMeasureIterator();

		while (iterator.hasNext()) {
			final Durational durational = iterator.next();
			final int voiceInMeasure = iterator.getVoiceOfPrevious();

			// The voice of the durational is not referenced at all in this measure and can
			// be ignored.
			if (!voicesInMeasures.get(measureNumber).contains(voiceInMeasure)) {
				continue;
			}

			final int indexInVoice = iterator.getIndexOfPrevious();
			Position positionInMeasure = new Position(partIndex, staffNumber, measureNumber,
					voiceInMeasure, indexInVoice);

			int voiceInPattern = voiceNumberMapping.get(voiceInMeasure);
			StaffVoicePair key = new StaffVoicePair(staffNumber, voiceInPattern);
			patternVoices.putIfAbsent(key, new ArrayList<>());

			List<Durational> patternVoice = patternVoices.get(key);

			if (positionsInPart.contains(positionInMeasure)) {
				patternVoice.add(durational);
				positionsInPart.remove(positionInMeasure);
				if (positionsInPart.isEmpty()) {
					break;
				}
			} else if (durational instanceof Chord) {
				// Check if there are references to notes within the chord.
				Chord chord = (Chord) durational;
				List<Note> includedNotes = new ArrayList<>();

				for (int indexInChord = 0; indexInChord < chord.getNoteCount(); indexInChord++) {
					Position positionInChord = new Position(partIndex, staffNumber, measureNumber,
							voiceInMeasure, indexInVoice, indexInChord);

					if (positionsInPart.contains(positionInChord)) {
						includedNotes.add(chord.getNote(indexInChord));
						positionsInPart.remove(positionInChord);
					}
				}

				if (!includedNotes.isEmpty()) {
					patternVoice.add(Chord.of(includedNotes));
					if (positionsInPart.isEmpty()) {
						break;
					}
				}

			} else {
				patternVoice.add(Rest.of(durational.getDuration()));
			}
		}
	}

	private void addFullMeasureRests(Map<Integer, Map<Integer, Integer>> voiceNumberMappingsPerStaff,
			SortedMap<StaffVoicePair, List<Durational>> patternVoices, Integer staffNumber,
			Measure measure) {

		// Create set to avoid adding multiple full measure rests to the same voice number.
		Set<Integer> patternVoiceNumbers = new HashSet<>(
				voiceNumberMappingsPerStaff.get(staffNumber).values());

		for (Integer patternVoiceNumber : patternVoiceNumbers) {
			final StaffVoicePair key = new StaffVoicePair(staffNumber, patternVoiceNumber);
			patternVoices.putIfAbsent(key, new ArrayList<>());
			patternVoices.get(key).add(Rest.of(measure.getTimeSignature().getTotalDuration()));
		}
	}

	/*
	 * Returns a map that has as its keys the staff numbers and as values maps, that map measure number
	 * to the set of voice numbers that are referred to by the positions.
	 */
	private Map<Integer, Map<Integer, Set<Integer>>> createVoicesInMeasuresMappings(Set<Position> positionsInPart) {
		Map<Integer, Map<Integer, Set<Integer>>> measureVoicesPerStaff = new HashMap<>();

		for (Position position : positionsInPart) {
			final int staffNumber = position.getStaffNumber();
			measureVoicesPerStaff.putIfAbsent(staffNumber, new HashMap<>());
			Map<Integer, Set<Integer>> voicesInMeasure = measureVoicesPerStaff.get(staffNumber);

			final int measureNumber = position.getMeasureNumber();
			voicesInMeasure.putIfAbsent(measureNumber, new HashSet<>());
			voicesInMeasure.get(measureNumber).add(position.getVoiceNumber());
		}

		return measureVoicesPerStaff;
	}

	/*
	 * Returns for each staff the mapping of voice in measure to voice in pattern.
	 * This mapping is used for combining non-overlapping voices into same voices
	 * in the produced pattern.
	 */
	private Map<Integer, Map<Integer, Integer>> createPatternVoiceMappings(
			Map<Integer, Map<Integer, Set<Integer>>> measureVoicesPerStaff) {
		Map<Integer, Set<Integer>> allVoiceNumbersInStaff = new HashMap<>();

		for (Integer staffNumber : measureVoicesPerStaff.keySet()) {
			Set<Integer> voicesInAllMeasuresOfStaff = measureVoicesPerStaff.get(staffNumber).values().stream()
					.flatMap(Set::stream).collect(Collectors.toSet());

			allVoiceNumbersInStaff.put(staffNumber, voicesInAllMeasuresOfStaff);
		}

		Map<Integer, Map<Integer, Integer>> patternVoiceMappings = new HashMap<>();

		for (Integer staffNumber : measureVoicesPerStaff.keySet()) {
			patternVoiceMappings.putIfAbsent(staffNumber, new HashMap<>());

			final Map<Integer, Integer> voiceMappings = patternVoiceMappings.get(staffNumber);
			final List<Integer> voiceNumbers = new ArrayList<>(allVoiceNumbersInStaff.get(staffNumber));
			voiceNumbers.sort(Integer::compareTo);

			for (Integer voiceNumber : voiceNumbers) {
				voiceMappings.put(voiceNumber, voiceNumber);
			}

			final Collection<Set<Integer>> voiceNumbersForMeasures = measureVoicesPerStaff.get(staffNumber)
					.values();

			for (int i = 0; i < voiceNumbers.size(); ++i) {

				int targetVoiceNumber = voiceNumbers.get(i);

				for (int j = i + 1; j < voiceNumbers.size(); ++j) {
					boolean mergeToTargetVoice = true;
					int sourceVoiceNumber = voiceNumbers.get(j);

					for (Set<Integer> voiceNumbersInMeasure : voiceNumbersForMeasures) {
						if (voiceNumbersInMeasure.contains(targetVoiceNumber) && voiceNumbersInMeasure
								.contains(sourceVoiceNumber)) {
							mergeToTargetVoice = false;
							break;
						}
					}

					if (mergeToTargetVoice) {
						voiceMappings.put(sourceVoiceNumber, targetVoiceNumber);
					}
				}
			}

		}

		return patternVoiceMappings;
	}

	/*
	 * Used as key when creating pattern to ensure pattern voices are ordered
	 * so from smallest staff number to greatest and from smalles voice number to
	 * greatest within a part.
	 */
	private final class StaffVoicePair implements Comparable<StaffVoicePair> {
		private final int staffNumber;
		private final int voiceNumber;

		private StaffVoicePair(int staffNumber, int voiceNumber) {
			this.staffNumber = staffNumber;
			this.voiceNumber = voiceNumber;
		}

		@Override
		public int compareTo(StaffVoicePair o) {

			final int staffNumberComparison = Integer.compare(this.staffNumber, o.staffNumber);
			if (staffNumberComparison != 0) {
				return staffNumberComparison;
			}

			return Integer.compare(this.voiceNumber, o.voiceNumber);
		}

		@Override
		public boolean equals(Object o) {
			if (!(o instanceof StaffVoicePair)) {
				return false;
			}

			final StaffVoicePair other = (StaffVoicePair) o;

			return this.staffNumber == other.staffNumber && this.voiceNumber == other.voiceNumber;
		}

		@Override
		public int hashCode() {
			return Objects.hash(staffNumber, voiceNumber);
		}
	}
}
