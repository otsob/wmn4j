/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;

/**
 * Common clefs.
 * @author Otso Björklund
 */
public class Clefs {
    public static final Clef G = Clef.getClef(Clef.Type.G, 2);
    public static final Clef F = Clef.getClef(Clef.Type.F, 4);
    public static final Clef ALTO = Clef.getClef(Clef.Type.C, 3);
    public static final Clef PERCUSSION = Clef.getClef(Clef.Type.PERCUSSION, 3);
}
