/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir.pattern_discovery;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Otso Björklund
 */
public class TEC {

	private final PointPattern pattern;
	private final List<NoteEventVector> translators;

	public TEC(PointPattern pattern, List<NoteEventVector> translators) {
		this.pattern = pattern;
		this.translators = translators;
	}

	public PointPattern getPattern() {
		return this.pattern;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();

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
