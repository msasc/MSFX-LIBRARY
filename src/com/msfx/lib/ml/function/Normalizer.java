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

package com.msfx.lib.ml.function;

/**
 * Normalizer.
 *
 * @author Miquel Sas
 */
public class Normalizer {

	/**
	 * Data high.
	 */
	private double dataHigh;
	/**
	 * Data low.
	 */
	private double dataLow;
	/**
	 * Normalized high.
	 */
	private double normalizedHigh;
	/**
	 * Normalized low.
	 */
	private double normalizedLow;

	/**
	 * Default constructor.
	 */
	public Normalizer() { }
	/**
	 * Constructor setting the normalized high and low as the data high and low..
	 *
	 * @param dataHigh Data high.
	 * @param dataLow  Data low.
	 */
	public Normalizer(double dataHigh, double dataLow) {
		this.dataHigh = dataHigh;
		this.dataLow = dataLow;
		this.normalizedHigh = dataHigh;
		this.normalizedLow = dataLow;
	}
	/**
	 * Constructor.
	 *
	 * @param dataHigh       Data high.
	 * @param dataLow        Data low.
	 * @param normalizedHigh Normalized high.
	 * @param normalizedLow  Normalized low.
	 */
	public Normalizer(
			double dataHigh,
			double dataLow,
			double normalizedHigh,
			double normalizedLow) {
		this.dataHigh = dataHigh;
		this.dataLow = dataLow;
		this.normalizedHigh = normalizedHigh;
		this.normalizedLow = normalizedLow;
	}
	/**
	 * Return the data high.
	 *
	 * @return The data high.
	 */
	public double getDataHigh() { return dataHigh; }
	/**
	 * Set the data high.
	 *
	 * @param dataHigh The data high.
	 */
	public void setDataHigh(double dataHigh) { this.dataHigh = dataHigh; }
	/**
	 * Return the data low.
	 *
	 * @return The data low.
	 */
	public double getDataLow() { return dataLow; }
	/**
	 * Set the data low.
	 *
	 * @param dataLow The data low.
	 */
	public void setDataLow(double dataLow) { this.dataLow = dataLow; }
	/**
	 * Return the normalized high.
	 *
	 * @return The normalized high.
	 */
	public double getNormalizedHigh() { return normalizedHigh; }
	/**
	 * Set the normalized high.
	 *
	 * @param normalizedHigh The normalized high.
	 */
	public void setNormalizedHigh(double normalizedHigh) { this.normalizedHigh = normalizedHigh; }
	/**
	 * Return the normalized low.
	 *
	 * @return The normalized low.
	 */
	public double getNormalizedLow() { return normalizedLow; }
	/**
	 * Set the normalized low.
	 *
	 * @param normalizedLow The normalized low.
	 */
	public void setNormalizedLow(double normalizedLow) { this.normalizedLow = normalizedLow; }
	/**
	 * Normalize the specified value.
	 *
	 * @param value The value to normalize.
	 * @return The normalized value.
	 */
	public double normalize(double value) {
		if (value > dataHigh) {
			return normalizedHigh;
		} else if (value < dataLow) {
			return normalizedLow;
		} else {
			double normalized = (value - dataLow);
			normalized /= (dataHigh - dataLow);
			normalized *= (normalizedHigh - normalizedLow);
			normalized += normalizedLow;
			return normalized;
		}
	}
	/**
	 * Denormalize the specified value.
	 *
	 * @param value The value to normalize.
	 * @return The normalized value.
	 */
	public double denormalize(final double value) {
		double denormalized = dataLow - dataHigh;
		denormalized *= value;
		denormalized -= (normalizedHigh * dataLow);
		denormalized += (dataHigh * normalizedLow);
		denormalized /= (normalizedLow - normalizedHigh);
		return denormalized;
	}
}
