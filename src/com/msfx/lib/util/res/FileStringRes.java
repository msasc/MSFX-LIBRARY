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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

import com.msfx.lib.util.Files;
import com.msfx.lib.util.Lists;

/**
 * File system string resources provider.
 *
 * @author Miquel Sas
 */
public class FileStringRes extends StringRes {

	/**
	 * Map with properties per locale.
	 */
	private final Map<Locale, Properties> map = new HashMap<>();
	/**
	 * List of string resource bundles.
	 */
	private final List<String> bundles = new ArrayList<>();
	/**
	 * Concurrent lock.
	 */
	private final ReentrantLock lock = new ReentrantLock();

	/**
	 * Constructor.
	 *
	 * @param bundles The list of reference bundles, without locale suffix.
	 */
	public FileStringRes(String... bundles) { this.bundles.addAll(Lists.asList(bundles)); }
	/**
	 * Load the bundles.
	 *
	 * @param properties The properties map.
	 * @param locale     The desired locale.
	 * @throws IOException
	 */
	private void loadBundles(Properties properties, Locale locale) throws IOException {
		for (String bundle : bundles) {
			File file = Files.getLocalizedFile(bundle, locale);
			if (file == null) continue;
			Properties fileProperties = Files.getProperties(file);
			properties.putAll(fileProperties);
		}
	}
	/**
	 * Returns the string that corresponds to the search key for the given locale.
	 */
	@Override
	public String getString(String key, Locale locale, String def) {
		try {
			lock.lock();
			Properties properties = map.get(locale);
			if (properties == null) {
				properties = new Properties();
				try {
					loadBundles(properties, locale);
				} catch (IOException exc) {
					exc.printStackTrace();
				}
				map.put(locale, properties);
			}
			String str = properties.getProperty(key);
			if (str == null && def == null) return "[" + key + "]";
			if (str == null && def != null) return def;
			return str;
		} finally {
			lock.unlock();
		}
	}
}
