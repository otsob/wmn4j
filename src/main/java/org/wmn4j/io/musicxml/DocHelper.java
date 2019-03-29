/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Helper methods for handling Document objects.
 */
final class DocHelper {

	/**
	 * Returns the child with the given name from the given parent node.
	 * <p>
	 * If there is no child with the given name, returns empty.
	 *
	 * @param parent    the parent node that is searched for the child
	 * @param childName the name of the child nodes
	 * @return the child with the given name from the given parent node
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
	 * Returns all children with the given name from the given parent node.
	 *
	 * @param parent    the parent node that is searched for children
	 * @param childName the name of the child nodes
	 * @return all children with the given name from the given parent node
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
