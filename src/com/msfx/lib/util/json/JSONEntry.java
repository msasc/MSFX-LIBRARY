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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.msfx.lib.util.json.JSONTypes.OBJECT;
import static com.msfx.lib.util.json.JSONTypes.ARRAY;
import static com.msfx.lib.util.json.JSONTypes.STRING;
import static com.msfx.lib.util.json.JSONTypes.NUMBER;
import static com.msfx.lib.util.json.JSONTypes.BOOLEAN;
import static com.msfx.lib.util.json.JSONTypes.NULL;
import static com.msfx.lib.util.json.JSONTypes.BINARY;
import static com.msfx.lib.util.json.JSONTypes.DATE;
import static com.msfx.lib.util.json.JSONTypes.TIME;
import static com.msfx.lib.util.json.JSONTypes.TIMESTAMP;

/**
 * An entry value of the JSONObject internal map, or an item of a JSONArray.
 *
 * @author Miquel Sas
 */
public class JSONEntry {

	/**
	 * Type.
	 */
	final JSONTypes type;
	/**
	 * Value.
	 */
	final Object value;

	/**
	 * Constructor of an entry of type NULL.
	 */
	public JSONEntry() {
		type = NULL;
		value = null;
	}
	/**
	 * Constructor assigning a non-null JSONObject.
	 *
	 * @param value A {@link JSONObject}
	 */
	public JSONEntry(JSONObject value) {
		if (value == null) throw new NullPointerException();
		this.type = OBJECT;
		this.value = value;
	}
	/**
	 * Constructor assigning a non-null JSONArray.
	 *
	 * @param value A {@link JSONArray}.
	 */
	public JSONEntry(JSONArray value) {
		if (value == null) throw new NullPointerException();
		this.type = ARRAY;
		this.value = value;
	}
	/**
	 * Constructor assigning a non-null string.
	 *
	 * @param value A {@link String}
	 */
	public JSONEntry(String value) {
		if (value == null) throw new NullPointerException();
		this.type = STRING;
		this.value = value;
	}
	/**
	 * Constructor assigning a non-null number.
	 *
	 * @param value A {@link Number}
	 */
	public JSONEntry(Number value) {
		if (value == null) throw new NullPointerException();
		this.type = NUMBER;
		if (value instanceof BigDecimal) this.value = value;
		else this.value = new BigDecimal(value.toString());
	}
	/**
	 * Constructor assigning a non-null number.
	 *
	 * @param value A {@link Number}
	 * @param scale The scale or number of decimals.
	 */
	public JSONEntry(Number value, int scale) {
		if (value == null) throw new NullPointerException();
		this.type = NUMBER;
		this.value = new BigDecimal(value.toString()).setScale(scale, RoundingMode.HALF_UP);
	}
	/**
	 * Constructor assigning a boolean.
	 *
	 * @param value A boolean.
	 */
	public JSONEntry(boolean value) {
		this.type = BOOLEAN;
		this.value = value;
	}
	/**
	 * Constructor assigning a non-null byte array.
	 *
	 * @param value A byte array.
	 */
	public JSONEntry(byte[] value) {
		this.type = BINARY;
		this.value = value;
	}
	/**
	 * Constructor assigning a local date.
	 *
	 * @param value A {@link LocalDate}.
	 */
	public JSONEntry(LocalDate value) {
		this.type = DATE;
		this.value = value;
	}
	/**
	 * Constructor assigning a local time.
	 *
	 * @param value A {@link LocalTime}.
	 */
	public JSONEntry(LocalTime value) {
		this.type = TIME;
		this.value = value;
	}
	/**
	 * Constructor assigning a local date-time.
	 *
	 * @param value A {@link LocalDateTime}.
	 */
	public JSONEntry(LocalDateTime value) {
		this.type = TIME;
		this.value = value;
	}

	/**
	 * Internal constructor.
	 *
	 * @param type  Type.
	 * @param value Value.
	 */
	JSONEntry(JSONTypes type, Object value) {

		/* Validate that type is not null. */
		if (type == null) throw new NullPointerException();

		/* Validate the value when it is not null. */
		if (value != null) {
			boolean valid = false;
			/* Standard types. */
			valid |= (type == JSONTypes.OBJECT && value instanceof JSONObject);
			valid |= (type == JSONTypes.ARRAY && value instanceof JSONArray);
			valid |= (type == JSONTypes.STRING && value instanceof String);
			valid |= (type == JSONTypes.NUMBER && value instanceof Number);
			valid |= (type == JSONTypes.NUMBER && value instanceof String);
			valid |= (type == JSONTypes.BOOLEAN && value instanceof Boolean);
			/* Extended types strict. */
			valid |= (type == JSONTypes.BINARY && value instanceof byte[]);
			valid |= (type == JSONTypes.DATE && value instanceof LocalDate);
			valid |= (type == JSONTypes.TIME && value instanceof LocalTime);
			valid |= (type == JSONTypes.TIMESTAMP && value instanceof LocalDateTime);
			if (!valid) {
				throw new IllegalArgumentException(
						"Invalid type " + type + " for " + value.getClass());
			}
		}

		/* Null value. Only standard NULL and extended types support a null value. */
		if (value == null) {
			boolean valid = false;
			/* Standard types: only null type supports nulls. */
			valid |= (type == JSONTypes.NULL);
			/* Extended types support null values. */
			valid |= (type == JSONTypes.BINARY);
			valid |= (type == JSONTypes.DATE);
			valid |= (type == JSONTypes.TIME);
			valid |= (type == JSONTypes.TIMESTAMP);
			if (!valid) {
				throw new IllegalArgumentException("Invalid type " + type + " for a null value.");
			}
		}

		/* Convert a standard number into decimal. */
		if (value != null && type == JSONTypes.NUMBER) {
			if (value instanceof String) value = new BigDecimal((String) value);
			if (value instanceof Double) value = BigDecimal.valueOf((Double) value);
			if (value instanceof Integer) value = BigDecimal.valueOf((Integer) value);
			if (value instanceof Long) value = BigDecimal.valueOf((Long) value);
		}

		/* Assign. */
		this.type = type;
		this.value = value;
	}

