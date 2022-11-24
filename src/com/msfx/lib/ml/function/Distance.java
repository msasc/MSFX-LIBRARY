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
 * Distance function (between two vectors).
 *
 * @author Miquel Sas
 */
public interface Distance {
	/**
	 * Returns the distance between the two vectors.
	 *
	 * @param x Vector x.
	 * @param y Vector y.
	 * @return The distance.
	 */
	double distance(double[] x, double[] y);
}
