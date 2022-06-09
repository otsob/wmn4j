/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
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
	private final String name;
	private final SortedSet<String> labels;

	PolyphonicPattern(Map<Integer, List<? extends Durational>> voices, String name, Set<String> labels) {
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
					.noneMatch(durational -> durational.isChord());
			if (isMonophonic) {
				throw new IllegalArgumentException("Trying to create a polyphonic pattern with monophonic contents");
			}
		}

		this.name = name;
		this.labels = Collections.unmodifiableSortedSet(new TreeSet<>(labels));
	}

	PolyphonicPattern(Map<Integer, List<? extends Durational>> voices, String name) {
		this(voices, name, Collections.emptySet());
	}

	PolyphonicPattern(Map<Integer, List<? extends Durational>> voices) {
		this(voices, null, Collections.emptySet());
	}

	PolyphonicPattern(List<? extends Durational> voice, String name, Set<String> labels) {
		List<Durational> voiceCopy = Collections.unmodifiableList(new ArrayList<>(voice));
		if (voiceCopy.isEmpty()) {
			throw new IllegalArgumentException("Cannot create pattern from empty voice");
		}

		Map<Integer, List<Durational>> voicesCopy = new HashMap<>();
		voicesCopy.put(DEFAULT_VOICE_NUMBER, voiceCopy);
		voices = Collections.unmodifiableMap(voicesCopy);

		this.name = name;
		this.labels = Collections.unmodifiableSortedSet(new TreeSet<>(labels));
	}

	PolyphonicPattern(List<? extends Durational> voice) {
		this(voice, null, Collections.emptySet());
	}

	@Override
	public Optional<String> getName() {
		return Optional.ofNullable(name);
	}

	@Override
	public SortedSet<String> getLabels() {
		return labels;
	}

	@Override
	public boolean isMonophonic() {
		return false;
	}

	@Override
	public int getVoiceCount() {
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
	public boolean hasLabel(String label) {
		return labels.contains(label);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Pattern)) {
			return false;
		}

		return containsEqualVoices((Pattern) o, PolyphonicPattern::iterablesEquals);
	}

	static boolean iterablesEquals(Iterable<Durational> a, Iterable<Durational> b) {
		Iterator<Durational> iterA = a.iterator();
		Iterator<Durational> iterB = b.iterator();

		while (iterA.hasNext() && iterB.hasNext()) {
			if (!iterA.next().equals(iterB.next())) {
				return false;
			}
		}

		// Check that both iterators are at the end: the same number of elements has been iterated.
		return iterA.hasNext() == iterB.hasNext();
	}

	private boolean containsEqualVoices(Pattern other,
			BiFunction<Iterable<Durational>, Iterable<Durational>, Boolean> voiceEquality) {
		if (other.getVoiceCount() != getVoiceCount()) {
			return false;
		}

		for (List<Durational> voice : voices.values()) {
			boolean isVoicePresentInOther = false;

			for (Integer voiceNumber : other.getVoiceNumbers()) {
				if (voiceEquality.apply(voice, other.getVoice(voiceNumber))) {
					isVoicePresentInOther = true;
					break;
				}
			}

			if (!isVoicePresentInOther) {
				return false;
			}
		}

		for (Integer voiceNumber : other.getVoiceNumbers()) {
			final Iterable<Durational> voiceInOther = other.getVoice(voiceNumber);

			if (voices.values().stream().noneMatch(voice -> voiceEquality.apply(voice, voiceInOther))) {
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean equalsInPitch(Pattern other) {
		BiFunction<Iterable<Durational>, Iterable<Durational>, Boolean> equalInPitches = (voiceA, voiceB) ->
				isNoteContentEqualWithTransformation(voiceA, voiceB, Note::getPitch);

		return containsEqualVoices(other, equalInPitches);
	}

	@Override
	public Durational get(int voiceNumber, int index) {
		return voices.get(voiceNumber).get(index);
	}

	@Override
	public int getVoiceSize(int voiceNumber) {
		return voices.get(voiceNumber).size();
	}

	private static boolean isNoteContentEqualWithTransformations(Iterable<Durational> voiceA,
			Iterable<Durational> voiceB,
			Function<Note, Object> voiceANoteTransformation, Function<Note, Object> voiceBNoteTransformation) {

		List<Durational> voiceAWithoutRests = withoutRests(voiceA);
		List<Durational> voiceBWithoutRests = withoutRests(voiceB);

		if (voiceAWithoutRests.size() != voiceBWithoutRests.size()) {
			return false;
		}

		for (int i = 0; i < voiceAWithoutRests.size(); ++i) {
			Durational durationalA = voiceAWithoutRests.get(i);
			Durational durationalB = voiceBWithoutRests.get(i);

			if (durationalA.isChord() && durationalB.isChord()) {
				Chord chordA = durationalA.toChord();
				Chord chordB = durationalB.toChord();

				if (!areChordsEqualWithTransformedNotes(chordA, chordB, voiceANoteTransformation,
						voiceBNoteTransformation)) {
					return false;
				}
			} else if (durationalA.isNote() && durationalB.isNote()) {
				Note noteA = durationalA.toNote();
				Note noteB = durationalB.toNote();

				if (!voiceANoteTransformation.apply(noteA).equals(voiceBNoteTransformation.apply(noteB))) {
					return false;
				}
			} else {
				return false;
			}
		}

		return true;

	}

	private static boolean isNoteContentEqualWithTransformation(Iterable<Durational> voiceA,
			Iterable<Durational> voiceB,
			Function<Note, Object> noteTransformation) {
		return isNoteContentEqualWithTransformations(voiceA, voiceB, noteTransformation, noteTransformation);
	}

	private static List<Durational> withoutRests(Iterable<Durational> voice) {
		List<Durational> withoutRests = new ArrayList<>();
		for (Durational durational : voice) {
			if (!durational.isRest()) {
				withoutRests.add(durational);
			}
		}

		return withoutRests;
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
		BiFunction<Iterable<Durational>, Iterable<Durational>, Boolean> equalsEnharmonically = (voiceA, voiceB) ->
				isNoteContentEqualWithTransformation(voiceA, voiceB,
						note -> note.getPitch().orElseGet(note::getDisplayPitch).toInt());
		return containsEqualVoices(other, equalsEnharmonically);
	}

	@Override
	public boolean equalsTranspositionally(Pattern other) {

		// TODO: Reimplement this with PointPatterns

		Function<Durational, Integer> toPitchNumber = durational -> {
			if (durational.isNote()) {
				final Note note = durational.toNote();
				return note.getPitch().orElseGet(note::getDisplayPitch).toInt();
			}

			return ((Chord) durational).getLowestNote().getPitch().get().toInt();
		};

		// Get the first pitches available in this patterns voices. For chords get the lowest.
		Collection<Integer> firstPitchNumbersInThis = voices.values().stream()
				.map(voice -> voice.stream().filter(durational -> !durational.isRest()).findFirst())
				.filter(first -> first.isPresent())
				.map(Optional::get)
				.map(toPitchNumber).collect(Collectors.toList());

		// Get the first pitch number of one voice from other.

		Integer pitchNumberInOther = null;
		for (Durational durational : other.getVoice(other.getVoiceNumbers().get(0))) {
			if (!durational.isRest()) {
				pitchNumberInOther = toPitchNumber.apply(durational);
				break;
			}
		}

		// Based on the pitch differences, create all tranpositional equivalence comparisons for the
		// possible transposition candidates.
		Collection<BiFunction<Iterable<Durational>, Iterable<Durational>, Boolean>> transpositionalEquivalenceCandidates
				= new ArrayList<>();
		for (Integer firstPitchNumberInThis : firstPitchNumbersInThis) {
			final int transposition = pitchNumberInOther - firstPitchNumberInThis;
			transpositionalEquivalenceCandidates.add((voiceA, voiceB) ->
					isNoteContentEqualWithTransformations(voiceA, voiceB,
							note -> note.getPitch().get().toInt() + transposition,
							note -> note.getPitch().get().toInt()));
		}

		return transpositionalEquivalenceCandidates.stream()
				.anyMatch(transpositionallyEquals -> containsEqualVoices(other, transpositionallyEquals));
	}

	@Override
	public boolean equalsInDurations(Pattern other) {
		return containsEqualVoices(other, MonophonicPattern::areVoicesEqualInDurations);
	}

	@Override
	public int size() {
		return voices.values().stream().map(List::size).reduce(0, Integer::sum);
	}

	@Override
	public int hashCode() {
		return Objects.hash(voices);
	}

	@Override
	public Iterator<Durational> iterator() {
		List<Durational> iterable = new ArrayList<>();

		for (Integer voiceNumber : getVoiceNumbers()) {
			iterable.addAll(voices.get(voiceNumber));
		}

		return iterable.iterator();
	}
}
