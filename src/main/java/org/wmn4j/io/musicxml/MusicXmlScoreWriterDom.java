/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.w3c.dom.Element;
import org.wmn4j.notation.Part;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.access.PartWiseScoreIterator;

import java.util.Optional;

final class MusicXmlScoreWriterDom extends MusicXmlWriterDom {

	private final Score score;
	private final int divisions;

	MusicXmlScoreWriterDom(Score score) {
		this.score = score;
		this.divisions = computeDivisions(new PartWiseScoreIterator(score));
	}

	@Override
	protected int getDivisions() {
		return divisions;
	}

	@Override
	protected void writeScoreAttributes(Element rootElement) {
		if (!score.getTitle().isEmpty()) {
			final Element workTitleElement = getDocument().createElement(MusicXmlTags.SCORE_WORK_TITLE);
			workTitleElement.setTextContent(score.getTitle().get());

			final Element workElement = getDocument().createElement(MusicXmlTags.SCORE_WORK);
			workElement.appendChild(workTitleElement);
			rootElement.appendChild(workElement);
		}

		Optional<String> movementTitle = score.getAttribute(Score.Attribute.MOVEMENT_TITLE);
		if (movementTitle.isPresent()) {
			final Element movementTitleElement = getDocument().createElement(MusicXmlTags.SCORE_MOVEMENT_TITLE);
			movementTitleElement.setTextContent(movementTitle.get());
			rootElement.appendChild(movementTitleElement);
		}

		final Element identificationElement = createIdentificationElement();
		if (identificationElement.hasChildNodes()) {
			rootElement.appendChild(identificationElement);
		}
	}

	@Override
	protected Element createIdentificationElement() {
		final Element identificationElement = getDocument().createElement(MusicXmlTags.SCORE_IDENTIFICATION);

		Optional<String> composerName = score.getAttribute(Score.Attribute.COMPOSER);
		if (composerName.isPresent()) {
			final Element composerElement = getDocument().createElement(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
			composerElement.setAttribute(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR_TYPE,
					MusicXmlTags.SCORE_IDENTIFICATION_COMPOSER);

			composerElement.setTextContent(composerName.get());
			identificationElement.appendChild(composerElement);
		}

		Optional<String> arrangerName = score.getAttribute(Score.Attribute.ARRANGER);
		if (arrangerName.isPresent()) {
			final Element arrangerElement = getDocument().createElement(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
			arrangerElement.setAttribute(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR_TYPE,
					MusicXmlTags.SCORE_IDENTIFICATION_ARRANGER);

			arrangerElement.setTextContent(arrangerName.get());
			identificationElement.appendChild(arrangerElement);
		}

		identificationElement.appendChild(createEncodingElement());

		return identificationElement;
	}

	@Override
	protected void writePartList(Element scoreRoot) {
		final Element partList = getDocument().createElement(MusicXmlTags.PART_LIST);

		for (int i = 0; i < this.score.getPartCount(); ++i) {
			final Element partElement = getDocument().createElement(MusicXmlTags.PLIST_SCORE_PART);
			final String partId = "P" + (i + 1);
			final Part part = this.score.getPart(i);
			addPartWithId(partId, part);

			partElement.setAttribute(MusicXmlTags.PART_ID, partId);
			final Element partName = getDocument().createElement(MusicXmlTags.PART_NAME);
			partName.setTextContent(part.getName().orElse(""));
			partElement.appendChild(partName);

			Optional<String> abbreviatedPartName = part.getAttribute(Part.Attribute.ABBREVIATED_NAME);
			if (abbreviatedPartName.isPresent()) {
				final Element abbreviatedPartNameElemen = getDocument()
						.createElement(MusicXmlTags.PART_NAME_ABBREVIATION);
				abbreviatedPartNameElemen.setTextContent(abbreviatedPartName.get());
				partElement.appendChild(abbreviatedPartNameElemen);
			}

			partList.appendChild(partElement);
		}

		scoreRoot.appendChild(partList);
	}
}
