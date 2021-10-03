/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.GraceNoteBuilder;
import org.wmn4j.notation.GraceNoteChordBuilder;
import org.wmn4j.notation.OrnamentalBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

class OrnamentalBuffer {
	private List<GraceNoteBuilder> chordBuffer = new ArrayList<>();
	private List<OrnamentalBuilder> unconnectedOrnamentals = new ArrayList<>();
	private BiConsumer<Integer, Integer> arpeggioResolver;
	private int staff;
	private int voice;

	void addNote(GraceNoteBuilder noteBuilder, boolean hasChordTag, int staff, int voice,
			BiConsumer<Integer, Integer> arpeggioResolver) {
		this.staff = staff;
		this.voice = voice;
		this.arpeggioResolver = arpeggioResolver;

		if (!hasChordTag && !chordBuffer.isEmpty()) {
			flushBufferToUnconnected();
		}

		this.chordBuffer.add(noteBuilder);
	}

	private void flushBufferToUnconnected() {
		if (chordBuffer.size() == 1) {
			unconnectedOrnamentals.add(popGraceNoteBuilder());
		} else {
			arpeggioResolver.accept(staff, voice);
			unconnectedOrnamentals.add(popGraceNoteChordBuilder());
		}
	}

	private GraceNoteChordBuilder popGraceNoteChordBuilder() {
		GraceNoteChordBuilder graceNoteChord = new GraceNoteChordBuilder();
		for (GraceNoteBuilder graceNote : chordBuffer) {
			graceNoteChord.add(graceNote);
		}

		this.chordBuffer.clear();
		return graceNoteChord;
	}

	private GraceNoteBuilder popGraceNoteBuilder() {
		GraceNoteBuilder builder = chordBuffer.get(0);
		chordBuffer.clear();
		return builder;
	}

	boolean isEmpty() {
		return unconnectedOrnamentals.isEmpty() && chordBuffer.isEmpty();
	}

	List<OrnamentalBuilder> popOrnamentalBuilders() {
		flushBufferToUnconnected();
		final List<OrnamentalBuilder> builders = new ArrayList<>(unconnectedOrnamentals);
		unconnectedOrnamentals.clear();
		return builders;
	}
}

