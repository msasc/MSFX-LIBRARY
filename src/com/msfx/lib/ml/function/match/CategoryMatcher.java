/*
 * Copyright (c) 2022 Miquel Sas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.msfx.lib.ml.function.match;

import com.msfx.lib.ml.function.Matcher;

import java.util.ArrayList;
import java.util.List;

/**
 * Evaluate the match when the correct output is a category, normally one element is
 * 1 and the rest 0. The index of the maximum of the network pattern has to be
 * the same as the index of the maximum of the pattern output.
 *
 * @author Miquel Sas
 */
public class CategoryMatcher implements Matcher {

	/**
	 * Constructor.
	 */
	public CategoryMatcher() { }

	/**
	 * Check whether the argument vectors can be considered to be the same.
	 *
	 * @param patternOutput Pattern output
	 * @param networkOutput Network output.
	 * @return A boolean.
	 */
	@Override
	public boolean match(List<double[]> patternOutput, List<double[]> networkOutput) {

		List<Integer> indexesPattern = new ArrayList<>();
		List<Integer> indexesNetwork = new ArrayList<>();
		for (int i = 0; i < patternOutput.size(); i++) {
			double[] pattern = patternOutput.get(i);
			double[] network = networkOutput.get(i);
			int indexPattern = -1;
			int indexNetwork = -1;
			double maxPattern = Double.NEGATIVE_INFINITY;
			double maxNetwork = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < pattern.length; j++) {
				if (pattern[j] > maxPattern) {
					maxPattern = pattern[j];
					indexPattern = j;
				}
				if (network[j] > maxNetwork) {
					maxNetwork = network[j];
					indexNetwork = j;
				}
			}
			indexesPattern.add(indexPattern);
			indexesNetwork.add(indexNetwork);
		}

		for (int i = 0; i < indexesPattern.size(); i++) {
			if (indexesPattern.get(i) != indexesNetwork.get(i)) {
				return false;
			}
		}
		return true;
	}
}
