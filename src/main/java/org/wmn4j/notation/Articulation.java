/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

/**
 * Articulations that affect a single note.
 */
public enum Articulation {
	/**
	 * Specifies an accent marking.
	 */
	ACCENT,

	/**
	 * Specifies a breath mark.
	 */
	BREATH_MARK,

	/**
	 * Specifies a caesura marking.
	 */
	CAESURA,

	/**
	 * Specifies a fermata.
	 */
	FERMATA,

	/**
	 * Specifies a slide down into a note.
	 */
	SLIDE_IN_DOWN,

	/**
	 * Specifies a slide up into a note.
	 */
	SLIDE_IN_UP,

	/**
	 * Specifies a marking representing an indeterminate slide down after a note.
	 */
	SLIDE_OUT_DOWN,

	/**
	 * Specifies a marking representing an indeterminate slide up after a note.
	 */
	SLIDE_OUT_UP,

	/**
	 * Specifies a spiccato marking.
	 */
	SPICCATO,

	/**
	 * Specifies a staccatissimo marking.
	 */
	STACCATISSIMO,

	/**
	 * Specifies a staccato marking.
	 */
	STACCATO,

	/**
	 * Specifies a stressed note marking.
	 */
	STRESS,

	/**
	 * Specifies a strong accent marking.
	 */
	STRONG_ACCENT,

	/**
	 * Specifies a tenuto marking.
	 */
	TENUTO,

	/**
	 * Specifies a marking that combines a tenuto line and a staccato dot symbol.
	 */
	TENUTO_STACCATO,

	/**
	 * Specifies an unstressed note marking.
	 */
	UNSTRESS
}
