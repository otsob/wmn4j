package wmnkitio.musicxml;

/**
 *
 * @author Otso Björklund
 */
public class MusicXmlTags {

    // Barline tags
    static final String BARLINE = "barline";
    static final String BARLINE_STYLE = "bar-style";
    static final String BARLINE_STYLE_LIGHT_HEAVY = "light-heavy";
    
    // Clef tags
    static final String CLEF = "clef";
    static final String CLEG_SIGN = "sign";
    static final String CLEF_G = "G";
    static final String CLEF_F = "F";
    
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
   
    // Note tags
    static final String NOTE = "note";
    static final String NOTE_PITCH = "pitch";
    static final String NOTE_REST = "rest";
    static final String NOTE_DURATION = "duration";
    static final String NOTE_VOICE = "voice";
    static final String NOTE_CHORD = "chord";
    static final String NOTE_GRACE_NOTE = "grace";
    
    // Part tags
    static final String PART = "part";
    static final String PART_ID = "id";
    static final String PART_NAME = "part-name";
    static final String PART_LIST = "part-list";
    static final String PLIST_SCORE_PART = "score-part";
    
    // Pitch tags
    static final String PITCH_STEP = "step";
    static final String PITCH_OCT = "octave";
    static final String PITCH_ALTER = "alter";
    
    // Score info tags
    static final String SCORE_MOVEMENT_TITLE = "movement-title";
    static final String SCORE_IDENTIFICATION = "identification";
    static final String SCORE_IDENTIFICATION_CREATOR = "creator";
    static final String SCORE_PARTWISE = "score-partwise";

    private MusicXmlTags() {
    }
}
