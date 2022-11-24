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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A group of tasks, packed together to request cancel all group tasks when one of them issues an
 * exception.
 *
 * @author Miquel Sas
 */
public class TaskGroup<T extends Task> {

	/** List of tasks. */
	private final List<T> tasks = new ArrayList<>();

	/**
	 * Constructor.
	 */
	public TaskGroup() {}
	/**
	 * Add a task to the group.
	 *
	 * @param task The task.
	 */
	public void add(T task) {
		task.group = this;
		tasks.add(task);
	}
	/**
	 * Add the collection of tasks.
	 *
	 * @param tasks The collection of tasks.
	 */
	public void addAll(Collection<T> tasks) { for (T task : tasks) { add(task); } }
	/**
	 * Returns the list of tasks as an unmodifiable collection.
	 *
	 * @return The list of tasks.
	 */
	public Collection<T> tasks() { return Collections.unmodifiableCollection(tasks); }
	/**
	 * Request all the tasks to cancel.
	 */
	public void requestCancel() { tasks.forEach(task -> task.requestCancel()); }
}
