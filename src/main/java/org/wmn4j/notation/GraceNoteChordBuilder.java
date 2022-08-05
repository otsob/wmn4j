/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for building {@link GraceNoteChord} objects.
 * <p>
 * Instances of this class are not thread-safe.
 */
public final class GraceNoteChordBuilder implements OrnamentalBuilder, Iterable<GraceNoteBuilder> {

	private final List<GraceNoteBuilder> noteBuilders;
	private GraceNoteChord cachedChord;

	/**
	 * Constructor that creates an empty builder.
	 */
	public GraceNoteChordBuilder() {
		noteBuilders = new ArrayList<>();
	}

	/**
	 * Adds the given grace note builder to this chord builder.
	 *
	 * @param graceNoteBuilder the grace note builder that is added to this builder
	 * @return reference to this
	 */
	public GraceNoteChordBuilder add(GraceNoteBuilder graceNoteBuilder) {
		noteBuilders.add(graceNoteBuilder);
		return this;
	}

	void setPrincipalNote(NoteBuilder principalNote) {
		noteBuilders.forEach(noteBuilder -> noteBuilder.setPrincipalNote(principalNote));
	}

	void setCachedChord(GraceNoteChord chord) {
		cachedChord = chord;
	}

	@Override
	public GraceNoteChord build() {
		if (cachedChord == null) {
			cachedChord = GraceNoteChord
					.of(noteBuilders.stream().map(GraceNoteBuilder::build).collect(Collectors.toList()));
		}

		return cachedChord;
	}

	@Override
	public Iterator<GraceNoteBuilder> iterator() {
		return noteBuilders.iterator();
	}
}
