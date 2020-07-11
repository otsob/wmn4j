/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * The properties of the project.
 */
public final class Wmn4j {

	private static final Logger LOG = LoggerFactory.getLogger(Wmn4j.class);

	private static final Wmn4j INSTANCE = new Wmn4j();

	private static final String PROPERTIES_FILE_NAME = "wmn4j.properties";
	private static final String VERSION = "version";

	private final String version;

	private Wmn4j() {
		version = readVersionFromProperties();
	}

	private String readVersionFromProperties() {
		String versionNumberFromProperties = "";

		try (InputStream input = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {

			if (input != null) {
				Properties projectProperties = new Properties();
				projectProperties.load(input);
				versionNumberFromProperties = projectProperties.getProperty(VERSION);
			} else {
				LOG.warn("Unable to read auto-generated Wmn4j properties file: " + PROPERTIES_FILE_NAME);
			}

		} catch (IOException exception) {
			LOG.warn("Reading Wmn4j properties failed with ", exception);
		}

		return versionNumberFromProperties;
	}

	/**
	 * Returns the version number of the project.
	 *
	 * @return the version number of the project
	 */
	public static String getVersion() {
		return INSTANCE.version;
	}

	/**
	 * Returns the name of this project.
	 *
	 * @return the name of this project
	 */
	public static String getName() {
		return "Western Music Notation for Java (wmn4j)";
	}

	/**
	 * Returns the name of this project along with the version number.
	 *
	 * @return the name of this project along with the version number
	 */
	public static String getNameWithVersion() {
		return getName() + " " + getVersion();
	}
}
