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

package com.msfx.lib.ml.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A pattern source backed by an array list.
 *
 * @author Miquel Sas
 */
public class ListPatternSource extends PatternSource {
	
	/** The underlying pattern list. */
	private final List<Pattern> patterns = new ArrayList<>();
	/** Pattern iterator. */
	private Iterator<Pattern> iterator;
	
	/**
	 * Constructor.
	 */
	public ListPatternSource() { }
	/**
	 * Add a pattern.
	 * @param pattern A pattern.
	 */
	public void add(Pattern pattern) { patterns.add(pattern); iterator = null; }
	/**
	 * Returns true it the source has more patterns.
	 * @return A boolean.
	 */
	@Override
	public boolean hasNext() { return iterator.hasNext(); }
	/**
	 * Returns the next pattern or null.
	 * @return The next pattern or null.
	 */
	@Override
	public Pattern next() { return iterator.next(); }
	/**
	 * Reset the source and point to the first pattern.
	 */
	@Override
	public void reset() { iterator = patterns.iterator(); }
	/**
	 * Return the size of the source.
	 * @return The size.
	 */
	@Override
	public int size() { return patterns.size(); }
}
