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

import com.msfx.lib.util.Lists;
import com.msfx.lib.util.Properties;
import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the dialog functionality. Uses a {@link PaneCombo} to layout components.
 * <p>
 * Buttons can have any action and, if tagged so, a close action is added to be executed after
 * its own action.
 * <p>
 * Since buttons have access to the scene through the source in the action event, they can interact
 * with any dialog control. Additionally, through their properties stored in the user data, they
 * have access to this dialog with the key "dialog" and its properties to store any information that
 * could be of interest even after closing the dialog.
 *
 * @author Miquel Sas
 */
public class Dialog {

	/** Stage. */
	private final Stage stage;
	/** Pane combo. */
	private final PaneCombo paneCombo;
	/** Close on escape property. */
	private boolean closeOnEscape = true;
	/** Properties. */
	private final Properties properties = new Properties();

	/**
	 * Constructor.
	 */
	public Dialog() { this(null, FX.PADDING); }
	/**
	 * Constructor.
	 * @param owner The owner window.
	 */
	public Dialog(Window owner) { this(owner, 5); }
	/**
	 * Constructor.
	 * @param owner   The owner window.
	 * @param padding Default padding, starting with button bar. Default is 5.
	 */
	public Dialog(Window owner, double padding) {
		paneCombo = new PaneCombo(padding);
		ListChangeListener<? super Node> listener = c -> setupButtons(c);
		paneCombo.getButtonBar().getButtons().addListener(listener);
		stage = new Stage();
		if (owner == null) {
			stage.initModality(Modality.APPLICATION_MODAL);
		} else {
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(owner);
		}
		stage.initStyle(StageStyle.DECORATED);
		stage.setResizable(true);

		Scene scene = new Scene(paneCombo.getRoot());
		stage.setScene(scene);
	}

	/**
	 * Return the pane combo.
	 * @return The combo pane.
	 */
	public PaneCombo getPaneCombo() { return paneCombo; }

	/**
	 * Set up a button that was added to the button bar.
	 * @param c The change interface that indicates what happened.
	 */
	private void setupButtons(ListChangeListener.Change<? extends Node> c) {
		if (c.next() && c.wasAdded()) {
			List<? extends Node> nodes = c.getList();
			int from = c.getFrom();
			int to = c.getTo();
			for (int i = from; i < to; i++) {
				Node node = nodes.get(i);
				if (!(node instanceof Button button)) continue;

				/* New list of actions. */
				ActionList actions = new ActionList();
				EventHandler<ActionEvent> action = button.getOnAction();
				if (action != null) {
					if (action instanceof ActionList currentActions) {
						actions.handlers().addAll(currentActions.handlers());
					} else {
						actions.handlers().add(action);
					}
				}
				if (!(Lists.getLast(actions.handlers()) instanceof ActionClose)) {
					Boolean close = FX.getProperties(button).getBoolean("close");
					if (close != null && close) {
						actions.handlers().add(new ActionClose());
					}
				}
				actions.handlers().add(e -> setResult(e));
				button.setOnAction(actions);

				/* Set this dialog as a button property. */
				FX.getProperties(button).put("dialog", this);
			}
		}

		stage.setOnCloseRequest(e -> {});
	}
	/**
	 * Set the result button.
	 * @param event The action event.
	 */
	private void setResult(ActionEvent event) {
		Object source = event.getSource();
		if (source != null && source instanceof Button) {
			properties.put("result-button", (Button) source);
		}
	}

	/**
	 * Return the dialog general properties.
	 * @return The properties.
	 */
	public Properties getProperties() { return properties; }
	/**
	 * Return the scene.
	 * @return The scene.
	 */
	public Scene getScene() { return stage.getScene(); }
	/**
	 * Return the stage.
	 * @return The stage.
	 */
	public Stage getStage() { return stage; }

	/**
	 * Set the close-on-escape property.
	 * @param close A boolean.
	 */
	public void setCloseOnEscape(boolean close) { this.closeOnEscape = close; }

	/**
	 * Size the window by screen ratios and center it.
	 * @param widthRatio  Width .
	 * @param heightRatio Height .
	 */
	public void sizeAndCenter(double widthRatio, double heightRatio) {
		FX.sizeAndCenter(getStage(), widthRatio, heightRatio);
	}

	/**
	 * Show the dialog and return the button that closed it.
	 * @return The button that closed the dialog.
	 */
	public Button show() {

		/* Set ESC to close the stage. */
		if (closeOnEscape) {
			List<Node> nodes = new ArrayList<>();
			FX.fillNodesFrom(nodes, getStage().getScene().getRoot());
			for (Node node : nodes) {
				node.setOnKeyPressed(e -> {
					if (e.getCode() == KeyCode.ESCAPE) {
						getStage().close();
					}
				});
			}
		}

		/* Do show. */
		getStage().showAndWait();

		/* Return the result button. */
		return (Button) properties.getObject("result-button");
	}
}
