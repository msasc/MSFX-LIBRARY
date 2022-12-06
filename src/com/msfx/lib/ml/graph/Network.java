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

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ForkJoinPool;

import com.msfx.lib.ml.graph.nodes.ActivationNode;
import com.msfx.lib.ml.graph.nodes.BiasNode;
import com.msfx.lib.ml.graph.nodes.WeightsNode;
import com.msfx.lib.util.json.JSONArray;
import com.msfx.lib.util.json.JSONObject;

/**
 * A network or computational graph, made of wired cells of nodes that interface through input and
 * output edges. Data is forward pushed to the input edges and a call to <i>forward()</i> triggers
 * the calculation processes to finally push the output to the output edges. Inversely, deltas or
 * errors are backward pushed to the output edges and a call to <i>backward()</i> triggers the
 * calculation processes to adjust internal parameters.
 *
 * @author Miquel Sas
 */
public class Network {

	/** Master map with all cells in this network. */
	private final Map<Cell, Cell> cells = new HashMap<>();

	/** List of input edges. */
	private List<Edge> inputEdges;
	/** List of output edges. */
	private List<Edge> outputEdges;
	/** List of layers in forward order. */
	private List<List<Node>> layers;
	/** Map with all edges in the network. */
	private Map<Edge, Edge> edges;

	/** Pool used in concurrent executions. */
	private ForkJoinPool pool;

	/**
	 * Constructor.
	 */
	public Network() {}

	/**
	 * Add a series of cells.
	 * @param cells The list of cells.
	 */
	public void add(Cell... cells) {
		for (Cell cell : cells) {
			cell.network = this;
			this.cells.put(cell, cell);
		}
	}
	/**
	 * Add a series of cells.
	 * @param cells The list of cells.
	 */
	public void add(List<Cell> cells) {
		for (Cell cell : cells) {
			cell.network = this;
			this.cells.put(cell, cell);
		}
	}

	/**
	 * Launch the backward pass.
	 * @param outputDeltasList List of arrays of output deltas, in the same order as the list of
	 *                         output edges.
	 */
	public void backward(List<double[]> outputDeltasList) {

		/* Validate initialized and sizes. */
		checkInitialized();
		checkSizes(outputDeltasList.size(), outputEdges.size());

		/* Push backward output deltas. */
		for (int i = 0; i < outputDeltasList.size(); i++) {
			double[] outputDeltas = outputDeltasList.get(i);
			outputEdges.get(i).pushBackward(outputDeltas);
		}

		/* Push backward layers. */
		for (int i = layers.size() - 1; i >= 0; i--) {
			List<Node> nodes = layers.get(i);
			for (Node node : nodes) {
				node.backward();
			}
		}

		/* Unfold. */
		unfold();
	}

	/**
	 * Launch the forward pass.
	 *
	 * @param inputValuesList List of arrays of input values, in the same order as the list of input
	 *                        edges.
	 */
	public void forward(List<double[]> inputValuesList) {

		/* Validate initialized and sizes. */
		checkInitialized();
		checkSizes(inputValuesList.size(), inputEdges.size());

		/* Push forward input values. */
		for (int i = 0; i < inputValuesList.size(); i++) {
			double[] inputValues = inputValuesList.get(i);
			inputEdges.get(i).pushForward(inputValues);
		}

		/* Push forward layers. */
		for (int i = 0; i < layers.size(); i++) {
			List<Node> nodes = layers.get(i);
			for (Node node : nodes) {
				node.forward();
			}
		}
	}

	/**
	 * Return the list of input edges.
	 * @return The list of input edges.
	 */
	public List<Edge> getInputEdges() { return Collections.unmodifiableList(inputEdges); }
	/**
	 * Return the list of output edges.
	 * @return The list of output edges.
	 */
	public List<Edge> getOutputEdges() { return Collections.unmodifiableList(outputEdges); }
	/**
	 * returns a list with the input sizes.
	 * @return The list with input sizes.
	 */
	public List<Integer> getInputSizes() { return getSizes(getInputEdges()); }
	/**
	 * returns a list with the output sizes.
	 * @return The list with output sizes.
	 */
	public List<Integer> getOutputSizes() { return getSizes(getOutputEdges()); }
	/**
	 * Returns the list of the sizes of the edges.
	 * @param edges The list of edges.
	 * @return The list of the sizes of the edges.
	 */
	private List<Integer> getSizes(List<Edge> edges) {
		List<Integer> sizes = new ArrayList<>();
		for (Edge edge : edges) { sizes.add(edge.size()); }
		return sizes;
	}

