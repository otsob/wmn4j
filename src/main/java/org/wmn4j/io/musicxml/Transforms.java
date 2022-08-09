/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durations;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.Lyric;
import org.wmn4j.notation.Notation;
import org.wmn4j.notation.Ornament;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.TimeSignature;
import org.wmn4j.notation.techniques.Technique;

import java.util.Objects;

/**
 * Transforms for transforming wmn4j objects from values parsed from MusicXML.
 * <p>
 * NOTE: Many of the methods in this class/namespace may return null.
 */
final class Transforms {

	private static final Logger LOG = LoggerFactory.getLogger(Transforms.class);

	static Pitch.Accidental alterToAccidental(int alter) {
		switch (alter) {
			case 0:
				return Pitch.Accidental.NATURAL;
			case 1:
				return Pitch.Accidental.SHARP;
			case 2:
				return Pitch.Accidental.DOUBLE_SHARP;
			case -1:
				return Pitch.Accidental.FLAT;
			case -2:
				return Pitch.Accidental.DOUBLE_FLAT;
		}

		LOG.warn("Unsupported alter value for pitch " + alter);
		return Pitch.Accidental.NATURAL;
	}

	static Pitch.Base stepToPitchBase(String stepString) {
		switch (stepString) {
			case "C":
				return Pitch.Base.C;
			case "D":
				return Pitch.Base.D;
			case "E":
				return Pitch.Base.E;
			case "F":
				return Pitch.Base.F;
			case "G":
				return Pitch.Base.G;
			case "A":
				return Pitch.Base.A;
			case "B": // Fall through
			default:
				return Pitch.Base.B;
		}
	}

	static Articulation stringToArticulation(String articulationString) {
		switch (articulationString) {
			case Tags.ACCENT:
				return Articulation.ACCENT;
			case Tags.BREATH_MARK:
				return Articulation.BREATH_MARK;
			case Tags.CAESURA:
				return Articulation.CAESURA;
			case Tags.FERMATA:
				return Articulation.FERMATA;
			case Tags.PLOP:
				return Articulation.SLIDE_IN_DOWN;
			case Tags.SCOOP:
				return Articulation.SLIDE_IN_UP;
			case Tags.FALLOFF:
				return Articulation.SLIDE_OUT_DOWN;
			case Tags.DOIT:
				return Articulation.SLIDE_OUT_UP;
			case Tags.SPICCATO:
				return Articulation.SPICCATO;
			case Tags.STACCATISSIMO:
				return Articulation.STACCATISSIMO;
			case Tags.STACCATO:
				return Articulation.STACCATO;
			case Tags.STRESS:
				return Articulation.STRESS;
			case Tags.STRONG_ACCENT:
				return Articulation.STRONG_ACCENT;
			case Tags.TENUTO:
				return Articulation.TENUTO;
			case Tags.DETACHED_LEGATO:
				return Articulation.TENUTO_STACCATO;
			case Tags.UNSTRESS:
				return Articulation.UNSTRESS;
		}

		return null;
	}

	static TimeSignature.Symbol symbolStringToTimeSigSymbol(String symbolString) {
		TimeSignature.Symbol symbol = TimeSignature.Symbol.NUMERIC;
		if (symbolString != null) {
			switch (symbolString) {
				case Tags.COMMON:
					symbol = TimeSignature.Symbol.COMMON;
					break;
				case Tags.CUT:
					symbol = TimeSignature.Symbol.CUT_TIME;
					break;
				case Tags.SINGLE_NUMBER:
					symbol = TimeSignature.Symbol.BEAT_NUMBER_ONLY;
					break;
				case Tags.NOTE:
					symbol = TimeSignature.Symbol.BEAT_DURATION_AS_NOTE;
					break;
				case Tags.DOTTED_NOTE:
					symbol = TimeSignature.Symbol.BEAT_DURATION_AS_DOTTED_NOTE;
					break;
				default:
					// Default value is already set on initialization.
					break;
			}
		}

		return symbol;
	}

