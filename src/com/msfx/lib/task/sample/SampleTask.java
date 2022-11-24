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
package com.msfx.lib.task.sample;

import com.msfx.lib.task.TaskProgress;

/**
 * @author Miquel Sas
 */
public class SampleTask extends TaskProgress {
	
	private Loops loops;

	public SampleTask(int iterations, long sleep, int throwAfterIterations) {
		loops = new Loops(this, iterations, sleep, throwAfterIterations);
	}

	@Override
	public void execute() throws Throwable {
		
		/* Reset and start. */
		loops.reset();
		int totalWork = loops.getIterations();
		int delta = 1;
		getMonitor().notifyStart();
		
		/* Iterate. */
		while (loops.next()) {
			if (cancel()) break;
			getMonitor().notifyMessage("Performing iteration " + loops.getIteration());
			getMonitor().notifyProgress(delta, totalWork);
		}
	}
}
