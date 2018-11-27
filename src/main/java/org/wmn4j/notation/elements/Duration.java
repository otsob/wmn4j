/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

import java.math.BigInteger;
import java.util.List;

/**
 * The <code>Duration</code> class represents the duration of any musical object
 * with a duration such as a note or a rest. Durations are handled as rational
 * numbers that tell what fraction of a whole note the duration is just like in
 * normal music terminology. For example, the duration of a quarter note is
 * handled as the rational number 1/4. The rational number is always reduced to
 * the lowest possible numerator and denominator. This class is immutable.
 * 
 * @author Otso Björklund
 */
public class Duration implements Comparable<Duration> {

	private final int numerator;
	private final int denominator;

	/**
	 * Returns a <code>Duration</code> object. The numerator and denominator must be
	 * at least 1.
	 * 
	 * @throws IllegalArgumentException
	 *             if numerator or denominator are less than 1.
	 * @param numerator
	 *            the numerator part of the duration.
	 * @param denominator
	 *            the denominator part of the duration.
	 * @return a Duration object.
	 */
	public static Duration getDuration(int numerator, int denominator) {

		if (numerator < 1)
			throw new IllegalArgumentException("numerator must be at least 1");
		if (denominator < 1)
			throw new IllegalArgumentException("denominator must be at least 1");

		// TODO: Come up with a more effective way of finding GCD
		if (numerator != 1) {
			int gcd = BigInteger.valueOf(numerator).gcd(BigInteger.valueOf(denominator)).intValue();
			numerator = numerator / gcd;
			denominator = denominator / gcd;
		}
		// TODO: Implement caching instead of creating new.
		return new Duration(numerator, denominator);
	}

	/**
	 * Constructor for the class. The constructor is private, to get a Duration
	 * object use the static method {@link #getDuration(int, int) getDuration}.
	 * 
	 * @param numerator
	 *            the numerator part of the duration.
	 * @param denominator
	 *            the denominator part of the duration.
	 */
	private Duration(int numerator, int denominator) {
		this.numerator = numerator;
		this.denominator = denominator;
	}

	/**
	 * @return the numerator part of the duration.
	 */
	public int getNumerator() {
		return this.numerator;
	}

	/**
	 * @return the denominator part of the duration.
	 */
	public int getDenominator() {
		return this.denominator;
	}

	/**
	 * Compares this <code>Duration</code> to <code>Object o</code> for equality. If
	 * <code>Object o</code> is of class <code>Duration</code> and has the same
	 * numerator and denominator as this, then <code>o</code> is equal to this.
	 * 
	 * @param o
	 *            an object against which this Duration is compared for equality.
	 * @return true if <code>Object o</code> is a <code>Duration</code> with the
	 *         same numerator and denominator as this, false otherwise.
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (!(o instanceof Duration))
			return false;

		Duration other = (Duration) o;

		return (this.numerator == other.numerator) && (this.denominator == other.denominator);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + this.numerator;
		hash = 83 * hash + this.denominator;
		return hash;
	}

	/**
	 * Durations are represented as strings of form
	 * <code>(numerator/denominator)</code>.
	 * 
	 * @return string representation of this Duration.
	 */
	@Override
	public String toString() {
		return "(" + this.numerator + "/" + this.denominator + ")";
	}

	/**
	 * Extend this Duration by adding a dot (add half of this Duration to itself).
	 * Does not change this Duration.
	 * 
	 * @return Duration that is this incremented by half of this.
	 */
	public Duration addDot() {
		return this.add(Duration.getDuration(this.numerator, 2 * this.denominator));
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
		return (float) this.numerator / this.denominator;
	}

	/**
	 * Returns a Duration that is the sum of this and other. Does not change this
	 * Duration (this class is immutable).
	 * 
	 * @param other
	 *            the Duration to be added to this.
	 * @return a Duration that is the sum of this and other.
	 */
	public Duration add(Duration other) {
		int nom = this.numerator * other.denominator + this.denominator * other.numerator;
		int denom = this.denominator * other.denominator;

		return getDuration(nom, denom);
	}

	/**
	 * Returns a Duration that is the this other minus other given as parameter.
	 * Does not change this Duration (this class is immutable).
	 * 
	 * @param other
	 *            the Duration to be subtracted from this.
	 * @return a Duration that is this other minus other.
	 */
	public Duration subtract(Duration other) {
		int nom = this.numerator * other.denominator - this.denominator * other.numerator;
		int denom = this.denominator * other.denominator;

		return getDuration(nom, denom);
	}

	/**
	 * Returns a Duration that is this duration multiplied by multiplier. Multiplier
	 * must be at least 1. Does not change this Duration (this class is immutable).
	 * 
	 * @throws IllegalArgumentException
	 *             if multiplier is less than 1.
	 * @param multiplier
	 *            the factor by which this duration should be multiplied.
	 * @return a Duration that is this duration multiplied by multiplier.
	 */
	public Duration multiplyBy(int multiplier) {
		if (multiplier < 1)
			throw new IllegalArgumentException("multiplier must be at least 1. Was " + multiplier);

		return getDuration(this.numerator * multiplier, this.denominator);
	}

	/**
	 * Returns a Duration that is this duration divided by divider. Divider must be
	 * at least 1. Does not change this Duration (this class is immutable).
	 * 
	 * @throws IllegalArgumentException
	 *             if divider is less than 1.
	 * @param divider
	 *            the factor by which this duration should be divided.
	 * @return a Duration that is this duration divided by divider.
	 */
	public Duration divideBy(int divider) {
		if (divider < 1)
			throw new IllegalArgumentException("divider must be at least 1. Was " + divider);

		return getDuration(this.numerator, this.denominator * divider);
	}

	/**
	 * @param other
	 *            Duration to which this is compared.
	 * @return true if this is longer than other, otherwise false.
	 */
	public boolean longerThan(Duration other) {
		return this.compareTo(other) > 0;
	}

	/**
	 * @param other
	 *            Duration to which this is compared.
	 * @return true if this is shorter than other, otherwise false.
	 */
	public boolean shorterThan(Duration other) {
		return this.compareTo(other) < 0;
	}

	/**
	 * Compare the length of this Duration and other.
	 * 
	 * @param other
	 *            the Duration against which this is compared for length.
	 * @return negative integer if this is shorter than other, positive integer if
	 *         this is longer than other, 0 if this is equal to other.
	 */
	@Override
	public int compareTo(Duration other) {
		return this.numerator * other.denominator - other.numerator * this.denominator;
	}

	/**
	 * Get the total summed duration of the durations in the List.
	 * 
	 * @throws IllegalArgumentException
	 *             if durations is empty.
	 * @param durations
	 *            List of <code>Duration</code> objects.
	 * @return The sum of durations in List.
	 */
	public static Duration sumOf(List<Duration> durations) {
		if (durations.isEmpty())
			throw new IllegalArgumentException("Cannot compute sum of durations from empty list");

		if (durations.size() == 1)
			return durations.get(0);

		// TODO: Optimize this.
		Duration cumulatedDur = durations.get(0);
		for (int i = 1; i < durations.size(); ++i) {
			cumulatedDur = cumulatedDur.add(durations.get(i));
		}

		return cumulatedDur;
	}
}
