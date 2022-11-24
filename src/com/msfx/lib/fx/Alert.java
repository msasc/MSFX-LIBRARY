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

import com.msfx.lib.util.HTML;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Window;

/**
 * Alert functionality.
 *
 * @author Miquel Sas
 */
public class Alert {

	/** Alert types. */
	public enum Type { PLAIN, CONFIRMATION, ERROR, INFORMATION, WARNING }
	/** Alert contents. */
	public enum Content { TEXT, HTML, NODE }

	/** Underlying dialog. */
	private final Dialog dialog;
	/** Header flow text. */
	private TextFlow headerText;
	/** Content type flow text. */
	private TextFlow contentText;
	/** Content type HTML. */
	private WebView contentHTML;

	/** Type. */
	private Type type;
	/** Content. */
	private Content content;

	/**
	 * Default constructor.
	 */
	public Alert() { this(null, 5, 0.5, 0.5); }
	/**
	 * Detail constructor.
	 * @param widthRatio  Width ratio.
	 * @param heightRatio Height ratio.
	 */
	public Alert(double widthRatio, double heightRatio) { this(null, 5, widthRatio, heightRatio); }
	/**
	 * Detail constructor.
	 * @param owner       Owner.
	 * @param padding     Padding.
	 * @param widthRatio  Width ratio.
	 * @param heightRatio Height ratio.
	 */
	public Alert(Window owner, double padding, double widthRatio, double heightRatio) {
		dialog = new Dialog(owner, padding);
		dialog.sizeAndCenter(widthRatio, heightRatio);
	}
	/**
	 * Set up and configure this alert dialog.
	 * @param type    Alert type.
	 * @param content Content type.
	 */
	public void setup(Type type, Content content) {

		this.type = type;
		this.content = content;
		double pad = dialog.getPaneCombo().getPadding();

		/* Set the image and the header if not type PLAIN. */
		if (type != Type.PLAIN) {

			headerText = new TextFlow();
			headerText
					.paddingProperty()
					.set(new Insets(0, pad, pad, pad));
			headerText
					.maxHeightProperty()
					.set(Region.USE_PREF_SIZE);

			ImageView image = null;
			if (type == Type.CONFIRMATION) image = FX.getImageView("dialog-confirm.png");
			if (type == Type.ERROR) image = FX.getImageView("dialog-error.png");
			if (type == Type.INFORMATION) image = FX.getImageView("dialog-information.png");
			if (type == Type.WARNING) image = FX.getImageView("dialog-warning.png");

			Separator hsepTop = new Separator(Orientation.HORIZONTAL);
			Separator hsepBottom = new Separator(Orientation.HORIZONTAL);

			GridPane.setConstraints(hsepTop, 0, 0, 2, 1);
			GridPane.setConstraints(
				headerText, 0, 1, 1, 1,
				HPos.LEFT, VPos.CENTER, Priority.ALWAYS, Priority.NEVER, null);
			GridPane.setConstraints(image, 1, 1, 1, 1);
			GridPane.setConstraints(hsepBottom, 0, 2, 2, 1);

			GridPane header = new GridPane();
			header.getChildren().addAll(hsepTop, headerText, image, hsepBottom);

			dialog
					.getPaneCombo()
					.setTop(header);
		}
		/* Setup top pane when type PLAIN. */
		if (type == Type.PLAIN) {
			Separator hsepBottom = new Separator(Orientation.HORIZONTAL);
			dialog
					.getPaneCombo()
					.setTop(hsepBottom);
		}

		/* Initialize the content for TEXT or HTML. */
		if (content == Content.TEXT) {
			contentText = new TextFlow();
			contentText
					.paddingProperty()
					.set(new Insets(0, pad, pad, pad));
			dialog
					.getPaneCombo()
					.setCenter(contentText);
		}
		if (content == Content.HTML) {
			contentHTML = new WebView();
			dialog
					.getPaneCombo()
					.setCenter(contentHTML);
		}

		/* Set default buttons that can be changed later. */
		if (type == Type.CONFIRMATION) {
			Button ok = Buttons.ok(true, false, true);
			Button cancel = Buttons.cancel(false, true, true);
			setButtons(ok, cancel);
		} else {
			Button ok = Buttons.ok(true, false, true);
			setButtons(ok);
		}
	}

	/**
	 * Set the argument buttons.
	 * @param buttons The list of buttons.
	 */
	public void setButtons(Button... buttons) {
		dialog.getPaneCombo().getButtonBar().getButtons().clear();
		dialog.getPaneCombo().getButtonBar().getButtons().addAll(buttons);
	}

	/**
	 * Add a text to the header if applicable.
	 * @param text The text.
	 */
	public void addHeaderText(String text) { addHeaderText(text, (Font) null); }
	/**
	 * Add a styled text to the header if applicable.
	 * @param text The text.
	 * @param font The font.
	 */
	public void addHeaderText(String text, Font font) { addHeaderText(text, font, null); }
	/**
	 * Add a styled text to the header if applicable.
	 * @param text  The text.
	 * @param style The style.
	 */
	public void addHeaderText(String text, String style) { addHeaderText(text, null, style); }
	/**
	 * Add a styled text to the header if applicable.
	 * @param text  The text.
	 * @param font  The font.
	 * @param style The style.
	 */
	public void addHeaderText(String text, Font font, String style) {
		Text textNode = new Text(text);
		if (font != null) textNode.setFont(font);
		if (style != null) textNode.setStyle(style);
		addHeaderText(textNode);
	}
	/**
	 * Add the argument text component to the header if applicable.
	 * @param text The text component.
	 */
	public void addHeaderText(Text text) {
		if (text == null) return;
		if (type == Type.PLAIN) return;
		headerText.getChildren().add(text);
	}

	/**
	 * Add a text to the content if applicable.
	 * @param text The text.
	 */
	public void addContentText(String text) { addContentText(text, null, null); }
	/**
	 * Add a styled text to the content if applicable.
	 * @param text The text.
	 * @param font The style.
	 */
	public void addContentText(String text, Font font) { addContentText(text, font, null); }
	/**
	 * Add a styled text to the content if applicable.
	 * @param text  The text.
	 * @param style The style.
	 */
	public void addContentText(String text, String style) { addContentText(text, null, style); }
	/**
	 * Add a styled text to the content if applicable.
	 * @param text  The text.
	 * @param font  The font.
	 * @param style The style.
	 */
	public void addContentText(String text, Font font, String style) {
		Text textNode = new Text(text);
		if (font != null) textNode.setFont(font);
		if (style != null) textNode.setStyle(style);
		addContentText(textNode);
	}
	/**
	 * Add the argument text component to the content if applicable.
	 *
	 * @param text The text component.
	 */
	public void addContentText(Text text) {
		if (text == null) return;
		if (content != Content.TEXT) return;
		contentText.getChildren().add(text);
	}

	/**
	 * Set the content to be a WebView control.
	 * @param html The HTML object.
	 */
	public void setContentHTML(HTML html) { setContentHTML(html.toString()); }
	/**
	 * Set the content to be a WebView control.
	 * @param html The HTML string.
	 */
	public void setContentHTML(String html) {
		if (html == null) return;
		if (content != Content.HTML) return;
		contentHTML.getEngine().loadContent(html);
	}

	/**
	 * Set the window title.
	 * @param title The title.
	 */
	public void setTitle(String title) { dialog.getStage().setTitle(title); }

	/**
	 * Show the dialog and return the button used to close it.
	 * @return The button that closed the dialog.
	 */
	public Button show() { return dialog.show(); }
}
