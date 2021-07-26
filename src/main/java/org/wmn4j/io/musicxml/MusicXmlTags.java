/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import java.util.Collections;
import java.util.Set;

final class MusicXmlTags {

	// Barline tags
	static final String BARLINE = "barline";
	static final String BARLINE_LOCATION = "location";
	static final String BARLINE_LOCATION_LEFT = "left";
	static final String BARLINE_LOCATION_RIGHT = "right";
	static final String BARLINE_REPEAT = "repeat";
	static final String BARLINE_REPEAT_DIR = "direction";
	static final String BARLINE_REPEAT_DIR_FORWARD = "forward";
	static final String BARLINE_REPEAT_DIR_BACKWARD = "backward";
	static final String BARLINE_STYLE = "bar-style";
	static final String BARLINE_STYLE_REGULAR = "regular";
	static final String BARLINE_STYLE_DASHED = "dashed";
	static final String BARLINE_STYLE_HEAVY = "heavy";
	static final String BARLINE_STYLE_HEAVY_LIGHT = "heavy-light";
	static final String BARLINE_STYLE_INVISIBLE = "none";
	static final String BARLINE_STYLE_LIGHT_HEAVY = "light-heavy";
	static final String BARLINE_STYLE_LIGHT_LIGHT = "light-light";

	// Clef tags
	static final String CLEF = "clef";
	static final String CLEF_SIGN = "sign";
	static final String CLEF_LINE = "line";
	static final String CLEF_G = "G";
	static final String CLEF_F = "F";
	static final String CLEF_C = "C";
	static final String CLEF_PERC = "percussion";
	static final String CLEF_STAFF = "number";

	// Measure tags
	static final String MEASURE = "measure";
	static final String MEASURE_NUM = "number";
	static final String MEASURE_ATTRIBUTES = "attributes";
	static final String MEAS_ATTR_DIVS = "divisions";
	static final String MEAS_ATTR_TIME = "time";
	static final String MEAS_ATTR_TIME_SYMBOL = "symbol";
	static final String MEAS_ATTR_TIME_COMMON = "common";
	static final String MEAS_ATTR_TIME_CUT = "cut";
	static final String MEAS_ATTR_TIME_NUMERATOR = "single-number";
	static final String MEAS_ATTR_TIME_NOTE = "note";
	static final String MEAS_ATTR_TIME_DOTTED_NOTE = "dotted-note";
	static final String MEAS_ATTR_BEAT_TYPE = "beat-type";
	static final String MEAS_ATTR_BEATS = "beats";
	static final String MEAS_ATTR_KEY = "key";
	static final String MEAS_ATTR_KEY_FIFTHS = "fifths";
	static final String MEAS_ATTR_STAVES = "staves";
	static final String MEAS_ATTR_STAFF_NUMBER = "number";
	static final String MEASURE_BACKUP = "backup";
	static final String MEASURE_FORWARD = "forward";

	// Note tags
	static final String NOTE = "note";
	static final String NOTE_PITCH = "pitch";
	static final String NOTE_REST = "rest";
	static final String NOTE_DURATION = "duration";
	static final String NOTE_VOICE = "voice";
	static final String NOTE_CHORD = "chord";
	static final String NOTE_GRACE_NOTE = "grace";
	static final String NOTE_GRACE_SLASH = "slash";
	static final String NOTE_STAFF = "staff";
	static final String NOTE_UNPITCHED = "unpitched";
	static final String UNPITCHED_STEP = "display-step";
	static final String UNPITCHED_OCTAVE = "display-octave";
	static final String TIE = "tie";
	static final String TIE_TYPE = "type";
	static final String TIE_START = "start";
	static final String TIE_STOP = "stop";
	static final String NOTATIONS = "notations";
	static final String NOTE_ARTICULATIONS = "articulations";
	static final String NOTE_DURATION_TYPE = "type";
	static final String DOT = "dot";

	// Note appearances supported in MusicXML
	static final String NOTE_TYPE_1024TH = "1024th";
	static final String NOTE_TYPE_512TH = "512th";
	static final String NOTE_TYPE_256TH = "256th";
	static final String NOTE_TYPE_128TH = "128th";
	static final String NOTE_TYPE_64TH = "64th";
	static final String NOTE_TYPE_32TH = "32nd";
	static final String NOTE_TYPE_16TH = "16th";
	static final String NOTE_TYPE_EIGHTH = "eighth";
	static final String NOTE_TYPE_QUARTER = "quarter";
	static final String NOTE_TYPE_HALF = "half";
	static final String NOTE_TYPE_WHOLE = "whole";
	static final String NOTE_TYPE_BREVE = "breve";
	static final String NOTE_TYPE_LONG = "long";
	static final String NOTE_TYPE_MAXIMA = "maxima";

