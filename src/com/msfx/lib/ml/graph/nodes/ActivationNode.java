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

import com.msfx.lib.ml.function.Activation;
import com.msfx.lib.ml.graph.Edge;
import com.msfx.lib.ml.graph.Node;
import com.msfx.lib.util.json.JSONObject;

import java.util.UUID;

/**
 * Activation node. Can have any number of input and output edges, but all must have the same size.
 *
 * @author Miquel Sas
 */
public class ActivationNode extends Node {

	/**
	 * Builder to restore from a JSON object with the node definition.
	 * @param obj The JSON object.
	 * @return The node.
	 */
	public static ActivationNode fromJSONObject(JSONObject obj) {
		String uuid = obj.get("uuid").getString();
		String actName = obj.get("activation").getString();
		double flatSpot = obj.get("flat-spot").getNumber().doubleValue();
		ActivationNode node = new ActivationNode(UUID.fromString(uuid));
		node.activation = Activation.get(actName);
		node.flatSpot = flatSpot;
		return node;
	}

	/** Activation function. */
	private Activation activation;
	/** Flat spot to avoid near zero derivatives. */
	private double flatSpot = 0.01;

	/**
	 * Constructor.
	 * @param activation The activation function.
	 */
	public ActivationNode(Activation activation) { this.activation = activation; }
	/**
	 * Constructor to restore.
	 * @param uuid UUID:
	 */
	private ActivationNode(UUID uuid) { super(uuid); }

	/**
	 * Add an input edge.
	 * @param edge The edge.
	 */
	public void addInputEdge(Edge edge) {
		if (isEmpty()) {
			getInputEdges().add(edge);
			return;
		}
		if (edge.size() != size()) throw new IllegalArgumentException("Invalid edge size");
		getInputEdges().add(edge);
	}
	/**
	 * Add an output edge.
	 * @param edge The edge.
	 */
	public void addOutputEdge(Edge edge) {
		if (isEmpty()) {
			getOutputEdges().add(edge);
			return;
		}
		if (edge.size() != size()) throw new IllegalArgumentException("Invalid edge size");
		getOutputEdges().add(edge);
	}

	/**
	 * Request deltas, apply any parameter update, and push deltas to input edges.
	 */
	@Override
	public void backward() {
		int size = size();
		double[] triggerDeltas = new double[size];
		for (Edge edge : getOutputEdges()) {
			double[] outputDeltas = edge.getBackwardDeltas();
			for (int n = 0; n < size; n++) {
				triggerDeltas[n] += outputDeltas[n];
			}			
		}

		// All output edges have the same output values
		double[] outputValues = getOutputEdges().get(0).getForwardValues();
		double[] derivatives = activation.derivatives(outputValues);
		for (int n = 0; n < size; n++) {
			triggerDeltas[n] = triggerDeltas[n] * (derivatives[n] + flatSpot);
		}

		for (Edge edge : getInputEdges()) {
			edge.pushBackward(triggerDeltas);
		}
	}

	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	@Override
	public void forward() {
		int size = size();
		double[] triggerValues = new double[size];
		for (Edge edge : getInputEdges()) {
			double[] inputValues = edge.getForwardValues();
			for (int n = 0; n < size; n++) {
				triggerValues[n] += inputValues[n];
			}
		}
		double[] outputValues = activation.activations(triggerValues);
		for (Edge edge : getOutputEdges()) {
			edge.pushForward(outputValues);
		}
	}

	/**
	 * The node is empty if both input and output edges are empty.
	 * @return A boolean.
	 */
	public boolean isEmpty() { return getInputEdges().isEmpty() && getOutputEdges().isEmpty(); }
	/**
	 * Return the node size, that is, zero if empty, otherwise the size of any of its edges.
	 * @return The size.
	 */
	public int size() {
		if (isEmpty()) return 0;
		else if (!getInputEdges().isEmpty()) return getInputEdges().get(0).size();
		else return getOutputEdges().get(0).size();
	}

	/**
	 * Append the particular node definition.
	 */
	public void toJSONObject(JSONObject def) {
		def.put("activation", activation.getClass().getSimpleName());
		def.put("flat-spot", flatSpot);
	}
}
