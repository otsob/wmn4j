/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class for building {@link Score} objects.
 * <p>
 * Instances of this class are not thread-safe.
 */
public class ScoreBuilder {

	private final Map<Score.Attribute, String> scoreAttr;
	private final List<PartBuilder> partBuilders;

	/**
	 * Constructor that creates an empty builder.
	 */
	public ScoreBuilder() {
		this.scoreAttr = new HashMap<>();
		this.partBuilders = new ArrayList<>();
	}

	/**
	 * Set the given attribute to given value.
	 *
	 * @param attribute      the attribute to be set
	 * @param attributeValue value for the attribute
	 * @return reference to this
	 */
	public ScoreBuilder setAttribute(Score.Attribute attribute, String attributeValue) {
		this.scoreAttr.put(attribute, attributeValue);
		return this;
	}

	/**
	 * Add {@link PartBuilder} to this builder.
	 *
	 * @param partBuilder partBuilder to add to this builder
	 * @return reference to this
	 */
	public ScoreBuilder addPart(PartBuilder partBuilder) {
		this.partBuilders.add(partBuilder);
		return this;
	}

	/**
	 * Returns a {@link Score} with the contents of this builder.
	 *
	 * @return a score with the contents of this builder
	 */
	public Score build() {
		padPartsWithEmptyMeasures();
		final List<Part> parts = new ArrayList<>();
		this.partBuilders.forEach((builder) -> parts.add(builder.build()));
		return Score.of(this.scoreAttr, parts);
	}

	private void padPartsWithEmptyMeasures() {
		Optional<List<MeasureBuilder>> longestStaffInScoreContentsOpt = partBuilders.stream()
				.flatMap(partBuilder -> partBuilder.getStaffNumbers().stream()
						.map(staffNumber -> partBuilder.getStaffContents(staffNumber)))
				.max(Comparator.comparing(List::size));

		if (longestStaffInScoreContentsOpt.isPresent()) {

			List<MeasureBuilder> longestStaffInScoreContents = longestStaffInScoreContentsOpt.get();

			for (PartBuilder partBuilder : partBuilders) {

				Optional<Integer> numberOfLongestStaffInPartOpt = partBuilder.getStaffNumbers().stream()
						.max(Comparator.comparing(staffNumber -> partBuilder.getStaffContents(staffNumber).size()));

				if (numberOfLongestStaffInPartOpt.isPresent()) {
					final int numberOfLongestStaffInPart = numberOfLongestStaffInPartOpt.get();
					final int longestStaffInPartLength = partBuilder.getStaffContents(numberOfLongestStaffInPart)
							.size();

					for (int i = longestStaffInPartLength; i < longestStaffInScoreContents.size(); ++i) {
						partBuilder.addToStaff(numberOfLongestStaffInPart,
								MeasureBuilder.withAttributesOf(longestStaffInScoreContents.get(i)));
					}
				}
			}
		}
	}
}
