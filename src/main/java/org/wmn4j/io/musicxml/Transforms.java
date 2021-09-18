/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wmn4j.notation.Articulation;
import org.wmn4j.notation.Barline;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.TimeSignature;

import java.util.Objects;

/**
 * Transforms for transforming wmn4j objects from values parsed from MusicXML.
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

	static Barline barStyleToBarline(String barStyleString, String repeatString) {
		switch (barStyleString) {
			case MusicXmlTags.BARLINE_STYLE_DASHED:
				return Barline.DASHED;
			case MusicXmlTags.BARLINE_STYLE_HEAVY:
				return Barline.THICK;
			case MusicXmlTags.BARLINE_STYLE_HEAVY_LIGHT:
				return Barline.REPEAT_LEFT;
			case MusicXmlTags.BARLINE_STYLE_INVISIBLE:
				return Barline.INVISIBLE;
			case MusicXmlTags.BARLINE_STYLE_LIGHT_HEAVY: {
				if (Objects.equals(repeatString, Tags.BACKWARD)) {
					return Barline.REPEAT_RIGHT;
				} else {
					return Barline.FINAL;
				}
			}
			case MusicXmlTags.BARLINE_STYLE_LIGHT_LIGHT:
				return Barline.DOUBLE;
			default:
				return Barline.SINGLE;
		}
	}

	static Clef.Symbol signToClefSymbol(String clefSign) {
		Clef.Symbol type;
		switch (clefSign) {
			case MusicXmlTags.CLEF_F:
				type = Clef.Symbol.F;
				break;
			case MusicXmlTags.CLEF_C:
				type = Clef.Symbol.C;
				break;
			case MusicXmlTags.CLEF_PERC:
				type = Clef.Symbol.PERCUSSION;
				break;
			case MusicXmlTags.CLEF_G: // Fall through
			default:
				type = Clef.Symbol.G;
				break;
		}

		return type;
	}

	private Transforms() {
		throw new UnsupportedOperationException("Not meant to be instantiated");
	}
}
