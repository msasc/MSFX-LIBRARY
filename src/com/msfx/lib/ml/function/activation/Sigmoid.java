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

package com.msfx.lib.ml.function.activation;

import com.msfx.lib.ml.function.Activation;

/**
 * Sigmoid activation.
 *
 * @author Miquel Sas
 */
public class Sigmoid extends Activation {

	/**
	 * Steepness.
	 */
	private final double sigma = 1.0;

	/**
	 * Constructor.
	 */
	public Sigmoid() { }

	/**
	 * Apply activation.
	 */
	@Override
	public double[] activations(double[] triggers) {
		double[] outputs = new double[triggers.length];
		double exp = 0;
		for (int i = 0; i < triggers.length; i++) {
			exp = Math.exp(-(sigma * triggers[i]));
			outputs[i] = 1 / (1 + exp);
		}
		return outputs;
	}

	/**
	 * Apply derivatives.
	 */
	@Override
	public double[] derivatives(double[] outputs) {
		double[] derivatives = new double[outputs.length];
		double out = 0;
		for (int i = 0; i < outputs.length; i++) {
			out = outputs[i];
			derivatives[i] = sigma * out * (1 - out);
		}
		return derivatives;
	}
}
