/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.techniques;

import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Pitch;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

/**
 * Represents a playing technique marking.
 * <p>
 * This class and all its subclasses are immutable.
 */
public sealed class Technique permits NumericTechnique, TextualTechnique, ComplexTechnique {

	/**
	 * Defines the type of the playing technique.
	 */
	public enum Type {
		/**
		 * Up bow on a stringed instrument.
		 */
		UP_BOW(false, false, false),

		/**
		 * Down bow on a stringed instrument.
		 */
		DOWN_BOW(false, false, false),

		/**
		 * Harmonic on a stringed instrument.
		 */
		HARMONIC(false, false, true),

		/**
		 * Open string on a stringed instrument.
		 */
		OPEN_STRING(false, false, false),

		/**
		 * Thumb position on a stringed instrument (e.g., cello).
		 */
		THUMB_POSITION(false, false, false),

		/**
		 * Fingering marking (number).
		 */
		FINGERING(false, true, false),

		/**
		 * Indicates the plucking finger on a plucked string instrument.
		 */
		PLUCK(true, false, false),

		/**
		 * Indicates double tongue technique on a wind instrument.
		 */
		DOUBLE_TONGUE(false, false, false),

		/**
		 * Indicates triple tongue technique on a wind instrument.
		 */
		TRIPLE_TONGUE(false, false, false),

		/**
		 * Indicates stopped on a wind instrument (e.g., french horn).
		 */
		STOPPED(false, false, false),

		/**
		 * Indicates snap pizzicato.
		 */
		SNAP_PIZZICATO(false, false, false),

		/**
		 * Indicates fret number.
		 */
		FRET(false, true, false),

		/**
		 * Indicates string of a stringed instrument as a number.
		 */
		STRING(false, true, false),

		/**
		 * Indicates hammer-on on a stringed instrument (e.g., guitar).
		 */
		HAMMER_ON(true, false, false),

		/**
		 * Indicates pull-off on a stringed instrument (e.g., guitar).
		 */
		PULL_OFF(true, false, false),

		/**
		 * Indicates a bend (e.g., on a guitar).
		 */
		BEND(false, false, true),

		/**
		 * Indicates tapping technique (e.g., on a guitar).
		 */
		TAP(true, false, false),

		/**
		 * Indicates use of heel on an organ pedal.
		 */
		HEEL(false, false, false),

		/**
		 * Indicates use of toe on an organ pedal.
		 */
		TOE(false, false, false),

		/**
		 * Indicates use of fingernails on a plucked string instrument (e.g., harp).
		 */
		FINGERNAILS(false, false, false),

		/**
		 * Indicates hole markings on a wind instrument.
		 */
		HOLE(false, false, true),

		/**
		 * Defines an arrow symbol.
		 */
		ARROW(false, false, true),

		/**
		 * Indicates a handbell technique.
		 */
		HANDBELL(true, false, false),

		/**
		 * Indicates a bend symbol used with brass instruments.
		 */
		BRASS_BEND(false, false, false),

		/**
		 * Indicates a flip symbol used with brass instruments.
		 */
		FLIP(false, false, false),

		/**
		 * Indicates a smear technique used with brass instruments.
		 */
		SMEAR(false, false, false),

		/**
		 * Indicates an open symbol (a circle).
		 */
		OPEN(false, false, false),

		/**
		 * Indicates a half-muted technique (e.f. used with horn).
		 */
		HALF_MUTED(false, false, false),

		/**
		 * Indicates use of a harmon mute with brass instruments.
		 */
		HARMON_MUTE(false, false, true),

		/**
		 * Represents the golpe symbol that is used for indicating tapping the pick guard in guitar music.
		 */
		GOLPE(false, false, false),

		/**
		 * Indicates a textual technique marking.
		 */
		OTHER(true, false, false);

		private final boolean isTextual;
		private final boolean isNumeric;
		private final boolean isComplex;

