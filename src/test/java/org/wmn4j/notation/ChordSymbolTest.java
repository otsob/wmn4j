/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChordSymbolTest {

	@Test
	void testCreatingChordSymbolWithoutInversion() {
		final ChordSymbol chord = ChordSymbol.of(ChordSymbol.Base.MAJOR,
				PitchName.of(Pitch.Base.C, Pitch.Accidental.SHARP), null, null);

		final var root = chord.getRoot();
		assertEquals(ChordSymbol.Base.MAJOR, chord.getBase());
		assertEquals(PitchName.of(Pitch.Base.C, Pitch.Accidental.SHARP), root);
		assertEquals(chord.getBass(), root);
		assertTrue(chord.getExtensions().isEmpty());
		assertFalse(chord.isInversion());
	}

	@Test
	void testCreatingChordSymbolWithInversion() {
		final ChordSymbol chord = ChordSymbol.of(ChordSymbol.Base.MAJOR,
				PitchName.of(Pitch.Base.C, Pitch.Accidental.SHARP),
				PitchName.of(Pitch.Base.G, Pitch.Accidental.SHARP), null);

		assertEquals(ChordSymbol.Base.MAJOR, chord.getBase());
		assertEquals(PitchName.of(Pitch.Base.C, Pitch.Accidental.SHARP), chord.getRoot());
		assertEquals(PitchName.of(Pitch.Base.G, Pitch.Accidental.SHARP), chord.getBass());
		assertTrue(chord.getExtensions().isEmpty());
		assertTrue(chord.isInversion());
	}

	@Test
	void testCreatingChordSymbolsWithExtensions() {
		final ChordSymbol Cmaj7 = ChordSymbol.of(ChordSymbol.Base.MAJOR,
				PitchName.of(Pitch.Base.C, Pitch.Accidental.NATURAL),
				null,
				Arrays.asList(ChordSymbol.extension(ChordSymbol.Extension.Type.MAJOR, Pitch.Accidental.NATURAL, 7)));

		assertEquals(ChordSymbol.Base.MAJOR, Cmaj7.getBase());
		assertEquals(1, Cmaj7.getExtensions().size());
		assertEquals(ChordSymbol.extension(ChordSymbol.Extension.Type.MAJOR, Pitch.Accidental.NATURAL, 7),
				Cmaj7.getExtensions().get(0));

		assertFalse(Cmaj7.isInversion());
	}

	@Test
	void testEqualityAndHashCode() {
		final ChordSymbol Cmaj7 = ChordSymbol.of(ChordSymbol.Base.MAJOR,
				PitchName.of(Pitch.Base.C, Pitch.Accidental.NATURAL),
				null,
				Arrays.asList(ChordSymbol.extension(ChordSymbol.Extension.Type.MAJOR, Pitch.Accidental.NATURAL, 7)));

		final ChordSymbol Cmaj7Copy = ChordSymbol.of(ChordSymbol.Base.MAJOR,
				PitchName.of(Pitch.Base.C, Pitch.Accidental.NATURAL),
				null,
				Arrays.asList(ChordSymbol.extension(ChordSymbol.Extension.Type.MAJOR, Pitch.Accidental.NATURAL, 7)));

		assertEquals(Cmaj7, Cmaj7);
		assertEquals(Cmaj7, Cmaj7Copy);
		assertEquals(Cmaj7.hashCode(), Cmaj7Copy.hashCode());

		final ChordSymbol Cmaj9 = ChordSymbol.of(ChordSymbol.Base.MAJOR,
				PitchName.of(Pitch.Base.C, Pitch.Accidental.NATURAL),
				null,
				Arrays.asList(ChordSymbol.extension(ChordSymbol.Extension.Type.MAJOR, Pitch.Accidental.NATURAL, 9)));

		assertNotEquals(Cmaj7, Cmaj9);
	}
}
