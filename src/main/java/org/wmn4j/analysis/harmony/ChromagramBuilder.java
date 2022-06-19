/*
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package org.wmn4j.analysis.harmony;

import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.PitchClass;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Builder for {@link Chromagram} instances.
 * <p>
 * Instances of this class are not thread-safe.
 */
public final class ChromagramBuilder {

	private final Function<Note, Double> weightFunction;
	private final Map<PitchClass, Double> profile;

	/**
	 * Returns a weight value for the note that is the double
	 * value of its duration.
	 *
	 * @param note the note for which the weight is returned
	 * @return a weight value for the note that is the double
	 * value of its duration
	 */
	public static Double durationWeight(Note note) {
		return note.getDuration().toDouble();
	}

	/**
	 * Constructor that creates a builder that adds to the pitch class
	 * bins based on count of notes or pitch classes. For example, adding a note
	 * with the pitch class C to builder created with this constructor will
	 * increment the value of C by 1.0.
	 */
	public ChromagramBuilder() {
		this(note -> 1.0);
	}

	/**
	 * Constructor that creates a builder that adds to the pitch class
	 * bins using the given weight function.
	 * <p>
	 * When adding a note to the  builder the value that is added for
	 * the corresponding pitch class is the value returned by the
	 * specified weight function. The weight function must produce only
	 * non-negative values.
	 *
	 * @param weightFunction the weight function used for computing the non-negative
	 *                       weight of a notes's pitch class
	 */
	public ChromagramBuilder(Function<Note, Double> weightFunction) {
		this.weightFunction = weightFunction;
		this.profile = new EnumMap<>(PitchClass.class);
		for (PitchClass pc : PitchClass.values()) {
			profile.put(pc, 0.0);
		}
	}

	/**
	 * Sets the value for the given pitch class.
	 *
	 * @param pc    the pitch class for which the value is set
	 * @param value a non-negative value to set for the pitch class
	 * @return reference to this
	 */
	public ChromagramBuilder setValue(PitchClass pc, double value) {
		if (value < 0.0) {
			throw new IllegalArgumentException("value must be at least 0.0");
		}

		this.profile.put(pc, value);
		return this;
	}

	/**
	 * Increments the values for the the pitch classes of the notes in the given chord.
	 * If a weight function is specified, then the added values are provided by the weight
	 * function.
	 *
	 * @param chord chord for which the pitch classes of the notes are incremented
	 * @return reference to this
	 */
	public ChromagramBuilder add(Chord chord) {
		for (Note note : chord) {
			this.add(note);
		}

		return this;
	}

	/**
	 * Increments the value of the pitch class of the note if the note has pitch.
	 * If a weight function is specified, then the added value is provided by the weight
	 * function.
	 *
	 * @param note note for which the pitch class is incremented
	 * @return reference to this
	 */
	public ChromagramBuilder add(Note note) {
		if (note.hasPitch()) {
			final Pitch pitch = note.getPitch().get();
			final PitchClass pc = pitch.getPitchClass();
			double value = profile.get(pitch.getPitchClass());
			value += weightFunction.apply(note);
			this.setValue(pc, value);
		}

		return this;
	}

	/**
	 * Returns a chromagram with the values set in this builder.
	 *
	 * @return a chromagram with the values set in this builder
	 */
	public Chromagram build() {
		return Chromagram.of(profile);
	}
}
