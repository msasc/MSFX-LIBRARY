/*
 * Copyright (c) 2022 Miquel Sas.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import javafx.scene.Node;
import javafx.stage.Window;

/**
 * Action to close the parent stage if any, related to the action event source.
 *
 * @author Miquel Sas
 */
public class ActionClose implements EventHandler<ActionEvent> {
	/**
	 * Constructor.
	 */
	public ActionClose() { }
	/**
	 * Handle the event.
	 * @param event The action event.
	 */
	@Override
	public void handle(ActionEvent event) {
		Object source = (event == null ? null : event.getSource());
		if (source != null && source instanceof Node node) {
			Window window = node.getScene().getWindow();
			if (window != null) window.hide();
		}
	}
}
