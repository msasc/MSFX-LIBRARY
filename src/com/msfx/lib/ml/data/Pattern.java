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

package com.msfx.lib.ml.data;

import java.util.List;

/**
 * A pattern of data.
 *
 * @author Miquel Sas
 */
public abstract class Pattern {
	
	/** Optional label. */
	private String label;

	/**
	 * Constructor.
	 */
	public Pattern() { }
	/**
	 * Return the pattern input values.
	 * @return The pattern input values.
	 */
	public abstract List<double[]> getInputValues();
	/**
	 * Return the optional pattern output values.
	 * @return The pattern output values.
	 */
	public abstract List<double[]> getOutputValues();
	/**
	 * Return the optional label.
	 * @return The label.
	 */
	public String getLabel() { return label; }
	/**
	 * Set the label.
	 * @param label The label.
	 */
	public void setLabel(String label) { this.label = label; }
}
