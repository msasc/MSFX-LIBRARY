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

import com.msfx.lib.util.res.StringRes;
import javafx.scene.control.Button;

/**
 * Button utilities.
 *
 * @author Miquel Sas
 */
public class Buttons {

	/** ID of the default ACCEPT button. */
	public static final String ACCEPT = "button-Accept";
	/** ID of the default APPLY button. */
	public static final String APPLY = "button-Apply";
	/** ID of the default CANCEL button. */
	public static final String CANCEL = "button-Cancel";
	/** ID of the default CLOSE button. */
	public static final String CLOSE = "button-Close";
	/** ID of the default ERROR button. */
	public static final String ERROR = "button-Error";
	/** ID of the default FINISH button. */
	public static final String FINISH = "button-Finish";
	/** ID of the default IGNORE button. */
	public static final String IGNORE = "button-Ignore";
	/** ID of the default NEXT button. */
	public static final String NEXT = "button-Next";
	/** ID of the default NO button. */
	public static final String NO = "button-No";
	/** ID of the default OK button. */
	public static final String OK = "button-Ok";
	/** ID of the default LOAD button. */
	public static final String LOAD = "button-Load";
	/** ID of the default OPEN button. */
	public static final String OPEN = "button-Open";
	/** ID of the default PREVIOUS button. */
	public static final String PREVIOUS = "button-Previous";
	/** ID of the default REMOVE button. */
	public static final String REMOVE = "button-Remove";
	/** ID of the default RETRY button. */
	public static final String RETRY = "button-Retry";
	/** ID of the default SELECT button. */
	public static final String SELECT = "button-Select";
	/** ID of the default START button. */
	public static final String START = "button-Start";
	/** ID of the default YES button. */
	public static final String YES = "button-Yes";

	/**
	 * Accept button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button accept(boolean defButton, boolean cancel, boolean close) {
		return button(ACCEPT, defButton, cancel, close);
	}
	/**
	 * Apply button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button apply(boolean defButton, boolean cancel, boolean close) {
		return button(APPLY, defButton, cancel, close);
	}
	/**
	 * Cancel button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button cancel(boolean defButton, boolean cancel, boolean close) {
		return button(CANCEL, defButton, cancel, close);
	}
	/**
	 * Close button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button close(boolean defButton, boolean cancel, boolean close) {
		return button(CLOSE, defButton, cancel, close);
	}
	/**
	 * Error button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button error(boolean defButton, boolean cancel, boolean close) {
		return button(ERROR, defButton, cancel, close);
	}
	/**
	 * Finish button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button finish(boolean defButton, boolean cancel, boolean close) {
		return button(FINISH, defButton, cancel, close);
	}
	/**
	 * Ignore button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button ignore(boolean defButton, boolean cancel, boolean close) {
		return button(IGNORE, defButton, cancel, close);
	}
	/**
	 * Next button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button next(boolean defButton, boolean cancel, boolean close) {
		return button(NEXT, defButton, cancel, close);
	}
	/**
	 * No button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button no(boolean defButton, boolean cancel, boolean close) {
		return button(NO, defButton, cancel, close);
	}
	/**
	 * Ok button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button ok(boolean defButton, boolean cancel, boolean close) {
		return button(OK, defButton, cancel, close);
	}
	/**
	 * Load button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button load(boolean defButton, boolean cancel, boolean close) {
		return button(LOAD, defButton, cancel, close);
	}
	/**
	 * Open button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button open(boolean defButton, boolean cancel, boolean close) {
		return button(OPEN, defButton, cancel, close);
	}
	/**
	 * Previous button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button previous(boolean defButton, boolean cancel, boolean close) {
		return button(PREVIOUS, defButton, cancel, close);
	}
	/**
	 * Remove button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button remove(boolean defButton, boolean cancel, boolean close) {
		return button(REMOVE, defButton, cancel, close);
	}
	/**
	 * Retry button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button retry(boolean defButton, boolean cancel, boolean close) {
		return button(RETRY, defButton, cancel, close);
	}
	/**
	 * Select button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button select(boolean defButton, boolean cancel, boolean close) {
		return button(SELECT, defButton, cancel, close);
	}
	/**
	 * Start button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button start(boolean defButton, boolean cancel, boolean close) {
		return button(START, defButton, cancel, close);
	}
	/**
	 * Yes button.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 * @return The button.
	 */
	public static Button yes(boolean defButton, boolean cancel, boolean close) {
		return button(YES, defButton, cancel, close);
	}

	/**
	 * Shortcut to create the button using the id to get the key and the default text..
	 * @param id        The button id.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 *                  executing the button initial action if any..
	 * @return
	 */
	private static Button
	button(String id, boolean defButton, boolean cancel, boolean close) {
		String def = id.substring(id.indexOf("-") + 1);
		return button(id, id, def, defButton, cancel, close);
	}
	/**
	 * Setup and return the button.
	 * @param id        The button id.
	 * @param key       Text resource key.
	 * @param def       Default text.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 *                  executing the button initial action if any..
	 * @return The button.
	 */
	public static Button
	button(String id, String key, String def, boolean defButton, boolean cancel, boolean close) {
		String text = StringRes.get(key, def);
		return button(id, text, defButton, cancel, close);
	}
	/**
	 * Setup and return the button.
	 * @param id        The button id.
	 * @param text      The text.
	 * @param defButton A boolean indicating whether it is the default button.
	 * @param cancel    A boolean indicating whether it is the cancel button.
	 * @param close     A boolean to set the close property, used in windows to close it after
	 *                  executing the button initial action if any..
	 * @return The button.
	 */
	public static Button
	button(String id, String text, boolean defButton, boolean cancel, boolean close) {
		Button button = new Button(text);
		button.setId(id);
		button.setDefaultButton(defButton);
		button.setCancelButton(cancel);
		FX.getProperties(button).put("close", close);
		return button;
	}
}
