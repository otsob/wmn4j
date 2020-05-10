/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.apache.commons.math3.fraction.Fraction;

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

	private static final int MAX_DOT_COUNT = 5;

	private final Fraction fraction;
	private final int dotCount;

	/**
	 * Returns an instance with the given numerator and denominator. The numerator
	 * and denominator must be at least 1.
	 *
	 * @param numerator   the positive numerator part of the duration
	 * @param denominator the positive denominator part of the duration
	 * @return an instance with the given numerator and denominator
	 */
	public static Duration of(int numerator, int denominator) {
		return create(new Fraction(numerator, denominator), 0);
	}

	/**
	 * Returns an instance with the given numerator and denominator and the given number
	 * of dots. The numerator and denominator must be at least 1. The given dot count must be positive
	 * and at most 5. The given dot count does not affect the length of the duration, in order to create
	 * dotted durations by extending the length of the duration use the {@link Duration#addDot()} method.
	 *
	 * @param numerator   the positive numerator part of the duration
	 * @param denominator the positive denominator part of the duration
	 * @param dotCount    the number of dots used to express the duration
	 * @return an instance with the given numerator and denominator
	 */
	public static Duration of(int numerator, int denominator, int dotCount) {
		return create(new Fraction(numerator, denominator), dotCount);
	}

	/**
	 * Private creator method.
	 */
	private static Duration create(Fraction fraction, int dotCount) {

		if (fraction.getNumerator() < 1) {
			throw new IllegalArgumentException("numerator must be at least 1");
		}
		if (fraction.getDenominator() < 1) {
			throw new IllegalArgumentException("denominator must be at least 1");
		}
		if (dotCount < 0 || dotCount > MAX_DOT_COUNT) {
			throw new IllegalArgumentException("dotCount must be at least zero and at most " + MAX_DOT_COUNT);
		}

		return new Duration(fraction, dotCount);
	}

	/**
	 * Constructor for the class. The constructor is private, to get a Duration
	 * object use the static method {@link #of(int, int) getDuration}.
	 */
	private Duration(Fraction fraction, int dotCount) {
		this.fraction = fraction;

		this.dotCount = dotCount;
	}

	/**
	 * Returns the numerator of this duration.
	 *
	 * @return the numerator of this duration
	 */
	public int getNumerator() {
		return fraction.getNumerator();
	}

	/**
	 * Returns the denominator of this duration.
	 *
	 * @return the denominator of this duration.
	 */
	public int getDenominator() {
		return fraction.getDenominator();
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

		return this.fraction.equals(other.fraction);
	}

	@Override
	public int hashCode() {
		return fraction.hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("(").append(getNumerator()).append("/").append(getDenominator()).append(")")
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

		final Fraction durationValue = this.fraction.add(this.fraction.divide(dotDurationDivisor));
		return create(durationValue, dotCount + 1);
	}

	/**
	 * Returns a duration that is this duration with one dot removed. If this duration has no
	 * dots specified (i.e. {@link Duration#getDotCount()} returns 0) for its expression then the method returns
	 * this Duration.
	 *
	 * @return a duration that is this duration with one dot removed
	 */
	public Duration removeDot() {
		if (dotCount == 0) {
			return this;
		}

		/*
		 * Using the geometric sum expression of dotted durations is possible to
		 * compute how much the last added dot contributed to the total duration.
		 */
		final int dotDurationDivisor = (1 << (dotCount + 1)) - 1;

		final Fraction lastDotDuration = this.fraction.divide(dotDurationDivisor);
		return create(this.fraction.subtract(lastDotDuration), dotCount - 1);
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
		return fraction.doubleValue();
	}

	/**
	 * Returns a duration that is the sum of this and other.
	 *
	 * @param other the Duration to be added to this.
	 * @return a Duration that is the sum of this and other.
	 */
	public Duration add(Duration other) {
		return create(this.fraction.add(other.fraction), 0);
	}

	/**
	 * Returns a duration that is this duration minus the duration given as
	 * parameter.
	 *
	 * @param other the Duration to be subtracted from this.
	 * @return a Duration that is this other minus other.
	 */
	public Duration subtract(Duration other) {
		return create(this.fraction.subtract(other.fraction), 0);
	}

	/**
	 * Returns a duration that is this duration multiplied by multiplier. Multiplier
	 * must be at least 1.
	 *
	 * @param multiplier the factor by which this duration should be multiplied
	 * @return a Duration that is this duration multiplied by multiplier
	 * @throws IllegalArgumentException if multiplier is less than 1
	 */
	public Duration multiply(int multiplier) {
		return create(this.fraction.multiply(multiplier), 0);
	}

	/**
	 * Returns a duration that is this duration divided by divider. Divider must be
	 * at least 1.
	 *
	 * @param divisor the factor by which this duration should be divided
	 * @return a Duration that is this duration divided by divider
	 * @throws IllegalArgumentException if divider is less than 1
	 */
	public Duration divide(int divisor) {
		return create(this.fraction.divide(divisor), dotCount);
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
		return this.fraction.compareTo(other.fraction);
	}

	/**
	 * Returns the total summed duration of the given durations.
	 * The given iterable of durations must not be empty.
	 *
	 * @param durations the durations whose sum is returned
	 * @return The sum of the given durations
	 */
	public static Duration sum(Iterable<Duration> durations) {
		Iterator<Duration> iterator = durations.iterator();
		Fraction cumulatedDur = Fraction.ZERO;

		while (iterator.hasNext()) {
			cumulatedDur = cumulatedDur.add(iterator.next().fraction);
		}

		return create(cumulatedDur, 0);
	}
}
