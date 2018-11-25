/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.noteobjects;

/**
 * Interface for all the notation objects that have a duration.
 * 
 * @author Otso Björklund
 */
public interface Durational {

	/**
	 * @return <code>Duration</code> of this.
	 */
	Duration getDuration();

	/**
	 * @return true if this is a rest, false otherwise.
	 */
	boolean isRest();
}
