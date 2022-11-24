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

import com.msfx.lib.util.Strings;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;

/**
 * A writer of JSON objects.
 *
 * @author Miquel Sas
 */
public class JSONWriter {

	/**
	 * Tab size, default is 3.
	 */
	private final int tabSize = 3;
	/**
	 * Tab level.
	 */
	private int tabLevel = 0;
	/**
	 * Maximum tab level.
	 */
	private int maxTabLevel = Integer.MAX_VALUE;
	/**
	 * A boolean that indicates whether the format should be readable.
	 */
	private boolean readable = false;

	/**
	 * Constructor of a writer with non readable format.
	 */
	public JSONWriter() {
	}
	/**
	 * Constructor assigning indicating whether the format should be readable.
	 *
	 * @param readable A boolean that indicates whether the format should be readable.
	 */
	public JSONWriter(boolean readable) {
		this.readable = readable;
	}

	/**
	 * Set the maximum tab level.
	 *
	 * @param maxTabLevel The maximum tab level.
	 */
	public void setMaxTabLevel(int maxTabLevel) {
		this.maxTabLevel = maxTabLevel;
	}

	/**
	 * Write an JSON object to the writer.
	 *
	 * @param w The writer.
	 * @param o The JSONObject.
	 * @throws IOException If an error occurs.
	 */
	public void write(Writer w, JSONObject o) throws IOException {
		boolean readable = this.readable && tabLevel < maxTabLevel;
		w.write("{");
		if (readable) {
			tabLevel++;
		}
		Iterator<String> i = o.keys().iterator();
		while (i.hasNext()) {
			if (readable) {
				w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
			}
			String key = i.next();
			w.write("\"" + key + "\":");
			JSONEntry e = o.get(key);
			write(w, e);
			if (i.hasNext()) w.write(",");
		}
		if (readable) {
			tabLevel--;
			w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
		}
		w.write("}");
	}
	/**
	 * Write an JSON array to the writer.
	 *
	 * @param w The writer.
	 * @param a The JSONArray.
	 * @throws IOException IOException If an error occurs.
	 */
	public void write(Writer w, JSONArray a) throws IOException {
		boolean readable = this.readable && tabLevel < maxTabLevel;
		w.write("[");
		if (readable) {
			tabLevel++;
		}
		Iterator<JSONEntry> i = a.iterator();
		while (i.hasNext()) {
			if (readable) {
				w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
			}
			write(w, i.next());
			if (i.hasNext()) w.write(",");
		}
		if (readable) {
			tabLevel--;
			w.write("\n" + Strings.repeat(" ", tabLevel * tabSize));
		}
		w.write("]");
	}
	/**
	 * Write an JSON entry to the writer.
	 *
	 * @param w The writer.
	 * @param e The JSONEntry.
	 * @throws IOException IOException If an error occurs.
	 */
	public void write(Writer w, JSONEntry e) throws IOException {

		/* Standard types. */
		if (e.isObject()) { write(w, e.getObject()); return; }
		if (e.isArray()) { write(w, e.getArray()); return; }
		if (e.isString()) { w.write("\"" + e.getString() + "\""); return; }
		if (e.isNumber()) { w.write(e.getNumber().toPlainString()); return; }
		if (e.isBoolean()) { w.write(Boolean.valueOf(e.getBoolean()).toString()); return; }
		if (e.isNullType()) { w.write("null"); return; }

		/* Extended types. */
		if (e.isBinary()) {
			w.write("{\"");
			w.write(e.type.key);
			w.write("\":\"");
			byte[] bytes = e.getBinary();
			if (bytes != null) {
				for (byte b : bytes) {
					String s = Integer.toString(b, 16);
					w.write(Strings.leftPad(s, 2, "0"));
				}
			}
			w.write("\"}");
		}
		if (e.isDate() || e.isTime() || e.isTimestamp()) {
			w.write("{\"");
			w.write(e.type.key);
			w.write("\":\"");
			if (e.value != null) {
				w.write(e.value.toString());
			}
			w.write("\"}");
		}
	}

	/**
	 * Returns the JSON array as a string.
	 *
	 * @param arr The JSON array.
	 * @return The string representation.
	 */
	public String toString(JSONArray arr) {
		try {
			StringWriter w = new StringWriter();
			write(w, arr);
			return w.toString();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}

	/**
	 * Returns the JSON object as a string.
	 *
	 * @param obj The JSON object.
	 * @return The string representation.
	 */
	public String toString(JSONObject obj) {
		try {
			StringWriter w = new StringWriter();
			write(w, obj);
			return w.toString();
		} catch (IOException exc) {
			exc.printStackTrace();
		}
		return null;
	}
}
