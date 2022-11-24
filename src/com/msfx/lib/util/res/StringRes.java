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

package com.msfx.lib.util.res;

import java.util.Locale;

/**
 * Provider of localized string resources.
 *
 * @author Miquel Sas
 */
public abstract class StringRes {
	/**
	 * Default resources installed when starting a desktop application.
	 */
	private static StringRes res;
	/**
	 * Return the string that corresponds to the search key.
	 *
	 * @param key Lookup key.
	 * @return The localized string.
	 */
	public static String get(String key) { return get(key, null); }
	/**
	 * Return the string that corresponds to the search key.
	 *
	 * @param key Lookup key.
	 * @param def The default string.
	 * @return The localized string.
	 */
	public static String get(String key, String def) {
		return get(key, Locale.getDefault(), def);
	}
	/**
	 * Return the string that corresponds to the search key.
	 *
	 * @param key    Lookup key.
	 * @param locale The preferred locale.
	 * @param def    The default string.
	 * @return The localized string.
	 */
	public static String get(String key, Locale locale, String def) {
		if (res == null && def == null) return "[" + key + "]";
		if (res == null && def != null) return def;
		return res.getString(key, locale, def);
	}
	/**
	 * Set the default static resources.
	 *
	 * @param res The resources.
	 */
	public static void setDefault(StringRes res) { StringRes.res = res; }

	/**
	 * Return the string that corresponds to the search key.
	 *
	 * @param key Lookup key.
	 * @return The localized string.
	 */
	public String getString(String key) {
		return getString(key, null);
	}
	/**
	 * Return the string that corresponds to the search key.
	 *
	 * @param key Lookup key.
	 * @param def The default string.
	 * @return The localized string.
	 */
	public String getString(String key, String def) {
		return getString(key, Locale.getDefault(), def);
	}
	/**
	 * Return the string that corresponds to the search key.
	 *
	 * @param key    Lookup key.
	 * @param locale The preferred locale.
	 * @param def    The default string.
	 * @return The localized string.
	 */
	public abstract String getString(String key, Locale locale, String def);
}
