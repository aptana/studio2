/* ***** BEGIN LICENSE BLOCK *****
 * Version: GPL 3
 *
 * This program is Copyright (C) 2007-2008 Aptana, Inc. All Rights Reserved
 * This program is licensed under the GNU General Public license, version 3 (GPL).
 *
 * This program is distributed in the hope that it will be useful, but
 * AS-IS and WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE, TITLE, or
 * NONINFRINGEMENT. Redistribution, except as permitted by the GPL,
 * is prohibited.
 *
 * You can redistribute and/or modify this program under the terms of the GPL, 
 * as published by the Free Software Foundation.  You should
 * have received a copy of the GNU General Public License, Version 3 along
 * with this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 * 
 * Aptana provides a special exception to allow redistribution of this file
 * with certain other code and certain additional terms
 * pursuant to Section 7 of the GPL. You may view the exception and these
 * terms on the web at http://www.aptana.com/legal/gpl/.
 * 
 * You may view the GPL, and Aptana's exception and additional terms in the file
 * titled license-jaxer.html in the main distribution folder of this program.
 * 
 * Any modifications to this file must keep this entire header intact.
 *
 * ***** END LICENSE BLOCK ***** */
package com.aptana.ide.editor.scriptdoc.parsing.reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * @author Kevin Lindsey
 */
public class SimpleDependencyGraph<T>
{
	private Map<String, List<String>> _vertices;
	private Map<String, T> _nameMap;
	
	/**
	 * SimpleDependencyGraph
	 */
	public SimpleDependencyGraph()
	{
		this._vertices = new HashMap<String, List<String>>();
		this._nameMap = new HashMap<String, T>();
	}
	
	/**
	 * addEdge
	 * 
	 * @param from
	 * @param to
	 */
	public void addEdge(String from, String to)
	{
		if (this.hasVertex(from))
		{
			if (this.hasVertex(to))
			{
				List<String> children = this._vertices.get(from);
				
				if (children.contains(to) == false)
				{
					children.add(to);
				}
			}
//			else
//			{
//				System.out.println("No 'to' vertex for '" + to + "'");
//			}
//		}
//		else
//		{
//			System.out.println("No 'from' vertex for '" + from + "'");
		}
	}
	
	/**
	 * addMapping
	 * 
	 * @param name
	 * @param item
	 */
	public void addMapping(String name, T item)
	{
		this._nameMap.put(name, item);
	}
	
	/**
	 * createVertex
	 * 
	 * @param name
	 */
	public void addVertex(String name)
	{
		if (this.hasVertex(name) == false)
		{
			List<String> children = new ArrayList<String>();
			
			this._vertices.put(name, children);
		}
	}
	
	/**
	 * getItem
	 * 
	 * @param name
	 * @return
	 */
	public T getItem(String name)
	{
		return this._nameMap.get(name);
	}
	
	/**
	 * hasVertex
	 * 
	 * @param name
	 * @return
	 */
	public boolean hasVertex(String name)
	{
		return this._vertices.containsKey(name);
	}
	
	/**
	 * size
	 * 
	 * @return
	 */
	public int size()
	{
		return this._vertices.size();
	}
	
	/**
	 * topologicalSort
	 * 
	 * @return
	 */
	public String[] topologicalSort()
	{
		Set<String> processed = new HashSet<String>();
		List<String> result = new ArrayList<String>();
		
		for (String name : this._vertices.keySet())
		{
			Stack<String> visited = new Stack<String>();
			Stack<String> active = new Stack<String>();
			
			// prime stack
			active.push(name);
			
			while (active.isEmpty() == false)
			{
				String current = active.pop();
				
				if (processed.contains(current) == false)
				{
					// flag as being processed to avoid duplicate processing
					processed.add(current);
					
					visited.add(current);
					
					// get children and process
					List<String> children = this._vertices.get(current);
					
					// add children in reverse order so they are processed in the correct order
					for (int i = children.size() - 1; i >= 0; i--)
					{
						active.push(children.get(i));
					}
				}
			}
			
			while (visited.isEmpty() == false)
			{
				result.add(visited.pop());
			}
		}
		
		return result.toArray(new String[result.size()]);
	}
}
