/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import java.util.Set;

/**
 * MusicXML tags and attributes. The name of the variables matches the tag string as closely as possible.
 */
final class Tags {

	static final String ABOVE = "above";
	static final String ACCENT = "accent";
	static final String ACCIDENTAL = "accidental";
	static final String ACTUAL_NOTES = "actual-notes";
	static final String ALTER = "alter";
	static final String ARPEGGIATE = "arpeggiate";
	static final String ARRANGER = "arranger";
	static final String ARTICULATIONS = "articulations";
	static final String ATTRIBUTES = "attributes";

	static final String BACKUP = "backup";
	static final String BACKWARD = "backward";
	static final String BARLINE = "barline";
	static final String BAR_STYLE = "bar-style";
	static final String BEAM = "beam";
	static final String BEATS = "beats";
	static final String BEAT_TYPE = "beat-type";
	static final String BOTTOM = "bottom";
	static final String BREATH_MARK = "breath-mark";

	static final String C = "C";
	static final String CAESURA = "caesura";
	static final String CANCEL = "cancel";
	static final String CHORD = "chord";
	static final String CLEF = "clef";
	static final String CLEF_OCTAVE_CHANGE = "clef-octave-change";
	static final String CODA = "coda";
	static final String COMMON = "common";
	static final String COMPOSER = "composer";
	static final String CONTINUE = "continue";
	static final String CREATOR = "creator";
	static final String CREDIT = "credit";
	static final String CUE = "cue";
	static final String CUT = "cut";

	static final String DASHED = "dashed";
	static final String DELAYED_INVERTED_TURN = "delayed-inverted-turn";
	static final String DELAYED_TURN = "delayed-turn";
	static final String DEFAULTS = "defaults";
	static final String DETACHED_LEGATO = "detached-legato";
	static final String DIRECTION = "direction";
	static final String DIRECTIVE = "directive";
	static final String DISPLAY_OCTAVE = "display-octave";
	static final String DISPLAY_STEP = "display-step";
	static final String DIVISIONS = "divisions";
	static final String DOIT = "doit";
	static final String DOT = "dot";
	static final String DOTTED = "dotted";
	static final String DOTTED_NOTE = "dotted-note";
	static final String DOWN = "down";
	static final String DURATION = "duration";

	static final String ENCODING = "encoding";
	static final String ENCODING_DATE = "encoding-date";
	static final String ENDING = "ending";

	static final String F = "F";
	static final String FALLOFF = "falloff";
	static final String FERMATA = "fermata";
	static final String FIFTHS = "fifths";
	static final String FIGURED_BASS = "figured-bass";
	static final String FOR_PART = "for-part";
	static final String FORWARD = "forward";

	static final String G = "G";
	static final String GLISSANDO = "glissando";
	static final String GRACE = "grace";
	static final String GROUP = "group";

	static final String HARMONY = "harmony";
	static final String HEAVY = "heavy";
	static final String HEAVY_LIGHT = "heavy-light";

	static final String ID = "id";
	static final String IDENTIFICATION = "identification";
	static final String INSTRUMENT = "instrument";
	static final String INSTRUMENTS = "instruments";
	static final String INTERCHANGEABLE = "interchangeable";
	static final String INVERTED_MORDENT = "inverted-mordent";
	static final String INVERTED_TURN = "inverted-turn";

	static final String KEY = "key";
	static final String KEY_STEP = "key-step";
	static final String KEY_ALTER = "key-alter";
	static final String KEY_ACCIDENTAL = "key-accidental";
	static final String KEY_OCTAVE = "key-octave";

	static final String LEFT = "left";
	static final String LIGHT_HEAVY = "light-heavy";
	static final String LIGHT_LIGHT = "light-light";
	static final String LINE = "line";
	static final String LINE_TYPE = "line-type";
	static final String LOCATION = "location";
	static final String LYRIC = "lyric";

	static final String MEASURE = "measure";
	static final String MEASURE_STYLE = "measure-style";
	static final String MISCELLANEOUS = "miscellaneous";
	static final String MODE = "mode";
	static final String MORDENT = "mordent";
	static final String MOVEMENT_NUMBER = "movement-number";
	static final String MOVEMENT_TITLE = "movement-title";

