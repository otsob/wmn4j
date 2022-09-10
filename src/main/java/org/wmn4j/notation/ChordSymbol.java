/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */

/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * Represents a chord symbol consisting of the pitch name and additional markings.
 * <p>
 * Chords are represented as a base triad (or dyad) whose type is specified by {@link Base}
 * and extensions specified by the {@link ChordSymbol.Extension} type.
 * <p>
 * This class is immutable.
 */
public final class ChordSymbol {

	private static final String MIN_STR = "min";
	private static final String DIM_STR = "dim";

	/**
	 * The type of the base triad or dyad on which the chord is built.
	 */
	public enum Base {
		/**
		 * Denotes a major triad base.
		 */
		MAJOR(""),

		/**
		 * Denotes a minor triad base.
		 */
		MINOR(MIN_STR),

		/**
		 * Denotes a diminished triad base.
		 */
		DIMINISHED(DIM_STR),

		/**
		 * Denotes an augmented triad base.
		 */
		AUGMENTED("+"),

		/**
		 * Denotes an open fifth without a third (a power chord).
		 */
		POWER_CHORD("5"),

		/**
		 * Denotes a triad with a suspended second (sus2).
		 */
		SUSPENDED_SECOND("sus2"),

		/**
		 * Denotes a triad with a suspended fourth (sus4).
		 */
		SUSPENDED_FOURTH("sus4");

		private final String asString;

		Base(String asString) {
			this.asString = asString;
		}
	}

	/**
	 * Represents a chord extension such as a seventh.
	 */
	public static final class Extension {

		/**
		 * Denotes the type of the extension.
		 */
		public enum Type {
			/**
			 * Denotes an "add" specifier for the extension.
			 */
			ADD("add"),

			/**
			 * Denotes an "omit" specifier for the extension.
			 */
			OMIT("omit"),

			/**
			 * Denotes a "min" specifier for the extension.
			 * For example, used in a minor seventh chord such as <code>Cmin7</code>.
			 */
			MINOR(MIN_STR),

			/**
			 * Denotes a "maj" specifier for the extension.
			 * For example, used in a major seventh chord such as <code>CMaj7</code>.
			 */
			MAJOR("maj"),

			/**
			 * Denotes a numeric extension without an additional word.
			 * Used for so-called "dominant" chord types, such as a dominant seventh
			 * chord, e.g., <code>C7</code>.
			 */
			PLAIN(""),

			/**
			 * Denotes an <a href="https://en.wikipedia.org/wiki/Altered_chord">altered chord</a>.
			 */
			ALTERED("alt"),

			/**
			 * Denotes a diminished extension, e.g., in a fully diminished 7th chord.
			 */
			DIM(DIM_STR);

			private final String asString;

			Type(String asString) {
				this.asString = asString;
			}
		}

		private final Type type;
		private final Pitch.Accidental accidental;
		private final int number;

		private Extension(Type type, Pitch.Accidental accidental, int number) {
			this.type = Objects.requireNonNull(type);
			this.accidental = Objects.requireNonNull(accidental);
			this.number = number;
		}

		/**
		 * Returns the type of the extension.
		 *
		 * @return the type of the extension
		 */
		public Type getType() {
			return type;
		}

		/**
		 * Returns the accidental marking in front of the number marking.
		 * <p>
		 * Defaults to natural if there is no number associated with the extension.
		 *
		 * @return the accidental marking in front of the number marking
		 */
		public Pitch.Accidental getAccidental() {
			return accidental;
		}

		/**
		 * Returns the number associated with the extension if present, false otherwise.
		 * <p>
		 * Extensions such as "alt" are sometimes written without a number.
		 *
		 * @return the number associated with the extension if present, false otherwise
		 */
		public OptionalInt getNumber() {
			if (number <= 0) {
				return OptionalInt.empty();
			}

			return OptionalInt.of(number);
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Extension extension = (Extension) o;
			return number == extension.number && type == extension.type && accidental == extension.accidental;
		}

		@Override
		public int hashCode() {
			return Objects.hash(type, accidental, number);
		}

		@Override
		public String toString() {
			return type.asString + accidental.prettyString() + number;
		}
	}

	/**
	 * Returns a chord extension with the given attributes.
	 *
	 * @param type       the type of the extension
	 * @param accidental the accidental associated with the number (natural means no visible accidental)
	 * @param number     the number in the extension
	 * @return a chord extension with the given attributes
	 */
	public static Extension extension(Extension.Type type, Pitch.Accidental accidental, int number) {
		return new Extension(type, accidental, number);
	}

	/**
	 * Returns a chord extension without a number.
	 *
	 * @param type the type of the extension
	 * @return a chord extension without a number
	 */
	public static Extension extension(Extension.Type type) {
		return new Extension(type, Pitch.Accidental.NATURAL, 0);
	}

	private final PitchName root;
	private final PitchName bass;
	private final Base base;

	private final List<Extension> extensions;

	/**
	 * Returns a chord symbol with the given attributes.
	 *
	 * @param base       the type of the base triad/dyad of this chord symbol
	 * @param root       the root pitch name of the chord
	 * @param bass       the bass of the chord (can be null for non inverted chords)
	 * @param extensions the extensions of the chord (can be null/empty) in the order they are written from left to
	 *                   right
	 * @return a chord symbol with the given attributes
	 */
	public static ChordSymbol of(Base base, PitchName root, PitchName bass, Collection<Extension> extensions) {
		return new ChordSymbol(base, root, bass, extensions);
	}

	private ChordSymbol(Base base, PitchName root, PitchName bass, Collection<Extension> extensions) {
		this.base = Objects.requireNonNull(base);
		this.root = Objects.requireNonNull(root);
		this.bass = bass == null ? this.root : bass;
		if (extensions != null && !extensions.isEmpty()) {
			this.extensions = List.copyOf(extensions);
		} else {
			this.extensions = Collections.emptyList();
		}
	}

	/**
	 * Returns the type of the base triad/dyad of this chord symbol.
	 *
	 * @return the type of the base triad/dyad of this chord symbol
	 */
	public Base getBase() {
		return base;
	}

	/**
	 * Returns the root of this chord symbol.
	 *
	 * @return the root of this chord symbol
	 */
	public PitchName getRoot() {
		return root;
	}

	/**
	 * Returns the bass of this chord.
	 * <p>
	 * If this chord symbol does not denote an inversion, then
	 * this is equal to the root of the chord.
	 *
	 * @return the bass of this chord
	 */
	public PitchName getBass() {
		return bass;
	}

	/**
	 * Returns true if this is an inversion, false otherwise.
	 *
	 * @return true if this is an inversion, false otherwise
	 */
	public boolean isInversion() {
		return !bass.equals(root);
	}

	/**
	 * Returns an unmodifiable view of the extensions in this chord symbol.
	 *
	 * @return an unmodifiable view of the extensions in this chord symbol
	 */
	public List<Extension> getExtensions() {
		return extensions;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ChordSymbol that = (ChordSymbol) o;
		return root.equals(that.root) && bass.equals(that.bass) && base == that.base && extensions.equals(
				that.extensions);
	}

	@Override
	public int hashCode() {
		return Objects.hash(root, bass, base, extensions);
	}

	@Override
	public String toString() {
		final var builder = new StringBuilder();

		builder.append(root);
		builder.append(base.asString);

		for (var extension : extensions) {
			builder.append(extension);
		}

		if (isInversion()) {
			builder.append("/").append(bass);
		}

		return builder.toString();
	}
}