	/**
	 * Returns the list of output values of the network, normally required after a forward() call.
	 * @return The list of output values.
	 */
	public List<double[]> getOutputValues() {
		List<double[]> outputValues = new ArrayList<>();
		for (Edge edge : outputEdges) {
			outputValues.add(edge.getForwardValues());
		}
		return outputValues;
	}

	/**
	 * Return a collection with all network nodes.
	 * @return The collection of nodes.
	 */
	public Collection<Node> getNodes() {
		Map<Node, Node> nodes = new HashMap<>();
		for (Cell cell : cells.values()) {
			for (Node node : cell.getNodes()) {
				nodes.put(node, node);
			}
		}
		return nodes.values();
	}

	/**
	 * Initialize the network. This method must be called after adding all the cells and wires, and
	 * before the call to the first <i>forward()</i> or <i>backward()</i>.
	 * <p>
	 * Builds the lists of input and output edges, and the list of layers in a forward order.
	 */
	public void initialize() {

		/* Lists of input and output edges. */
		inputEdges = new ArrayList<>();
		outputEdges = new ArrayList<>();
		for (Cell cell : cells.values()) {
			for (Node node : cell.getNodes()) {
				for (Edge edge : node.getInputEdges()) {
					if (edge.isInput()) inputEdges.add(edge);
				}
				for (Edge edge : node.getOutputEdges()) {
					if (edge.isOutput()) outputEdges.add(edge);
				}
			}
		}

		/* List of layers. */
		layers = new ArrayList<>();

		/* Map of nodes avoid infinite recurrence and working list of edges. */
		Map<Node, Node> processedNodes = new HashMap<>();
		List<Edge> scanEdges = new ArrayList<>();

		/* Start with input edges. */
		scanEdges.addAll(inputEdges);

		/* Build layers with output nodes of the edges, until no more output nodes are found. */
		while (true) {
			List<Node> layer = new ArrayList<>();

			/* Add output nodes to the layer. */
			for (Edge edge : scanEdges) {
				Node node = edge.getOutputNode();
				if (node != null && !processedNodes.containsKey(node)) {
					layer.add(node);
					processedNodes.put(node, node);
				}
			}

			/* If the layer is empty, we are one, if not add it and continue. */
			if (layer.isEmpty()) break;
			layers.add(layer);

			/* Fill edges with output edges of the layer. */
			scanEdges.clear();
			for (Node node : layer) { scanEdges.addAll(node.getOutputEdges()); }
		}

		/* Build the map with all edges. */
		edges = new HashMap<>();
		for (List<Node> nodes : layers) {
			for (Node node : nodes) {
				for (Edge edge : node.getInputEdges()) {
					edges.put(edge, edge);
				}
				for (Edge edge : node.getOutputEdges()) {
					edges.put(edge, edge);
				}
			}
		}
	}

	/**
	 * Terminate the network usage and free resources.
	 */
	public void terminate() {
		if (pool != null) pool.shutdown();
	}

	/**
	 * Indicate that processing should be done in parallel.
	 * @param parallel A boolean.
	 */
	public void setParallelProcessing(boolean parallel) {
		if (pool != null) pool.shutdown();
		if (parallel) {
			pool = new ForkJoinPool(Runtime.getRuntime().availableProcessors() * 2);
		} else {
			pool = null;
		}
	}

	/**
	 * Check whether parallel processing should be done when possible.
	 * @return A boolean.
	 */
	public boolean isParallelProcessing() { return pool != null; }
	/**
	 * Return the parallel pool.
	 * @return The pool.
	 */
	public ForkJoinPool getPool() { return pool; }

	/**
	 * Unfold edges.
	 */
	public void unfold() {
		checkInitialized();
		for (Edge edge : edges.values()) { edge.unfold(); }
	}

