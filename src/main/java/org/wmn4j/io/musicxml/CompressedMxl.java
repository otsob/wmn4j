/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.io.musicxml;

/**
 * String constants used with compressed MusicXML files.
 */
final class CompressedMxl {
	static final String META_INF_PATH = "META-INF/container.xml";
	static final String CONTAINER_TAG = "container";
	static final String ROOTFILE_TAG = "rootfile";
	static final String ROOTFILES_TAG = "rootfiles";
	static final String FULL_PATH_ATTR = "full-path";
	static final String MEDIA_TYPE_ATTR = "media-type";
	static final String COMPRESSED_CONTENT_TYPE = "application/vnd.recordare.musicxml";
	static final String UNCOMPRESSED_CONTENT_TYPE = "application/vnd.recordare.musicxml+xml";

	private CompressedMxl() {
		// Not meant to be instantiated.
	}
}
