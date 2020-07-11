/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io;

/**
 * Exception for cases in which parsing a file fails. For example, this may be thrown when attempting to read an invalid
 * MusicXML file.
 */
public final class ParsingFailureException extends Exception {

	/**
	 * Constructor.
	 *
	 * @param message a message that indicates why the parsing failed
	 */
	public ParsingFailureException(String message) {
		super(message);
	}
}
