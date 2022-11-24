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
 * ReLU activation.
 *
 * @author Miquel Sas
 */
public class ReLU extends Activation {

	/**
	 * Leaky alpha.
	 */
	private final double alpha = 0.1;

	/**
	 * Constructor.
	 */
	public ReLU() { }

	/**
	 * Apply activation.
	 */
	@Override
	public double[] activations(double[] triggers) {
		double[] outputs = new double[triggers.length];
		for (int i = 0; i < triggers.length; i++) {
			double trigger = triggers[i];
			double output = (trigger <= 0 ? alpha * trigger : trigger);
			outputs[i] = output;
		}
		return outputs;
	}

	/**
	 * Apply derivatives.
	 */
	@SuppressWarnings("unused")
	@Override
	public double[] derivatives(double[] outputs) {
		double[] derivatives = new double[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			derivatives[i] = (alpha == 0.0 ? 0.0 : 1.0);
		}
		return derivatives;
	}
}
