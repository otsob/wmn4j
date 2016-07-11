
package wmnkitnotation;

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
    
    @Override
    public boolean isRest() {
        return true;
    }
    
    @Override
    public String toString() {
        return "R" + this.duration.toString();
    }
}
