/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibnotation;
import java.util.Arrays;

/**
 * Collection of typical key signatures associated with major and minor keys.
 * @author Otso Björklund
 */
public class KeySignatures {
    // Basic key signatures with sharps
    public static final KeySignature CMaj_Amin = KeySignature.getKeySig(null, null);
    public static final KeySignature GMaj_Emin = KeySignature.getKeySig(Arrays.asList(Pitch.Base.F), null);
    public static final KeySignature DMaj_Bmin = KeySignature.getKeySig(Arrays.asList(Pitch.Base.F, Pitch.Base.C), null);
    public static final KeySignature AMaj_FSharpMin 
            = KeySignature.getKeySig(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G), null);
    public static final KeySignature EMaj_CSharpMin 
            = KeySignature.getKeySig(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D), null);
    public static final KeySignature BMaj_GSharpMin 
            = KeySignature.getKeySig(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D, Pitch.Base.A), null);
    public static final KeySignature FSharpMaj_DSharpMin 
            = KeySignature.getKeySig(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D, Pitch.Base.A, Pitch.Base.E), null);
    
    // Basic key signatures with flats
    public static final KeySignature FMaj_Dmin = KeySignature.getKeySig(null, Arrays.asList(Pitch.Base.B));
    public static final KeySignature BFlatMaj_Gmin = KeySignature.getKeySig(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E));
    public static final KeySignature EFlatMaj_Cmin = KeySignature.getKeySig(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A));
    public static final KeySignature AFlatMaj_Fmin 
            = KeySignature.getKeySig(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D));
    public static final KeySignature DFlatMaj_BFlatMin 
            = KeySignature.getKeySig(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D, Pitch.Base.G));
    public static final KeySignature GFlatMaj_EFlatMin 
            = KeySignature.getKeySig(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D, Pitch.Base.G, Pitch.Base.C));
    
    // Not meant to be instantiated.
    private KeySignatures() {}
}
