/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.Lyric;

import java.util.ArrayList;
import java.util.List;

final class LyricBuffer {
	private List<String> texts;
	private Lyric.Type type;
	private boolean startsExtension;

	LyricBuffer() {
		texts = new ArrayList<>();
		startsExtension = false;
	}

	void setStartsExtension(boolean startsExtension) {
		this.startsExtension = startsExtension;
	}

	boolean startsExtension() {
		return startsExtension;
	}

	String getJoinedText() {
		return String.join("", texts);
	}

	void addText(String text) {
		texts.add(text);
	}

	void setType(Lyric.Type type) {
		this.type = type;
	}

	Lyric.Type getType() {
		return type;
	}
}
