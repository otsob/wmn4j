/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wmn4j.notation.ConnectableBuilder;
import org.wmn4j.notation.GraceNoteBuilder;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.NoteBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Keeps track of connected notations and resolves them when all notation elements
 * connected by the notation have been read from the MusicXML file. Notations are tracked
 * separately for each staff and each voice within a staff.
 */
class NotationResolver {

	private static final Set<Notation.Type> ARPEGGIATIONS = EnumSet.of(Notation.Type.ARPEGGIATE,
			Notation.Type.ARPEGGIATE_DOWN, Notation.Type.ARPEGGIATE_UP, Notation.Type.NON_ARPEGGIATE);

	private static final Notation.Style DEFAULT_STYLE = Notation.Style.SOLID;
	private static final Logger LOG = LoggerFactory.getLogger(NotationResolver.class);

	static int parseNotationNumber(String numberString) {
		// Default to zero when the number attribute is not set
		return numberString == null ? 0 : Integer.parseInt(numberString);
	}

	private PartContext currentPartContext;

	private final Map<Integer, Map<Integer, Map<Unresolved, Unresolved>>> unresolvedNotations = new HashMap<>();

	boolean hasUnresolvedNotations(int staffNumber, int voiceNumber) {
		if (unresolvedNotations.containsKey(staffNumber)) {
			return !unresolvedNotations.get(staffNumber).getOrDefault(voiceNumber, Collections.emptyMap()).isEmpty();
		}

		return false;
	}

	void startOrContinueNotation(int notationNumber, Notation.Type notationType,
			Notation.Style notationStyle, ConnectableBuilder builder) {

		final int staffNumber = currentPartContext.getStaff();
		final int voiceNumber = currentPartContext.getVoice();

		if (!unresolvedNotations.containsKey(staffNumber)) {
			unresolvedNotations.put(staffNumber, new HashMap<>());
		}

		final var unresolvedForVoices = unresolvedNotations.get(staffNumber);
		if (!unresolvedForVoices.containsKey(voiceNumber)) {
			unresolvedForVoices.put(voiceNumber, new HashMap<>());
		}

		final var unresolveds = unresolvedForVoices.get(voiceNumber);
		Unresolved unresolved = new Unresolved(notationNumber, notationType, notationStyle);
		// Get the existing one if there is one, otherwise put the new unresolved.
		if (!unresolveds.containsKey(unresolved)) {
			unresolveds.put(unresolved, unresolved);
		}
		unresolved = unresolveds.get(unresolved);
		unresolved.addConnectedBuilder(builder);
	}

	void continueOngoingNotations(ConnectableBuilder builder) {
		final int staffNumber = currentPartContext.getStaff();
		final int voiceNumber = currentPartContext.getVoice();
		if (!hasUnresolvedNotations(staffNumber, voiceNumber)) {
			return;
		}

		final var unresolvedForVoice = unresolvedNotations.get(staffNumber).get(voiceNumber);
		for (Map.Entry<Unresolved, Unresolved> entry : unresolvedForVoice.entrySet()) {
			final var ongoingNotation = entry.getValue();
			final var type = ongoingNotation.notationType;
			// All other arpeggiation types apart from non-arpeggiate are explicitly set for each note, so
			// they should not be continued here.
			final boolean isContinuable = !type.isArpeggiation() || type.equals(Notation.Type.NON_ARPEGGIATE);
			if (!ongoingNotation.containsBuilder(builder) && isContinuable) {
				ongoingNotation.addConnectedBuilder(builder);
			}
		}
	}

