/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class that represents a polyphonic pattern. In a polyphonic pattern there can be
 * multiple voices and chords.
 */
final class PolyphonicPattern implements Pattern {

	private static final Integer DEFAULT_VOICE_NUMBER = 1;

	private final Map<Integer, List<Durational>> voices;

	PolyphonicPattern(Map<Integer, List<? extends Durational>> voices) {
		Map<Integer, List<Durational>> voicesCopy = new HashMap<>();

		for (Integer voiceNumber : voices.keySet()) {
			List<Durational> voiceContents = new ArrayList<>();
			voiceContents.addAll(voices.get(voiceNumber));
			if (voiceContents.isEmpty()) {
				throw new IllegalArgumentException("Cannot create pattern with empty voice");
			}
			voicesCopy.put(voiceNumber, Collections.unmodifiableList(voiceContents));
		}

		this.voices = Collections.unmodifiableMap(voicesCopy);
		if (this.voices.isEmpty()) {
			throw new IllegalArgumentException("Cannot create polyphonic pattern from empty voices");
		}
		if (this.voices.keySet().size() == 1) {
			final Integer voiceNumber = this.voices.keySet().iterator().next();
			final boolean isMonophonic = this.voices.get(voiceNumber).stream()
					.noneMatch(durational -> durational instanceof Chord);
			if (isMonophonic) {
				throw new IllegalArgumentException("Trying to create a polyphonic pattern with monophonic contents");
			}
		}
	}

	PolyphonicPattern(List<? extends Durational> voice) {
		List<Durational> voiceCopy = Collections.unmodifiableList(new ArrayList<>(voice));
		if (voiceCopy.isEmpty()) {
			throw new IllegalArgumentException("Cannot create pattern from empty voice");
		}

		Map<Integer, List<Durational>> voicesCopy = new HashMap<>();
		voicesCopy.put(DEFAULT_VOICE_NUMBER, voiceCopy);
		voices = Collections.unmodifiableMap(voicesCopy);
	}

	@Override
	public List<Durational> getContents() {
		return voices.values().stream().flatMap(voice -> voice.stream()).collect(Collectors.toList());
	}

	@Override
	public boolean isMonophonic() {
		return false;
	}

	@Override
	public int getNumberOfVoices() {
		return voices.size();
	}

	@Override
	public List<Integer> getVoiceNumbers() {
		List<Integer> voiceNumbers = new ArrayList<>(voices.keySet());
		voiceNumbers.sort(Integer::compareTo);
		return voiceNumbers;
	}

