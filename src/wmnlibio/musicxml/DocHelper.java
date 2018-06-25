/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibio.musicxml;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Helpers methods for handling <code>Document</code> objects.
 * 
 * @author Otso Björklund
 *
 */
class DocHelper {
	/**
	 * Find the child with childName from the children of Node parent. return null
	 * if no child with given name is found.
	 */
	static Node findChild(Node parent, String childName) {
		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child != null && child.getNodeName() != null) {
				if (child.getNodeName().equals(childName))
					return child;
			}
		}

		return null;
	}

	/**
	 * Find all children with given name from Node parent.
	 */
	static List<Node> findChildren(Node parent, String childName) {
		List<Node> foundChildren = new ArrayList<>();

		NodeList children = parent.getChildNodes();
		for (int i = 0; i < children.getLength(); ++i) {
			Node child = children.item(i);
			if (child != null && child.getNodeName() != null) {
				if (children.item(i).getNodeName().equals(childName))
					foundChildren.add(child);
			}
		}

		return foundChildren;
	}

	private DocHelper() {
		// Not meant to be instantiated.
	}
}