	/**
	 * Check whether this entry value is a {@link JSONObject}.
	 *
	 * @return A boolean.
	 */
	public boolean isObject() { return type == OBJECT; }
	/**
	 * Check whether this entry value is a {@link JSONArray}.
	 *
	 * @return A boolean.
	 */
	public boolean isArray() { return type == ARRAY; }
	/**
	 * Check whether this entry value is a {@link String}.
	 *
	 * @return A boolean.
	 */
	public boolean isString() { return type == STRING; }
	/**
	 * Check whether this entry value is a {@link Number}.
	 *
	 * @return A boolean.
	 */
	public boolean isNumber() { return type == NUMBER; }
	/**
	 * Check whether this entry value is a boolean.
	 *
	 * @return A boolean.
	 */
	public boolean isBoolean() { return type == BOOLEAN; }
	/**
	 * Check whether this entry type is NULL.
	 *
	 * @return A boolean.
	 */
	public boolean isNullType() { return type == NULL; }
	/**
	 * Check whether this entry value is null.
	 *
	 * @return A boolean.
	 */
	public boolean isNullValue() { return value == null; }
	/**
	 * Check whether this entry value is a byte[].
	 *
	 * @return A boolean.
	 */
	public boolean isBinary() { return type == BINARY; }
	/**
	 * Check whether this entry value is a {@link LocalDate}.
	 *
	 * @return A boolean.
	 */
	public boolean isDate() { return type == DATE; }
	/**
	 * Check whether this entry value is a {@link LocalTime}.
	 *
	 * @return A boolean.
	 */
	public boolean isTime() { return type == TIME; }
	/**
	 * Check whether this entry value is a {@link LocalDateTime}.
	 *
	 * @return A boolean.
	 */
	public boolean isTimestamp() { return type == TIMESTAMP; }

	/**
	 * Return the value as a {@link JSONObject}, if it is so.
	 *
	 * @return A {@link JSONObject}.
	 */
	public JSONObject getObject() {
		if (type != OBJECT) throw new IllegalStateException("Value is not OBJECT");
		return (JSONObject) value;
	}
	/**
	 * Return the value as a {@link JSONArray}, if it is so.
	 *
	 * @return A {@link JSONArray}.
	 */
	public JSONArray getArray() {
		if (type != ARRAY) throw new IllegalStateException("Value is not ARRAY");
		return (JSONArray) value;
	}
	/**
	 * Return the value as a {@link String}, if it is so.
	 *
	 * @return A {@link String}.
	 */
	public String getString() {
		if (type != STRING) throw new IllegalStateException("Value is not STRING");
		return (String) value;
	}
	/**
	 * Return the value as a {@link Number}, if it is so.
	 *
	 * @return A {@link Number}.
	 */
	public BigDecimal getNumber() {
		if (type != NUMBER) throw new IllegalStateException("Value is not NUMBER");
		return (BigDecimal) value;
	}
	/**
	 * Return the value as a boolean.
	 *
	 * @return A boolean.
	 */
	public boolean getBoolean() {
		if (type != BOOLEAN) throw new IllegalStateException("Value is not BOOLEAN");
		return (boolean) value;
	}
	/**
	 * Return the value as a byte[], if it is so.
	 *
	 * @return A byte[] or null.
	 */
	public byte[] getBinary() {
		if (type != BINARY) throw new IllegalStateException("Value is not BINARY");
		return (byte[]) value;
	}
	/**
	 * Return the value as a {@link LocalDate}, if it is so.
	 *
	 * @return A {@link LocalDate}.
	 */
	public LocalDate getDate() {
		if (type != DATE) throw new IllegalStateException("Value is not DATE");
		return (LocalDate) value;
	}
	/**
	 * Return the value as a {@link LocalTime}, if it is so.
	 *
	 * @return A {@link LocalTime}.
	 */
	public LocalTime getTime() {
		if (type != TIME) throw new IllegalStateException("Value is not TIME");
		return (LocalTime) value;
	}
	/**
	 * Return the value as a {@link LocalDateTime}, if it is so.
	 *
	 * @return A {@link LocalDateTime}.
	 */
	public LocalDateTime getTimestamp() {
		if (type != TIMESTAMP) throw new IllegalStateException("Value is not TIMESTAMP");
		return (LocalDateTime) value;
	}
	/**
	 * Return the type.
	 *
	 * @return The type.
	 */
	public JSONTypes getType() {
		return type;
	}
}
