/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

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
	 * Returns true if this <code>Pattern</code> is equal to other.
	 *
	 * @param other the <code>Pattern</code> this is compared against for equality
	 * @return true if this pattern contains the exact same notes, rest, and chords
	 * in the same order as other. Otherwise returns false.
	 */
	boolean equals(Pattern other);

	/**
	 * Returns true if this pattern does not contain any notes occur simultaneously,
	 * otherwise returns false.
	 *
	 * @return true if this pattern does not contain any notes occur simultaneously.
	 * Otherwise returns false.
	 */
	boolean isMonophonic();

	/**
	 * Returns true if this <code>Pattern</code> contains the same pitches in the
	 * same order as other, otherwise returns false. The pitches must be spelled the
	 * same way for the patterns to be considered equal in pitch. Rhythm is ignored.
	 *
	 * @param other the <code>Pattern</code> against which this is compared for
	 *              pitch equality.
	 * @return true if this <code>Pattern</code> contains the same pitches in the
	 * same order as other, otherwise returns false
	 */
	boolean equalsInPitch(Pattern other);

	/**
	 * Returns true if this <code>Pattern</code> contains the enharmonically same
	 * pitches in the same order as other, otherwise returns false. The pitch
	 * spellings are not considered, but they are compared using enharmonic
	 * equality. Rhythm is ignored.
	 *
	 * @param other the <code>Pattern</code> against which this is compared for
	 *              enharmonic pitch equality.
	 * @return true if this <code>Pattern</code> contains the enharmonically same
	 * pitches in the same order as other, otherwise returns false
	 */
	boolean equalsEnharmonicallyInPitch(Pattern other);

	/**
	 * Returns true if this <code>Pattern</code> can be transposed so that its
	 * pitches are enharmonically equal to those of other, otherwise returns false.
	 *
	 * @param other the <code>Pattern</code> against which this is compared for
	 *              transposed enharmonic pitch equality.
	 * @return true if this <code>Pattern</code> can be transposed so that its
	 * pitches are enharmonically equal to those of other, otherwise returns
	 * false
	 */
	boolean equalsInTransposedPitch(Pattern other);

	/**
	 * Returns true if this <code>Pattern</code> has the same rhythm as other. Both
	 * the onsets and durations must match for rhythms to be considered equal.
	 * Pitches are ignored.
	 *
	 * @param other the <code>Pattern</code> against which this is compared for
	 *              rhythm equality.
	 * @return true if this <code>Pattern</code> has the same rhythm as other,
	 * otherwise returns false
	 */
	boolean equalsInRhythm(Pattern other);

	/**
	 * Returns true if the onset times of notes in this <code>Pattern</code> match
	 * the onset times of notes in other, otherwise returns false. Pitches are
	 * ignored.
	 *
	 * @param other the <code>Pattern</code> against which this is compared for
	 *              onset equality.
	 * @return true if the onset times of notes in this <code>Pattern</code> match
	 * the onset times of notes in other, otherwise returns false
	 */
	boolean equalsInOnsets(Pattern other);
}
