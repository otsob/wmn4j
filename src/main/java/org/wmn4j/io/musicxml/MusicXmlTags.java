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
	static final String MEAS_ATTR_BEAT_TYPE = "beat-type";
	static final String MEAS_ATTR_BEATS = "beats";
	static final String MEAS_ATTR_KEY = "key";
	static final String MEAS_ATTR_KEY_FIFTHS = "fifths";
	static final String MEAS_ATTR_STAVES = "staves";
	static final String MEAS_ATTR_STAFF_NUMBER = "number";

	// Note tags
	static final String NOTE = "note";
	static final String NOTE_PITCH = "pitch";
	static final String NOTE_REST = "rest";
	static final String NOTE_DURATION = "duration";
	static final String NOTE_VOICE = "voice";
	static final String NOTE_CHORD = "chord";
	static final String NOTE_GRACE_NOTE = "grace";
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

	// Articulations
	static final String STACCATO = "staccato";
	static final String ACCENT = "accent";
	static final String TENUTO = "tenuto";
	static final String FERMATA = "fermata";

	// Markings
	static final String SLUR = "slur";
	static final String GLISSANDO = "glissando";
	static final String MARKING_NUMBER = "number";
	static final String MARKING_TYPE = "type";
	static final String MARKING_TYPE_START = "start";
	static final String MARKING_TYPE_STOP = "stop";

	static final Set<String> MARKING_NODE_NAMES = Collections.unmodifiableSet(Set.of(SLUR, GLISSANDO));

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

	private MusicXmlTags() {
	}
}
