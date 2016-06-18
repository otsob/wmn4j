
package WMNKitClasses;

/**
 * Collection of basic durations
 * @author Otso Björklund
 */
public class Durations {
    public static final Duration WHOLE = Duration.getDuration(1, 1);
    public static final Duration HALF = Duration.getDuration(1, 2);
    public static final Duration QUARTER = Duration.getDuration(1, 4);
    public static final Duration EIGHT = Duration.getDuration(1, 8);
    public static final Duration SIXTEENTH = Duration.getDuration(1, 16);
    public static final Duration THIRTYSECOND = Duration.getDuration(1, 32);
    
    // Not meant to be instantiated
    private Durations() {
        throw new AssertionError();
    }
}
