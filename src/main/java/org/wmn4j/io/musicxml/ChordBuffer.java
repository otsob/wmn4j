/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.NoteBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Class for handling the reading of chords in MusicXML.
 */
final class ChordBuffer {
	private List<NoteBuilder> chordBuffer = new ArrayList<>();
	private int voice;
	private Notation.Type arpeggiation;

	ChordBuffer() {
	}

	void addNote(NoteBuilder noteBuilder, int voice) {
		this.chordBuffer.add(noteBuilder);
		this.voice = voice;
	}

	/**
	 * Empties the content of this chord buffer as a suitable DurationalBuilder
	 * to the correct voice in the given builder.
	 *
	 * @param builder the builder to which the content is placed.
	 */
	void flushTo(MeasureBuilder builder) {
		if (!this.chordBuffer.isEmpty()) {
			if (this.chordBuffer.size() > 1) {
				if (arpeggiation != null) {
					Notation arpeggio = Notation.of(arpeggiation);
					chordBuffer.sort(Comparator.comparing(NoteBuilder::getPitch));

					NoteBuilder prev = null;

					for (NoteBuilder note : chordBuffer) {
						if (prev != null) {
							prev.connectWith(arpeggio, note);
						}

						prev = note;
					}
				}

				builder.addToVoice(this.voice, new ChordBuilder(chordBuffer));
			} else if (this.chordBuffer.size() == 1) {
				final NoteBuilder noteBuilder = this.chordBuffer.get(0);
				builder.addToVoice(this.voice, noteBuilder);
			}

			this.chordBuffer = new ArrayList<>();
			this.arpeggiation = null;
		}
	}

	void setArpeggiation(Notation.Type arpeggiation) {
		this.arpeggiation = arpeggiation;
	}
}
