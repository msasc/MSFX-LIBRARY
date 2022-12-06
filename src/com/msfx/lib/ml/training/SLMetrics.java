/*
 * Copyright (c) 2022 Miquel Sas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.msfx.lib.ml.training;

import java.util.ArrayList;
import java.util.List;

import com.msfx.lib.ml.function.Matcher;
import com.msfx.lib.ml.function.match.CategoryMatcher;
import com.msfx.lib.util.Numbers;
import com.msfx.lib.util.Vector;

/**
 * Metrics used to evaluate the performance in a supervised learning training process.
 * 
 * @author Miquel Sas
 */
public class SLMetrics {

	public static record Track(
			/** Number of matches. */
			int matches,
			/** Calls to compute. */
			int calls,
			/** Average absolute error. */
			double errorAvg,
			/** Average absolute error standard deviation. */
			double errorStd,
			/** Performance. */
			double performance) {}

	/** M-Squared vector. */
	private List<double[]> errors;
	/** Match function, default is match category. */
	private Matcher matcher = new CategoryMatcher();

	/** List of lengths of the arrays of pattern and network output. */
	private int[] lengths;

	/** Number of matches. */
	private int matches;
	/** Calls to compute. */
	private int calls;

	/** Average absolute error. */
	private double errorAvg;
	/** Average absolute error standard deviation. */
	private double errorStd;

	/**
	 * Constructor.
	 * @param lengths List of lengths of the arrays of pattern and network output.
	 */
	public SLMetrics(List<Integer> lengths) { this(Numbers.toIntArray(lengths)); }

	/**
	 * Constructor.
	 * @param lengths List of lengths of the arrays of pattern and network output.
	 */
	public SLMetrics(int... lengths) {
		if (lengths == null || lengths.length == 0) throw new IllegalArgumentException();
		this.lengths = lengths;
		reset();
	}

	/**
	 * Compute a pattern and a network output to calculate the metric values.
	 * @param patternOutput The pattern output.
	 * @param networkOutput The network output.
	 */
	public void compute(List<double[]> patternOutput, List<double[]> networkOutput) {

		boolean valid = true;
		valid &= (patternOutput.size() == lengths.length);
		valid &= (networkOutput.size() == lengths.length);
		for (int i = 0; i < patternOutput.size(); i++) {
			valid &= (patternOutput.get(i).length == lengths[i]);
			valid &= (networkOutput.get(i).length == lengths[i]);
		}
		if (!valid) throw new IllegalArgumentException();

		for (int i = 0; i < lengths.length; i++) {
			double[] pattern = patternOutput.get(i);
			double[] network = networkOutput.get(i);
			double[] error = errors.get(i);
			for (int j = 0; j < lengths[i]; j++) {
				error[j] += Math.abs(pattern[j] - network[j]);
			}
		}

		if (matcher.match(patternOutput, networkOutput)) {
			matches++;
		}

		calls++;
		int length = 0;
		for (int len : lengths) { length += len; }
		double[] error = new double[length];
		int index = 0;
		for (int i = 0; i < lengths.length; i++) {
			for (int j = 0; j < lengths[i]; j++) {
				error[index++] = errors.get(i)[j] / calls;
			}
		}
		errorAvg = Vector.mean(error);
		errorStd = Vector.stddev(error, errorAvg);
	}

	/**
	 * Returns a track with the current metrics.
	 * @return The track with the current metrics.
	 */
	public Track getTrack() {
		double performance = (calls == 0 ? 0.0 : (double) matches / (double) calls);
		return new Track(matches, calls, errorAvg, errorStd, performance);
	}

	/**
	 * Reset.
	 */
	public void reset() {
		errors = new ArrayList<double[]>();
		for (int length : lengths) {
			errors.add(new double[length]);
		}
		matches = 0;
		calls = 0;
		errorAvg = 0;
		errorStd = 0;
	}

	/**
	 * Set the matcher.
	 * @param matcher The matcher.
	 */
	public void setMatcher(Matcher matcher) { this.matcher = matcher; }
}
