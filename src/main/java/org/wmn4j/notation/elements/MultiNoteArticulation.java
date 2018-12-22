/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.elements;

/**
 * Class that represents markings that span across multiple notes, such as slurs
 * and glissando. Is immutable.
 *
 * @author Otso Björklund
 */
public class MultiNoteArticulation {

	/**
	 * The type of the articulation.
	 */
	public enum Type {
	/**
	 * Specifies a slur.
	 */
	SLUR,

	/**
	 * Specifies a glissando marking.
	 */
	GLISSANDO
	}

	private final Type type;
	// TODO: Keep track of all notes and positions that are affected by this.

	/**
	 * @param type Type of articulation.
	 */
	public MultiNoteArticulation(Type type) {
		this.type = type;
	}

	/**
	 * @return The type of this.
	 */
	public Type getType() {
		return type;
	}

}