	@Override
	public List<Durational> getVoice(int voiceNumber) {
		return voices.get(voiceNumber);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Pattern)) {
			return false;
		}

		return containsEqualVoices((Pattern) o, List::equals);
	}

	private boolean containsEqualVoices(Pattern other,
			BiFunction<List<Durational>, List<Durational>, Boolean> voiceEquality) {
		if (other.getNumberOfVoices() != getNumberOfVoices()) {
			return false;
		}

		for (List<Durational> voice : voices.values()) {
			boolean isVoicePresentInOther = false;

			for (Integer voiceNumber : other.getVoiceNumbers()) {
				List<Durational> voiceInOther = other.getVoice(voiceNumber);
				if (voiceEquality.apply(voice, voiceInOther)) {
					isVoicePresentInOther = true;
					break;
				}
			}

			if (!isVoicePresentInOther) {
				return false;
			}
		}

		for (Integer voiceNumber : other.getVoiceNumbers()) {
			List<Durational> voiceInOther = other.getVoice(voiceNumber);

			if (voices.values().stream().noneMatch(voice -> voiceEquality.apply(voice, voiceInOther))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equalsInPitch(Pattern other) {
		BiFunction<List<Durational>, List<Durational>, Boolean> equalInPitches = (voiceA, voiceB) ->
				isNoteContentEqualWithTransformation(voiceA, voiceB, Note::getPitch);

		return containsEqualVoices(other, equalInPitches);
	}

	private static boolean isNoteContentEqualWithTransformations(List<Durational> voiceA, List<Durational> voiceB,
			Function<Note, Object> voiceANoteTransformation, Function<Note, Object> voiceBNoteTransformation) {

		List<Durational> voiceAWithoutRests = withoutRests(voiceA);
		List<Durational> voiceBWithoutRests = withoutRests(voiceB);

		if (voiceAWithoutRests.size() != voiceBWithoutRests.size()) {
			return false;
		}

		for (int i = 0; i < voiceAWithoutRests.size(); ++i) {
			Durational durationalA = voiceAWithoutRests.get(i);
			Durational durationalB = voiceBWithoutRests.get(i);

			if (durationalA instanceof Chord && durationalB instanceof Chord) {
				Chord chordA = (Chord) durationalA;
				Chord chordB = (Chord) durationalB;

				if (!areChordsEqualWithTransformedNotes(chordA, chordB, voiceANoteTransformation,
						voiceBNoteTransformation)) {
					return false;
				}
			} else if (durationalA instanceof Note && durationalB instanceof Note) {
				Note noteA = (Note) durationalA;
				Note noteB = (Note) durationalB;

				if (!voiceANoteTransformation.apply(noteA).equals(voiceBNoteTransformation.apply(noteB))) {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;

	}

	private static boolean isNoteContentEqualWithTransformation(List<Durational> voiceA, List<Durational> voiceB,
			Function<Note, Object> noteTransformation) {
		return isNoteContentEqualWithTransformations(voiceA, voiceB, noteTransformation, noteTransformation);
	}

	private static List<Durational> withoutRests(List<Durational> voice) {
		return voice.stream().filter(durational -> !durational.isRest()).collect(Collectors.toList());
	}

	private static boolean areChordsEqualWithTransformedNotes(Chord chordA, Chord chordB,
			Function<Note, Object> chordANoteTransformation, Function<Note, Object> chordBNoteTransformation) {

		if (chordA.getNoteCount() != chordB.getNoteCount()) {
			return false;
		}

		for (int i = 0; i < chordA.getNoteCount(); ++i) {
			Object transformedNoteA = chordANoteTransformation.apply(chordA.getNote(i));
			Object transformedNoteB = chordBNoteTransformation.apply(chordB.getNote(i));

			if (!transformedNoteA.equals(transformedNoteB)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equalsEnharmonically(Pattern other) {
		BiFunction<List<Durational>, List<Durational>, Boolean> equalsEnharmonically = (voiceA, voiceB) ->
				isNoteContentEqualWithTransformation(voiceA, voiceB, note -> note.getPitch().toInt());
		return containsEqualVoices(other, equalsEnharmonically);
	}

	@Override
	public boolean equalsTranspositionally(Pattern other) {

		Function<Durational, Integer> toPitchNumber = durational -> {
			if (durational instanceof Note) {
				return ((Note) durational).getPitch().toInt();
			}

			return ((Chord) durational).getLowestNote().getPitch().toInt();
		};

		// Get the first pitches available in this patterns voices. For chords get the lowest.
		Collection<Integer> firstPitchNumbersInThis = voices.values().stream()
				.map(voice -> voice.stream().filter(durational -> !durational.isRest()).findFirst())
				.filter(first -> first.isPresent())
				.map(Optional::get)
				.map(toPitchNumber).collect(Collectors.toList());

		// Get the first pitch number of one voice from other.
		Integer pitchNumberInOther = other.getVoice(other.getVoiceNumbers().get(0)).stream()
				.filter(durational -> !durational.isRest())
				.findFirst().map(toPitchNumber).orElseThrow();

		// Based on the pitch differences, create all tranpositional equivalence comparisons for the
		// possible transposition candidates.
		Collection<BiFunction<List<Durational>, List<Durational>, Boolean>> transpositionalEquivalenceCandidates
				= new ArrayList<>();
		for (Integer firstPitchNumberInThis : firstPitchNumbersInThis) {
			final int transposition = pitchNumberInOther - firstPitchNumberInThis;
			transpositionalEquivalenceCandidates.add((voiceA, voiceB) ->
					isNoteContentEqualWithTransformations(voiceA, voiceB,
							note -> note.getPitch().toInt() + transposition, note -> note.getPitch().toInt()));
		}

		return transpositionalEquivalenceCandidates.stream()
				.anyMatch(transpositionallyEquals -> containsEqualVoices(other, transpositionallyEquals));
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see wmnlibmir.Pattern#equalsInDurations(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInDurations(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(voices);
	}
}
