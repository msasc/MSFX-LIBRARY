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

package com.msfx.lib.ml.graph.nodes;

import com.msfx.lib.ml.graph.Graph;
import com.msfx.lib.ml.graph.Graph.Range;
import com.msfx.lib.ml.graph.Node;
import com.msfx.lib.util.json.JSONArray;
import com.msfx.lib.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;

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
		node.learningRate = obj.get("learning-rate").getNumber().doubleValue();
		node.learningRateMin = obj.get("learning-rate-min").getNumber().doubleValue();
		node.decayModule = obj.get("decay-module").getNumber().doubleValue();
		node.decayFactor = obj.get("decay-factor").getNumber().doubleValue();
		node.momentum = obj.get("momentum").getNumber().doubleValue();
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

	private class TaskBackward implements Callable<Void> {
		private int inStart, inEnd;
		private TaskBackward(int inStart, int inEnd) { this.inStart = inStart; this.inEnd = inEnd; }
		public Void call() throws Exception { backward(inStart, inEnd); return null; }
	}
	private class TaskForward implements Callable<Void> {
		private int outStart, outEnd;
		private TaskForward(int outStart, int outEnd) { this.outStart = outStart; this.outEnd = outEnd; }
		public Void call() throws Exception { forward(outStart, outEnd); return null; }
	}

	/** Input size. */
	private int inputSize;
	/** Output size. */
	private int outputSize;

	/** Input values read from the unique input edge. */
	private double[] inputValues;
	/** Output values pushed to the output edge. */
	private double[] outputValues;

	/** Output deltas read from the unique output edge. */
	private double[] outputDeltas;
	/** input deltas pushed from the unique input edge. */
	private double[] inputDeltas;

	/** Gradients (in, out). */
	private double[][] gradients;
	/** Weights (in, out). */
	private double[][] weights;

	/** Momentum factor. */
	private double momentum = 0.0;

	/** Starting learning rate. */
	private double learningRate = 0.1;
	/** Minimum learning rate. */
	private double learningRateMin = 0.005;
	/** Learning rate decay module. */
	private double decayModule = 1000;
	/** Learning rate decay factor. */
	private double decayFactor = 0.999;
	/** Counter of calls to backward. */
	private int calls = 0;

	/** List of backward tasks when parallel executing is implemented. */
	private List<Callable<Void>> backwardTasks;
	/** List of forward tasks when parallel executing is implemented. */
	private List<Callable<Void>> forwardTasks;

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
		Random rand = new Random(100000);
		for (int in = 0; in < inputSize; in++) {
			for (int out = 0; out < outputSize; out++) {
				weights[in][out] = rand.nextGaussian();
			}
		}
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

		boolean parallel = getCell().getNetwork().isParallelProcessing();
		if (!parallel) {
			int inStart = 0;
			int inEnd = inputSize - 1;
			backward(inStart, inEnd);
		} else {
			if (backwardTasks == null) {
				List<Range> ranges = getRanges(inputSize);
				backwardTasks = new ArrayList<>();
				for (Range range : ranges) { backwardTasks.add(new TaskBackward(range.start, range.end)); }
			}
			getCell().getNetwork().getPool().invokeAll(backwardTasks);
		}

		getInputEdges().get(0).pushBackward(inputDeltas);

		if (decayModule > 0) {
			calls++;
			if (calls % decayModule == 0) {
				learningRate = Math.max(learningRate * decayFactor, learningRateMin);
			}
		}

	}

	/**
	 * Backward process from start input indexes to end, included.
	 * @param inStart Start input index.
	 * @param inEnd   End input index, included.
	 */
	private void backward(int inStart, int inEnd) {
		for (int in = inStart; in <= inEnd; in++) {
			inputDeltas[in] = 0;
			double inputValue = inputValues[in];
			for (int out = 0; out < outputSize; out++) {

				double weight = weights[in][out];
				double outputDelta = outputDeltas[out];
				double gradientPrev = gradients[in][out];
				double gradientCurr = outputDelta * inputValue;

				double inputDelta = (weight * outputDelta);
				inputDeltas[in] += inputDelta;

				double gradient = (momentum * gradientPrev) + (1 - momentum) * gradientCurr;
				gradients[in][out] = gradient;

				double weightDelta = learningRate * gradient;
				weights[in][out] += weightDelta;
			}
		}
	}

	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	@Override
	public void forward() {

		inputValues = getInputEdges().get(0).getForwardValues();
		outputValues = new double[outputSize];

		boolean parallel = getCell().getNetwork().isParallelProcessing();
		if (!parallel) {
			int outStart = 0;
			int outEnd = outputSize - 1;
			forward(outStart, outEnd);
		} else {
			if (forwardTasks == null) {
				List<Range> ranges = getRanges(outputSize);
				forwardTasks = new ArrayList<>();
				for (Range range : ranges) { forwardTasks.add(new TaskForward(range.start, range.end)); }
			}
			getCell().getNetwork().getPool().invokeAll(forwardTasks);
		}

		getOutputEdges().get(0).pushForward(outputValues);
	}
	/**
	 * Forward process from start output indexes to end, included.
	 * @param outStart Start output index.
	 * @param outEnd   End output index, included.
	 */
	private void forward(int outStart, int outEnd) {
		for (int out = outStart; out <= outEnd; out++) {
			outputValues[out] = 0;
			for (int in = 0; in < inputSize; in++) {
				double input = inputValues[in];
				double weight = weights[in][out];
				outputValues[out] += (input * weight);
			}
		}
	}

	private List<Range> getRanges(int count) {
		int module = Runtime.getRuntime().availableProcessors();
		if (module < count / 4) module = count / 4;
		return Graph.getRanges(count, module);
	}

	/**
	 * Append the particular node definition.
	 */
	public void toJSONObject(JSONObject def) {
		def.put("input-size", inputSize);
		def.put("output-size", outputSize);
		def.put("learning-rate", learningRate);
		def.put("learning-rate-min", learningRateMin);
		def.put("decay-module", decayModule);
		def.put("decay-factor", decayFactor);
		def.put("momentum", momentum);
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
