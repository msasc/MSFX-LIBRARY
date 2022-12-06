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
/**
 * 
 */
package com.msfx.lib.fx;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import com.msfx.lib.util.Console;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

/**
 * A console mapped to a text area.
 * 
 * @author Miquel Sas
 */
public class TextConsole implements Console {

	/**
	 * Writer into the text area console.
	 */
	private class CSW extends Writer {
		/** Writes characters from the source into the console. */
		public void write(char[] src, int off, int len) throws IOException {
			if (src == null) throw new NullPointerException();
			if (len == 0) return;
			boolean outOfBounds = false;
			outOfBounds |= (off < 0);
			outOfBounds |= (off > src.length);
			outOfBounds |= (len < 0);
			outOfBounds |= ((off + len) > src.length);
			outOfBounds |= (off + len) < 0;
			if (outOfBounds) throw new IndexOutOfBoundsException();
			StringBuilder dst = new StringBuilder(len);
			for (int i = off; i < off + len; i++) { dst.append(src[i]); }
			TextConsole.this.append(dst.toString());
		}
		/** Not applicable. */
		public void flush() throws IOException {}
		/** Not applicable. */
		public void close() throws IOException {}
	}

	/** Text area. */
	private TextArea textArea;
	/** Maximum number of line, zero no limit, default is 1000. */
//	private int maxLines = 1000;
	/** Print writer. */
	private PrintWriter printWriter;

	/**
	 * Constructor.
	 */
	public TextConsole() {
		textArea = new TextArea();
		printWriter = new PrintWriter(new CSW());
	}
	
	public TextArea getControl() { return textArea; }
	
	public void print(String str) { printWriter.print(str); }
	public void println() { printWriter.println(); }
	public void println(String str) { printWriter.println(str); }
	
	public void clear() {
		Platform.runLater(() -> {
			textArea.clear();
		});
	}
	
	/**
	 * Append the string to the console.
	 * 
	 * @param str The string to append.
	 */
	private void append(String str) {
		Platform.runLater(() -> {
			textArea.appendText(str);
//			if (maxLines > 0) {
//				String text = textArea.getText();
//				int lineCount = Strings.countLines(text);
//				if (lineCount > (maxLines + (maxLines / 2))) {
//					int startLine = 0;
//					int endLine = lineCount - maxLines;
//					int startOffset = Strings.offsetStartLine(text, startLine);
//					int endOffset = Strings.offsetEndLine(text, endLine);
//					textArea.deleteText(startOffset, endOffset);
//				}
//			}
			textArea.end();
		});
	}	
}
