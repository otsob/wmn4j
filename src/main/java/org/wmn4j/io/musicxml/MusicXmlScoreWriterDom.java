package org.wmn4j.io.musicxml;

import org.w3c.dom.Element;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Score;

final class MusicXmlScoreWriterDom extends MusicXmlWriterDom {

	private final Score score;

	MusicXmlScoreWriterDom(Score score) {
		super(score);
		this.score = score;
	}

	@Override
	protected void writeScoreAttributes(Element rootElement) {
		if (!score.getTitle().isEmpty()) {
			final Element workTitleElement = getDocument().createElement(MusicXmlTags.SCORE_WORK_TITLE);
			workTitleElement.setTextContent(score.getTitle());

			final Element workElement = getDocument().createElement(MusicXmlTags.SCORE_WORK);
			workElement.appendChild(workTitleElement);
			rootElement.appendChild(workElement);
		}

		if (!score.getAttribute(Score.Attribute.MOVEMENT_TITLE).isEmpty()) {
			final Element movementTitleElement = getDocument().createElement(MusicXmlTags.SCORE_MOVEMENT_TITLE);
			movementTitleElement.setTextContent(score.getAttribute(Score.Attribute.MOVEMENT_TITLE));
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

		if (!score.getAttribute(Score.Attribute.COMPOSER).isEmpty()) {
			final Element composerElement = getDocument().createElement(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
			composerElement.setAttribute(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR_TYPE,
					MusicXmlTags.SCORE_IDENTIFICATION_COMPOSER);

			composerElement.setTextContent(score.getAttribute(Score.Attribute.COMPOSER));
			identificationElement.appendChild(composerElement);
		}

		if (!score.getAttribute(Score.Attribute.ARRANGER).isEmpty()) {
			final Element arrangerElement = getDocument().createElement(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR);
			arrangerElement.setAttribute(MusicXmlTags.SCORE_IDENTIFICATION_CREATOR_TYPE,
					MusicXmlTags.SCORE_IDENTIFICATION_ARRANGER);

			arrangerElement.setTextContent(score.getAttribute(Score.Attribute.ARRANGER));
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
			partName.setTextContent(part.getName());
			partElement.appendChild(partName);

			if (!part.getAttribute(Part.Attribute.ABBREVIATED_NAME).isEmpty()) {
				final Element abbreviatedPartName = getDocument().createElement(MusicXmlTags.PART_NAME_ABBREVIATION);
				abbreviatedPartName.setTextContent(part.getAttribute(Part.Attribute.ABBREVIATED_NAME));
				partElement.appendChild(abbreviatedPartName);
			}

			partList.appendChild(partElement);
		}

		scoreRoot.appendChild(partList);
	}
}
