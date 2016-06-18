
package WMNKitClasses;

/**
 *
 * @author Otso Björklund
 */
public class Rest implements NotationElement {
    private final Duration duration;
    
    public static Rest getRest(Duration duration) {
        return new Rest(duration);
    }
    
    private Rest(Duration duration) {
        this.duration = duration;
    }
    
    @Override
    public Duration getDuration() {
        return this.duration;
    }
    
}
