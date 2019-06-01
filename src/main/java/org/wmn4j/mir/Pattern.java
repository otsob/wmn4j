/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import org.wmn4j.notation.elements.Durational;

import java.util.List;

/**
 * Interface for musical patterns. The patterns can represent motifs, melodies,
 * or segments of polyphonic music. A pattern is a sequence of notes, chords,
 * and rests. A pattern can be monophonic or polyphonic. In a monophonic pattern
 * there are no simultaneously occurring notes or chords. In a polyphonic
 * pattern there can be multiple notes occurring at the same time in chords or
 * in multiple voices.
 */
public interface Pattern {

	/**
	 * Returns a monophonic pattern with the given contents. The contents must be strictly monophonic, i.e., they cannot
	 * contain any chords.
	 *
	 * @param contents non-empty list containing the notation elements of the pattern in temporal order
	 * @return a pattern with the given contents
	 */
	static Pattern monophonicOf(List<Durational> contents) {
		return new MonophonicPattern(contents);
	}

	/**
	 * Returns the contents of this pattern. For monophonic patterns the contents are returned in temporal order. For
	 * polyphonic patterns the order is not specified.
	 *
	 * @return the contents of this pattern
	 */
	List<Durational> getContents();

	/**
	 * Returns true if this pattern does not contain any notes occur simultaneously,
	 * otherwise returns false.
	 *
	 * @return true if this pattern does not contain any notes occur simultaneously.
	 * Otherwise returns false.
	 */
	boolean isMonophonic();

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
	 * Returns true if this pattern can be transposed so that its
	 * pitches are enharmonically equal to those of other, otherwise returns false.
	 *
	 * @param other the pattern against which this is compared for
	 *              transposed enharmonic pitch equality.
	 * @return true if this pattern can be transposed so that its
	 * pitches are enharmonically equal to those of other, otherwise returns
	 * false
	 */
	boolean equalsInTransposedPitch(Pattern other);

	/**
	 * Returns true if this pattern has the same rhythm as other. Both
	 * the onsets and durations must match for rhythms to be considered equal.
	 * Pitches are ignored.
	 *
	 * @param other the pattern against which this is compared for
	 *              rhythm equality.
	 * @return true if this pattern has the same rhythm as other,
	 * otherwise returns false
	 */
	boolean equalsInRhythm(Pattern other);

	/**
	 * Returns true if the onset times of notes in this pattern match
	 * the onset times of notes in other, otherwise returns false. Pitches are
	 * ignored.
	 *
	 * @param other the pattern against which this is compared for
	 *              onset equality.
	 * @return true if the onset times of notes in this pattern match
	 * the onset times of notes in other, otherwise returns false
	 */
	boolean equalsInOnsets(Pattern other);
}
