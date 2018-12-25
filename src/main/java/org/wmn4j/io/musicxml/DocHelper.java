/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helpers methods for handling <code>Document</code> objects.
 *
 * @author Otso Björklund
 *
 */
final class DocHelper {
	/**
	 * Find the child with childName from the children of Node parent.
	 *
	 * @return <code>Optional</code> that contains the child <code>Node</code> if it
	 *         is found, otherwise an empty <code>Optional</code>.
	 */
	static Optional<Node> findChild(Node parent, String childName) {
		final NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			final Node child = children.item(i);
			if (child != null && child.getNodeName() != null) {
				if (child.getNodeName().equals(childName)) {
					return Optional.of(child);
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Find all children with given name from Node parent.
	 */
	static List<Node> findChildren(Node parent, String childName) {
		final List<Node> foundChildren = new ArrayList<>();

		final NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			final Node child = children.item(i);
			if (child != null && child.getNodeName() != null) {
				if (children.item(i).getNodeName().equals(childName)) {
					foundChildren.add(child);
				}
			}
		}

		return foundChildren;
	}

	private DocHelper() {
		// Not meant to be instantiated.
	}
}
