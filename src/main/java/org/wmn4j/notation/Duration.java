/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Iterator;

/**
 * Represents the duration of any musical object with a duration such as a note
 * or a rest. Durations are handled as rational numbers that tell what fraction
 * of a whole note the duration is just like in normal music terminology. For
 * example, the duration of a quarter note is handled as the rational number
 * 1/4. The rational number is always reduced to the lowest possible numerator
 * and denominator.
 * <p>
 * In order to correctly create dotted durations, use the {@link Duration#addDot()} method.
 * The number of dots a duration has is not used in comparisons, only the actual length of
 * the duration is considered in comparison. For example 1/4 + 1/8 is considered equal to
 * a dotted 1/4. Operations such as add and subtract are not guaranteed to retain dot information.
 * <p>
 * This class is immutable.
 */
public final class Duration implements Comparable<Duration> {

	private final int numerator;
	private final int denominator;
	private final int dotCount;

	/**
	 * Returns an instance with the given numerator and denominator. The numerator
	 * and denominator must be at least 1.
	 *
	 * @param numerator   the numerator part of the duration
	 * @param denominator the denominator part of the duration
	 * @return an instance with the given numerator and denominator
	 * @throws IllegalArgumentException if numerator or denominator are less than 1
	 */
	public static Duration of(int numerator, int denominator) {
		return of(numerator, denominator, 0);
	}

	/**
	 * Returns an instance with the given numerator and denominator and the given number
	 * of dots. The numerator and denominator must be at least 1. The given dot count must be positive
	 * and at most 5. The given dot count does not affect the length of the duration, in order to create
	 * dotted durations by extending the length of the duration use the {@link Duration#addDot()} method.
	 *
	 * @param numerator   the numerator part of the duration
	 * @param denominator the denominator part of the duration
	 * @param dotCount    the number of dots used to express the duration
	 * @return an instance with the given numerator and denominator
	 * @throws IllegalArgumentException if numerator or denominator are less than 1
	 */
	public static Duration of(int numerator, int denominator, int dotCount) {

		if (numerator < 1) {
			throw new IllegalArgumentException("numerator must be at least 1");
		}
		if (denominator < 1) {
			throw new IllegalArgumentException("denominator must be at least 1");
		}
		if (dotCount < 0 || dotCount > 5) {
			throw new IllegalArgumentException("dotCount must be at least zero and at most 5.");
		}

		int reducedNumerator = numerator;
		int reducedDenominator = denominator;

		// TODO: Come up with a more effective way of finding GCD
		if (numerator != 1) {
			final int gcd = BigInteger.valueOf(numerator).gcd(BigInteger.valueOf(denominator)).intValue();
			reducedNumerator = numerator / gcd;
			reducedDenominator = denominator / gcd;
		}
		// TODO: Implement caching instead of creating new.
		return new Duration(reducedNumerator, reducedDenominator, dotCount);
	}

	/**
	 * Constructor for the class. The constructor is private, to get a Duration
	 * object use the static method {@link #of(int, int) getDuration}.
	 *
	 * @param numerator   the numerator part of the duration
	 * @param denominator the denominator part of the duration
	 */
	private Duration(int numerator, int denominator, int dotCount) {
		this.numerator = numerator;
		this.denominator = denominator;

		this.dotCount = dotCount;
	}

	/**
	 * Returns the numerator of this duration.
	 *
	 * @return the numerator of this duration
	 */
	public int getNumerator() {
		return this.numerator;
	}

	/**
	 * Returns the denominator of this duration.
	 *
	 * @return the denominator of this duration.
	 */
	public int getDenominator() {
		return this.denominator;
	}

	/**
	 * Returns true if this is equal to the given object.
	 *
	 * @param o an object against which this Duration is compared for equality
	 * @return true if the given object is a {@link Duration} with the same
	 * numerator and denominator as this, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}

		if (!(o instanceof Duration)) {
			return false;
		}

		final Duration other = (Duration) o;

		return (this.numerator == other.numerator) && (this.denominator == other.denominator);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + this.numerator;
		hash = 83 * hash + this.denominator;
		return hash;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(this.numerator).append("/").append(this.denominator).append(")")
				.append(".".repeat(getDotCount()));
		return builder.toString();
	}

	/**
	 * Returns a duration that is this duration with a dot added to it.
	 *
	 * @return duration that is this duration with a dot added to it
	 */
	public Duration addDot() {
		/*
		 * Adding the nth dot adds half of what the (n + 1)th dot added. For example,
		 * a duration with three dots d_3 is formed by adding dots to a dotless duration d_0:
		 * d_3 = d_0 + d_0/2 + d_0/4 + d_0/8. This forms a geometric sum. From this it's
		 * possible to infer that adding a dot to a duration d_n with n dots adds
		 * d_n / (2^(n + 2) - 2) to the duration.
		 */
		final int dotDurationDivisor = (1 << (dotCount + 2)) - 2;
		final Duration dotDuration = this.divideBy(dotDurationDivisor);
		final Duration dottedDuration = this.add(dotDuration);

		return of(dottedDuration.getNumerator(), dottedDuration.getDenominator(), dotCount + 1);
	}