	/**
	 * Check that the network has been properly initialized.
	 */
	private void checkInitialized() {
		if (inputEdges == null || outputEdges == null || layers == null || edges == null) {
			throw new IllegalStateException("Network not properly initialized.");
		}
	}
	/**
	 * Check sizes.
	 */
	private void checkSizes(int size1, int size2) {
		if (size1 != size2) {
			throw new IllegalArgumentException("Sizes do not match.");
		}
	}

	/**
	 * Restore the network from a JSONObject.
	 * @param net The object.
	 */
	public void fromJSONObject(JSONObject net) {

		/* Read the cells. */
		JSONArray arr_cells = net.get("cells").getArray();
		for (int i = 0; i < arr_cells.size(); i++) {

			JSONObject cell_obj = arr_cells.get(i).getObject();
			String cell_uuid = cell_obj.get("uuid").getString();
			String cell_name = cell_obj.get("name").getString();

			Cell cell = new Cell(UUID.fromString(cell_uuid), cell_name);
			cell.network = this;

			JSONArray arr_nodes = cell_obj.get("nodes").getArray();
			for (int j = 0; j < arr_nodes.size(); j++) {
				JSONObject node_obj = arr_nodes.get(j).getObject();
				String node_name = node_obj.get("name").getString();
				Node node = null;
				if (node_name.equals(ActivationNode.class.getSimpleName())) {
					node = ActivationNode.fromJSONObject(node_obj);
				}
				if (node_name.equals(BiasNode.class.getSimpleName())) {
					node = BiasNode.fromJSONObject(node_obj);
				}
				if (node_name.equals(WeightsNode.class.getSimpleName())) {
					node = WeightsNode.fromJSONObject(node_obj);
				}
				cell.putNode(node);
			}

			cells.put(cell, cell);
		}

		/* Build a map with all nodes by string UUID. */
		Map<String, Node> nodes = new HashMap<>();
		for (Cell cell : cells.values()) {
			for (Node node : cell.getNodes()) {
				nodes.put(node.getUUID().toString(), node);
			}
		}

		/* Read edges and wire. */
		JSONArray arr_edges = net.get("edges").getArray();
		for (int i = 0; i < arr_edges.size(); i++) {
			JSONObject obj = arr_edges.get(i).getObject();
			String uuid = obj.get("uuid").getString();
			int size = obj.get("size").getNumber().intValue();
			Edge edge = new Edge(size, UUID.fromString(uuid));
			if (obj.contains("input-node")) {
				uuid = obj.get("input-node").getString();
				Node node = nodes.get(uuid);
				if (node != null) {
					edge.setInputNode(node);
					node.getOutputEdges().add(edge);
				}
			}
			if (obj.contains("output-node")) {
				uuid = obj.get("output-node").getString();
				Node node = nodes.get(uuid);
				if (node != null) {
					edge.setOutputNode(node);
					node.getInputEdges().add(edge);
				}
			}
		}

		/* Initialize. */
		initialize();
	}

	/**
	 * Return a JSON definition of the edge.
	 * @return The JSON definition.
	 */
	public JSONObject toJSONObject() {
		JSONObject net = new JSONObject();

		/* Save cells. */
		JSONArray arrCells = new JSONArray();
		for (Cell cell : cells.values()) {
			arrCells.add(cell.toJSONObject());
		}
		net.put("cells", arrCells);

		/* Save edges. */
		JSONArray arrEdges = new JSONArray();
		for (Edge edge : edges.values()) {
			arrEdges.add(edge.toJSONObject());
		}
		net.put("edges", arrEdges);

		return net;
	}

	/**
	 * Restore the network.
	 * @param reader Input reader.
	 * @throws IOException If such an error occurs.
	 */
	public void restore(Reader reader) throws IOException {
		JSONObject net = JSONObject.parse(reader);
		fromJSONObject(net);
	}
	/**
	 * Save the network.
	 * @param writer Output writer.
	 * @throws IOException If such an error occurs.
	 */
	public void save(Writer writer) throws IOException {
		JSONObject net = toJSONObject();
		writer.write(net.toString(true));
	}
}
