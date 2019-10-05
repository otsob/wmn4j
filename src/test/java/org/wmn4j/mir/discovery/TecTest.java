package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TecTest {

	@Test
	void testWhenTecIsCreatedThenItHasCorrectContent() {
		PointPattern pattern = new PointPattern(
				Arrays.asList(new NoteEventVector(1.0, 20, 0), new NoteEventVector(2.0, 12, 1)));

		List<NoteEventVector> translators = Arrays
				.asList(new NoteEventVector(5.0, 32, 0), new NoteEventVector(12.0, 48, 1));

		Tec tec = new Tec(pattern, translators);

		assertEquals(pattern, tec.getPattern());
		assertEquals(translators, tec.getTranslators());
	}
}
