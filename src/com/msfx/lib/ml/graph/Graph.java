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
/**
 * 
 */
package com.msfx.lib.ml.graph;

import java.util.ArrayList;
import java.util.List;

import com.msfx.lib.ml.function.Activation;
import com.msfx.lib.ml.graph.nodes.ActivationNode;
import com.msfx.lib.ml.graph.nodes.BiasNode;
import com.msfx.lib.ml.graph.nodes.WeightsNode;

/**
 * Graph utility functions.
 * 
 * @author Miquel Sas
 */
public class Graph {

	/**
	 * Range structure.
	 */
	public static class Range {
		public int start;
		public int end;
		public Range(int start, int end) { this.start = start; this.end = end; }
	}

	/**
	 * Returns the list of ranges to cover a certain number of indexes using the argument module.
	 * @param count  The number of indexes.
	 * @param module The module to fraction count (available processors)
	 * @return The list of ranges.
	 */
	public static List<Range> getRanges(int count, int module) {
		List<Range> ranges = new ArrayList<>();
		int indexes = (count > module ? Math.max(count / module, 1) : 1);
		int start = 0;
		while (true) {
			int end = start + indexes - 1;
			if (end >= count) {
				end = count - 1;
			}
			ranges.add(new Range(start, end));
			if (end == count - 1) {
				break;
			}
			start = end + 1;
		}
		return ranges;
	}

	/**
	 * Connect two nodes of a network with an edge of the given size.
	 * @param size       The size.
	 * @param inputNode  Input node.
	 * @param outputNode Output node.
	 */
	public static void connect(int size, Node inputNode, Node outputNode) {
		Edge edge = new Edge(size);
		if (inputNode != null) {
			edge.setInputNode(inputNode);
			inputNode.getOutputEdges().add(edge);
		}
		if (outputNode != null) {
			edge.setOutputNode(outputNode);
			outputNode.getInputEdges().add(edge);
		}
	}
	/**
	 * Connect two cells.
	 * @param cellIn  Input cell.
	 * @param cellOut Output cell.
	 */
	public static void connect(Cell cellIn, Cell cellOut) {

		/* Output edges of the input cell and input edges of the output cell. */
		List<Edge> edgesOut = cellIn.getOutputEdges();
		List<Edge> edgesIn = cellOut.getInputEdges();

		/* Validate number of edges. */
		if (cellIn.getOutputEdges().size() != cellOut.getInputEdges().size()) {
			throw new IllegalArgumentException("Invalid in-out number of edges");
		}

		int numEdges = edgesOut.size();

		/* Validate sizes of edges. */
		for (int i = 0; i < numEdges; i++) {
			Edge edgeOut = edgesOut.get(i);
			Edge edgeIn = edgesIn.get(i);
			if (edgeOut.size() != edgeIn.size()) {
				throw new IllegalArgumentException("Invalid edge sizes");
			}
		}

		/* Do connect. */
		for (int i = 0; i < numEdges; i++) {

			/* Edges out and in. */
			Edge edgeOut = cellIn.getOutputEdges().get(i);
			Edge edgeIn = cellOut.getInputEdges().get(i);

			/* Connection edge. */
			Node nodeOut = edgeOut.getInputNode();
			Node nodeIn = edgeIn.getOutputNode();
			Edge edge = new Edge(edgeOut.size());
			edge.setInputNode(nodeOut);
			edge.setOutputNode(nodeIn);

			/* Replace edges out and in by the connect edge. */
			for (int j = 0; j < nodeOut.getOutputEdges().size(); j++) {
				if (nodeOut.getOutputEdges().get(j).equals(edgeOut)) {
					nodeOut.getOutputEdges().set(j, edge);
					break;
				}
			}
			for (int j = 0; j < nodeIn.getInputEdges().size(); j++) {
				if (nodeIn.getInputEdges().get(j).equals(edgeIn)) {
					nodeIn.getInputEdges().set(j, edge);
					break;
				}
			}
		}
	}

	/**
	 * Creates a generic RNN cell definition that can range from a simple BP cell without bias, up
	 * to a RNN cell with bias.
	 * 
	 * @param inputSize  Input size.
	 * @param outputSize Output size.
	 * @param activation Activation function.
	 * @param recurrent  A boolean that indicates whether the cell is recurrent.
	 * @param bias       A boolean that indicates whether the activation node will use biases.
	 */
	public static Cell rnn(int inputSize, int outputSize, Activation activation, boolean recurrent, boolean bias) {

		StringBuilder name = new StringBuilder();
		name.append("RNN-");
		name.append(inputSize);
		name.append("-");
		name.append(outputSize);
		name.append("-");
		name.append(activation.getId());
		if (recurrent || bias) {
			name.append("-");
			if (recurrent) name.append("REC");
			if (bias) name.append("BIAS");
		}

		Cell cell = new Cell(name.toString());

		/* Normal weights node. Connect an input edge. */
		WeightsNode weightsNode = new WeightsNode(inputSize, outputSize);
		connect(inputSize, null, weightsNode);
		cell.putNode(weightsNode);

		/* Activation node. */
		ActivationNode actNode = new ActivationNode(activation);
		cell.putNode(actNode);

		/* Connect normal weights node to activation node. */
		connect(outputSize, weightsNode, actNode);

		/* Case bias node required. */
		if (bias) {
			BiasNode biasNode = new BiasNode(outputSize);
			connect(outputSize, biasNode, actNode);
			cell.putNode(biasNode);
		}

		/* Case recurrent weights node required. */
		if (recurrent) {
			WeightsNode recwNode = new WeightsNode(outputSize, outputSize);
			connect(outputSize, actNode, recwNode);
			connect(outputSize, recwNode, actNode);
			cell.putNode(recwNode);
		}

		/* Connect the output edge to the activation node. */
		connect(outputSize, actNode, null);

		return cell;
	}

}
