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

package com.msfx.lib.util;

import java.io.*;
import java.util.*;
import java.util.Properties;

/**
 * File utility functions.
 *
 * @author Miquel Sas
 */
public class Files {
	/**
	 * Returns the file extension is present.
	 *
	 * @param fileName The file name.
	 * @return The extension part.
	 */
	public static String getFileExtension(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) return "";
		return fileName.substring(index + 1);
	}
	/**
	 * Returns the file name if an exension is present.
	 *
	 * @param fileName The file name.
	 * @return The name part.
	 */
	public static String getFileName(String fileName) {
		int index = fileName.lastIndexOf('.');
		if (index == -1) return fileName;
		return fileName.substring(0, index);
	}
	/**
	 * Return the file or null, scanning the current name as a file, and then the available class
	 * path entries recursively.
	 *
	 * @param fileName The file name to search.
	 * @return The file or null.
	 */
	public static File findFileWithinClassPathEntries(String fileName) {
		return findFileWithinPathEntries(fileName, getClassPathEntries());
	}
	/**
	 * Return the file or null, scanning the current name as a file, and then the argument list of
	 * path entries.
	 *
	 * @param fileName The file name to search.
	 * @param entries  The list of path entries.
	 * @return
	 */
	public static File findFileWithinPathEntries(String fileName, List<String> entries) {
		/* Check direct. */
		File file = new File(fileName);
		if (file.exists() && file.isFile()) return file;
		/* Check path entries. */
		for (String s : entries) {
			File entry = new File(s);
			File check = getFileRecursive(entry, file);
			if (check != null) return check;
		}
		return null;
	}
	/**
	 * Return the directory or null, scanning the current name as a directory, and then the
	 * available class path entries recursively.
	 *
	 * @param dirName The directory name to search.
	 * @return The directory or null.
	 */
	public static File findDirectoryWithinClassPathEntries(String dirName) {
		return findDirectoryWithinPathEntries(dirName, getClassPathEntries());
	}
	/**
	 * Return the directory or null, scanning the current name as a directory, and then the argument
	 * list of path entries.
	 *
	 * @param dirName The directory name to search.
	 * @param entries The list of path entries.
	 * @return
	 */
	public static File findDirectoryWithinPathEntries(String dirName, List<String> entries) {
		/* Check direct. */
		File file = new File(dirName);
		if (file.exists() && file.isDirectory()) return file;
		/* Check path entries. */
		for (String s : entries) {
			File entry = new File(s);
			File check = getDirectoryRecursive(entry, file);
			if (check != null) return check;
		}
		return null;
	}
	/**
	 * Return the file composed by the parent and child, scanning recursively if the parent is a
	 * directory.
	 *
	 * @param parent The parent, file or directory.
	 * @param file   The file to search.
	 * @return The file or null if not found recursively.
	 */
	public static File getFileRecursive(File parent, File file) {
		if (parent.isFile()) {
			String parentPath = parent.getAbsolutePath();
			String filePath = file.getPath();
			if (parentPath.endsWith(filePath)) {
				return parent;
			}
		}
		if (parent.isDirectory()) {
			File[] children = parent.listFiles();
			for (File child : children) {
				File check = getFileRecursive(child, file);
				if (check != null) return check;
			}
		}
		return null;
	}
	/**
	 * Return the directory composed by the parent and child, scanning recursively if the parent is
	 * a directory.
	 *
	 * @param parent The parent, file or directory.
	 * @param file   The directory to search.
	 * @return The file or null if not found recursively.
	 */
	public static File getDirectoryRecursive(File parent, File file) {
		if (parent.isDirectory()) {
			String parentPath = parent.getAbsolutePath();
			String filePath = file.getPath();
			if (parentPath.endsWith(filePath)) {
				return parent;
			}
			File[] children = parent.listFiles();
			for (File child : children) {
				File check = getDirectoryRecursive(child, file);
				if (check != null) return check;
			}
		}
		return null;
	}
	/**
	 * Returns the localized file or the default given the locale, the file name and the extension.
	 *
	 * @param fileName The file name.
	 * @param locale   The locale.
	 * @return The localized file or null if it does not exist.
	 */
	public static File getLocalizedFile(String fileName, Locale locale) {
		File file = null;
		String name = getFileName(fileName);
		String ext = getFileExtension(fileName);
		if (!ext.isEmpty()) ext = "." + ext;

		/* First attempt: language and country. */
		if (!locale.getCountry().isEmpty()) {
			fileName = name + "_" + locale.getLanguage() + "_" + locale.getCountry() + ext;
			file = findFileWithinClassPathEntries(fileName);
		}
		if (file != null) return file;

		/* Second attempt: language only. */
		if (!locale.getLanguage().isEmpty()) {
			fileName = name + "_" + locale.getLanguage() + ext;
			file = findFileWithinClassPathEntries(fileName);
		}
		if (file != null) return file;

		/* Third attempt: no locale reference. */
		fileName = name + ext;
		file = findFileWithinClassPathEntries(fileName);
		if (file != null) return file;

		/* Not found at all. */
		return null;
	}
	/**
	 * Returns the list of path entries parsing the class path string.
	 *
	 * @return A list of path entries.
	 */
	public static List<String> getClassPathEntries() {
		String classPath = System.getProperty("java.class.path");
		String pathSeparator = System.getProperty("path.separator");
		StringTokenizer tokenizer = new StringTokenizer(classPath, pathSeparator);
		List<String> entries = new ArrayList<>();
		while (tokenizer.hasMoreTokens()) { entries.add(tokenizer.nextToken()); }
		return entries;
	}
	/**
	 * Gets the properties by loading the file.
	 *
	 * @param file The file.
	 * @return The properties.
	 */
	public static Properties getProperties(File file) throws IOException {
		boolean xml = getFileExtension(file.getName()).equalsIgnoreCase("xml");
		FileInputStream fileIn = new FileInputStream(file);
		BufferedInputStream buffer = new BufferedInputStream(fileIn, 4096);
		Properties properties = getProperties(buffer, xml);
		buffer.close();
		fileIn.close();
		return properties;
	}
	/**
	 * Gets the properties from the input stream.
	 *
	 * @param stream The input stream.
	 * @param xml    A boolean that indicates if the input stream has an xml format
	 * @return The properties.
	 */
	public static Properties getProperties(InputStream stream, boolean xml) throws IOException {
		Properties properties = new Properties();
		if (xml) properties.loadFromXML(stream);
		else properties.load(stream);
		return properties;
	}
}
