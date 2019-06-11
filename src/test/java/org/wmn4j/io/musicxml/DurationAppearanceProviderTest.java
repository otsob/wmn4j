package org.wmn4j.io.musicxml;

import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.wmn4j.notation.elements.Duration;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
