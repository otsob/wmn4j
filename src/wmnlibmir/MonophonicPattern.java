/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import wmnlibnotation.noteobjects.Chord;
import wmnlibnotation.noteobjects.Durational;

/**
 * A class for representing monophonic musical patterns. In a monophonic pattern
 * no notes occur simultaneously. The pattern cannot contain chords and does not
 * consist of multiple voices. This class is immutable.
 * 
 * @author Otso Björklund
 */
public final class MonophonicPattern implements Pattern {

	private final List<Durational> contents;

	public MonophonicPattern(List<Durational> contents) {
		this.contents = Collections.unmodifiableList(new ArrayList<>(contents));
		if (this.contents == null)
			throw new NullPointerException("Cannot create pattern with null contents");
		if (this.contents.isEmpty())
			throw new IllegalArgumentException("Cannot create pattern with empty contents");
		if (this.contents.stream().anyMatch((dur) -> (dur instanceof Chord)))
			throw new IllegalArgumentException("Contents contain a Chord. Contents must be monophonic");
	}

	public List<Durational> getContents() {
		return this.contents;
	}

	@Override
	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		for (Durational dur : this.contents)
			strBuilder.append(dur.toString());

		return strBuilder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wmnlibmir.Pattern#equals(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equals(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wmnlibmir.Pattern#isMonophonic()
	 */
	@Override
	public boolean isMonophonic() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wmnlibmir.Pattern#equalsInPitch(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wmnlibmir.Pattern#equalsEnharmonicallyInPitch(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsEnharmonicallyInPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wmnlibmir.Pattern#equalsInTransposedPitch(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInTransposedPitch(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wmnlibmir.Pattern#equalsInRhythm(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInRhythm(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see wmnlibmir.Pattern#equalsInOnsets(wmnlibmir.Pattern)
	 */
	@Override
	public boolean equalsInOnsets(Pattern other) {
		// TODO Auto-generated method stub
		return false;
	}
}
