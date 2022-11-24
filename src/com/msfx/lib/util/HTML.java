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

import java.util.ArrayList;
import java.util.List;

/**
 * Helper to build HTML content.
 *
 * @author Miquel Sas
 */
public class HTML {

	private static void print(StringBuilder b, String s) { b.append(s); }
	private static void println(StringBuilder b) { b.append("\n"); }
	private static void println(StringBuilder b, String s) { println(b); print(b, s); }
	private static void printStyles(StringBuilder b, String... styles) {
		print(b, "\"");
		List<String> styleList = parseCSS(styles);
		for (String style : styleList) {
			print(b, style);
			print(b, ";");
		}
		print(b, "\"");
	}

	/**
	 * Parse CSS styles (with ; as separator)
	 * @param styleArray The list of styles.
	 * @return A list of styles.
	 */
	public static List<String> parseCSS(String... styleArray) {
		List<String> styles = new ArrayList<>();
		for (String style : styleArray) {
			List<String> styleList = Strings.parse(style, ";");
			for (int i = 0; i < styleList.size(); i++) {
				styleList.set(i, styleList.get(i).trim());
			}
			styles.addAll(styleList);
		}
		return styles;
	}

	/** Head styles. */
	private final StringBuilder head;
	/** Body. */
	private final StringBuilder body;

	/**
	 * Constructor.
	 */
	public HTML() {
		head = new StringBuilder();
		body = new StringBuilder();
	}

	/**
	 * Add head styles.
	 * @param key    The class key, either a simple class with the form ".class", a tag of a
	 *               specific class like for instance "p.class", or a simple tag like "td".
	 * @param styles The list of styles.
	 */
	public void headStyles(String key, String... styles) {
		if (head.length() > 0) println(head);
		print(head, key);
		print(head, " {");
		List<String> styleList = parseCSS(styles);
		for (String style : styleList) {
			println(head);
			print(head, style);
			print(head, ";");
		}
		println(head, "}");
	}
	/**
	 * Print an end tag.
	 * @param tag The tag.
	 */
	public void endTag(String tag) {
		print(body, "</");
		print(body, tag);
		print(body, ">");
	}
	/**
	 * Print text to the body, under the current tag, adding the styles.
	 * @param text   The text.
	 * @param styles The list of styles.
	 */
	public void print(String text, String... styles) {
		if (styles != null) {
			print(body, "<span style=");
			printStyles(body, styles);
			print(body, ">");
		}
		print(body, text);
		if (styles != null) {
			print(body, "</span>");
		}
	}
	/**
	 * Print a start tag.
	 * @param tag    The tag.
	 * @param styles The list of additional styles.
	 */
	public void startTag(String tag, String... styles) {
		startTagClassStyles(tag, null, styles);
	}
	/**
	 * Print a start tag.
	 * @param tag    The tag.
	 * @param clazz  The class name.
	 * @param styles The list of additional styles.
	 */
	public void startTagClass(String tag, String clazz, String... styles) {
		startTagClassStyles(tag, clazz, styles);
	}
	/**
	 * Start a tag with optional class and styles.
	 * @param tag    The tag.
	 * @param clazz  The optional class.
	 * @param styles The optional styles.
	 */
	private void startTagClassStyles(String tag, String clazz, String... styles) {
		print(body, "<");
		print(body, tag);
		if (clazz != null) {
			print(body, " class=\"" + clazz + "\"");
		}
		if (styles != null) {
			print(body, " style=");
			printStyles(body, styles);
		}
		print(body, ">");
	}
	/**
	 * Print a table detail.
	 * @param text   The text.
	 * @param styles List of styles.
	 */
	public void td(String text, String... styles) {
		startTag("td", styles);
		print(text);
		endTag("td");
	}
	/**
	 * Return the HTML string.
	 */
	public String toString() {
		StringBuilder html = new StringBuilder();
		print(html, "<!DOCTYPE html>");
		println(html, "<html>");
		println(html, "<head>");
		println(html, "<style>");
		println(html, head.toString());
		println(html, "</style>");
		println(html, "</head>");
		println(html, "<body>");
		println(html, body.toString());
		println(html, "</body>");
		println(html, "</html>");
		return html.toString();
	}
}
