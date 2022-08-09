/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LyricTest {

	@Test
	void testLyricCreation() {
		final Lyric lyric = Lyric.of("Syl", Lyric.Type.START);
		assertEquals("Syl", lyric.getText());
		assertEquals(Lyric.Type.START, lyric.getType());

		assertThrows(NullPointerException.class, () -> Lyric.of(null, Lyric.Type.END));
		assertThrows(NullPointerException.class, () -> Lyric.of("", null));
	}

	@Test
	void testGivenEqualLyricsEqualsReturnsTrueAndHashCodesMatch() {
		final Lyric lyric1 = Lyric.of("Syl", Lyric.Type.START);
		final Lyric lyric2 = Lyric.of("Syl", Lyric.Type.START);

		assertEquals(lyric1, lyric1);
		assertEquals(lyric1, lyric2);
		assertEquals(lyric1.hashCode(), lyric2.hashCode());
	}

	@Test
	void testGivenUnEqualLyricsEqualsReturnsFalse() {
		final Lyric lyric1 = Lyric.of("Syl", Lyric.Type.START);
		final Lyric lyric2 = Lyric.of("Wo", Lyric.Type.START);
		assertNotEquals(lyric1, lyric2);
		assertNotEquals(lyric1, Lyric.of("Syl", Lyric.Type.INDEPENDENT));
	}
}
