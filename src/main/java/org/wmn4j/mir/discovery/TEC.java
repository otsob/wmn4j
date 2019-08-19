/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import java.util.List;

/**
 *
 * @author Otso Björklund
 */
class TEC {

	private final PointPattern pattern;
	private final List<NoteEventVector> translators;

	TEC(PointPattern pattern, List<NoteEventVector> translators) {
		this.pattern = pattern;
		this.translators = translators;
	}

	PointPattern getPattern() {
		return this.pattern;
	}

	@Override
	public String toString() {
		final StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("pattern: {");
		for (NoteEventVector p : this.pattern.getPoints()) {
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
