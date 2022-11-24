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

package com.msfx.lib.ml.graph.nodes;

import com.msfx.lib.ml.graph.Node;
import com.msfx.lib.util.json.JSONArray;
import com.msfx.lib.util.json.JSONObject;

import java.util.Random;
import java.util.UUID;

/**
 * Minimum weights node using stochastic gradient descent back propagation to apply adjustments.
 *
 * @author Miquel Sas
 */
public class WeightsNode extends Node {

	/**
	 * Builder to restore from a JSON object with the node definition.
	 *
	 * @param obj The JSON object.
	 * @return The node.
	 */
	public static WeightsNode fromJSONObject(JSONObject obj) {
		String uuid = obj.get("uuid").getString();
		WeightsNode node = new WeightsNode(UUID.fromString(uuid));
		node.inputSize = obj.get("input-size").getNumber().intValue();
		node.outputSize = obj.get("output-size").getNumber().intValue();
		node.eta = obj.get("eta").getNumber().doubleValue();
		node.alpha = obj.get("alpha").getNumber().doubleValue();
		node.lambda = obj.get("lambda").getNumber().doubleValue();
		node.gradients = new double[node.inputSize][node.outputSize];
		node.weights = new double[node.inputSize][node.outputSize];
		JSONArray arrIn = obj.get("weights").getArray();
		for (int in = 0; in < arrIn.size(); in++) {
			JSONArray arrOut = arrIn.get(in).getArray();
			for (int out = 0; out < arrOut.size(); out++) {
				node.weights[in][out] = arrOut.get(out).getNumber().doubleValue();
			}
		}
		return node;
	}

	/**
	 * Input size.
	 */
	private int inputSize;
	/**
	 * Output size.
	 */
	private int outputSize;

	/**
	 * Input values read from the unique input edge.
	 */
	private double[] inputValues;
	/**
	 * Output values pushed to the output edge.
	 */
	private double[] outputValues;

	/**
	 * Output deltas read from the unique output edge.
	 */
	private double[] outputDeltas;
	/**
	 * input deltas pushed from the unique input edge.
	 */
	private double[] inputDeltas;

	/**
	 * Gradients (in, out).
	 */
	private double[][] gradients;
	/**
	 * Weights (in, out).
	 */
	private double[][] weights;

	/**
	 * Learning rate.
	 */
	private double eta = 0.1;
	/**
	 * Momentum factor.
	 */
	private double alpha = 0.0;
	/**
	 * Weight decay factor, which is also a regularization term.
	 */
	private double lambda = 0.0;

	/**
	 * Constructor.
	 *
	 * @param inputSize  Input size.
	 * @param outputSize Output size.
	 */
	public WeightsNode(int inputSize, int outputSize) {
		this.inputSize = inputSize;
		this.outputSize = outputSize;
		this.gradients = new double[inputSize][outputSize];
		this.weights = new double[inputSize][outputSize];

		/* Randomly initialize weights. */
		Random rand = new Random();
		for (int in = 0; in < inputSize; in++) {
			for (int out = 0; out < outputSize; out++) {
				weights[in][out] = rand.nextGaussian();
			}
		}

		// TODO Check initialize inputDeltas and outputValues and not create an array per pulse.
	}
	/**
	 * Constructor to restore.
	 *
	 * @param uuid UUID:
	 */
	WeightsNode(UUID uuid) { super(uuid); }

	/**
	 * Request deltas, apply any parameter update, and push deltas to input edges.
	 */
	@Override
	public void backward() {
		inputValues = getInputEdges().get(0).getForwardValues();
		outputDeltas = getOutputEdges().get(0).getBackwardDeltas();
		inputDeltas = new double[inputSize];
		for (int in = 0; in < inputSize; in++) {
			inputDeltas[in] = 0;
			double inputValue = inputValues[in];
			for (int out = 0; out < outputSize; out++) {
				double weight = weights[in][out];
				double outputDelta = outputDeltas[out];
				double gradientPrev = gradients[in][out];
				double gradientCurr = (1 - alpha) * eta * outputDelta * inputValue + (alpha * gradientPrev);
				inputDeltas[in] += (weight * outputDelta);
				gradients[in][out] = gradientCurr;
				weights[in][out] += gradientCurr;
				weights[in][out] *= (1.0 - eta * lambda);
			}
		}
		getInputEdges().get(0).pushBackward(inputDeltas);
	}

	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	@Override
	public void forward() {
		inputValues = getInputEdges().get(0).getForwardValues();
		outputValues = new double[outputSize];
		for (int out = 0; out < outputSize; out++) {
			outputValues[out] = 0;
			for (int in = 0; in < inputSize; in++) {
				double input = inputValues[in];
				double weight = weights[in][out];
				outputValues[out] += (input * weight);
			}
		}
		getOutputEdges().get(0).pushForward(outputValues);
	}

	/**
	 * Append the particular node definition.
	 */
	public void toJSONObject(JSONObject def) {
		def.put("input-size", inputSize);
		def.put("output-size", outputSize);
		def.put("eta", eta);
		def.put("alpha", alpha);
		def.put("lambda", lambda);
		JSONArray arrIn = new JSONArray();
		for (int in = 0; in < inputSize; in++) {
			JSONArray arrOut = new JSONArray();
			for (int out = 0; out < outputSize; out++) {
				arrOut.add(weights[in][out]);
			}
			arrIn.add(arrOut);
		}
		def.put("weights", arrIn);
	}
}