	/**
	 * Returns the number of dots in this duration.
	 * The dots do not affect the mathematical duration and do not thus
	 * affect the equality comparisons of durations.
	 *
	 * @return the number of dots in this duration
	 */
	public int getDotCount() {
		return dotCount;
	}

	/**
	 * Return the fraction of the duration as a double. This should only be used for
	 * computation in analysis algorithms, such as pitch profiles weighted by
	 * durations. Many common durations, such as triplets, cannot be represented
	 * exactly as floating-point numbers which can cause problems with cumulated
	 * durations not adding up to the expected durations. For accurate arithmetic
	 * with Durations the methods provided by this class should be used.
	 *
	 * @return the fraction numerator/denominator as double.
	 */
	public double toDouble() {
		return (double) this.numerator / this.denominator;
	}

	/**
	 * Returns a duration that is the sum of this and other.
	 *
	 * @param other the Duration to be added to this.
	 * @return a Duration that is the sum of this and other.
	 */
	public Duration add(Duration other) {
		final int nom = this.numerator * other.denominator + this.denominator * other.numerator;
		final int denom = this.denominator * other.denominator;

		return of(nom, denom);
	}

	/**
	 * Returns a duration that is this duration minus the duration given as
	 * parameter.
	 *
	 * @param other the Duration to be subtracted from this.
	 * @return a Duration that is this other minus other.
	 */
	public Duration subtract(Duration other) {
		final int nom = this.numerator * other.denominator - this.denominator * other.numerator;
		final int denom = this.denominator * other.denominator;

		return of(nom, denom);
	}

	/**
	 * Returns a duration that is this duration multiplied by multiplier. Multiplier
	 * must be at least 1.
	 *
	 * @param multiplier the factor by which this duration should be multiplied
	 * @return a Duration that is this duration multiplied by multiplier
	 * @throws IllegalArgumentException if multiplier is less than 1
	 */
	public Duration multiplyBy(int multiplier) {
		if (multiplier < 1) {
			throw new IllegalArgumentException("multiplier must be at least 1. Was " + multiplier);
		}

		return of(this.numerator * multiplier, this.denominator);
	}

	/**
	 * Returns a duration that is this duration divided by divider. Divider must be
	 * at least 1.
	 *
	 * @param divisor the factor by which this duration should be divided
	 * @return a Duration that is this duration divided by divider
	 * @throws IllegalArgumentException if divider is less than 1
	 */
	public Duration divideBy(int divisor) {
		if (divisor < 1) {
			throw new IllegalArgumentException("divider must be at least 1. Was " + divisor);
		}

		return of(this.numerator, this.denominator * divisor);
	}

	/**
	 * Returns true if this is longer than other, otherwise false.
	 *
	 * @param other Duration to which this is compared
	 * @return true if this is longer than other, otherwise false
	 */
	public boolean isLongerThan(Duration other) {
		return this.compareTo(other) > 0;
	}

	/**
	 * Returns true if this is shorter than other, otherwise false.
	 *
	 * @param other Duration to which this is compared
	 * @return true if this is shorter than other, otherwise false
	 */
	public boolean isShorterThan(Duration other) {
		return this.compareTo(other) < 0;
	}

	/**
	 * Compare the length of this Duration and other.
	 *
	 * @param other the Duration against which this is compared for length.
	 * @return negative integer if this is shorter than other, positive integer if
	 * this is longer than other, 0 if this is equal to other.
	 */
	@Override
	public int compareTo(Duration other) {
		return this.numerator * other.denominator - other.numerator * this.denominator;
	}

	/**
	 * Returns the total summed duration of the given durations.
	 *
	 * @param durations the durations whose sum is returned
	 * @return The sum of the given durations
	 * @throws IllegalArgumentException if durations is empty
	 */
	public static Duration sumOf(Collection<Duration> durations) {
		if (durations.isEmpty()) {
			throw new IllegalArgumentException("Cannot compute sum of durations from empty list");
		}

		Iterator<Duration> iterator = durations.iterator();
		Duration cumulatedDur = iterator.next();

		// TODO: Optimize this.
		while (iterator.hasNext()) {
			cumulatedDur = cumulatedDur.add(iterator.next());
		}

		return cumulatedDur;
	}
}