	static KeySignature fifthsToKeySig(int fifths) {
		switch (fifths) {
			case 0:
				return KeySignatures.CMAJ_AMIN;
			case 1:
				return KeySignatures.GMAJ_EMIN;
			case 2:
				return KeySignatures.DMAJ_BMIN;
			case 3:
				return KeySignatures.AMAJ_FSHARPMIN;
			case 4:
				return KeySignatures.EMAJ_CSHARPMIN;
			case 5:
				return KeySignatures.BMAJ_GSHARPMIN;
			case 6:
				return KeySignatures.FSHARPMAJ_DSHARPMIN;
			case -1:
				return KeySignatures.FMAJ_DMIN;
			case -2:
				return KeySignatures.BFLATMAJ_GMIN;
			case -3:
				return KeySignatures.EFLATMAJ_CMIN;
			case -4:
				return KeySignatures.AFLATMAJ_FMIN;
			case -5:
				return KeySignatures.DFLATMAJ_BFLATMIN;
			case -6:
				return KeySignatures.GFLATMAJ_EFLATMIN;
		}

		return KeySignatures.CMAJ_AMIN;
	}

	static String timeSignatureTypeToString(TimeSignature.Symbol symbol) {

		switch (symbol) {
			case COMMON:
				return Tags.COMMON;
			case CUT_TIME:
				return Tags.CUT;
			case BEAT_NUMBER_ONLY:
				return Tags.SINGLE_NUMBER;
			case BEAT_DURATION_AS_NOTE:
				return Tags.NOTE;
			case BEAT_DURATION_AS_DOTTED_NOTE:
				return Tags.DOTTED_NOTE;
			case NUMERIC: // Fall through to default
			default:
				// Do not set symbol if NUMERIC
		}

		return null;
	}

	static Barline barStyleToBarline(String barStyleString, String repeatString) {
		switch (barStyleString) {
			case Tags.DASHED:
				return Barline.DASHED;
			case Tags.HEAVY:
				return Barline.THICK;
			case Tags.HEAVY_LIGHT:
				return Barline.REPEAT_LEFT;
			case Tags.NONE:
				return Barline.INVISIBLE;
			case Tags.LIGHT_HEAVY: {
				if (Objects.equals(repeatString, Tags.BACKWARD)) {
					return Barline.REPEAT_RIGHT;
				} else {
					return Barline.FINAL;
				}
			}
			case Tags.LIGHT_LIGHT:
				return Barline.DOUBLE;
			default:
				return Barline.SINGLE;
		}
	}

	static Clef.Symbol signToClefSymbol(String clefSign) {
		Clef.Symbol type;
		switch (clefSign) {
			case Tags.F:
				type = Clef.Symbol.F;
				break;
			case Tags.C:
				type = Clef.Symbol.C;
				break;
			case Tags.PERCUSSION:
				type = Clef.Symbol.PERCUSSION;
				break;
			case Tags.G: // Fall through
			default:
				type = Clef.Symbol.G;
				break;
		}

		return type;
	}

	static Ornament tagToOrnament(String tag) {

		if (tag == null) {
			return null;
		}

		switch (tag) {
			case Tags.DELAYED_INVERTED_TURN:
				return Ornament.of(Ornament.Type.DELAYED_INVERTED_TURN);
			case Tags.DELAYED_TURN:
				return Ornament.of(Ornament.Type.DELAYED_TURN);
			case Tags.INVERTED_MORDENT:
				return Ornament.of(Ornament.Type.INVERTED_MORDENT);
			case Tags.INVERTED_TURN:
				return Ornament.of(Ornament.Type.INVERTED_TURN);
			case Tags.MORDENT:
				return Ornament.of(Ornament.Type.MORDENT);
			case Tags.TRILL_MARK:
				return Ornament.of(Ornament.Type.TRILL);
			case Tags.TURN:
				return Ornament.of(Ornament.Type.TURN);
			default:
				return null;
		}
	}