	static final String NEW_SYSTEM = "new-system";
	static final String NO = "no";
	static final String NONE = "none";
	static final String NON_ARPEGGIATE = "non-arpeggiate";
	static final String NORMAL_NOTES = "normal-notes";
	static final String NOTATIONS = "notations";
	static final String NOTE = "note";
	static final String NOTEHEAD = "notehead";
	static final String NOTEHEAD_TEXT = "notehead-text";
	static final String NOTE_1024TH = "1024th";
	static final String NOTE_128TH = "128th";
	static final String NOTE_16TH = "16th";
	static final String NOTE_256TH = "256th";
	static final String NOTE_32TH = "32nd";
	static final String NOTE_512TH = "512th";
	static final String NOTE_64TH = "64th";
	static final String NOTE_BREVE = "breve";
	static final String NOTE_EIGHTH = "eighth";
	static final String NOTE_HALF = "half";
	static final String NOTE_LONG = "long";
	static final String NOTE_MAXIMA = "maxima";
	static final String NOTE_QUARTER = "quarter";
	static final String NOTE_WHOLE = "whole";
	static final String NUMBER = "number";

	static final String OCTAVE = "octave";
	static final String OFFSET = "offset";
	static final String OPUS = "opus";
	static final String ORNAMENTS = "ornaments";

	static final String PART = "part";
	static final String PART_ABBREVIATION = "part-abbreviation";
	static final String PART_ABBREVIATION_DISPLAY = "part-abbreviation-display";
	static final String PART_GROUP = "part-group";
	static final String PART_LINK = "part-link";
	static final String PART_LIST = "part-list";
	static final String PART_NAME = "part-name";
	static final String PART_NAME_DISPLAY = "part-name-display";
	static final String PART_SYMBOL = "part-symbol";
	static final String PERCUSSION = "percussion";
	static final String PITCH = "pitch";
	static final String PLACEMENT = "placement";
	static final String PLOP = "plop";
	static final String PRINT = "print";
	static final String PRINT_OBJECT = "print-object";

	static final String REGULAR = "regular";
	static final String RELATION = "relation";
	static final String REPEAT = "repeat";
	static final String REST = "rest";
	static final String RIGHT = "right";
	static final String RIGHTS = "rights";

	static final String SCOOP = "scoop";
	static final String SCORE_PART = "score-part";
	static final String SCORE_PARTWISE = "score-partwise";
	static final String SEGNO = "segno";
	static final String SENZA_MISURA = "senza-misura";
	static final String SIGN = "sign";
	static final String SINGLE_NUMBER = "single-number";
	static final String SLASH = "slash";
	static final String SLIDE = "slide";
	static final String SLUR = "slur";
	static final String SOFTWARE = "software";
	static final String SOLID = "solid";
	static final String SOURCE = "source";
	static final String SPICCATO = "spiccato";
	static final String STACCATISSIMO = "staccatissimo";
	static final String STACCATO = "staccato";
	static final String STAFF = "staff";
	static final String STAFF_DETAILS = "staff-details";
	static final String START = "start";
	static final String STAVES = "staves";
	static final String STEM = "stem";
	static final String STEP = "step";
	static final String STOP = "stop";
	static final String STRESS = "stress";
	static final String STRONG_ACCENT = "strong-accent";
	static final String SYMBOL = "symbol";

	static final String TENUTO = "tenuto";
	static final String TIE = "tie";
	static final String TIED = "tied";
	static final String TIME = "time";
	static final String TIME_MODIFICATION = "time-modification";
	static final String TOP = "top";
	static final String TRANSPOSE = "transpose";
	static final String TREMOLO = "tremolo";
	static final String TRILL_MARK = "trill-mark";
	static final String TURN = "turn";
	static final String TYPE = "type";

	static final String UNPITCHED = "unpitched";
	static final String UNSTRESS = "unstress";
	static final String UP = "up";

	static final String VERSION = "version";
	static final String VOICE = "voice";

	static final String WAVY = "wavy";
	static final String WAVY_LINE = "wavy-line";
	static final String WORDS = "words";
	static final String WORK = "work";
	static final String WORK_NUMBER = "work-number";
	static final String WORK_TITLE = "work-title";

	static final String YES = "yes";

	static final Set<String> CONNECTED_NOTATIONS = Set.of(TIED, SLUR, GLISSANDO, SLIDE, ARPEGGIATE, NON_ARPEGGIATE);

	private Tags() {
		throw new UnsupportedOperationException("Not meant to be instantiated");
	}
}
