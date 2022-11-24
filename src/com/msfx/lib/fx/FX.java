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

import com.msfx.lib.util.Files;
import com.msfx.lib.util.Properties;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;
import javafx.stage.Window;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * Global FX utilities.
 *
 * @author Miquel Sas
 */
public class FX {

	/** Default padding. */
	public static final double PADDING = 5;

	/**
	 * Fill the source list of nodes with nodes starting at the argument node, included.
	 * @param nodes     The list to fill.
	 * @param startNode The starting node.
	 */
	public static void fillNodesFrom(List<Node> nodes, Node startNode) {
		nodes.add(startNode);
		if (startNode instanceof Parent parent) {
			if (parent instanceof TabPane) {
				((TabPane) parent).getTabs().forEach(tab -> fillNodesFrom(nodes, tab.getContent()));
			} else {
				parent.getChildrenUnmodifiable().forEach(child -> fillNodesFrom(nodes, child));
			}
		}
	}
	/**
	 * Return the resource as an ImageView.
	 * @param resource The resource path.
	 * @return The image view.
	 */
	public static ImageView getImageView(String resource) {
		File file = Files.findFileWithinClassPathEntries(resource);
		if (file == null) return null;
		ImageView imageView = null;
		try {
			FileInputStream fi = new FileInputStream(file);
			Image image = new Image(fi);
			imageView = new ImageView(image);
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return imageView;
	}
	/**
	 * Return the properties stored in the node user data.
	 * @param node The node.
	 * @return The properties.
	 */
	public static Properties getProperties(Node node) {
		Object userData = node.getUserData();
		if (userData != null && !(userData instanceof Properties)) {
			throw new IllegalArgumentException("Node has its own user data type");
		}
		Properties properties = (Properties) node.getUserData();
		if (properties == null) {
			properties = new Properties();
			node.setUserData(properties);
		}
		return properties;
	}
	/**
	 * Size the window by screen s and center it.
	 * @param window      The window.
	 * @param widthRatio  Width .
	 * @param heightRatio Height .
	 */
	public static void sizeAndCenter(Window window, double widthRatio, double heightRatio) {
		Rectangle2D rect = Screen.getPrimary().getVisualBounds();
		double width = rect.getWidth() * widthRatio;
		double height = rect.getHeight() * heightRatio;
		double x = (rect.getWidth() - width) / 2;
		double y = (rect.getHeight() - height) / 2;
		window.setWidth(width);
		window.setHeight(height);
		window.setX(x);
		window.setY(y);
	}
}