	static Ornament lineNumbersToTremolo(int tremoloLines) {
		switch (tremoloLines) {
			case 2:
				return Ornament.of(Ornament.Type.DOUBLE_TREMOLO);
			case 3:
				return Ornament.of(Ornament.Type.TRIPLE_TREMOLO);
			case 1: // Fall through
			default:
				return Ornament.of(Ornament.Type.SINGLE_TREMOLO);
		}
	}

	static Score.Attribute creatorTypeToAttribute(String type) {
		if (type == null) {
			return Score.Attribute.COMPOSER;
		}

		switch (type) {
			case Tags.ARRANGER:
				return Score.Attribute.ARRANGER;
			case Tags.COMPOSER: // Fall through.
			default:
				return Score.Attribute.COMPOSER;
		}
	}

	static Notation.Type stringToNotationType(String typeString, String arpeggioDirection) {
		switch (typeString) {
			case Tags.TIED:
				return Notation.Type.TIE;
			case Tags.SLUR:
				return Notation.Type.SLUR;
			case Tags.GLISSANDO:
			case Tags.SLIDE: // Slide is practically always glissando in MusicXML, fall through.
				return Notation.Type.GLISSANDO;
			case Tags.ARPEGGIATE:
				if (arpeggioDirection == null) {
					return Notation.Type.ARPEGGIATE;
				}

				if (Tags.DOWN.equals(arpeggioDirection)) {
					return Notation.Type.ARPEGGIATE_DOWN;
				}

				if (Tags.UP.equals(arpeggioDirection)) {
					return Notation.Type.ARPEGGIATE_UP;
				}
			case Tags.NON_ARPEGGIATE:
				return Notation.Type.NON_ARPEGGIATE;
		}

		LOG.warn("Tried to parse unsupported notation type: " + typeString);
		return Notation.Type.SLUR;
	}

	static Notation.Style stringToNotationStyle(String styleString) {
		if (styleString == null) {
			return Notation.Style.SOLID;
		}

		switch (styleString) {
			case Tags.DASHED:
				return Notation.Style.DASHED;
			case Tags.DOTTED:
				return Notation.Style.DOTTED;
			case Tags.WAVY:
				return Notation.Style.WAVY;
			case Tags.SOLID: // Fall through.
			default:
				return Notation.Style.SOLID;
		}
	}

	static Duration noteTypeToDuration(String noteType) {
		if (noteType == null) {
			return null;
		}

		switch (noteType) {
			case Tags.NOTE_1024TH:
				return Duration.of(1, 1024);
			case Tags.NOTE_512TH:
				return Duration.of(1, 512);
			case Tags.NOTE_256TH:
				return Duration.of(1, 256);
			case Tags.NOTE_128TH:
				return Duration.of(1, 128);
			case Tags.NOTE_64TH:
				return Duration.of(1, 64);
			case Tags.NOTE_32TH:
				return Duration.of(1, 32);
			case Tags.NOTE_16TH:
				return Durations.SIXTEENTH;
			case Tags.NOTE_EIGHTH:
				return Durations.EIGHTH;
			case Tags.NOTE_QUARTER:
				return Durations.QUARTER;
			case Tags.HALF:
				return Durations.HALF;
			case Tags.NOTE_WHOLE:
				return Durations.WHOLE;
			case Tags.NOTE_BREVE:
				return Duration.of(2, 1);
			case Tags.NOTE_LONG:
				return Duration.of(4, 1);
			case Tags.NOTE_MAXIMA:
				return Duration.of(8, 1);
			default:
				return null;
		}
	}

	static String clefSymbolToString(Clef.Symbol symbol) {
		switch (symbol) {
			case G:
				return Tags.G;
			case F:
				return Tags.F;
			case C:
				return Tags.C;
			case PERCUSSION:
				return Tags.PERCUSSION;
			default:
				throw new IllegalStateException("Unexpected clef symbol: " + symbol);
		}
	}

