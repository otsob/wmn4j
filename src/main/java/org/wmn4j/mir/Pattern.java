/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;

/**
 * Interface for musical patterns. The patterns can represent motifs, melodies,
 * or segments of polyphonic music. A pattern is a sequence of notes, chords,
 * and rests. A pattern can be monophonic or polyphonic. In a monophonic pattern
 * there are no simultaneously occurring notes or chords. In a polyphonic
 * pattern there can be multiple notes occurring at the same time in chords or
 * in multiple voices.
 * <p>
 * The notation elements in monophonic pattern are iterated in temporal orders.
 * For polyphonic patterns the elements are iterated first by iterating through the
 * voice with the smallest number and then moving on the voice with the next greatest
 * number and so on.
 * <p>
 * Implementations of this interface are required to be thread-safe.
 */
public interface Pattern extends Iterable<Durational> {

	/**
	 * Returns a pattern with the given contents.
	 *
	 * @param contents non-empty list containing the notation elements of the pattern in temporal order
	 * @return a pattern with the given contents
	 */
	static Pattern of(List<? extends Durational> contents) {
		return of(contents, null);
	}

	/**
	 * Returns a polyphonic pattern with the given voices.
	 *
	 * @param voices the voices of the pattern
	 * @param name   the name of the pattern
	 * @return polyphonic pattern with the given voices
	 */
	static Pattern of(Map<Integer, List<? extends Durational>> voices, String name) {
		return new PolyphonicPattern(voices, name, Collections.emptySet());
	}

	/**
	 * Returns a polyphonic pattern with the given voices.
	 *
	 * @param voices the voices of the pattern
	 * @param name   the name of the pattern
	 * @param labels the labels associated with the pattern
	 * @return polyphonic pattern with the given voices
	 */
	static Pattern of(Map<Integer, List<? extends Durational>> voices, String name, Set<String> labels) {
		return new PolyphonicPattern(voices, name, labels);
	}

	/**
	 * Returns a pattern with the given contents.
	 *
	 * @param contents non-empty list containing the notation elements of the pattern in temporal order
	 * @param name     the name of the pattern
	 * @return a pattern with the given contents
	 */
	static Pattern of(List<? extends Durational> contents, String name) {
		return of(contents, name, Collections.emptySet());
	}

	/**
	 * Returns a pattern with the given contents.
	 *
	 * @param contents non-empty list containing the notation elements of the pattern in temporal order
	 * @param name     the name of the pattern
	 * @param labels   the labels associated with the pattern
	 * @return a pattern with the given contents
	 */
	static Pattern of(List<? extends Durational> contents, String name, Set<String> labels) {
		final boolean isPolyphonic = contents.stream().anyMatch(durational -> durational instanceof Chord);
		if (isPolyphonic) {
			return new PolyphonicPattern(contents, name, labels);
		}

		return new MonophonicPattern(contents, name, labels);
	}

	/**
	 * Returns a polyphonic pattern with the given voices.
	 *
	 * @param voices the voices of the pattern
	 * @return polyphonic pattern with the given voices
	 */
	static Pattern of(Map<Integer, List<? extends Durational>> voices) {
		return new PolyphonicPattern(voices);
	}

	/**
	 * Returns the name of the pattern.
	 * <p>
	 * If the pattern does not have a name, then returns empty.
	 *
	 * @return the name of the pattern
	 */
	Optional<String> getName();

	/**
	 * Returns the labels associated with this pattern in lexicographical order.
	 *
	 * @return the labels associated with this pattern in lexicographical order
	 */
	SortedSet<String> getLabels();

	/**
	 * Returns true if this pattern contains only a single voice and does not contain
	 * any notes that occur simultaneously, otherwise returns false.
	 *
	 * @return true if this pattern contains only a single voice and does not contain
	 * any notes that occur simultaneously
	 */
	boolean isMonophonic();

	/**
	 * Returns the number of voices in this pattern.
	 *
	 * @return the number of voices in this pattern
	 */
	int getVoiceCount();

	/**
	 * Returns the voice numbers in this pattern in ascending order.
	 *
	 * @return the voice numbers in this pattern in ascending order
	 */
	List<Integer> getVoiceNumbers();

	/**
	 * Returns the contents of the voice with the given number.
	 * The contents of the voice are iterated in their temporal order, i.e.,
	 * from left to right int notation.
	 *
	 * @param voiceNumber the number of the voice whose contents are returned
	 * @return the contents of the voice with the given number
	 */
	Iterable<Durational> getVoice(int voiceNumber);

	/**
	 * Returns the notation element at the given index in the voice
	 * with the given number.
	 *
	 * @param voiceNumber the number of the voice from which to retrieve the notation element
	 * @param index       the index of the notation element in the voice
	 * @return the notation element at the given index in the voice
	 * with the given number
	 */
	Durational get(int voiceNumber, int index);

	/**
	 * Returns the number of notation elements in the voice with the given number.
	 *
	 * @param voiceNumber the number of the voice for which the size is returned
	 * @return the number of notation elements in the voice with the given number
	 */
	int getVoiceSize(int voiceNumber);

	/**
	 * Returns true if this pattern has the given labels.
	 *
	 * @param label the label whose presence is checked
	 * @return true if this pattern has the given labels
	 */
	boolean hasLabel(String label);

	/**
	 * Returns true if this pattern contains the same pitches in the
	 * same order as other, otherwise returns false. The pitches must be spelled the
	 * same way for the patterns to be considered equal in pitch. Rhythm is ignored.
	 *
	 * @param other the pattern against which this is compared for
	 *              pitch equality.
	 * @return true if this pattern contains the same pitches in the
	 * same order as other, otherwise returns false
	 */
	boolean equalsInPitch(Pattern other);

	/**
	 * Returns true if this pattern contains the enharmonically same
	 * pitches in the same order as other, otherwise returns false. The pitch
	 * spellings are not considered, but they are compared using enharmonic
	 * equality. Rhythm is ignored.
	 *
	 * @param other the pattern against which this is compared for
	 *              enharmonic pitch equality.
	 * @return true if this pattern contains the enharmonically same
	 * pitches in the same order as other, otherwise returns false
	 */
	boolean equalsEnharmonically(Pattern other);

	/**
	 * Returns true if this pattern can be transposed chromatically so that its
	 * pitches are enharmonically equal to those of other, otherwise returns false.
	 * Durations are not considered in the comparison.
	 *
	 * @param other the pattern against which this is compared for
	 *              transposed enharmonic pitch equality.
	 * @return true if this pattern can be transposed chromatically so that its
	 * pitches are enharmonically equal to those of other, otherwise returns
	 * false
	 */
	boolean equalsTranspositionally(Pattern other);

	/**
	 * Returns true if this pattern has the same durations as the other pattern. The durations of notes in this pattern
	 * must match the durations of notes in the given pattern and the durations of rests in this must match the
	 * durations of the rests in the given pattern.
	 * Pitches are ignored.
	 *
	 * @param other the pattern against which this is compared for
	 *              durational equality
	 * @return true if this pattern has the same durations as the other pattern
	 */
	boolean equalsInDurations(Pattern other);

	/**
	 * Returns the number of durational notation elements in this pattern.
	 *
	 * @return the number of durational notation elements in this pattern
	 */
	int size();
}
