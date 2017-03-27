/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

/**
 * Interface for parts in a score.
 * @author Otso Björklund
 */
public interface Part extends Iterable<Measure> {
    enum Attr {NAME, ABBR_NAME};
    
    public String getName();
    public boolean isMultiStaff();
    public String getPartAttribute(Attr attribute);
}
