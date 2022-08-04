/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.techniques;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Represents a playing technique marking.
 * <p>
 * This class and all its subclasses are immutable.
 */
public sealed class Technique permits NumericTechnique, TextualTechnique {

	/**
	 * Defines the type of the playing technique.
	 */
	public enum Type {
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

	/**
	 * Returns a new plain technique with the given type.
	 *
	 * @param type the type of the technique
	 * @return new plain technique with the given type
	 */
	public static Technique of(Type type) {
		return new Technique(type);
	}

	/**
	 * Returns a new technique marking of a textual type.
	 *
	 * @param type the type of the technique
	 * @param text the text of the technique
	 * @return a new technique marking of a textual type
	 */
	public static Technique of(Type type, String text) {
		return new TextualTechnique(type, text);
	}

	/**
	 * Returns a new technique marking with a number in it.
	 *
	 * @param type   the type of the technique
	 * @param number the number in the technique marking
	 * @return a new technique marking with a number in it
	 */
	public static Technique of(Type type, int number) {
		return new NumericTechnique(type, number);
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

	/**
	 * Returns the text in this technique marking if this is a textual technique marking.
	 *
	 * @return the text in this technique marking if this is a textual technique marking
	 */
	public Optional<String> getText() {
		return Optional.empty();
	}

	/**
	 * Returns the number in this technique marking if this is a numeric technique marking.
	 *
	 * @return the number in this technique marking if this is a numeric technique marking
	 */
	public OptionalInt getNumber() {
		return OptionalInt.empty();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Technique technique = (Technique) o;
		return type == technique.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(type);
	}
}
