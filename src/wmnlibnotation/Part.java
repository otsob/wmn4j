/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

/**
 * Interface for parts in a score.
 * The class <code>PartBuilder</code> can be used for constructing <code>Part</code> objects.
 * @author Otso Björklund
 */
public interface Part extends Iterable<Measure> {
    
    /**
     * Attribute types that a <code>Part</code> can have.
     */
    enum Attribute { NAME, ABBR_NAME };
    
    /**
     * @return name of this <code>Part</code>.
     */
    public String getName();
    
    /**
     * @return true if this <code>Part</code> has multiple staves. False otherwise.
     */
    public boolean isMultiStaff();
    
    /**
     * @return number of staves in this part.
     */
    public int getStaffCount();
    
    /**
     * @param attribute the Attribute to get from this <code>Part</code>.
     * @return the String associated with the attribute, 
     * or an empty string if the attribute is not set.
     */
    public String getPartAttribute(Attribute attribute);
    
    /**
     * Get the number of measures in the part.
     * The count is based on the measure numbers, so even if a part has multiple staves
     * its measure count is the largest measure number.
     * @return number of measures in the part. 
     * If there is a pickup measure, it is included in the count.
     */
    public int getMeasureCount();
    
    /**
     * Get the number of complete measures.
     * @return the number of measures excluding the pickup measure.
     */
    public int getFullMeasureCount();
    
}
