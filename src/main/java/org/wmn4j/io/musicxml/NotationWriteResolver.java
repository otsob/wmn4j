/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.wmn4j.notation.Notation;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Helper class for keeping track of connected notations when writing MusicXML.
 */
class NotationWriteResolver {
	private static final int MAX_NOTATION_NUMBER = 6;
	private static final Map<Notation.Type, String> NOTATION_TYPES_WITH_START_STOP = Map.of(
			Notation.Type.TIE, Tags.TIED,
			Notation.Type.SLUR, Tags.SLUR,
			// Slide type is ignored here as it is practically same as glissando
			Notation.Type.GLISSANDO, Tags.GLISSANDO,
			Notation.Type.NON_ARPEGGIATE, Tags.NON_ARPEGGIATE);

	private static final Map<Notation.Type, String> ARPEGGIATION_TYPES = Map.of(
			Notation.Type.ARPEGGIATE, Tags.ARPEGGIATE,
			Notation.Type.ARPEGGIATE_DOWN, Tags.ARPEGGIATE,
			Notation.Type.ARPEGGIATE_UP, Tags.ARPEGGIATE);

	private static final Map<Notation.Style, String> NOTATION_STYLES = Map.of(
			Notation.Style.SOLID, Tags.SOLID,
			Notation.Style.DASHED, Tags.DASHED,
			Notation.Style.DOTTED, Tags.DOTTED,
			Notation.Style.WAVY, Tags.WAVY);

	private final XMLStreamWriter writer;
	private Integer nextAvailableNotationNumber = Integer.valueOf(1);

	NotationWriteResolver(XMLStreamWriter writer) {
		this.writer = writer;
	}

	private Map<Notation, Integer> unresolvedNotations = new HashMap<>();
	private Set<Integer> usedNotationNumbers = new HashSet<>(MAX_NOTATION_NUMBER);

	boolean canStartOrStop(Notation.Type type) {
		return NOTATION_TYPES_WITH_START_STOP.containsKey(type);
	}

	boolean isArpeggiation(Notation.Type type) {
		return ARPEGGIATION_TYPES.containsKey(type);
	}

	void writeArpeggiationElement(Notation notation) throws XMLStreamException {
		Notation.Type type = notation.getType();

		// Use a constant for arpeggio notation number and do not set it to the element attribute
		// because multiple different arpeggiations rarely happen for the same note, and
		// number resolving is harder to implement for arpeggios.
		unresolvedNotations.put(notation, 0);

		writer.writeEmptyElement(ARPEGGIATION_TYPES.get(type));

		if (type.equals(Notation.Type.ARPEGGIATE_DOWN)) {
			writer.writeAttribute(Tags.DIRECTION, Tags.DOWN);
		}

		if (type.equals(Notation.Type.ARPEGGIATE_UP)) {
			writer.writeAttribute(Tags.DIRECTION, Tags.UP);
		}
	}

	void writeNotationStartElement(Notation notation) throws XMLStreamException {

		writer.writeEmptyElement(NOTATION_TYPES_WITH_START_STOP.get(notation.getType()));

		final Integer notationNumber = getNextAvailableNotationNumber();
		unresolvedNotations.put(notation, notationNumber);

		writer.writeAttribute(Tags.NUMBER, notationNumber.toString());

		if (notation.getType().equals(Notation.Type.NON_ARPEGGIATE)) {
			writer.writeAttribute(Tags.TYPE, Tags.BOTTOM);
		} else {
			writer.writeAttribute(Tags.TYPE, Tags.START);
			if (NOTATION_STYLES.containsKey(notation.getStyle())) {
				writer.writeAttribute(Tags.LINE_TYPE, NOTATION_STYLES.get(notation.getStyle()));
			}
		}
	}

	void writeNotationStopElement(Notation notation) throws XMLStreamException {
		if (unresolvedNotations.containsKey(notation)) {

			writer.writeEmptyElement(NOTATION_TYPES_WITH_START_STOP.get(notation.getType()));
			writer.writeAttribute(Tags.NUMBER, unresolvedNotations.get(notation).toString());

			usedNotationNumbers.remove(unresolvedNotations.get(notation));
			unresolvedNotations.remove(notation);

			if (notation.getType().equals(Notation.Type.NON_ARPEGGIATE)) {
				writer.writeAttribute(Tags.TYPE, Tags.TOP);
			} else {
				writer.writeAttribute(Tags.TYPE, Tags.STOP);
				if (NOTATION_STYLES.containsKey(notation.getStyle())) {
					writer.writeAttribute(Tags.LINE_TYPE, NOTATION_STYLES.get(notation.getStyle()));
				}
			}
		}

	}

	private Integer getNextAvailableNotationNumber() {
		Integer availableNumber = nextAvailableNotationNumber;
		usedNotationNumbers.add(availableNumber);

		for (int next = 1; next <= MAX_NOTATION_NUMBER; ++next) {
			if (!usedNotationNumbers.contains(next)) {
				nextAvailableNotationNumber = next;
				break;
			}
		}

		return availableNumber;
	}
}
