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
 * TANH activation.
 *
 * @author Miquel Sas
 */
public class TANH extends Activation {

	/**
	 * Constructor.
	 */
	public TANH() {}

	/**
	 * Apply activations.
	 */
	@Override
	public double[] activations(double[] triggers) {
		double[] outputs = new double[triggers.length];
		double epos = 0;
		double eneg = 0;
		for (int i = 0; i < triggers.length; i++) {
			epos = Math.exp(triggers[i]);
			eneg = Math.exp(-triggers[i]);
			outputs[i] = (epos - eneg) / (epos + eneg);
		}
		return outputs;
	}

	/**
	 * Apply derivatives.
	 */
	@Override
	public double[] derivatives(double[] outputs) {
		double[] derivatives = new double[outputs.length];
		for (int i = 0; i < outputs.length; i++) {
			derivatives[i] = (1.0 + outputs[i]) * (1 - outputs[i]);
		}
		return derivatives;
	}
}
