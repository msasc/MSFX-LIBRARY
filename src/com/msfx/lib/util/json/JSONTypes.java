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

package com.msfx.lib.util.json;

/**
 * JSON types.
 *
 * @author Miquel Sas
 */
public enum JSONTypes {

	/**
	 * Standard JSON object type.
	 */
	OBJECT(null),
	/**
	 * Standard JSON array type.
	 */
	ARRAY(null),
	/**
	 * Standard string type.
	 */
	STRING(null),
	/**
	 * Standard number type.
	 */
	NUMBER(null),
	/**
	 * Standard boolean type.
	 */
	BOOLEAN(null),
	/**
	 * Standard NULL type.
	 */
	NULL(null),

	/**
	 * Extended binary type.
	 */
	BINARY("%bin%"),
	/**
	 * Extended binary type.
	 */
	DATE("%dt%"),
	/**
	 * Extended binary type.
	 */
	TIME("%tm%"),
	/**
	 * Extended binary type.
	 */
	TIMESTAMP("%ts%");

	/**
	 * Extended types.
	 */
	public static final JSONTypes[] EXTENDED_TYPES = new JSONTypes[] {
			BINARY, DATE, TIME, TIMESTAMP
	};

	/**
	 * Key to store extended types.
	 */
	public final String key;
	/**
	 * Constructor assigning the key for extended types.
	 *
	 * @param key The key for extended types.
	 */
	JSONTypes(String key) {
		this.key = key;
	}
}
