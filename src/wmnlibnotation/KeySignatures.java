/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation;
import java.util.Arrays;

/**
 * Collection of typical key signatures associated with major and minor keys.
 * @author Otso Björklund
 */
public class KeySignatures {
    // Basic key signatures with sharps
    public static final KeySignature CMAJ_AMIN = new KeySignature(null, null);
    public static final KeySignature GMAJ_EMIN = new KeySignature(Arrays.asList(Pitch.Base.F), null);
    public static final KeySignature DMAJ_BMIN = new KeySignature(Arrays.asList(Pitch.Base.F, Pitch.Base.C), null);
    public static final KeySignature AMAJ_FSHARPMIN 
            = new KeySignature(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G), null);
    public static final KeySignature EMAJ_CSHARPMIN 
            = new KeySignature(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D), null);
    public static final KeySignature BMAJ_GSHARPMIN 
            = new KeySignature(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D, Pitch.Base.A), null);
    public static final KeySignature FSHARPMAJ_DSHARPMIN 
            = new KeySignature(Arrays.asList(Pitch.Base.F, Pitch.Base.C, Pitch.Base.G, Pitch.Base.D, Pitch.Base.A, Pitch.Base.E), null);
    
    // Basic key signatures with flats
    public static final KeySignature FMAJ_DMIN = new KeySignature(null, Arrays.asList(Pitch.Base.B));
    public static final KeySignature BFLATMAJ_GMIN = new KeySignature(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E));
    public static final KeySignature EFLATMAJ_CMIN = new KeySignature(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A));
    public static final KeySignature AFLATMAJ_FMIN 
            = new KeySignature(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D));
    public static final KeySignature DFLATMAJ_BFLATMIN 
            = new KeySignature(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D, Pitch.Base.G));
    public static final KeySignature GFLATMAJ_EFLATMIN 
            = new KeySignature(null, Arrays.asList(Pitch.Base.B, Pitch.Base.E, Pitch.Base.A, Pitch.Base.D, Pitch.Base.G, Pitch.Base.C));
    
    private KeySignatures() {
        // Not meant to be instantiated.
        throw new AssertionError();
    }
}