	static String barlineStyleToString(Barline barline) {
		switch (barline) {
			case SINGLE:
				return Tags.REGULAR;
			case DOUBLE:
				return Tags.LIGHT_LIGHT;
			case REPEAT_LEFT:
				return Tags.HEAVY_LIGHT;
			case REPEAT_RIGHT: // Fall through
			case FINAL:
				return Tags.LIGHT_HEAVY;
			case DASHED:
				return Tags.DASHED;
			case THICK:
				return Tags.HEAVY;
			case INVISIBLE:  // Fall through
			case NONE:
				return Tags.NONE;
			default:
				throw new IllegalStateException("Unexpected barline type: " + barline);
		}
	}

	static String articulationToTag(Articulation articulation) {
		switch (articulation) {
			case ACCENT:
				return Tags.ACCENT;
			case BREATH_MARK:
				return Tags.BREATH_MARK;
			case CAESURA:
				return Tags.CAESURA;
			case SLIDE_IN_DOWN:
				return Tags.PLOP;
			case SLIDE_IN_UP:
				return Tags.SCOOP;
			case SLIDE_OUT_DOWN:
				return Tags.FALLOFF;
			case SLIDE_OUT_UP:
				return Tags.DOIT;
			case SPICCATO:
				return Tags.SPICCATO;
			case STACCATISSIMO:
				return Tags.STACCATISSIMO;
			case STACCATO:
				return Tags.STACCATO;
			case STRESS:
				return Tags.STRESS;
			case STRONG_ACCENT:
				return Tags.STRONG_ACCENT;
			case TENUTO:
				return Tags.TENUTO;
			case TENUTO_STACCATO:
				return Tags.DETACHED_LEGATO;
			case UNSTRESS:
				return Tags.UNSTRESS;
			case FERMATA:
				// Fermata is not treated as an articulation in MusicXml, so fermatas ar handled elsewhere.
				// Fall through to null
			default:
				return null;
		}
	}

	static String ornamentToTag(Ornament.Type ornamentType) {
		switch (ornamentType) {
			case DELAYED_INVERTED_TURN:
				return Tags.DELAYED_INVERTED_TURN;
			case DELAYED_TURN:
				return Tags.DELAYED_TURN;
			case INVERTED_MORDENT:
				return Tags.INVERTED_MORDENT;
			case INVERTED_TURN:
				return Tags.INVERTED_TURN;
			case MORDENT:
				return Tags.MORDENT;
			case SINGLE_TREMOLO: // Fall through
			case DOUBLE_TREMOLO: // Fall through
			case TRIPLE_TREMOLO:
				return Tags.TREMOLO;
			case TRILL:
				return Tags.TRILL_MARK;
			case TURN:
				return Tags.TURN;
			default:
				return null;
		}
	}

	static Technique.Type tagToTechniqueType(String tag) {
		switch (tag) {
			case Tags.UP_BOW:
				return Technique.Type.UP_BOW;
			case Tags.DOWN_BOW:
				return Technique.Type.DOWN_BOW;
			case Tags.HARMONIC:
				return Technique.Type.HARMONIC;
			case Tags.OPEN_STRING:
				return Technique.Type.OPEN_STRING;
			case Tags.THUMB_POSITION:
				return Technique.Type.THUMB_POSITION;
			case Tags.FINGERING:
				return Technique.Type.FINGERING;
			case Tags.PLUCK:
				return Technique.Type.PLUCK;
			case Tags.DOUBLE_TONGUE:
				return Technique.Type.DOUBLE_TONGUE;
			case Tags.TRIPLE_TONGUE:
				return Technique.Type.TRIPLE_TONGUE;
			case Tags.STOPPED:
				return Technique.Type.STOPPED;
			case Tags.SNAP_PIZZICATO:
				return Technique.Type.SNAP_PIZZICATO;
			case Tags.FRET:
				return Technique.Type.FRET;
			case Tags.STRING:
				return Technique.Type.STRING;
			case Tags.HAMMER_ON:
				return Technique.Type.HAMMER_ON;
			case Tags.PULL_OFF:
				return Technique.Type.PULL_OFF;
			case Tags.BEND:
				return Technique.Type.BEND;
			case Tags.TAP:
				return Technique.Type.TAP;
			case Tags.HEEL:
				return Technique.Type.HEEL;
			case Tags.TOE:
				return Technique.Type.TOE;
			case Tags.FINGERNAILS:
				return Technique.Type.FINGERNAILS;
			case Tags.HOLE:
				return Technique.Type.HOLE;
			case Tags.ARROW:
				return Technique.Type.ARROW;
			case Tags.HANDBELL:
				return Technique.Type.HANDBELL;
			case Tags.BRASS_BEND:
				return Technique.Type.BRASS_BEND;
			case Tags.FLIP:
				return Technique.Type.FLIP;
			case Tags.SMEAR:
				return Technique.Type.SMEAR;
			case Tags.OPEN:
				return Technique.Type.OPEN;
			case Tags.HALF_MUTED:
				return Technique.Type.HALF_MUTED;
			case Tags.GOLPE:
				return Technique.Type.GOLPE;
			case Tags.OTHER_TECHNICAL:
				return Technique.Type.OTHER;
			default:
				return null;
		}
	}

