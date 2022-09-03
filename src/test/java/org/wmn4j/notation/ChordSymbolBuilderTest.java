/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChordSymbolBuilderTest {

	@Test
	void testGivenChordBuilderWithMultipleExtensionsThenChordSymbolIsCorrectlyBuilt() {
		ChordSymbolBuilder builder = new ChordSymbolBuilder();
		builder.setBase(ChordSymbol.Base.MAJOR);
		builder.setRoot(PitchName.of(Pitch.Base.D, Pitch.Accidental.SHARP));
		builder.setBass(PitchName.of(Pitch.Base.A, Pitch.Accidental.SHARP));
		builder.addExtension(ChordSymbol.extension(ChordSymbol.Extension.Type.PLAIN, Pitch.Accidental.NATURAL, 11));
		builder.addExtension(ChordSymbol.extension(ChordSymbol.Extension.Type.OMIT, Pitch.Accidental.NATURAL, 7));

		final var chord = builder.build();
		assertEquals(ChordSymbol.Base.MAJOR, chord.getBase());
		assertEquals(PitchName.of(Pitch.Base.D, Pitch.Accidental.SHARP), chord.getRoot());
		assertEquals(PitchName.of(Pitch.Base.A, Pitch.Accidental.SHARP), chord.getBass());
		assertEquals(2, chord.getExtensions().size());
		assertEquals(ChordSymbol.extension(ChordSymbol.Extension.Type.PLAIN, Pitch.Accidental.NATURAL, 11),
				chord.getExtensions().get(0));
		assertEquals(ChordSymbol.extension(ChordSymbol.Extension.Type.OMIT, Pitch.Accidental.NATURAL, 7),
				chord.getExtensions().get(1));

	}

}
