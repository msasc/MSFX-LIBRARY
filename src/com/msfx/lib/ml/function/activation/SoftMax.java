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
import com.msfx.lib.util.Numbers;

/**
 * Soft-max activation.
 *
 * @author Miquel Sas
 */
public class SoftMax extends Activation {

	/**
	 * Constructor.
	 */
	public SoftMax() { }

	/**
	 * Apply activations.
	 */
	@Override
	public double[] activations(double[] triggers) {
		double[] outputs = new double[triggers.length];
		double div = 0;
		for (int i = 0; i < triggers.length; i++) {
			double p = Numbers.bound(Math.exp(triggers[i]));
			outputs[i] = p;
			div += p;
		}
		if (div != 0) {
			for (int i = 0; i < triggers.length; i++) {
				outputs[i] /= div;
			}
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
			derivatives[i] = 1.0;
		}
		return derivatives;
	}
}
