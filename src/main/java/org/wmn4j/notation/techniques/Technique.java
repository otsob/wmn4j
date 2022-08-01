/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.techniques;

import java.util.Objects;

/**
 * Represents a playing technique marking.
 * <p>
 * This class and all its subclasses are immutable.
 */
public final class Technique {

	/**
	 * Defines the type of the playing technique.
	 */
	enum Type {

		/**
		 * Up bow on a stringed instrument.
		 */
		UP_BOW,

		/**
		 * Down bow on a stringed instrument.
		 */
		DOWN_BOW,

		/**
		 * Harmonic on a stringed instrument.
		 */
		HARMONIC,

		/**
		 * Open string on a stringed instrument.
		 */
		OPEN_STRING,

		/**
		 * Thumb position on a stringed instrument (e.g., cello).
		 */
		THUMB_POSITION,

		/**
		 * Fingering marking (number).
		 */
		FINGERING,

		/**
		 * Indicates the plucking finger on a plucked string instrument.
		 */
		PLUCK,

		/**
		 * Indicates double tongue technique on a wind instrument.
		 */
		DOUBLE_TONGUE,

		/**
		 * Indicates triple tongue technique on a wind instrument.
		 */
		TRIPLE_TONGUE,

		/**
		 * Indicates stopped on a wind instrument (e.g., french horn).
		 */
		STOPPED,

		/**
		 * Indicates snap pizzicato.
		 */
		SNAP_PIZZICATO,

		/**
		 * Indicates fret number.
		 */
		FRET,

		/**
		 * Indicates string of a stringed instrument as a number.
		 */
		STRING,

		/**
		 * Indicates hammer-on on a stringed instrument (e.g., guitar).
		 */
		HAMMER_ON,

		/**
		 * Indicates pull-off on a stringed instrument (e.g., guitar).
		 */
		PULL_OFF,

		/**
		 * Indicates a bend (e.g., on a guitar).
		 */
		BEND,

		/**
		 * Indicates tapping technique (e.g., on a guitar).
		 */
		TAP,

		/**
		 * Indicates use of heel on an organ pedal.
		 */
		HEEL,

		/**
		 * Indicates use of toe on an organ pedal.
		 */
		TOE,

		/**
		 * Indicates use of fingernails on a plucked string instrument (e.g., harp).
		 */
		FINGERNAILS,

		/**
		 * Indicates hole markings on a wind instrument.
		 */
		HOLE,

		/**
		 * Defines an arrow symbol.
		 */
		ARROW,

		/**
		 * Indicates a handbell symbol.
		 */
		HANDBELL,

		/**
		 * Indicates a bend symbol used with brass instruments.
		 */
		BRASS_BEND,

		/**
		 * Indicates a flip symbol used with brass instruments.
		 */
		FLIP,

		/**
		 * Indicates a smear technique used with brass instruments.
		 */
		SMEAR,

		/**
		 * Indicates an open symbol (a circle).
		 */
		OPEN,

		/**
		 * Indicates a half-muted technique (e.f. used with horn).
		 */
		HALF_MUTED,

		/**
		 * Indicates use of a harmon mute with brass instruments.
		 */
		HARMON_MUTE,

		/**
		 * Represents the golpe symbol that is used for indicating tapping the pick guard in guitar music.
		 */
		GOLPE,

		/**
		 * Indicates a textual technique marking.
		 */
		OTHER
	}

	private final Type type;

	Technique(Type type) {
		this.type = Objects.requireNonNull(type);
	}

	/**
	 * Returns the type of this playing technique marking.
	 *
	 * @return the type of this playing technique marking
	 */
	public Type getType() {
		return type;
	}
}
