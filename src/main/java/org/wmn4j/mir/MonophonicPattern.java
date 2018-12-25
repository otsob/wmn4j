/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.mir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.wmn4j.notation.elements.Chord;
import org.wmn4j.notation.elements.Durational;

/**
 * A class for representing monophonic musical patterns. In a monophonic pattern
 * no notes occur simultaneously. The pattern cannot contain chords and does not
 * consist of multiple voices. This class is immutable.
 *
 * @author Otso Björklund
 */
public final class MonophonicPattern implements Pattern {

	private final List<Durational> contents;

	/**
	 * Constructor.
	 *
	 * @param contents the notation elements in temporal order that make up the
	 *                 pattern
	 */
	public MonophonicPattern(List<Durational> contents) {
		this.contents = Collections.unmodifiableList(new ArrayList<>(contents));
		if (this.contents == null) {
			throw new NullPointerException("Cannot create pattern with null contents");
		}
		if (this.contents.isEmpty()) {
			throw new IllegalArgumentException("Cannot create pattern with empty contents");
		}
		if (this.contents.stream().anyMatch((dur) -> (dur instanceof Chord))) {
			throw new IllegalArgumentException("Contents contain a Chord. Contents must be monophonic");
		}
	}

	/**
	 * Returns the notation elements in temporal order that make up the pattern.
	 *
	 * @return the notation elements in temporal order that make up the pattern
	 */
	public List<Durational> getContents() {
		return this.contents;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for (Durational dur : this.contents) {
			strBuilder.append(dur.toString());
		}

		return strBuilder.toString();
	}

	@Override
	public boolean equals(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMonophonic() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equalsInPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equalsEnharmonicallyInPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equalsInTransposedPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equalsInRhythm(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean equalsInOnsets(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}
}
