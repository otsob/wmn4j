package org.wmn4j.notation;

/**
 * Represents an ornament, such as a trill or mordent. This class represents ornaments that are linked to
 * a single note, connected notations are represented using the class {@link Notation}.
 * <p>
 * This class is immutable.
 */
public final class Ornament {

	/**
	 * Defines the type of an ornament.
	 */
	public enum Type {
		/**
		 * Specifies a delayed and inverted turn (gruppetto).
		 */
		DELAYED_INVERTED_TURN,

		/**
		 * Specifies a delayed turn (gruppetto).
		 */
		DELAYED_TURN,

		/**
		 * Specifies an inverted mordent.
		 */
		INVERTED_MORDENT,

		/**
		 * Specifies an inverted turn (gruppetto).
		 */
		INVERTED_TURN,

		/**
		 * Specifies a mordent.
		 */
		MORDENT,

		/**
		 * Specifies a single tremolo marking.
		 */
		SINGLE_TREMOLO,

		/**
		 * Specifies a double tremolo marking.
		 */
		DOUBLE_TREMOLO,

		/**
		 * Specifies a triple tremolo marking.
		 */
		TRIPLE_TREMOLO,

		/**
		 * Specifies a trill marking.
		 */
		TRILL,

		/**
		 * Specifies a turn (gruppetto).
		 */
		TURN
	}

	/**
	 * Returns an ornament of the given type.
	 *
	 * @param type the type of the ornament
	 * @return an ornament of the given type
	 */
	public static Ornament of(Type type) {
		return new Ornament(type);
	}

	private final Type type;

	private Ornament(Type type) {
		this.type = type;
	}

	/**
	 * Returns the type of this ornament.
	 *
	 * @return the type of this ornament
	 */
	public Type getType() {
		return type;
	}
}
