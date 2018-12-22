/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Barline types.
 */
public enum Barline {
	/**
	 * Specifies no barline.
	 */
	NONE,

	/**
	 * Specifies a normal barline.
	 */
	SINGLE,

	/**
	 * Specifies a double barline.
	 */
	DOUBLE,

	/**
	 * Specifies a repeat on the left side.
	 */
	REPEAT_LEFT,

	/**
	 * Specifies a repeat on the right side.
	 */
	REPEAT_RIGHT,

	/**
	 * Specifies a final barline.
	 */
	FINAL,

	/**
	 * Specifies a dashed barline.
	 */
	DASHED,

	/**
	 * Specifies a thick barline.
	 */
	THICK,

	/**
	 * Specifies an invisible barline.
	 */
	INVISIBLE
}
