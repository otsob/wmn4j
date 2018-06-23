/*
 * Copyright 2018 Otso Björklund.
 * Distributed under the MIT license (see LICENSE.txt or https://opensource.org/licenses/MIT).
 */
package wmnlibmir.pattern_discovery;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 *
 * @author Otso Björklund
 */
public class PatternAlgorithmsTest {

	public PatternAlgorithmsTest() {
	}

	private List<NoteEventVector> getDataset() {
		List<NoteEventVector> dataset = new ArrayList<>();
		double[] components1 = { 3, 2 };
		dataset.add(new NoteEventVector(components1));

		double[] components2 = { 2, 3 };
		dataset.add(new NoteEventVector(components2));

		double[] components3 = { 2, 2 };
		dataset.add(new NoteEventVector(components3));

		double[] components4 = { 2, 1 };
		dataset.add(new NoteEventVector(components4));

		double[] components5 = { 1, 1 };
		dataset.add(new NoteEventVector(components5));

		double[] components6 = { 1, 3 };
		dataset.add(new NoteEventVector(components6));

		return dataset;
	}

	private boolean tecsAreCorrect(List<TEC> tecs) {
		return tecs.size() == 4;
	}

	@Test
	public void testComputeTecs() {
		List<TEC> tecs = PatternAlgorithms.computeTecs(new PointSet(getDataset()));
		for (TEC tec : tecs) {
			System.out.println(tec);
		}
		assertTrue(tecsAreCorrect(tecs));
	}
}
