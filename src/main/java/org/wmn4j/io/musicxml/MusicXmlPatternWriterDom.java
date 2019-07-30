package org.wmn4j.io.musicxml;

import org.w3c.dom.Element;
import org.wmn4j.mir.Pattern;
import org.wmn4j.notation.elements.Barline;
import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Clef;
import org.wmn4j.notation.elements.Clefs;
import org.wmn4j.notation.elements.Duration;
import org.wmn4j.notation.elements.Durational;
import org.wmn4j.notation.elements.Durations;
import org.wmn4j.notation.elements.KeySignature;
import org.wmn4j.notation.elements.KeySignatures;
import org.wmn4j.notation.elements.Measure;
import org.wmn4j.notation.elements.MeasureAttributes;
import org.wmn4j.notation.elements.Note;
import org.wmn4j.notation.elements.Part;
import org.wmn4j.notation.elements.Pitch;
import org.wmn4j.notation.elements.SingleStaffPart;
import org.wmn4j.notation.elements.TimeSignature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class MusicXmlPatternWriterDom extends MusicXmlWriterDom {

	private static final KeySignature DEFAULT_KEY_SIGNATURE = KeySignatures.CMAJ_AMIN;
	private static final Barline PATTERN_ENDING_BARLINE = Barline.DOUBLE;
	private static final String DEFAULT_SCORE_TITLE = "Patterns";
	private static final int MIDDLE_C_AS_INT = Pitch.of(Pitch.Base.C, 0, 4).toInt();
	private static final Duration MEASURE_CUTOFF_DURATION = Durations.WHOLE;

	private final int divisions;
	private final Part partFromPatterns;
	private int measureNumber = 1;

	MusicXmlPatternWriterDom(Collection<Pattern> patterns) {
		Collection<Durational> allDurationals = new ArrayList<>();
		patterns.forEach(pattern -> {
			allDurationals.addAll(pattern.getContents());
		});

		this.divisions = computeDivisions(allDurationals.iterator());
		this.partFromPatterns = fromPatterns(patterns);
	}

	MusicXmlPatternWriterDom(Pattern pattern) {
		this(Collections.singletonList(pattern));
	}

	private Part fromPatterns(Collection<Pattern> patterns) {

		final List<Measure> allMeasures = new ArrayList<>();
		for (Pattern pattern : patterns) {
			allMeasures.addAll(singleVoicePatternToMeasureList(pattern));
		}

		return SingleStaffPart.of("", allMeasures);
	}

	private List<Measure> singleVoicePatternToMeasureList(Pattern pattern) {
		List<Measure> measures = new ArrayList<>();

		List<Durational> voice = new ArrayList<>();

		Duration cumulatedDuration = null;

		Iterator<Durational> iterator = pattern.getContents().iterator();
		while (iterator.hasNext()) {
			Durational durational = iterator.next();
			voice.add(durational);

			cumulatedDuration = cumulatedDuration != null
					? durational.getDuration().add(cumulatedDuration)
					: durational.getDuration();

			if (cumulatedDuration.isLongerThan(MEASURE_CUTOFF_DURATION)) {
				measures.add(createMeasureFromVoice(voice, cumulatedDuration, !iterator.hasNext(), measureNumber));
				measureNumber++;
				cumulatedDuration = null;
				voice = new ArrayList<>();
			}
		}

		if (!voice.isEmpty()) {
			measures.add(createMeasureFromVoice(voice, cumulatedDuration, true, measureNumber));
		}

		return measures;
	}

	private Measure createMeasureFromVoice(List<Durational> voice, Duration totalDuration, boolean isLast,
			int measureNumber) {
		TimeSignature timeSignature = TimeSignature.of(totalDuration.getNumerator(), totalDuration.getDenominator());
		Barline rightBarline = isLast ? PATTERN_ENDING_BARLINE : Barline.INVISIBLE;

		MeasureAttributes attributes = MeasureAttributes
				.of(timeSignature, DEFAULT_KEY_SIGNATURE, rightBarline, findSuitableClef(voice));

		return Measure.of(measureNumber, Collections.singletonMap(1, voice), attributes);
	}

	@Override
	protected int getDivisions() {
		return divisions;
	}

	@Override
	protected void writeScoreAttributes(Element rootElement) {
		final Element workTitleElement = getDocument().createElement(MusicXmlTags.SCORE_WORK_TITLE);
		workTitleElement.setTextContent(DEFAULT_SCORE_TITLE);

		final Element workElement = getDocument().createElement(MusicXmlTags.SCORE_WORK);
		workElement.appendChild(workTitleElement);
		rootElement.appendChild(workElement);

		rootElement.appendChild(createIdentificationElement());
	}

	@Override
	protected Element createIdentificationElement() {
		final Element identificationElement = getDocument().createElement(MusicXmlTags.SCORE_IDENTIFICATION);
		identificationElement.appendChild(createEncodingElement());
		return identificationElement;
	}

	@Override
	protected void writePartList(Element scoreRoot) {
		final Element partList = getDocument().createElement(MusicXmlTags.PART_LIST);

		final Element partElement = getDocument().createElement(MusicXmlTags.PLIST_SCORE_PART);
		final String partId = "P1";
		addPartWithId(partId, partFromPatterns);
		partElement.setAttribute(MusicXmlTags.PART_ID, partId);

		final Element partName = getDocument().createElement(MusicXmlTags.PART_NAME);
		// An empty part name element is needed to make the MusicXML valid.
		partElement.appendChild(partName);

		partList.appendChild(partElement);

		scoreRoot.appendChild(partList);
	}

	private Clef findSuitableClef(List<Durational> voice) {
		int sumOfPitch = 0;
		int pitchCount = 0;
		for (Durational durational : voice) {
			if (durational instanceof Note) {
				sumOfPitch += ((Note) durational).getPitch().toInt();
				pitchCount++;
			}

			if (durational instanceof Chord) {
				for (Note note : (Chord) durational) {
					sumOfPitch += note.getPitch().toInt();
					pitchCount++;
				}
			}
		}

		if (pitchCount == 0) {
			return Clefs.G;
		}

		final int averagePitch = sumOfPitch / pitchCount;
		if (averagePitch > MIDDLE_C_AS_INT) {
			return Clefs.G;
		}

		return Clefs.F;
	}
}
