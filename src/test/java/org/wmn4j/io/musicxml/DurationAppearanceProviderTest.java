/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.Durations;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class DurationAppearanceProviderTest {

	private Document createDocument() {
		try {
			final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

			return docBuilder.newDocument();
		} catch (Exception e) {
			fail("Failed to created document due to " + e);
		}

		return null;
	}

	@Test
	void testGivenBasicDurationCorrectTypeElementIsReturned() {
		final DurationAppearanceProvider provider = DurationAppearanceProvider.INSTANCE;
		final Document document = createDocument();

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 1024), document), MusicXmlTags.NOTE_TYPE_1024TH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 512), document), MusicXmlTags.NOTE_TYPE_512TH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 256), document), MusicXmlTags.NOTE_TYPE_256TH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 128), document), MusicXmlTags.NOTE_TYPE_128TH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 64), document), MusicXmlTags.NOTE_TYPE_64TH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 32), document), MusicXmlTags.NOTE_TYPE_32TH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 16), document), MusicXmlTags.NOTE_TYPE_16TH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 8), document), MusicXmlTags.NOTE_TYPE_EIGHTH);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 4), document), MusicXmlTags.NOTE_TYPE_QUARTER);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 2), document), MusicXmlTags.NOTE_TYPE_HALF);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(1, 1), document), MusicXmlTags.NOTE_TYPE_WHOLE);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(2, 1), document), MusicXmlTags.NOTE_TYPE_BREVE);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(4, 1), document), MusicXmlTags.NOTE_TYPE_LONG);

		assertBasicDurationsReturnSingleElementOfCorrectType(
				provider.getAppearanceElements(Duration.of(8, 1), document), MusicXmlTags.NOTE_TYPE_MAXIMA);
	}

	private void assertBasicDurationsReturnSingleElementOfCorrectType(Collection<Element> elements,
			String expectedContent) {
		assertEquals(1, elements.size());
		Element onlyElement = elements.iterator().next();
		assertEquals(MusicXmlTags.NOTE_DURATION_TYPE, onlyElement.getNodeName());
		assertEquals(expectedContent, onlyElement.getTextContent());
	}

	@Test
	void testGivenDottedDurationCorrectTypeAndDotElementAreReturned() {

		final DurationAppearanceProvider provider = DurationAppearanceProvider.INSTANCE;
		final Document document = createDocument();

		final Duration dottedSixteenth = Duration.of(1, 16).addDot();

		assertDottedDurationHasCorrectTypeAndDot(
				provider.getAppearanceElements(dottedSixteenth, document), MusicXmlTags.NOTE_TYPE_16TH);

		final Duration dottedEighth = Duration.of(1, 8).addDot();

		assertDottedDurationHasCorrectTypeAndDot(
				provider.getAppearanceElements(dottedEighth, document), MusicXmlTags.NOTE_TYPE_EIGHTH);

		final Duration dottedQuarter = Duration.of(1, 4).addDot();

		assertDottedDurationHasCorrectTypeAndDot(
				provider.getAppearanceElements(dottedQuarter, document), MusicXmlTags.NOTE_TYPE_QUARTER);

		final Duration dottedHalf = Duration.of(1, 2).addDot();

		assertDottedDurationHasCorrectTypeAndDot(
				provider.getAppearanceElements(dottedHalf, document), MusicXmlTags.NOTE_TYPE_HALF);
	}

	private void assertDottedDurationHasCorrectTypeAndDot(Collection<Element> elements, String expectedContent) {
		assertEquals(2, elements.size(), "Elements should contains two elements, type and dot.");

		Optional<Element> onlyElementOpt = elements.stream()
				.filter(element -> element.getNodeName().equals(MusicXmlTags.NOTE_DURATION_TYPE)).findAny();

		assertTrue(onlyElementOpt.isPresent(), "Did not find expected type element");

		Element typeElement = onlyElementOpt.get();
		assertEquals(MusicXmlTags.NOTE_DURATION_TYPE, typeElement.getNodeName());
		assertEquals(expectedContent, typeElement.getTextContent());

		assertTrue(elements.stream().filter(element -> element.getNodeName().equals(MusicXmlTags.DOT)).findAny()
				.isPresent(), "elements list does not contain dot element");
	}

	@Test
	void testGivenNonDottedDurationNoDotElementIsReturned() {
		final DurationAppearanceProvider provider = DurationAppearanceProvider.INSTANCE;
		final Document document = createDocument();

		final Duration eight = Duration.of(1, 8);
		Collection<Element> eighthAppearanceElements = provider.getAppearanceElements(eight, document);
		assertTrue(eighthAppearanceElements.stream().filter(element -> element.getNodeName().equals(MusicXmlTags.DOT))
				.findAny()
				.isEmpty(), "elements contained dot element that it should not contain");

		final Duration quintupletDuration = Duration.of(3, 1111);
		Collection<Element> quintupletAppearanceElements = provider.getAppearanceElements(quintupletDuration, document);
		assertTrue(
				quintupletAppearanceElements.stream().filter(element -> element.getNodeName().equals(MusicXmlTags.DOT))
						.findAny()
						.isEmpty(), "elements contained dot element that it should not contain");
	}

	@Test
	void testGivenTupletCorrectTimeModificationsAreReturned() {
		final DurationAppearanceProvider provider = DurationAppearanceProvider.INSTANCE;
		final Document document = createDocument();

		final Duration eighthTriplet = Durations.EIGHTH_TRIPLET;
		final Collection<Element> eighthTripletAppearance = provider.getAppearanceElements(eighthTriplet, document);
		assertTupletAppearanceElements(eighthTripletAppearance, MusicXmlTags.NOTE_TYPE_EIGHTH, 3, 2);

		final Duration sixteenthQuintuplet = Durations.QUARTER.divide(5);
		final Collection<Element> sixteenthQuintupletAppearance = provider
				.getAppearanceElements(sixteenthQuintuplet, document);
		assertTupletAppearanceElements(sixteenthQuintupletAppearance, MusicXmlTags.NOTE_TYPE_16TH, 5, 4);

		final Duration sixteenthSeptuplet = Durations.QUARTER.divide(7);
		final Collection<Element> sixteenthSeptupletAppearance = provider
				.getAppearanceElements(sixteenthSeptuplet, document);
		assertTupletAppearanceElements(sixteenthSeptupletAppearance, MusicXmlTags.NOTE_TYPE_16TH, 7, 4);

		final Duration halfTriplet = Durations.WHOLE.divide(3);
		final Collection<Element> halfTripletAppearance = provider.getAppearanceElements(halfTriplet, document);
		assertTupletAppearanceElements(halfTripletAppearance, MusicXmlTags.NOTE_TYPE_HALF, 3, 2);
	}

	private void assertTupletAppearanceElements(Collection<Element> elements, String expectedAppearance,
			int timeModificationActual, int timeModificationNormal) {

		Optional<Element> onlyElementOpt = elements.stream()
				.filter(element -> element.getNodeName().equals(MusicXmlTags.NOTE_DURATION_TYPE)).findAny();

		assertTrue(onlyElementOpt.isPresent(), "Did not find expected type element");

		Element typeElement = onlyElementOpt.get();
		assertEquals(MusicXmlTags.NOTE_DURATION_TYPE, typeElement.getNodeName());
		assertEquals(expectedAppearance, typeElement.getTextContent());

		Optional<Element> timeModificationOpt = elements.stream()
				.filter(element -> element.getNodeName().equals(MusicXmlTags.TIME_MODIFICATION)).findAny();

		assertTrue(timeModificationOpt.isPresent(), "Missing expected time modification element");

		NodeList timeModificationChildren = timeModificationOpt.get().getChildNodes();
		assertEquals(2, timeModificationChildren.getLength(),
				"Time modification should have exactly two children: actual-notes and normal-notes.");

		for (int i = 0; i < timeModificationChildren.getLength(); ++i) {
			final Node timeModificationNode = timeModificationChildren.item(i);

			if (MusicXmlTags.TIME_MODIFICATION_ACTUAL_NOTES.equals(timeModificationNode.getNodeName())) {
				assertEquals(timeModificationActual, Integer.parseInt(timeModificationNode.getTextContent()),
						"Incorrect actual-notes content");
			} else if (MusicXmlTags.TIME_MODIFICATION_NORMAL_NOTES.equals(timeModificationNode.getNodeName())) {
				assertEquals(timeModificationNormal, Integer.parseInt(timeModificationNode.getTextContent()),
						"Incorrect normal-notes content");
			} else {
				fail("Found child in time modification element that was not expected: " + timeModificationNode
						.getTextContent());
			}
		}

	}
}
