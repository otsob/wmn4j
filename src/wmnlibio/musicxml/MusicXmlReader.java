/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wmnlibio.musicxml;

import java.io.IOException;
import wmnlibnotation.Score;

/**
 *
 * @author Otso Björklund
 */
public interface MusicXmlReader {
    public Score readScore(String fileName)  throws IOException ;
}
