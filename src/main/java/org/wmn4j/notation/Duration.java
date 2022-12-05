/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.apache.commons.math3.fraction.Fraction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Represents the duration of any musical object with a duration such as a note
 * or a rest. Durations are handled as rational numbers that tell what fraction
 * of a whole note the duration is just like in normal music terminology. For
 * example, the duration of a quarter note is handled as the rational number
 * 1/4. The rational number is always reduced to the lowest possible numerator
 * and denominator.
 * <p>
 * Durations have dot count and tuplet divisor information, however these are only used for
 * duration expression information and do not affect the equality and ordering of durations.
 * Only the actual length of the duration is considered in comparison. For example 1/4 + 1/8 is considered equal to
 * a dotted 1/4. In order to correctly create dotted durations and tuplet durations, use the {@link Duration#addDot()}
 * and {@link Duration#divide(int)} methods. Addition, subtraction, and multiplication are not guaranteed to retain
 * expression information. Whether a duration is expressible as a single notation symbol using its expression
 * information
 * can be checked with the {@link Duration#hasExpression()} method.
 * <p>
 * This class is immutable.
 */
public final class Duration implements Comparable<Duration> {

	private static final int MAX_DOT_COUNT = 5;
	private static final int DEFAULT_DOT_COUNT = 0;
	private static final int DEFAULT_TUPLET_DIVISOR = 1;

	// Maximum value a basic note type denominator can have to be expressible at all
	// in music notation.
	private static final int MAX_EXPRESSIBLE_BASIC_DENOMINATOR = 1024;

	// Numerators that are allowed to occur in simplified durations, covers
	// cases like breve (double whole note) and longa (quadruple whole note).
	private static final Set<Integer> EXPRESSIBLE_NUMERATORS = Set.of(1, 2, 4);

	// The numerators that would indicate a duration with 1, 2, or 3 dots.
	// These are used for decomposing durations without expression information into
	// expressible durations.
	private static final Set<Integer> DOTTED_DURATION_NUMERATORS = Set.of(3, 7, 15);
	// More or less common tuplet divisors to test for when decomposing.
	// Some tuplet divisors are left out (e.g. 6) as they can always be represented also as triplets and it's
	// hard (impossible?) to infer whether a duration should be a triplet or sextuplet based on just
	// its mathematical duration.
	private static final List<Integer> COMMON_TUPLET_DIVISORS = Arrays.asList(3, 5, 7, 11, 13);

	private final Fraction fraction;
	private final int tupletDivisor;
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
		return create(new Fraction(numerator, denominator), DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR);
	}

	/**
	 * Returns an instance with the given numerator and denominator and the given number
	 * of dots. The numerator and denominator must be at least 1. The given dot count must be positive
	 * and at most 5. The tupletDivisor must be positive. The given dot count and tuplet divisor do
	 * not affect the length of the duration, they are only used to specify how the duration is expressed.
	 * In order to create
	 * dotted durations by extending the length of the duration use the {@link Duration#addDot()} method.
	 * To create tuplets it is recommended to use the {@link Duration#divide(int divisor)} method.
	 *
	 * @param numerator     the positive numerator part of the duration
	 * @param denominator   the positive denominator part of the duration
	 * @param dotCount      the number of dots used to express the duration
	 * @param tupletDivisor the divisor used for expressing the duration as a tuplet
	 * @return an instance with the given numerator and denominator and expression information
	 */
	public static Duration of(int numerator, int denominator, int dotCount, int tupletDivisor) {
		return create(new Fraction(numerator, denominator), dotCount, tupletDivisor);
	}

	/**
	 * Private creator method.
	 */
	private static Duration create(Fraction fraction, int dotCount, int tupletDivisor) {

		if (fraction.getNumerator() < 1) {
			throw new IllegalArgumentException("numerator must be at least 1");
		}
		if (fraction.getDenominator() < 1) {
			throw new IllegalArgumentException("denominator must be at least 1");
		}
		if (dotCount < 0 || dotCount > MAX_DOT_COUNT) {
			throw new IllegalArgumentException("dotCount must be at least zero and at most " + MAX_DOT_COUNT);
		}
		if (tupletDivisor < 1) {
			throw new IllegalArgumentException("tupletDivisor must be positive");
		}

		return new Duration(fraction, dotCount, tupletDivisor);
	}

	/**
	 * Constructor for the class. The constructor is private, to get a Duration
	 * object use the static method {@link #of(int, int) getDuration}.
	 */
	private Duration(Fraction fraction, int dotCount, int tupletDivisor) {
		this.fraction = fraction;
		this.dotCount = dotCount;
		this.tupletDivisor = tupletDivisor;
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
	 * <p>
	 * This method retains tuplet divisor information.
	 *
	 * @return duration that is this duration with a dot added to it
	 */
	public Duration addDot() {
		/*
		 * Adding the nth dot adds half of what the (n - 1)th dot added. For example,
		 * a duration with three dots d_3 is formed by adding dots to a dotless duration d_0:
		 * d_3 = d_0 + d_0/2 + d_0/4 + d_0/8. This forms a geometric sum. From this it's
		 * possible to infer that adding a dot to a duration d_n with n dots adds
		 * d_n / (2^(n + 2) - 2) to the duration.
		 */
		final int dotDurationDivisor = (1 << (dotCount + 2)) - 2;

		final Fraction durationValue = this.fraction.add(this.fraction.divide(dotDurationDivisor));
		return create(durationValue, dotCount + 1, tupletDivisor);
	}

	/**
	 * Returns a duration that is this duration with one dot removed. If this duration has no
	 * dots specified (i.e. {@link Duration#getDotCount()} returns 0) for its expression then the method returns
	 * this Duration.
	 * <p>
	 * This method retains tuplet divisor information.
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
		return create(this.fraction.subtract(lastDotDuration), dotCount - 1, tupletDivisor);
	}

	/**
	 * Returns a duration that is this duration with all dots removed. If this duration has no
	 * dots specified (i.e. {@link Duration#getDotCount()} returns 0) for its expression then the method returns
	 * this Duration.
	 * <p>
	 * This method retains tuplet divisor information.
	 *
	 * @return a duration that is this duration with all dots removed.
	 */
	public Duration removeDots() {
		if (dotCount == 0) {
			return this;
		}

		Fraction durationWithoutDots = removeDots(this.fraction, dotCount);
		return create(durationWithoutDots, DEFAULT_DOT_COUNT, tupletDivisor);
	}

	private Fraction removeDots(Fraction withDots, int dotCount) {
		/*
		 * If a duration d_n has n dots, then the duration d_0 that is produced by removing all dots is
		 * d_0 = d_n / (2 - (1/2)^n). This is derived from the geometric sum produced by adding n dots to
		 * a duration.
		 */
		Fraction divisor = new Fraction(2, 1).subtract(new Fraction(1, (1 << dotCount)));
		return withDots.divide(divisor);
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
	 * Returns the tuplet divisor used to express this duration in notation.
	 * For example, for a triplet duration this method will return 3.
	 * For non-tuplets returns 1. Note that whether
	 * the correct tuplet divisor is available depends on how the duration has been created.
	 * Addition, subtraction, and multiplication are not guaranteed to reserve expression information.
	 *
	 * @return the tuplet divisor used to express this duration in notation
	 */
	public int getTupletDivisor() {
		return tupletDivisor;
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
	 * <p>
	 * Addition can create durations that are not directly expressible as a single
	 * notation symbol, therefore this method does not retain dot count or
	 * tuplet divisor information.
	 *
	 * @param other the Duration to be added to this.
	 * @return a Duration that is the sum of this and other.
	 */
	public Duration add(Duration other) {
		return create(this.fraction.add(other.fraction), DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR);
	}

	/**
	 * Returns a duration that is this duration minus the duration given as
	 * parameter.
	 * <p>
	 * Subtraction can create durations that are not directly expressible as a single
	 * notation symbol, therefore this method does not retain dot count or
	 * tuplet divisor information.
	 *
	 * @param other the Duration to be subtracted from this.
	 * @return a Duration that is this other minus other.
	 */
	public Duration subtract(Duration other) {
		return create(this.fraction.subtract(other.fraction), DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR);
	}

	/**
	 * Returns a duration that is this duration multiplied by multiplier. Multiplier
	 * must be at least 1.
	 * <p>
	 * Multiplication can create durations that are not directly expressible as a single
	 * notation symbol, therefore this method does not retain dot count or
	 * tuplet divisor information.
	 *
	 * @param multiplier the factor by which this duration should be multiplied
	 * @return a Duration that is this duration multiplied by multiplier
	 * @throws IllegalArgumentException if multiplier is less than 1
	 */
	public Duration multiply(int multiplier) {
		return create(this.fraction.multiply(multiplier), DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR);
	}

	/**
	 * Returns a duration that is this duration divided by divider. Divider must be
	 * at least 1.
	 * <p>
	 * Division retains dot count information and sets the tuplet information
	 * of the duration. Division by powers of two does not affect tuplet divisor.
	 *
	 * @param divisor the factor by which this duration should be divided
	 * @return a Duration that is this duration divided by divider
	 * @throws IllegalArgumentException if divider is less than 1
	 */
	public Duration divide(int divisor) {
		int newTupletDivisor = tupletDivisor;
		if (!isPowerOfTwo(divisor)) {
			newTupletDivisor *= divisor;
		}

		return create(this.fraction.divide(divisor), dotCount, newTupletDivisor);
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
	 * Returns true if this duration is expressible as a single notation symbol with
	 * the expression information it contains (dot count, tuplet divisor).
	 *
	 * @return true if this duration is expressible as a single notation symbol with contianed expression information.
	 */
	public boolean hasExpression() {
		return isExpressible(fraction, dotCount, tupletDivisor);
	}

	private boolean isExpressible(Fraction fraction, int dotCount, int tupletDivisor) {
		// Try to simpilify the duration to one corresponding to a basic notation symbol, i.e., one that
		// is expressible as (1 / 2^n).
		Fraction basicDuration = fraction;
		if (dotCount > 0) {
			basicDuration = removeDots(basicDuration, dotCount);
		}

		int simplifiedDenominator = basicDuration.getDenominator();

		if (tupletDivisor > DEFAULT_TUPLET_DIVISOR) {
			// Simplify the denominator using the tuplet divisor if it specifies a tuplet duration.
			simplifiedDenominator /= tupletDivisor;

			// Check that the division was even.
			if (simplifiedDenominator * tupletDivisor != basicDuration.getDenominator()) {
				return false;
			}
		}

		// After removing dots and dividing away the tuplet information, the
		// numerator should be 1 (most durations), 2 (some durations like breve or 2/3),
		// or 4 (longa, the longest note type),
		// and the denominator should be a power of two if the expression information was correct.
		boolean isNumeratorExpressible = EXPRESSIBLE_NUMERATORS.contains(basicDuration.getNumerator());
		boolean isDenominatorExpressible =
				isPowerOfTwo(simplifiedDenominator) && simplifiedDenominator <= MAX_EXPRESSIBLE_BASIC_DENOMINATOR;

		return isNumeratorExpressible && isDenominatorExpressible;
	}

	/**
	 * Returns a decomposition of this duration into a sum of smaller durations with
	 * the aim of only producing expressible durations. All returned durations are not guaranteed
	 * to be expressible.
	 * If this duration has an expression and max duration is greater than or equal to this,
	 * then returns a list with this as its only element.
	 * The durations of the decomposition should add up to this duration.
	 *
	 * @param maxDuration the maximum duration to use in the sum decomposition
	 * @return decomposition of this duration into expressible durations that are at most
	 * the given max duration
	 */
	public List<Duration> decompose(Duration maxDuration) {
		if (!this.isLongerThan(maxDuration) && this.hasExpression()) {
			return Collections.singletonList(this);
		}

		List<Duration> maxDurationDecomposition = Collections.singletonList(maxDuration);
		if (!maxDuration.hasExpression()) {
			maxDurationDecomposition = decomposeFractionToExpressibleDurations(maxDuration.fraction);
		}

		int maxDurationRepetitions = fraction.divide(maxDuration.fraction).intValue();
		List<Duration> decomposition = new ArrayList<>();
		for (int i = 0; i < maxDurationRepetitions; ++i) {
			decomposition.addAll(maxDurationDecomposition);
		}

		Fraction leftOver = fraction.subtract(maxDuration.fraction.multiply(maxDurationRepetitions));
		decomposition.addAll(decomposeFractionToExpressibleDurations(leftOver));

		return decomposition;
	}

	private List<Duration> decomposeFractionToExpressibleDurations(Fraction durationFraction) {
		List<Duration> decomposition = new ArrayList<>();
		Fraction leftOver = durationFraction;

		// Repeat until there's no leftover duration
		while (leftOver.compareTo(Fraction.ZERO) > 0) {
			Duration largestFit = findLargestExpressibleDuration(leftOver);
			leftOver = leftOver.subtract(largestFit.fraction);
			decomposition.add(largestFit);
		}

		return decomposition;
	}

	/**
	 * Tries to find and return the largest expressible duration that is at most the given fraction.
	 */
	private Duration findLargestExpressibleDuration(Fraction durationFraction) {
		if (isExpressible(durationFraction, DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR)) {
			return create(durationFraction, DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR);
		}

		final int originalNumerator = durationFraction.getNumerator();
		final int originalDenominator = durationFraction.getDenominator();

		// Simplify the fraction first by seeing if it can be expressed as a dotted
		// duration and what the required dot count is.
		Fraction dotlessFraction = durationFraction;
		int dotCount = 0;

		if (DOTTED_DURATION_NUMERATORS.contains(originalNumerator)) {
			// The number of dots is log_2(numerator + 1) - 1 for the supported dot counts.
			dotCount = Integer.numberOfTrailingZeros(originalNumerator + 1) - 1;
			dotlessFraction = removeDots(durationFraction, dotCount);
		}

		// Use the fraction in the dotless form to find a suitable
		// tuplet divisor.
		final int dotlessDenominator = dotlessFraction.getDenominator();
		int tupletDivisor = 1;
		final boolean isBasicDenominator = isPowerOfTwo(dotlessDenominator);
		if (!isBasicDenominator) {
			for (int divisor : COMMON_TUPLET_DIVISORS) {
				int divided = dotlessDenominator / divisor;
				boolean isEvenDivision = divided * divisor == dotlessDenominator;
				if (isEvenDivision && isPowerOfTwo(divided)) {
					tupletDivisor = divisor;
					break;
				}
			}

			// If the tuplet divisor is not a common one, use
			// the denominator directly as tuplet divisor.
			// This can produce some very unusual tuplets, but they
			// should be expressible in music notation.
			if (tupletDivisor == 1) {
				tupletDivisor = originalDenominator;
			}
		}

		// If the duration is expressible with the found dot count and
		// tuplet divisor, return that.
		if (isExpressible(durationFraction, dotCount, tupletDivisor)) {
			return create(durationFraction, dotCount, tupletDivisor);
		}

		if (isBasicDenominator) {
			final int largestFitNumerator = Math.max((originalNumerator / 2) * 2, 1);
			return create(new Fraction(largestFitNumerator, durationFraction.getDenominator()), DEFAULT_DOT_COUNT,
					DEFAULT_TUPLET_DIVISOR);
		}

		if (originalNumerator > tupletDivisor) {
			// If the given fraction is not expressible as a duration with the given
			// dot count and tuplet divisor, find the largest duration that can be fitted
			// into the fraction with the tuplet information.

			int coefficient = tupletDivisor != 1 ? tupletDivisor : Math.max(durationFraction.getDenominator() / 2, 1);

			int largestFitNumerator = Math.max(coefficient * (originalNumerator / coefficient), 1);
			Fraction largestFit = new Fraction(largestFitNumerator, durationFraction.getDenominator());

			return create(largestFit, DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR);
		}

		return create(new Fraction(1, originalDenominator), 0, tupletDivisor);
	}

	/**
	 * Returns the total summed duration of the given durations.
	 * The given iterable of durations must not be empty.
	 * <p>
	 * Addition can create durations that are not directly expressible as a single
	 * notation symbol, therefore this method does not retain dot count or
	 * tuplet divisor information.
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

		return create(cumulatedDur, DEFAULT_DOT_COUNT, DEFAULT_TUPLET_DIVISOR);
	}

	private boolean isPowerOfTwo(int number) {
		return number > 0 && ((number & (number - 1)) == 0);
	}
}
