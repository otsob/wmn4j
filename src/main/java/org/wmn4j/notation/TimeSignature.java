/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Objects;

/**
 * Class for time signatures. Time signatures consist of the number of beats and
 * the duration of each beat. For example, for the time signature 4/4 the number
 * of beats is 4 and the duration of beat is equal to the duration of a quarter note.
 * <p>
 * This class is immutable.
 */
public final class TimeSignature {

	/**
	 * Represents the symbol used to mark the time signature.
	 */
	public enum Symbol {
		/**
		 * Denotes a time signature specified as a pair of numbers, for example, 4/4.
		 */
		NUMERIC,

		/**
		 * Denotes a time signature that uses the symbol C.
		 */
		COMMON,

		/**
		 * Denotes cut time, i.e., alla breve.
		 */
		CUT_TIME,

		/**
		 * Denotes a time signature where only the number of beats is shown.
		 */
		BEAT_NUMBER_ONLY,

		/**
		 * Denotes a time signature where the beat duration is shown as a note.
		 */
		BEAT_DURATION_AS_NOTE,

		/**
		 * Denotes a time signature where the beat duration is shown as a note that
		 * has a duration that is three times the duration of the beat and the numerator
		 * is one third of the number of beats.
		 */
		BEAT_DURATION_AS_DOTTED_NOTE,
	}

	private final int beats;
	private final Duration beatDuration;
	private final Symbol symbol;

	/**
	 * Returns a time signature with the given numerator and denominator.
	 *
	 * @param numerator   the top number of the time signature
	 * @param denominator the bottom number of the time signature
	 * @return a time signature with the given numerator and denominator
	 */
	public static TimeSignature of(int numerator, int denominator) {
		return of(numerator, Duration.of(1, denominator), Symbol.NUMERIC);
	}

	/**
	 * Returns a time signature with the given numerator and beat duration.
	 *
	 * @param beats        number of beats in measure
	 * @param beatDuration the Duration of the beats
	 * @return a time signature with the given numerator and beat duration
	 * @throws IllegalArgumentException if beats is less than 1
	 * @throws NullPointerException     if beatDuration is null
	 */
	public static TimeSignature of(int beats, Duration beatDuration) {
		return of(beats, beatDuration, Symbol.NUMERIC);
	}

	/**
	 * Returns a time signature with the given numerator, beat duration, and symbol.
	 *
	 * @param beats        number of beats in measure
	 * @param beatDuration the Duration of the beats
	 * @param symbol       the symbol of the time signature
	 * @return a time signature with the given numerator, beat duration, and symbol
	 * @throws IllegalArgumentException if beats is less than 1
	 * @throws NullPointerException     if beatDuration is null
	 */
	public static TimeSignature of(int beats, Duration beatDuration, Symbol symbol) {
		if (beats < 1) {
			throw new IllegalArgumentException("beats must be at least 1.");
		}

		return new TimeSignature(beats, Objects.requireNonNull(beatDuration), symbol);
	}

	private TimeSignature(int beats, Duration beatDuration, Symbol symbol) {
		this.beats = beats;
		this.beatDuration = beatDuration;
		this.symbol = symbol;
	}

	/**
	 * @return number of beats.
	 */
	public int getBeatCount() {
		return this.beats;
	}

	/**
	 * @return the Duration of beats.
	 */
	public Duration getBeatDuration() {
		return this.beatDuration;
	}

	/**
	 * Get the total duration of a measure with this time signature. For example for
	 * time signature 4/4 duration is one whole note.
	 *
	 * @return sum of beat durations in a measure.
	 */
	public Duration getTotalDuration() {
		return this.beatDuration.multiply(this.beats);
	}

	/**
	 * Returns the symbol used for this time signature.
	 *
	 * @return the symbol used for this time signature
	 */
	public Symbol getSymbol() {
		return symbol;
	}

	/**
	 * Compare for equality.
	 *
	 * @param o Object against which this is compared for equality.
	 * @return true if o is an instance of TimeSignature and o has the same number
	 * of beats and same beat duration as this. false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof TimeSignature)) {
			return false;
		}

		final TimeSignature other = (TimeSignature) o;

		return this.beatDuration.equals(other.beatDuration) && this.beats == other.beats && this.symbol
				.equals(other.symbol);
	}

	@Override
	public int hashCode() {
		return Objects.hash(beats, beatDuration, symbol);
	}

	/**
	 * Get String representation of <code>TimeSignature</code>. String
	 * representation is of form <code>Time(numBeats/beatDuration)</code>.
	 *
	 * @return string representation of TimeSignature.
	 */
	@Override
	public String toString() {
		final String symbolString = symbol.equals(Symbol.NUMERIC) ? "" : "-" + symbol.toString();
		return "Time(" + this.beats + "/" + this.beatDuration.getDenominator() + ")" + symbolString;
	}
}
