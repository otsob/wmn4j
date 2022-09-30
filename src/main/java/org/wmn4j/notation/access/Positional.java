/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.notation.access;

import org.wmn4j.notation.Durational;

/**
 * Pairs a durational with its position within a score.
 *
 * @param durational the durational element
 * @param position   the durational element's position within a score
 */
public record Positional(Durational durational, Position position) {
}
