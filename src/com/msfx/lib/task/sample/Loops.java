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
package com.msfx.lib.task.sample;

import com.msfx.lib.task.Task;

/**
 * Loops manager.
 * 
 * @author Miquel Sas
 */
public class Loops {

	/** Parent task. */
	private final Task task;
	/** Total number of iterations. */
	private final int iterations;
	/** Iteration sleep. */
	private final long sleep;
	/** Throw after iterations. */
	private final int throwAfterIterations;

	/** Current iteration. */
	private int iteration = 0;

	/**
	 * Constructor.
	 * @param iterations           Number of iterations.
	 * @param sleep                Sleep millis.
	 * @param throwAfterIterations Number of iterations after which an exception will be thrown.
	 */
	public Loops(Task task, int iterations, long sleep, int throwAfterIterations) {
		this.task = task;
		this.iterations = iterations;
		this.sleep = sleep;
		if (throwAfterIterations > 0) {
			this.throwAfterIterations = throwAfterIterations;
		} else {
			this.throwAfterIterations = -1;
		}
	}

	/**
	 * Return the current iteration.
	 * @return The current iteration,
	 */
	public int getIteration() { return iteration; }
	/**
	 * Return the total number of iterations.
	 * @return The number of iterations.
	 */
	public int getIterations() { return iterations; }

	/**
	 * Request the next step iteration.
	 * @return A boolean indicating whether the next step has been performed.
	 * @throws Exception If <i><b>throwAfterIterations</b></i> has been set GT zero.
	 */
	public boolean next() throws Exception {
		if (task.cancel()) return false;
		if (iteration++ < iterations) {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException ignore) {
				Thread.currentThread().interrupt();
			}
			if (throwAfterIterations > 0) {
				if (iteration >= throwAfterIterations) {
					throw new Exception("Throw iterations reached: " + throwAfterIterations);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Reset the iterator.
	 */
	public void reset() { iteration = 0; }
}