		Type(boolean isTextual, boolean isNumeric, boolean isComplex) {
			this.isTextual = isTextual;
			this.isNumeric = isNumeric;
			this.isComplex = isComplex;
		}

		/**
		 * Returns true if this is a plain type of technique marking with only a symbol.
		 *
		 * @return true if this is a plain type of technique marking with only a symbol
		 */
		public boolean isSymbolic() {
			return !(isTextual || isNumeric || isComplex);
		}

		/**
		 * Returns true if this is a textual technique marking.
		 *
		 * @return true if this is a textual technique marking
		 */
		public boolean isTextual() {
			return isTextual;
		}

		/**
		 * Returns true if this is a numeric technique marking.
		 *
		 * @return true if this is a numeric technique marking
		 */
		public boolean isNumeric() {
			return isNumeric;
		}

		/**
		 * Returns true if this is a complex technique marking.
		 * <p>
		 * Complex technique markings are types like artificial harmonics that
		 * contain more complicate additional data.
		 *
		 * @return true if this is a complex technique marking
		 */
		public boolean isComplex() {
			return isComplex;
		}
	}

	/**
	 * Denotes hole/mute positions on wind instruments.
	 */
	public enum Opening {
		/**
		 * Fully open.
		 */
		OPEN,

		/**
		 * Fully closed.
		 */
		CLOSED,

		/**
		 * Half open.
		 */
		HALF_OPEN;
	}

	/**
	 * Denotes the additional values of more complex technique types.
	 */
	public enum AdditionalValue {
		/**
		 * Denotes releasing a bend at the associated offset from bend start.
		 */
		BEND_RELEASE(Duration.class),

		/**
		 * The alteration in semitones for a bend technique.
		 * Allows microtonal bends.
		 */
		BEND_SEMITONES(Double.class),

		/**
		 * The base pitch of a harmonic.
		 */
		HARMONIC_BASE_PITCH(Pitch.class),

		/**
		 * The sounding pitch of a harmonic.
		 */
		HARMONIC_SOUNDING_PITCH(Pitch.class),

		/**
		 * The pitch at which a string is pressed to produce a harmonic.
		 */
		HARMONIC_TOUCHING_PITCH(Pitch.class),

		/**
		 * True for artificial harmonics.
		 */
		IS_ARTIFICIAL_HARMONIC(Boolean.class),

		/**
		 * True for natural harmonics.
		 */
		IS_NATURAL_HARMONIC(Boolean.class),

		/**
		 * Denotes a pre-bend.
		 */
		PRE_BEND(Boolean.class),

		/**
		 * Denotes bending using a whammy bar on guitar with a text direction.
		 */
		BEND_WITH_BAR(String.class),

		/**
		 * Denotes the position of a harmon mute.
		 */
		HARMON_MUTE_POSITION(Opening.class);

		private final Class<?> valueClass;

		AdditionalValue(Class<?> valueClass) {
			this.valueClass = valueClass;
		}

		/**
		 * Returns the class of the value.
		 *
		 * @return the class of the value
		 */
		public Class<?> getValueClass() {
			return valueClass;
		}
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

	/**
	 * Returns a new technique marking with additional values.
	 *
	 * @param type             the type of the techniqy
	 * @param additionalValues the additional values of the technique marking
	 * @return a new technique marking with additional values
	 */
	public static Technique of(Type type, Map<AdditionalValue, Object> additionalValues) {
		return new ComplexTechnique(type, additionalValues);
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

	/**
	 * Returns the value of the additional technique value if present.
	 * <p>
	 * These are available only for complex technique types.
	 *
	 * @param value     the name of the value
	 * @param valueType the type of the value
	 * @param <T>       the type of the value
	 * @return the value of the additional technique value if present
	 */
	public <T> Optional<T> getValue(AdditionalValue value, Class<T> valueType) {
		return Optional.empty();
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

	@Override
	public String toString() {
		return type.toString();
	}
}
