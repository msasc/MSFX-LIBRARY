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

package com.msfx.lib.fx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * An action event handler that chains a list of action event handlers.
 *
 * @author Miquel Sas
 */
public class ActionList implements EventHandler<ActionEvent> {
	
	/** List of action event handlers. */
	private final List<EventHandler<ActionEvent>> handlers = new ArrayList<>();
	
	/**
	 * Constructor.
	 */
	public ActionList() {}
	/**
	 * Return the list of handlers to allow operations on it.
	 * @return The list of handlers.
	 */
	public List<EventHandler<ActionEvent>> handlers() { return handlers; }
	/**
	 * Invoked by buttons, menu items, and action launchers.
	 * @param event The action event.
	 */
	@Override
	public void handle(ActionEvent event) { 
		for (EventHandler<ActionEvent> handler : handlers) {
			if (!event.isConsumed()) handler.handle(event);
		}
	}
}
