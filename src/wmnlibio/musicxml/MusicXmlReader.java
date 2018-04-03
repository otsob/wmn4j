/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibio.musicxml;

import java.io.IOException;
import wmnlibnotation.Score;

/**
 *
 * @author Otso Björklund
 */
public interface MusicXmlReader {
    public Score readScore(String fileName) throws IOException;
}
