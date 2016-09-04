package wmnlibnotation;

import java.math.BigInteger;

/**
 * Defines a duration
 * @author Otso Björklund
 */
public class Duration implements Comparable<Duration> {
    
    private final int nominator;
    private final int denominator;
    
    public static Duration getDuration(int nominator, int denominator) {
        return new Duration(nominator, denominator);
    }
    
    private Duration(int nominator, int denominator) {
        if(nominator < 1)
            throw new IllegalArgumentException("nominator must be at least 1");
        if(denominator < 1)
            throw new IllegalArgumentException("denominator must be at least 1");
    
        // Todo: Come up with a more effective way of finding GCD
        int gcd = BigInteger.valueOf(nominator).gcd(BigInteger.valueOf(denominator)).intValue();
        this.nominator = nominator / gcd;
        this.denominator = denominator / gcd;
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

    public Duration add(Duration duration) {
        int nom = this.nominator * duration.denominator + this.denominator * duration.nominator;
        int denom = this.denominator * duration.denominator;
        
        return getDuration(nom, denom);
    }
    
    public boolean longerThan(Duration duration) {
        return this.compareTo(duration) > 0;
    }
    
    public boolean shorterThan(Duration duration) {
        return this.compareTo(duration) < 0;
    }
    
    public Duration addDot() {
        return this.add(Duration.getDuration(this.nominator, 2 * this.denominator));
    }
    
    @Override
    public int compareTo(Duration o) {
        return this.nominator * o.denominator - o.nominator * this.denominator;
    }
}
