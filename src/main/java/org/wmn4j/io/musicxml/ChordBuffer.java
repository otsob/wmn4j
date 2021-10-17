/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

/**
 * Class for handling the reading of chords in MusicXML.
 */
final class ChordBuffer {
	private List<NoteBuilder> chordBuffer = new ArrayList<>();
	private int staff;
	private int voice;

	void addNote(NoteBuilder noteBuilder, int staff, int voice) {
		this.chordBuffer.add(noteBuilder);
		this.staff = staff;
		this.voice = voice;
	}

	/**
	 * Empties the content of this chord buffer as a suitable DurationalBuilder
	 * to the correct voice in the given builder.
	 *
	 * @param builder the builder to which the content is placed.
	 * @return returns the builder that was added to the measure builder. Returns null if buffer was empty.
	 */
	DurationalBuilder flushTo(MeasureBuilder builder, BiConsumer<Integer, Integer> arpeggioResolver) {
		DurationalBuilder addedBuilder = null;

		if (!this.chordBuffer.isEmpty()) {
			if (this.chordBuffer.size() > 1) {
				arpeggioResolver.accept(staff, voice);
				addedBuilder = new ChordBuilder(chordBuffer);
			} else if (this.chordBuffer.size() == 1) {
				addedBuilder = this.chordBuffer.get(0);
			}

			builder.addToVoice(this.voice, addedBuilder);
			this.chordBuffer = new ArrayList<>();
		}

		return addedBuilder;
	}
}
