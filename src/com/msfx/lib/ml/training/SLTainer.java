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
package com.msfx.lib.ml.training;

import java.util.ArrayList;
import java.util.List;

import com.msfx.lib.ml.data.Pattern;
import com.msfx.lib.ml.data.PatternSource;
import com.msfx.lib.ml.graph.Network;
import com.msfx.lib.task.TaskProgress;
import com.msfx.lib.util.Vector;

/**
 * Supervised Learning trainer.
 * 
 * @author Miquel Sas
 */
public class SLTainer extends TaskProgress {

	/** Notification level epoch. */
	private static final int LEVEL_EPOCH = 0;
	/** Notification level pattern. */
	private static final int LEVEL_PATTERN = 1;

	/** Pattern source training. */
	private PatternSource sourceTrain;
	/** Optional pattern source test. */
	private PatternSource sourceTest;
	/** The network to train. */
	private Network network;

	/** Number of epochs or iterations on the train source, default to 100. */
	private int epochs = 100;

	/**
	 * Constructor setting two levels of progress.
	 */
	public SLTainer() { super(2); }

	/**
	 * Execute this trainer task.
	 */
	@Override
	public void execute() throws Throwable {

		/* Validate. */
		if (network == null) throw new IllegalStateException("Null network");
		if (sourceTrain == null) throw new IllegalStateException("Null training source");

		/* Start monitor. */
		getMonitor().notifyStart(LEVEL_EPOCH);

		/* Initialize the network. */
		network.initialize();

		/* Total work and work done. */
		long totalWork = sourceTrain.size() * epochs;
		long totalDone = 0;

		/* Iterate epochs. */
		for (int epoch = 0; epoch < epochs; epoch++) {

			/* Check cancelled. */
			if (cancel()) break;

			/* Start the source level. */
			getMonitor().notifyStart(LEVEL_PATTERN);
			long epochWork = sourceTrain.size();
			long epochDone = 0;
			sourceTrain.reset();
			while (sourceTrain.hasNext()) {

				/* Check cancelled. */
				if (cancel()) break;

				/* Notify. */
				totalDone++;
				epochDone++;
				getMonitor().notifyMessage(LEVEL_EPOCH, "Processing epoch " + (epoch + 1) + ", pattern " + totalDone);
				getMonitor().notifyMessage(LEVEL_PATTERN, "Processing pattern " + epochDone);
				getMonitor().notifyProgress(LEVEL_EPOCH, totalDone, totalWork);
				getMonitor().notifyProgress(LEVEL_PATTERN, epochDone, epochWork);

				/* Read pattern and process it. */
				Pattern pattern = sourceTrain.next();
				List<double[]> patternInput = pattern.getInputValues();
				List<double[]> patternOutput = pattern.getOutputValues();
				network.forward(patternInput);
				List<double[]> networkOutput = network.getOutputValues();
				List<double[]> networkDeltas = new ArrayList<>();
				for (int i = 0; i < networkOutput.size(); i++) {
					double[] p_output = patternOutput.get(i);
					double[] n_output = networkOutput.get(i);
					double[] n_deltas = Vector.subtract(p_output, n_output);
					networkDeltas.add(n_deltas);
				}
				network.backward(networkDeltas);
			}

			/* End monitor of pattern. */
			getMonitor().notifyEnd(LEVEL_PATTERN);
		}

		/* End monitor of pattern. */
		getMonitor().notifyEnd(LEVEL_EPOCH);
	}
}
