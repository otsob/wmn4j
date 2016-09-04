
package wmnlibnotation;

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
        
        if(this.duration == null)
            throw new NullPointerException();
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
    
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        
        if(!(o instanceof Rest))
            return false;
        
        Rest other = (Rest) o;
        
        return this.duration.equals(other.duration);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        return hash;
    }
}
