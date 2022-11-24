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

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * A layout component that combines a {@link BorderPane} with a bottom {@link ButtonBar}, that can
 * be used in dialogs and frames.
 *
 * @author Miquel Sas
 */
public class PaneCombo {

	/** Content border pane. */
	private final BorderPane content;
	/** Button bar. */
	private final ButtonBar buttonBar;
	/** Root border pane. */
	private final BorderPane root;
	/** Padding, available to be used when filling the content. */
	private double padding = FX.PADDING;

	/**
	 * Constructor with default padding to 5.
	 */
	public PaneCombo() { this(5); }
	/**
	 * Constructor.
	 * @param padding Padding.
	 */
	public PaneCombo(double padding) {
		this.padding = padding;

		content = new BorderPane();
		content.setId("content");

		buttonBar = new ButtonBar();
		buttonBar.setId("button-bar");
		buttonBar.setPadding(new Insets(padding));

		VBox vbox = new VBox();
		vbox.getChildren().addAll(new Separator(), buttonBar);

		root = new BorderPane();
		root.setId("root");
		root.setCenter(content);
		root.setBottom(vbox);
	}

	/**
	 * Return the button bar to be able to add or insert buttons.
	 * @return The button bar.
	 */
	public ButtonBar getButtonBar() { return buttonBar; }

	/**
	 * Return the center node.
	 * @return The center node.
	 */
	public Node getCenter() { return content.getCenter(); }
	/**
	 * Return the top node.
	 * @return The top node.
	 */
	public Node getTop() { return content.getTop(); }
	/**
	 * Return the bottom node.
	 * @return The bottom node.
	 */
	public Node getBottom() { return content.getBottom(); }
	/**
	 * Return the left node.
	 * @return The left node.
	 */
	public Node getLeft() { return content.getLeft(); }
	/**
	 * Return the right node.
	 * @return The right node.
	 */
	public Node getRight() { return content.getRight(); }

	/**
	 * Set the center node.
	 * @param node A node.
	 */
	public void setCenter(Node node) { content.setCenter(node); }
	/**
	 * Set the top node.
	 * @param node A node.
	 */
	public void setTop(Node node) { content.setTop(node); }
	/**
	 * Set the bottom node.
	 * @param node A node.
	 */
	public void setBottom(Node node) { content.setBottom(node); }
	/**
	 * Set the left node.
	 * @param node A node.
	 */
	public void setLeft(Node node) { content.setLeft(node); }
	/**
	 * Set the right node.
	 * @param node A node.
	 */
	public void setRight(Node node) { content.setRight(node); }

	/**
	 * Return the default padding to be used when building the content if so required.
	 * @return The default padding.
	 */
	public double getPadding() { return padding; }

	/**
	 * Return the root node.
	 * @return The root node
	 */
	public Parent getRoot() { return root; }
}
