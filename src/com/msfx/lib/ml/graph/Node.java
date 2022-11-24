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

package com.msfx.lib.ml.graph;

import com.msfx.lib.util.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A node of a neural network computational graph.
 * <p>
 * In the forward pass, a node reads data, normally values, from the input edges, processes it, and
 * pushes the resulting data to the output edges.
 * <p>
 * In the backward pass, a node may read data, normally deltas to adjust parameters, from the output
 * edges, does any internal necessary operation, and optionally pushes backward data to the input
 * edges.
 *
 * @author Miquel Sas
 */
public abstract class Node {

	/**
	 * Universal unique id.
	 */
	private final UUID uuid;

	/**
	 * The cell to which the node belongs.
	 */
	private Cell cell;

	/**
	 * List of input edges.
	 */
	private final List<Edge> inputEdges = new ArrayList<>();
	/**
	 * List of output edges.
	 */
	private final List<Edge> outputEdges = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public Node() { uuid = UUID.randomUUID(); }
	/**
	 * Constructor to restore.
	 *
	 * @param uuid UUID:
	 */
	protected Node(UUID uuid) { this.uuid = uuid; }

	/**
	 * Return the universal unique id.
	 *
	 * @return The UUID.
	 */
	public UUID getUUID() { return uuid; }

	/**
	 * Request deltas, apply any parameter update, and push deltas to input edges.
	 */
	public abstract void backward();

	/**
	 * Request values from input edges, apply node calculations and push values to output edges.
	 */
	public abstract void forward();

	/**
	 * Return the list of input edges.
	 *
	 * @return the list of edges.
	 */
	public List<Edge> getInputEdges() { return inputEdges; }
	/**
	 * Return the list of output edges.
	 *
	 * @return The list of edges.
	 */
	public List<Edge> getOutputEdges() { return outputEdges; }

	/**
	 * Return the cell to which the node belongs.
	 *
	 * @return The cell.
	 */
	public Cell getCell() { return cell; }
	/**
	 * Set the cell to which the node should belong.
	 *
	 * @param cell The cell.
	 */
	void setCell(Cell cell) { this.cell = cell; }

	/**
	 * Check equality.
	 */
	@Override
	public boolean equals(Object o) {
		return (o instanceof Node n ? uuid.equals(n.uuid) : false);
	}
	/**
	 * Return a suitable hash code.
	 */
	@Override
	public int hashCode() { return uuid.hashCode(); }

	/**
	 * Return a JSON definition of the edge.
	 * @return The JSON definition.
	 */
	public JSONObject toJSONObject() {
		JSONObject def = new JSONObject();
		def.put("uuid", getUUID().toString());
		def.put("name", getClass().getSimpleName());
		toJSONObject(def);
		return def;
	}
	/**
	 * Append the particular node definition.
	 * @param def The JSONObject definition.
	 */
	public abstract void toJSONObject(JSONObject def);
}
