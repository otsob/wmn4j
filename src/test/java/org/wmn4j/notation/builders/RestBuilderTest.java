package org.wmn4j.notation.builders;

import org.junit.jupiter.api.Test;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestBuilderTest {

	@Test
	void testWhenCreatingRestBuilderFromRestCorrectValuesAreSet() {
		final Rest eighthRest = Rest.of(Duration.of(1, 8));
		RestBuilder eighthBuilder = new RestBuilder(eighthRest);
		assertEquals(eighthRest, eighthBuilder.build());

		final Rest tripletRest = Rest.of(Duration.of(1, 12));
		RestBuilder tripletBuilder = new RestBuilder(tripletRest);
		assertEquals(tripletRest, tripletBuilder.build());
	}
}
