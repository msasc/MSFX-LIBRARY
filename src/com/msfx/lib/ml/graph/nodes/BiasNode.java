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

import java.util.Arrays;
import java.util.UUID;

/**
 * A bias node without weights adjustments. The backward process does nothing.
 * @author Miquel Sas
 */
public class BiasNode extends Node {
	/**
	 * Builder to restore from a JSON object with the node definition.
	 * @param obj The JSON object.
	 * @return The node.
	 */
	public static BiasNode fromJSONObject(JSONObject obj) {
		String uuid = obj.get("uuid").getString();
		BiasNode node = new BiasNode(UUID.fromString(uuid));
		JSONArray arr = obj.get("output-values").getArray();
		node.outputValues = new double[arr.size()];
		for (int i = 0; i < arr.size(); i++) {
			node.outputValues[i] = arr.get(i).getNumber().doubleValue();
		}
		return node;
	}

	/** Bias output values. */
	private double[] outputValues;

	/**
	 * Constructor.
	 * @param size The bias size.
	 */
	public BiasNode(int size) {
		outputValues = new double[size];
		Arrays.fill(outputValues, 1.0);
	}
	/**
	 * Constructor to restore.
	 * @param uuid UUID:
	 */
	BiasNode(UUID uuid) { super(uuid); }

	/**
	 * Request deltas, apply any parameter update, and push deltas to input edges.
	 */
	@Override
	public void backward() {}

	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	@Override
	public void forward() {
		for (int out = 0; out < getOutputEdges().size(); out++) {
			getOutputEdges().get(out).pushForward(outputValues);
		}
	}

	/**
	 * Append the particular node definition.
	 */
	public void toJSONObject(JSONObject def) {
		JSONArray arr = new JSONArray();
		for (double value : outputValues) { arr.add(value); }
		def.put("output-values", arr);
	}
}
