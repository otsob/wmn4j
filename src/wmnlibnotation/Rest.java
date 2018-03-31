
package wmnlibnotation;

/**
 * This class represents a rest. 
 * This class is immutable.
 * @author Otso Björklund
 */
public class Rest implements Durational {
    private final Duration duration;
    
    /**
     * Get a <code>Rest</code> with duration specified by <code>Duration duration</code>.
     * @throws NullPointerException if duration is null.
     * @param duration the duration of the rest.
     * @return Rest with specified duration.
     */
    public static Rest getRest(Duration duration) {
        if(duration == null)
            throw new NullPointerException();
        
        // TODO: Use interner pattern for caching.
        return new Rest(duration);
    }
    
    /**
     * Private constructor. Use the static method 
     * {@link #getRest(wmnlibnotation.Duration)  getRest} 
     * to get a <code>Rest</code> object.
     * @param duration the duration of the rest.
     */
    private Rest(Duration duration) {
        this.duration = duration;
    }
    
    /**
     * Get the <code>Duration</code> of this.
     * @return the duration of this Rest.
     */
    @Override
    public Duration getDuration() {
        return this.duration;
    }
    
    /**
     * Method for checking if the Durational is a Rest.
     * @return true.
     */
    @Override
    public boolean isRest() {
        return true;
    }
    
    /**
     * Get the String representation of Rest.
     * @return String of form <code>RD</code> where <code>D</code> is the 
     * String representation of the Duration of this Rest.
     */
    @Override
    public String toString() {
        return "R" + this.duration.toString();
    }
    
    /**
     * Compare this <code>Rest</code> for equality against <code>Object o</code>.
     * @param o Object against which this is compared for equality.
     * @return true if Object o is a Rest and the Duration of o is equal 
     * to the Duration of this.
     */
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