	static final String TIME_MODIFICATION = "time-modification";
	static final String TIME_MODIFICATION_ACTUAL_NOTES = "actual-notes";
	static final String TIME_MODIFICATION_NORMAL_NOTES = "normal-notes";

	// Articulations
	static final String ACCENT = "accent";
	static final String BREATH_MARK = "breath-mark";
	static final String CAESURA = "caesura";
	static final String FERMATA = "fermata";
	static final String SLIDE_IN_DOWN = "plop";
	static final String SLIDE_IN_UP = "scoop";
	static final String SLIDE_OUT_DOWN = "falloff";
	static final String SLIDE_OUT_UP = "doit";
	static final String SPICCATO = "spiccato";
	static final String STACCATISSIMO = "staccatissimo";
	static final String STACCATO = "staccato";
	static final String STRESS = "stress";
	static final String STRONG_ACCENT = "strong-accent";
	static final String TENUTO = "tenuto";
	static final String TENUTO_STACCATO = "detached-legato";
	static final String UNSTRESS = "unstress";

	// Notations
	static final String TIED = "tied";
	static final String SLUR = "slur";
	static final String GLISSANDO = "glissando";
	static final String SLIDE = "slide";
	static final String ARPEGGIATE = "arpeggiate";
	static final String ARPEGGIO_DIRECTION = "direction";
	static final String ARPEGGIO_DIRECTION_DOWN = "down";
	static final String ARPEGGIO_DIRECTION_UP = "up";
	static final String NON_ARPEGGIATE = "non-arpeggiate";
	static final String NON_ARPEGGIATE_BOTTOM = "bottom";
	static final String NON_ARPEGGIATE_TOP = "top";
	static final String NOTATION_NUMBER = "number";
	static final String NOTATION_TYPE = "type";
	static final String NOTATION_TYPE_START = "start";
	static final String NOTATION_TYPE_STOP = "stop";

	static final String NOTATION_LINE_TYPE = "line-type";
	static final String NOTATION_LINE_SOLID = "solid";
	static final String NOTATION_LINE_DASHED = "dashed";
	static final String NOTATION_LINE_DOTTED = "dotted";
	static final String NOTATION_LINE_WAVY = "wavy";

	static final Set<String> CONNECTED_NOTATION_NODE_NAMES = Collections
			.unmodifiableSet(Set.of(TIED, SLUR, GLISSANDO, SLIDE, ARPEGGIATE, NON_ARPEGGIATE));

	// Ornaments
	static final String ORNAMENTS = "ornaments";
	static final String DELAYED_INVERTED_TURN = "delayed-inverted-turn";
	static final String DELAYED_TURN = "delayed-turn";
	static final String INVERTED_MORDENT = "inverted-mordent";
	static final String INVERTED_TURN = "inverted-turn";
	static final String MORDENT = "mordent";
	static final String TREMOLO = "tremolo";
	static final String TRILL_MARK = "trill-mark";
	static final String TURN = "turn";

	// Direction tags
	static final String DIRECTION = "direction";
	static final String DIRECTION_PLACEMENT = "placement";
	static final String DIRECTION_TYPE = "direction-type";
	static final String DIRECTION_ABOVE = "above";
	static final String DIRECTION_WORDS = "words";
	static final String OFFSET = "offset";

	// Part tags
	static final String PART = "part";
	static final String PART_ID = "id";
	static final String PART_NAME = "part-name";
	static final String PART_NAME_ABBREVIATION = "part-abbreviation";
	static final String PART_LIST = "part-list";
	static final String PLIST_SCORE_PART = "score-part";

	// Pitch tags
	static final String PITCH_STEP = "step";
	static final String PITCH_OCT = "octave";
	static final String PITCH_ALTER = "alter";

	// Score info tags
	static final String SCORE_MOVEMENT_TITLE = "movement-title";
	static final String SCORE_WORK = "work";
	static final String SCORE_WORK_TITLE = "work-title";
	static final String SCORE_IDENTIFICATION = "identification";
	static final String SCORE_IDENTIFICATION_CREATOR = "creator";
	static final String SCORE_IDENTIFICATION_CREATOR_TYPE = "type";
	static final String SCORE_IDENTIFICATION_COMPOSER = "composer";
	static final String SCORE_IDENTIFICATION_ARRANGER = "arranger";
	static final String SCORE_PARTWISE = "score-partwise";
	static final String MUSICXML_VERSION = "version";
	static final String ENCODING = "encoding";
	static final String SOFTWARE = "software";
	static final String ENCODING_DATE = "encoding-date";

	// Score layout related tags and attributes
	static final String PRINT = "print";
	static final String PRINT_OBJECT = "print-object";
	static final String NEW_SYSTEM = "new-system";
	static final String YES = "yes";
	static final String NO = "no";

	private MusicXmlTags() {
	}
}