	void resolveArpeggios(int staff, int voice) {
		if (!hasUnresolvedNotations(staff, voice)) {
			return;
		}

		final var unresolveds = unresolvedNotations.get(staff).get(voice);
		final Collection<Unresolved> unresolvedArpeggiations = unresolveds.keySet().stream()
				.filter(unresolved -> unresolved.notationType.isArpeggiation() && !unresolved.notationType.equals(
						Notation.Type.NON_ARPEGGIATE)).collect(Collectors.toList());

		for (Unresolved unresolvedArpeggiation : unresolvedArpeggiations) {
			// Check the size to avoid resolving arpeggios for chords that have just been started.
			if (unresolvedArpeggiation.connectedBuilders.size() > 1) {
				final ConnectableBuilder lastBuilder = unresolvedArpeggiation.popLastBuilder();
				endNotation(unresolvedArpeggiation.notationNumber, unresolvedArpeggiation.notationType, lastBuilder);
			}
		}
	}

	void endNotation(int notationNumber, Notation.Type notationType, ConnectableBuilder lastBuilder) {

		final int staffNumber = currentPartContext.getStaff();
		final int voiceNumber = currentPartContext.getVoice();

		if (!hasUnresolvedNotations(staffNumber, voiceNumber)) {
			LOG.warn("Trying to end notation in a voice with no notations, staff {}, voice {}, type {}", staffNumber,
					voiceNumber, notationType);
			return;
		}

		final var unresolvedsForVoice = unresolvedNotations.get(staffNumber).get(voiceNumber);
		final var unresolvedNotation = unresolvedsForVoice.getOrDefault(
				new Unresolved(notationNumber, notationType, DEFAULT_STYLE), null);
		if (unresolvedNotation != null) {
			unresolvedNotation.addConnectedBuilder(lastBuilder);
			unresolvedNotation.resolve();
			unresolvedsForVoice.remove(unresolvedNotation);
		} else {
			LOG.warn("Trying to end notation that has no builders, staff {}, voice {}, type {}, notation number {}",
					staffNumber, voiceNumber, notationType, notationNumber);
		}
	}

	void reset(PartContext partContext) {
		currentPartContext = partContext;
		currentPartContext.setArpeggioResolver(this::resolveArpeggios);
		unresolvedNotations.clear();
	}

	class Unresolved {

		private final int notationNumber;
		private final Notation.Type notationType;
		private final Notation.Style notationStyle;
		private final List<ConnectableBuilder> connectedBuilders;

		Unresolved(int notationNumber, Notation.Type notationType, Notation.Style notationStyle) {
			this.notationNumber = notationNumber;
			this.notationType = notationType;
			this.notationStyle = notationStyle;
			this.connectedBuilders = new ArrayList<>();
		}

		boolean containsBuilder(ConnectableBuilder builder) {
			return connectedBuilders.contains(builder);
		}

		void addConnectedBuilder(ConnectableBuilder builder) {
			connectedBuilders.add(builder);
		}

		void resolve() {
			final Notation notation = Notation.of(notationType, notationStyle);
			final int indexOfLast = connectedBuilders.size() - 1;
			for (int i = 0; i < indexOfLast; ++i) {
				ConnectableBuilder nextBuilder = connectedBuilders.get(i + 1);
				if (nextBuilder instanceof NoteBuilder) {
					connectedBuilders.get(i).connectWith(notation, (NoteBuilder) nextBuilder);
				} else if (nextBuilder instanceof GraceNoteBuilder) {
					connectedBuilders.get(i).connectWith(notation, (GraceNoteBuilder) nextBuilder);
				}
			}
		}

		ConnectableBuilder popLastBuilder() {
			final int lastIndex = connectedBuilders.size() - 1;
			final var lastBuilder = connectedBuilders.get(lastIndex);
			connectedBuilders.remove(lastBuilder);
			return lastBuilder;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}

			if (!(o instanceof Unresolved)) {
				return false;
			}

			// notationStyle is intentionally omitted.
			Unresolved other = (Unresolved) o;
			return notationNumber == other.notationNumber
					&& notationType.equals(other.notationType);
		}

		@Override
		public int hashCode() {
			return Objects.hash(notationNumber, notationType);
		}
	}
}
