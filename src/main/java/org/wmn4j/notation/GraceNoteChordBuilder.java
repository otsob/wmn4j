/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class for building {@link GraceNoteChord} objects.
 * <p>
 * Instances of this class are not thread-safe.
 */
public final class GraceNoteChordBuilder implements OrnamentalBuilder {

	private final List<GraceNoteBuilder> noteBuilders;

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
	 */
	public void add(GraceNoteBuilder graceNoteBuilder) {
		noteBuilders.add(graceNoteBuilder);
	}

	@Override
	public GraceNoteChord build() {
		return GraceNoteChord.of(noteBuilders.stream().map(GraceNoteBuilder::build).collect(Collectors.toList()));
	}
}
