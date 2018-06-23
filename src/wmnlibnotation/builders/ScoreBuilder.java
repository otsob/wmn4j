/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibnotation.builders;

import wmnlibnotation.noteobjects.Part;
import wmnlibnotation.noteobjects.Score;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for building <code>Score</code> objects.
 * 
 * @author Otso Björklund
 */
public class ScoreBuilder {

	private Map<Score.Attribute, String> scoreAttr;
	private List<PartBuilder> partBuilders;

	public ScoreBuilder() {
		this.scoreAttr = new HashMap<>();
		this.partBuilders = new ArrayList<>();
	}

	/**
	 * Set attribute to given value.
	 * 
	 * @param attribute
	 *            the attribute to be set.
	 * @param attrValue
	 *            value for the attribute.
	 */
	public void setAttribute(Score.Attribute attribute, String attrValue) {
		this.scoreAttr.put(attribute, attrValue);
	}

	/**
	 * Add <code>PartBuilder</code> to builder.
	 * 
	 * @param partBuilder
	 *            partBuilder to added to this builder.
	 */
	public void addPart(PartBuilder partBuilder) {
		this.partBuilders.add(partBuilder);
	}

	/**
	 * Build a <code>Score</code> with the values specified in this builder.
	 * 
	 * @return a <code>Score</code> with the values specified in this builder.
	 */
	public Score build() {
		List<Part> parts = new ArrayList<>();
		this.partBuilders.forEach((builder) -> parts.add(builder.build()));
		return new Score(this.scoreAttr, parts);
	}
}