	static String techniqueTypeToTag(Technique.Type type) {
		switch (type) {
			case UP_BOW:
				return Tags.UP_BOW;
			case DOWN_BOW:
				return Tags.DOWN_BOW;
			case HARMONIC:
				return Tags.HARMONIC;
			case OPEN_STRING:
				return Tags.OPEN_STRING;
			case THUMB_POSITION:
				return Tags.THUMB_POSITION;
			case FINGERING:
				return Tags.FINGERING;
			case PLUCK:
				return Tags.PLUCK;
			case DOUBLE_TONGUE:
				return Tags.DOUBLE_TONGUE;
			case TRIPLE_TONGUE:
				return Tags.TRIPLE_TONGUE;
			case STOPPED:
				return Tags.STOPPED;
			case SNAP_PIZZICATO:
				return Tags.SNAP_PIZZICATO;
			case FRET:
				return Tags.FRET;
			case STRING:
				return Tags.STRING;
			case HAMMER_ON:
				return Tags.HAMMER_ON;
			case PULL_OFF:
				return Tags.PULL_OFF;
			case BEND:
				return Tags.BEND;
			case TAP:
				return Tags.TAP;
			case HEEL:
				return Tags.HEEL;
			case TOE:
				return Tags.TOE;
			case FINGERNAILS:
				return Tags.FINGERNAILS;
			case HOLE:
				return Tags.HOLE;
			case ARROW:
				return Tags.ARROW;
			case HANDBELL:
				return Tags.HANDBELL;
			case BRASS_BEND:
				return Tags.BRASS_BEND;
			case FLIP:
				return Tags.FLIP;
			case SMEAR:
				return Tags.SMEAR;
			case OPEN:
				return Tags.OPEN;
			case HALF_MUTED:
				return Tags.HALF_MUTED;
			case HARMON_MUTE:
				return Tags.HARMON_MUTE;
			case GOLPE:
				return Tags.GOLPE;
			case OTHER:
				return Tags.OTHER_TECHNICAL;
			default:
				return null;
		}
	}

	static Technique.Opening textToOpeningType(String text) {
		switch (text) {
			case Tags.YES:
				return Technique.Opening.CLOSED;
			case Tags.NO:
				return Technique.Opening.OPEN;
			case Tags.HALF:
				return Technique.Opening.HALF_OPEN;
			default:
				return null;
		}
	}

	static String openingTypeToText(Technique.Opening opening) {
		switch (opening) {
			case CLOSED:
				return Tags.YES;
			case OPEN:
				return Tags.NO;
			case HALF_OPEN:
				return Tags.HALF;
			default:
				return null;
		}
	}

	static Lyric.Type syllabicToLyricType(String tag) {
		switch (tag) {
			case Tags.BEGIN:
				return Lyric.Type.START;
			case Tags.END:
				return Lyric.Type.END;
			case Tags.MIDDLE:
				return Lyric.Type.MIDDLE;
			case Tags.SINGLE:
				return Lyric.Type.INDEPENDENT;
			default:
				return null;
		}
	}

	private Transforms() {
		throw new UnsupportedOperationException("Not meant to be instantiated");
	}
}
