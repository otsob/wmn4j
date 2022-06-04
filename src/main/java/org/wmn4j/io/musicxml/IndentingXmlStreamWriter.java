/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;
import java.util.Collections;

final class IndentingXmlStreamWriter implements XMLStreamWriter {

	private static final String LINE_BREAK = "\n";
	private static final String INDENT = "  ";

	private final XMLStreamWriter writer;
	private int depth;
	private boolean valueWritten;

	IndentingXmlStreamWriter(OutputStream outputStream) throws XMLStreamException {
		this.writer = XMLOutputFactory.newInstance().createXMLStreamWriter(outputStream);
		this.depth = 0;
		this.valueWritten = false;
	}

	private void writeIndent(boolean isStart) throws XMLStreamException {
		if (!isStart) {
			--depth;
		}

		writer.writeCharacters(LINE_BREAK);
		writer.writeCharacters(getIndentation(depth));

		if (isStart) {
			++depth;
		}
	}

	private String getIndentation(int depth) {
		return String.join("", Collections.nCopies(depth, INDENT));
	}

	@Override
	public void writeStartElement(String localName) throws XMLStreamException {
		writeIndent(true);
		writer.writeStartElement(localName);
	}

	@Override
	public void writeStartElement(String namespaceURI, String localName) throws XMLStreamException {
		writeIndent(true);
		writer.writeStartElement(namespaceURI, localName);
	}

	@Override
	public void writeStartElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		writeIndent(true);
		writer.writeStartElement(prefix, localName, namespaceURI);
	}

	@Override
	public void writeEmptyElement(String namespaceURI, String localName) throws XMLStreamException {
		writeIndent(true);
		writer.writeEmptyElement(namespaceURI, localName);
		--depth;
	}

	@Override
	public void writeEmptyElement(String prefix, String localName, String namespaceURI) throws XMLStreamException {
		writeIndent(true);
		writer.writeEmptyElement(prefix, localName, namespaceURI);
		--depth;
	}

	@Override
	public void writeEmptyElement(String localName) throws XMLStreamException {
		writeIndent(true);
		writer.writeEmptyElement(localName);
		--depth;
	}

	@Override
	public void writeEndElement() throws XMLStreamException {
		if (valueWritten) {
			--depth;
			valueWritten = false;
		} else {
			writeIndent(false);
		}
		writer.writeEndElement();
	}

	@Override
	public void writeEndDocument() throws XMLStreamException {
		writer.writeEndDocument();
	}

	@Override
	public void close() throws XMLStreamException {
		writer.close();
	}

	@Override
	public void flush() throws XMLStreamException {
		writer.flush();
	}

	@Override
	public void writeAttribute(String localName, String value) throws XMLStreamException {
		writer.writeAttribute(localName, value);
	}

	@Override
	public void writeAttribute(String prefix, String namespaceURI, String localName, String value)
			throws XMLStreamException {
		writer.writeAttribute(prefix, namespaceURI, localName, value);
	}

	@Override
	public void writeAttribute(String namespaceURI, String localName, String value) throws XMLStreamException {
		writer.writeAttribute(namespaceURI, localName, value);
	}

	@Override
	public void writeNamespace(String prefix, String namespaceURI) throws XMLStreamException {
		writer.writeNamespace(prefix, namespaceURI);
	}

	@Override
	public void writeDefaultNamespace(String namespaceURI) throws XMLStreamException {
		writer.writeDefaultNamespace(namespaceURI);
	}

	@Override
	public void writeComment(String data) throws XMLStreamException {
		writer.writeComment(data);
	}

	@Override
	public void writeProcessingInstruction(String target) throws XMLStreamException {
		writer.writeProcessingInstruction(target);
	}

	@Override
	public void writeProcessingInstruction(String target, String data) throws XMLStreamException {
		writer.writeProcessingInstruction(target, data);
	}

	@Override
	public void writeCData(String data) throws XMLStreamException {
		writer.writeCData(data);
	}

	@Override
	public void writeDTD(String dtd) throws XMLStreamException {
		writer.writeDTD(dtd);
	}

	@Override
	public void writeEntityRef(String name) throws XMLStreamException {
		writer.writeEntityRef(name);
	}

	@Override
	public void writeStartDocument() throws XMLStreamException {
		writer.writeStartDocument();
	}

	@Override
	public void writeStartDocument(String version) throws XMLStreamException {
		writer.writeStartDocument(version);
	}

	@Override
	public void writeStartDocument(String encoding, String version) throws XMLStreamException {
		writer.writeStartDocument(encoding, version);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		writer.writeCharacters(text);
		valueWritten = true;
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
		writer.writeCharacters(text, start, len);
		valueWritten = true;
	}

	@Override
	public String getPrefix(String uri) throws XMLStreamException {
		return writer.getPrefix(uri);
	}

	@Override
	public void setPrefix(String prefix, String uri) throws XMLStreamException {
		writer.setPrefix(prefix, uri);
	}

	@Override
	public void setDefaultNamespace(String uri) throws XMLStreamException {
		writer.setDefaultNamespace(uri);
	}

	@Override
	public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
		writer.setNamespaceContext(context);
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return writer.getNamespaceContext();
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return writer.getProperty(name);
	}
}
