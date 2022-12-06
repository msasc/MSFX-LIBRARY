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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.msfx.lib.ml.data.Pattern;
import com.msfx.lib.ml.data.PatternSource;
import com.msfx.lib.ml.graph.Network;
import com.msfx.lib.task.TaskProgress;
import com.msfx.lib.util.Console;
import com.msfx.lib.util.Numbers;
import com.msfx.lib.util.Strings;
import com.msfx.lib.util.Vector;

/**
 * Supervised Learning trainer.
 * 
 * @author Miquel Sas
 */
public class SLTrainer extends TaskProgress {

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

	/** Optional console to output additional information. */
	private Console console;

	/**
	 * Constructor setting two levels of progress.
	 */
	public SLTrainer() { super(2); }

	/**
	 * Set the number of epochs to train.
	 * @param epochs The number of epochs.
	 */
	public void setEpochs(int epochs) { this.epochs = epochs; }
	/**
	 * Set the network.
	 * @param network The network.
	 */
	public void setNetwork(Network network) { this.network = network; }
	/**
	 * Set the test source.
	 * @param sourceTest The test source of patterns.
	 */
	public void setSourceTest(PatternSource sourceTest) { this.sourceTest = sourceTest; }
	/**
	 * Set the train source.
	 * @param sourceTrain The train source of patterns.
	 */
	public void setSourceTrain(PatternSource sourceTrain) { this.sourceTrain = sourceTrain; }
	/**
	 * Set the optional console.
	 * @param cs The console.
	 */
	public void setConsole(Console cs) { this.console = cs; }

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

		/* Metrics. */
		SLMetrics trainMetrics = new SLMetrics(network.getOutputSizes());
		SLMetrics.Track trackPrev = null;

		/* Iterate epochs. */
		boolean consoleHeader = false;
		if (console != null) console.clear();
		for (int epoch = 0; epoch < epochs; epoch++) {

			/* Start the source level. */
			getMonitor().notifyStart(LEVEL_PATTERN);
			long patternWork = sourceTrain.size();
			long patternDone = 0;
			sourceTrain.reset();
			trainMetrics.reset();
			while (sourceTrain.hasNext()) {

				/* Check cancelled. */
				if (cancel()) break;

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

				/* Calculate train metrics. */
				trainMetrics.compute(patternOutput, networkOutput);

				/* Notify. */
				totalDone++;
				patternDone++;

				StringBuilder epochMsg = new StringBuilder();
				epochMsg.append("Processing epoch ");
				epochMsg.append((epoch + 1));
				epochMsg.append(" of ");
				epochMsg.append(epochs);
				epochMsg.append(", pattern ");
				epochMsg.append(totalDone);
				epochMsg.append(" of ");
				epochMsg.append(totalWork);
				getMonitor().notifyMessage(LEVEL_EPOCH, epochMsg.toString());

				StringBuilder patternMsg = new StringBuilder();
				patternMsg.append("Processing pattern ");
				patternMsg.append(patternDone);
				patternMsg.append(" of ");
				patternMsg.append(patternWork);
				getMonitor().notifyMessage(LEVEL_PATTERN, patternMsg.toString());

				getMonitor().notifyProgress(LEVEL_EPOCH, 1, totalWork);
				getMonitor().notifyProgress(LEVEL_PATTERN, 1, patternWork);

			}

			/* Check cancelled. */
			if (cancel()) break;

			/* If there is a console, report performance. */
			if (console != null) {

				SLMetrics.Track track = trainMetrics.getTrack();
				int matches = track.matches();
				int calls = track.calls();
				BigDecimal perf = Numbers.getBigDecimal(100 * track.performance(), 4);
				BigDecimal errorAvg = Numbers.getBigDecimal(track.errorAvg(), 8);

				String sep = "  ";
				int padEpoch = Math.max(Integer.toString(epochs).length(), "Epoch".length());
				int padMatches = Math.max(Integer.toString(calls).length(), "Matches".length());
				int padCalls = Math.max(Integer.toString(calls).length(), "Calls".length());
				int padPerf = Math.max(perf.toPlainString().length(), "Perform".length());
				int padError = Math.max(errorAvg.toPlainString().length(), "Error-Avg".length());
				int padPerfDif = Math.max(perf.toPlainString().length() + 1, "Perform-Dif".length());
				int padErrorDif = Math.max(errorAvg.toPlainString().length() + 1, "Error-Dif".length());
				if (!consoleHeader) {
					consoleHeader = true;
					console.print(Strings.leftPad("Epoch", padEpoch));
					console.print(sep);
					console.print(Strings.leftPad("Matches", padMatches));
					console.print(sep);
					console.print(Strings.leftPad("Calls", padCalls));
					console.print(sep);
					console.print(Strings.leftPad("Perform", padPerf));
					console.print(sep);
					console.print(Strings.leftPad("Error-Avg", padError));
					console.print(sep);
					console.print(Strings.leftPad("Perform-Dif", padPerfDif));
					console.print(sep);
					console.print(Strings.leftPad("Error-Dif", padErrorDif));
					console.println();
				}
				console.print(Strings.leftPad(epoch + 1, padEpoch));
				console.print(sep);
				console.print(Strings.leftPad(matches, padMatches));
				console.print(sep);
				console.print(Strings.leftPad(calls, padCalls));
				console.print(sep);
				console.print(Strings.leftPad(perf.toPlainString(), padPerf));
				console.print(sep);
				console.print(Strings.leftPad(errorAvg.toPlainString(), padError));
				if (trackPrev != null) {
					BigDecimal perfPrev = Numbers.getBigDecimal(100 * trackPrev.performance(), 4);
					BigDecimal perfDif = perf.subtract(perfPrev);
					BigDecimal errorPrev = Numbers.getBigDecimal(trackPrev.errorAvg(), 8);
					BigDecimal errorDif = errorAvg.subtract(errorPrev);
					console.print(sep);
					console.print(Strings.leftPad(perfDif.toPlainString(), padPerfDif));
					console.print(sep);
					console.print(Strings.leftPad(errorDif.toPlainString(), padErrorDif));
				}
				console.println();
				
				
				trackPrev = track;
			}

			/* End monitor of pattern. */
			getMonitor().notifyEnd(LEVEL_PATTERN);
		}

		/* End monitor of pattern. */
		getMonitor().notifyEnd(LEVEL_EPOCH);
	}
}
