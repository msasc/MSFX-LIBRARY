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

package com.msfx.lib.ml.graph;

import com.msfx.lib.util.json.JSONArray;
import com.msfx.lib.util.json.JSONObject;

import java.util.*;

/**
 * Cells of the network computational graph. A cell can be considered a sub-network.
 *
 * @author Miquel Sas
 */
public class Cell {

	/** Name. */
	private String name;
	/** Universal unique id. */
	private final UUID uuid;
	/** Master map with cell nodes. */
	private Map<Node, Node> nodes;
	
	/** Parent network. */
	Network network;

	/**
	 * Constructor.
	 * @param name The cell name.
	 */
	public Cell(String name) {
		this.name = name;
		this.uuid = UUID.randomUUID();
		this.nodes = new HashMap<>();
	}
	/**
	 * Constructor to restore.
	 *
	 * @param uuid UUID.
	 * @param name Name.
	 */
	Cell(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
	}

	/**
	 * Return the network that contains this cell.
	 * @return The network.
	 */
	public Network getNetwork() { return network; }
	
	/**
	 * Put a node in a restore process.
	 * @param node The node.
	 */
	void putNode(Node node) {
		node.cell = this;
		nodes.put(node, node);
	}

	/**
	 * Return the universal unique id.
	 * @return The UUID.
	 */
	public UUID getUUID() { return uuid; }
	/**
	 * Return the name.
	 * @return The name.
	 */
	public String getName() { return name; }

	/**
	 * Returns the list of input edges, edges that are input edges of nodes of the cell, and are
	 * input because they do not have an input node, or their input node is from another cell.
	 * @return The list of input edges.
	 */
	public List<Edge> getInputEdges() {
		List<Edge> edges = new ArrayList<>();
		for (Node node : nodes.values()) {
			for (Edge edge : node.getInputEdges()) {
				if (edge.isInput()) {
					edges.add(edge);
					continue;
				}
				if (!edge.getInputNode().getCell().equals(this)) {
					edges.add(edge);
					continue;
				}
			}
		}
		return edges;
	}
	/**
	 * Returns the list of output edges, edges that are output edges of nodes of the cell, and are
	 * output because they do not have an output node, or their output node is from another cell.
	 * @return The list of output edges.
	 */
	public List<Edge> getOutputEdges() {
		List<Edge> edges = new ArrayList<>();
		for (Node node : nodes.values()) {
			for (Edge edge : node.getOutputEdges()) {
				if (edge.isOutput()) {
					edges.add(edge);
					continue;
				}
				if (!edge.getOutputNode().getCell().equals(this)) {
					edges.add(edge);
					continue;
				}
			}
		}
		return edges;
	}
	/**
	 * Return the collection of nodes in this cell.
	 * @return The nodes.
	 */
	public Collection<Node> getNodes() { return nodes.values(); }

	/**
	 * Check equality.
	 */
	@Override
	public boolean equals(Object o) {
		return (o instanceof Cell c ? uuid.equals(c.uuid) : false);
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
		def.put("name", name);
		JSONArray arrNodes = new JSONArray();
		for (Node node : nodes.values()) {
			arrNodes.add(node.toJSONObject());
		}
		def.put("nodes", arrNodes);
		return def;
	}
}
