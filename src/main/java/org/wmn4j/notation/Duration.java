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
 * This class is immutable.
 */
public final class Duration implements Comparable<Duration> {

	private final int numerator;
	private final int denominator;

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

		if (numerator < 1) {
			throw new IllegalArgumentException("numerator must be at least 1");
		}
		if (denominator < 1) {
			throw new IllegalArgumentException("denominator must be at least 1");
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
		return new Duration(reducedNumerator, reducedDenominator);
	}

	/**
	 * Constructor for the class. The constructor is private, to get a Duration
	 * object use the static method {@link #of(int, int) getDuration}.
	 *
	 * @param numerator   the numerator part of the duration
	 * @param denominator the denominator part of the duration
	 */
	private Duration(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
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
		return "(" + this.numerator + "/" + this.denominator + ")";
	}

	/**
	 * Returns a duration that is this duration with half of this duration added to
	 * it.
	 *
	 * @return duration that is this incremented by half of this
	 */
	public Duration addDot() {
		return this.add(Duration.of(this.numerator, 2 * this.denominator));
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
