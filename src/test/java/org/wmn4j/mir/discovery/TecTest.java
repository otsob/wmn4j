/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir.discovery;

import org.junit.jupiter.api.Test;
import org.wmn4j.representation.geometric.Point2D;
import org.wmn4j.representation.geometric.PointPattern;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TecTest {

	@Test
	void testWhenTecIsCreatedThenItHasCorrectContent() {
		PointPattern<Point2D> pattern = new PointPattern<>(
				Arrays.asList(new Point2D(1.0, 20), new Point2D(2.0, 12)));

		List<Point2D> translators = Arrays
				.asList(new Point2D(5.0, 32), new Point2D(12.0, 48));

		Tec<Point2D> tec = new Tec<>(pattern, translators);

		assertEquals(pattern, tec.getPattern());
		assertEquals(translators, tec.getTranslators());
	}
}
