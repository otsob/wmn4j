package WMNKitClasses;

/**
 * Defines a duration
 * @author Otso Björklund
 */
public class Duration {
    
    private final int nominator;
    private final int denominator;
    
    public static Duration getDuration(int nominator, int denominator) {
        return new Duration(nominator, denominator);
    }
    
    private Duration(int nominator, int denominator) {
        this.nominator = nominator;
        this.denominator = denominator;
    }
    
    public int getNominator() {
        return this.nominator;
    }
    
    public int getDenominator() {
        return this.denominator;
    }
    
    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;
        
        if(!(o instanceof Duration))
            return false;
        
        Duration other = (Duration) o;
        
        return (this.nominator == other.nominator) 
                && (this.denominator == other.denominator);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + this.nominator;
        hash = 83 * hash + this.denominator;
        return hash;
    }
    
    @Override 
    public String toString() {
        return "(" + this.nominator + "/" + this.denominator + ")";
    }
    
}
