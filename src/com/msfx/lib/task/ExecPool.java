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

import com.msfx.lib.util.Numbers;
import com.msfx.lib.util.Strings;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * A pool to execute tasks implemented using a {@link ForkJoinPool}.
 *
 * @author Miquel Sas
 */
public class ExecPool {

	/**
	 * Thread of the pool, named using the root name.
	 */
	private class ThreadTask extends ForkJoinWorkerThread {
		/**
		 * Constructor.
		 */
		protected ThreadTask(ForkJoinPool pool) {
			super(pool);
			int pad = Numbers.getDigits(pool.getParallelism());
			String index = Integer.toString(getPoolIndex());
			setName(name + "-THREAD-" + Strings.leftPad(index, pad, "0"));
		}
	}
	/**
	 * Task thread factory.
	 */
	private class ThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
		@Override
		public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
			return new ThreadTask(pool);
		}
	}
	/**
	 * Uncaught exception handler.
	 */
	private static class ThreadHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread t, Throwable e) {}
	}

	/** Root name of the pool. */
	private final String name;
	/** Fork join pool for execution. */
	private final ForkJoinPool pool;

	/**
	 * Constructor with a default "ROOT" name and "availableProcessors" pool size.
	 */
	public ExecPool() { this(Runtime.getRuntime().availableProcessors()); }
	/**
	 * Constructor assigning the pool size with a default "ROOT" name.
	 * @param poolSize The pool size.
	 */
	public ExecPool(int poolSize) { this("ROOT", poolSize); }
	/**
	 * Constructor the root name for threads and the pool size.
	 * @param name     The root name of the pool.
	 * @param poolSize The pool size.
	 */
	public ExecPool(String name, int poolSize) {
		if (poolSize < 1) {
			throw new IllegalArgumentException("Invalid pool size " + poolSize);
		}
		this.name = name;
		this.pool = new ForkJoinPool(poolSize, new ThreadFactory(), new ThreadHandler(), true);
	}

	/**
	 * Execute the argument collection of tasks until all finished, either by
	 * correctly ending their work or by throwing an exception.
	 * @param tasks The collection of tasks.
	 */
	public void execute(Collection<? extends Task> tasks) { pool.invokeAll(tasks); }
	/**
	 * Execute the argument list of tasks until all finished, either by correctly
	 * ending their work or by throwing an exception.
	 * @param tasks The list of tasks.
	 */
	public void execute(Task... tasks) {
		List<Task> taskList = new ArrayList<>();
		Collections.addAll(taskList, tasks);
		pool.invokeAll(taskList);
	}
	/**
	 * Execute a group of tasks that will cancel on exception.
	 *
	 * @param group The group of tasks.
	 */
	public void execute(TaskGroup<? extends Task> group) { execute(group.tasks()); }

	/**
	 * Submit the collection of tasks for execution as soon as possible.
	 *
	 * @param tasks The collection of tasks.
	 */
	public void submit(Collection<? extends Task> tasks) {
		for (Task task : tasks) { pool.submit((Runnable) task); }
	}
	/**
	 * Submit a group of tasks that will cancel on exception.
	 *
	 * @param group The group of tasks.
	 */
	public void submit(TaskGroup<? extends Task> group) { submit(group.tasks()); }
	/**
	 * Submit the list of tasks for execution as soon as possible.
	 *
	 * @param tasks The list of tasks.
	 */
	public void submit(Task... tasks) {
		for (Task task : tasks) { pool.submit((Runnable) task); }
	}

	/**
	 * Request the pool to shut down.
	 */
	public void shutdown() { pool.shutdown(); }
	/**
	 * Request the pool to shut down canceling already executing tasks.
	 */
	public void shutdownNow() { pool.shutdownNow(); }

	/**
	 * Wait for termination of the collection of tasks submitted.
	 * @param tasks The collection of tasks to wait for their termination.
	 */
	public void waitForTermination(Collection<? extends Task> tasks) {
		for (;;) {
			boolean allTerminated = true;
			for (Task task : tasks) {
				Thread.yield();
				if (!task.hasTerminated()) {
					allTerminated = false;
					break;
				}
			}
			if (allTerminated) break;
		}
	}
	/**
	 * Wait for termination of the group of tasks submitted.
	 * @param group The group of tasks to wait for their termination.
	 */
	public void waitForTermination(TaskGroup<? extends Task> group) {
		waitForTermination(group.tasks());
	}
}
