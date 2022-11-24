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

package com.msfx.lib.util;

import java.util.*;

/**
 * A useful and quite generic properties table with typed access for commonly used objects.
 *
 * @author Miquel Sas
 */
public class Properties {

	/**
	 * The properties map.
	 */
	private final Map<Object, Object> properties = new HashMap<>();

	/**
	 * Constructor.
	 */
	public Properties() { }

	/**
	 * Clear these properties.
	 */
	public void clear() { properties.clear(); }

	/**
	 * Returns a stored object.
	 *
	 * @param key The key.
	 * @return The stored object.
	 */
	public Object getObject(Object key) { return getObject(key, null); }
	/**
	 * Returns a stored object.
	 *
	 * @param key The key.
	 * @param def Default value.
	 * @return The stored object.
	 */
	public Object getObject(Object key, Object def) {
		Object val = properties.get(key);
		return (val != null ? val : def);
	}

	/**
	 * Returns a stored Boolean value.
	 *
	 * @param key The key.
	 * @return The stored boolean value.
	 */
	public Boolean getBoolean(Object key) {
		return getBoolean(key, null);
	}
	/**
	 * Returns a stored Boolean value.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The stored boolean value.
	 */
	public Boolean getBoolean(Object key, Boolean def) {
		return (Boolean) getObject(key, def);
	}
	/**
	 * Returns a stored Double value.
	 *
	 * @param key The key.
	 * @return The stored double value.
	 */
	public Double getDouble(Object key) {
		return getDouble(key, 0.0);
	}
	/**
	 * Returns a stored Double value.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The stored double value.
	 */
	public Double getDouble(Object key, Double def) {
		return (Double) getObject(key, def);
	}
	/**
	 * Return a stored double vector.
	 *
	 * @param key The key.
	 * @return The double vector.
	 */
	public double[] getDouble1A(Object key) {
		return getDouble1A(key, null);
	}
	/**
	 * Return a stored double vector.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The double vector.
	 */
	public double[] getDouble1A(Object key, double[] def) {
		return (double[]) getObject(key, def);
	}
	/**
	 * Return a stored double 2d matrix.
	 *
	 * @param key The key.
	 * @return The double 2d matrix.
	 */
	public double[][] getDouble2A(Object key) {
		return getDouble2A(key, null);
	}
	/**
	 * Return a stored double 2d matrix.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The double 2d matrix.
	 */
	public double[][] getDouble2A(Object key, double[][] def) {
		return (double[][]) getObject(key, def);
	}
	/**
	 * Returns a stored Integer value.
	 *
	 * @param key The key.
	 * @return The stored integer value.
	 */
	public Integer getInteger(Object key) {
		return getInteger(key, 0);
	}
	/**
	 * Returns a stored Integer value.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The stored integer value.
	 */
	public Integer getInteger(Object key, Integer def) {
		return (Integer) getObject(key, def);
	}
	/**
	 * Returns a stored Long value.
	 *
	 * @param key The key.
	 * @return The stored long value.
	 */
	public Long getLong(Object key) {
		return getLong(key, 0L);
	}
	/**
	 * Returns a stored Long value.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The stored long value.
	 */
	public Long getLong(Object key, Long def) {
		return (Long) getObject(key, def);
	}
	/**
	 * Returns a stored string value, returning <code>null</code> if not set.
	 *
	 * @param key The key.
	 * @return The stored string value.
	 */
	public String getString(Object key) {
		return getString(key, "");
	}
	/**
	 * Returns a stored string value, returning <code>null</code> if not set.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The stored string value.
	 */
	public String getString(Object key, String def) {
		return (String) getObject(key, def);
	}

	/**
	 * Return a stored typed list.
	 *
	 * @param <T> The type of the elements of the list.
	 * @param key The key.
	 * @return The typed list.
	 */
	public <T> List<T> getList(Object key) {
		return getList(key, null);
	}
	/**
	 * Return a stored typed list.
	 *
	 * @param <T> The type of the elements of the list.
	 * @param key The key.
	 * @param def Default value
	 * @return The typed list.
	 */
	@SuppressWarnings("unchecked")
	public <T> List<T> getList(Object key, List<T> def) {
		return (List<T>) getObject(key, def);
	}
	/**
	 * Return a stored typed map.
	 *
	 * @param key The key.
	 * @return The typed map.
	 */
	public <K, V> Map<K, V> getMap(Object key) {
		return getMap(key, null);
	}
	/**
	 * Return a stored typed map.
	 *
	 * @param key The key.
	 * @param def Default value
	 * @return The typed map.
	 */
	@SuppressWarnings("unchecked")
	public <K, V> Map<K, V> getMap(Object key, Map<K, V> def) {
		return (Map<K, V>) getObject(key, def);
	}

	/**
	 * Put a value.
	 *
	 * @param key   The key.
	 * @param value The value.
	 */
	public void put(Object key, Object value) {
		properties.put(key, value);
	}
	/**
	 * Put all properties.
	 *
	 * @param properties The properties to use to fill.
	 */
	public void putAll(Properties properties) {
		this.properties.putAll(properties.properties);
	}

	/**
	 * Remove the property at key.
	 *
	 * @param key The key.
	 * @return The removed property or null.
	 */
	public Object remove(Object key) {
		return properties.remove(key);
	}

	/**
	 * Return the set of keys.
	 *
	 * @return The set of keys.
	 */
	public Set<Object> keys() {
		return properties.keySet();
	}
	/**
	 * Return the collection of values.
	 *
	 * @return The values.
	 */
	public Collection<Object> values() {
		return properties.values();
	}
}
