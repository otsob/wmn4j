/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import java.util.Collections;
import java.util.List;

/**
 * Represents a translational equivalence class [1].
 * <p>
 * [1] Meredith, David, Lemström, Kjell, and Wiggins, Geraint A.:
 * Algorithms for discovering repeated patterns in multidimensional representations of polyphonic music.
 * Journal of New Music Research, 31(4):321-345, 2002.
 * <p>
 * This class is immutable.
 */
final class Tec {

	private final PointPattern pattern;
	private final List<NoteEventVector> translators;

	/**
	 * Constructor. The new instance takes ownership of the
	 * passed parameters.
	 *
	 * @param pattern     the pattern for this TEC
	 * @param translators the translators of this TEC
	 */
	Tec(PointPattern pattern, List<NoteEventVector> translators) {
		this.pattern = pattern;
		this.translators = Collections.unmodifiableList(translators);
	}

	PointPattern getPattern() {
		return this.pattern;
	}

	List<NoteEventVector> getTranslators() {
		return translators;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("pattern: {");
		for (NoteEventVector p : this.pattern) {
			strBuilder.append(p.toString()).append(", ");
		}
		strBuilder.replace(strBuilder.length() - 2, strBuilder.length(), "");
		strBuilder.append("}, translators: {");

		for (NoteEventVector t : this.translators) {
			strBuilder.append(t.toString()).append(", ");
		}
		strBuilder.replace(strBuilder.length() - 2, strBuilder.length(), "");
		strBuilder.append("}");

		return strBuilder.toString();
	}
}
