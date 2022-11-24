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
package com.msfx.lib.task;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A task aimed to be executed in a task pool. Implements {@link Callable} to be executed in a
 * {@link ExecPool}, and {@link Runnable} to be run independently or by any thread.
 *
 * @author Miquel Sas
 */
public abstract class Task implements Runnable, Callable<Void> {

	/**
	 * Check the list of tasks and the first non null exception if any.
	 *
	 * @param tasks List of tasks.
	 */
	public static void check(Collection<? extends Task> tasks) throws Throwable {
		for (Task task : tasks) {
			if (task.exception != null) {
				throw task.exception;
			}
		}
	}

	/** The state, READY on creation. */
	private final AtomicReference<State> state = new AtomicReference<>(State.READY);
	/** A boolean that indicates whether the execution should be cancelled. */
	private final AtomicBoolean cancelRequested = new AtomicBoolean(false);
	/** Exception thrown when the execution failed. */
	private Throwable exception = null;
	/** The group the task belongs to. */
	TaskGroup<? extends Task> group = null;

	/**
	 * Constructor.
	 */
	public Task() {}

	/**
	 * Execute the task.
	 *
	 * @throws Throwable If an error occurs.
	 */
	public abstract void execute() throws Throwable;
	/**
	 * Optional, called before any other action when execution starts.
	 */
	protected void beforeExecute() {}
	/**
	 * Optional, called after execution ends.
	 */
	protected void afterExecute() {}

	/**
	 * Launch the execution tracing state.
	 */
	protected void executeTask() {

		/* Before execution. */
		beforeExecute();

		/* Register start time and initialize execution. */
		setState(State.RUNNING);

		/* Execute and register eventual exception. */
		try {
			execute();
		} catch (Throwable exc) {
			exception = exc;
		}

		/* Register end time and set final state. */
		if (exception != null) {
			setState(State.FAILED);
			if (group != null) group.requestCancel();
		} else if (state.get() != State.CANCELLED) {
			setState(State.SUCCEEDED);
		}

		/* After execution. */
		afterExecute();
	}
	/**
	 * Callable implementation.
	 */
	@Override
	public final Void call() { executeTask(); return null; }
	/**
	 * Runnable implementation.
	 */
	@Override
	public final void run() { executeTask(); }

	/**
	 * Returns the state.
	 * @return The state.
	 */
	public State getState() { return state.get(); }
	/**
	 * Sets the state.
	 * @param state The new state.
	 */
	protected void setState(State state) { this.state.set(state); }
	/**
	 * Indicate that the task has been already cancelled. Extenders should call this method when
	 * acquainted of a request of cancel, and seamlessly exit the main loop.
	 */
	protected void setCancelled() { setState(State.CANCELLED); }

	/**
	 * Request the task to try to seamlessly cancel execution.
	 */
	public void requestCancel() { cancelRequested.set(true); }
	/**
	 * Returns a boolean indicating whether cancel execution has been requested, and
	 * thus the task should seamlessly stop processing.
	 *
	 * @return A boolean indicating whether cancel has been requested.
	 */
	public boolean shouldCancel() {
		if (Thread.currentThread().isInterrupted()) return true;
		return cancelRequested.get();
	}

	/**
	 * Check whether the task should be cancelled and, if so, cancel it and return true, otherwise
	 * return false.
	 * @return A boolean indicating that the task has been cancelled if so requested.
	 */
	public boolean cancel() {
		if (shouldCancel()) {
			setCancelled();
			return true;
		}
		return false;
	}

	/**
	 * Returns a boolean that indicates whether the task is ready and waiting to be
	 * executed.
	 * @return A boolean.
	 */
	public boolean isReady() { return state.get() == State.READY; }
	/**
	 * Returns a boolean that indicates whether the task is running.
	 * @return A boolean.
	 */
	public boolean isRunning() { return state.get() == State.RUNNING; }
	/**
	 * Returns a boolean that indicates whether the task has terminated
	 * successfully.
	 * @return A boolean
	 */
	public boolean hasSucceded() { return state.get() == State.SUCCEEDED; }
	/**
	 * Returns a boolean that indicates whether the task was cancelled.
	 * @return A boolean.
	 */
	public boolean wasCancelled() { return state.get() == State.CANCELLED; }
	/**
	 * Returns a boolean that indicates whether the task has failed and thrown an
	 * exception.
	 * @return A boolean.
	 */
	public boolean hasFailed() { return state.get() == State.FAILED; }
	/**
	 * Returns a boolean that indicates whether the task has terminated.
	 *
	 * @return A boolean.
	 */
	public boolean hasTerminated() {
		State s = state.get();
		return (s == State.SUCCEEDED || s == State.CANCELLED || s == State.FAILED);
	}

	/**
	 * Reinitialize the task setting its state to ready.
	 */
	public void reinitialize() {
		exception = null;
		cancelRequested.set(false);
		setState(State.READY);
	}
}
