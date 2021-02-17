/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.Key;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.PitchClass;
import org.wmn4j.notation.Score;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides key analysis using the Krumhansl-Schmuckler key finding algorithm.
 * <p>
 * The Krumhansl and Schmuckler key finding algorithm [1] works by forming a chromagram
 * (see {@link Chromagram}) of the given input and comparing that chromagram against
 * predefined chromagrams for each major and minor key. The key whose chromagram has the
 * greatest correlation with the chromagram of the input is returned.
 * The predefined chromagrams are based on music psychology research.
 * <p>
 * This implementation of the algorithm uses duration weighing, i.e.
 * the amount a note adds to a pitch class is directly proportional to
 * its duration, when building the chromagram for the input.
 *
 * <p>
 * [1] Krumhansl, C. L. (1990). Cognitive foundations of musical pitch.
 * New York: Oxford University Press.
 * <p>
 * This class is immutable.
 */
public final class KSKeyAnalysis implements KeyAnalysis {

	private final Key key;

	/**
	 * Returns a key analysis of the given selection using the Krumhansl and Schmuckler algorithm.
	 *
	 * @param selection the selection for which the key analysis is returned
	 * @return a key analysis of the given selection
	 */
	public static KeyAnalysis of(Iterable<Durational> selection) {
		return new KSKeyAnalysis(selection.iterator());
	}

	/**
	 * Returns a key analysis of the given score using the Krumhansl and Schmuckler algorithm.
	 *
	 * @param score the score for which the key analysis is returned
	 * @return a key analysis of the given selection
	 */
	public static KeyAnalysis of(Score score) {
		return new KSKeyAnalysis(score.partwiseIterator());
	}

	private static Key findBestMatchingKey(Iterator<Durational> selectionIterator) {
		final ChromagramBuilder builder = new ChromagramBuilder(ChromagramBuilder::durationWeight);
		while (selectionIterator.hasNext()) {
			Durational dur = selectionIterator.next();
			if (dur instanceof Note) {
				builder.add((Note) dur);
			}
			if (dur instanceof Chord) {
				builder.add((Chord) dur);
			}
		}

		return ChromagramMatcher.INSTANCE.findBestMatch(builder.build());
	}

	private KSKeyAnalysis(Iterator<Durational> selectionIterator) {
		this.key = findBestMatchingKey(selectionIterator);
	}

	@Override
	public Key getKey() {
		return key;
	}

	/**
	 * Singleton for finding the best matching key from the key profiles
	 * definedby Krumhansl and Schmuckler.
	 */
	private enum ChromagramMatcher {

		INSTANCE;

		private static final Logger LOG = LoggerFactory.getLogger(ChromagramMatcher.class);
		private static final String KEY_PROFILES_RESOURCE = "org/wmn4j/analysis/harmony/KSKeyProfiles.csv";
		private final Map<Key, Chromagram> keyProfiles;

		private Map<Key, Chromagram> readKeysFromResourceFile() {
			Map<Key, Chromagram> profiles = new EnumMap<>(Key.class);
			InputStream input = getClass().getClassLoader().getResourceAsStream(KEY_PROFILES_RESOURCE);

			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
				String line = reader.readLine();

				while (line != null && !line.isEmpty()) {
					if (line.charAt(0) != '#') {
						final String[] lineContents = line.split(",");
						final Key key = keyFromString(lineContents[0].trim());

						ChromagramBuilder chromagramBuilder = new ChromagramBuilder();

						for (int i = 1; i < lineContents.length; ++i) {
							final PitchClass pc = PitchClass.fromInt(i - 1);
							chromagramBuilder.setValue(pc, Double.parseDouble(lineContents[i].trim()));
						}

						profiles.put(key, chromagramBuilder.build());
					}
					line = reader.readLine();
				}
			} catch (Exception exception) {
				LOG.error("Failed to initialize key analysis profiles:", exception);
			}

			return Collections.unmodifiableMap(profiles);
		}

		private Key keyFromString(String keyAsString) {
			// NOTE: This implementation is not ideal, it depends on the
			// key names being written in the file in exactly
			// the same way as in they are written in the
			// enum names.
			return Key.valueOf(keyAsString);
		}

		ChromagramMatcher() {
			keyProfiles = readKeysFromResourceFile();
		}

		/**
		 * Returns the key whose profile has maximum correlation
		 * with the given chromagram.
		 */
		Key findBestMatch(Chromagram chromagram) {
			double maxCorrelation = -Double.MAX_VALUE;
			Key bestMatch = Key.C_MAJOR;

			for (Key key : keyProfiles.keySet()) {
				final double correlation = chromagram.correlation(keyProfiles.get(key));
				if (correlation > maxCorrelation) {
					maxCorrelation = correlation;
					bestMatch = key;
				}
			}

			return bestMatch;
		}
	}
}
