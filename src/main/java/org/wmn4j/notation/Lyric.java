/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.Objects;

/**
 * Represents a fragment of lyrics associated with single note.
 * <p>
 * This class is immutable.
 */
public final class Lyric {

	/**
	 * Default separator to use for separating elided words from each other.
	 */
	public static final String ELISION_SEPARATOR = "\uE550";

	/**
	 * Specifies the type of the text content in the lyric.
	 */
	public enum Type {
		/**
		 * The lyric text contains the starting syllable of a longer word or extended lyric.
		 */
		START,

		/**
		 * The lyric text contains an independent syllable or word within a single note.
		 */
		INDEPENDENT,

		/**
		 * The lyric text contains the ending of word that is elided with a following syllable.
		 */
		ELIDED,

		/**
		 * The lyric text contains a middle syllable of a word.
		 */
		MIDDLE,

		/**
		 * The lyric starts an extended syllable.
		 */
		EXTENDED,

		/**
		 * The lyric is an extension of the previous syllable and the text in it should not be
		 * repeated.
		 */
		EXTENSION,

		/**
		 * The lyric text contains the ending syllable of a word.
		 */
		END
	}

	private final String text;
	private final Type type;

	/**
	 * Returns a lyric instance with the given values.
	 *
	 * @param text the text content of the lyrics
	 * @param type the type of the text content
	 * @return a lyric instance with the given values
	 */
	public static Lyric of(String text, Type type) {
		return new Lyric(text, type);
	}

	private Lyric(String text, Type type) {
		this.text = Objects.requireNonNull(text);
		this.type = Objects.requireNonNull(type);
	}

	/**
	 * Returns the text content of this lyric.
	 * <p>
	 * In most cases the text content is a single syllable.
	 * In elided lyrics the text content may contain multiple syllables
	 * that are elided together.
	 * <p>
	 * If this lyric is an extension, then the text is equal to the previous
	 * syllable that this lyric extends.
	 *
	 * @return the text content of the lyric
	 */
	public String getText() {
		return text;
	}

	/**
	 * Returns the type of this lyric.
	 *
	 * @return the type of this lyric
	 */
	public Type getType() {
		return type;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof Lyric)) {
			return false;
		}

		Lyric lyric = (Lyric) o;
		return Objects.equals(text, lyric.text) && type == lyric.type;
	}

	@Override
	public int hashCode() {
		return Objects.hash(text, type);
	}

	@Override
	public String toString() {
		return "Lyric{" + "text='" + text + '\'' + ", type=" + type + '}';
	}
}
