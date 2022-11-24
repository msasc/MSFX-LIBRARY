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

package com.msfx.lib.ml.function;

import com.msfx.lib.ml.function.activation.*;

/**
 * Activation function.
 *
 * @author Miquel Sas
 */
public abstract class Activation {

	/**
	 * Singleton BipolarSigmoid activation.
	 */
	public static final Activation BIPOLAR_SIGMOID = new BipolarSigmoid();
	/**
	 * Singleton bipolar ReLU.
	 */
	public static final Activation RELU = new ReLU();
	/**
	 * Singleton Sigmoid activation.
	 */
	public static final Activation SIGMOID = new Sigmoid();
	/**
	 * Singleton SoftMax activation.
	 */
	public static final Activation SOFT_MAX = new SoftMax();
	/**
	 * Singleton TANH activation.
	 */
	public static final Activation TANH = new TANH();

	/**
	 * Return the function given the name in a restore operation.
	 *
	 * @param name The activation name.
	 * @return The activation given the name.
	 */
	public static Activation get(String name) {
		if (name.equals(BipolarSigmoid.class.getSimpleName())) return new BipolarSigmoid();
		if (name.equals(ReLU.class.getSimpleName())) return new ReLU();
		if (name.equals(Sigmoid.class.getSimpleName())) return new Sigmoid();
		if (name.equals(SoftMax.class.getSimpleName())) return new SoftMax();
		if (name.equals(TANH.class.getSimpleName())) return new TANH();
		throw new IllegalArgumentException("Invalid ativation name: " + name);
	}

	/**
	 * Default constructor.
	 */
	protected Activation() { }

	/**
	 * Return an identification id of this activation function.
	 *
	 * @return The id.
	 */
	public String getId() { return getClass().getSimpleName(); }

	/**
	 * Calculates the output values of the function given the trigger values.
	 *
	 * @param triggers The trigger (weighted sum plus bias) values.
	 * @return The activation outputs .
	 */
	public abstract double[] activations(double[] triggers);

	/**
	 * Calculates the first derivatives of the function, given the outputs.
	 *
	 * @param outputs The outputs obtained applying the triggers to <i>activations</i>.
	 * @return The derivatives.
	 */
	public abstract double[] derivatives(double[] outputs);
}
